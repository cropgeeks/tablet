// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

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

	private AssemblyPanel aPanel;

	private JMenuItem mSelectTracks;

	FeaturesCanvasML(AssemblyPanel aPanel)
	{
		fCanvas = aPanel.featuresCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		this.aPanel = aPanel;

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
		// TODO
		int track = 0;

		ArrayList<Feature> data = fCanvas.vContig.getTrack(0).getFeatures(x, x);

		for (Feature f: data)
		{
			//System.out.println(f.getGFFType() + " " + f.getDataPS());
			//System.out.println();

			if(f.getGFFType().equals("CIGAR-I") && !aPanel.getCigarIHighlighter().isVisible())
			{
				CigarFeature cigarFeature = (CigarFeature)f;
				new CigarIHighlighter(aPanel, cigarFeature.getVisualPS()+1, cigarFeature);
				rCanvas.repaint();
			}
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + rCanvas.offset;
		int track = 0;

		ArrayList<Feature> data = fCanvas.vContig.getTrack(0).getFeatures(xIndex, xIndex);

		for (Feature f: data)
		{
			if(f.getGFFType().equals("CIGAR-I"))
			{
				CigarFeature cigarFeature = (CigarFeature)f;
				if(!aPanel.getCigarIHighlighter().isVisible())
				{
					aPanel.getCigarIHighlighter().setMouseBase(cigarFeature.getVisualPS()+1);
					aPanel.getCigarIHighlighter().setCigarFeature(cigarFeature);
					aPanel.getCigarIHighlighter().add();
				}
				else
				{
					aPanel.getCigarIHighlighter().setMouseBase(null);
					aPanel.getCigarIHighlighter().setCigarFeature(null);
					aPanel.getCigarIHighlighter().remove();
				}
				rCanvas.repaint();
			}
		}
	}
}