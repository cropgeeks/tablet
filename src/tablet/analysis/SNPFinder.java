// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.io.*;

import scri.commons.gui.*;

import htsjdk.samtools.*;

public class SNPFinder extends SimpleJob
{
	private Assembly assembly;
	private ArrayList<HashMap<String, Metric>> data;
	private static String[] states = new String[] { " ?", "d?", " *", "d*", " N", "dN", " A", "dA", " C", "dC", " G", "dG", " T", "dT" };
	private File file;
	private BufferedWriter out;

	public SNPFinder(File file, Assembly assembly)
	{
		this.assembly = assembly;
		this.file = file;
	}

	public void runJob(int jobNum) throws Exception
	{
		out = new BufferedWriter(new FileWriter(file));
		for (Contig contig : assembly)
			maximum += contig.getTableData().readCount;

		SamReader reader = assembly.getBamBam().getBamFileHandler().getBamReader();

		for (Contig contig : assembly)
		{
			if (!okToRun)
				break;

			if (contig.getTableData().readCount == 0)
				continue;

			Consensus consensus = contig.getConsensus();
			CigarParser parser = new CigarParser(contig);

			// Allocate the data array ready for processing
			data = new ArrayList<HashMap<String, Metric>>();
			for (int i=0; i < consensus.length(); i++)
				data.add(new HashMap<String, Metric>());

			// For every read in this contig...
			SAMRecordIterator itor = reader.queryOverlapping(contig.getName(), 0, 0);

			while (itor.hasNext() && okToRun())
			{
				SAMRecord record = itor.next();
				if (record.getReadUnmappedFlag())
					continue;

				ReadMetaData rmd = new ReadMetaData();

				try
				{
					String seq = parser.parse(record.getReadString(), record.getAlignmentStart()-1, record.getCigarString(), null);
					rmd.setData(new StringBuilder(seq));
					BasePositionComparator.compare(contig, rmd, record.getAlignmentStart()-1);

					processRead(rmd, consensus, record.getReadGroup().getSample(), record.getAlignmentStart()-1);
				}
				catch (Exception e) {}
			}
			itor.close();

			printResults(contig);
		}
		out.close();
	}

	private void processRead(ReadMetaData rmd, Consensus consensus, String readGroup, int pos)
	{
		for (int i=0; i < rmd.length(); i++)
		{
			// Base equals current base in the consensus
			int base = pos+i;

			Metric metric = getMetric(base, readGroup, consensus);
			metric.coverage++;

			byte state = rmd.getStateAt(i);

			metric.bases[state]++;
		}

		progress++;
	}

	private Metric getMetric(int base, String readGroup, Consensus consensus)
	{
		HashMap<String, Metric> hashMap = data.get(base);

		if (hashMap.get(readGroup) == null)
		{
			Metric metric = new Metric(readGroup);
			metric.ref = consensus.getSequence().getStateAt(base);

			hashMap.put(readGroup, metric);
		}

		return hashMap.get(readGroup);
	}

	private void printResults(Contig contig) throws Exception
	{
		Sequence consensus = contig.getConsensus().getSequence();
		out.write("Contig=" + contig.getName());
		out.newLine();

		for (int base=0; base < contig.getConsensus().length(); base++)
		{
			ArrayList<Metric> metrics = getMetrics(base);

			boolean hasDelta = false;
			int coverage = 0;

			for (Metric metric : metrics)
			{
				coverage += metric.coverage;
				if (metric.hasDelta())
				{
					hasDelta = true;
				}
			}

			if (hasDelta)
			{
				byte cState = consensus.getStateAt(base);
				out.write("SNP\t" + (base+1) + "\t" + states[cState] + "\t" + coverage);
				out.newLine();
				for (Metric metric : metrics)
				{
					out.write("Sample=" + metric.readGroup);
					out.newLine();

					for (int i=0; i < metric.bases.length; i++)
					{
						out.write(states[i] + "=" + metric.bases[i]);
						out.newLine();
					}
				}
			}
		}
	}

	private ArrayList<Metric> getMetrics(int base)
	{
		ArrayList<Metric> metrics = new ArrayList<>();

		HashMap<String, Metric> hashMap = data.get(base);

		for (String key : hashMap.keySet())
			metrics.add(hashMap.get(key));

		return metrics;
	}

	private static class Metric
	{
		int coverage;
		int[] bases = new int[14];
		String readGroup;
		byte ref;

		Metric(String readGroup)
		{
			this.readGroup = readGroup;
		}

		boolean hasDelta()
		{
			int deltaCount = 0;

			for (int i=1; i < bases.length; i += 2)
				deltaCount += bases[i];

			return deltaCount > 0;
		}
	}
}