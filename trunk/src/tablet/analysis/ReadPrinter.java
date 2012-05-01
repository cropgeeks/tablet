// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import java.util.ArrayList;

import scri.commons.gui.*;

import tablet.data.*;

/**
 * Class which carries out exporting of read data to text files. The read data
 * is Fasta formatted and can be exported in column, screen and contig chunks.
 */
public class ReadPrinter extends SimpleJob
{
	// Static fields for setting the type of ReadPrinter
	public static final int COLUMN_TYPE = 0;
	public static final int SCREEN_TYPE = 1;
	public static final int CONTIG_TYPE = 2;

	private File file;
	private IReadManager manager;
	private int readPrinterType;
	private int colIndex, xS, xE, yS, yE;

	/**
	 * Constructor to use for a contig printer (exports all the reads in a
	 * contig).
	 */
	public ReadPrinter(File file, IReadManager manager)
	{
		this.file = file;
		this.manager = manager;
		readPrinterType = CONTIG_TYPE;
	}

	/**
	 * Constructor to use for a column printer (exports all the reads which
	 * intersect the column given by colIndex).
	 */
	public ReadPrinter(File file, IReadManager manager, int colIndex)
	{
		this.file = file;
		this.manager = manager;
		this.colIndex = colIndex;
		readPrinterType = COLUMN_TYPE;
	}

	/**
	 * Constructor to use for a screen printer (exports all the reads which
	 * can be currently seen on screen).
	 */
	public ReadPrinter(File file, IReadManager manager, int xS, int xE, int yS, int yE)
	{
		this.file = file;
		this.manager = manager;
		this.xS = xS;
		this.xE = xE;
		this.yS = yS;
		this.yE = yE;
		readPrinterType = SCREEN_TYPE;
	}

	public void runJob(int jobIndex) throws Exception
	{
		switch(readPrinterType)
		{
			case COLUMN_TYPE: columnPrinter(); break;
			case SCREEN_TYPE: screenPrinter(); break;
			case CONTIG_TYPE: contigPrinter(); break;
		}
	}

	/**
	 * Exports the reads which intersects the column given by colIndex.
	 */
	private void columnPrinter()
			throws IOException
	{
		maximum = manager.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for(int i=0; i < manager.size(); i++)
		{
			if (!okToRun)
				return;

			Read read = manager.getReadAt(i, colIndex);
			if(read != null)
				printRead(read, out);
		}

		out.close();
	}

	/**
	 * Exports the reads where any part of the read can be found in the reads
	 * canvas window when the printer is launched.
	 */
	private void screenPrinter() throws IOException
	{
		maximum = manager.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for(int i=yS; i <= yE && i < manager.size(); i++)
		{
			if(!okToRun)
				return;

			ArrayList<Read> line = manager.getLine(i);

			for(int j=findStartRead(line); j < line.size() && line.get(j).s() < xE; j++)
			{
				Read read = line.get(j);
				printRead(read, out);
			}
		}
		out.close();
	}

	/**
	 * Exports the reads where any part of the read can be found in the reads
	 * canvas window when the printer is launched.
	 */
	private void contigPrinter() throws IOException
	{
		for	(int j=0; j < manager.size(); j++)
		{
			maximum += manager.getLine(j).size();
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for	(int i=0; i < manager.size(); i++)
		{
			if (!okToRun)
				return;

			for (Read read : manager.getLine(i))
			{
				if (read != null)
					printRead(read, out);
			}
		}
		out.close();
	}

	/**
	 * (Binary) Searches for the first read in the current window, for the current
	 * line of the display. Returns its index within the reads ArrayList.
	 */
	private int findStartRead(ArrayList<Read> reads)
	{
		int l = 0;
		int h = reads.size();
		int m = 0;

		while(l < h)
		{
			m = l + ((h-l)/2);

			int windowed = reads.get(m).compareToWindow(xS, xE);

			if(windowed == -1)
				l = m+1;
			else if(windowed == 0)
				break;
			else
				h = m;
		}

		// Binary search only guarantees finding a read in the window, must search
		// back to find first read in window
		Read read = reads.get(m);
		while(m > 0 && read.s() > xS)
		{
			m--;
			read = reads.get(m);
		}

		// Adjust if we've gone too far
		if (read.e() < xS)
			m++;

		return m;
	}

	/**
	 * Method for exporting a single read given a read and the BufferedWriter
	 * for the file which the read is to be written into.
	 */
	private void printRead(Read read, BufferedWriter out) throws IOException
	{
		ReadMetaData rmd = Assembly.getReadMetaData(read, false);
		ReadNameData rnd = Assembly.getReadNameData(read);

		out.write(">" + rnd.getName() + " pos=" + (read.s() + 1)
				+ " len=" + read.length());
		out.newLine();

		String readString = rmd.toString();
		boolean newLine = false;

		for (int j = 0; j < readString.length(); j++)
		{
			out.write(readString.charAt(j));
			// Spec reccommends that each line in a Fasta file is less than
			// 80 characters in length
			if (j % 78 == 0 && j != 0)
			{
				newLine = true;
				out.newLine();
			}
			else
				newLine = false;
		}

		if (newLine != true)
			out.newLine();

		progress++;
	}

}