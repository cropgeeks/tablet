package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.file.*;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFormatException;

public class BAIFileReader extends SimpleJob implements IAssemblyReader
{
	private IReadCache readCache;
	private File cacheDir;
	private String cacheid;

	private Assembly assembly;
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int bamFile = -1;
	private int refFile = -1;

	private AssemblyFile[] files = new AssemblyFile[2];
	private AssemblyFile bamIndexFile, faiIndexFile;

	private long baiBytes, fastaBytes;
	private String message;

	public BAIFileReader(IReadCache readCache, File cacheDir, String cacheid)
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
				if (files[i].getName().toLowerCase().endsWith(".bam") && files[i].length() > 0)
				{
					bamFile = i;

					// If so, do we have an index file that goes with it?
					AssemblyFile file1 = getBaiFile(files[i], false);
					AssemblyFile file2 = getBaiFile(files[i], true);

					if (file1.length() > 0)
						bamIndexFile = file1;
					else if (file2.length() > 0)
						bamIndexFile = file2;
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

		readReferenceFile(files[refFile]);

		downloadBaiFile();
	}

	private AssemblyFile getBaiFile(AssemblyFile file, boolean typeTwo)
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

	public int getValue()
	{
		return (int) (baiBytes + fastaBytes);
	}

	public Assembly getAssembly()
		{ return assembly; }

	public String getMessage()
		{ return message; }
}