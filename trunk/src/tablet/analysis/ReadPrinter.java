package tablet.analysis;

import java.io.*;

import tablet.data.*;

public class ReadPrinter extends SimpleJob
{
	public static final int COLUMN_TYPE = 0;

	private File file;
	private IReadManager manager;
	private int readPrinterType;
	private int colIndex;

	public ReadPrinter(File file, IReadManager manager, int readPrinterType, int colIndex)
	{
		this.file = file;
		this.manager = manager;
		this.readPrinterType = readPrinterType;
		this.colIndex = colIndex;
	}

	public ReadPrinter(File file, IReadManager manager, int readPrinterType)
	{
		this.file = file;
		this.manager = manager;
		this.readPrinterType = readPrinterType;
	}

	public void runJob(int jobIndex) throws Exception
	{
		switch(readPrinterType)
		{
			case COLUMN_TYPE: columnPrinter(); break;
		}
	}

	private void columnPrinter()
			throws IOException
	{
		maximum = manager.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for(int i=0; i < manager.size(); i++)
		{
			if (!okToRun)
				return;

			Read read = manager.getReadAt(i, colIndex);
			if(read != null)
			{
				ReadMetaData rmd = Assembly.getReadMetaData(read, false);
				ReadNameData rnd = Assembly.getReadNameData(read);

				out.write(rnd.getName() + "\t" + rmd.toString() + "\t" + read.getStartPosition());
				out.newLine();
			}

			progress++;
		}

		out.close();
	}

}
