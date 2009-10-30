// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;

import tablet.gui.viewer.*;
import tablet.gui.viewer.colors.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;
	public static boolean isSCRIUser = false;
	public static int rating = 0;

	// The local working directory for disk caching
	public static String cacheDir =
		SystemUtils.getTempUserDirectory("scri-tablet").getPath();

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	// Unique Tablet ID for this user
	public static String tabletID = SystemUtils.createGUID(32);

	// Display localised text in...
	public static String localeText = "auto";

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 1000;
	public static int guiWinMainHeight = 700;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// Position on screen of the JumpToDialog
	public static int guiJumpToX = -9999;
	public static int guiJumpToY = -9999;

	// The location of the main splits pane divider
	public static int guiSplitterLocation = 200;
	public static int guiSplitterLocationPrev = 200;

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";

	// A list of previously accessed documents
	public static String[] guiRecentDocs = new String[10];

	// Prompt for okToClose messages?
	public static boolean guiWarnOnClose = true;
	public static boolean guiWarnOnExit = true;

	// The warning shown when toggling between padded/unpadded feature values
	public static boolean guiWarnOnPaddedFeatureToggle = true;

	// A list of previously accessed assembly documents
	public static String assRecentDocs = "";
	// A list of previously accessed referebce documents
	public static String refRecentDocs = "";
	// Was a selection made from the reference file
	public static boolean refNotUsed = true;
	public static boolean guiWarnNoRef = true;

	// What filtering option was last in use for the contigs panel
	public static int guiContigsFilter = 0;

	// What panels should be hidden (to give more space to the main canvas)
	public static boolean guiHideOverview = false;
	public static boolean guiHideConsensus = false;
	public static boolean guiHideScaleBar = false;
	public static boolean guiHideContigs = false;
	public static boolean guiHideCoverage = false;

	// What was that last type of jumpToBase jump performed?
	public static boolean guiUsePaddedJumpToBases = true;
	public static int guiJumpToBase = 1;
	public static boolean guiFeaturesArePadded = true;

	// Colour scheme in use
	public static int visColorScheme = ColorScheme.STANDARD;

	// Display reads in packed or stacked form?
	public static boolean visPacked = true;

	// Overview canvas type
	public static int visOverviewType = OverviewCanvas.SCALEDDATA;

	// Current zoom level of the reads canvas
	public static int visReadsCanvasZoom = 7;
	// Current intensity level for the variant highlighting
	public static int visVariantAlpha = 0;

	// Display popup info tooltips on the main canvas or not
	public static boolean visInfoPaneActive = true;
	// Hide the display of unpadded values (in various places) or not
	public static boolean visHideUnpaddedValues = false;

	// Which protein translations should be shown?
	public static String visProteins = "1 0 0 0 0 0";

	// What type of pad/stop characters should be displayed
	public static int visPadCharType = 0;
	public static int visStopCharType = 0;

	static void setDefaults()
	{

	}

	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentDocument(String[] files)
	{
		// Convert the array of files back into a single string
		String mostRecent = files[0];
		for (int i = 1; i < files.length; i++)
			mostRecent += "<!TABLET!>" + files[i];

		LinkedList<String> list = new LinkedList<String>();
		for (String file: guiRecentDocs)
			list.add(file);

		if (list.contains(mostRecent))
			list.remove(mostRecent);

		list.addFirst(mostRecent);

		for (int i = 0; i < guiRecentDocs.length; i++)
			guiRecentDocs[i] = list.get(i);
	}
}