// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.*;
import tablet.gui.viewer.colors.*;
import tablet.io.*;

import scri.commons.gui.*;
import scri.commons.io.*;

public class Prefs extends XMLPreferences
{
	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;
	public static boolean isSCRIUser = false;
	public static int rating = 0;
	public static String visColorSeed = "" + (System.currentTimeMillis() -
		1987200000L); // First appearance after 7 days (now-23 days)

	// The local working directory for disk caching
	public static String cacheFolder =
		FileUtils.getTempUserDirectory("scri-tablet").getPath();

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	// Unique Tablet ID for this user
	public static String tabletID = SystemUtils.createGUID(32);

	// Display localised text in...
	public static String localeText = "auto";

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 1000;
	public static int guiWinMainHeight = 700;
	public static int guiWinMainX = Integer.MIN_VALUE;
	public static int guiWinMainY = Integer.MIN_VALUE;
	public static boolean guiWinMainMaximized = false;

	// Position on screen of the JumpToDialog
	public static int guiJumpToX = Integer.MIN_VALUE;
	public static int guiJumpToY = Integer.MIN_VALUE;

	// The location of the main splits pane divider
	public static int guiSplitterLocation = 200;
	public static int guiSplitterLocationPrev = 200;

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";

	// Prompt for okToClose messages?
	public static boolean guiWarnOnClose = true;
	public static boolean guiWarnOnExit = true;

	// The warning shown when toggling between padded/unpadded feature values
	public static boolean guiWarnOnPaddedFeatureToggle = true;

	// Warn when SAM/BAM expected reference lengths don't match what's provided
	public static boolean guiWarnRefLengths = true;

	public static boolean guiWarnNoRef = true;

	public static String guiScannerRecent = "";

	public static boolean guiWarnSearchLimitExceeded = true;

	// What filtering option was last in use for the contigs panel
	public static int guiContigsFilter = 0;
	// What filtering option was last in use for the features panel
	public static int guiFeaturesFilter = 0;

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
	// Whether to always draw with colours at all zoom levels
	public static boolean visColorsAtAllZooms = false;

	// Display reads in packed or stacked form?
	public static boolean visPacked = true;

	// "Tag" variants in red
	public static boolean visTagVariants = true;

	// Pad reads with the following gap between each read
	public static int visPadReads = 0;

	// Overview canvas type
	public static int visOverviewType = OverviewCanvas.SCALEDDATA;

	// Current zoom level of the reads canvas
	public static int visReadsZoomLevel = 16;
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

	// True if ?, *, N characters should *not* be marked up in red as variants
	public static boolean visNeverTagUnknownBases = false;

	public static boolean visOverlayNames = false;

	public static boolean guiFindPanelSearchCurrentContig = true;
	public static int guiSearchType = 0;
	public static String recentSearches = "";

	// Truncation limit (number of characters) for text in the tooltip
	public static int visToolTipLimit = 75;

	public static int visCigarInsertMinimum = 10;

	public static boolean visAutoCreateCigarTracks = true;

	// Should the CoveragePrinter class write values for consensus bases that
	// are padded?
	public static boolean printPads = true;

	// Disk caching options
	public static boolean ioCacheReads = true;
	public static boolean ioNeverCacheBAM = true;
	public static boolean cacheMappings = true;
	public static boolean ioDeleteRefCache = false;

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

	public static String recentCustomEnzymes = "";

	public static boolean visOutlineAlpha = true;

	public static boolean visCigarOverlayVisible = true;

	public static int snapshotDelay = 2000;


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