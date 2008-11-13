package av.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import scri.commons.gui.*;

public class AssemblyViewer
{
	private static File prefsFile = new File(
		System.getProperty("user.home"), ".assemblyviewer.xml");
	private static Prefs prefs = new Prefs();

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		Prefs.setDefaults();
		prefs.loadPreferences(prefsFile, Prefs.class);

		new AssemblyViewer();
	}

	AssemblyViewer()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {}

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (winMain.okToExit() == false)
					return;

				shutdown();
			}
		});

		TaskDialog.initialize(winMain, "Assembly Viewer");

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}
}