package tablet.io;

import java.io.*;
import java.util.HashMap;

import tablet.analysis.SimpleJob;
import tablet.data.*;
import tablet.data.cache.IReadCache;


public class BAIFileReader extends SimpleJob implements IAssemblyReader
{
	private IReadCache readCache;
	private Assembly assembly;
	private FaiFileReader refReader;
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int bamFile = -1;
	private int refFile = -1;

	private AssemblyFile[] files = new AssemblyFile[2];
	private AssemblyFile bamIndexFile, faiIndexFile;

	private File cacheDir;
	private String cacheid;

	public BAIFileReader(IReadCache readCache, File cacheDir, String cacheid)
	{
		this.readCache = readCache;
		this.cacheDir = cacheDir;
		this.cacheid = cacheid;
	}

	public boolean canRead() throws Exception
	{
		refReader = new FaiFileReader(assembly, contigHash);

		for(int i = 0; i < files.length; i++)
		{
			if (refReader.canRead(files[i]))
			{
				refFile = i;
			}
			else
			{
				// Check to see if we even have a BAM file
				if(files[i].getName().toLowerCase().endsWith(".bam") && files[i].length() > 0)
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

	public void runJob(int index) throws Exception
	{
		if(refFile >= 0)
			readReferenceFile();
	}

	private void readReferenceFile() throws Exception
	{
		refReader.readReferenceFile(files[refFile]);
	}

	public void setInputs(AssemblyFile[] files, Assembly assembly)
	{
		this.files = files;
		this.assembly = assembly;
	}

	public Assembly getAssembly()
	{
		return assembly;
	}

	public int getJobCount()
	{
		return 1;
	}

	public String getMessage()
	{
		return "";
	}

	private AssemblyFile getBaiFile(AssemblyFile file, boolean typeTwo)
	{
		String name = file.getName();
		String newName = name + ".bai";

		if (typeTwo)
			newName = name.substring(0, name.lastIndexOf(".bam")) + ".bai";

		return new AssemblyFile(file.getPath().replaceAll(name, newName));
	}

	private void downloadBaiFile() throws Exception
	{
		if(bamIndexFile.isURL() == false)
			return;
		
		File file = new File(cacheDir, "Tablet-"+cacheid+bamIndexFile.getName());

		//TODO do the 5555 trick to avoid int / long problems
		maximum = (int) bamIndexFile.length();
		progress = 0;

		BufferedInputStream inputStream = new BufferedInputStream(bamIndexFile.getInputStream());
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));

		int i;
		while((i = inputStream.read()) != -1)
		{
			outputStream.write(i);
			progress++;
		}

		inputStream.close();
		outputStream.close();

		bamIndexFile = new AssemblyFile(file.getPath());
	}
}
