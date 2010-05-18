// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.dialog.*;
import tablet.gui.viewer.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

public class BandBAM extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	public static JCommandButton bWindow, bPrevious;

	BandBAM(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandBAM.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Open an assembly (32x32 main button)
		bWindow = new JCommandButton(
			RB.getString("gui.ribbon.BandBAM.bWindow"),
			RibbonController.getIcon("BAMWINDOW32", 32));
		Actions.bamWindow = new ActionRepeatableButtonModel(bWindow);
		Actions.bamWindow.addActionListener(this);
		bWindow.setActionModel(Actions.bamWindow);
		bWindow.setActionKeyTip("W");
		bWindow.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandBAM.bWindow.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandBAM.bWindow.richtip")));

		// Import features
		bPrevious = new JCommandButton(
			RB.getString("gui.ribbon.BandBAM.bPrevious"),
			RibbonController.getIcon("BAMPREVIOUS32", 32));
		Actions.bamPrevious = new ActionRepeatableButtonModel(bPrevious);
		Actions.bamPrevious.addActionListener(this);
		bPrevious.setActionModel(Actions.bamPrevious);
		bPrevious.setActionKeyTip("F");
		bPrevious.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandBAM.bPrevious.tooltip"),
			RB.getString("gui.ribbon.BandBAM.bPrevious.richtip")));


		addCommandButton(bWindow, RibbonElementPriority.TOP);
		addCommandButton(bPrevious, RibbonElementPriority.TOP);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.bamWindow)
		{
			BamWindowDialog dialog = new BamWindowDialog(winMain);

			if (dialog.isOK())
			{
				reloadBam();

				Tablet.winMain.getAssemblyPanel().forceRedraw();
			}
		}

		else if (e.getSource() == Actions.bamPrevious)
			Tablet.winMain.getAssemblyPanel().bamPrevious();
	}

	// TODO: I don't like this: it's messy and I don't trust it
	private void reloadBam()
	{
		AssemblyPanel aPanel = Tablet.winMain.getAssemblyPanel();
		Assembly assembly = aPanel.getAssembly();
		Contig contig = aPanel.getContig();

		if (contig != null)
		{
			BamBam bambam = assembly.getBamBam();

			if (bambam != null)
			{
				int s = bambam.getS();
				bambam.reset(Prefs.bamSize);
				bambam.setBlockStart(contig, s);

				aPanel.processBamDataChange();
			}
		}
	}
}