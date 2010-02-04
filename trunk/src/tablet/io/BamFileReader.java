package tablet.io;

import java.io.*;
import java.util.HashMap;
import net.sf.samtools.*;
import scri.commons.gui.RB;
import tablet.analysis.BasePositionComparator;
import tablet.data.*;
import tablet.data.auxiliary.CigarFeature;
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

	private int readID;

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
		Assembly.isBam(true);

		SAMFileReader inputSam = new SAMFileReader(getInputStream(samIndex, false));
		readID = 0;
		CigarParser parser = new CigarParser();
		Contig prev = null;

		boolean found = false;

		if(contigHash.isEmpty())
		{
			for(SAMSequenceRecord record : inputSam.getFileHeader().getSequenceDictionary().getSequences())
			{
				consensus = new Consensus();
				contig = new Contig(record.getSequenceName(), true, 0);
				contig.setConsensusSequence(consensus);
				contigHash.put(record.getSequenceName(), contig);
				assembly.addContig(contig);
			}
		}

		for (final SAMRecord record : inputSam)
		{
			if(!okToRun)
				break;

			if (!record.getReadUnmappedFlag())
			{
				Contig contigToAddTo = contigHash.get(record.getReferenceName());

				if(prev == null)
				{
					prev = contigToAddTo;
					parser.setCurrentContigName(contigToAddTo.getName());
				}
				else if(prev != contigToAddTo)
				{
					prev = contigToAddTo;
					parser.setCurrentContigName(contigToAddTo.getName());
				}

				//create the Read and ReadMetaData objects
				createRead(contigToAddTo, record, parser);
			}
		}
		processCigarFeatures(parser);

		assembly.setName(files[samIndex].getName());
		inputSam.close();
	}

	private void createRead(Contig contigToAddTo, final SAMRecord record, CigarParser parser) throws Exception
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
			contigToAddTo.getReads().add(read);

			// Do base-position comparison...

			BasePositionComparator.compare(contigToAddTo.getConsensus(), rmd, readStartPos);

			rmd.setCigar(record.getCigar().toString());

			readCache.setReadMetaData(rmd);
			readID++;
		}
	}

	private void processCigarFeatures(CigarParser parser)
	{
		for (String feature : parser.getFeatureMap().keySet())
		{
			String[] featureElements = feature.split("Tablet-Separator");
			int count = parser.getFeatureMap().get(feature);
			CigarFeature cigarFeature = new CigarFeature("CIGAR-I", "CIG" + featureElements[1], Integer.parseInt(featureElements[1]) - 1, Integer.parseInt(featureElements[1]), count);
			Contig ctg = contigHash.get(featureElements[0]);
			if (ctg != null)
			{
				ctg.getFeatures().add(cigarFeature);
			}
		}
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

	@Override
	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigHash.size(), readID);
	}
}