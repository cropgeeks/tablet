package tablet.gui;

import java.io.*;
import java.lang.management.*;

import tablet.data.*;
import tablet.gui.dialog.*;
import tablet.io.*;

import scri.commons.gui.*;

public class Commands
{
	private WinMain winMain;

	Commands(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void fileOpen(String filename)
	{
		if (filename == null)
		{
			// TODO: Prompt for file...
			System.out.println("Opening file...");
			return;
		}

		File file = new File(filename);

		ImportHandler ioHandler = new ImportHandler(filename);

		String title = RB.getString("gui.Commands.fileOpen.title");
		String label = RB.getString("gui.Commands.fileOpen.label");
		String[] msgs = new String[] {
			RB.format("gui.Commands.fileOpen.msg01", file.getName()),
			RB.getString("gui.Commands.fileOpen.msg02"),
			RB.getString("gui.Commands.fileOpen.msg03") };

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(ioHandler, title, label, msgs);
		if (dialog.jobOK() == false)
		{
			if (dialog.getException() != null)
			{
				TaskDialog.error(
					"Error opening " + filename + "\n\n" + dialog.getException(),
					RB.getString("gui.text.close"));
			}

			return;
		}

		long freeMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		System.out.println("Memory used: " + nf.format(freeMem/1024f/1024f) + "MB\n");

		winMain.setAssembly(ioHandler.getAssembly());
	}
}