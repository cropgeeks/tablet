// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;

import tablet.io.samtools.*;


/**
 * Class that represents an assembly file, that may be a traditional file on
 * disk, or a reference to a file located on the web (in http:// format).
 */
public class AssemblyFile implements Comparable<AssemblyFile>
{
	public static final int UNKNOWN = 0;
	public static final int ACE     = 1;
	public static final int AFG     = 2;
	public static final int SAM     = 3;
	public static final int BAM     = 4;
	public static final int MAQ     = 5;
	public static final int SOAP    = 6;
	public static final int FASTA   = 20;
	public static final int FASTQ   = 21;
	public static final int GFF3    = 40;
	public static final int BED     = 41;
	public static final int VCF		= 42;
	public static final int GTF		= 43;

	public static final int TABLET  = 100;

	private String filename;
	private URL url;
	private File file;
	private boolean isCompressed;

	private int type = UNKNOWN;

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

	public boolean equals(Object obj)
	{
		if (obj == null || obj.getClass() != getClass())
            return false;

		return ((AssemblyFile)obj).getPath().equals(getPath());
	}

	public int getType()
		{ return type; }

	public String getPath()
		{ return filename; }

	/**
	 * Returns true if this file is an actual ASSEMBLY file (ie, not a reference
	 * or GFF3 file, etc).
	 */
	public boolean isAssemblyFile()
	{
		return (type > 0 && type < 20);
	}

	public boolean isTabletFile()
	{
		return type == TABLET;
	}

	public boolean isAnnotationFile()
	{
		return type == GFF3 || type == BED || type == VCF || type == GTF;
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

	public long length()
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

	boolean exists()
	{
		if (file != null)
			return file.exists();

		try
		{
			URLConnection conn = url.openConnection();
			conn.getInputStream().close();

			return true;
		}
		catch (Exception e) { return false; }
	}

	/**
	 * Returns the last modified time of the file (if it *is* a file, otherwise
	 * it just returns 0 for a URL file).
	 */
	public long modified()
	{
		if (isURL())
			return 0;

		return file.lastModified();
	}

	// Returns the main input stream for this file
	InputStream getInputStream()
		throws Exception
	{
		if (file != null)
			return new FileInputStream(file);

		return url.openStream();
	}

	// Returns an input stream suitable for use reading from, which might be a
	// a normal input stream, or a gzipped input stream
	InputStream getDataStream()
		throws Exception
	{
		InputStream is = getInputStream();

		System.out.println();
		System.out.println("testing " + file);

		try
		{
			isCompressed = true;

			return new GZIPInputStream(is);
		}
		catch (Exception e)
		{
			isCompressed = false;
			is.close();
		}

		return getInputStream();
	}

	boolean isCompressed()
		{ return isCompressed; }

	URL getURL()
	{
		return url;
	}

	public boolean isURL()
	{
		return url != null;
	}

	public File getFile()
	{
		return file;
	}

	boolean isReferenceFile()
	{
		return (type == FASTA || type == FASTQ);
	}

	public int compareTo(AssemblyFile other)
	{
		if (type < other.type)
			return -1;
		else if (type == other.type)
			return  0;
		else
			return  1;
	}

	/**
	 * Determine the "type" of this AssemblyFile; in terms of types that are
	 * recognised by Tablet. So far, every type can be uniquely distingished by
	 * looking at just the first "line" of the file (including binary BAM).
	 */
	public boolean canDetermineType()
	{
		try
		{
			// Read the first line of the file
			String str = getFirstLine();

			if (str != null)
			{
				if (isTablet(str))
					type = TABLET;

				else if (isAce(str))
					type = ACE;
				else if (isAfg(str))
					type = AFG;
				else if (isSam(str))
					type = SAM;
				else if (isBam(str))
					type = BAM;
				else if (isMaq(str))
					type = MAQ;
				else if (isGtf(str))
					type = GTF;
				else if (isSoap(str))
					type = SOAP;
				else if (isFasta(str))
					type = FASTA;
				else if (isFastq(str))
					type = FASTQ;
				else if (isGff3(str))
					type = GFF3;
				else if (isBed(str))
					type = BED;
				else if (isVcf(str))
					type = VCF;
			}

/*			if (type == UNKNOWN)
			{
				SAMFileReader reader = new SAMFileReader(getInputStream());
				if (reader.isBinary())
					type = BAM;
				reader.close();
			}
*/
		}
		catch (Exception e) { System.out.println(e);}

		return (type != UNKNOWN);
	}

	private boolean isTablet(String str)
	{
		return str.startsWith("<tablet");
	}

	private boolean isAce(String str)
	{
		return str.startsWith("AS ");
	}

	private boolean isAfg(String str)
	{
		return str.startsWith("{");
	}

	private boolean isSam(String str)
	{
		// Can we match on the header?
		if (str.startsWith("@HD") ||
				str.startsWith("@SQ") ||
				str.startsWith("@RG") ||
				str.startsWith("@PG") ||
				str.startsWith("@CO"))
			{
				// "@" also matches FASTAQ, so check we have a tabbed line
				if (str.split("\t").length >= 2)
					return true;
			}

		// If it didn't start with a header, then it should be an alignment line
		String[] tokens = str.split("\t");
		if (tokens.length >= 11)
		{
			// The 2nd, 4th and 5th columns should be a number
			try
			{
				Integer.parseInt(tokens[1]);
				Integer.parseInt(tokens[3]);
				Integer.parseInt(tokens[4]);

				return true;
			}
			catch (Exception e) {}
		}

		return false;
	}

	private boolean isBam(String str)
	{
		return (str.length() >= 4 && str.substring(0, 4).equals("BAM\1"));
	}

	private boolean isMaq(String str)
	{
		String[] tokens = str.split("\t");

		// We're looking for 16 columns, with col4 being either "-" or "+"
		return (tokens.length == 16
			&& (tokens[3].equals("-") || tokens[3].equals("+")));
	}

	private boolean isSoap(String str)
	{
		String[] tokens = str.split("\t");

		// We're looking for > 7 columns, with col7 being either "-" or "+"
		return (tokens.length > 7
			&& (tokens[6].equals("-") || tokens[6].equals("+")));
	}

	private boolean isFasta(String str)
	{
		return str.startsWith(">");
	}

	private boolean isFastq(String str)
	{
		return str.startsWith("@");
	}

	private boolean isGff3(String str)
	{
		if (str.trim().toLowerCase().startsWith("##gff-version"))
		{
			try
			{
				// Deals with "##gff-version3" "##gff-version 3" etc
				if (Integer.parseInt(str.substring(13).trim()) == 3)
					return true;
			}
			catch (Exception e) {}
		}

		return false;
	}

	private boolean isBed(String str)
	{
		// The file *may* starts with a "track " comment
		if (str.trim().toLowerCase().startsWith("track "))
			return true;

		// Failing that, if the 2nd and 3rd columns are numbers, then it could
		// be a bed file
		try
		{
			String[] tokens = str.split("\t");
			Integer.parseInt(tokens[1]);
			Integer.parseInt(tokens[2]);
			return true;
		}
		catch (Exception e) {}

		return false;
	}

	private boolean isVcf(String str)
	{
		return str.trim().toLowerCase().startsWith("##fileformat=vcf");
	}

	private boolean isGtf(String str)
	{
		String[] tokens = str.split("\t");
		if (tokens.length < 9)
			return false;
		else
			return str.split("\t")[8].toLowerCase().trim().startsWith("gene_id");
	}

	// Attempts to read the first 2048 bytes of the file, converting the stream
	// into a string that is then split by line separator and the first line
	// returned (if any).
	private String getFirstLine()
	{
		try
		{
			Reader rd = new InputStreamReader(getDataStream());//, "ASCII");
	        char[] buf = new char[2048];

			int num = rd.read(buf);
			rd.close();

			for (int c = 0; c < num; c++)
				if (buf[c] == '\n' || buf[c] == '\r')
					return new String(buf, 0, c);

			return new String(buf);
		}
		catch (Exception e) { e.printStackTrace(); }

		return null;
	}
}