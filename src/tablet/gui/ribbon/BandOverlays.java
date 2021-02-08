// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

public class BandOverlays extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bInfoPane;
	private JCommandToggleButton bEnableText;
	private JCommandToggleButton bReadNames;

	private CommandToggleButtonGroup shadowGroup;
	private JCommandToggleButton bShadowingOff;
	private JCommandToggleButton bShadowingCenter;
	private JCommandToggleButton bShadowingCustom;

	private JCommandToggleButton bHideCigarOverlayer;
	private JCommandToggleButton bNeverFadeOverlays;

	BandOverlays(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandOverlays.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));

		// Toggle the infoPane tooltips on or off
		bInfoPane = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bInfoPane"),
			RibbonController.getIcon("INFOPANE16", 16));
		Actions.overlaysInfoPane = new ActionToggleButtonModel(false);
		Actions.overlaysInfoPane.setSelected(Prefs.visInfoPaneActive);
		Actions.overlaysInfoPane.addActionListener(this);
		bInfoPane.setActionModel(Actions.overlaysInfoPane);
		bInfoPane.setActionKeyTip("I");
		bInfoPane.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bInfoPane.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bInfoPane.richtip")));


		// Toggle the text overlays on or off
		bEnableText = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bEnableText"),
			RibbonController.getIcon("ENABLETEXT16", 16));
		Actions.overlaysEnableText = new ActionToggleButtonModel(false);
		Actions.overlaysEnableText.setSelected(Prefs.visEnableText);
		Actions.overlaysEnableText.addActionListener(this);
		bEnableText.setActionModel(Actions.overlaysEnableText);
		bEnableText.setActionKeyTip("B");
		bEnableText.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bEnableText.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bEnableText.richtip")));


		// Toggle overlaying read names on or off
		bReadNames = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bReadNames"),
			RibbonController.getIcon("OVERLAYNAMES16", 16));
		Actions.overlayReadNames = new ActionToggleButtonModel(false);
		Actions.overlayReadNames.setSelected(Prefs.visOverlayNames);
		Actions.overlayReadNames.addActionListener(this);
		bReadNames.setActionModel(Actions.overlayReadNames);
		bReadNames.setActionKeyTip("N");
		bReadNames.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverlays.bReadNames.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverlays.bReadNames.richtip")));


		addCommandButton(bInfoPane, RibbonElementPriority.MEDIUM);
		addCommandButton(bEnableText, RibbonElementPriority.MEDIUM);
		addCommandButton(bReadNames, RibbonElementPriority.MEDIUM);



		// Read shadowing - OFF
		bShadowingOff = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bShadowingOff"),
			RibbonController.getIcon("SHADOWOFF16", 16));
		Actions.overlayShadowingOff = new ActionToggleButtonModel(false);
		Actions.overlayShadowingOff.setSelected(Prefs.visReadShadowing == 0);
		Actions.overlayShadowingOff.addActionListener(this);
		bShadowingOff.setActionModel(Actions.overlayShadowingOff);
		bShadowingOff.setActionKeyTip("RO");
		bShadowingOff.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bShadowingOff.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bShadowingOff.richtip")));

		// Read shadowing - CENTER
		bShadowingCenter = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bShadowingCenter"),
			RibbonController.getIcon("SHADOWCENTER16", 16));
		Actions.overlayShadowingCenter = new ActionToggleButtonModel(false);
		Actions.overlayShadowingCenter.setSelected(Prefs.visReadShadowing == 1);
		Actions.overlayShadowingCenter.addActionListener(this);
		bShadowingCenter.setActionModel(Actions.overlayShadowingCenter);
		bShadowingCenter.setActionKeyTip("RT");
		bShadowingCenter.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bShadowingCenter.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bShadowingCenter.richtip")));

		// Read shadowing - CUSTOM
		bShadowingCustom = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bShadowingCustom"),
			RibbonController.getIcon("SHADOWCUSTOM16", 16));
		Actions.overlayShadowingCustom = new ActionToggleButtonModel(false);
		Actions.overlayShadowingCustom.setSelected(Prefs.visReadShadowing == 2);
		Actions.overlayShadowingCustom.addActionListener(this);
		bShadowingCustom.setActionModel(Actions.overlayShadowingCustom);
		bShadowingCustom.setActionKeyTip("RC");
		bShadowingCustom.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bShadowingCustom.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bShadowingCustom.richtip")));

		shadowGroup = new CommandToggleButtonGroup();
		shadowGroup.add(bShadowingOff);
		shadowGroup.add(bShadowingCenter);
		shadowGroup.add(bShadowingCustom);

		addCommandButton(bShadowingOff, RibbonElementPriority.MEDIUM);
		addCommandButton(bShadowingCenter, RibbonElementPriority.MEDIUM);
		addCommandButton(bShadowingCustom, RibbonElementPriority.MEDIUM);

			// Hide the overview panel
		bHideCigarOverlayer = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bShowCigarOverlayer"),
			RibbonController.getIcon("CIGAROVERLAY", 16));
		Actions.overlayShowCigar = new ActionToggleButtonModel(false);
		Actions.overlayShowCigar.setSelected(Prefs.visCigarOverlayVisible);
		Actions.overlayShowCigar.addActionListener(this);
		bHideCigarOverlayer.setActionModel(Actions.overlayShowCigar);
		bHideCigarOverlayer.setActionKeyTip("HC");
		bHideCigarOverlayer.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bShowCigarOverlayer.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bShowCigarOverlayer.richtip")));

		addCommandButton(bHideCigarOverlayer, RibbonElementPriority.MEDIUM);


		bNeverFadeOverlays = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverlays.bNeverFadeOverlays"),
			RibbonController.getIcon("STICKYHIGHLIGHTS", 16));
		Actions.overlayNeverFade = new ActionToggleButtonModel(false);
		Actions.overlayNeverFade.setSelected(Prefs.visNeverFadeOverlays);
		Actions.overlayNeverFade.addActionListener(this);
		bNeverFadeOverlays.setActionModel(Actions.overlayNeverFade);
		bNeverFadeOverlays.setActionKeyTip("HF");
		bNeverFadeOverlays.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOverlays.bNeverFadeOverlays.tooltip"),
			RB.getString("gui.ribbon.BandOverlays.bNeverFadeOverlays.richtip")));

		addCommandButton(bNeverFadeOverlays, RibbonElementPriority.MEDIUM);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.overlaysInfoPane)
			Prefs.visInfoPaneActive = !Prefs.visInfoPaneActive;

		else if (e.getSource() == Actions.overlaysEnableText)
		{
			Prefs.visEnableText = !Prefs.visEnableText;
			winMain.getAssemblyPanel().updateColorScheme();
		}

		else if (e.getSource() == Actions.overlayReadNames)
			actionReadNames();

		else if (e.getSource() == Actions.overlayShadowingOff)
			actionShadowingOff();

		else if (e.getSource() == Actions.overlayShadowingCenter)
			actionShadowingCenter();

		else if (e.getSource() == Actions.overlayShadowingCustom)
			actionShadowingCustom();

		else if (e.getSource() == Actions.overlayShowCigar)
		{
			Prefs.visCigarOverlayVisible = !Prefs.visCigarOverlayVisible;
			winMain.getAssemblyPanel().toggleCigarOverlayer();
		}

		else if (e.getSource() == Actions.overlayNeverFade)
			winMain.getAssemblyPanel().toggleFadingOverlays();
	}

	public void actionShadowingOff()
	{
		Prefs.visReadShadowing = 0;
		winMain.getAssemblyPanel().repaint();

		// BUG: Workaround for API allowing toggle groups to be unselected
		Actions.overlayShadowingOff.setSelected(true);
	}

	public void actionShadowingCenter()
	{
		Prefs.visReadShadowing = 1;
		winMain.getAssemblyPanel().repaint();

		// BUG: Workaround for API allowing toggle groups to be unselected
		Actions.overlayShadowingCenter.setSelected(true);
	}

	public void actionShadowingCustom()
	{
		Prefs.visReadShadowing = 2;
		winMain.getAssemblyPanel().repaint();

		// BUG: Workaround for API allowing toggle groups to be unselected
		Actions.overlayShadowingCustom.setSelected(true);
	}

	void actionReadNames()
	{
		Prefs.visOverlayNames = !Prefs.visOverlayNames;
		winMain.getAssemblyPanel().toggleNameOverlay();
	}
}