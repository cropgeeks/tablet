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

	public static ActionToggleButtonModel homeOptionsInfoPane16;
	public static ActionToggleButtonModel homeOptionsHidePads16;

	public static ActionToggleButtonModel homeStylesStandard;
	public static ActionToggleButtonModel homeStylesText;

	void setRibbonController(RibbonController ribbon)
		{ this.ribbon = ribbon; }

	public static void closed()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(false);
		applicationMenuSave16.setEnabled(false);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(false);
		HomeAdjustBand.variantSliderComponent.setEnabled(false);

		homeOptionsInfoPane16.setEnabled(false);
		homeOptionsHidePads16.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesText.setEnabled(false);
	}

	public static void openedNoContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
		applicationMenuSave16.setEnabled(false);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(false);
		HomeAdjustBand.variantSliderComponent.setEnabled(false);

		homeOptionsInfoPane16.setEnabled(false);
		homeOptionsHidePads16.setEnabled(false);

		homeStylesStandard.setEnabled(false);
		homeStylesText.setEnabled(false);
	}

	public static void openedContigSelected()
	{
		// Application menu options
		ApplicationMenu.mSave.setEnabled(false);
		ApplicationMenu.mSaveAs.setEnabled(false);
		ApplicationMenu.mClose.setEnabled(true);
		applicationMenuSave16.setEnabled(false);

		// Ribbon controls
		HomeAdjustBand.zoomSliderComponent.setEnabled(true);
		HomeAdjustBand.variantSliderComponent.setEnabled(true);

		homeOptionsInfoPane16.setEnabled(true);
		homeOptionsHidePads16.setEnabled(true);

		homeStylesStandard.setEnabled(true);
		homeStylesText.setEnabled(true);
	}
}