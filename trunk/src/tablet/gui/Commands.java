package tablet.gui;

import java.io.*;
import java.lang.management.*;

import tablet.data.*;
import tablet.io.*;

class Commands
{
	private WinMain winMain;

	Commands(WinMain winMain)
	{
		this.winMain = winMain;
	}

	void fileOpen(String filename)
	{
		// Load in the data
		try
		{
			ImportHandler ioHandler = new ImportHandler();

			ioHandler.readFile(filename);
			Assembly assembly = ioHandler.getAssembly();

			long freeMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

			System.out.println("Memory used: " + nf.format(freeMem/1024f/1024f) + "MB\n");

			winMain.setAssembly(assembly);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}