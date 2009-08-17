package tablet.gui;

import java.awt.*;
import java.io.File;
import java.net.*;
import javax.swing.*;

import scri.commons.gui.*;

public class TabletUtils
{
	public static boolean dirChanged = false;

	public static JPanel getButtonPanel()
	{
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		p1.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
			BorderFactory.createEmptyBorder(10, 0, 10, 5)));

		return p1;
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

	public static String getFilename(String title, String filePath)
	{
		WinMain winMain = Tablet.winMain;
		// Decide on AWT or Swing dialog based on OS X or not
		if (SystemUtils.isMacOS() == false)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(title);
			if(dirChanged)
			    fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));
			else
			    fc.setCurrentDirectory(new File(filePath));

			if (fc.showOpenDialog(winMain) != JFileChooser.APPROVE_OPTION)
				return null;

			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();
			return fc.getSelectedFile().getPath();
		}
		else
		{
			FileDialog fd = new FileDialog(winMain, title, FileDialog.LOAD);
			if(dirChanged)
			    fd.setDirectory(Prefs.guiCurrentDir);
			else
			    fd.setDirectory(filePath);
			fd.setLocationRelativeTo(winMain);
			fd.setVisible(true);

			if (fd.getFile() == null)
				return null;

			Prefs.guiCurrentDir = fd.getDirectory();
			return new File(fd.getDirectory(), fd.getFile()).getPath();
		}
	}
}