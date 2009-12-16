// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import static tablet.gui.viewer.OverviewCanvas.*;

import scri.commons.gui.*;

/** A mouse listener for all interaction with the overview canvas. */
class OverviewCanvasML extends MouseInputAdapter implements ActionListener
{
	private OverviewCanvas canvas;

	// Popup menu options
	private JCheckBoxMenuItem mHide;
	private JCheckBoxMenuItem mScaled, mCoverage;
	private JMenuItem mReset;

	OverviewCanvasML(OverviewCanvas canvas, JComponent c)
	{
		this.canvas = canvas;

		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);

		if(e.isControlDown() || e.isMetaDown() && canvas.dragging)
			canvas.drawSubset(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);

		if (e.isPopupTrigger())
			displayMenu(null, e);

		if(e.isControlDown() || e.isMetaDown())
		{
			canvas.visualS = e.getX();
			canvas.dragging = true;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		canvas.dragging = false;
		
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);

		if (e.isPopupTrigger())
			displayMenu(null, e);

		if((e.isControlDown() || e.isMetaDown() )&& !e.isPopupTrigger())
		{
			canvas.setSubset(e);
		}
	}

	// Create and display the popup menu
	void displayMenu(JComponent button, MouseEvent e)
	{
		mHide = new JCheckBoxMenuItem();
		RB.setText(mHide, "gui.viewer.OverviewCanvas.mHide");
		mHide.setSelected(!Prefs.guiHideOverview);
		mHide.addActionListener(this);

		mScaled = new JCheckBoxMenuItem();
		RB.setText(mScaled, "gui.viewer.OverviewCanvas.mScaled");
		mScaled.setSelected(Prefs.visOverviewType == SCALEDDATA);
		mScaled.addActionListener(this);

		mCoverage = new JCheckBoxMenuItem();
		RB.setText(mCoverage, "gui.viewer.OverviewCanvas.mCoverage");
		mCoverage.setSelected(Prefs.visOverviewType == COVERAGE);
		mCoverage.addActionListener(this);

		mReset = new JMenuItem();
		RB.setText(mReset, "gui.viewer.OverviewCanvas.mReset");
		mReset.addActionListener(this);

		JPopupMenu menu = new JPopupMenu();
		menu.add(mHide);
		menu.addSeparator();
		menu.add(mScaled);
		menu.add(mCoverage);
		menu.addSeparator();
		menu.add(mReset);

		if (button != null)
		{
			int x = button.getX();// - button.getWidth();
			int y = button.getY() + button.getHeight();
			menu.show(button, x, y);
		}
		else
			menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		// Show or hide the overview canvas
		if (e.getSource() == mHide)
		{
			Prefs.guiHideOverview = !Prefs.guiHideOverview;
			Tablet.winMain.getAssemblyPanel().setVisibilities();
		}

		// Switch overview rendering to the scaled overview
		else if (e.getSource() == mScaled && Prefs.visOverviewType != SCALEDDATA)
		{
			Prefs.visOverviewType = SCALEDDATA;
			canvas.createImage();
		}

		// Switch overvie rendering to the coverage overview
		else if (e.getSource() == mCoverage && Prefs.visOverviewType != COVERAGE)
		{
			Prefs.visOverviewType = COVERAGE;
			canvas.createImage();
		}

		//reset overview so that it displays its original information
		else if (e.getSource() == mReset)
		{
			canvas.getAssemblyPanel().getContig().resetVisualContig();
			canvas.getAssemblyPanel().setContig(canvas.getAssemblyPanel().getContig());
		}
	}
}