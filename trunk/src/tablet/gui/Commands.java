package tablet.gui;

import java.awt.*;
import java.io.*;
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
			RB.getString("gui.Commands.fileOpen.msg02") };

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(ioHandler, title, label, msgs);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					"Error opening " + filename + "\n\n" + dialog.getException(),
					RB.getString("gui.text.close"));
			}

			return;
		}

		Prefs.setRecentDocument(filename);
		winMain.setAssembly(ioHandler.getAssembly());

		// See if a feature file can be loaded at this point too
		if (getFeatureFile(file) != null)
			importFeatures(getFeatureFile(file).getPath());
	}

	public void importFeatures(String filename)
	{
		// If no file was passed in then we need to prompt the user to pick one
		if (filename == null)
		{
			filename = getFilename(RB.getString("gui.Commands.importFeatures.openDialog"));
			if (filename == null)
				return;
		}

		File file = new File(filename);

		Assembly assembly = winMain.getAssemblyPanel().getAssembly();
		GFF3Reader reader = new GFF3Reader(file, assembly);

		String title = RB.getString("gui.Commands.importFeatures.title");
		String label = RB.getString("gui.Commands.importFeatures.label");
		String[] msgs = new String[] {
			RB.format("gui.Commands.importFeatures.msg", file.getName()) };

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(reader, title, label, msgs);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					"Error importing features from " + filename + "\n\n" + dialog.getException(),
					RB.getString("gui.text.close"));
			}

			return;
		}

		winMain.getContigsPanel().updateTable(assembly);
	}

	private String getFilename(String title)
	{
		// Decide on AWT or Swing dialog based on OS X or not
		if (SystemUtils.isMacOS() == false)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(title);
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

	// Given assemblyfile.<ext> see if there is a featurefile.gff file that is
	// in the same directory as it, and return it.
	private File getFeatureFile(File assemblyFile)
	{
		String name = assemblyFile.getName();

		// Remove the file extension
		int index = name.lastIndexOf(".");
		if (index != -1)
			name = name.substring(0, index);

		File file = new File(assemblyFile.getParent(), name + ".gff");

		if (file.exists())
			return file;
		else
			return null;
	}
}