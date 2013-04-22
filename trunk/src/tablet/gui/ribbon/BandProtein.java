// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class BandProtein extends JFlowRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton[] bEnable;

	BandProtein(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandProtein.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		String[] states = Prefs.visProteins.split("\\s+");

		bEnable = new JCommandToggleButton[6];
		Actions.proteinEnable = new ActionToggleButtonModel[6];

		for (int i = 0; i < 6; i++)
		{
			Actions.proteinEnable[i] = new ActionToggleButtonModel(false);

			bEnable[i] = new JCommandToggleButton("",
				RibbonController.getIcon("HIDEPROTEINS" + i, 16));
			Actions.proteinEnable[i] = new ActionToggleButtonModel(false);
			Actions.proteinEnable[i].setSelected(states[i].equals("1"));
			Actions.proteinEnable[i].addActionListener(this);
			bEnable[i].setActionModel(Actions.proteinEnable[i]);
			bEnable[i].setActionRichTooltip(new RichTooltip(
				RB.getString("gui.ribbon.BandProtein.bHideProteins.tooltip"),
				RB.getString("gui.ribbon.BandProtein.bHideProteins.richtip")));
		}

		bEnable[0].setActionKeyTip("PA");
		bEnable[1].setActionKeyTip("PS");
		bEnable[2].setActionKeyTip("PD");
		bEnable[3].setActionKeyTip("PZ");
		bEnable[4].setActionKeyTip("PX");
		bEnable[5].setActionKeyTip("PC");

		JCommandButtonStrip strip1 = new JCommandButtonStrip();
		JCommandButtonStrip strip2 = new JCommandButtonStrip();

		for (int i = 0; i < 3; i++)
			strip1.add(bEnable[i]);
		for (int i = 3; i < 6; i++)
			strip2.add(bEnable[i]);

		addFlowComponent(strip1);
		addFlowComponent(strip2);
	}

	public void actionPerformed(ActionEvent e)
	{
		boolean[] states = new boolean[6];

		for (int i = 0; i < states.length; i++)
			states[i] = Actions.proteinEnable[i].isSelected();

		winMain.getAssemblyPanel().setProteinStates(states);
	}
}