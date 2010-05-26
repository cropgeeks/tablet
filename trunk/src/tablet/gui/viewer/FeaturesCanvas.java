// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import static java.awt.RenderingHints.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.colors.*;

class FeaturesCanvas extends TrackingCanvas
{
	private AssemblyPanel aPanel;

	private Contig contig;
	private VisualContig vContig;
	private Consensus consensus;

	private Dimension dimension = new Dimension();

	// The height of a SINGLE track
	private static final int H = 20;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	FeaturesCanvas()
	{
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			consensus = contig.getConsensus();

			prepareTracks(contig);
			dimension = new Dimension(0, vContig.getTrackCount()*H+5);
		}

		// Remove tablet.data references if nothing is going to be displayed
		else
		{
			consensus = null;
			dimension = new Dimension(0, 0);
		}

		this.contig = contig;

		setPreferredSize(dimension);
		revalidate();
	}

	private void prepareTracks(Contig contig)
	{
		try
		{
			// Set up the tracks for this contig
			vContig = aPanel.getVisualContig();

			FeatureTrackCreator ftc = new FeatureTrackCreator(vContig, contig);
			ftc.runJob(0);
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (consensus.length() == 0)
			return;

		offset = contig.getVisualStart();

		int ntW = rCanvas.ntW;
		int xS = rCanvas.xS + offset;
		int xE = rCanvas.xE + offset;


		for (int trackNum = 0; trackNum < vContig.getTrackCount(); trackNum++)
		{
			FeatureTrack track = vContig.getTrack(trackNum);
			ArrayList<Feature> features = track.getFeatures(xS, xE);

			if (trackNum > 0)
				g.translate(0, H);

			g.setColor(Color.lightGray);
			g.setStroke(dashed);
			g.drawLine(x1, H/2, x2, H/2);
			g.setStroke(solid);
			g.setColor(Color.red);

			for (Feature f: features)
			{
				int p1 = f.getP1() - offset;
				int p2 = f.getP2() - offset;

				g.setPaint(new Color(255, 0, 0, 50));
				g.fillRect(p1*ntW, H/4, (p2-p1+1)*ntW-1, H/2);
				g.setPaint(new Color(0, 0, 0, 50));
				g.drawRect(p1*ntW, H/4, (p2-p1+1)*ntW-1, H/2);

//				int w = p2-p1+1;
//				int[] xx = new int[] { p1*ntW, (p1+w)*ntW, ((p1+w)*ntW - p1*ntW)/2 };
//				int[] yy = new int[] { 0, 0, H };
//				g.fillPolygon(xx, yy, 3);
			}

			g.setColor(Color.black);
			g.setFont(new Font("Dialog", Font.PLAIN, 7));
			g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(track.getName(), x1, 6);
		}
	}
}