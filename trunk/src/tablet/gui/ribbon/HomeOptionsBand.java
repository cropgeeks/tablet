package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import tablet.gui.viewer.*;

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


		JCommandButtonStrip strip = new JCommandButtonStrip();
		strip.add(bInfoPane);
		strip.add(bHidePads);

		addFlowComponent(strip);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.homeOptionsInfoPane16)
			Prefs.visInfoPaneActive = !Prefs.visInfoPaneActive;

		else if (e.getSource() == Actions.homeOptionsHidePads16)
		{
			Prefs.visHideUnpaddedValues = !Prefs.visHideUnpaddedValues;
			winMain.repaint();
		}
	}
}