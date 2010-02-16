package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.file.*;

import net.sf.samtools.*;

public class BamFileReader extends TrackableReader
{
	private IReadCache readCache;
	private File cacheDir;
	private String cacheid;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int bamFile = -1;
	private int refFile = -1;
	private AssemblyFile bamIndexFile, faiIndexFile;

	private boolean isIndeterminate = true;
	private long baiBytes, fastaBytes;
	private String message;

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
		for (int i = 0; i < files.length; i++)
		{
			if (canReadFasta(files[i]))
				refFile = i;

			else
			{
				// Check to see if we even have a BAM file
				if (files[i].getName().toLowerCase().endsWith(".bam") && files[i].exists())
				{
					bamFile = i;

					// If so, do we have an index file that goes with it?
					AssemblyFile file1 = getBaiIndexFile(files[i], false);
					AssemblyFile file2 = getBaiIndexFile(files[i], true);

					if (file1.exists())
						bamIndexFile = file1;
					else if (file2.exists())
						bamIndexFile = file2;

					//TODO: If index doesn't exist inform user?
					if(bamIndexFile == null)
						throw new Exception("Index file missing");
				}
			}
		}

		return (bamFile >= 0 && bamIndexFile != null && refFile >= 0);
	}

	// Checks to see if the given file is FASTA
	private boolean canReadFasta(AssemblyFile file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(
			new InputStreamReader(file.getInputStream()));

		String str = in.readLine();
		boolean isFastaFile = (str != null && str.startsWith(">"));
		in.close();

		return isFastaFile;
	}

	public void runJob(int index) throws Exception
	{
		// TODO: 5555?
		maximum = (int) files[refFile].length() + (int) bamIndexFile.length();

		System.out.println("Reading FASTA...");
		isIndeterminate = false;
		readReferenceFile(files[refFile]);

		downloadBaiFile();
		isIndeterminate = true;

		message = "Opening BAM...";
		System.out.println("Opening BAM...");
		openBamFile();

		if (okToRun)
		{
			BamFileHandler bamHandler = new BamFileHandler(readCache, bamReader, assembly);
			assembly.setBamHandler(bamHandler);
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

	private void readReferenceFile(AssemblyFile file)
		throws Exception
	{
		message = "Reading FASTA reference file...";

		ProgressInputStream is = new ProgressInputStream(file.getInputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		Contig contig = null;
		StringBuilder sb = null;
		String str = null;

		while ((str = in.readLine()) != null && okToRun)
		{
			fastaBytes = is.getBytesRead();

			if (str.startsWith(">"))
			{
				if (sb != null && sb.length() > 0)
					addContig(contig, new Consensus(), sb);

				sb = new StringBuilder();

				String name;
				if (str.indexOf(" ") != -1)
					name = str.substring(1, str.indexOf(" "));
				else
					name = str.substring(1);

				contig = new Contig(name, true, 0);
				contigHash.put(name, contig);
			}

			else
				sb.append(str.trim());
		}

		if (sb != null && sb.length() > 0)
			addContig(contig, new Consensus(), sb);

		is.close();
	}

	private void addContig(Contig contig, Consensus consensus, StringBuilder sb)
	{
		consensus.setData(sb.toString());
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		byte[] bq = new byte[consensus.length()];
		consensus.setBaseQualities(bq);

		assembly.addContig(contig);
	}

	private void downloadBaiFile() throws Exception
	{
		if(bamIndexFile.isURL() == false)
			return;

		File file = new File(cacheDir, "Tablet-"+cacheid+bamIndexFile.getName());

		message = "Reading BAI assembly index file...";
		System.out.println("Downloading BAI index file");

		BufferedInputStream inputStream = new BufferedInputStream(bamIndexFile.getInputStream());
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));

		int i;
		while((i = inputStream.read()) != -1 && okToRun)
		{
			outputStream.write(i);
			baiBytes++;
		}

		inputStream.close();
		outputStream.close();

		bamIndexFile = new AssemblyFile(file.getPath());
	}

	private void openBamFile() throws Exception
	{
		AssemblyFile bam = files[bamFile];

		if(bam.isURL())
			bamReader = new SAMFileReader(bam.getURL(), bamIndexFile.getFile(), false);
		else
			bamReader = new SAMFileReader(bam.getFile(), bamIndexFile.getFile());
	}

	public int getValue()
	{
		return (int) (baiBytes + fastaBytes);
	}

	public boolean isIndeterminate()
		{ return isIndeterminate; }

	public int getMaximum()
		{ return maximum; }

	public String getMessage()
		{ return message; }
}