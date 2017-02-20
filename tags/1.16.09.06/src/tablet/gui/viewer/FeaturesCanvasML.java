// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
	private JMenuItem mAddRestriction;
	private JMenuItem mCopyFeature;

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
		int x = fCanvas.getMouseX(e);
		int xIndex = rCanvas.getBaseForPixel(rCanvas.pX1 + x);

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
		mCopyReference.setEnabled(features != null && features.size() > 0);

		mSelectTracks = new JMenuItem("");
		RB.setText(mSelectTracks, "gui.viewer.FeaturesCanvasML.mSelectTracks");
		mSelectTracks.addActionListener(this);

		mCopyFeature = new JMenuItem("");
		RB.setText(mCopyFeature, "gui.viewer.FeaturesCanvasML.mCopyFeature");
		mCopyFeature.setIcon(Icons.getIcon("CLIPBOARD"));
		mCopyFeature.addActionListener(this);
		mCopyFeature.setEnabled(features != null && features.size() > 0);

		mAddRestriction = new JMenuItem(RB.getString("gui.viewer.FeaturesCanvasML.addRestrictionEnyme"));
		mAddRestriction.addActionListener(this);

		menu.add(mCopyReference);
		menu.add(mCopyFeature);
		menu.addSeparator();
		menu.add(mSelectTracks);
		menu.add(mAddRestriction);
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

				str.append(text).append(System.getProperty("line.separator"));
			}

			StringSelection selection = new StringSelection(str.toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					selection, null);
		}

		else if (e.getSource() == mSelectTracks)
			Tablet.winMain.getFeaturesPanel().editFeatures();

		else if (e.getSource() == mAddRestriction)
			Tablet.winMain.getRestrictionEnzymeDialog().setVisible(true);

		else if (e.getSource() == mCopyFeature)
		{
			// Doesn't deal elegantly with overlapping features (note the
			// effectively ignored for loop)
			String str = "";
			for (Feature f: features)
				str = getCopyTextForFeature(f);

			StringSelection selection = new StringSelection(str);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}

	private String getCopyTextForFeature(Feature f)
	{
		StringBuilder str = new StringBuilder();

		int p1 = f.getVisualPS();
		int p2 = f.getVisualPE();

		String lb = System.getProperty("line.separator");

		str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.type", f.getGFFType())).append(lb);
		str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.name", f.getName())).append(lb);

		if (Prefs.guiFeaturesArePadded)
		{
			str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.padded",
				TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1))).append(lb);
			str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.unpadded",
				DisplayData.getUnpadded(p1), DisplayData.getUnpadded(p2))).append(lb);
		}
		else
		{
			str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.unpadded",
				TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1))).append(lb);
			str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.padded",
				DisplayData.getPadded(p1), DisplayData.getPadded(p2))).append(lb);
		}

		if (f instanceof CigarFeature)
		{
			int count = ((CigarFeature)f).count();

			if (f instanceof CigarFeature)
				str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.insertCount", count));
			else if (f instanceof CigarFeature)
				str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.deletionCount", count));
		}
		else
			str.append(RB.format("gui.viewer.FeaturesCanvasML.copyFeature.tags", f.getTagsAsString()));

		return str.toString();
	}

	private void detectFeature(int x, int y)
	{
		int track;

		// Check if mouse is over a track on the features canvas
		if (y < fCanvas.vContig.getTrackCount())
			track = y;
		else
			return;

		// Get the features directly from the features canvas (which in turn
		// searches the interval tree of features.
		features = fCanvas.getFeatures(track, x);

		if (features.isEmpty())
			fCanvas.setToolTipText(null);

		for (Feature feature: features)
		{
			int pS = feature.getVisualPS();
			int pE = feature.getVisualPE();

			if (feature instanceof CigarFeature)
				getCigarTooltip((CigarFeature)feature, pS, pE);
			else
				getNormalTooltip(feature, pS, pE);
		}
	}

	private void getNormalTooltip(Feature f, int pS, int pE)
	{
		if (Prefs.guiFeaturesArePadded)
		{
			fCanvas.setToolTipText(RB.format("gui.viewer.FeaturesCanvasML.tooltip.padded",
				f.getGFFType(), f.getName(),
				TabletUtils.nf.format(pS+1), TabletUtils.nf.format(pE+1),
				DisplayData.getUnpadded(pS), DisplayData.getUnpadded(pE),
				f.getTagsAsHTMLString()));
		}
		else
		{
			fCanvas.setToolTipText(RB.format("gui.viewer.FeaturesCanvasML.tooltip.unpadded",
				f.getGFFType(), f.getName(),
				TabletUtils.nf.format(pS+1), TabletUtils.nf.format(pE+1),
				DisplayData.getPadded(pS), DisplayData.getPadded(pE),
				f.getTagsAsHTMLString()));
		}
	}

	private void getCigarTooltip(CigarFeature f, int pS, int pE)
	{
		int count = f.count();
		if (Prefs.guiFeaturesArePadded)
		{
			fCanvas.setToolTipText(RB.format("gui.viewer.FeaturesCanvasML.tooltip.padded.cigarFeature",
				f.getGFFType(),
				TabletUtils.nf.format(pS+1), TabletUtils.nf.format(pE+1),
				DisplayData.getUnpadded(pS), DisplayData.getUnpadded(pE),
				count));
		}
		else
		{
			fCanvas.setToolTipText(RB.format("gui.viewer.FeaturesCanvasML.tooltip.unpadded.cigarFeature",
				f.getGFFType(),
				TabletUtils.nf.format(pS+1), TabletUtils.nf.format(pE+1),
				DisplayData.getPadded(pS), DisplayData.getPadded(pE),
				count));
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.isPopupTrigger())
			return;

		if (aPanel.getCigarIHighlighter().isVisible())
			aPanel.getCigarIHighlighter().removeHighlight();

		for (Feature f: features)
		{
			if(f instanceof CigarFeature)
			{
				CigarFeature cigarFeature = (CigarFeature)f;
				CigarIHighlighter cigarHighlighter = aPanel.getCigarIHighlighter();

				if (cigarHighlighter.isVisible() == false)
					cigarHighlighter.highlightFeature(cigarFeature);
				else
					cigarHighlighter.removeHighlight();
			}
		}
	}
}