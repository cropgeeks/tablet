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
		assemblyPanel = new AssemblyPanel(this);
		contigPanel = new ContigPanel(assemblyPanel);

		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(assemblyPanel, dropAdapter));

		final JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.setDividerLocation(Prefs.guiSplitterLocation);
		splitter.setLeftComponent(contigPanel);
		splitter.setRightComponent(assemblyPanel);

		splitter.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
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
		assemblyPanel.setAssembly(assembly);
		contigPanel.setAssembly(assembly);

		String title = RB.getString("gui.WinMain.title");
		setTitle(assembly.getName() + " - " + title + " - " + Install4j.VERSION);
	}
}