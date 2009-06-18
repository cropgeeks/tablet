package tablet.gui;

import java.io.*;
import java.lang.management.*;
import javax.swing.*;

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
		// If no file was passed in then we need to prompt the user to pick one
		if (filename == null)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(RB.getString("gui.Commands.fileOpen.openDialog"));
			fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

//			FileNameExtensionFilter filter = new FileNameExtensionFilter(
//				RB.getString("other.Filters.project"), "flapjack");
//			fc.addChoosableFileFilter(filter);

			if (fc.showOpenDialog(winMain) != JFileChooser.APPROVE_OPTION)
				return;

			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();
			filename = fc.getSelectedFile().getPath();
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

		Prefs.setRecentDocument(filename);

		long freeMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		System.out.println("Memory used: " + nf.format(freeMem/1024f/1024f) + "MB\n");

		winMain.setAssembly(ioHandler.getAssembly());
	}
}