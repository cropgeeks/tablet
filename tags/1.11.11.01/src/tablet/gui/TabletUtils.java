// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.math.*;
import java.security.*;
import java.net.*;
import java.text.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import scri.commons.gui.*;

public class TabletUtils
{
	public static NumberFormat nf = NumberFormat.getInstance();

	public static Color red1 = new Color(169, 46, 34);
	public static Color red2 = new Color(255, 170, 170);

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
		final String html = "http://bioinf.hutton.ac.uk/tablet/help/" + topic + ".shtml";

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

	public static void sendFeedback()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.mail(new URI("mailto:tablet@hutton.ac.uk?subject=Tablet%20Feedback"));
		}
		catch (Exception e) { System.out.println(e); }
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

			// Set the filter to a a specific type...
			if (selectedFilter != -1)
				fc.setFileFilter(filters[selectedFilter]);
			// ...or leave the default at "all files"
			else
				fc.setFileFilter(fc.getChoosableFileFilters()[0]);
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

	public static void copyTableToClipboard(JTable table, AbstractTableModel model)
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");

		// Column headers
		for (int c = 0; c < model.getColumnCount(); c++)
		{
			text.append(model.getColumnName(c));
			text.append(c < model.getColumnCount()-1 ? "\t" : newline);
		}

		// Each row
		for (int r = 0; r < table.getRowCount(); r++)
		{
			int row = table.convertRowIndexToModel(r);

			for (int c = 0; c < model.getColumnCount(); c++)
			{
				text.append(model.getValueAt(row, c));
				text.append(c < model.getColumnCount()-1 ? "\t" : newline);
			}
		}

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	public static String getMD5Sum(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5Digest = md.digest(str.getBytes());
			BigInteger md5Number = new BigInteger(1, md5Digest);
			String md5String = md5Number.toString(16);

			return md5String;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * @param panel the panel to apply the effect to
	 * @param opague if the panel should be opague or not on OS X
	 */
	public static void setPanelColor(JPanel panel, boolean opagueOnOSX)
	{
		if (SystemUtils.isMacOS() == false)
			panel.setBackground(Color.white);

		else
			panel.setOpaque(opagueOnOSX);
	}
}