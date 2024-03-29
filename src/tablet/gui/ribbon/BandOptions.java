// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class BandOptions extends JFlowRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bHidePads;

	private JCommandButton bHideOverview;

	private JCommandToggleButton bHideConsensus;
	private JCommandToggleButton bHideScaleBar;
	private JCommandToggleButton bHideCoverage;
	private JCommandToggleButton bHideContigs;

	BandOptions(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandOptions.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Toggle the display of unpadded scores/values on or off
		bHidePads = new JCommandToggleButton("",
			RibbonController.getIcon("PADDED16", 16));
		Actions.optionsHidePads16 = new ActionToggleButtonModel(false);
		Actions.optionsHidePads16.setSelected(Prefs.visHideUnpaddedValues);
		Actions.optionsHidePads16.addActionListener(this);
		bHidePads.setActionModel(Actions.optionsHidePads16);
		bHidePads.setActionKeyTip("U");
		bHidePads.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHidePads.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHidePads.richtip")));


		JCommandButtonStrip optionsStrip = new JCommandButtonStrip();
		optionsStrip.add(bHidePads);
		addFlowComponent(optionsStrip);


		// Hide the overview panel
		bHideOverview = new JCommandButton("",
			RibbonController.getIcon("HIDEOVERVIEW16", 16));
		Actions.optionsHideOverview = new ActionRepeatableButtonModel(bHideOverview);
		Actions.optionsHideOverview.setSelected(Prefs.guiHideOverview);
		Actions.optionsHideOverview.addActionListener(this);
		bHideOverview.setActionModel(Actions.optionsHideOverview);
		bHideOverview.setActionKeyTip("HO");
		bHideOverview.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHideOverview.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHideOverview.richtip")));

		JCommandButtonStrip menuStrip = new JCommandButtonStrip();
		menuStrip.add(bHideOverview);
		addFlowComponent(menuStrip);


		// Hide the consensus canvas
		bHideConsensus = new JCommandToggleButton("",
			RibbonController.getIcon("HIDECONSENSUS16", 16));
		Actions.optionsHideConsensus = new ActionToggleButtonModel(false);
		Actions.optionsHideConsensus.setSelected(Prefs.guiHideConsensus);
		Actions.optionsHideConsensus.addActionListener(this);
		bHideConsensus.setActionModel(Actions.optionsHideConsensus);
		bHideConsensus.setActionKeyTip("HC");
		bHideConsensus.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHideConsensus.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHideConsensus.richtip")));

		// Hide the scale bar
		bHideScaleBar = new JCommandToggleButton("",
			RibbonController.getIcon("HIDESCALEBAR16", 16));
		Actions.optionsHideScaleBar = new ActionToggleButtonModel(false);
		Actions.optionsHideScaleBar.setSelected(Prefs.guiHideScaleBar);
		Actions.optionsHideScaleBar.addActionListener(this);
		bHideScaleBar.setActionModel(Actions.optionsHideScaleBar);
		bHideScaleBar.setActionKeyTip("HS");
		bHideScaleBar.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHideScaleBar.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHideScaleBar.richtip")));

		// Hide the coverage display
		bHideCoverage = new JCommandToggleButton("",
			RibbonController.getIcon("HIDECOVERAGE16", 16));
		Actions.optionsHideCoverage = new ActionToggleButtonModel(false);
		Actions.optionsHideCoverage.setSelected(Prefs.guiHideCoverage);
		Actions.optionsHideCoverage.addActionListener(this);
		bHideCoverage.setActionModel(Actions.optionsHideCoverage);
		bHideCoverage.setActionKeyTip("HV");
		bHideCoverage.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHideCoverage.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHideCoverage.richtip")));

		// Hide the contigs panel
		bHideContigs = new JCommandToggleButton("",
			RibbonController.getIcon("HIDECONTIGS16", 16));
		Actions.optionsHideContigs = new ActionToggleButtonModel(false);
		Actions.optionsHideContigs.setSelected(Prefs.guiHideContigs);
		Actions.optionsHideContigs.addActionListener(this);
		bHideContigs.setActionModel(Actions.optionsHideContigs);
		bHideContigs.setActionKeyTip("HT");
		bHideContigs.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandOptions.bHideContigs.tooltip"),
			RB.getString("gui.ribbon.BandOptions.bHideContigs.richtip")));

		JCommandButtonStrip panelsStrip = new JCommandButtonStrip();
		panelsStrip.add(bHideConsensus);
		panelsStrip.add(bHideScaleBar);
		panelsStrip.add(bHideCoverage);
		panelsStrip.add(bHideContigs);
		addFlowComponent(panelsStrip);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		// Primary options
		if (source == Actions.optionsHidePads16)
		{
			Prefs.visHideUnpaddedValues = !Prefs.visHideUnpaddedValues;

			winMain.getAssemblyPanel().updateContigInformation();
			winMain.repaint();
		}

		// Panel controls
		else if (source == Actions.optionsHideOverview)
		{
			winMain.getAssemblyPanel().displayOverviewOptions(bHideOverview);
		}
		else if (source == Actions.optionsHideConsensus)
		{
			Prefs.guiHideConsensus = !Prefs.guiHideConsensus;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.optionsHideScaleBar)
		{
			Prefs.guiHideScaleBar = !Prefs.guiHideScaleBar;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.optionsHideCoverage)
		{
			Prefs.guiHideCoverage = !Prefs.guiHideCoverage;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.optionsHideContigs)
		{
			Prefs.guiHideContigs = !Prefs.guiHideContigs;
			winMain.toggleSplitterLocation();
		}
	}
}