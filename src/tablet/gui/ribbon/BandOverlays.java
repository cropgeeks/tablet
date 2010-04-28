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

class BandOverlays extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bInfoPane;
	private JCommandToggleButton bEnableText;
	private JCommandToggleButton bReadNames;

	private JCommandButton bPageLeft;

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
		RibbonController.assignShortcut(bReadNames,
			KeyStroke.getKeyStroke(KeyEvent.VK_N, Tablet.menuShortcut));


		// Page left
/*		bPageLeft = new JCommandButton(
			RB.getString("gui.ribbon.BandNavigate.bPageLeft"),
			RibbonController.getIcon("NAVLBLU32", 32));
		Actions.navigatePageLeft = new ActionRepeatableButtonModel(bPageLeft);
		Actions.navigatePageLeft.addActionListener(this);
		bPageLeft.setActionModel(Actions.navigatePageLeft);
		bPageLeft.setActionKeyTip("L");
		bPageLeft.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandNavigate.bPageLeft.tooltip"),
			RB.getString("gui.ribbon.BandNavigate.bPageLeft.richtip")));
*/

		addCommandButton(bInfoPane, RibbonElementPriority.MEDIUM);
		addCommandButton(bEnableText, RibbonElementPriority.MEDIUM);
		addCommandButton(bReadNames, RibbonElementPriority.MEDIUM);

//		addCommandButton(bPageLeft, RibbonElementPriority.MEDIUM);
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
		{
			Prefs.visOverlayNames = !Prefs.visOverlayNames;
			winMain.getAssemblyPanel().toggleNameOverlay();
		}
	}
}