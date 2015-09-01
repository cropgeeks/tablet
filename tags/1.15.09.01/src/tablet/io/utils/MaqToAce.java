// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io.utils;

import java.io.File;

public class MaqToAce
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
			throws Exception
	{
		String fastq = null, maqtxt = null, directory = null, filename = null, option = null;

		if(args[0].equals("--help"))
		{
			usage();
			return;
		}

		if(args.length > 5)
		{
			System.out.println("\nToo many arguments provided.");
			usage();
			return;
		}
		else if(args.length < 4)
		{
			System.out.println("\nToo few arguments provided.");
			usage();
			return;
		}

		for(String arg : args)
		{
			if(arg.startsWith("-fastq="))
				fastq = arg.substring(7);
			else if(arg.startsWith("-maqtxt="))
				maqtxt = arg.substring(8);
			else if(arg.startsWith("-dir="))
				directory = arg.substring(5);
			else if(arg.startsWith("-filename="))
				filename = arg.substring(10);
			else if(arg.equals("-b"))
				option = "-b";
			else if(arg.equals("-m"))
				option = "-m";
		}

        File[] files = new File[2];

		if(fastq != null && maqtxt != null)
		{
			files[0] = new File(fastq);
			files[1] = new File(maqtxt);
		}
		else
		{
			System.out.println("One of the required Maq files has not been supplied.");
			usage();
			return;
		}

		AceFileWriter writer;
		MaqFileReader reader = new MaqFileReader(files);

		if(directory != null && filename != null)
		{
			writer = new AceFileWriter(directory, filename);
		}
		else
		{
			System.out.println("Either the directory path, or filename have not been supplied.");
			usage();
			return;
		}

		IOHandler handler = new IOHandler(reader, writer);
		handler.run(option);
	}

	public static void usage()
	{
		System.out.println("\nUsage:\n");
		System.out.println("[-fastq=sequence.fastq] [-maqtxt=sequence.txt] [-dir=/path/to/directory] [-filename=chosen_filename] [options]");
		System.out.println("\nOptions:");
		System.out.println("\t -b output is batched into a number of files with the same number of contigs as each other.");
		System.out.println("\t -m output one ace file for each contig.");
		System.out.println("\nIf no options are selected a single ace file is produced containing all the contigs in the Maq assembly.");
	}
}