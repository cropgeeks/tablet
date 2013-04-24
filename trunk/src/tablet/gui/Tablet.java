// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import tablet.data.cache.*;
import tablet.gui.dialog.*;
import tablet.gui.ribbon.*;
import tablet.gui.viewer.colors.*;
import tablet.io.*;

import scri.commons.gui.*;
import scri.commons.file.*;

import apple.dts.samplecode.osxadapter.*;

public class Tablet implements Thread.UncaughtExceptionHandler
{
	private static File prefsFile = getPrefsFile();
	private static File mruFile;
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	// Optional path to any files to be loaded when Tablet opens
	private static String[] initialFiles = null;

	// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
	public static int menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static String winKey;

	private static String contig = null;
	private static Integer position = null;

	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tablet");
		// This disables the API preferences, which can cause problems on Linux
		// - see: http://www.allaboutbalance.com/articles/disableprefs
//		System.setProperty("java.util.prefs.PreferencesFactory", "scri.commons.gui.DisabledPreferencesFactory");

		System.out.println("Tablet " + Install4j.getVersion() + " on "
			+ System.getProperty("os.name")	+ " (" + System.getProperty("os.arch") + ")");
		System.out.println("Using " + prefsFile);

		mruFile = new File(prefsFile.getParent(), "tablet-recent.xml");
		TabletFileHandler.loadMRUList(mruFile);

		ColorPrefs.load();
		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);
		prefs.setVariables();


		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.tablet");

		parseCommandLineArguments(args);

		Install4j.doStartUpCheck();

		new Tablet();
	}

	private static void parseCommandLineArguments(String[] args) throws NumberFormatException
	{
		if (args.length > 0)
		{
			// Temporary arrayList for potential files
			ArrayList<String> initFiles = new ArrayList<String>();

			for (int i = 0; i < args.length; i++)
			{
				String arg = args[i];

				// Parse out arguments of form view:contigName:position
				if (arg.toLowerCase().startsWith("view:"))
				{
					contig = arg.substring(arg.indexOf(":") + 1);

					int index = contig.lastIndexOf(":");
					// Check if there were any colons
					if (index != -1)
					{
						// Check that the colon is not the last character in the string
						if ((index + 1) < contig.length())
							position = Integer.parseInt(contig.substring(index + 1));

						// The rest of the string up to the colon must be the contig name
						contig = contig.substring(0, index);
					}
				}

				else
					initFiles.add(arg);

				// Create new array which is the size of the temporary ArrayList.
				// No need to change loading code this way.
				if(initFiles.size() > 0)
					initialFiles = initFiles.toArray(new String[initFiles.size()]);
				else
					initialFiles = null;
			}
		}
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

				long thirtyDays = 2592000000L; // This is 30 days: 2592000000L;
				long thirtyDaysAgo = System.currentTimeMillis() - thirtyDays;

				if (!Prefs.isFirstRun &&
					Long.valueOf(Prefs.visColorSeed) < thirtyDaysAgo)
				{
					Prefs.visColorSeed = "" + System.currentTimeMillis();
					// Force a re-save on prefs so that multiple Tablet launches
					// (without closing any of them) don't show the dialog every
					// time
					prefs.savePreferences(prefsFile, Prefs.class);
					new CitationDialog();
					// TODO extend XMLPreferences to deal with longs?

				}

				// Do we want to open an initial project?
				if (initialFiles != null)
				{
					// Create a TabletFile object from the command line args
					TabletFile tabletFile = TabletFileHandler.createFromFileList(initialFiles);
					// And also add in any contig:position arguments too (note
					// that these will override any entires in a .tablet file)
					if (contig != null)
						tabletFile.contig = contig;
					if (position != null)
						tabletFile.position = position;

					winMain.getCommands().fileOpen(tabletFile);
				}
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

		// Clear the cache
		for (File file: new File(Prefs.cacheFolder).listFiles())
			if (file.getName().contains(".refs") == false)
				file.delete();

		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);
		ColorPrefs.save();
		TabletFileHandler.saveMRUList(mruFile);

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
					TabletUtils.visitURL("http://bioinf.hutton.ac.uk/tablet/help/memory.shtml");

				System.exit(1);
			}
		};

		SwingUtilities.invokeLater(r);
	}

	private static File getPrefsFile()
	{
		// Ensure the .scri-bioinf folder exists
		File fldr = new File(System.getProperty("user.home"), ".scri-bioinf");
		fldr.mkdirs();

		// Color-prefs file
		ColorPrefs.setFile(new File(fldr, "tablet-colors.xml"));

		// Cached reference file
		ConsensusFileCache.setIndexFile(new File(fldr, "tablet-refs.xml"));

		// This is the file we really want
		File file = new File(fldr, "tablet.xml");
		// So if it exists, just use it
		if (file.exists())
			return file;

		// If not, see if the "old" (pre 21/06/2010) file is available
		File old = new File(System.getProperty("user.home"), ".tablet.xml");
		if (old.exists())
			try { FileUtils.copyFile(old, file, true); }
			catch (IOException e) {}

		return file;
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
			TabletFile tabletFile =
				TabletFileHandler.createFromFileList(new String[] { path });
			winMain.getCommands().fileOpen(tabletFile);
		}

		// Otherwise, mark it for opening once Tablet is ready
		else
		{
			initialFiles = new String[1];
			initialFiles[0] = path;
		}
	}

	public static File getLogFile()
	{
		try
		{
			File root = new File(System.getProperty("user.home"));
			File folder = new File(root, ".scri-bioinf");
			File logFile = new File(folder, "tablet.log");

			return logFile;
		}
		catch (Throwable e) { return new File(""); }
	}
}