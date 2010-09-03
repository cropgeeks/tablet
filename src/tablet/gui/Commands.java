// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.Desktop;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.filechooser.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.dialog.*;
import tablet.gui.viewer.*;
import tablet.io.*;
import javax.swing.*;

import scri.commons.gui.*;

public class Commands
{
	private WinMain winMain;

	Commands(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void fileOpen(String[] filenames)
	{
		if (winMain.okToExit(true) == false)
			return;

		// If no file was passed in then we need to prompt the user to pick one
		if (filenames == null)
		{
			ImportAssemblyDialog importDialog = new ImportAssemblyDialog();

			if (importDialog.useExamples())
			{
				ExampleDatasetDialog exampleDialog = new ExampleDatasetDialog();
				if((filenames = exampleDialog.getFilenames()) == null)
					return;
			}

			else if ((filenames = importDialog.getFilenames()) == null)
				return;
		}

		winMain.closeAssembly();
		System.gc();

		AssemblyFileHandler assemblyFileHandler = new AssemblyFileHandler(filenames, new File(Prefs.cacheDir));

		String title = RB.getString("gui.Commands.fileOpen.title");
		String label = RB.getString("gui.Commands.fileOpen.label");

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(assemblyFileHandler, title, label);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				String files = "";
				for (int i = 0; i < filenames.length; i++)
					files += "\n     " + filenames[i];

				TaskDialog.error(RB.format("gui.Commands.fileOpen.error",
					dialog.getException(), files),
					RB.getString("gui.text.close"));
			}

			return;
		}

		Prefs.setRecentDocument(filenames);
		winMain.setAssembly(assemblyFileHandler.getAssembly());

		// See if a feature file can be loaded at this point too
		if(filenames.length == 1)
		{
			File file = new File(filenames[0]);
			if (getFeatureFile(file) != null)
				importFeatures(getFeatureFile(file).getPath(), false);
		}

		// Pop up a warning if the ref lengths don't match
		if (!assemblyFileHandler.refLengthsOK() && Prefs.guiWarnRefLengths)
		{
			String msg = RB.getString("gui.commands.fileOpen.refLengthsOK");
			JCheckBox chkbox = new JCheckBox();
			RB.setText(chkbox, "gui.commands.fileOpen.checkWarning");

			String[] options = new String[] { RB.getString("gui.text.close") };

			int response = TaskDialog.show(msg, TaskDialog.WAR, 0, chkbox, options);
			Prefs.guiWarnRefLengths = !chkbox.isSelected();
		}
	}

	public void importFeatures(String filename, boolean showSummary)
	{
		// If no file was passed in then we need to prompt the user to pick one
		if (filename == null)
		{
			FileNameExtensionFilter[] filters = new FileNameExtensionFilter[] {
				new FileNameExtensionFilter(RB.getString("gui.text.formats.gff"), "gff", "gff3"),
				new FileNameExtensionFilter(RB.getString("gui.text.formats.txt"), "txt") };

			filename = TabletUtils.getOpenFilename(RB.getString(
				"gui.Commands.importFeatures.openDialog"), null, filters, 0);

			if (filename == null)
				return;
		}

		Assembly assembly = winMain.getAssemblyPanel().getAssembly();
		GFF3Reader reader = new GFF3Reader(filename, assembly);

		String title = RB.getString("gui.Commands.importFeatures.title");
		String label = RB.getString("gui.Commands.importFeatures.label");

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(reader, title, label);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					RB.format("gui.Commands.importFeatures.exception",
					dialog.getException()),
					RB.getString("gui.text.close"));
			}

			return;
		}

		winMain.getContigsPanel().updateTable(assembly);

		if (showSummary)
			TaskDialog.info(RB.format("gui.Commands.importFeatures.summary",
				reader.getFeaturesRead(), reader.getFeaturesAdded()),
				RB.getString("gui.text.close"));
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

	public void exportImage()
	{
		AssemblyPanel aPanel = winMain.getAssemblyPanel();

		String aName = aPanel.getAssembly().getName();
		String cName = aPanel.getContig().getName();
		File saveAs = new File(Prefs.guiCurrentDir, aName+"-"+cName+".png");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.Commands.exportImage.pngFiles"), "png");

		// Ask the user for a filename to save the current view as
		String filename = TabletUtils.getSaveFilename(
			RB.getString("gui.Commands.exportImage.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		try
		{
			BufferedImage image = winMain.getAssemblyPanel().getBackBuffer();
			ImageIO.write(image, "png", new File(filename));

			TaskDialog.info(
				RB.format("gui.Commands.exportImage.success", filename),
				RB.getString("gui.text.close"));
		}
		catch (Exception e)
		{
			e.printStackTrace();

			TaskDialog.error(
				RB.format("gui.Commands.exportImage.exception", e),
				RB.getString("gui.text.close"));
		}
	}

	public void exportCoverage()
	{
		Assembly assembly = winMain.getAssemblyPanel().getAssembly();

		if(assembly.getBamBam() == null)
		{
			String aName = assembly.getName();
			File saveAs = new File(Prefs.guiCurrentDir, aName+".txt");

			FileNameExtensionFilter filter = new FileNameExtensionFilter(
				RB.getString("gui.text.formats.txt"), "txt");

			// Ask the user for a filename to save the current view as
			String filename = TabletUtils.getSaveFilename(
				RB.getString("gui.Commands.exportCoverage.saveDialog"), saveAs, filter);

			// Quit if the user cancelled the file selection
			if (filename == null)
				return;

			CoveragePrinter printer = new CoveragePrinter(new File(filename), assembly);

			ProgressDialog dialog = new ProgressDialog(printer,
				RB.getString("gui.Commands.exportCoverage.title"),
				RB.getString("gui.Commands.exportCoverage.label"));

			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED &&
				dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.error(
					RB.format("gui.Commands.exportCoverage.exception",
					dialog.getException()),
					RB.getString("gui.text.close"));
			}
			else
				TaskDialog.info(
					RB.format("gui.Commands.exportCoverage.success", filename),
					RB.getString("gui.text.close"));
		}
		else
		{
			TaskDialog.warning("Sorry but this feature is not currently "
				+ "supported for BAM assemblies. We hope to resolve this in "
				+ "the future.", RB.getString("gui.text.close"));
		}
	}

	public void exportColumnData(IReadManager manager, int colIndex) throws IOException
	{
		AssemblyPanel aPanel = winMain.getAssemblyPanel();
		
		String aName = aPanel.getAssembly().getName();
		File saveAs = new File(Prefs.guiCurrentDir, aName+".txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = TabletUtils.getSaveFilename(
			RB.getString("gui.Commands.exportCoverage.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		ReadPrinter printer = new ReadPrinter(new File(filename), manager, ReadPrinter.COLUMN_TYPE, colIndex);

		ProgressDialog dialog = new ProgressDialog(printer,
				RB.getString("gui.Commands.exportReadColumn.title"),
				RB.getString("gui.Commands.exportReadColumn.label"));

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED &&
				dialog.getResult() == ProgressDialog.JOB_FAILED)
		{
			dialog.getException().printStackTrace();
			TaskDialog.error(
				RB.format("gui.Commands.exportReadColumn.exception",
				dialog.getException()),
				RB.getString("gui.text.close"));
		}
		else
		{
			int result = TaskDialog.show(RB.format("gui.Commands.exportReadColumn.success", filename),
					TaskDialog.INF, 0, new String[] {"Open file", RB.getString("gui.text.close")});
			if(result == 0)
			{
				// Before more Desktop API is used, first check
				// whether the API is supported by this particular
				// virtual machine (VM) on this particular host.
				if (Desktop.isDesktopSupported())
				{
					Desktop desktop = Desktop.getDesktop();
					desktop.open(new File(filename));
				}
			}
		}
	}
}