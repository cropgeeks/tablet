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
		setDropTarget(new DropTarget(assemblyPanel, dropAdapter));

		ctrlTabs.addTab("", contigsPanel);
		ctrlTabs.setTitleAt(0, contigsPanel.getTitle());
		ctrlTabs.addTab("", featuresPanel);
		ctrlTabs.setTitleAt(1, featuresPanel.getTitle());
		ctrlTabs.setEnabledAt(1, false);

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.setDividerLocation(Prefs.guiSplitterLocation);
		splitter.setOneTouchExpandable(true);
		splitter.setLeftComponent(ctrlTabs);
		splitter.setRightComponent(new LogoPanel(new BorderLayout()));

		splitter.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				if (splitter.getDividerLocation() != 1)
					Prefs.guiSplitterLocation = splitter.getDividerLocation();
			}
		});

		add(splitter);
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
			splitter.setRightComponent(new LogoPanel(new BorderLayout()));

		splitter.setDividerLocation(location);
	}

	// Closes the current assembly and the cache associated with it
	public void closeAssembly()
	{
		// TODO: What else can be closed/set to null/etc to save memory?
		// The assemblyPanel will still have a reference to any data just now

		try
		{
			if (assembly != null)
				assembly.getCache().close();
		}
		catch (Exception e) { e.printStackTrace(); }

		contigsPanel.setAssembly(null);
		Actions.closed();
	}

	// Creates and registers a timer for monitoring memory usage
	private void createMemoryTimer()
	{
		final DecimalFormat df = new DecimalFormat("0.00");
		final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();

		// TODO: Is this reporting correctly?
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				long used = bean.getHeapMemoryUsage().getUsed()
					+ bean.getNonHeapMemoryUsage().getUsed();

				String label = RB.format("gui.WinMain.memory",
					df.format(used/1024f/1024f));
				RibbonController.setMemoryLabel(label);
			}
		};

		javax.swing.Timer timer = new javax.swing.Timer(10000, listener);
		timer.setInitialDelay(0);
		timer.start();
	}

	private static class LogoPanel extends JPanel
	{
		private static ImageIcon logo = Icons.getIcon("SCRILARGE");

		LogoPanel(LayoutManager lm)
		{
			super(lm);
			setBackground(Color.white);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			int w = getWidth();
			int h = getHeight();

			g.drawImage(logo.getImage(), 0, 0, w, w, null);

			String str = "Please don't distribute Tablet outside of SCRI";
			int strWidth = g.getFontMetrics().stringWidth(str);

			g.setColor(Color.lightGray);
			g.setFont(new Font("Dialog", Font.BOLD, 14));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(str, w/2-(strWidth/2), getHeight()/2);
		}
	}
}