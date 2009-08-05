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

	// TODO: The scri-commons XML loading needs to support arrays
	public static String guiRecent01 = "";
	public static String guiRecent02 = "";
	public static String guiRecent03 = "";
	public static String guiRecent04 = "";
	public static String guiRecent05 = "";
	public static String guiRecent06 = "";
	public static String guiRecent07 = "";
	public static String guiRecent08 = "";
	public static String guiRecent09 = "";
	public static String guiRecent10 = "";

	static void setDefaults()
	{

	}

	public static LinkedList<String> getRecentDocuments()
	{
		LinkedList<String> list = new LinkedList<String>();

		list.add(guiRecent01);
		list.add(guiRecent02);
		list.add(guiRecent03);
		list.add(guiRecent04);
		list.add(guiRecent05);
		list.add(guiRecent06);
		list.add(guiRecent07);
		list.add(guiRecent08);
		list.add(guiRecent09);
		list.add(guiRecent10);

		return list;
	}

	public static void setRecentDocument(String document)
	{
		LinkedList<String> list = getRecentDocuments();

		if (list.contains(document))
			list.remove(document);

		list.addFirst(document);

		guiRecent01 = list.get(0);
		guiRecent02 = list.get(1);
		guiRecent03 = list.get(2);
		guiRecent04 = list.get(3);
		guiRecent05 = list.get(4);
		guiRecent06 = list.get(5);
		guiRecent07 = list.get(6);
		guiRecent08 = list.get(7);
		guiRecent09 = list.get(8);
		guiRecent10 = list.get(9);
	}
}