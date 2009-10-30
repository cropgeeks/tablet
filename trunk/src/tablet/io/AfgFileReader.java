// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import tablet.analysis.BasePositionComparator;
import tablet.data.*;
import tablet.data.cache.*;

class AfgFileReader extends TrackableReader
{
	private IReadCache readCache;

	private Contig contig;

	// The index of the AFG file in the files[] array
	private int afgIndex = -1;

	// Incrementing count of the number of reads processed
	private int readsFound = 0;

	// Pretty much all the tokenizing we do is based on this one pattern
	private Pattern p = Pattern.compile("\\s+");

	//a list of all the reads in the assembly - this is needed because the read infor is provided before the tile info, which dictates
	//how the reads are aligned against the consensus
	//reads and tiles are matched to each other by means of the internal ID of the read within the file
	//reads are supplied in that order by default, starting at 1

	//the Tablet ID for the read currently being processed
	private int currReadID = 0;

	//the standard gap character for this file format
	private final String gapChar = "-";

	//this temporary disk cache holds the names of the reads while we read them in, to conserve memory
	//private AfgNameCache cache = null;

	private IReadCache tempCache;
	private File cacheDir;

	private File cacheFile;
	private File indexFile;

	private boolean firstTileFound = false;

	//=======================================c'tor==========================================

	AfgFileReader()
	{
	}

	AfgFileReader(IReadCache readCache, File cacheDir)
	{
		this.readCache = readCache;
		this.cacheDir = cacheDir;
	}

	//=======================================methods==========================================

	boolean canRead()
		throws Exception
	{
		for (int i = 0; i < files.length; i++)
		{
			// Read and check for the header
			in = new BufferedReader(new InputStreamReader(getInputStream(0)));
			str = readLine();

			if (str != null && str.startsWith("{"))
				afgIndex = i;

			in.close();
			is.close();
		}

		return (afgIndex >= 0);
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(afgIndex), "ASCII"));

		//open the temporary file cache for the read names
		long time = System.currentTimeMillis();
		cacheFile = new File(cacheDir, time + "-" + files[0].getName() + ".tempCache");
		indexFile = new File(cacheDir, time + "-" + files[0].getName() + ".tempCacheIndex");

		tempCache = FileCache.createWritableCache(cacheFile, indexFile);

		// Scan for contigs
		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("{RED"))
				processRead();

			else if (str.startsWith("{CTG"))
				processContig();
		}

		assembly.setName(files[0].getName());
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a CTG tag -- this contains contig information (internal ID, external ID i.e. name, unpadded sequence, quality scores)
	private void processContig() throws Exception
	{
		String consensusSeq = null; // the consensus sequence itself
		String qualScores = null; // quality scores for consensus sequence

		Consensus consensus = new Consensus();

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

				//deal with the consensus sequence
				consensus.setData(consensusSeq);
				consensus.calculateUnpaddedLength();
				contig.setConsensusSequence(consensus);
			}

			//read alignment info
			else if (str.startsWith("{TLE"))
			{
				processReadLocation();
			}
		}

		//make new contig and add to assembly
		assembly.addContig(contig);

		//convert consensus qualScores to numerical values (ASCII codes)
		qualScores = convertStringtoASCII(qualScores);
		//convert to byte array and set on the consensus object
		processBaseQualities(consensus, qualScores);

	}

	//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a TLE tag -- this contains the info about the alignment of a read against the consensus
	private void processReadLocation() throws Exception
	{
		if(!firstTileFound)
		{
			firstTileFound = true;
			tempCache.close();
			tempCache = FileCache.createReadableCache(cacheFile, indexFile);
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
		ReadMetaData readMetaData = tempCache.getReadMetaData(tileIID-1);
		readMetaData.calculateUnpaddedLength();

		Read read = new Read();

		//set Tablet read id on the current read
		read.setID(currReadID);

		//set start pos
		read.setStartPosition(offset);

		read.setLength(readMetaData.length());

		//now deal with the gap positions
		//first check whether we have any gaps in the existing sequence and remove them if there are

		//if the read is meant to be complemented we need to do this ourselves here now
		//in the afg format all reads sequences are stored as uncomplemented
		if((isComplemented && !readMetaData.isComplemented()) || (!isComplemented && readMetaData.isComplemented()))
		{
			reverseComplementRead(readMetaData);
			readMetaData.setComplmented(isComplemented);
		}

		//check for gaps and insert if present
		if(gapPositions != null && gapPositions.size() > 0)
		{
			StringBuilder buf = new StringBuilder(readMetaData.toString());
			for(Integer i : gapPositions)
			{
				buf.insert(i, gapChar);
			}
			readMetaData.setData(buf.toString());
		}

		//add read to this contig
		contig.getReads().add(read);
		readsFound++;

		BasePositionComparator.compare(contig.getConsensus(), readMetaData, read.getStartPosition());

		readCache.setReadMetaData(readMetaData);

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
		//Read read = new Read();
		ReadMetaData readMetaData = new ReadMetaData();

		//String qualScores = null; // quality scores for  sequence  -- not used for now

		//parse the RED tag
		while ((str = readLine()) != null && str.length() > 0 && !str.startsWith("}"))
		{
			// external ID AKA contig name
			if(str.startsWith("eid"))
			{
				readMetaData.setName(str.substring(str.indexOf(":") + 1));
			}

			// the  sequence itself
			else if (str.startsWith("seq"))
			{
				StringBuilder seqBuf = new StringBuilder();
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					seqBuf.append(str);
				}
				readMetaData.setData(seqBuf.toString());
			}
		}

		readMetaData.calculateUnpaddedLength();
		readMetaData.setComplmented(false);

		//store this read in the local list so we can look it up later when we are dealing with the alignment of reads against the
		//consensus sequence
		//at this point the read is not complemented so we can set the boolean (3rd param) to false accordingly
		tempCache.setReadMetaData(readMetaData);
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

	private void reverseComplementRead(ReadMetaData rmd)
	{
		int length = rmd.length();
		StringBuilder sb = new StringBuilder(length);

		for (int i = length-1; i >= 0; i--)
		{
			byte state = rmd.getStateAt(i);
			sb.append(rmd.getComplementaryDNA(state));
		}

		rmd.setData(sb.toString());
	}
}