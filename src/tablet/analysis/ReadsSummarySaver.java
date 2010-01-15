// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.dialog.*;

import scri.commons.gui.*;

public class ReadsSummarySaver extends SimpleJob
{
	private File file;
	private Contig contig;

	private String lastRead = "";

	public ReadsSummarySaver(File file, Contig contig)
	{
		this.file = file;
		this.contig = contig;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		maximum = contig.getReads().size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		out.write(RB.getString("analysis.ReadsSummarySaver.readName") + "\t"
			+ RB.getString("analysis.ReadsSummarySaver.startPos") + "\t"
			+ RB.getString("analysis.ReadsSummarySaver.length"));
		out.newLine();

		for (Read read: contig.getReads())
		{
			if (!okToRun)
				break;

			ReadMetaData rmd = Assembly.getReadMetaData(read);
			lastRead = rmd.getName();

			out.write(lastRead + "\t" + (read.getStartPosition() + 1)
				+ "\t" + rmd.length());
			out.newLine();

			progress++;
		}

		out.close();
	}

	public String getMessage()
	{
		return RB.format("analysis.ReadsSummarySaver.status", lastRead);
	}
}