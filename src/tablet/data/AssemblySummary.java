// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;
import scri.commons.gui.RB;

import tablet.gui.TabletUtils;
import tablet.io.*;

public class AssemblySummary
{
	private long readCount;
	private long averageReads;
	private long n50;
	private long n90;
	private int contigCount;
	private int averageContigLen;
	private AssemblyFile reference;
	private AssemblyFile assembly;

	// Should be called with a (descending sort order) list of contig lengths
	// and a list of read counts.
	public void calculateStatistics(ArrayList<Integer> contigLengths, ArrayList<Integer> readCounts)
	{
		readCount = calculateTotalReadCount(readCounts);
		averageReads = readCount / readCounts.size();
		n50 = calculateNX(0.5f, contigLengths);
		n90 = calculateNX(0.9f, contigLengths);
		contigCount = contigLengths.size();
		averageContigLen = calculateAverageContigLength(contigLengths);
	}

	private long calculateTotalReadCount(ArrayList<Integer> readCounts)
	{
		readCount = 0;
		for (Integer count : readCounts)
			readCount += count;

		return readCount;
	}

	// percent should be between 0 and 1.
	// contigLengths should be a descending order list of contig lengths
	private long calculateNX(float percent, ArrayList<Integer> contigLengths)
	{
		// Get the total length of all contigs in the assembly
		long total = 0;
		for (Integer i : contigLengths)
			total += i;

		// Calculate nX based on percentage passed in by user
		long nCutOff = (long) (total * percent);
		long current = 0;
		long lastAdded = 0;
		for (Integer length : contigLengths)
		{
			current += length;
			lastAdded = length;

			if (current >= nCutOff)
				break;
		}

		return lastAdded;
	}

	private int calculateAverageContigLength(ArrayList<Integer> contigLengths)
	{
		long totalLen = 0;
		for (Integer length : contigLengths)
			totalLen += length;

		return (int) (totalLen / contigLengths.size());
	}

	public String getReadCount()
	{
		return TabletUtils.nf.format(readCount);
	}

	public String getAverageReads()
	{
		return TabletUtils.nf.format(averageReads);
	}

	public String getN50()
	{
		return TabletUtils.nf.format(n50);
	}

	public String getN90()
	{
		return TabletUtils.nf.format(n90);
	}

	public String getContigCount()
	{
		return TabletUtils.nf.format(contigCount);
	}

	public String getAverageContigLen()
	{
		return TabletUtils.nf.format(averageContigLen);
	}

	public String getReferenceName()
	{
		return reference == null ? RB.getString("gui.dialog.SummaryStatsDialog.table.noRef") : reference.getName();
	}

	public String getReferenceSize()
	{
		if (reference != null)
		{
			long len = reference.length();

			if (len == 0)
				return RB.getString("gui.dialog.SummaryStatsDialog.table.unknown");

			String lengthString = makeLengthString(len);
			return lengthString;
		}

		return "";
	}

	public void setReference(AssemblyFile reference)
	{
		this.reference = reference;
	}

	public String getAssemblyName()
	{
		return assembly == null ? "" : assembly.getName();
	}

	public String getAssemblySize()
	{
		long len = assembly.length();

		if (len == 0)
			return RB.getString("gui.dialog.SummaryStatsDialog.table.unknown");

		String lengthString = makeLengthString(len);
		return lengthString;
	}

	public void setAssembly(AssemblyFile assembly)
	{
		this.assembly = assembly;
	}

	private String makeLengthString(long len)
	{
		String lengthString;
		if (len > 1024*1024*1024)
			lengthString = TabletUtils.nf.format(len / (1024*1024*1024)) + " GB";
		else if (len > 1024*1024)
			lengthString = TabletUtils.nf.format(len / (1024*1024)) + " MB";
		else if (len > 1024)
			lengthString = TabletUtils.nf.format(len / (1024)) + " KB";
		else
			lengthString = TabletUtils.nf.format(len) + " bytes";
		return lengthString;
	}
}