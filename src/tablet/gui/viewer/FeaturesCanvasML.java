// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.auxiliary.*;
import tablet.gui.*;

import scri.commons.gui.*;

class FeaturesCanvasML extends MouseInputAdapter implements ActionListener
{
	private FeaturesCanvas fCanvas;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	private JMenuItem mSelectTracks;

	FeaturesCanvasML(AssemblyPanel aPanel)
	{
		fCanvas = aPanel.featuresCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		fCanvas.addMouseListener(this);
		fCanvas.addMouseMotionListener(this);

		new ReadsCanvasDragHandler(aPanel, fCanvas);

		mSelectTracks = new JMenuItem("");
		RB.setText(mSelectTracks, "gui.viewer.FeaturesCanvasML.mSelectTracks");
		mSelectTracks.addActionListener(this);
	}

	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + rCanvas.offset;
		sCanvas.setMouseBase(xIndex);

		detectFeature(xIndex);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	private void displayMenu(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		menu.add(mSelectTracks);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mSelectTracks)
			Tablet.winMain.getFeaturesPanel().editFeatures();
	}

	private void detectFeature(int x)
	{
		System.out.println(x);

		// TODO
		int track = 0;

		ArrayList<Feature> data = fCanvas.vContig.getTrack(0).getFeatures(x, x);

		for (Feature f: data)
		{
			System.out.println(f.getGFFType() + " " + f.getDataPS());
			System.out.println();
		}
	}
}