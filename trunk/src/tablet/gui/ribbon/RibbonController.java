package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;

public class RibbonController
{
	private static WinMain winMain;
	private static JRibbon ribbon;

	private static JLabel titleLabel = new JLabel(" ");

	public RibbonController(WinMain winMain)
	{
		this.winMain = winMain;
		this.ribbon = winMain.getRibbon();

		RibbonTask homeTask = new RibbonTask(
			RB.getString("gui.ribbon.RibbonController.home"),
			new HomeAssembliesBand(winMain),
			new HomeAdjustBand(winMain),
			new HomeOptionsBand(winMain));

		homeTask.setKeyTip("H");
		ribbon.addTask(homeTask);

		ribbon.setApplicationMenu(new ApplicationMenu(winMain));
		ribbon.setApplicationMenuKeyTip("F");

		ribbon.addTaskbarComponent(new JSeparator(JSeparator.VERTICAL));
		ribbon.addTaskbarComponent(titleLabel);

		ribbon.configureHelp(getIcon("HELP16", 16), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("help"); }
		});
	}

	public static void setTitleLabel(String str)
	{
		titleLabel.setText(" " + str);
		ribbon.repaint();
	}

	static ResizableIcon getIcon(String name, int size)
	{
		Image icon = Icons.getIcon(name).getImage();
		Dimension dimension = new Dimension(size, size);

		return ImageWrapperResizableIcon.getIcon(icon, dimension);
	}

	/** Associates a keyboard shortcut with a button on the ribbon. */
	static void assignShortcut(final AbstractCommandButton button, KeyStroke ks)
	{
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				button.doActionClick();
			}
		};

		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "action");
		button.getActionMap().put("action", action);
	}
}