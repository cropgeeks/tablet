package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

class HomeVisualizationBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bOpen;

	HomeVisualizationBand(WinMain winMain)
	{
		super("Visualization", new EmptyResizableIcon(32));

		this.winMain = winMain;

		JSlider slider1 = new JSlider();
		addRibbonComponent(new JRibbonComponent(RibbonController.getIcon("ZOOM16", 16), "Slider1:", slider1));
	}

	public void actionPerformed(ActionEvent e)
	{
	}
}