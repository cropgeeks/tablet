// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
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

class BandNavigate extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bPageLeft;
	private JCommandButton bPageRight;
	private JCommandButton bJumpTo;
	private JCommandButton bNextFeature;
	private JCommandButton bPrevFeature;

	BandNavigate(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandNavigate.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));

		// Page left
		bPageLeft = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bPageLeft"),
			RibbonController.getIcon("NAVLBLU32", 32));
		Actions.navigatePageLeft = new ActionRepeatableButtonModel(bPageLeft);
		Actions.navigatePageLeft.addActionListener(this);
		bPageLeft.setActionModel(Actions.navigatePageLeft);
		bPageLeft.setActionKeyTip("L");
		bPageLeft.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandNavigate.bPageLeft.tooltip"),
			RB.getString("gui.ribbon.BandNavigate.bPageLeft.richtip")));

		// Page right
		bPageRight = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bPageRight"),
			RibbonController.getIcon("NAVRBLU32", 32));
		Actions.navigatePageRight = new ActionRepeatableButtonModel(bPageRight);
		Actions.navigatePageRight.addActionListener(this);
		bPageRight.setActionModel(Actions.navigatePageRight);
		bPageRight.setActionKeyTip("R");
		bPageRight.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandNavigate.bPageRight.tooltip"),
			RB.getString("gui.ribbon.BandNavigate.bPageRight.richtip")));

		// Jump to base...
		bJumpTo = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bJumpTo"),
			RibbonController.getIcon("JUMPTO32", 32));
		Actions.navigateJumpTo = new ActionRepeatableButtonModel(bJumpTo);
		Actions.navigateJumpTo.addActionListener(this);
		bJumpTo.setActionModel(Actions.navigateJumpTo);
		bJumpTo.setActionKeyTip("J");
		bJumpTo.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandNavigate.bJumpTo.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandNavigate.bJumpTo.richtip")));
		RibbonController.assignShortcut(bJumpTo,
			KeyStroke.getKeyStroke(KeyEvent.VK_J, Tablet.menuShortcut));

		// Next Feature...
		bNextFeature = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bNextFeature"),
			RibbonController.getIcon("NEXTFEATURE32", 32));
		Actions.navigateNextFeature = new ActionRepeatableButtonModel(bNextFeature);
		Actions.navigateNextFeature.addActionListener(this);
		bNextFeature.setActionModel(Actions.navigateNextFeature);
		bNextFeature.setActionKeyTip(".");
		bNextFeature.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandNavigate.bNextFeature.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandNavigate.bNextFeature.richtip")));
//		RibbonController.assignShortcut(bNextFeature,
//			KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, Tablet.menuShortcut));

		// Previous Feature...
		bPrevFeature = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bPrevFeature"),
			RibbonController.getIcon("PREVIOUSFEATURE32", 32));
		Actions.navigatePrevFeature = new ActionRepeatableButtonModel(bPrevFeature);
		Actions.navigatePrevFeature.addActionListener(this);
		bPrevFeature.setActionModel(Actions.navigatePrevFeature);
		bPrevFeature.setActionKeyTip(",");
		bPrevFeature.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandNavigate.bPrevFeature.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandNavigate.bPrevFeature.richtip")));
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
		if (e.getSource() == Actions.navigatePageLeft)
			winMain.getAssemblyPanel().pageLeft();

		else if (e.getSource() == Actions.navigatePageRight)
			winMain.getAssemblyPanel().pageRight();

		else if (e.getSource() == Actions.navigateJumpTo)
		{
			if (DisplayData.hasPaddedToUnpadded() == false &&
				DisplayData.hasUnpaddedToPadded() == false)
			{
				TaskDialog.info(
					RB.getString("gui.ribbon.BandNavigate.jumpError"),
					RB.getString("gui.text.close"));
				return;
			}

			winMain.getJumpToDialog().setVisible(true);
		}

		else if(e.getSource() == Actions.navigateNextFeature)
		{
			winMain.getFeaturesPanel().nextFeature();
		}

		else if(e.getSource() == Actions.navigatePrevFeature)
		{
			winMain.getFeaturesPanel().prevFeature();
		}
	}
}