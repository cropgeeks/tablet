// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.lang.management.*;
import java.text.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.dialog.*;
import tablet.gui.ribbon.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

import org.jvnet.flamingo.ribbon.JRibbonFrame;

public class WinMain extends JRibbonFrame
{
	private Commands commands = new Commands(this);

	private JSplitPane splitter;
	private JTabbedPane ctrlTabs;

	private AssemblyPanel assemblyPanel;
	private ContigsPanel contigsPanel;
	private FeaturesPanel featuresPanel;

	private Assembly assembly;

	private JumpToDialog jumpToDialog;

	WinMain()
	{
		createControls();

		long s = System.currentTimeMillis();
		new RibbonController(this);
		System.out.println("Ribbon UI created in " + (System.currentTimeMillis()-s) + "ms");

		ArrayList<Image> images = new ArrayList<Image>(2);
		images.add(Icons.getIcon("APPICON64").getImage());
		images.add(Icons.getIcon("APPICON22").getImage());
		setIconImages(images);

		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.isFirstRun || Prefs.guiWinMainX > (scrnW-50) || Prefs.guiWinMainY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		addListeners();

		Actions.closed();
	}

	private void createControls()
	{
		ctrlTabs = new JTabbedPane();

		assemblyPanel = new AssemblyPanel(this);
		contigsPanel = new ContigsPanel(this, assemblyPanel, ctrlTabs);
		featuresPanel = new FeaturesPanel(assemblyPanel, ctrlTabs);

		contigsPanel.setFeaturesPanel(featuresPanel);

		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(this, dropAdapter));

		ctrlTabs.addTab("", contigsPanel);
		ctrlTabs.setTitleAt(0, contigsPanel.getTitle(0));
		ctrlTabs.addTab("", featuresPanel);
		ctrlTabs.setTitleAt(1, featuresPanel.getTitle(0));
		ctrlTabs.setEnabledAt(1, false);

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.setDividerLocation(Prefs.guiSplitterLocation);
		splitter.setOneTouchExpandable(true);
		splitter.setLeftComponent(ctrlTabs);
		splitter.setRightComponent(new NavPanel(this));

		splitter.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				if (splitter.getDividerLocation() != 1)
					Prefs.guiSplitterLocation = splitter.getDividerLocation();
			}
		});

		add(splitter);
		add(new WinMainStatusBar(), BorderLayout.SOUTH);
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});

		createMemoryTimer();
	}

	public boolean okToExit()
	{
		return true;
	}

	public void exit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}

	public Commands getCommands()
		{ return commands; }

	public AssemblyPanel getAssemblyPanel()
		{ return assemblyPanel; }

	ContigsPanel getContigsPanel()
		{ return contigsPanel; }

	public JumpToDialog getJumpToDialog()
	{
		if (jumpToDialog == null)
			jumpToDialog = new JumpToDialog(this);

		return jumpToDialog;
	}

	void setAssembly(Assembly assembly)
	{
		this.assembly = assembly;

		assemblyPanel.setAssembly(assembly);
		contigsPanel.setAssembly(assembly);

		String title = RB.getString("gui.WinMain.title");
		setTitle(assembly.getName() + " - " + title + " - " + Install4j.VERSION);

		Actions.openedNoContigSelected();
	}

	void setAssemblyPanelVisible(boolean isVisible)
	{
		int location = splitter.getDividerLocation();

		if (isVisible)
			splitter.setRightComponent(assemblyPanel);
		else
			splitter.setRightComponent(new NavPanel(this));

		splitter.setDividerLocation(location);
	}

	// Closes the current assembly and the cache associated with it
	public void closeAssembly()
	{
		try {
			if (assembly != null)
				assembly.getCache().close();
		}
		catch (Exception e) {}

		assembly = null;

		contigsPanel.setAssembly(null);
		assemblyPanel.setAssembly(null);

		Actions.closed();
	}

	// Creates and registers a timer for monitoring memory usage
	private void createMemoryTimer()
	{
		final DecimalFormat df = new DecimalFormat("0.00");
		final MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();
		final ThreadMXBean tBean = ManagementFactory.getThreadMXBean();

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				long used = mBean.getHeapMemoryUsage().getUsed()
					+ mBean.getNonHeapMemoryUsage().getUsed();

				String label = RB.format("gui.WinMain.memory",
					df.format(used/1024f/1024f),
					tBean.getThreadCount()-tBean.getDaemonThreadCount());
				RibbonController.setMemoryLabel(label);
			}
		};

		javax.swing.Timer timer = new javax.swing.Timer(2500, listener);
		timer.setInitialDelay(0);
		timer.start();
	}
}