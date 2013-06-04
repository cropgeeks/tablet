// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.gui.*;
import tablet.io.*;

public class ExampleDatasetDialog extends JDialog implements ActionListener
{
	private ExampleDatasetPanelNB panel;
	private Properties properties;
	private JButton bClose, bLoad;
	private TabletFile tabletFile;

	public ExampleDatasetDialog()
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.ExampleDatasetDialog.title"),
			true
		);

		panel = new ExampleDatasetPanelNB(this);
		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		// Attempt to load properties file containing the example dataset information
		// from the server.
		try
		{
			properties = new Properties();
			// The folder on the server
			URL url = new URL("http://bioinf.hutton.ac.uk/tablet/sample-data/exampledata.xml");
			properties.loadFromXML(url.openStream());
		}
		catch(Exception e)
		{
			TaskDialog.error(RB.getString("gui.dialog.ExampleDatasetDialog.error"), RB.getString("gui.text.close"));
			setVisible(false);
			return;
		}
		setupDialogComponents();

		SwingUtils.addCloseHandler(this, bClose);
		getRootPane().setDefaultButton(bLoad);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
		setVisible(true);
	}

	/**
	 * Create the button bar which can be seen at the bottom of the dialog.
	 */
	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bClose.addActionListener(this);
		bLoad = SwingUtils.getButton(RB.getString("gui.text.open"));
		bLoad.addActionListener(this);

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bLoad);
		p1.add(bClose);

		return p1;
	}

	/**
	 * Setup the ComboBox with the Titles for each example dataset.
	 */
	private void setupDialogComponents()
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		Enumeration<Object> e = properties.keys();
		while(e.hasMoreElements())
		{
			String key = (String) e.nextElement();
			model.addElement(key);
		}

		panel.datasetCombo.setModel(model);

		// Set up the description textarea with the text fo the description of the
		// combo box element which is selected by default
		doListSelection();
	}

	/**
	 * Fill the description textarea with the description text from the newly selected
	 * combobox element.
	 */
	private void doListSelection()
	{
		panel.lblType.setText(getValueElement("type"));
		panel.lblDetails.setText(getValueElement("details"));
		panel.descriptionTextArea.setText(getValueElement("description"));
		panel.descriptionTextArea.setCaretPosition(0);
	}

	/**
	 * Picks out any element from the value field of the properties object. The
	 * paramater desiredElement is used to check against after the string has been
	 * split on the semi-colon delimiter (element delimiter). desiredElement should
	 * be the key of a key-value pair which is the element.
	 */
	private String getValueElement(String desiredElement)
	{
		String value = properties.getProperty((String) panel.datasetCombo.getSelectedItem());
		String [] elements = value.split(";");
		for(String element : elements)
		{
			if(element.startsWith(desiredElement))
			{
				String [] subelements = element.split("=");
				return subelements[1];
			}
		}
		return null;
	}

	/**
	 * Load the file using the usual Tablet loading code.
	 */
	private void setupFilenames()
	{
		String assemblyFile = getValueElement("ass_file");
		String referenceFile = getValueElement("ref_file");
		if (referenceFile == null)
		{
			String[] filenames = new String[1];
			filenames[0] = assemblyFile;
			tabletFile = TabletFileHandler.createFromFileList(filenames);
		}
		else
		{
			String[] filenames = new String[2];
			filenames[0] = assemblyFile;
			filenames[1] = referenceFile;
			tabletFile = TabletFileHandler.createFromFileList(filenames);
		}
	}

	public TabletFile getTabletFile()
		{ return tabletFile; }

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == panel.datasetCombo)
			doListSelection();

		if(e.getSource() == bLoad)
		{
			setVisible(false);
			setupFilenames();
		}

		if(e.getSource() == bClose)
			setVisible(false);
	}
}