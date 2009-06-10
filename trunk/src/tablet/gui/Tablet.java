package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import scri.commons.gui.*;

import apple.dts.samplecode.osxadapter.*;

public class Tablet
{
	private static File prefsFile = new File(
		System.getProperty("user.home"), ".tablet.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tablet");

		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.tablet");

		Install4j.doStartUpCheck();

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

			if (SystemUtils.isWindows())
			{
				// Overrides the JOptionPane dialogs with better icons
				UIManager.put("OptionPane.errorIcon", Icons.getIcon("WINERROR"));
				UIManager.put("OptionPane.informationIcon", Icons.getIcon("WININFORMATION"));
				UIManager.put("OptionPane.warningIcon", Icons.getIcon("WINWARNING"));
				UIManager.put("OptionPane.questionIcon", Icons.getIcon("WINQUESTION"));
			}

			// Keep Apple happy...
			if (SystemUtils.isMacOS())
				handleOSXStupidities();
			// And use Nimbus for all non-Apple systems
			else
				Nimbus.customizeNimbus();
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

	// --------------------------------------------------
	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		try
		{
			// Register handlers to deal with the System menu about/quit options
//			OSXAdapter.setPreferencesHandler(this,
//				getClass().getDeclaredMethod("osxPreferences", (Class[])null));
//			OSXAdapter.setAboutHandler(this,
//				getClass().getDeclaredMethod("osxAbout", (Class[])null));
			OSXAdapter.setQuitHandler(this,
				getClass().getDeclaredMethod("osxShutdown", (Class[])null));

			// Dock the menu bar at the top of the screen
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (Exception e) {}
	}

	/** "Preferences" on the OS X system menu. */
	public void osxPreferences()
	{
//		winMain.mHelp.helpPrefs();
	}

	/** "About Flapjack" on the OS X system menu. */
	public void osxAbout()
	{
//		winMain.mHelp.helpAbout();
	}

	/** "Quit Flapjack" on the OS X system menu. */
	public boolean osxShutdown()
	{
		shutdown();
		return true;
	}
}