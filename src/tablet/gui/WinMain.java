package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import javax.swing.*;

import tablet.data.*;
import tablet.io.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private Assembly assembly;

	private AssemblyPanel assemblyPanel;

	WinMain(String filename)
	{
		// Load in the data
		try
		{
			TestReader reader = new TestReader(filename);
			assembly = reader.getAssembly();

			long freeMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

			System.out.println("Memory used: " + nf.format(freeMem/1024f/1024f) + "MB\n");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		assemblyPanel = new AssemblyPanel(this);
		add(assemblyPanel);

		assemblyPanel.setAssembly(assembly);


		setTitle("Tablet");
//		setIconImage(Icons.getIcon("AV").getImage());

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
}