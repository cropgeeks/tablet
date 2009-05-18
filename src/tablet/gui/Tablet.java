package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import scri.commons.gui.*;

public class Tablet
{
	private static File prefsFile = new File(
		System.getProperty("user.home"), ".tablet.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.tablet");

		if (args.length == 1)
			new Tablet(args[0]);
		else
			new Tablet(null);
	}

	Tablet(final String filename)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// Use the office look for Windows (but not for Vista)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista())
			{
				UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");

				// Gives XP the same (nicer) grey background that Vista uses
				UIManager.put("Panel.background", new Color(240, 240, 240));

				// Overrides the JOptionPane dialogs with better icons
				UIManager.put("OptionPane.errorIcon", Icons.getIcon("WINERROR"));
				UIManager.put("OptionPane.informationIcon", Icons.getIcon("WININFORMATION"));
				UIManager.put("OptionPane.warningIcon", Icons.getIcon("WINWARNING"));
				UIManager.put("OptionPane.questionIcon", Icons.getIcon("WINQUESTION"));
			}
		}
		catch (Exception e) {}

		winMain = new WinMain(filename);

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				// Do we want to open an initial project?
				if (filename != null)
					winMain.getCommands().fileOpen(filename);
			}

			public void windowClosing(WindowEvent e)
			{
				if (winMain.okToExit() == false)
					return;

				shutdown();
			}
		});


		TaskDialog.initialize(winMain, RB.getString("gui.WinMain.title"));

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}
}