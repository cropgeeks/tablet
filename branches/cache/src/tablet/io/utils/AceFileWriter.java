package tablet.io.utils;

import java.io.*;

/**
 * Provides facilites which allow an ACE file to be written one line at a time.
 *
 */
public class AceFileWriter
{
	private BufferedWriter out;
	private File filepath;
	private String lineSeparator = System.getProperty("line.separator");

	/**
	 * Constructor which takes the filepath for the potential production of ACE
	 * files.
	 *
	 * @param filepath	Desired path to output files barring any file extension.
	 * @throws IOException
	 */
	public AceFileWriter(String directoryPath, String filename)
		throws IOException
	{
		this.filepath = new File(directoryPath, filename);
		File file = new File(directoryPath);
		if(!file.exists())
		{
			file.mkdir();
		}
	}

	/**
	 * Method to write the header of an ACE file. Takes the number of contigs to
	 * be contained in the ACE file as an argument.
	 *
	 * @param contigs	The number of contigs to be contained in the ACE file.
	 * @throws IOException
	 */
	public void writeHeader(int contigs)
		throws IOException
	{
		String header = "AS " + contigs + " 0" + lineSeparator + lineSeparator;
		out.write(header, 0, header.length());
	}

	/**
	 * Method which allows a contig header to be written to file.
	 *
	 * @param name			The name of the contig.
	 * @param consensus		The consensus sequence for the contig.
	 * @param complement	Whether or not the contig is complemented.
	 * @param reads			The number of reads held in the contig.
	 * @throws IOException
	 */
	public void writeContig(String name, String consensus, boolean complement, int reads)
		throws IOException
	{
		char complemented;
		if(complement)
			complemented = 'C';
		else
			complemented = 'U';
		String contigHeader = "CO " + name + " " + consensus.length() + " " + reads + " 00" + " " + complemented + lineSeparator;
		out.write(contigHeader, 0, contigHeader.length());
		contigHeader = consensus + lineSeparator + lineSeparator;
		out.write(contigHeader, 0, contigHeader.length());
	}

	/**
	 * Method which writes the base qualities for a contig.
	 *
	 * @param qualities		A byte array containing the base qualities to be
	 *						written.
	 * @throws IOException
	 */
	public void writeBaseQualities(byte[] qualities)
		throws IOException
	{
		out.write("BQ" + lineSeparator);
		for(byte quality : qualities)
		{
			if(quality != -1)
				out.write(quality + " ");
			else
				out.write("  ");
		}
		out.newLine();
		out.newLine();
	}

	/**
	 * Method to write information about a single read to file.
	 *
	 * @param name			The name of the read.
	 * @param complement	Whether or not the read is complemented.
	 * @param pos			The start position of the read.
	 * @throws IOException
	 */
	public void writeReadInfo(String name, boolean complement, int pos)
		throws IOException
	{
		char complemented;
		String readName;

		if(complement)
			complemented = 'C';
		else
			complemented = 'U';
		
		readName = "AF " + name + " " + complemented + " " + pos;
		out.write(readName, 0, readName.length());
		out.newLine();
	}

	/**
	 * Method which writes the read data itself to file.
	 *
	 * @param name	The read's name.
	 * @param data	The read itself.
	 * @throws IOException
	 */
	public void writeReadData(String name, String data)
		throws IOException
	{
		String readData;

		readData = "RD " + name + " " + data.length() + " " + 0 + " " + 0 + lineSeparator;
		out.write(readData, 0, readData.length());
		readData = data + lineSeparator + lineSeparator;
		out.write(readData, 0, readData.length());
	}

	/**
	 * Accessor for the ACE file's BufferedWriter.
	 *
	 * @return	out. The BufferedReader for the ACE file.
	 */
	public BufferedWriter getOut()
	{
		return out;
	}

	/**
	 * Method which allows the BufferedWriter to be set.
	 *
	 * @param out	The BufferedWriter to use to write to the ACE file.
	 */
	public void setOut(BufferedWriter out)
	{
		this.out = out;
	}

	/**
	 * Accessor which returns the filepath of the ACE file(s).
	 *
	 * @return	filepath. The filepath for the ACE files(s) to be written to.
	 */
	public File getFile()
	{
		return filepath;
	}
}
