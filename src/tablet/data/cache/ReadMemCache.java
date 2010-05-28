// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.util.*;

import tablet.data.*;

public class ReadMemCache implements IReadCache
{
	private ArrayList<ReadMetaData> cache = new ArrayList<ReadMetaData>();

	public void openForWriting()
		throws IOException
	{}

	public void openForReading()
		throws IOException
	{}

	public void close()
		throws IOException
	{}

	public ReadMetaData getReadMetaData(int id, boolean dataOnly)
		{ return cache.get(id); }

	public void setReadMetaData(ReadMetaData readMetaData)
		throws Exception
		{ cache.add(readMetaData); }
}