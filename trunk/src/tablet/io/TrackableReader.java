package tablet.io;

import java.io.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.file.*;

abstract class TrackableReader extends SimpleJob
{
	// Read data
	protected File file;
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	protected Assembly assembly;

	void setInputs(File file, Assembly assembly)
	{
		this.file = file;
		this.assembly = assembly;
	}

	protected String readLine()
		throws IOException
	{
		lineCount++;
		return in.readLine();
	}

	Assembly getAssembly()
		{ return assembly; }

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return 50000; }

	public int getValue()
	{
		if (is == null)
			return 0;

		float bytesRead = is.getBytesRead();
		float size = is.getSize();

		return Math.round((bytesRead / size) * 50000);
	}

	protected ProgressInputStream getInputStream()
		throws Exception
	{
		is = new ProgressInputStream(new FileInputStream(file));
		is.setSize(file.length());

		return is;
	}

	/** Returns true if this reader can understand the file given to it. */
	abstract boolean canRead()
		throws Exception;
}