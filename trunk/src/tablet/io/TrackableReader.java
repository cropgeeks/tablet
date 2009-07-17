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
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	protected Assembly assembly;

	void setInputs(ProgressInputStream is, Assembly assembly)
	{
		this.is = is;
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
		float bytesRead = is.getBytesRead();
		float size = is.getSize();

		return Math.round((bytesRead / size) * 50000);
	}
}