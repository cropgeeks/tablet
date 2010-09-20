// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import static java.awt.RenderingHints.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

public class FeaturesCanvas extends TrackingCanvas
{
	private AssemblyPanel aPanel;

	private Contig contig;
	VisualContig vContig;
	private Consensus consensus;

	private Dimension dimension = new Dimension();

	// The height of a SINGLE track
	static final int H = 20;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	private Paint snpPaint;
	private Paint cigarPaint;

	FeaturesCanvas()
	{
//		snpPaint = new GradientPaint(0, 0, new Color(255, 255, 255, 50),
//			0, H, new Color(200, 0, 0, 50));

		snpPaint = new Color(0, 200, 0, 150);
		cigarPaint = TabletUtils.red1;
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;

		new FeaturesCanvasML(aPanel);
	}

	public void setContig(Contig contig)
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

		if (vContig != null && vContig.getTrackCount() > 0)
		{
			setVisible(true);
			setPreferredSize(dimension);
		}
		else
			setVisible(false);
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

		long s = System.currentTimeMillis();

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
			g.drawLine(x1, H/2+2, x2, H/2+2);
			g.setStroke(solid);

			for (Feature f: features)
			{
				int p1 = f.getVisualPS() - offset;
				int p2 = f.getVisualPE() - offset;

				// Paint a SNP as a triangle, pointing down at the position
				if (f.getGFFType().equals("SNP"))
				{
					g.translate(p1*ntW, 0);
					int w = (p2-p1+1)*ntW;

					Path2D.Double path = new Path2D.Double();
					path.moveTo(0, H/4);
					path.lineTo(w, H/4);
					path.lineTo(w/2, H-H/4);
					path.closePath();

					g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
					g.setPaint(snpPaint);
					g.fill(path);
					g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);

					g.translate(-(p1*ntW), 0);
				}

				// Paint a cigar_insertion feature as an "I" bar
				else if (f.getGFFType().equals("CIGAR-I"))
				{
					g.setPaint(cigarPaint);

					// Top horizontal bar
					g.drawLine(p1*ntW, H/4+2, p2*ntW+ntW-1, H/4+2);
					// Vertical bar
					g.fillRect(p2*ntW-1, H/4+2, 2, H-3-(H/4+2));
					// Bottom horizontal bar
					g.drawLine(p1*ntW, H-3, p2*ntW+ntW-1, H-3);
				}

				// All other features are rendered as rectangles from p1 to p2
				else
				{
					// Full color
					Color cF = Feature.colors.get(f.getGFFType());
					// Alpha version
					Color cA = new Color(cF.getRed(), cF.getGreen(), cF.getBlue(), 50);

					g.setPaint(cA);
					g.fillRect(p1*ntW, H/4+2, (p2-p1+1)*ntW-1, H/2);
					g.setPaint(cF);
					g.drawRect(p1*ntW, H/4+2, (p2-p1+1)*ntW-1, H/2);
				}
			}

			g.setColor(Color.black);
			g.setFont(new Font("Dialog", Font.PLAIN, 7));
			g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(track.getName(), x1, 6);
		}

		long e = System.currentTimeMillis();
//		System.out.println("FeaturesCanvas: " + (e-s) + "ms");
	}
}