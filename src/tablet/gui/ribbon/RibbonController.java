// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

public class RibbonController
{
	private static JRibbon ribbon;

	private static JLabel titleLabel = new JLabel(" ");
	private static JLabel memoryLabel = new JLabel(" ");

	public static BandAssemblies bandAssemblies;
	public static BandStyles bandStyles;
	public static BandAdjust bandAdjust;
	public static BandNavigate bandNavigate;
	public static BandOverlays bandOverlays;

	public static BandProtein bandProtein;
	public static BandBAM bandBAM;
	public static BandOptions bandOptions;

	public RibbonController(WinMain winMain)
	{
		RibbonController.ribbon = winMain.getRibbon();

		// The Home ribbon
		RibbonTask homeTask = new RibbonTask(
			RB.getString("gui.ribbon.RibbonController.home"),
			bandAssemblies = new BandAssemblies(winMain),
			bandStyles = new BandStyles(winMain),
			bandAdjust = new BandAdjust(winMain),
			bandNavigate = new BandNavigate(winMain),
			bandOverlays = new BandOverlays(winMain));

		homeTask.setKeyTip("H");
		ribbon.addTask(homeTask);


		// The Advanced ribbon
		RibbonTask advancedTask = new RibbonTask(
			RB.getString("gui.ribbon.RibbonController.advanced"),
			bandProtein = new BandProtein(winMain),
			bandBAM = new BandBAM(winMain),
			bandOptions = new BandOptions(winMain));

		advancedTask.setKeyTip("A");
		ribbon.addTask(advancedTask);


		// Add application menu
		ribbon.setApplicationMenu(new ApplicationMenu(winMain));
		ribbon.setApplicationMenuKeyTip("F");

		ribbon.addTaskbarComponent(new JSeparator(JSeparator.VERTICAL));
		ribbon.addTaskbarComponent(titleLabel);
		ribbon.addTaskbarComponent(new JSeparator(JSeparator.VERTICAL));
		ribbon.addTaskbarComponent(memoryLabel);

		ribbon.configureHelp(getIcon("HELP16", 16), new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				JButton bHelp = new JButton();
				TabletUtils.setHelp(bHelp, "index");
				bHelp.doClick();
			}
		});
	}

	public static void setTitleLabel(String str)
	{
		titleLabel.setText(" " + str);
		ribbon.repaint();
	}

	public static void setMemoryLabel(String str)
	{
		memoryLabel.setText(" " + str);
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