// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.geom.*;
import static java.awt.RenderingHints.*;
import java.util.*;
import java.util.stream.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;

public class FeaturesCanvas extends TrackingCanvas
{
	private AssemblyPanel aPanel;

	private Contig contig;
	VisualContig vContig;

	private Dimension dimension = new Dimension();

	// The height of a SINGLE track
	static final int H = 20;

	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);
	private BasicStroke solid = new BasicStroke(1);

	private Paint snpPaint;
	private Paint cigarPaint;
	private Paint enzymePaint;

	private ArrayList<ArrayList<Feature>> featuresOnScreen = new ArrayList<>();

	FeaturesCanvas()
	{
//		snpPaint = new GradientPaint(0, 0, new Color(255, 255, 255, 50),
//			0, H, new Color(200, 0, 0, 50));

		snpPaint = new Color(0, 200, 0, 150);
		cigarPaint = TabletUtils.red1;
		enzymePaint = new Color(70, 116, 162, 150);
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
			vContig = aPanel.getVisualContig();
			prepareTracks(contig);

			dimension = new Dimension(0, vContig.getTrackCount()*H+5);
		}

		else
			dimension = new Dimension(0, 0);

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

	@Override
	public Dimension getPreferredSize()
		{ return dimension; }

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		long s = System.currentTimeMillis();

		offset = contig.getVisualStart();

		float ntW = rCanvas.ntW;
		int xS = rCanvas.xS + offset;
		int xE = rCanvas.xE + offset;

		featuresOnScreen = new ArrayList<ArrayList<Feature>>();

		for (int trackNum = 0; trackNum < vContig.getTrackCount(); trackNum++)
		{
			FeatureTrack track = vContig.getTrack(trackNum);
			ArrayList<Feature> features = track.getFeatures(xS, xE);
			featuresOnScreen.add(features);

			if (trackNum > 0)
				g.translate(0, H);

			g.setColor(Color.lightGray);
			g.setStroke(dashed);
			g.drawLine(x1, H/2+2, x2, H/2+2);
			g.setStroke(solid);

			for (Feature f: features)
			{
				int p1 = f.getVisualPS();
				int p2 = f.getVisualPE();

				String featureType = f.getGFFType().toUpperCase();

				switch (featureType)
				{
					case Feature.SNP:
						drawSnp(g, ntW, p1, p2);
						break;
					case Feature.CIGAR_I:
						drawCigarI(g, ntW, p1, p2);
						break;
					case Feature.CIGAR_LEFT_CLIP:
						drawCigarLeftClip(g, ntW, p1, p2);
						break;
					case Feature.CIGAR_RIGHT_CLIP:
						drawCigarRightClip(g, ntW, p1, p2);
						break;

					default:
						drawGenericFeature(g, ntW, f, p1, p2);
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

	private void drawSnp(Graphics2D g, float ntW, int p1, int p2)
	{
		int start = rCanvas.getFirstRenderedPixel(p1);
		int w = rCanvas.getFinalRenderedPixel(p2) - rCanvas.getFirstRenderedPixel(p1);

		if (ntW < 1)
			start = rCanvas.getFinalRenderedPixel(p1);

		if (ntW <= 1)
			w = 1;

		g.translate(start, 0);

		Path2D.Double path = new Path2D.Double();
		path.moveTo(0, H/4);
		path.lineTo(w, H/4);
		path.lineTo(w/2, H-H/4);
		path.closePath();

		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setPaint(snpPaint);
		g.fill(path);
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);

		g.translate(-start, 0);
	}

	private void drawCigarI(Graphics2D g, float ntW, int p1, int p2)
	{
		g.setPaint(cigarPaint);
		int start = rCanvas.getFirstRenderedPixel(p1);
		int end = rCanvas.getFinalRenderedPixel(p2);
		int middle = start + ((end - start) / 2);
		int centWidth = 2;

		if (ntW < 1)
		{
			start = rCanvas.getFinalRenderedPixel(p1);
			middle = start;
			centWidth = 1;
		}

		int above = H/4 + 2;
		int below = H-4;

		// Top horizontal bar
		g.drawLine(start, above, end, above);
		// Vertical bar
		g.fillRect(middle, H/4+2, centWidth, below-above);
		// Bottom horizontal bar
		g.drawLine(start, below, end, below);
	}

	private void drawCigarLeftClip(Graphics2D g, float ntW, int p1, int p2)
	{
		g.setPaint(cigarPaint);

		int start = rCanvas.getFirstRenderedPixel(p1);
		int end = rCanvas.getFinalRenderedPixel(p2);
		int centWidth = 2;

		if (ntW < 1)
		{
			start = rCanvas.getFinalRenderedPixel(p1);
			centWidth = 1;
		}

		int above = H/4 + 2;
		int below = H-4;

		// Top horizontal bar
		g.drawLine(start, above, end, above);
		// Vertical bar
		g.fillRect(start, H/4+2, centWidth, below-above);
		// Bottom horizontal bar
		g.drawLine(start, below, end, below);
	}

	private void drawCigarRightClip(Graphics2D g, float ntW, int p1, int p2)
	{
		g.setPaint(cigarPaint);

		int start = rCanvas.getFirstRenderedPixel(p1);
		int end = rCanvas.getFinalRenderedPixel(p2);
		int centWidth = 2;

		if (ntW < 1)
		{
			start = rCanvas.getFinalRenderedPixel(p1);
			centWidth = 1;
		}

		int above = H/4 + 2;
		int below = H-4;

		// Top horizontal bar
		g.drawLine(start, above, end, above);
		// Vertical bar
		g.fillRect(end-1, H/4+2, centWidth, below-above);
		// Bottom horizontal bar
		g.drawLine(start, below, end, below);
	}

	private void drawEnzymeFeature(Graphics2D g, float ntW, EnzymeFeature f, int p1, int p2)
	{
		EnzymeFeature enzyme = f;
		int cutPoint = p1+enzyme.getCutPoint();

		int cPoint = rCanvas.getFirstRenderedPixel(cutPoint);

		int start = rCanvas.getFirstRenderedPixel(p1);
		int end = rCanvas.getFinalRenderedPixel(p2);

		if (ntW < 1)
		{
			cPoint = rCanvas.getFinalRenderedPixel(cutPoint);
			start = rCanvas.getFinalRenderedPixel(p1);
		}

		g.setPaint(enzymePaint);
		g.fillRect(start, H/4+5, end-start, H/4-1);
		//g.drawRect(start, H/4+5, end, H/4-1);

		// Draw line over last pixel in previous base and first pixel
		// in next base
		if (enzyme.getCutPoint() != -1)
		{
			g.setStroke(new BasicStroke(2));
			if (ntW < 1)
				g.setStroke(new BasicStroke(1));
			g.drawLine(cPoint, H/4+2, cPoint, H-3);
			g.setStroke(solid);
		}
	}

	private void drawGenericFeature(Graphics2D g, float ntW, Feature f, int p1, int p2)
	{
		// Full color
		Color cF = Feature.colors.get(f.getGFFType());
		// Alpha version
		Color cA = new Color(cF.getRed(), cF.getGreen(), cF.getBlue(), 50);

		int start = rCanvas.getFirstRenderedPixel(p1);
		int end = rCanvas.getFinalRenderedPixel(p2);

		if (ntW < 1)
			start = rCanvas.getFinalRenderedPixel(p1);

		g.setPaint(cA);
		g.fillRect(start, H/4+2, end-start, H/2);
		g.setPaint(cF);
		g.drawRect(start, H/4+2, end-start, H/2);
	}

	// Used by the mouse handling code in FeaturesCanvasML. Returns the features
	// on a given track, at a given position.
	ArrayList<Feature> getFeatures(int track, int pos)
	{
		return featuresOnScreen.get(track)
				.stream()
				.filter(f -> f.getDataPS() <= pos && f.getDataPE() >= pos)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}