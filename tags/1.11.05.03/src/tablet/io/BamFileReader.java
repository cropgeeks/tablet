// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;

import net.sf.samtools.*;

public class BamFileReader extends TrackableReader
{
	private IReadCache readCache;
	private ReadSQLCache nameCache;
	private File cacheDir;
	private String cacheid;

	private ReferenceFileReader refReader;
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	// We need (eventually) the bam file itself, its index, and reference data
	private AssemblyFile bamFile;
	private AssemblyFile baiFile;
	private AssemblyFile refFile;

	// Status tracks what's happening (0=get ref, 1=get bai, 2=open bam)
	private static int status = 0;

	private boolean refLengthsOK = true;

	BamFileReader()
	{
	}

	BamFileReader(IReadCache readCache, ReadSQLCache nameCache, File cacheDir, String cacheid)
	{
		this.readCache = readCache;
		this.nameCache = nameCache;
		this.cacheDir = cacheDir;
		this.cacheid = cacheid;
	}

	public void setInputs(AssemblyFile[] files, Assembly assembly)
	{
		this.files = files;
		this.assembly = assembly;
	}

	public void runJob(int index) throws Exception
	{
		// Get a reference to the BAM file
		bamFile = files[ASBINDEX];

		// Get a reference to the reference file (if it exists)
		if (files.length > 1 && files[REFINDEX].isReferenceFile())
			refFile = files[REFINDEX];


		// Do we have an index file?
		AssemblyFile bai1 = getBaiIndexFile(bamFile, false);
		AssemblyFile bai2 = getBaiIndexFile(bamFile, true);

		if (bai1.exists())
			baiFile = bai1;
		else if (bai2.exists())
			baiFile = bai2;

		if (baiFile == null)
			throw new IOException("An index file could not be found "
				+ "(" + bai1.getName() + " or " + bai2.getName()
				+ ").\nYou may need to use samtools to generate one.");


		// Fake up an AssemblyFile[] array for TrackableReader
		if (baiFile.isURL() && refFile != null)
			files = new AssemblyFile[] { refFile, baiFile };
		else if (baiFile.isURL())
			files = new AssemblyFile[] { baiFile };
		else if (refFile != null)
			files = new AssemblyFile[] { refFile };

		super.setInputs(files, assembly);


		if (okToRun && refFile != null)
			readReferenceFile();

		if (okToRun)
			downloadBaiFile();

		if (okToRun)
		{
			BamFileHandler bamHandler = new BamFileHandler(readCache, nameCache, bamFile, baiFile, assembly);
			status = 2;

			bamHandler.openBamFile(contigHash);

			assembly.setBamHandler(bamHandler);
			assembly.setName(bamFile.getName());

			refLengthsOK = bamHandler.refLengthsOK();
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
		status = 0;

		refReader = new ReferenceFileReader(assembly, contigHash);

		in = new BufferedReader(new InputStreamReader(getInputStream(0), "ASCII"));

		refReader.readReferenceFile(this, refFile);

		in.close();
	}

	private void downloadBaiFile() throws Exception
	{
		status = 1;

		// We only need to download it if it's not already held locally
		if (baiFile.isURL() == false)
			return;

		// Are we after the first or second entry in the array?
		int baiIndex = files.length == 1 ? 0 : 1;

		File dir = new File(cacheDir, "Tablet-"+cacheid+"-"+baiFile.getName());
		dir.mkdir();

		File file = new File(dir, baiFile.getName());

		BufferedInputStream inputStream = new BufferedInputStream(
			getInputStream(baiIndex));
		BufferedOutputStream outputStream = new BufferedOutputStream(
			new FileOutputStream(file.getAbsolutePath()));

		int i;
		while((i = inputStream.read()) != -1 && okToRun)
			outputStream.write(i);

		inputStream.close();
		outputStream.close();

		baiFile = new AssemblyFile(file.getPath());
	}

	public String getMessage()
	{
		switch (status)
		{
			case 0: return RB.format("io.BamFileReader.fasta",
				getTransferRate(), contigHash.size());

			case 1:	return RB.format("io.BamFileReader.bai", getTransferRate());

			case 2:
				isIndeterminate = true;
				return RB.getString("io.BamFileReader.bam");
		}

		return "";
	}

	protected boolean refLengthsOK()
		{ return refLengthsOK; }
}