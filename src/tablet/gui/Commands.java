// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.dialog.*;
import tablet.gui.viewer.*;
import tablet.io.*;

import scri.commons.gui.*;

public class Commands
{
	private WinMain winMain;

	private AssemblyFile[] files;
	private TabletFile tabletFile;
	private boolean hasAssembly;

	Commands(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void fileOpen(TabletFile tabletFile)
	{
		if (tabletFile != null)
		{
			if (tabletFile.hasDeterminedTypes == false)
				tabletFile.determineFileTypes();

			if (tabletFile.hasAssembly() && winMain.okToExit(true) == false)
				return;
		}

		// If no file was passed in then we need to prompt the user to pick one
		if (tabletFile == null)
		{
			ImportAssemblyDialog importDialog = new ImportAssemblyDialog();

			if (importDialog.useExamples())
			{
				ExampleDatasetDialog exampleDialog = new ExampleDatasetDialog();
				if ((tabletFile = exampleDialog.getTabletFile()) == null)
					return;
			}

			else if ((tabletFile = importDialog.getTabletFile()) == null)
				return;
		}


		// Attempt to open the assembly
		if (tabletFile.hasAssembly())
			if (openAssembly(tabletFile) == false)
				return;

		// Attempt to open any FEATURE files (if an assembly is loaded)
		if (winMain.getAssemblyPanel().getAssembly() != null)
			for (AssemblyFile annotation: tabletFile.annotations)
				importFeatures(annotation.getPath(), !tabletFile.hasAssembly());


		// Do any further TabletFile post-load operations (eg, jump to contig)
		if (tabletFile.contig != null)
			winMain.getContigsPanel().moveToContigPosition(tabletFile.contig, tabletFile.position);
	}

	private boolean openAssembly(TabletFile tabletFile)
	{
		winMain.closeAssembly();
		System.gc();

		AssemblyFile[] files = tabletFile.getFileList();

		AssemblyFileHandler assemblyFileHandler = new AssemblyFileHandler(
			files, new File(Prefs.cacheFolder));


		String title = RB.getString("gui.Commands.fileOpen.title");
		String label = RB.getString("gui.Commands.fileOpen.label");

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(assemblyFileHandler, title, label, Tablet.winMain);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				String filenames = "";
				for (int i = 0; i < files.length; i++)
					filenames += "\n     " + files[i].getPath();

				TaskDialog.showOpenLog(RB.format("gui.Commands.fileOpen.error",
					dialog.getException(), filenames), Tablet.getLogFile());

			}

			return false;
		}

		TabletFileHandler.addAsMostRecent(tabletFile);
		Assembly assembly = assemblyFileHandler.getAssembly();
		winMain.setAssembly(assembly);

		assembly.getAssemblyStatistics().setAssembly(tabletFile.assembly);
		assembly.getAssemblyStatistics().setReference(tabletFile.reference);


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

		return true;
	}

	public void importFeatures(String filename, boolean showSummary)
	{
		// If no file was passed in then we need to prompt the user to pick one
		if (filename == null)
		{
			FileNameExtensionFilter[] filters = new FileNameExtensionFilter[] {
				new FileNameExtensionFilter(RB.getString("gui.text.formats.gff"), "gff", "gff3"),
				new FileNameExtensionFilter(RB.getString("gui.text.formats.bed"), "bed", "bed"),
				new FileNameExtensionFilter(RB.getString("gui.text.formats.txt"), "txt") };

			filename = TabletUtils.getOpenFilename(RB.getString(
				"gui.Commands.importFeatures.openDialog"), null, filters, -1);

			if (filename == null)
				return;
		}

		Assembly assembly = winMain.getAssemblyPanel().getAssembly();

		// Create a wrapper around the input file
		AssemblyFile features = new AssemblyFile(filename);
		features.canDetermineType();

		// And then decide what reader to use based on its type
		FeatureReader reader = null;
		switch (features.getType())
		{
			case AssemblyFile.BED:
				reader = new BedReader(filename, assembly); break;

			case AssemblyFile.VCF:
				reader = new VcfReader(filename, assembly); break;

			case AssemblyFile.GTF:
				reader = new GtfReader(filename, assembly); break;

			default: // GFF3
				reader = new GFF3Reader(filename, assembly); break;
		}


		String title = RB.getString("gui.Commands.importFeatures.title");
		String label = RB.getString("gui.Commands.importFeatures.label");

		// Run the job...
		ProgressDialog dialog = new ProgressDialog(reader, title, label, Tablet.winMain);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.showOpenLog(RB.format("gui.Commands.importFeatures.exception",
					dialog.getException()), Tablet.getLogFile());
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

			TaskDialog.showOpenLog(RB.format("gui.Commands.exportImage.exception",
				e), Tablet.getLogFile());
		}
	}

	public void exportCoverage()
	{
		Assembly assembly = winMain.getAssemblyPanel().getAssembly();

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
			RB.getString("gui.Commands.exportCoverage.label"),
			Tablet.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.showOpenLog(RB.format("gui.Commands.exportCoverage.exception",
					dialog.getException()), Tablet.getLogFile());
			}
		}
		else
			TaskDialog.info(
				RB.format("gui.Commands.exportCoverage.success", filename),
				RB.getString("gui.text.close"));

		winMain.getAssemblyPanel().validateConsensusCache();
	}

	public void exportColumnData(IReadManager manager, int colIndex) throws IOException
	{
		String filename = getFilename();

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		ReadPrinter printer = new ReadPrinter(new File(filename), manager, colIndex);

		ProgressDialog dialog = new ProgressDialog(printer,
				RB.getString("gui.Commands.exportReadColumn.title"),
				RB.getString("gui.Commands.exportReadColumn.label"),
				Tablet.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.showOpenLog(
					RB.format("gui.Commands.exportReadColumn.exception",
					dialog.getException()),Tablet.getLogFile());
			}

			return;
		}

		TaskDialog.showFileOpen(
			RB.format("gui.Commands.exportReadColumn.success", filename),
			TaskDialog.INF, new File(filename));
	}

	public void exportScreenData(IReadManager manager, int xS, int xE, int yS, int yE) throws IOException
	{
		// Ask the user for a filename to save the current view as
		String filename = getFilename();

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		ReadPrinter printer = new ReadPrinter(new File(filename), manager, xS, xE, yS, yE);

		ProgressDialog dialog = new ProgressDialog(printer,
				RB.getString("gui.Commands.exportVisibleReads.title"),
				RB.getString("gui.Commands.exportVisibleReads.label"),
				Tablet.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.showOpenLog(
					RB.format("gui.Commands.exportVisibleReads.exception",
					dialog.getException()),	Tablet.getLogFile());
			}

			return;
		}

		TaskDialog.showFileOpen(
			RB.format("gui.Commands.exportVisibleReads.success", filename),
			TaskDialog.INF, new File(filename));
	}

	public void exportContigData(IReadManager manager) throws IOException
	{
		// Ask the user for a filename to save the current view as
		String filename = getFilename();

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		ReadPrinter printer = new ReadPrinter(new File(filename), manager);

		ProgressDialog dialog = new ProgressDialog(printer,
				RB.getString("gui.Commands.exportContigReads.title"),
				RB.getString("gui.Commands.exportContigReads.label"),
				Tablet.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();
				TaskDialog.showOpenLog(
					RB.format("gui.Commands.exportContigReads.exception",
					dialog.getException()), Tablet.getLogFile());
			}

			return;
		}

		TaskDialog.showFileOpen(
			RB.format("gui.Commands.exportContigReads.success", filename),
			TaskDialog.INF, new File(filename));
	}

	public void exportSNPs()
	{
		Assembly assembly = winMain.getAssemblyPanel().getAssembly();

		if(assembly.getBamBam() != null)
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

			SNPFinder finder = new SNPFinder(new File(filename), assembly);

			ProgressDialog dialog = new ProgressDialog(finder,
				"Exporting SNPs",
				"Exporting SNPs - please be patient...",
				Tablet.winMain);

			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				{
					dialog.getException().printStackTrace();

					TaskDialog.showOpenLog(RB.format("gui.Commands.exportSNPs.exception",
						dialog.getException()),	Tablet.getLogFile());
				}
			}
			else
				TaskDialog.info(
					RB.format("gui.Commands.exportSNPs.success", filename),
					RB.getString("gui.text.close"));

			winMain.getAssemblyPanel().validateConsensusCache();
		}
		else
		{
			TaskDialog.warning("Sorry but this feature is not currently "
				+ "supported for non-BAM assemblies.", RB.getString("gui.text.close"));
		}
	}


	/**
	 * Display a save dialog to get the filename a file should be saved under.
	 */
	private String getFilename()
	{
		AssemblyPanel aPanel = winMain.getAssemblyPanel();
		String aName = aPanel.getAssembly().getName();
		File saveAs = new File(Prefs.guiCurrentDir, aName + ".txt");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(RB.getString("gui.text.formats.txt"), "txt");
		// Ask the user for a filename to save the current view as
		String filename = TabletUtils.getSaveFilename(RB.getString("gui.Commands.exportCoverage.saveDialog"), saveAs, filter);
		return filename;
	}

}