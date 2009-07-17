package tablet.gui;

import tablet.gui.ribbon.*;

import org.jvnet.flamingo.common.model.*;

/**
 * Contains static links to all the action models for the controls in the
 * toolbar so they can be checked/set from anywhere within Tablet.
 */
public class Actions
{
	private RibbonController ribbon;

	public static ActionRepeatableButtonModel homeAssembliesOpen16;
	public static ActionRepeatableButtonModel homeAssembliesOpen32;
	public static ActionRepeatableButtonModel applicationMenuSave16;
	public static ActionRepeatableButtonModel homeAssembliesImportFeatures;

	public static ActionToggleButtonModel homeOptionsInfoPane16;
	public static ActionToggleButtonModel homeOptionsHidePads16;
	public static ActionToggleButtonModel homeOptionsHideOverview;
	public static ActionToggleButtonModel homeOptionsHideConsensus;
	public static ActionToggleButtonModel homeOptionsHideScaleBar;
	public static ActionToggleButtonModel homeOptionsHideContigs;
	public static ActionRepeatableButtonModel homeOptionsHideProteins;

	public static ActionToggleButtonModel homeStylesStandard;
	public static ActionToggleButtonModel homeStylesText;
	public static ActionToggleButtonModel homeStylesPacked;
	public static ActionToggleButtonModel homeStylesStacked;

	public static ActionRepeatableButtonModel homeNavigatePageLeft;
	public static ActionRepeatableButtonModel homeNavigatePageRight;
	public static ActionRepeatableButtonModel homeNavigateJumpTo;

	void setRibbonController(RibbonController ribbon)
		{ this.ribbon = ribbon; }

	public static void closed()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(false);
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
		homeOptionsHideContigs.setEnabled(false);
		homeOptionsHideProteins.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesText.setEnabled(false);
		homeStylesPacked.setEnabled(false);
		homeStylesStacked.setEnabled(false);

		homeNavigatePageLeft.setEnabled(false);
		homeNavigatePageRight.setEnabled(false);
		homeNavigateJumpTo.setEnabled(false);
	}

	public static void openedNoContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
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
		homeOptionsHideContigs.setEnabled(false);
		homeOptionsHideProteins.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesText.setEnabled(false);
		homeStylesPacked.setEnabled(false);
		homeStylesStacked.setEnabled(false);

		homeNavigatePageLeft.setEnabled(false);
		homeNavigatePageRight.setEnabled(false);
		homeNavigateJumpTo.setEnabled(false);
	}

	public static void openedContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
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
		homeOptionsHideContigs.setEnabled(false);
		homeOptionsHideProteins.setEnabled(true);

		homeStylesStandard.setEnabled(true);
		homeStylesText.setEnabled(true);
		homeStylesPacked.setEnabled(true);
		homeStylesStacked.setEnabled(true);

		homeNavigatePageLeft.setEnabled(true);
		homeNavigatePageRight.setEnabled(true);
		homeNavigateJumpTo.setEnabled(true);
	}
}