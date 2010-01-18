// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;

/**
 * Class that represents an assembly file, that may be a traditional file on
 * disk, or a reference to a file located on the web (in http:// format).
 */
class AssemblyFile
{
	private String filename;

	private URL url;
	private File file;

	AssemblyFile(String filename)
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

	String getName()
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
		try { return url.openConnection().getContentLength(); }
		catch (Exception e) { return 0; }
	}

	InputStream getInputStream()
		throws Exception
	{
		if (file != null)
			return new FileInputStream(file);

		return url.openStream();
	}
}