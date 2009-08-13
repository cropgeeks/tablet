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
	protected File[] files;
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	protected Assembly assembly;

	void setInputs(File[] files, Assembly assembly)
	{
		this.files = files;
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
		{ return 5555; }

	public int getValue()
	{
		if (is == null)
			return 0;

		float bytesRead = is.getBytesRead();
		float size = is.getSize();

		return Math.round((bytesRead / size) * 5555);
	}

	protected ProgressInputStream getInputStream(int index)
		throws Exception
	{
		is = new ProgressInputStream(new FileInputStream(files[index]));
		is.setSize(files[index].length());

		return is;
	}

	/** Returns true if this reader can understand the file given to it. */
	abstract boolean canRead()
		throws Exception;
}