// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.CigarFeature.*;
import tablet.gui.Prefs;

public class CigarOverlayer extends AlphaOverlay
{
	private Color cigarColor;
	private Color vibrant;

	public CigarOverlayer(AssemblyPanel aPanel)
	{
		super(aPanel);

		//if we're already animating cancel that animation and replace it with
		//this new one
		if (previous != null)
		{
			previous.interrupt();
		}
		previous = this;

		defaultColors();
	}

	public void render(Graphics2D g)
	{
		if (rCanvas.ntW < 1)
			return;

		VisualContig vContig = aPanel.getVisualContig();

		int xS = rCanvas.getBaseForPixel(rCanvas.pX1);
		int xE = rCanvas.getBaseForPixel(rCanvas.pX2);

		g.setPaint(currentColor(Prefs.visReadsZoomLevel));

		Stroke oldStroke = g.getStroke();

		g.setPaint(currentColor(Prefs.visReadsZoomLevel));
		// Set the outline width based on the zoom level
		g.setStroke(calculateStroke(Prefs.visReadsZoomLevel, oldStroke));

		ArrayList<Feature> cigarI = getTrackByName(vContig, Feature.CIGAR_I, xS, xE);
		renderCigar(cigarI, g);

		ArrayList<Feature> cigarD = getTrackByName(vContig, Feature.CIGAR_D, xS, xE);
		g.setPaint(Color.BLUE);
		renderCigar(cigarD, g);

		ArrayList<Feature> cigarLeftClip = getTrackByName(vContig, Feature.CIGAR_LEFT_CLIP, xS, xE);
		g.setPaint(currentColor(Prefs.visReadsZoomLevel));
		renderCigarClip(cigarLeftClip, Feature.CIGAR_LEFT_CLIP, g);

		ArrayList<Feature> cigarRightClip = getTrackByName(vContig, Feature.CIGAR_RIGHT_CLIP, xS, xE);
		g.setPaint(currentColor(Prefs.visReadsZoomLevel));
		renderCigarClip(cigarRightClip, Feature.CIGAR_RIGHT_CLIP, g);

		g.setStroke(oldStroke);
	}

	private ArrayList<Feature> getTrackByName(VisualContig vContig, String name, int xS, int xE)
	{
		ArrayList<Feature> features = new ArrayList<>();

		for (int i=0; i < vContig.getTrackCount(); i++)
		{
			FeatureTrack track = vContig.getTrack(i);
			if (track.getName().equals(name))
				features = track.getFeatures(xS, xE);
		}

		return features;
	}

	private void renderCigar(ArrayList<Feature> features, Graphics2D g)
	{
		for (Feature feature : features)
		{
			int base = feature.getVisualPS();

			int start = rCanvas.getFirstRenderedPixel(base);
			int end = rCanvas.getFinalRenderedPixel(feature.getVisualPE());
			int length = end-start;
			int yS = rCanvas.pY1 / rCanvas.ntH;
			int yE = rCanvas.pY2 / rCanvas.ntH;

			for (int row = yS; row <= yE; row++)
			{
				Read read = rCanvas.reads.getReadAt(row, base);
				Read r2 = rCanvas.reads.getReadAt(row, base+1);

				renderCigarForFeature((CigarFeature)feature, read, r2, g, start, row*rCanvas.ntH, length, rCanvas.readH-1);
			}
		}
	}

	private void renderCigarForFeature(CigarFeature feature, Read r1, Read r2, Graphics2D g, int x, int y, int width, int height)
	{
		feature.getEvents().stream()
			.filter(e -> e.getRead().equals(r1) || e.getRead().equals(r2))
			.forEach(e -> g.drawRect(x, y, width, height));
	}

	private void renderCigarClip(ArrayList<Feature> features, String eventName, Graphics2D g)
	{
		for (Feature feature : features)
		{
			int base = feature.getVisualPS();

			int start = rCanvas.getFirstRenderedPixel(base);
			int end = rCanvas.getFinalRenderedPixel(feature.getVisualPE());
			int barWidth = 2;

			if (rCanvas.ntW < 1)
			{
				start = rCanvas.getFinalRenderedPixel(base);
				barWidth = 1;
			}

			int yS = rCanvas.pY1 / rCanvas.ntH;
			int yE = rCanvas.pY2 / rCanvas.ntH;

			for (int row = yS; row <= yE; row++)
			{
				Read read = rCanvas.reads.getReadAt(row, base);
				Read r2 = rCanvas.reads.getReadAt(row, base+1);

				for (CigarEvent event : ((CigarFeature)feature).getEvents())
				{
					if (event.getRead().equals(read) || event.getRead().equals(r2))
					{
						// Top horizontal bar
						g.drawLine(start+1, row * rCanvas.ntH, end, row * rCanvas.ntH);
						// Vertical bar
						if (eventName.equals(Feature.CIGAR_LEFT_CLIP))
							g.fillRect(start, row * rCanvas.ntH, barWidth, rCanvas.readH - 1);
						else if (eventName.equals(Feature.CIGAR_RIGHT_CLIP))
							g.fillRect(end, row * rCanvas.ntH, barWidth, rCanvas.readH - 1);
						// Bottom horizontal bar
						g.drawLine(start+1, row * rCanvas.ntH + rCanvas.readH - 1, end, row * rCanvas.ntH + rCanvas.readH - 1);
					}
				}
			}
		}
	}

	@Override
	public void run()
	{

		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the desired ending alpha)
			alphaEffect = (0 + (i * 5));

			updateAlphas();

			rCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		defaultColors();
	}

	private void updateAlphas()
	{
		cigarColor = new Color(169, 46, 34, alphaEffect);
		vibrant = new Color(104, 20, 0, alphaEffect);
	}

	private void defaultColors()
	{
		cigarColor = new Color(169, 46, 34);
		vibrant = new Color(204, 20, 0);
	}

	private Color currentColor(int zoom)
	{
		Color current;

		if (zoom > 7 || zoom < 21)
			current = cigarColor;
		else
			current = vibrant;

		return current;
	}

	private Stroke calculateStroke(int zoom, Stroke oldStroke)
	{
		Stroke stroke;

		if (zoom > 15)
			stroke = new BasicStroke(2);
		else
			stroke = oldStroke;

		return stroke;
	}
}