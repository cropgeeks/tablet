// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import tablet.gui.dialog.*;
import tablet.gui.dialog.prefs.*;
import tablet.gui.ribbon.*;

import scri.commons.gui.*;
import scri.commons.file.*;

import apple.dts.samplecode.osxadapter.*;

public class Tablet implements Thread.UncaughtExceptionHandler
{
	private static File prefsFile = new File(
		System.getProperty("user.home"), ".tablet.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	// Optional path to any files to be loaded when Tablet opens
	private static String[] initialFiles = null;

	// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
	public static int menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static String winKey;

	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tablet");
		// This disables the API preferences, which can cause problems on Linux
		// - see: http://www.allaboutbalance.com/articles/disableprefs
//		System.setProperty("java.util.prefs.PreferencesFactory", "scri.commons.gui.DisabledPreferencesFactory");

		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);
		prefs.setVariables();

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.tablet");

		if (args.length > 0)
		{
			initialFiles = new String[args.length];
			for (int i = 0; i < args.length; i++)
				initialFiles[i] = args[i];
		}

		Install4j.doStartUpCheck();

		new Tablet();
	}

	Tablet()
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
			{
				handleOSXStupidities();
				winKey = RB.getString("gui.text.cmnd");
			}

			// And use Nimbus for all non-Apple systems
			else
			{
				Nimbus.customizeNimbus();
				winKey = RB.getString("gui.text.ctrl");
			}
		}
		catch (Exception e) {}

		Thread.setDefaultUncaughtExceptionHandler(this);

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				winMain.validateCacheFolder();

				// Do we want to open an initial project?
				if (initialFiles != null)
					winMain.getCommands().fileOpen(initialFiles);
			}

			public void windowClosing(WindowEvent e)
			{
				if (winMain.okToExit(false))
					shutdown();
			}
		});


		TaskDialog.initialize(winMain, RB.getString("gui.WinMain.title"));

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		// Attempt to remove any temp files that were in use
		winMain.closeAssembly();

		for (File file: new File(Prefs.cacheDir).listFiles())
		{
			if (file.isFile() && file.getName().startsWith("Tablet-"))
				file.delete();
		}

//		FileUtils.emptyDirectory(new File(Prefs.cacheDir), true);

		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}

	public void uncaughtException(Thread thread, Throwable throwable)
	{
		throwable.printStackTrace();

		if (throwable instanceof java.lang.OutOfMemoryError == false)
			return;

		// We open the dialog using SwingUtilities because the uncaughtException
		// may have happened in a non EDT thread
		Runnable r = new Runnable() {
			public void run()
			{
				if (winMain != null)
					winMain.dispose();

				String msg = RB.getString("gui.Tablet.outOfMemory");
				String[] options = new String[] {
					RB.getString("gui.text.help"),
					RB.getString("gui.text.close")
				};

				int response = TaskDialog.show(msg, TaskDialog.ERR, 0, options);
				if (response == 0)
					TabletUtils.visitURL("http://bioinf.scri.ac.uk/tablet/help/memory.shtml");

				System.exit(1);
			}
		};

		SwingUtilities.invokeLater(r);
	}

	// --------------------------------------------------
	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		try
		{
			// Register handlers to deal with the System menu about/quit options
			OSXAdapter.setPreferencesHandler(this,
				getClass().getDeclaredMethod("osxPreferences", (Class[])null));
			OSXAdapter.setAboutHandler(this,
				getClass().getDeclaredMethod("osxAbout", (Class[])null));
			OSXAdapter.setQuitHandler(this,
				getClass().getDeclaredMethod("osxShutdown", (Class[])null));
			OSXAdapter.setFileHandler(this,
				getClass().getDeclaredMethod("osxOpen", new Class[] { String.class }));

			// Dock the menu bar at the top of the screen
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (Exception e) {}
	}

	/** "Preferences" on the OS X system menu. */
	public void osxPreferences()
	{
		ApplicationMenu.displayPreferences(null);
	}

	/** "About Tablet" on the OS X system menu. */
	public void osxAbout()
	{
		new AboutDialog();
	}

	/** "Quit Tablet" on the OS X system menu. */
	public boolean osxShutdown()
	{
		if (winMain.okToExit(false) == false)
			return false;

		shutdown();
		return true;
	}

	public void osxOpen(String path)
	{
		// If Tablet is already open, then open the file straight away
		if (winMain != null && winMain.isVisible())
		{
			// TODO: If we have project modified checks, do them here too
			winMain.getCommands().fileOpen(new String[] { path });
		}

		// Otherwise, mark it for opening once Tablet is ready
		else
		{
			initialFiles = new String[1];
			initialFiles[0] = path;
		}
	}
}