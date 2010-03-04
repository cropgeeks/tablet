// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import javax.swing.*;

import tablet.data.auxiliary.*;
import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

class HomeNavigateBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bPageLeft;
	private JCommandButton bPageRight;
	private JCommandButton bJumpTo;
	private JCommandButton bNextFeature;
	private JCommandButton bPrevFeature;

	HomeNavigateBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeNavigateBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));

		// Page left
		bPageLeft = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageLeft"),
			RibbonController.getIcon("NAVLBLU32", 32));
		Actions.homeNavigatePageLeft = new ActionRepeatableButtonModel(bPageLeft);
		Actions.homeNavigatePageLeft.addActionListener(this);
		bPageLeft.setActionModel(Actions.homeNavigatePageLeft);
		bPageLeft.setActionKeyTip("L");
		bPageLeft.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageLeft.tooltip"),
			RB.getString("gui.ribbon.HomeNavigateBand.bPageLeft.richtip")));

		// Page right
		bPageRight = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageRight"),
			RibbonController.getIcon("NAVRBLU32", 32));
		Actions.homeNavigatePageRight = new ActionRepeatableButtonModel(bPageRight);
		Actions.homeNavigatePageRight.addActionListener(this);
		bPageRight.setActionModel(Actions.homeNavigatePageRight);
		bPageRight.setActionKeyTip("R");
		bPageRight.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageRight.tooltip"),
			RB.getString("gui.ribbon.HomeNavigateBand.bPageRight.richtip")));

		// Jump to base...
		bJumpTo = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bJumpTo"),
			RibbonController.getIcon("JUMPTO32", 32));
		Actions.homeNavigateJumpTo = new ActionRepeatableButtonModel(bJumpTo);
		Actions.homeNavigateJumpTo.addActionListener(this);
		bJumpTo.setActionModel(Actions.homeNavigateJumpTo);
		bJumpTo.setActionKeyTip("J");
		bJumpTo.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeNavigateBand.bJumpTo.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.HomeNavigateBand.bJumpTo.richtip")));
		RibbonController.assignShortcut(bJumpTo,
			KeyStroke.getKeyStroke(KeyEvent.VK_J, Tablet.menuShortcut));

		// Next Feature...
		bNextFeature = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bNextFeature"),
			RibbonController.getIcon("NEXTFEATURE32", 32));
		Actions.homeNavigateNextFeature = new ActionRepeatableButtonModel(bNextFeature);
		Actions.homeNavigateNextFeature.addActionListener(this);
		bNextFeature.setActionModel(Actions.homeNavigateNextFeature);
		bNextFeature.setActionKeyTip(".");
		bNextFeature.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeNavigateBand.bNextFeature.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.HomeNavigateBand.bNextFeature.richtip")));
//		RibbonController.assignShortcut(bNextFeature,
//			KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, Tablet.menuShortcut));

		// Previous Feature...
		bPrevFeature = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bPrevFeature"),
			RibbonController.getIcon("PREVIOUSFEATURE32", 32));
		Actions.homeNavigatePrevFeature = new ActionRepeatableButtonModel(bPrevFeature);
		Actions.homeNavigatePrevFeature.addActionListener(this);
		bPrevFeature.setActionModel(Actions.homeNavigatePrevFeature);
		bPrevFeature.setActionKeyTip(",");
		bPrevFeature.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeNavigateBand.bPrevFeature.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.HomeNavigateBand.bPrevFeature.richtip")));
//		RibbonController.assignShortcut(bPrevFeature,
//			KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Tablet.menuShortcut));

		addCommandButton(bPageLeft, RibbonElementPriority.MEDIUM);
		addCommandButton(bPageRight, RibbonElementPriority.MEDIUM);
		addCommandButton(bJumpTo, RibbonElementPriority.MEDIUM);
		addCommandButton(bPrevFeature, RibbonElementPriority.MEDIUM);
		addCommandButton(bNextFeature, RibbonElementPriority.MEDIUM);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.homeNavigatePageLeft)
			winMain.getAssemblyPanel().pageLeft();

		else if (e.getSource() == Actions.homeNavigatePageRight)
			winMain.getAssemblyPanel().pageRight();

		else if (e.getSource() == Actions.homeNavigateJumpTo)
		{
			if (DisplayData.hasPaddedToUnpadded() == false &&
				DisplayData.hasUnpaddedToPadded() == false)
			{
				TaskDialog.info(
					RB.getString("gui.ribbon.HomeNavigateBand.jumpError"),
					RB.getString("gui.text.close"));
				return;
			}

			winMain.getJumpToDialog().setVisible(true);
		}

		else if(e.getSource() == Actions.homeNavigateNextFeature)
		{
			winMain.getFeaturesPanel().nextFeature();
		}

		else if(e.getSource() == Actions.homeNavigatePrevFeature)
		{
			winMain.getFeaturesPanel().prevFeature();
		}
	}
}