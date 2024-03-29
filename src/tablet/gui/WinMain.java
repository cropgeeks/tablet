// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
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
	private int splitterMin = 1;
	private JTabbedPane ctrlTabs;

	private AssemblyPanel assemblyPanel;
	private ContigsPanel contigsPanel;
	private FeaturesPanel featuresPanel;
	private FindPanel findPanel;
	private ReadsPanel readsPanel;
	private ReadGroupsPanel readGroupsPanel;

	private Assembly assembly;

	private JumpToDialog jumpToDialog;

	private ConsensusSubsequenceDialog consensusSubsequenceDialog;
	private RestrictionEnzymeDialog restrictionEnzymeDialog;

	WinMain()
	{
		long s = System.currentTimeMillis();
		new RibbonController(this);
		System.out.println("Ribbon UI created in " + (System.currentTimeMillis()-s) + "ms");

		createControls();

		ArrayList<Image> images = new ArrayList<>(2);
		images.add(Icons.getIcon("APPICON32").getImage());
		images.add(Icons.getIcon("APPICON16").getImage());
		setIconImages(images);

		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		ToolTipManager.sharedInstance().setDismissDelay(8000);


		// Determine where on screen to display
		SwingUtils.positionWindow(
			this, null, Prefs.guiWinMainX, Prefs.guiWinMainY);

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
		findPanel = new FindPanel(assemblyPanel, this);
		readsPanel = new ReadsPanel(assemblyPanel);
		readGroupsPanel = new ReadGroupsPanel();

		contigsPanel.setFeaturesPanel(featuresPanel);
		contigsPanel.setFindPanel(findPanel);
		contigsPanel.setReadsPanel(readsPanel);
		contigsPanel.setReadGroupsPanel(readGroupsPanel);
		assemblyPanel.setFindPanel(findPanel);

		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(this, dropAdapter));

		ctrlTabs.addTab("", contigsPanel);
		ctrlTabs.setIconAt(0, Icons.getIcon("CONTIGSTAB"));
		ctrlTabs.setToolTipTextAt(0, RB.getString("gui.WinMain.tabsContigs"));
		ctrlTabs.addTab("", featuresPanel);
		ctrlTabs.setIconAt(1, Icons.getIcon("FEATURESTAB"));
		ctrlTabs.setToolTipTextAt(1, RB.getString("gui.WinMain.tabsFeatures"));
		ctrlTabs.addTab("", readsPanel);
		ctrlTabs.setIconAt(2, Icons.getIcon("VISIBLEREADSTAB"));
		ctrlTabs.setToolTipTextAt(2, RB.getString("gui.WinMain.tabsVisibleReads"));
		ctrlTabs.addTab("", readGroupsPanel);
		ctrlTabs.setIconAt(3, Icons.getIcon("READGROUPSTAB"));
		ctrlTabs.setToolTipTextAt(3, RB.getString("gui.WinMain.tabsReadGroups"));
		ctrlTabs.addTab("", findPanel);
		ctrlTabs.setIconAt(4, Icons.getIcon("FIND"));
		ctrlTabs.setToolTipTextAt(4, RB.getString("gui.WinMain.tabsSearch"));

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.setBorder(BorderFactory.createEmptyBorder());
		splitter.setDividerLocation(Prefs.guiSplitterLocation);
		splitter.setOneTouchExpandable(true);
		splitter.setLeftComponent(ctrlTabs);
		splitter.setRightComponent(NavPanel.getLinksPanel(this));
		toggleSplitterLocation();

		if (SystemUtils.isMacOS())
			splitterMin = 2;

		splitter.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				Prefs.guiSplitterLocationPrev = Prefs.guiSplitterLocation;

				// If the splitter is NOT hidden...
				if (splitter.getDividerLocation() != splitterMin)
				{
					Prefs.guiSplitterLocation = splitter.getDividerLocation();
					Actions.optionsHideContigs.setSelected(false);
					Prefs.guiHideContigs = false;
				}
				// If the splitter IS hidden...
				else
				{
					Actions.optionsHideContigs.setSelected(true);
					Prefs.guiHideContigs = true;
				}
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

	public boolean okToExit(boolean isClose)
	{
		// If the user doesn't care, just allow it
		if (isClose == false && Prefs.guiWarnOnExit == false)
			return true;
		if (isClose && Prefs.guiWarnOnClose == false)
			return true;

		// If no assembly is loaded, it's fine too
		if (assembly == null)
			return true;

		// For all other situations, we need to prompt...
		String msg = null;
		JCheckBox checkbox = new JCheckBox();

		if (isClose)
		{
			msg = RB.getString("gui.WinMain.okToCloseMsg");
			RB.setText(checkbox, "gui.WinMain.warnOnClose");
		}
		else
		{
			msg = RB.getString("gui.WinMain.okToExitMsg");
			RB.setText(checkbox, "gui.WinMain.warnOnExit");
		}

		String[] options = new String[] {
			RB.getString("gui.text.yes"),
			RB.getString("gui.text.no") };

		int response = TaskDialog.show(msg, TaskDialog.QST, 1, checkbox, options);

		if (isClose)
			Prefs.guiWarnOnClose = !checkbox.isSelected();
		else
			Prefs.guiWarnOnExit = !checkbox.isSelected();

		return response == 0;
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

	public ContigsPanel getContigsPanel()
		{ return contigsPanel; }

	public FeaturesPanel getFeaturesPanel()
		{ return featuresPanel; }

	public ReadsPanel getReadsPanel()
		{ return readsPanel; }

	public ReadGroupsPanel getReadGroupsPanel()
		{ return readGroupsPanel; }

	public void focusOnFindPanel()
	{
		ctrlTabs.setSelectedIndex(4);
	}

	public JumpToDialog getJumpToDialog()
	{
		if (jumpToDialog == null)
			jumpToDialog = new JumpToDialog(this);

		return jumpToDialog;
	}

	public ConsensusSubsequenceDialog getConsensusSubsequenceDialog()
	{
		if (consensusSubsequenceDialog == null)
			consensusSubsequenceDialog = new ConsensusSubsequenceDialog(this);

		consensusSubsequenceDialog.updateModel(assemblyPanel.getContig());

		return consensusSubsequenceDialog;
	}

	public RestrictionEnzymeDialog getRestrictionEnzymeDialog()
	{
		if (restrictionEnzymeDialog == null)
			restrictionEnzymeDialog = new RestrictionEnzymeDialog();
		return restrictionEnzymeDialog;
	}

	public SubsetOverviewDialog getSubsetOverviewDialog()
	{
		return new SubsetOverviewDialog(this);
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
		else if (assembly != null)
			splitter.setRightComponent(NavPanel.getContigsPanel(assembly));
		else
			splitter.setRightComponent(NavPanel.getLinksPanel(this));

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

		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
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

	public void toggleSplitterLocation()
	{
		if (Prefs.guiHideContigs)
			splitter.setDividerLocation(splitterMin);
		else
			splitter.setDividerLocation(Prefs.guiSplitterLocationPrev);
	}

	public void validateCacheFolder()
	{
		new File(Prefs.cacheFolder).mkdirs();
		File test = new File(Prefs.cacheFolder, "Tablet-"+ SystemUtils.createGUID(24) + ".cache");
		test.deleteOnExit();

		try
		{
			if (test.createNewFile() == false)
				cacheInvalid(new IOException(
					RB.getString("gui.WinMain.cacheWriteError")));
		}
		catch (IOException e) {
			cacheInvalid(e);
		}

		test.delete();
	}

	private void cacheInvalid(IOException e)
	{
		String msg = RB.format("gui.WinMain.cacheError", Prefs.cacheFolder, e);

		TaskDialog.showOpenLog(msg, Tablet.getLogFile());
	}
}