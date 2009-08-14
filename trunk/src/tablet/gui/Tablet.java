package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import tablet.gui.dialog.*;
import tablet.gui.dialog.prefs.*;

import scri.commons.gui.*;
import scri.commons.file.*;

import apple.dts.samplecode.osxadapter.*;

public class Tablet
{
	private static File prefsFile = new File(
		System.getProperty("user.home"), ".tablet.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	// Optional path to a file to be loaded when Tablet opens
	private static String initialFile = null;

	// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
	public static int menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static String winKey;

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
			initialFile = args[0];

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

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				// Do we want to open an initial project?
				if (initialFile != null)
					winMain.getCommands().fileOpen(new String[] { initialFile });

				if (Install4j.displayUpdate)
					TabletUtils.visitURL("http://bioinf.scri.ac.uk/tablet/svn.txt");
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
		// Attempt to remove any temp files that were in use
		winMain.closeAssembly();

		File cacheDir = SystemUtils.getTempUserDirectory("scri-tablet");
		FileUtils.emptyDirectory(cacheDir, true);

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
		new PreferencesDialog();
	}

	/** "About Tablet" on the OS X system menu. */
	public void osxAbout()
	{
		new AboutDialog();
	}

	/** "Quit Tablet" on the OS X system menu. */
	public boolean osxShutdown()
	{
		shutdown();
		return true;
	}

	public void osxOpen(String path)
	{
		JOptionPane.showMessageDialog(null, Thread.currentThread() + ", " + Thread.currentThread().getName());

		// If Tablet is already open, then open the file straight away
		if (winMain != null && winMain.isVisible())
		{
			// TODO: If we have project modified checks, do them here too
			winMain.getCommands().fileOpen(new String[] { path });
		}

		// Otherwise, mark it for opening once Tablet is ready
		else
			initialFile = path;
	}
}