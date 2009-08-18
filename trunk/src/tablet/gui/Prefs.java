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

	// Last known (install4j) version number of Tablet that was run
	public static String lastVersion = null;
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

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";

	// A list of previously accessed documents
	public static String[] guiRecentDocs = new String[10];

	// A list of previously accessed ace documents
	public static String aceRecentDocs = "";
	// A list of previously accessed afg documents
	public static String afgRecentDocs = "";
	// A list of previously accessed soap documents
	public static String soapRecentDocs = "";
	// A list of previously accessed fasta documents
	public static String fastaRecentDocs = "";

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

	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentFiles(String[] files, String[] recentDocs)
	{
		for (int i = 0; i < files.length; i++)
			recentDocs[i] = files[i];
	}
}