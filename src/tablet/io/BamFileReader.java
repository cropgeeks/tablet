package tablet.io;

import java.io.*;
import java.util.HashMap;
import net.sf.samtools.*;
import tablet.analysis.BasePositionComparator;
import tablet.data.*;
import tablet.data.cache.IReadCache;

public class BamFileReader extends TrackableReader
{
	private IReadCache readCache;

	private Contig contig;
	private Consensus consensus;
	private Read read;

	private ReferenceFileReader refReader;

	// We maintain a local hashtable of contigs to help with finding a
	// contig quickly when processing consensus tags
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();
	private int samIndex = -1;
	private int refIndex = -1;

	private CigarParser cigarParser;

	BamFileReader(IReadCache readCache)
	{
		this.readCache = readCache;
	}

	boolean canRead() throws Exception
	{
		refReader = new ReferenceFileReader(assembly, contigHash);

		for(int i = 0; i < files.length; i++)
		{
			final SAMFileReader inputSam = new SAMFileReader(getInputStream(i, false));
			if(inputSam.isBinary() && inputSam.getFileHeader().getVersion().equals(SAMFileHeader.CURRENT_VERSION))
			{
				inputSam.close();
				samIndex = i;
			}
			else if (refReader.canRead(files[i]) != AssemblyFileHandler.UNKNOWN)
			{
				refIndex = i;
			}
		}
		return (samIndex >= 0);
	}

	public void runJob(int jobIndex) throws Exception
	{
		if(refIndex >= 0)
			readReferenceFile();

		readBamFile();
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(refIndex, true), "ASCII"));

		refReader.readReferenceFile(this, files[refIndex]);

		in.close();
	}

	private void readBamFile() throws Exception
	{
		SAMFileReader inputSam = new SAMFileReader(getInputStream(samIndex, false));
		int readID = 0;
		CigarParser parser = new CigarParser();
		Contig prev = null;

		boolean found = false;

		for (final SAMRecord record : inputSam)
		{
			//If we don't have a reference file...need to grab contig names from somewhere
			if (!contigHash.containsKey(record.getReferenceName()) && !record.getReferenceName().equals(SAMRecord.NO_ALIGNMENT_REFERENCE_NAME))
			{
				consensus = new Consensus();
				consensus.setBaseQualities(record.getBaseQualities());
				contig = new Contig(record.getReferenceName(), true, 0);
				contig.setConsensusSequence(consensus);
				contigHash.put(record.getReferenceName(), contig);
				assembly.addContig(contig);
			}


			if (!record.getReadUnmappedFlag())
			{
				Contig contigToAddTo = contigHash.get(record.getReferenceName());

				//create the Read and ReadMetaData objects
				readID = createRead(contigToAddTo, record, readID, parser);
			}
		}
		
		assembly.setName(files[samIndex].getName());
		inputSam.close();
	}

	private int createRead(Contig contigToAddTo, final SAMRecord record, int readID, CigarParser parser) throws Exception
	{
		if (contigToAddTo != null)
		{
			int readStartPos = record.getAlignmentStart()-1;
			ReadMetaData rmd = new ReadMetaData(record.getReadName(), record.getReadNegativeStrandFlag());
			
			String fullRead = parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString());
			rmd.setData(fullRead);
			read = new Read(readID, readStartPos);

			rmd.calculateUnpaddedLength();
			read.setLength(rmd.length());
			read.setCigar(record.getCigarString());
			contigToAddTo.getReads().add(read);

			// Do base-position comparison...

			BasePositionComparator.compare(contigToAddTo.getConsensus(), rmd, readStartPos);
			
			readCache.setReadMetaData(rmd);
			readID++;
		}
		return readID;
	}

	private void addContig(Contig contig, Consensus consensus, StringBuilder bases, StringBuilder qlt)
	{
		consensus.setData(bases.toString());
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		byte[] bq = new byte[consensus.length()];
		for(int i = 0; i < bq.length; i++)
		{
			bq[i] = (byte) (qlt.charAt(i) - 33);
			if(bq[i] > 100)
				bq[i] = 100;
		}
		consensus.setBaseQualities(bq);

		assembly.addContig(contig);
	}
}
