// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

public class CoveragePrinter extends SimpleJob
{
	private File file;
	private Assembly assembly;
	private Contig contig;

	public CoveragePrinter(File file, Assembly assembly)
	{
		this.assembly = assembly;
		this.file = file;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		for (Contig contig: assembly)
			maximum++;

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for (Contig contig: assembly)
		{
			if (!okToRun)
				break;

			this.contig = contig;

			out.write(contig.getName());
			out.newLine();

			if (assembly.getBamBam() != null)
			{
				assembly.getBamBam().reset(Prefs.bamSize);
				assembly.getBamBam().setBlockStart(contig, 0);
				assembly.getBamBam().loadDataBlock(contig);
			}

			CoverageCalculator cc = new CoverageCalculator(contig);
			cc.runJob(0);

			int[] coverage = cc.getCoverage();
			int s = -contig.getVisualStart();
			int e = contig.getConsensus().length() -1 -contig.getVisualStart();

			Sequence cSeq = contig.getConsensus().getSequence();

			int c = 0;
			for (int i = s; i <= e && okToRun; i++, c++)
			{
				if (Prefs.printPads || cSeq.getStateAt(i-s) != Sequence.P)
				{
					out.write(" " + coverage[i]);
					if (c == 49)
					{
						out.newLine();
						c = -1;
					}
				}
			}

			if (c > 0)
				out.newLine();

			progress++;
		}

		out.close();
	}

	public String getMessage()
	{
		if (contig != null)
			return RB.format("analysis.CoveragePrinter.status", contig.getName());
		else
			return " ";
	}
}