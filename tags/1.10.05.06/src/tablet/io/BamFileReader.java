package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;
import scri.commons.file.*;

import net.sf.samtools.*;

public class BamFileReader extends TrackableReader
{
	private IReadCache readCache;
	private File cacheDir;
	private String cacheid;

	private ReferenceFileReader refReader;
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	// We need (eventually) the bam file itself, its index, and reference data
	private AssemblyFile bamFile;
	private AssemblyFile baiFile;
	private AssemblyFile refFile;

	// Status tracks what's happening (0=get ref, 1=get bai, 2=open bam)
	private int status = 0;

	// The Picard object we're ultimately trying to create
	private SAMFileReader bamReader;

	BamFileReader()
	{
	}

	BamFileReader(IReadCache readCache, File cacheDir, String cacheid)
	{
		this.readCache = readCache;
		this.cacheDir = cacheDir;
		this.cacheid = cacheid;
	}

	public void setInputs(AssemblyFile[] files, Assembly assembly)
	{
		this.files = files;
		this.assembly = assembly;
	}

	// Checks to see if we have been given a FASTA and a BAM file
	public boolean canRead() throws Exception
	{
		refReader = new ReferenceFileReader(assembly, contigHash);

		for (int i = 0; i < files.length; i++)
		{
			// Check to see if we even have a BAM file
			if (files[i].getName().toLowerCase().endsWith(".bam") && files[i].exists())
			{
				bamFile = files[i];

				// If so, do we have an index file that goes with it?
				AssemblyFile file1 = getBaiIndexFile(files[i], false);
				AssemblyFile file2 = getBaiIndexFile(files[i], true);

				if (file1.exists())
					baiFile = file1;
				else if (file2.exists())
					baiFile = file2;

				if (baiFile == null)
					throw new IOException("An index file could not be found "
						+ "(" + file1.getName() + " or " + file2.getName()
						+ ").\nYou may need to use samtools to generate one.");
			}

			else if (refReader.canRead(files[i]) != AssemblyFileHandler.UNKNOWN)
				refFile = files[i];
		}

		return (bamFile != null && baiFile != null);
	}

	public void runJob(int index) throws Exception
	{
		if (refFile == null)
			throw new IOException("No FASTA reference file was provided. "
				+ "Tablet cannot load BAM files without a reference.");

		// Fake up an AssemblyFile[] array for TrackableReader
		if (baiFile.isURL())
			files = new AssemblyFile[] { refFile, baiFile };
		else
			files = new AssemblyFile[] { refFile };

		super.setInputs(files, assembly);


		if (okToRun)
			readReferenceFile();
		if (okToRun)
			downloadBaiFile();
		if (okToRun)
			openBamFile();

		if (okToRun)
		{
			BamFileHandler bamHandler = new BamFileHandler(readCache, bamReader, assembly);
			assembly.setBamHandler(bamHandler);

			assembly.setName(bamFile.getName());
		}
	}

	private AssemblyFile getBaiIndexFile(AssemblyFile file, boolean typeTwo)
	{
		String name = file.getName();
		String newName = name + ".bai";

		if (typeTwo)
			newName = name.substring(0, name.lastIndexOf(".bam")) + ".bai";

		return new AssemblyFile(file.getPath().replaceAll(name, newName));
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(0, true), "ASCII"));

		refReader.readReferenceFile(this, refFile);

		in.close();
	}

	private void downloadBaiFile() throws Exception
	{
		status = 1;

		// We only need to download it if it's not already held locally
		if (baiFile.isURL() == false)
			return;

		File file = new File(cacheDir, "Tablet-"+cacheid+baiFile.getName());

		BufferedInputStream inputStream = new BufferedInputStream(
			getInputStream(1, true));
		BufferedOutputStream outputStream = new BufferedOutputStream(
			new FileOutputStream(file.getAbsolutePath()));

		int i;
		while((i = inputStream.read()) != -1 && okToRun)
			outputStream.write(i);

		inputStream.close();
		outputStream.close();

		baiFile = new AssemblyFile(file.getPath());
	}

	private void openBamFile() throws Exception
	{
		status = 2;

		AssemblyFile bam = bamFile;
		AssemblyFile bai = baiFile;

		if (bam.isURL())
			bamReader = new SAMFileReader(bam.getURL(), bai.getFile(), false);
		else
			bamReader = new SAMFileReader(bam.getFile(), bai.getFile());
	}

	public String getMessage()
	{
		switch (status)
		{
			case 0: return RB.format("io.BamFileReader.fasta",
				getTransferRate(), contigHash.size());

			case 1:	return RB.format("io.BamFileReader.bai", getTransferRate());

			case 2: return RB.getString("io.BamFileReader.bam");
		}

		return "";
	}
}