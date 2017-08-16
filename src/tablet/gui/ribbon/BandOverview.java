// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

import tablet.gui.*;
import tablet.gui.viewer.*;

public class BandOverview extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private CommandToggleButtonGroup group;
	private JCommandToggleButton bScaled, bCoverage, bCoordinates;
	private JCommandButton bReset, bSubset;

	BandOverview(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandOverview.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Setup buttons
		bScaled = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverview.bScaled"),
			RibbonController.getIcon("OVERVIEW", 32));
		Actions.overviewScaled = new ActionToggleButtonModel(false);
		Actions.overviewScaled.addActionListener(this);
		if(Prefs.visOverviewType == OverviewCanvas.SCALEDDATA)
			Actions.overviewScaled.setSelected(true);
		bScaled.setActionModel(Actions.overviewScaled);
		bScaled.setActionKeyTip("W");
		bScaled.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverview.bScaled.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverview.bScaled.richtip")));

		bCoverage = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverview.bCoverage"),
			RibbonController.getIcon("COVERAGEOVERVIEW", 32));
		Actions.overviewCoverage = new ActionToggleButtonModel(false);
		Actions.overviewCoverage.addActionListener(this);
		if(Prefs.visOverviewType == OverviewCanvas.COVERAGE)
			Actions.overviewCoverage.setSelected(true);
		bCoverage.setActionModel(Actions.overviewCoverage);
		bCoverage.setActionKeyTip("W");
		bCoverage.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverview.bCoverage.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverview.bCoverage.richtip")));

		bSubset = new JCommandButton(
			RB.getString("gui.ribbon.BandOverview.bSubset"),
			RibbonController.getIcon("SUBSETOVERVIEW", 16));
		Actions.overviewSubset = new ActionRepeatableButtonModel(bSubset);
		Actions.overviewSubset.addActionListener(this);
		bSubset.setActionModel(Actions.overviewSubset);
		bSubset.setActionKeyTip("W");
		bSubset.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverview.bSubset.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverview.bSubset.richtip")));

		bReset = new JCommandButton(
			RB.getString("gui.ribbon.BandOverview.bReset"),
			RibbonController.getIcon("RESETOVERVIEW", 16));
		Actions.overviewReset = new ActionRepeatableButtonModel(bReset);
		Actions.overviewReset.addActionListener(this);
		bReset.setActionModel(Actions.overviewReset);
		bReset.setActionKeyTip("W");
		bReset.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverview.bReset.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverview.bReset.richtip")));

		bCoordinates = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandOverview.bCoordinates"),
			RibbonController.getIcon("OVERVIEWCOORDINATES", 16));
		Actions.overviewCoordinates = new ActionToggleButtonModel(false);
		Actions.overviewCoordinates.addActionListener(this);
		Actions.overviewCoordinates.setSelected(!Prefs.guiHideOverviewPositions);
		bCoordinates.setActionModel(Actions.overviewCoordinates);
		bCoordinates.setActionKeyTip("W");
		bCoordinates.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandOverview.bCoordinates.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandOverview.bCoordinates.richtip")));

		// Mutually exclusive toggle group, means only one of the buttons can be
		// selected at once.
		group = new CommandToggleButtonGroup();
		group.add(bScaled);
		group.add(bCoverage);

		addCommandButton(bScaled, RibbonElementPriority.TOP);
		addCommandButton(bCoverage, RibbonElementPriority.TOP);
		addCommandButton(bSubset, RibbonElementPriority.MEDIUM);
		addCommandButton(bReset, RibbonElementPriority.MEDIUM);
		addCommandButton(bCoordinates, RibbonElementPriority.MEDIUM);

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == Actions.overviewSubset)
			actionShowSubsetDialog();

		else if(e.getSource() == Actions.overviewReset)
			actionReset();

		else if(e.getSource() == Actions.overviewCoordinates)
			actionToggleCoordinates();

		else if(e.getSource() == Actions.overviewScaled && Prefs.visOverviewType != OverviewCanvas.SCALEDDATA)
			actionToggleScaled();

		else if(e.getSource() == Actions.overviewCoverage && Prefs.visOverviewType != OverviewCanvas.COVERAGE)
			actionToggleCoverage();
	}

	// Change the overview to the scaled data view
	public void actionToggleScaled()
	{
		Prefs.visOverviewType = OverviewCanvas.SCALEDDATA;
		winMain.getAssemblyPanel().getOverviewCanvas().createImage();
		// This is needed for the right click menu which calls this method to update the ribbon
		Actions.overviewScaled.setSelected(Prefs.visOverviewType == OverviewCanvas.SCALEDDATA);
	}

	// Change the overview to the coverage view
	public void actionToggleCoverage()
	{
		Prefs.visOverviewType = OverviewCanvas.COVERAGE;
		winMain.getAssemblyPanel().getOverviewCanvas().createImage();
		// This is needed for the right click menu which calls this method to update the ribbon
		Actions.overviewCoverage.setSelected(Prefs.visOverviewType == OverviewCanvas.COVERAGE);
	}

	public void actionShowSubsetDialog()
	{
		winMain.getSubsetOverviewDialog();
	}

	public void actionReset()
	{
		winMain.getAssemblyPanel().getOverviewCanvas().resetOverview();
	}

	public void actionToggleCoordinates()
	{
		Prefs.guiHideOverviewPositions = !Prefs.guiHideOverviewPositions;
		winMain.getAssemblyPanel().getOverviewCanvas().repaint();
		// This is needed for the right click menu which calls this method to update the ribbon
		Actions.overviewCoordinates.setSelected(!Prefs.guiHideOverviewPositions);
	}
}