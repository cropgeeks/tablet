package tablet.gui;

import java.awt.*;

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
	public static int guiWinMainWidth = 800;
	public static int guiWinMainHeight = 600;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// The location of the main splits pane divider
	public static int guiSplitterLocation = 150;

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";

	// Display reads in packed or stacked form?
	public static int visReadLayout = 1;

	// Current zoom level of the reads canvas
	public static int visReadsCanvasZoom = 7;
	// Current intensity level for the variant highlighting
	public static int visVariantAlpha = 0;

	static void setDefaults()
	{

	}
}
