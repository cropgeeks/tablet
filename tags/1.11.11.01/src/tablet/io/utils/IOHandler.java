// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class which controls the reading and writing operations required to convert
 * from a Maq assembly to an ACE assembly.
 *
 */
public class IOHandler
{
	MaqFileReader reader;
	AceFileWriter writer;
	//List to temporarily store read data until it can be written in its correct
	//place in the ACE file.
	ArrayList<String[]> tempList = new ArrayList<String[]>();

	int contigs;;
	float fileSize;
	int contigLoop = 0;
	int loop = 0;
	int contigsWritten = 0;
	long[] contigReads;

	int consensusLength;
	int writtenBases;

	/**
	 * Constructor, takes a MaqFileReader and an AceFileWriter as its arguments.
	 *
	 * @param reader	The MaqFileReader object which handles reading from a
	 *					Maq assembly.
	 * @param writer	The AceFileWriter object which handles writing to an ACE
	 *					assembly.
	 */
	IOHandler(MaqFileReader reader, AceFileWriter writer)
	{
		this.reader = reader;
		this.writer = writer;
	}

	/**
	 * The method which carries out the conversion from Maq assembly to ACE
	 * assembly.
	 *
	 * @param option	The command line option (if any) passed to the program.
	 */
	public void run(String option)
		throws IOException
	{
		try
		{
			System.out.println();

			contigs = reader.readNoContigs();

			if(reader.getMaqFileSize() == 0)
				fileSize = 1;
			else
				fileSize = reader.getMaqFileSize();

			if(option == null)
				oneAceFile();
			else
			{
				if(option.equals("-b"))
					batched();
				else if(option.equals("-m"))
					onePerContig();
			}
			//Tidy up Buffered readers and writers.
			System.out.print("0% |**********| 100% - 100%" + "\r\n\n");
			System.out.println("Maq to ACE conversion complete.");
			reader.getInMaq().close();
			reader.getInFastQ().close();
			writer.getOut().close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("\n\nThere may have been an issue with conversion. Please ensure that the converted file matches what you expected.");
		}
		finally
		{
			reader.getInMaq().close();
			reader.getInFastQ().close();
			writer.getOut().close();
			System.out.println("consensus length " + consensusLength + " bq length " + writtenBases);
		}
	}

	/**
	 * Method to display the progress bar on the command line.
	 *
	 * @param totalContigs		The total number of contigs in the assembly.
	 * @param contigsWritten	The numebr of contigs written to file so far.
	 */
	public void progressBar(int totalContigs, int contigsWritten)
	{
		float percent = ((float)contigsWritten/(float)totalContigs)*100f;

		if(percent >= 0 && percent < 10)
			System.out.print("0% |          | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 10 && percent < 20)
			System.out.print("0% |*         | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 20 && percent < 30)
			System.out.print("0% |**        | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 30 && percent < 40)
			System.out.print("0% |***       | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 40 && percent < 50)
			System.out.print("0% |****      | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 50 && percent < 60)
			System.out.print("0% |*****     | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >=60 && percent < 70)
			System.out.print("0% |******    | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 70 && percent  < 80)
			System.out.print("0% |*******   | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 80 && percent < 90)
			System.out.print("0% |********  | 100% - " + Math.round(percent) + "%" + "\r");
		else if(percent >= 90 && percent < 100)
			System.out.print("0% |********* | 100% - " + Math.round(percent) + "%" + "\r");
	}

	/**
	 * Method to output a single (or group of contigs) to file. Reading from the
	 * Maq assembly and writing to the ACE assembly.
	 *
	 * @param contigLoop		The number of contigs to be written.
	 * @param totalContigs		The total number of contigs in the assenbly.
	 * @param contigsWritten	The number of contigs written so far.
	 * @param option			The command line argument passed by the user.
	 * @throws Exception
	 */
	public void processContigs(int contigLoop, int totalContigs, int contigsWritten, String option)
		throws IOException
	{
		for(int k=contigLoop; k > 0; k--)
		{
			//read a single contig from the Maq FASTQ file.
			reader.readFastqEntry();

			if(option != null && option.equals("-m"))
			{
				writer.setOut(new BufferedWriter(new FileWriter(writer.getFile().getPath() + "_" + new String(reader.getContigName().substring(1)) + "_" + reader.getConsensus().length() + ".ace")));
				writer.writeHeader(contigLoop, 0);
			}

			tempList = new ArrayList<String[]>();

			int reads = 0;
			String[] vars = reader.getReadInfo();
			while(reader.getContigName().equals("@"+vars[2]))
			{
				tempList.add(vars);
				reads++;
				vars = reader.getReadInfo();
			}

			consensusLength = reader.getConsensus().length();
			//Write the contig header to disk.
			writer.writeContig(new String(reader.getContigName().substring(1)), reader.getConsensus().toString(), true, reads+1);
			//Write the base qualities to disc.
			byte[] bq = reader.calculateBaseQualities(reader.getBaseQualities());
			writer.writeBaseQualities(bq);

			writer.writeReadInfo(".TabletReference", true, 1);

			//Write the read information to disk
			for(String[] readInfo : tempList)
			{
				writer.writeReadInfo(readInfo[0], readInfo[3].equals("-"), Integer.parseInt(readInfo[4]));
			}

			writer.writeBS(reader.getConsensus().toString(), ".TabletReference");

			writer.writeReadData(".TabletReference", reader.getConsensus().toString());

			//Write the read data to disk.
			for(String[] readInfo : tempList)
			{
				writer.writeReadData(readInfo[0], readInfo[1]);
			}
			//clear the temporary memory cache of read info.
			tempList.clear();

			contigsWritten++;

			//update the progress bar
			progressBar(totalContigs, contigsWritten);
		}
	}

	/**
	 * Method for producing output files which batch contigs together in such a
	 * way that each file has the same number of contigs.
	 *
	 * @throws IOException
	 */
	public void batched()
		throws IOException
	{
		contigLoop = (int)(contigs / fileSize);
		//loop = (int);
		if(fileSize < 1)
			loop = 1;
		else
			loop = (int)fileSize;

		int batchNo = 1;

		while(loop > 0)
		{
			writer.setOut(new BufferedWriter(new FileWriter(writer.getFile().getPath() + "_batch_" + batchNo + ".ace")));
			writer.writeHeader(contigLoop, 0);
			batchNo++;

			processContigs(contigLoop, contigs, contigsWritten, "-b");
			contigsWritten += contigLoop;

			writer.getOut().close();

			loop--;
		}

		while(contigs-contigsWritten > 1)
		{
			writer.setOut(new BufferedWriter(new FileWriter(writer.getFile().getPath() + "_batch_" + batchNo + ".ace")));
			writer.writeHeader(contigs, 0);

			processContigs(contigs-contigsWritten, contigs, contigsWritten, "-b");
			contigsWritten += contigs;
		}
	}

	/**
	 * Method which outputs one ACE file for each contig in the Maq assembly.
	 *
	 * @throws IOException
	 */
	public void onePerContig()
		throws IOException
	{
		contigLoop = 1;
		loop = contigs;

		while(loop > 0)
		{
			processContigs(contigLoop, contigs, contigsWritten, "-m");
			contigsWritten += contigLoop;

			writer.getOut().close();

			loop--;
		}
	}

	/**
	 * Method for outputting one ACE file for the entire Maq assembly.
	 *
	 * @throws IOException
	 */
	public void oneAceFile()
		throws IOException
	{
		contigLoop = contigs;

		contigReads = new long [1];

		writer.setOut(new BufferedWriter(new FileWriter(writer.getFile().getPath() + ".ace")));
		contigReads[0] = processReads(contigLoop);
		writer.getOut().close();
		reader.getInFastQ().close();
		reader.resetInFastQ();
		reader.getInMaq().close();
		reader.resetInMaq();
		reader.readNoContigs();
		writer.setOut(new BufferedWriter(new FileWriter(writer.getFile().getPath() + ".ace", true)));
		writer.writeHeader(contigLoop, contigReads[0]);

		processContigs(contigLoop, contigs, contigsWritten, null);
		contigsWritten += contigLoop;
	}

	public long processReads(int contigLoop) throws IOException
	{
		long totalReads = 0;
		String[] vars = new String[5];

		for(int k=contigLoop; k > 0; k--)
		{
			reader.readFastqEntry();

			System.arraycopy(reader.getReadInfo(), 0, vars, 0, 5);
			while(reader.getContigName().equals("@"+vars[2]))
			{
				tempList.add(vars);
				totalReads++;
				System.arraycopy(reader.getReadInfo(), 0, vars, 0, 5);
			}
		}
		return totalReads+1;
	}
}