// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io.utils;

import java.io.*;

/**
 * A class which provides facilities which allow a Maq assembly to be read one
 * line at a time. This class only makes available those elements of the Maq
 * assembly which would be required for Tablet.
 *
 */
public class MaqFileReader
{
	private File[] files;

	private BufferedReader inFastQ;
	private BufferedReader inMaq;

	private String contigName;
	// The consensus sequence
	private StringBuilder consensus = null;
	//the base qualities
	private StringBuilder baseQualities = null;
	private int contigs = 0;
	private float maqFileSize;

	// The index of the Maq file in the files[] array
	private int maqIndex = -1;
	// The index of the FASTQ file in the files[] array
	private int fastqIndex = -1;

	/**
	 * Constructor for MaqFileReader. Takes an array of files as its argument
	 * and sets up the BufferedReaders for both files.
	 *
	 * @param files			An array of File objects representing the  FASTQ and
	 *						.txt Maq files.
	 * @throws Exception
	 */
	public MaqFileReader(File[] files)
		throws IOException
	{
		this.files = files;

		//if we can read the files set up the buffered readers
		if(canRead())
		{
			inFastQ = new BufferedReader(new FileReader(files[fastqIndex]));
			inMaq = new BufferedReader(new FileReader(files[maqIndex]));
			maqFileSize = ((float)files[maqIndex].length() / (1024f*1024f*512f));
		}
	}

	/**
	 * Checks if the Maq file can be read. Tries each of the files in the files
	 * array against each filetype looking for matches in the files provided
	 * and those we expect.
	 *
	 * @return	whether or not the files are readable.
	 * @throws Exception
	 */
	boolean canRead()
		throws IOException
	{
		boolean foundMaq = false;
		boolean foundFastq = false;

		// We need to check each file to see if it is readable
		for (int i = 0; i < 2; i++)
		{
			if (isMaqFile(i))
			{
				foundMaq = true;
				maqIndex = i;
			}
			else if (isFastqFile(i))
			{
				foundFastq = true;
				fastqIndex = i;
			}
		}

		return (foundMaq && foundFastq);
	}

	/**
	 * Checks that we have a Maq file by assuming 16 columns of data.
	 *
	 * @param fileIndex	The index of the file being tested in the files array.
	 * @return	True if the file was a Maq file.
	 * @throws Exception
	 */
	private boolean isMaqFile(int fileIndex)
		throws IOException
	{
		inMaq = new BufferedReader(new FileReader(files[fileIndex]));

		String str = inMaq.readLine();

		boolean isMaqFile = (str != null && str.split("\t").length == 16);
		inMaq.close();

		return isMaqFile;
	}

	/**
	 * Checks that the file is a FastQ file by checking for a leading @.
	 *
	 * @param fileIndex	The index of the file being tested in the files array.
	 * @return	True if the file is a FastQ file.
	 * @throws Exception
	 */
	private boolean isFastqFile(int fileIndex)
		throws IOException
	{
		inFastQ = new BufferedReader(new FileReader(files[fileIndex]));

		String str = inFastQ.readLine();

		boolean isFastqFile = (str != null && str.startsWith("@"));
		inFastQ.close();

		return isFastqFile;
	}

	/**
	 * Reads a single entry from the FastQ file. This will be one whole contig
	 * with consensus information and base quality scores.
	 *
	 * @throws Exception
	 */
	public void readFastqEntry()
		throws IOException
	{

		consensus = null;
		baseQualities = null;

		String str = inFastQ.readLine();
		if(str != null)
		{
			//Check for the contig name marker.
			if (str.startsWith("@"))
			{
				consensus = new StringBuilder();

				contigName = str;
				str = inFastQ.readLine();
				//while the line does not begin with + (base qualities marker)
				//add lines to the consensus sequence.
				while(!str.startsWith("+"))
				{
					consensus.append(str.trim());
					str = inFastQ.readLine();
				}
			}

			if(str.startsWith("+"))
			{
				int length = consensus.length();
				baseQualities = new StringBuilder();
				//Process quality data to avoid incorrectly catching @ markers
				while(baseQualities.length() != length)
				{
					str = inFastQ.readLine();
					baseQualities.append(str.trim());
				}
			}
		}
	}

	/**
	 * Method to quickly parse through the file to discover the number of
	 * contigs contained in the assembly.
	 *
	 * @return	the number of contigs.
	 * @throws IOException
	 */
	public int readNoContigs()
		throws IOException
	{
		int linesRead = 0;

		String str = inFastQ.readLine();
		while(str != null)
		{
			if (str.startsWith("@"))
			{
				linesRead = 0;
				contigs++;

				str = inFastQ.readLine();
				while(!str.startsWith("+"))
				{
					linesRead++;
					str = inFastQ.readLine();
				}
			}

			//Process quality data to avoid incorrectly catching @ markers
			if(str.startsWith("+"))
			{
				int length = 0;
				str = inFastQ.readLine();
				while(length != linesRead)
				{
					length++;
					str = inFastQ.readLine();
				}
			}
		}
		inFastQ.close();
		inFastQ = new BufferedReader(new FileReader(files[fastqIndex]));
		return contigs;
	}

	/**
	 * Reads a single entry from the Maq file. Returns an array which contains
	 * all the information required from one entry in the Maq file.
	 *
	 * @return	A String array containing all the information required about 1
	 *			read for a Tablet .ace aasembly.
	 * @throws Exception
	 */
	public String[] getReadInfo()
		throws IOException
	{
		String string;
		String[] vars = new String[5];
		if((string = inMaq.readLine()) != null)
		{
			String[] tokens = string.split("\t");
			//read name
			vars[0] = new String(tokens[0]);
			//read data
			vars[1] = new String(tokens[14]);
			//chromosome
			vars[2] = new String(tokens[1]);
			//strand
			vars[3] = new String(tokens[3]);
			//read position
			vars[4] = new String(tokens[2]);
		}
		return vars;
	}

	/**
	 * Calculates the base qualities in bytes from a StringBuilder containing
	 * String data.
	 *
	 * @param qlt	A StringBuilder object which contains the quality data.
	 * @return		A byte array containing the base qualities.
	 */
	public byte[] calculateBaseQualities(StringBuilder qlt)
	{
		byte[] bq = new byte[qlt.length()];
		for(int i = 0; i < bq.length -1; i++)
		{
			bq[i] = (byte) (qlt.charAt(i) - 33);
			if(bq[i] > 100)
				bq[i] = 100;
		}
		return bq;
	}

	/**
	 * Accessor for name. Returns the name of the contig being read from.
	 *
	 * @return	contigName, the name of the contig being read from.
	 */
	public String getContigName()
	{
		return contigName;
	}

	/**
	 * Accessor for the consensus sequence of the current contig.
	 *
	 * @return	consensus. A StringBuilder object containing the consensus
	 *			sequence for the contig being read from.
	 */
	public StringBuilder getConsensus()
	{
		return consensus;
	}

	/**
	 * Accessor for baseQualities. Returns the base quality scores for the
	 * consensus sequence.
	 *
	 * @return	baseQualities. A StringBuilder object containing the base quality
	 * scores for the consensus sequence.
	 */
	public StringBuilder getBaseQualities()
	{
		return baseQualities;
	}

	/**
	 * Accessor for the Maq file's BufferedReader.
	 *
	 * @return	inMaq. The Maq file's BufferedReader.
	 */
	public BufferedReader getInMaq()
	{
		return inMaq;
	}

	/**
	 * Accessor for files array.
	 *
	 * @return	files. The array of files containing the Maq file and the FastQ
	 *			file.
	 */
	public File getFiles()
	{
		return files[maqIndex];
	}

	/**
	 * Accessor for the FastQ file's BufferedReader.
	 *
	 * @return	inFastQ. The BufferedReader for the FastQ file.
	 */
	public BufferedReader getInFastQ()
	{
		return inFastQ;
	}

	public void resetInFastQ() throws FileNotFoundException
	{
		inFastQ = new BufferedReader(new FileReader(files[fastqIndex]));
	}

	public void resetInMaq() throws FileNotFoundException
	{
		inMaq = new BufferedReader(new FileReader(files[maqIndex]));
	}

	/**
	 * Returns the size in Gb of the Maq file.
	 *
	 * @return	maqFileSize. The size in Gb of the Maq file.
	 */
	public float getMaqFileSize()
	{
		return maqFileSize;
	}

}