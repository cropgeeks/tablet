// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.ArrayList;
import tablet.data.*;

public class VisibleReadsGrabberThread extends Thread
{
	private Boolean killMe = false;
	private static VisibleReadsGrabberThread previousThread;

	private int delay = 500;
	private int xS, xE, yS, yE;

	private IReadManager manager;

	public VisibleReadsGrabberThread(IReadManager manager, int xS, int xE, int yS, int yE)
	{
		this.manager = manager;
		this.xS = xS;
		this.xE = xE;
		this.yS = yS;
		this.yE = yE;

		// Cancel any previous rendering threads that might be running
		if (previousThread != null)
		{
			previousThread.killMe = true;
			previousThread.interrupt();
		}

		previousThread = this;
		start();
	}

	//sleeps for a minimal amount of time to create a short delay for repainting, then repaints with antialias on
	//used for prettier redraws after aliased drawing (in some situations)
	public void run()
	{
		this.setName("ScreenReadGrabber");
		setPriority(Thread.MIN_PRIORITY);

		Tablet.winMain.getReadsPanel().clear();

		try
		{
			Thread.sleep(delay);
		}
		catch (InterruptedException e) {}

		if (killMe)
			return;

		ArrayList<Read> reads = findVisibleReads();

		Tablet.winMain.getReadsPanel().setTableModel(reads);
	}

	private ArrayList<Read> findVisibleReads()
	{
		ArrayList<Read> reads = new ArrayList<>();

		for(int i=yS; i <= yE && i < manager.size(); i++)
		{
			ArrayList<Read> line = manager.getLine(i);

			for(int j=findStartRead(line); j < line.size() && line.get(j).s() < xE; j++)
			{
				Read found = line.get(j);
				if (found instanceof MateLink == false)
					reads.add(line.get(j));
			}
		}

		return reads;
	}

	/**
	 * (Binary) Searches for the first read in the current window, for the current
	 * line of the display. Returns its index within the reads ArrayList.
	 */
	private int findStartRead(ArrayList<Read> line)
	{
		int l = 0;
		int h = line.size();
		int m = 0;

		while(l < h)
		{
			m = l + ((h-l)/2);

			int windowed = line.get(m).compareToWindow(xS, xE);

			if(windowed == -1)
				l = m+1;
			else if(windowed == 0)
				break;
			else
				h = m;
		}

		// Binary search only guarantees finding a read in the window, must search
		// back to find first read in window
		Read read = line.get(m);
		while(m > 0 && read.s() > xS)
		{
			m--;
			read = line.get(m);
		}

		// Adjust if we've gone too far
		if (read.e() < xS)
			m++;

		return m;
	}
}