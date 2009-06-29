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

class HomeAssembliesBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bOpen16, bOpen32;

	HomeAssembliesBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeAssembliesBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Open an assembly (32x32 main button)
		bOpen32 = new JCommandButton(
			RB.getString("gui.ribbon.HomeAssembliesBand.bOpen"),
			RibbonController.getIcon("FILEOPEN32", 32));
		Actions.homeAssembliesOpen32 = new ActionRepeatableButtonModel(bOpen32);
		Actions.homeAssembliesOpen32.addActionListener(this);
		bOpen32.setActionModel(Actions.homeAssembliesOpen32);
		bOpen32.setActionKeyTip("O");
		bOpen32.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeAssembliesBand.bOpen.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.HomeAssembliesBand.bOpen.richtip")));
		RibbonController.assignShortcut(bOpen32,
			KeyStroke.getKeyStroke(KeyEvent.VK_O, Tablet.menuShortcut));


		// Open an assembly (16x16 shortcut button)
		bOpen16 = new JCommandButton("",
			RibbonController.getIcon("FILEOPEN16", 16));
		Actions.homeAssembliesOpen16 = new ActionRepeatableButtonModel(bOpen16);
		Actions.homeAssembliesOpen16.addActionListener(this);
		bOpen16.setActionModel(Actions.homeAssembliesOpen16);
		bOpen16.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeAssembliesBand.bOpen.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.HomeAssembliesBand.bOpen.richtip")));
		bOpen16.setActionKeyTip("1");

		addCommandButton(bOpen32, RibbonElementPriority.TOP);
//		startGroup();

		winMain.getRibbon().addTaskbarComponent(bOpen16);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.homeAssembliesOpen32 ||
			e.getSource() == Actions.homeAssembliesOpen16)
		{
			winMain.getCommands().fileOpen(null);
		}
	}
}