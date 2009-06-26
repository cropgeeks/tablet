package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

class HomeNavigateBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bPageLeft, bPageRight;

	HomeNavigateBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeNavigateBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Page left
		bPageLeft = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageLeft"),
			RibbonController.getIcon("NAVLEFT32", 32));
		Actions.homeNavigatePageLeft = new ActionRepeatableButtonModel(bPageLeft);
		Actions.homeNavigatePageLeft.addActionListener(this);
		bPageLeft.setActionModel(Actions.homeNavigatePageLeft);
		bPageLeft.setActionKeyTip("L");
		bPageLeft.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeNavigateBand.bPageLeft.tooltip"),
			RB.getString("gui.ribbon.HomeNavigateBand.bPageLeft.richtip")));

		// Page right
		bPageRight = new JCommandButton(
			RB.getString("gui.ribbon.HomeNavigateBand.bPageRight"),
			RibbonController.getIcon("NAVRIGHT32", 32));
		Actions.homeNavigatePageRight = new ActionRepeatableButtonModel(bPageRight);
		Actions.homeNavigatePageRight.addActionListener(this);
		bPageRight.setActionModel(Actions.homeNavigatePageRight);
		bPageRight.setActionKeyTip("R");
		bPageRight.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeNavigateBand.bPageRight.tooltip"),
			RB.getString("gui.ribbon.HomeNavigateBand.bPageRight.richtip")));


		addCommandButton(bPageLeft, RibbonElementPriority.LOW);
		addCommandButton(bPageRight, RibbonElementPriority.LOW);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.homeNavigatePageLeft)
			winMain.getAssemblyPanel().pageLeft();

		else if (e.getSource() == Actions.homeNavigatePageRight)
			winMain.getAssemblyPanel().pageRight();
	}
}