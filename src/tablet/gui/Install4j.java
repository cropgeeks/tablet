// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import com.install4j.api.*;
import com.install4j.api.launcher.*;
import com.install4j.api.update.*;

import scri.commons.gui.*;

/**
 * Utility class that performs install4j updater actions on behalf of Tablet.
 */
public class Install4j
{
	private static String URL = "http://bioinf.scri.ac.uk/tablet/installers/updates.xml";

	public static String VERSION;

	public static final int NEVER = 0;
	public static final int STARTUP = 1;
	public static final int DAILY = 2;
	public static final int WEEKLY = 3;
	public static final int MONTHLY = 4;

	/**
	 * install4j update check. This will only work when running under the full
	 * install4j environment, so expect exceptions everywhere else
	 */
	static void doStartUpCheck()
	{
		getVersion();
		pingServer();

		Runnable r = new Runnable() {
			public void run()
			{
				checkForUpdate();
			}
		};

		new Thread(r).start();
	}

	private static void checkForUpdate()
	{
		try
		{
			switch (Prefs.guiUpdateSchedule)
			{
				case STARTUP:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);
					break;
				case DAILY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.DAILY);
					break;
				case WEEKLY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.WEEKLY);
					break;
				case MONTHLY:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.MONTHLY);
					break;

				default:
					UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.NEVER);
			}

			if (UpdateScheduleRegistry.checkAndReset() == false)
				return;

			UpdateDescriptor ud = UpdateChecker.getUpdateDescriptor(URL, ApplicationDisplayMode.GUI);

			if (ud.getPossibleUpdateEntry() != null)
				checkForUpdate(true);
		}
		catch (Exception e) { e.printStackTrace(); }
		catch (Error e) { e.printStackTrace(); }
	}

	/**
	 * Shows the install4j updater app to check for updates and download/install
	 * any that are found.
	 */
	static void checkForUpdate(boolean block)
	{
		try
		{
			ApplicationLauncher.launchApplication("65", null, block, null);
		}
		catch (IOException e) {}
	}

	private static void getVersion()
	{
		// Attempt to get the version string from the jar's manifest
		VERSION = Tablet.class.getPackage().getImplementationVersion();

		// If it's not found, we must be running the development version
		if (VERSION == null)
			VERSION = "x.xx.xx.xx";
	}

	private static void pingServer()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					// Track this user as an SCRI user if they have ever run the
					// software on the SCRI network
					if (Prefs.isSCRIUser == false && SystemUtils.isSCRIUser())
						Prefs.isSCRIUser = true;

					// Safely encode the URL's parameters
					String id = URLEncoder.encode(Prefs.tabletID, "UTF-8");
					String version = URLEncoder.encode(VERSION, "UTF-8");
					String locale = URLEncoder.encode("" + Locale.getDefault(), "UTF-8");
					String os = URLEncoder.encode(System.getProperty("os.name")
						+ " (" + System.getProperty("os.arch") + ")", "UTF-8");
					String user = URLEncoder.encode(System.getProperty("user.name"), "UTF-8");

					String addr = "http://bioinf.scri.ac.uk/tablet/logs/tablet.pl"
						+ "?id=" + id
						+ "&version=" + version
						+ "&locale=" + locale
						+ "&rating=" + Prefs.rating
						+ "&os=" + os;

					// We DO NOT log usernames from non-SCRI addresses
					if (Prefs.isSCRIUser)
						addr += "&user=" + user;

					// Nudges the cgi script to log the fact that a version of
					// Flapjack has been run
					URL url = new URL(addr);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					c.getResponseCode();
					c.disconnect();
				}
				catch (Exception e) {}
			}
		};

		// We run this in a separate thread to avoid any waits due to lack of an
		// internet connection or the server being non-responsive
		new Thread(r).start();
	}
}