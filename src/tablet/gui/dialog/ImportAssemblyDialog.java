// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.filechooser.*;

import tablet.gui.*;

import scri.commons.gui.*;

public class ImportAssemblyDialog extends JDialog
	implements ActionListener, ItemListener
{
	private CardLayout cardLayout = new CardLayout();
	private JPanel cardsPanel;

	private NBImportAssemblyACEPanel acePanel;
	private NBImportAssemblyAFGPanel afgPanel;
	private NBImportAssemblySOAPPanel soapPanel;
	private NBImportAssemblyMaqPanel maqPanel;

	private static final String ACEPANEL  = "ACE";
	private static final String AFGPANEL  = "AFG";
	private static final String SOAPPANEL = "SOAP";
	private static final String MAQPANEL = "Maq";

	// File filters used by each of the browse options
	private FileNameExtensionFilter[] aceFilters;
	private FileNameExtensionFilter[] afgFilters;
	private FileNameExtensionFilter[] soapFilters;
	private FileNameExtensionFilter[] fastaFilters;
	private FileNameExtensionFilter[] maqFilters;
	private FileNameExtensionFilter[] fastqFilters;

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
		acePanel = new NBImportAssemblyACEPanel(this);
		afgPanel = new NBImportAssemblyAFGPanel(this);
		soapPanel = new NBImportAssemblySOAPPanel(this);
		maqPanel = new NBImportAssemblyMaqPanel(this);

		// Create the CardLayout for flicking between input types
		cardsPanel = new JPanel(cardLayout);
		cardsPanel.add(acePanel, ACEPANEL);
		cardsPanel.add(afgPanel, AFGPANEL);
		cardsPanel.add(soapPanel, SOAPPANEL);
		cardsPanel.add(maqPanel, MAQPANEL);
		initDisplay();

		add(nbPanel, BorderLayout.NORTH);
		add(cardsPanel);
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
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		bOpen = SwingUtils.getButton(RB.getString("gui.text.open"));
		bOpen.addActionListener(this);

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bOpen);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	private void initDisplay()
	{
		switch (Prefs.guiLastFileType)
		{
			case 0: cardLayout.show(cardsPanel, ACEPANEL); break;
			case 1: cardLayout.show(cardsPanel, AFGPANEL); break;
			case 2: cardLayout.show(cardsPanel, MAQPANEL); break;
			case 3: cardLayout.show(cardsPanel, SOAPPANEL); break;
		}

		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		aceFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.ace"), "ace"),
			txtFilter };

		afgFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.afg"), "afg"),
			txtFilter };

		soapFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.soap"), "soap"),
			txtFilter };

		fastaFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.fasta"), "fasta"),
			txtFilter };

		maqFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.maq"), "maq", "txt"),
			txtFilter };

		fastqFilters = new FileNameExtensionFilter[] {
			new FileNameExtensionFilter(RB.getString("gui.text.formats.fastq"), "fastq"),
			txtFilter };
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
			if (Prefs.guiLastFileType == 0 && acePanel.isOK())
				filenames = acePanel.getFilenames();

			else if (Prefs.guiLastFileType == 1 && afgPanel.isOK())
				filenames = afgPanel.getFilenames();

			else if (Prefs.guiLastFileType == 2 && maqPanel.isOK())
				filenames = maqPanel.getFilenames();

			else if (Prefs.guiLastFileType == 3 && soapPanel.isOK())
				filenames = soapPanel.getFilenames();

			if (filenames != null)
				setVisible(false);
		}

		else if (e.getSource() == acePanel.aceComboBox)
		{
			String value = (String) acePanel.aceComboBox.getSelectedItem();
			updateComboBox(value, acePanel.aceComboBox, acePanel.recentFiles);
		}

		else if (e.getSource() == afgPanel.afgComboBox)
		{
			String value = (String) afgPanel.afgComboBox.getSelectedItem();
			updateComboBox(value, afgPanel.afgComboBox, afgPanel.recentFiles);
		}

		else if (e.getSource() == soapPanel.soapComboBox)
		{
			String value = (String) soapPanel.soapComboBox.getSelectedItem();
			updateComboBox(value, soapPanel.soapComboBox, soapPanel.recentFilesSoap);
		}

		else if (e.getSource() == soapPanel.fastaComboBox)
		{
			String value = (String) soapPanel.fastaComboBox.getSelectedItem();
			updateComboBox(value, soapPanel.fastaComboBox, soapPanel.recentFilesFasta);
		}

		else if (e.getSource() == maqPanel.maqComboBox)
		{
			String value = (String) maqPanel.maqComboBox.getSelectedItem();
			updateComboBox(value, maqPanel.maqComboBox, maqPanel.recentFilesMaq);
		}

		else if (e.getSource() == maqPanel.fastqComboBox)
		{
			String value = (String) maqPanel.fastqComboBox.getSelectedItem();
			updateComboBox(value, maqPanel.fastqComboBox, maqPanel.recentFilesFastq);
		}

		else if (e.getSource() == acePanel.bBrowse)
			browse(acePanel.aceComboBox, acePanel.recentFiles, aceFilters);

		else if (e.getSource() == afgPanel.bBrowse)
			browse(afgPanel.afgComboBox, afgPanel.recentFiles, afgFilters);

		else if (e.getSource() == soapPanel.bBrowse1)
			browse(soapPanel.soapComboBox, soapPanel.recentFilesSoap, soapFilters);

		else if (e.getSource() == soapPanel.bBrowse2)
			browse(soapPanel.fastaComboBox, soapPanel.recentFilesFasta, fastaFilters);

		else if (e.getSource() == maqPanel.bBrowse1)
			browse(maqPanel.maqComboBox, maqPanel.recentFilesMaq, maqFilters);

		else if (e.getSource() == maqPanel.bBrowse2)
			browse(maqPanel.fastqComboBox, maqPanel.recentFilesFastq, fastqFilters);
	}

	// Toggle to another layout
	public void itemStateChanged(ItemEvent e)
	{
		String layout = null;

		switch (nbPanel.assemblyComboBox.getSelectedIndex())
		{
			case 0: layout = ACEPANEL;  Prefs.guiLastFileType = 0; break;
			case 1: layout = AFGPANEL;  Prefs.guiLastFileType = 1; break;
			case 2: layout = MAQPANEL; Prefs.guiLastFileType = 2; break;
			case 3: layout = SOAPPANEL; Prefs.guiLastFileType = 3; break;
			
		}

		cardLayout.show(cardsPanel, layout);
	}

	//update the combobox and list of recent files from the new input
	void browse(JComboBox combo, LinkedList<String> recentFiles, FileNameExtensionFilter[] filters)
	{
		String filename = null;
		String title = RB.getString("gui.dialog.ImportAssemblyDialog.browse");

		if (combo.getSelectedItem() != null)
		{
			File file = new File(combo.getSelectedItem().toString());
			filename = TabletUtils.getOpenFilename(title, file, filters, 0);
		}
		else
			filename = TabletUtils.getOpenFilename(title, null, filters, 0);

		updateComboBox(filename, combo, recentFiles);
	}

	void updateComboBox(String value, JComboBox combo, LinkedList<String> recentFiles)
	{
		if (value != null)
		{
			recentFiles.remove(value);
			recentFiles.addFirst(value);
		}

		// Add the new entry to the list, and keep its overall size 10 or less
		while (recentFiles.size() > 10)
			recentFiles.removeLast();

		// The combo's box's ALs need to be removed before changing it
		ActionListener[] listeners = combo.getActionListeners();
		for (ActionListener al: listeners)
			combo.removeActionListener(al);

		// Update the combo box to contain the same items
		combo.removeAllItems();
		for (String entry: recentFiles)
			combo.addItem(entry);

		for (ActionListener al: listeners)
			combo.addActionListener(al);
	}
}