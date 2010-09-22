// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import tablet.analysis.BasePositionComparator;
import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;

class AfgFileReader extends TrackableReader
{
	private IReadCache readCache;
	private ReadSQLCache nameCache;
	private HashMap<Integer, Integer> iids = new HashMap<Integer, Integer>();
	private int tmpCacheID = 0;

	private Contig contig;

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
	private ReadSQLCache sqlCache;
	private File cacheDir;

	private File cacheFile;
	private File indexFile;
	private File sqlCacheFile;

	private boolean firstTileFound = false;

	// Count of the number of reads and contigs processed (GUI tracking only)
	private int contigsAdded = 0;
	private int readsAdded = 0;

	AfgFileReader()
	{
	}

	AfgFileReader(IReadCache readCache, ReadSQLCache nameCache, File cacheDir)
	{
		this.readCache = readCache;
		this.nameCache = nameCache;
		this.cacheDir = cacheDir;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(ASBINDEX), "ASCII"));

		//open the temporary file cache for the read names
		long time = System.currentTimeMillis();
		cacheFile = new File(cacheDir, time + "-" + files[0].getName() + ".tempCache");
		indexFile = new File(cacheDir, time + "-" + files[0].getName() + ".tempCacheIndex");
		sqlCacheFile = new File(cacheDir, time + "-" + files[0].getName() + ".db");

		tempCache = new ReadFileCache(cacheFile, indexFile);
		tempCache.openForWriting();

		sqlCache = new ReadSQLCache(sqlCacheFile);
		sqlCache.openForWriting();

		// Scan for contigs
		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("{RED"))
				processRead();

			else if (str.startsWith("{CTG"))
				processContig();
		}
		
		Assembly.setIsPaired(false);
		assembly.setName(files[ASBINDEX].getName());
	}

	//parses a CTG tag -- this contains contig information (internal ID, external ID i.e. name, unpadded sequence, quality scores)
	private void processContig() throws Exception
	{
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

				//deal with the consensus sequence
				consensus.setData(seqBuf);
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
		contigsAdded++;

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
			tempCache.openForReading();
			sqlCache.openForReading();
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
					String[] gaps = str.trim().split(" ");
					for (String gap: gaps)
						gapPositions.add(Integer.parseInt(gap));
				}
			}
		}

		//if the clear range values start with the largest first, the read is complemented
		boolean isComplemented = clearRangeStart > clearRangeEnd;

		// Offset amount for the first gap
		int gapOffset = 0;

		//retrieve the corresponding read from the array list using the internal id
		int id = iids.get(tileIID);
		ReadMetaData readMetaData = tempCache.getReadMetaData(id, false);
		ReadNameData readNameData = sqlCache.getReadNameData(id);

		Read read = new Read();

		//set Tablet read id on the current read
		read.setID(currReadID);

		// Modify the start position of the read by the amount specified using
		// the clear range data. Also compute the gapOffset which (in the same
		// way) needs to be modified by this amount to get the correct starting
		// position for the first gap.
		// Forward read...
		if (clearRangeEnd > clearRangeStart)
		{
			offset = offset - clearRangeStart;
			gapOffset = clearRangeStart;
		}
		// Reverse read...
		else
		{
			offset = offset - (readNameData.getUnpaddedLength() - clearRangeStart);
			gapOffset = readNameData.getUnpaddedLength() - clearRangeStart;
		}

		//set start pos
		read.setStartPosition(offset);

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
			int gapCount = 0;
			for(Integer i : gapPositions)
			{
				buf.insert(i + gapOffset + gapCount, gapChar);
				gapCount++;
			}
			readMetaData.setData(buf);
		}

		read.setLength(readMetaData.length());

		//add read to this contig
		contig.getReads().add(read);

		BasePositionComparator.compare(contig, readMetaData, read.getStartPosition());

		ReadNameData rnd = sqlCache.getReadNameData(id);

		readCache.setReadMetaData(readMetaData);
		readsAdded++;

		nameCache.setReadNameData(rnd);

		//remember to increment the read id
		currReadID++;
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------

	//parses a RED tag -- contains read information (internal ID, external ID i.e. name, unpadded sequence, quality scores)
	private void processRead() throws Exception
	{
		ReadNameData rnd = new ReadNameData();
		//Read read = new Read();
		ReadMetaData readMetaData = new ReadMetaData();

		//String qualScores = null; // quality scores for  sequence  -- not used for now

		//parse the RED tag
		while ((str = readLine()) != null && str.length() > 0 && !str.startsWith("}"))
		{
			// external ID AKA contig name
			if(str.startsWith("eid"))
			{
				rnd.setName(str.substring(str.indexOf(":") + 1));
			}

			else if (str.startsWith("iid"))
			{
				int iid = Integer.parseInt(str.substring(str.indexOf(":") + 1));
				iids.put(iid, tmpCacheID);
			}

			// the  sequence itself
			else if (str.startsWith("seq"))
			{
				StringBuilder seqBuf = new StringBuilder();
				while ((str = readLine()) != null && str.length() > 0 && str.charAt(0) != '.')
				{
					seqBuf.append(str);
				}
				readMetaData.setData(seqBuf);
			}
		}

		int uLength = readMetaData.calculateUnpaddedLength();
		rnd.setUnpaddedLength(uLength);
		readMetaData.setComplmented(false);

		//store this read in the local list so we can look it up later when we are dealing with the alignment of reads against the
		//consensus sequence
		//at this point the read is not complemented so we can set the boolean (3rd param) to false accordingly
		tempCache.setReadMetaData(readMetaData);

		sqlCache.setReadNameData(rnd);

		tmpCacheID++;
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

		rmd.setData(sb);
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigsAdded, readsAdded);
	}
}