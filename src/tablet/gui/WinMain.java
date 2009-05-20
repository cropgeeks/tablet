package tablet.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import tablet.data.*;
import tablet.io.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private Commands commands = new Commands(this);

	private JSplitPane splitter;
	private JTabbedPane ctrlTabs;

	private AssemblyPanel assemblyPanel;
	private ContigPanel contigPanel;

	private Assembly assembly;

	WinMain(String filename)
	{
		createControls();

		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
		setIconImage(Icons.getIcon("APPICON").getImage());
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
	}

	private void createControls()
	{
		ctrlTabs = new JTabbedPane();

		assemblyPanel = new AssemblyPanel(this);
		contigPanel = new ContigPanel(assemblyPanel, ctrlTabs);

		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(assemblyPanel, dropAdapter));

		ctrlTabs.addTab("", contigPanel);
		ctrlTabs.setTitleAt(0, contigPanel.getTitle(null));
		ctrlTabs.addTab("Features", new JPanel());

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
	}



	boolean okToExit()
	{
		return true;
	}

	void fileExit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}

	Commands getCommands()
		{ return commands; }

	void setAssembly(Assembly assembly)
	{
		int location = splitter.getDividerLocation();
		splitter.setRightComponent(assemblyPanel);
		splitter.setDividerLocation(location);

		assemblyPanel.setAssembly(assembly);
		contigPanel.setAssembly(assembly);

		String title = RB.getString("gui.WinMain.title");
		setTitle(assembly.getName() + " - " + title + " - " + Install4j.VERSION);
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
		}
	}
}