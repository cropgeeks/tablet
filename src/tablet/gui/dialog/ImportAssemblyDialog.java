// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import tablet.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class ImportAssemblyDialog extends JDialog
	implements ActionListener
{
	// File filters used by each of the browse options
	private FileNameExtensionFilter[] assFilters;
	private FileNameExtensionFilter[] refFilters;

	private JButton bCancel, bHelp, bOpen;

	private NBImportAssemblyPanel nbPanel;

	private String[] filenames;

	public ImportAssemblyDialog()
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.ImportAssemblyDialog.title"),
			true
		);

		nbPanel = new NBImportAssemblyPanel(this);
		initDisplay();

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bCancel);
		getRootPane().setDefaultButton(bOpen);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOpen = SwingUtils.getButton(RB.getString("gui.text.open"));
		bOpen.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		TabletUtils.setHelp(bHelp, "gui.dialog.ImportAssemblyDialog");

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bOpen);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	private void initDisplay()
	{
		assFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.ace"), "ace"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.afg"), "afg"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.maq"), "maq", "txt"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.sam"), "sam"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.bam"), "bam"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.soap"), "soap"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.txt"), "txt")
		};

		refFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.fasta"), "fasta", "fa"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.fastq"), "fastq"),
			new FileNameExtensionFilter(RB.getString("gui.text.formats.txt"), "txt")
		};
	}

	public String[] getFilenames()
		{ return filenames; }

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCancel)
			setVisible(false);

		// Open button selected - find out which file type and get the files
		else if(e.getSource() == bOpen)
		{
			if (referenceCheckOK() == false)
				return;

			if (nbPanel.isUsingReference())
			{
				filenames = new String[] {
					nbPanel.file1Combo.getText(),
					nbPanel.file2Combo.getText() };
				Prefs.refNotUsed = false;
			}
			else
			{
				filenames = new String[] { nbPanel.file1Combo.getText() };
				Prefs.refNotUsed = true;
			}

			if (filenames != null)
			{
				Prefs.assRecentDocs = nbPanel.file1Combo.getHistory();
				Prefs.refRecentDocs = nbPanel.file2Combo.getHistory();

				setVisible(false);
			}
		}

		else if (e.getSource() == nbPanel.bBrowse1)
		{
			browse(nbPanel.file1Combo, assFilters);
		}
		else if (e.getSource() == nbPanel.bBrowse2)
		{
			browse(nbPanel.file2Combo, refFilters);
		}
	}

	// Update the combobox and list of recent files from the new input
	void browse(HistoryComboBox combo, FileNameExtensionFilter[] filters)
	{
		String filename = null;
		String title = RB.getString("gui.dialog.ImportAssemblyDialog.browse");

		String str = combo.getText();
		// Only use the existing entry if its length > 0 && not a web address
		if (str.length() > 0 && !str.toLowerCase().startsWith("http://"))
		{
			File file = new File(str);
			filename = TabletUtils.getOpenFilename(title, file, filters, -1);
		}
		// Otherwise pass null to the dialog which will use the last used dir
		else
			filename = TabletUtils.getOpenFilename(title, null, filters, -1);

		combo.updateComboBox(filename);
	}

	private boolean referenceCheckOK()
	{
		if (nbPanel.isIgnoringReference() && nbPanel.detectedBAM)
		{
			String msg = RB.getString("gui.dialog.ImportAssemblyDialog.warnBamNoRef");
			TaskDialog.error(msg, RB.getString("gui.text.close"));

			return false;
		}

		if (nbPanel.isIgnoringReference() && Prefs.guiWarnNoRef)
		{
			String msg = RB.getString("gui.dialog.ImportAssemblyDialog.warnNoRef");
			JCheckBox checkbox = new JCheckBox();
			RB.setText(checkbox, "gui.dialog.ImportAssemblyDialog.checkWarning");

			String[] options = new String[] {
				RB.getString("gui.dialog.ImportAssemblyDialog.continue"),
				RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, TaskDialog.QST, 1, checkbox, options);
			Prefs.guiWarnNoRef = !checkbox.isSelected();

			if (response != 0)
				return false;
		}

		return true;
	}
}