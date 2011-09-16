// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
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

	private AssemblyPanel aPanel;

	private JMenuItem mCopyReference;
	private JMenuItem mSelectTracks;

	// A list of features under the mouse (will only be > 1 if features overlap)
	private ArrayList<Feature> features;

	FeaturesCanvasML(AssemblyPanel aPanel)
	{
		fCanvas = aPanel.featuresCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		this.aPanel = aPanel;

		fCanvas.addMouseListener(this);
		fCanvas.addMouseMotionListener(this);

		new ReadsCanvasDragHandler(aPanel, fCanvas);
	}

	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null);
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + rCanvas.offset;
		int yIndex = e.getY() / fCanvas.H;

		sCanvas.setMouseBase(xIndex);

		detectFeature(xIndex, yIndex);
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

		mCopyReference = new JMenuItem("");
		RB.setText(mCopyReference, "gui.viewer.FeaturesCanvasML.mCopyReference");
		mCopyReference.setIcon(Icons.getIcon("CLIPBOARD"));
		mCopyReference.addActionListener(this);
		mCopyReference.setEnabled(features.size() > 0);

		mSelectTracks = new JMenuItem("");
		RB.setText(mSelectTracks, "gui.viewer.FeaturesCanvasML.mSelectTracks");
		mSelectTracks.addActionListener(this);

		menu.add(mCopyReference);
		menu.addSeparator();
		menu.add(mSelectTracks);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mCopyReference)
		{
			StringBuilder str = new StringBuilder();

			for (Feature feature: features)
			{
				int start = feature.getDataPS();
				int end   = feature.getDataPE();

				// Get contig name and consensus / reference string
				String name = aPanel.getContig().getName()
					+ "_" + (start+1) + "-" + (end+1);
				String consensus = aPanel.getContig().getConsensus().toString();

				// Carry out the substring operation and format data as fasta
				String seq = consensus.substring(start, end+1);
				String text = TabletUtils.formatFASTA(name, seq);

				str.append(text + System.getProperty("line.separator"));
			}

			StringSelection selection = new StringSelection(str.toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					selection, null);
		}

		else if (e.getSource() == mSelectTracks)
			Tablet.winMain.getFeaturesPanel().editFeatures();
	}

	private void detectFeature(int x, int y)
	{
		int track;

		// Check if mouse is over a track on the features canvas
		if (y < fCanvas.vContig.getTrackCount())
			track = y;
		else
			return;

		features = fCanvas.vContig.getTrack(track).getFeatures(x, x);

		if (features.isEmpty())
			fCanvas.setToolTipText(null);

		for (Feature f: features)
		{
			if(f.getGFFType().equals("CIGAR-I") && !aPanel.getCigarIHighlighter().isVisible())
			{
				CigarFeature cigarFeature = (CigarFeature)f;
				new CigarIHighlighter(aPanel, cigarFeature.getVisualPS()+1, cigarFeature);
				rCanvas.repaint();
			}

			// Set tooltip for the feature under the mouse
			fCanvas.setToolTipText(RB.format("gui.viewer.FeaturesCanvasML.tooltip",
					f.getGFFType(), f.getName(),
					TabletUtils.nf.format(f.getVisualPS()+1), TabletUtils.nf.format(f.getVisualPE()+1)));
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.isPopupTrigger())
			return;

		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + rCanvas.offset;
		int track = 0;

		for (Feature f: features)
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