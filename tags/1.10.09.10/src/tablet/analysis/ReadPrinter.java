package tablet.analysis;

import java.io.*;
import java.util.ArrayList;

import tablet.data.*;

public class ReadPrinter extends SimpleJob
{
	public static final int COLUMN_TYPE = 0;
	public static final int SCREEN_TYPE = 1;

	private File file;
	private IReadManager manager;
	private int readPrinterType;
	private int colIndex, xS, xE;

	/**
	 * Constructor to use for a column printer.
	 */
	public ReadPrinter(File file, IReadManager manager, int readPrinterType, int colIndex)
	{
		this.file = file;
		this.manager = manager;
		this.readPrinterType = readPrinterType;
		this.colIndex = colIndex;
	}

	/**
	 * Constructor to use for a screen printer.
	 */
	public ReadPrinter(File file, IReadManager manager, int readPrinterType, int xS, int xE)
	{
		this.file = file;
		this.manager = manager;
		this.readPrinterType = readPrinterType;
		this.xS = xS;
		this.xE = xE;
	}

	public void runJob(int jobIndex) throws Exception
	{
		switch(readPrinterType)
		{
			case COLUMN_TYPE: columnPrinter(); break;
			case SCREEN_TYPE: screenPrinter(); break;
		}
	}

	/**
	 * Prints out the reads which intersects the column given by colIndex.
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
			{
				ReadMetaData rmd = Assembly.getReadMetaData(read, false);
				ReadNameData rnd = Assembly.getReadNameData(read);

				out.write(rnd.getName() + "\t" + rmd.toString() + "\t" + read.getStartPosition());
				out.newLine();
			}

			progress++;
		}

		out.close();
	}

	/**
	 * Prints out the reads where any part of the read can be found in the reads
	 * canvas window when the printer is launched.
	 */
	private void screenPrinter() throws IOException
	{
		maximum = manager.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		for(int i=0; i < manager.size(); i++)
		{
			if(!okToRun)
				return;

			ArrayList<Read> reads = manager.getLine(i);

			int readIndex = findStartRead(reads);
			Read read = reads.get(readIndex);

			// Loop over the reads in the window on this line
			while(read != null &&  readIndex < reads.size() && read.getStartPosition() < xE)
			{
				if(read != null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, false);
					ReadNameData rnd = Assembly.getReadNameData(read);

					out.write(rnd.getName() + "\t" + rmd.toString() + "\t" + read.getStartPosition());
					out.newLine();
				}

				readIndex++;
				read = reads.get(readIndex);
				progress++;
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
		while(m > 0 && read.getStartPosition() > xS)
		{
			m--;
			read = reads.get(m);
		}

		return m;
	}

}
