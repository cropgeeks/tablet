// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io.samtools;

import java.io.*;
import java.util.*;

public abstract class StreamCatcher extends Thread
{
	private BufferedReader reader = null;
	private boolean isRunning = true;

	public StreamCatcher(InputStream in)
	{
		reader = new BufferedReader(new InputStreamReader(in));
		start();
	}

	public void run()
	{
		try
		{
			String line = reader.readLine();
			StringTokenizer st = null;

			while (line != null)
			{
				processLine(line);
				line = reader.readLine();
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}

		try { reader.close(); }
		catch (IOException e) {}

		isRunning = false;
	}

	boolean isRunning()
	{ return isRunning; }

	protected abstract void processLine(String line);
}