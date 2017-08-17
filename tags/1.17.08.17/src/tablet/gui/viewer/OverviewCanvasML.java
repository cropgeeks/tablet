// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import static tablet.gui.viewer.OverviewCanvas.*;
import static tablet.gui.ribbon.RibbonController.*;

import scri.commons.gui.*;

/** A mouse listener for all interaction with the overview canvas. */
class OverviewCanvasML extends MouseInputAdapter implements ActionListener
{
	private OverviewCanvas canvas;

	// Popup menu options
	private JCheckBoxMenuItem mHide, mHideCoordinates;
	private JCheckBoxMenuItem mScaled, mCoverage;
	private JMenuItem mSubset, mReset;

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

		if(isMetaClick(e) && canvas.dragging)
			canvas.updateSubsetVariables(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);

		if (e.isPopupTrigger())
		{
			displayMenu(null, e);
			canvas.dragging = false;
		}
		else if(isMetaClick(e))
		{
			canvas.tempOS = e.getX();
			canvas.tempOE = e.getX();
			canvas.dragging = true;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.processMouse(e);

		if (e.isPopupTrigger())
			displayMenu(null, e);
		else if(isMetaClick(e) && canvas.dragging)
		{
			canvas.setSubset(e);
		}

		canvas.dragging = false;
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return canvas.isOSX && e.isMetaDown() || !canvas.isOSX && e.isControlDown();
	}

	// Create and display the popup menu
	void displayMenu(JComponent button, MouseEvent e)
	{
		mHide = new JCheckBoxMenuItem();
		RB.setText(mHide, "gui.viewer.OverviewCanvas.mHide");
		mHide.setSelected(!Prefs.guiHideOverview);
		mHide.addActionListener(this);

		mHideCoordinates = new JCheckBoxMenuItem();
		RB.setText(mHideCoordinates, "gui.viewer.OverviewCanvas.mHideCoordinates");
		mHideCoordinates.setSelected(!Prefs.guiHideOverviewPositions);
		mHideCoordinates.addActionListener(this);

		mScaled = new JCheckBoxMenuItem();
		RB.setText(mScaled, "gui.viewer.OverviewCanvas.mScaled");
		mScaled.setSelected(Prefs.visOverviewType == SCALEDDATA);
		mScaled.addActionListener(this);

		mCoverage = new JCheckBoxMenuItem();
		RB.setText(mCoverage, "gui.viewer.OverviewCanvas.mCoverage");
		mCoverage.setSelected(Prefs.visOverviewType == COVERAGE);
		mCoverage.addActionListener(this);

		mSubset = new JMenuItem();
		RB.setText(mSubset, "gui.viewer.OverviewCanvas.mSubset");
		mSubset.addActionListener(this);

		mReset = new JMenuItem();
		RB.setText(mReset, "gui.viewer.OverviewCanvas.mReset");
		mReset.setEnabled(Actions.overviewReset.isEnabled());
		mReset.addActionListener(this);

		JPopupMenu menu = new JPopupMenu();
		menu.add(mHide);
		menu.addSeparator();
		menu.add(mHideCoordinates);
		menu.addSeparator();
		menu.add(mScaled);
		menu.add(mCoverage);
		menu.addSeparator();
		menu.add(mSubset);
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

		// Show or hide the position values
		else if (e.getSource() == mHideCoordinates)
		{
			bandOverview.actionToggleCoordinates();
		}

		// Switch overview rendering to the scaled overview
		else if (e.getSource() == mScaled && Prefs.visOverviewType != SCALEDDATA)
		{
			bandOverview.actionToggleScaled();
		}

		// Switch overview rendering to the coverage overview
		else if (e.getSource() == mCoverage && Prefs.visOverviewType != COVERAGE)
		{
			bandOverview.actionToggleCoverage();
		}

		else if (e.getSource() == mSubset)
		{
			bandOverview.actionShowSubsetDialog();
		}

		else if (e.getSource() == mReset)
		{
			bandOverview.actionReset();
		}
	}
}