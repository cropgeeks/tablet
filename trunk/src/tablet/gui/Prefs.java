// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.*;
import tablet.gui.viewer.colors.*;
import tablet.io.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;
	public static boolean isHuttonised = false;
	public static boolean isSCRIUser = false;
	public static int rating = 0;
	public static String visColorSeed = "" + (System.currentTimeMillis() -
		1987200000L); // First appearance after 7 days (now-23 days)

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

	// Warn when SAM/BAM expected reference lengths don't match what's provided
	public static boolean guiWarnRefLengths = true;

	// A list of previously accessed assembly documents
	public static String assRecentDocs = "";
	// A list of previously accessed referebce documents
	public static String refRecentDocs = "";
	// Was a selection made from the reference file
	public static boolean refNotUsed = true;
	public static boolean guiWarnNoRef = true;

	public static String guiScannerRecent = "";

	public static boolean guiWarnSearchLimitExceeded = true;

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

	// Finder preferences.
	public static boolean guiRegexSearching = true;
	public static int guiSearchLimit = 500;
	public static boolean guiSearchIgnorePads = true;
	public static int guiSearchDBLimit = 500000;

	// Colour scheme in use
	public static int visColorScheme = ReadScheme.STANDARD;

	// Display reads in packed or stacked form?
	public static boolean visPacked = true;

	// "Tag" variants in red
	public static boolean visTagVariants = true;

	// Pad reads with the following gap between each read
	public static int visPadReads = 0;

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

	public static boolean visOverlayNames = false;

	public static boolean guiFindPanelSearchCurrentContig = true;
	public static int guiFindPanelSearchType = 0;
	public static String recentSearches = "";

	// Truncation limit (number of characters) for text in the tooltip
	public static int visToolTipLimit = 75;

	// Should the CoveragePrinter class write values for consensus bases that
	// are padded?
	public static boolean printPads = true;

	// Disk caching options
	public static boolean cacheReads = true;
	public static boolean cacheMappings = true;

	// Bam assembly options
	public static int bamSize = 25000;

	public static boolean guiHideOverviewPositions = false;

	public static int visReadShadowing = 0;

	public static boolean visEnableText = true;


	public static boolean ioAceProcessQA = true;
	public static boolean ioBamValidationIsLenient = true;
	public static boolean ioAmbiguousToN = false;

	public static String ioSamtoolsPath = "";

	// Display reads in paired form?
	public static boolean visPaired = false;

	public static boolean visPairLines = true;


	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentDocument(AssemblyFile[] files)
	{
		// Convert the array of files back into a single string
		String mostRecent = files[0].getPath();
		for (int i = 1; i < files.length; i++)
			mostRecent += "<!TABLET!>" + files[i].getPath();

		LinkedList<String> list = new LinkedList<String>();
		for (String file: guiRecentDocs)
			list.add(file);

		if (list.contains(mostRecent))
			list.remove(mostRecent);

		list.addFirst(mostRecent);

		for (int i = 0; i < guiRecentDocs.length; i++)
			guiRecentDocs[i] = list.get(i);
	}

	// Sets some static variables within other classes to their correct values.
	// Done for classes that we'd prefer not to have direct access to Prefs.
	public static void setVariables()
	{
		// tablet.data:
		Sequence.AMBIGUOUS_TO_N = Prefs.ioAmbiguousToN;

		// tablet.data.auxiliary
		Feature.ISPADDED = Prefs.guiFeaturesArePadded;

		// tablet.io:
		AceFileReader.PROCESS_QA = Prefs.ioAceProcessQA;
		BamFileHandler.VALIDATION_LENIENT = Prefs.ioBamValidationIsLenient;
	}
}