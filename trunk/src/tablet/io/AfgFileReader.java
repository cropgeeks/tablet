package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import scri.commons.gui.*;
import tablet.data.*;
import tablet.data.cache.*;

class AfgFileReader extends TrackableReader
{
	private boolean useAscii;
	private IReadCache readCache;

	private Contig contig;

	// Incrementing count of the number of reads processed
	private int readsFound = 0;

	// Pretty much all the tokenizing we do is based on this one pattern
	private Pattern p = Pattern.compile("\\s+");

	//a list of all the reads in the assembly - this is needed because the read infor is provided before the tile info, which dictates
	//how the reads are aligned against the consensus
	//reads and tiles are matched to each other by means of the internal ID of the read within the file
	//reads are supplied in that order by default, starting at 1
	private ArrayList<AfgRead> afgReadList = new ArrayList<AfgRead>();

	//the Tablet ID for the read currently being processed
	private int currReadID = 0;

	//the standard gap character for this file format
	private final String gapChar = "-";

	//this temporary disk cache holds the names of the  reads while we read them in, to conserve memory
	private AfgNameCache cache = null;

	private boolean firstTileFound = false;

	//=======================================c'tor==========================================

	AfgFileReader(IReadCache readCache, boolean useAscii)
	{
		this.readCache = readCache;
		this.useAscii = useAscii;
	}

	//=======================================methods==========================================

	boolean canRead()
		throws Exception
	{
		// Read and check for the header
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));
		str = readLine();

		boolean isAFGFile = (str != null && str.startsWith("{"));

		in.close();
		is.close();

		return isAFGFile;
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	public void runJob(int jobIndex)
		throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(getInputStream(0), "ASCII")); // ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(getInputStream(0)));

		//open the temporary file cache for the read names
		long time = System.currentTimeMillis();
		File cacheDir = SystemUtils.getTempUserDirectory("scri-tablet");
		File cacheFile = new File(cacheDir, time + "-" + files[0].getName() + ".names");

		cache = new AfgNameCache(cacheFile);
		cache.openForWriting();

		// Scan for contigs
		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("{RED"))
				processRead();

			else if (str.startsWith("{CTG"))
				processContig();

		}

		//close the temporary file cache for the read names
		//this also deletes the file itself
		cache.close();

		assembly.setName(files[0].getName());
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a CTG tag -- this contains contig information (internal ID, external ID i.e. name, unpadded sequence, quality scores)
	private void processContig() throws Exception
	{
		String consensusSeq = null; // the consensus sequence itself
		String qualScores = null; // quality scores for consensus sequence

		//parse the CTG tag
		while ((str = readLine()) != null && str.length() > 0 && !str.startsWith("}"))
		{
			//external ID AKA contig name
			if (str.startsWith("eid"))
			{
				String name = str.substring(str.indexOf(":") + 1);
//				System.out.println("found new contig: " + name);
				contig = new Contig(name, true, 0);
			}

			//quality scores for consensus sequence
			//in FASTQ format these are ASCII characters whose code is the actual numerical value of the score
			else if (str.startsWith("qlt"))
			{
				StringBuilder qltBuf = new StringBuilder();
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					qltBuf.append(str);
				}
				qualScores = qltBuf.toString();
			}

			//the consensus sequence itself
			else if (str.startsWith("seq"))
			{
				StringBuilder seqBuf = new StringBuilder();
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					seqBuf.append(str);
				}
				consensusSeq = seqBuf.toString();
			}

			//read alignment info
			else if (str.startsWith("{TLE"))
			{
				processReadLocation();
			}
		}

		//make new contig and add to assembly
		assembly.addContig(contig);


		//deal with the consensus sequence
		Consensus consensus = new Consensus();
		consensus.setData(consensusSeq);
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		//convert consensus qualScores to numerical values (ASCII codes)
		qualScores = convertStringtoASCII(qualScores);
		//convert to byte array and set on the consensus object
		processBaseQualities(consensus, qualScores);

//		System.out.println("added new contig: " + name);
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a TLE tag -- this contains the info about the alignment of a read against the consensus
	private void processReadLocation() throws Exception
	{
		if(!firstTileFound)
		{
			firstTileFound = true;
			cache.openForReading();
			System.out.println("opening cache for reading");
		}

		//this contains the positions of the gap characters in this read
		ArrayList<Integer> gapPositions = new ArrayList<Integer>();
		//internal ID for the read, start pos (zero-based), start and end of clear range
		int tileIID= -1;
		int offset= -1;
		int clearRangeStart= -1;
		int clearRangeEnd = -1;

		//parse the TLE tag
		while ((str = readLine()) != null && str.length() > 0 && !str.trim().equals("}"))
		{
			//the clear range tag (clr)
			if (str.startsWith("clr"))
			{
				clearRangeStart = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.indexOf(",")));
				clearRangeEnd = Integer.parseInt(str.substring(str.indexOf(",") + 1));
			}
			//the internal read ID tag (src)
			if (str.startsWith("src"))
			{
				tileIID = Integer.parseInt(str.substring(str.trim().indexOf(":") + 1));
			}
			//the offset tag (start position, zero-based)
			else if (str.startsWith("off"))
			{
				offset = Integer.parseInt(str.substring(str.trim().indexOf(":") + 1));
			}
			//list of gap positions needs to be parsed
			else if(str.startsWith("gap"))
			{
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					gapPositions.add(Integer.parseInt(str.trim()));
				}
			}
		}

		//if the clear range values start with the largest first, the read is complemented
		boolean isComplemented = clearRangeStart > clearRangeEnd;

		//retrieve the corresponding read from the array list using the internal id
		//tile internal ID is 1-based but list is 0-based so need to subtract 1 here to get the right read
		AfgRead afgRead = afgReadList.get(tileIID-1);
		int unpaddedLength = afgRead.read.calculateUnpaddedLength();
		//retrieve the read name from the temporary disk cache we keep for this
//		System.out.println("attempting to retrieve name from cache: " + (tileIID-1));
		String readName = cache.getName(tileIID-1);

		//check whether it already has a start position
		//if not, add it
		//if it has, clone it so that we have the correct number of reads matching the number of tiles
		if(afgRead.startPositionIsSet)
		{
			Read read = afgRead.read.clone();
			AfgRead newAfgRead = new AfgRead(read, afgRead.isComplemented);
			//now point the current object at this new object so we can consistently apply the same steps to existing and cloned objects afterwards
			afgRead = newAfgRead;
		}

		//set Tablet read id on the current read
		afgRead.read.setID(currReadID);

		//set start pos
		afgRead.read.setStartPosition(offset);
		afgRead.startPositionIsSet = true;

		//now deal with the gap positions
		//first check whether we have any gaps in the existing sequence and remove them if there are
		String cleanSeq = null;
		if (unpaddedLength != afgRead.read.length())
		{
			cleanSeq = afgRead.read.toString().replaceAll(gapChar, "");
			afgRead.read.setData(cleanSeq);
		}

		//if the read is meant to be complemented we need to do this ourselves here now
		//in the afg format all reads sequences are stored as uncomplemented
		if((isComplemented && !afgRead.isComplemented) || (!isComplemented && afgRead.isComplemented))
		{
			reverseComplementRead(afgRead.read);
			afgRead.isComplemented = isComplemented;
		}

		//check for gaps and insert if present
		if(gapPositions != null && gapPositions.size() > 0)
		{
			StringBuilder buf = new StringBuilder(cleanSeq);
			for(Integer i : gapPositions)
			{
				buf.insert(i, gapChar);
			}
			afgRead.read.setData(buf.toString());
		}

		//add read to this contig
		contig.getReads().add(afgRead.read);
		readsFound++;

		// Store the metadata about the read in the cache
		//TODO: the "complemented" boolean is hard coded to false here for now -- deal with this later
		ReadMetaData rmd = new ReadMetaData(readName, isComplemented, unpaddedLength);
		readCache.setReadMetaData(rmd);

		//remember to increment the read id
		currReadID++;

		//every so often, print out the number of reads found
		if (readsFound % 250000 == 0)
			System.out.println(" reads found: " + readsFound);
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a RED tag -- contains read information (internal ID, external ID i.e. name, unpadded sequence, quality scores)
	private void processRead() throws Exception
	{
		Read read = new Read();

		//String qualScores = null; // quality scores for  sequence  -- not used for now

		//parse the RED tag
		while ((str = readLine()) != null && str.length() > 0 && !str.startsWith("}"))
		{
			// external ID AKA contig name
			if (str.startsWith("eid"))
			{
				// Store the name separately in the temporary disk cache, to preserve memory
				cache.storeName(str.substring(str.indexOf(":") + 1));
			}

			// internal ID -- for cross referencing within AFG file
//			else if (str.startsWith("iid"))
//				internalID = Integer.parseInt(str.substring(str.indexOf(":") + 1));

//			// quality scores for sequence
//			else if (str.startsWith("qlt"))
//			{
//				StringBuffer qltBuf = new StringBuffer();
//				while ((str = readLine()) != null && str.length() > 0 && !str.trim().equals("."))
//				{
//					qltBuf.append(str);
//				}
//				qualScores = qltBuf.toString();
//			}

			// the  sequence itself
			else if (str.startsWith("seq"))
			{
				StringBuilder seqBuf = new StringBuilder();
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					seqBuf.append(str);
				}
				read.setData(seqBuf.toString());
			}
		}

		//store this read in the local list so we can look it up later when we are dealing with the alignment of reads against the
		//consensus sequence
		//at this point the read is not complemented so we can set the boolean (3rd param) to false accordingly
		afgReadList.add(new AfgRead(read, false));
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------


	private String convertStringtoASCII(String string)
	{
		StringBuilder convertedStrBuf = new StringBuilder();

		 for (int i = 0; i < string.length(); ++i)
		{
			 int ascii = (string.charAt(i)) - 48;
			convertedStrBuf.append(ascii + " ");
		}

		return convertedStrBuf.toString();
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------


	private void processBaseQualities(Consensus consensus, String qualScores)
		throws Exception
	{
		String[] tokens = p.split(qualScores.trim());
		byte[] bq = new byte[consensus.length()];

		for (int t = 0, i = 0; t < tokens.length; t++, i++)
		{
			// Skip padded bases, because the quality string doesn't score them
			while (consensus.getStateAt(i) == Sequence.P)
			{
				bq[i] = -1;
				i++;
			}

			bq[i] = Byte.parseByte(tokens[t]);
		}

		consensus.setBaseQualities(bq);
	}

	private void reverseComplementRead(Read read)
	{
		int length = read.length();
		StringBuilder sb = new StringBuilder(length);

		for (int i = length-1; i >= 0; i--)
		{
			byte state = read.getStateAt(i);
			sb.append(read.getComplementaryDNA(state));
		}

		read.setData(sb.toString());
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	private static class AfgRead
	{
		private Read read;
		private boolean startPositionIsSet;
		private boolean isComplemented;

		AfgRead(Read read, boolean isComplemented)
		{
			this.read = read;
			this.isComplemented = isComplemented;
		}
	}
}