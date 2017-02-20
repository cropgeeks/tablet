// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

public class GFF3Reader extends FeatureReader
{
	public GFF3Reader(String filename, Assembly assembly)
	{
		super(filename, assembly);
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));

		while ((str = readLine()) != null && okToRun)
		{
			// http://modencode.oicr.on.ca/validate_gff3_online/validate_gff3.html

			// If we find these directives, just quit, as the rest of the file
			// isn't going to be GFF stuff any more
			if (str.startsWith(">") || str.startsWith("##FASTA"))
				break;

			// Ignore blank lines or comment lines
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			// Split the line into its 9 tokens
			String[] tokens = str.split("\t");
			if (tokens.length != 9)
				throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

			processFeature(tokens);
		}

		in.close();

		// If everything loaded in ok, then we can assign what was found
		assignFeatures();
	}

	private void processFeature(String[] tokens)
		throws Exception
	{
		String contigName = URLDecoder.decode(tokens[0], "UTF-8");
		int start = Integer.parseInt(tokens[3]);
		int end   = Integer.parseInt(tokens[4]);

		// See if we can parse out a name
		// A name must be formed from "Name=[name];" (not name=) and special
		// characters must be URL encoded. The ending ; won't be there if it's
		// the last tag in the string
		String name = "";
		int index1 = tokens[8].indexOf("Name=");
		int offset = 5;
		// If Name can't be found, can we use ID instead
		if (index1 == -1)
		{
			index1 = tokens[8].indexOf("ID=");
			offset = 3;
		}

		if (index1 != -1)
		{
			int index2 = tokens[8].indexOf(";", index1);
			if (index2 != -1)
				name = tokens[8].substring(index1+offset, index2);
			else
				name = tokens[8].substring(index1+offset);

			name = URLDecoder.decode(name, "UTF-8");
		}

		String gffType = new String(tokens[2].toUpperCase());
		Feature f = new Feature(gffType, name, start-1, end-1);

		// Can we split the tags
		String[] tags = tokens[8].split(";");
		f.setTags(tags);

		getFeatures(contigName).add(f);
		cRead++;
	}
}