// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;
import java.util.zip.*;

/**
 * Class that represents an assembly file, that may be a traditional file on
 * disk, or a reference to a file located on the web (in http:// format).
 */
public class AssemblyFile
{
	private String filename;

	private URL url;
	private File file;

	public AssemblyFile(String filename)
	{
		this.filename = filename;

		try
		{
			url = new URL(filename);
		}
		catch (MalformedURLException e)
		{
			file = new File(filename);
		}
	}

	public String getPath()
	{
		return filename;
	}

	public String getName()
	{
		// Return either the name of the file
		if (file != null)
			return file.getName();

		// Or parse the URL to determine the filename part of it:
		// http://someserver/somefolder/file.ext?argument=parameter
		//                              ^^^^^^^^
		else
		{
			String name = filename;

			if (name.indexOf("?") != -1)
				name = name.substring(0, name.indexOf("?"));

			int slashIndex = name.lastIndexOf("/");
			if (slashIndex != -1)
				name = name.substring(slashIndex + 1);

			return name;
		}
	}

	long length()
	{
		if (file != null)
			return file.length();

		// This might fail, but it doesn't matter, as any subsequent load will
		// fail too, and the error can be caught then
		try
		{
			URLConnection conn = url.openConnection();
			conn.getInputStream().close();

			return conn.getContentLength();
		}
		catch (Exception e) { return 0; }
	}

	// Returns the input stream for this file
	InputStream getInputStream()
		throws Exception
	{
		if (file != null)
			return new FileInputStream(file);

		return url.openStream();
	}

	// Returns an input stream suitable for use by a Reference-File reader,
	// which might be a normal input stream, or a gzipped input stream
	InputStream getReferenceInputStream()
		throws Exception
	{
		InputStream is = getInputStream();

		try
		{
			return new GZIPInputStream(is);
		}
		catch (Exception e)
		{
			is.close();
		}

		return getInputStream();
	}

	URL getURL()
	{
		return url;
	}

	boolean isURL()
	{
		return url != null;
	}

	File getFile()
	{
		return file;
	}
}