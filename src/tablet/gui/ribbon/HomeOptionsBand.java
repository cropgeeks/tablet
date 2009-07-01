package tablet.gui.ribbon;

import java.awt.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class HomeOptionsBand extends JFlowRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bInfoPane;
	private JCommandToggleButton bHidePads;

	private JCommandToggleButton bHideOverview;
	private JCommandToggleButton bHideConsensus;
	private JCommandButton bHideProteins;
	private JCommandToggleButton bHideScaleBar;
	private JCommandToggleButton bHideContigs;


	HomeOptionsBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeOptionsBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Toggle the infoPane tooltips on or off
		bInfoPane = new JCommandToggleButton("",
			RibbonController.getIcon("INFOPANE16", 16));
		Actions.homeOptionsInfoPane16 = new ActionToggleButtonModel(false);
		Actions.homeOptionsInfoPane16.setSelected(Prefs.visInfoPaneActive);
		Actions.homeOptionsInfoPane16.addActionListener(this);
		bInfoPane.setActionModel(Actions.homeOptionsInfoPane16);
		bInfoPane.setActionKeyTip("I");
		bInfoPane.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bInfoPane.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bInfoPane.richtip")));

		// Toggle the display of unpadded scores/values on or off
		bHidePads = new JCommandToggleButton("",
			RibbonController.getIcon("PADDED16", 16));
		Actions.homeOptionsHidePads16 = new ActionToggleButtonModel(false);
		Actions.homeOptionsHidePads16.setSelected(Prefs.visHideUnpaddedValues);
		Actions.homeOptionsHidePads16.addActionListener(this);
		bHidePads.setActionModel(Actions.homeOptionsHidePads16);
		bHidePads.setActionKeyTip("U");
		bHidePads.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHidePads.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHidePads.richtip")));


		JCommandButtonStrip optionsStrip = new JCommandButtonStrip();
		optionsStrip.add(bInfoPane);
		optionsStrip.add(bHidePads);
		addFlowComponent(optionsStrip);


		// Hide the overview panel
		bHideOverview = new JCommandToggleButton("",
			RibbonController.getIcon("HIDEOVERVIEW16", 16));
		Actions.homeOptionsHideOverview = new ActionToggleButtonModel(false);
		Actions.homeOptionsHideOverview.setSelected(Prefs.guiHideOverview);
		Actions.homeOptionsHideOverview.addActionListener(this);
		bHideOverview.setActionModel(Actions.homeOptionsHideOverview);
		bHideOverview.setActionKeyTip("HO");
		bHideOverview.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHideOverview.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHideOverview.richtip")));

		// Hide the consensus canvas
		bHideConsensus = new JCommandToggleButton("",
			RibbonController.getIcon("HIDECONSENSUS16", 16));
		Actions.homeOptionsHideConsensus = new ActionToggleButtonModel(false);
		Actions.homeOptionsHideConsensus.setSelected(Prefs.guiHideConsensus);
		Actions.homeOptionsHideConsensus.addActionListener(this);
		bHideConsensus.setActionModel(Actions.homeOptionsHideConsensus);
		bHideConsensus.setActionKeyTip("HC");
		bHideConsensus.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHideConsensus.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHideConsensus.richtip")));

		// Hide the scale bar
		bHideScaleBar = new JCommandToggleButton("",
			RibbonController.getIcon("HIDESCALEBAR16", 16));
		Actions.homeOptionsHideScaleBar = new ActionToggleButtonModel(false);
		Actions.homeOptionsHideScaleBar.setSelected(Prefs.guiHideScaleBar);
		Actions.homeOptionsHideScaleBar.addActionListener(this);
		bHideScaleBar.setActionModel(Actions.homeOptionsHideScaleBar);
		bHideScaleBar.setActionKeyTip("HS");
		bHideScaleBar.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHideScaleBar.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHideScaleBar.richtip")));

		// Hide the contigs panel
		bHideContigs = new JCommandToggleButton("",
			RibbonController.getIcon("HIDECONTIGS16", 16));
		Actions.homeOptionsHideContigs = new ActionToggleButtonModel(false);
		Actions.homeOptionsHideContigs.setSelected(Prefs.guiHideContigs);
		Actions.homeOptionsHideContigs.addActionListener(this);
		bHideContigs.setActionModel(Actions.homeOptionsHideContigs);
		bHideContigs.setActionKeyTip("HT");
		bHideContigs.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHideContigs.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHideContigs.richtip")));

		// Open the proteins option menu
		bHideProteins = new JCommandButton("",
			RibbonController.getIcon("HIDEPROTEINS16", 16));
		Actions.homeOptionsHideProteins = new ActionRepeatableButtonModel(bHideProteins);
		Actions.homeOptionsHideProteins.addActionListener(this);
		bHideProteins.setActionModel(Actions.homeOptionsHideProteins);
		bHideProteins.setActionKeyTip("HP");
		bHideProteins.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeOptionsBand.bHideProteins.tooltip"),
			RB.getString("gui.ribbon.HomeOptionsBand.bHideProteins.richtip")));


		JCommandButtonStrip panelsStrip = new JCommandButtonStrip();
		panelsStrip.add(bHideOverview);
		panelsStrip.add(bHideProteins);
		panelsStrip.add(bHideConsensus);
		panelsStrip.add(bHideScaleBar);
		panelsStrip.add(bHideContigs);
		addFlowComponent(panelsStrip);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		// Primary options
		if (source == Actions.homeOptionsInfoPane16)
			Prefs.visInfoPaneActive = !Prefs.visInfoPaneActive;

		else if (source == Actions.homeOptionsHidePads16)
		{
			Prefs.visHideUnpaddedValues = !Prefs.visHideUnpaddedValues;
			winMain.repaint();
		}

		// Panel controls
		else if (source == Actions.homeOptionsHideOverview)
		{
			Prefs.guiHideOverview = !Prefs.guiHideOverview;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.homeOptionsHideProteins)
		{
			winMain.getAssemblyPanel().displayProteinOptions(bHideProteins);
		}
		else if (source == Actions.homeOptionsHideConsensus)
		{
			Prefs.guiHideConsensus = !Prefs.guiHideConsensus;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.homeOptionsHideScaleBar)
		{
			Prefs.guiHideScaleBar = !Prefs.guiHideScaleBar;
			winMain.getAssemblyPanel().setVisibilities();
		}
		else if (source == Actions.homeOptionsHideContigs)
		{
		}
	}
}