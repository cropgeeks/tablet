// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import scri.commons.gui.*;

public class TabletUtils
{
	public static JPanel getButtonPanel()
	{
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		p1.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
			BorderFactory.createEmptyBorder(10, 0, 10, 5)));

		return p1;
	}

	/**
	 * Registers a button to display Tablet help on the specified topic. Will
	 * make both the button's actionListener and a keypress of F1 take Tablet
	 * to the appropriate help page (on the web).
	 */
	public static void setHelp(final JButton button, String topic)
	{
		final String html = "http://bioinf.scri.ac.uk/tablet/help/" + topic + ".shtml";

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				visitURL(html);
			}
		});

		// TODO: is there a better way of doing this that doesn't rely on having
		// an actionListener AND an AbstractAction both doing the same thing
		AbstractAction helpAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				visitURL(html);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "help");
		button.getActionMap().put("help", helpAction);
	}

	/** Produces a FASTA formatted version of a sequence. */
	public static String formatFASTA(String title, String sequence)
	{
		String lb = System.getProperty("line.separator");

		StringBuilder text = new StringBuilder(sequence.length());
		text.append(">" + title + lb);

		for (int i = 0; i < sequence.length(); i += 50)
		{
			try	{
				text.append(sequence.substring(i, i+50) + lb);
			}
			catch (Exception e)	{
				text.append(sequence.substring(i, sequence.length()) + lb);
			}
		}

		return text.toString();
	}

	public static void visitURL(String html)
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();

			URI uri = new URI(html);
			desktop.browse(uri);
		}
		catch (Exception e)
		{
			TaskDialog.error(
				RB.format("gui.TabletUtils.urlError", html),
				RB.getString("gui.text.close"));
		}
	}

	/**
	 * Shows an OPEN file dialog, returning the path to the file selected as a
	 * string.
	 */
	public static String getOpenFilename(String title, File filePath,
		FileNameExtensionFilter[] filters, int selectedFilter)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);

		if (filePath != null)
			fc.setCurrentDirectory(filePath);
		else
			fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

		if (filters != null)
		{
			for (FileNameExtensionFilter filter: filters)
				fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filters[selectedFilter]);
		}

		if (fc.showOpenDialog(Tablet.winMain) != JFileChooser.APPROVE_OPTION)
			return null;

		Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

		return fc.getSelectedFile().getPath();
	}

	/**
	 * Shows a SAVE file dialog, returning the path to the file selected as a
	 * string. Also prompts to ensure the user really does want to overwrite an
	 * existing file if one is chosen.
	 */
	public static String getSaveFilename(
		String title, File file, FileNameExtensionFilter filter)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(filter);

		if (file != null)
			fc.setSelectedFile(file);

		while (fc.showSaveDialog(Tablet.winMain) == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();

			// Make sure it has an appropriate extension
			if (file.exists() == false)
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + "." + filter.getExtensions()[0]);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("gui.TabletUtils.getSaveFilename.confirm", file);
				String[] options = new String[] {
					RB.getString("gui.TabletUtils.getSaveFilename.overwrite"),
					RB.getString("gui.TabletUtils.getSaveFilename.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, TaskDialog.WAR, 1, options);

				// Rename...
				if (response == 1)
					continue;
				// Closed dialog or clicked cancel...
				else if (response == -1 || response == 2)
					return null;
			}

			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

			return file.getPath();
		}

		return null;
	}
}