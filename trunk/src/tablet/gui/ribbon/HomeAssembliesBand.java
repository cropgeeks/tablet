package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

class HomeAssembliesBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bOpen16, bOpen32;

	HomeAssembliesBand(WinMain winMain)
	{
		super("Assemblies", new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Open an assembly (32x32 main button)
		bOpen32 = new JCommandButton("Open Assembly", RibbonController.getIcon("FILEOPEN32", 32));
		Actions.homeAssembliesOpen32 = new ActionRepeatableButtonModel(bOpen32);
		Actions.homeAssembliesOpen32.addActionListener(this);
		bOpen32.setActionModel(Actions.homeAssembliesOpen32);
		bOpen32.setActionKeyTip("O");

		// Open an assembly (16x16 shortcut button)
		bOpen16 = new JCommandButton("Open Assembly", RibbonController.getIcon("FILEOPEN16", 16));
		Actions.homeAssembliesOpen16 = new ActionRepeatableButtonModel(bOpen16);
		Actions.homeAssembliesOpen16.addActionListener(this);
		bOpen16.setActionModel(Actions.homeAssembliesOpen16);
		bOpen16.setActionKeyTip("1");

		addCommandButton(bOpen32, RibbonElementPriority.TOP);
//		startGroup();
//		addCommandButton(bTest, RibbonElementPriority.TOP);

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