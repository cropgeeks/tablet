package tablet.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

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
			filename = getFilename(RB.getString("gui.Commands.fileOpen.openDialog"));
			if (filename == null)
				return;
		}

		File file = new File(filename);

		winMain.closeAssembly();
		System.gc();

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
		winMain.setAssembly(ioHandler.getAssembly());
	}

	private String getFilename(String title)
	{
		// Decide on AWT or Swing dialog based on OS X or not
		if (SystemUtils.isMacOS() == false)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(RB.getString("gui.Commands.fileOpen.openDialog"));
			fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

			if (fc.showOpenDialog(winMain) != JFileChooser.APPROVE_OPTION)
				return null;

			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();
			return fc.getSelectedFile().getPath();
		}
		else
		{
			FileDialog fd = new FileDialog(winMain, title, FileDialog.LOAD);
			fd.setDirectory(Prefs.guiCurrentDir);
			fd.setLocationRelativeTo(winMain);
			fd.setVisible(true);

			if (fd.getFile() == null)
				return null;

			Prefs.guiCurrentDir = fd.getDirectory();
			return new File(fd.getDirectory(), fd.getFile()).getPath();
		}
	}
}