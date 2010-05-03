// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import tablet.data.*;
import tablet.gui.ribbon.*;

import org.jvnet.flamingo.common.model.*;

/**
 * Contains static links to all the action models for the controls in the
 * toolbar so they can be checked/set from anywhere within Tablet.
 */
public class Actions
{
	public static ActionRepeatableButtonModel assembliesOpen16;
	public static ActionRepeatableButtonModel assembliesOpen32;
	public static ActionRepeatableButtonModel applicationMenuSave16;
	public static ActionRepeatableButtonModel assembliesImportFeatures;

	public static ActionToggleButtonModel optionsHidePads16;
	public static ActionToggleButtonModel optionsHideConsensus;
	public static ActionToggleButtonModel optionsHideScaleBar;
	public static ActionToggleButtonModel optionsHideCoverage;
	public static ActionToggleButtonModel optionsHideContigs;
	public static ActionRepeatableButtonModel optionsHideOverview;

	public static ActionToggleButtonModel stylesStandard;
	public static ActionToggleButtonModel stylesDirection;
	public static ActionToggleButtonModel stylesText;
	public static ActionToggleButtonModel stylesPacked;
	public static ActionToggleButtonModel stylesStacked;
	public static ActionToggleButtonModel stylesTagVariants;

	public static ActionRepeatableButtonModel navigatePageLeft;
	public static ActionRepeatableButtonModel navigatePageRight;
	public static ActionRepeatableButtonModel navigateJumpTo;
	public static ActionRepeatableButtonModel navigateNextFeature;
	public static ActionRepeatableButtonModel navigatePrevFeature;

	public static ActionRepeatableButtonModel bamWindow;
	public static ActionRepeatableButtonModel bamPrevious;

	public static ActionToggleButtonModel overlaysInfoPane;
	public static ActionToggleButtonModel overlaysEnableText;
	public static ActionToggleButtonModel overlayReadNames;
	public static ActionToggleButtonModel overlayShadowingOff;
	public static ActionToggleButtonModel overlayShadowingCenter;
	public static ActionToggleButtonModel overlayShadowingCustom;

	public static ActionToggleButtonModel[] proteinEnable;


	public static void closed()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(false);
		ApplicationMenu.bExport.setEnabled(false);
		ApplicationMenu.bCoverage.setEnabled(false);
		applicationMenuSave16.setEnabled(false);
		assembliesImportFeatures.setEnabled(false);

		// Ribbon controls
		BandAdjust.zoomSliderComponent.setEnabled(false);
		BandAdjust.variantSliderComponent.setEnabled(false);

		optionsHidePads16.setEnabled(false);
		optionsHideOverview.setEnabled(false);
		optionsHideConsensus.setEnabled(false);
		optionsHideScaleBar.setEnabled(false);
		optionsHideCoverage.setEnabled(false);

		stylesStandard.setEnabled(false);
		stylesDirection.setEnabled(false);
		stylesText.setEnabled(false);
		stylesPacked.setEnabled(false);
		stylesStacked.setEnabled(false);
		stylesTagVariants.setEnabled(false);

		navigatePageLeft.setEnabled(false);
		navigatePageRight.setEnabled(false);
		navigateJumpTo.setEnabled(false);
		navigateNextFeature.setEnabled(false);
		navigatePrevFeature.setEnabled(false);

		for (ActionToggleButtonModel b: proteinEnable)
			b.setEnabled(false);

		bamPrevious.setEnabled(false);

		overlaysInfoPane.setEnabled(false);
		overlaysEnableText.setEnabled(false);
		overlayReadNames.setEnabled(false);
		overlayShadowingOff.setEnabled(false);
		overlayShadowingCenter.setEnabled(false);
		overlayShadowingCustom.setEnabled(false);
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
		assembliesImportFeatures.setEnabled(true);

		// Ribbon controls
		BandAdjust.zoomSliderComponent.setEnabled(false);
		BandAdjust.variantSliderComponent.setEnabled(false);

		optionsHidePads16.setEnabled(false);
		optionsHideOverview.setEnabled(false);
		optionsHideConsensus.setEnabled(false);
		optionsHideScaleBar.setEnabled(false);
		optionsHideCoverage.setEnabled(false);

		stylesStandard.setEnabled(false);
		stylesDirection.setEnabled(false);
		stylesText.setEnabled(false);
		stylesPacked.setEnabled(false);
		stylesStacked.setEnabled(false);
		stylesTagVariants.setEnabled(false);

		navigatePageLeft.setEnabled(false);
		navigatePageRight.setEnabled(false);
		navigateJumpTo.setEnabled(false);
		navigateNextFeature.setEnabled(false);
		navigatePrevFeature.setEnabled(false);

		for (ActionToggleButtonModel b: proteinEnable)
			b.setEnabled(false);

		bamPrevious.setEnabled(false);

		overlaysInfoPane.setEnabled(false);
		overlaysEnableText.setEnabled(false);
		overlayReadNames.setEnabled(false);
		overlayShadowingOff.setEnabled(false);
		overlayShadowingCenter.setEnabled(false);
		overlayShadowingCustom.setEnabled(false);
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
		assembliesImportFeatures.setEnabled(true);

		// Ribbon controls
		BandAdjust.zoomSliderComponent.setEnabled(true);
		BandAdjust.variantSliderComponent.setEnabled(true);

		optionsHidePads16.setEnabled(true);
		optionsHideOverview.setEnabled(true);
		optionsHideConsensus.setEnabled(true);
		optionsHideScaleBar.setEnabled(true);
		optionsHideCoverage.setEnabled(true);

		stylesStandard.setEnabled(true);
		stylesDirection.setEnabled(true);
		stylesText.setEnabled(true);
		stylesPacked.setEnabled(true);
		stylesStacked.setEnabled(true);
		stylesTagVariants.setEnabled(true);

		navigatePageLeft.setEnabled(true);
		navigatePageRight.setEnabled(true);
		navigateJumpTo.setEnabled(true);
		navigateNextFeature.setEnabled(true);
		navigatePrevFeature.setEnabled(true);

		for (ActionToggleButtonModel b: proteinEnable)
			b.setEnabled(true);

		Assembly assembly = Tablet.winMain.getAssemblyPanel().getAssembly();
		bamPrevious.setEnabled(assembly.getBamBam() != null);

		overlaysInfoPane.setEnabled(true);
		overlaysEnableText.setEnabled(true);
		overlayReadNames.setEnabled(!Prefs.visPacked);
		overlayShadowingOff.setEnabled(true);
		overlayShadowingCenter.setEnabled(true);
		overlayShadowingCustom.setEnabled(true);
	}
}