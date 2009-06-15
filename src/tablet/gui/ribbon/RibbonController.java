package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;

public class RibbonController
{
	private static WinMain winMain;

	private static JLabel titleLabel = new JLabel("");

	public RibbonController(WinMain winMain)
	{
		this.winMain = winMain;

		RibbonTask homeTask = new RibbonTask("Home",
			new HomeAssembliesBand(winMain),
			new HomeVisualizationBand(winMain));

		winMain.getRibbon().addTask(homeTask);

		winMain.getRibbon().addTaskbarComponent(new JSeparator(JSeparator.VERTICAL));
		winMain.getRibbon().addTaskbarComponent(titleLabel);
//		winMain.getRibbon().setBorder(BorderFactory.createEmptyBorder(-24, 0, 2, 0));

		winMain.getRibbon().setApplicationMenu(new ApplicationMenu(winMain));

		winMain.getRibbon().configureHelp(getIcon("HELP16", 16), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("help"); }
		});
	}

	public static void setTitleLabel(String str)
	{
		titleLabel.setText(" " + str);
		winMain.getRibbon().repaint();
	}

	static ResizableIcon getIcon(String name, int size)
	{
		Image icon = Icons.getIcon(name).getImage();
		Dimension dimension = new Dimension(size, size);

		return ImageWrapperResizableIcon.getIcon(icon, dimension);
	}
}