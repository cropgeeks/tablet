// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import tablet.gui.ribbon.*;

import org.jvnet.flamingo.common.model.*;

/**
 * Contains static links to all the action models for the controls in the
 * toolbar so they can be checked/set from anywhere within Tablet.
 */
public class Actions
{
	public static ActionRepeatableButtonModel homeAssembliesOpen16;
	public static ActionRepeatableButtonModel homeAssembliesOpen32;
	public static ActionRepeatableButtonModel applicationMenuSave16;
	public static ActionRepeatableButtonModel homeAssembliesImportFeatures;

	public static ActionToggleButtonModel homeOptionsInfoPane16;
	public static ActionToggleButtonModel homeOptionsHidePads16;
	public static ActionToggleButtonModel homeOptionsHideConsensus;
	public static ActionToggleButtonModel homeOptionsHideScaleBar;
	public static ActionToggleButtonModel homeOptionsHideCoverage;
	public static ActionToggleButtonModel homeOptionsHideContigs;
	public static ActionToggleButtonModel homeOptionsOverlayReadNames;
	public static ActionRepeatableButtonModel homeOptionsHideOverview;
	public static ActionRepeatableButtonModel homeOptionsHideProteins;

	public static ActionToggleButtonModel homeStylesStandard;
	public static ActionToggleButtonModel homeStylesDirection;
	public static ActionToggleButtonModel homeStylesText;
	public static ActionToggleButtonModel homeStylesPacked;
	public static ActionToggleButtonModel homeStylesStacked;
	public static ActionToggleButtonModel homeStylesTagVariants;

	public static ActionRepeatableButtonModel homeNavigatePageLeft;
	public static ActionRepeatableButtonModel homeNavigatePageRight;
	public static ActionRepeatableButtonModel homeNavigateJumpTo;
	public static ActionRepeatableButtonModel homeNavigateNextFeature;
	public static ActionRepeatableButtonModel homeNavigatePrevFeature;
	public static ActionToggleButtonModel homeOptionsReadShadower;
	public static ActionToggleButtonModel homeOptionsHideBaseText;
	public static ActionToggleButtonModel homeOptionsShadowerCentred;

	public static void closed()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(false);
		ApplicationMenu.bExport.setEnabled(false);
		ApplicationMenu.bCoverage.setEnabled(false);
		applicationMenuSave16.setEnabled(false);
		homeAssembliesImportFeatures.setEnabled(false);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(false);
		HomeAdjustBand.variantSliderComponent.setEnabled(false);

		homeOptionsInfoPane16.setEnabled(false);
		homeOptionsHidePads16.setEnabled(false);
		homeOptionsHideOverview.setEnabled(false);
		homeOptionsHideConsensus.setEnabled(false);
		homeOptionsHideScaleBar.setEnabled(false);
		homeOptionsHideCoverage.setEnabled(false);
		homeOptionsHideProteins.setEnabled(false);
		homeOptionsOverlayReadNames.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesDirection.setEnabled(false);
		homeStylesText.setEnabled(false);
		homeStylesPacked.setEnabled(false);
		homeStylesStacked.setEnabled(false);
		homeStylesTagVariants.setEnabled(false);

		homeNavigatePageLeft.setEnabled(false);
		homeNavigatePageRight.setEnabled(false);
		homeNavigateJumpTo.setEnabled(false);
		homeNavigateNextFeature.setEnabled(false);
		homeNavigatePrevFeature.setEnabled(false);
		homeOptionsReadShadower.setEnabled(false);
		homeOptionsHideBaseText.setEnabled(false);
		homeOptionsShadowerCentred.setEnabled(false);
	}

	public static void openedNoContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
		ApplicationMenu.bExport.setEnabled(false);
		ApplicationMenu.bCoverage.setEnabled(true);
		applicationMenuSave16.setEnabled(false);
		homeAssembliesImportFeatures.setEnabled(true);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(false);
		HomeAdjustBand.variantSliderComponent.setEnabled(false);

		homeOptionsInfoPane16.setEnabled(false);
		homeOptionsHidePads16.setEnabled(false);
		homeOptionsHideOverview.setEnabled(false);
		homeOptionsHideConsensus.setEnabled(false);
		homeOptionsHideScaleBar.setEnabled(false);
		homeOptionsHideCoverage.setEnabled(false);
		homeOptionsHideProteins.setEnabled(false);
		homeOptionsOverlayReadNames.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesDirection.setEnabled(false);
		homeStylesText.setEnabled(false);
		homeStylesPacked.setEnabled(false);
		homeStylesStacked.setEnabled(false);
		homeStylesTagVariants.setEnabled(false);

		homeNavigatePageLeft.setEnabled(false);
		homeNavigatePageRight.setEnabled(false);
		homeNavigateJumpTo.setEnabled(false);
		homeNavigateNextFeature.setEnabled(false);
		homeNavigatePrevFeature.setEnabled(false);
		homeOptionsReadShadower.setEnabled(false);
		homeOptionsHideBaseText.setEnabled(false);
		homeOptionsShadowerCentred.setEnabled(false);
	}

	public static void openedContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
		ApplicationMenu.bExport.setEnabled(true);
		ApplicationMenu.bCoverage.setEnabled(true);
		applicationMenuSave16.setEnabled(false);
		homeAssembliesImportFeatures.setEnabled(true);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(true);
		HomeAdjustBand.variantSliderComponent.setEnabled(true);

		homeOptionsInfoPane16.setEnabled(true);
		homeOptionsHidePads16.setEnabled(true);
		homeOptionsHideOverview.setEnabled(true);
		homeOptionsHideConsensus.setEnabled(true);
		homeOptionsHideScaleBar.setEnabled(true);
		homeOptionsHideCoverage.setEnabled(true);
		homeOptionsHideProteins.setEnabled(true);
		homeOptionsOverlayReadNames.setEnabled(!Prefs.visPacked);

		homeStylesStandard.setEnabled(true);
		homeStylesDirection.setEnabled(true);
		homeStylesText.setEnabled(true);
		homeStylesPacked.setEnabled(true);
		homeStylesStacked.setEnabled(true);
		homeStylesTagVariants.setEnabled(true);

		homeNavigatePageLeft.setEnabled(true);
		homeNavigatePageRight.setEnabled(true);
		homeNavigateJumpTo.setEnabled(true);
		homeNavigateNextFeature.setEnabled(true);
		homeNavigatePrevFeature.setEnabled(true);
		homeOptionsReadShadower.setEnabled(true);
		homeOptionsHideBaseText.setEnabled(true);
		homeOptionsShadowerCentred.setEnabled(true);
	}
}