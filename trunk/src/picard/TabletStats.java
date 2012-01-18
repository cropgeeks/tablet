// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package net.sf.samtools;

import java.io.*;

import net.sf.samtools.*;

/**
 * Tablet class that we've injected into the Picard package to allow the export
 * of data that is hidden from the public API. Whenever we update sam.jar for
 * Tablet, we need to reinsert this class, and then recompile/rejar Picard.
 */
public class TabletStats
{
	private int[] alignedRecords;

	public TabletStats()
	{
	}

	public void doWork(InputStream input, File indexFile)
		throws Exception
	{
		BAMFileReader bam = new BAMFileReader(input, indexFile, false, SAMFileReader.ValidationStringency.SILENT);

		AbstractBAMFileIndex index = (AbstractBAMFileIndex) bam.getIndex();

		// read through all the bins of every reference.
		int nRefs = index.getNumberOfReferences();

		alignedRecords = new int[nRefs];

		for (int i = 0; i < nRefs; i++)
		{
			BAMIndexContent content = index.query(i, 0, -1); // todo: it would be faster just to skip to the last bin

			alignedRecords[i] = content.getMetaData().getAlignedRecordCount();
		}
	}

	public int getAlignedRecordCount(int refIndex)
	{
		return alignedRecords[refIndex];
	}
}