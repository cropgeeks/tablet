// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
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
	boolean hidden;

	private Color cigarColor;
	private Color vibrant;

	public CigarOverlayer(AssemblyPanel aPanel, boolean hidden)
	{
		super(aPanel);

		this.hidden = hidden;

		//if we're already animating cancel that animation and replace it with
		//this new one
		if (previous != null)
		{
			previous.interrupt();
			rCanvas.overlays.remove(previous);
		}
		previous = this;

		defaultColors();
	}

	public void render(Graphics2D g)
	{
		VisualContig vContig = aPanel.getVisualContig();

		int xS = rCanvas.getBaseForPixel(rCanvas.pX1);
		int xE = rCanvas.getBaseForPixel(rCanvas.pX2);

		g.setPaint(currentColor(Prefs.visReadsZoomLevel));

		Stroke oldStroke = g.getStroke();

		g.setPaint(currentColor(Prefs.visReadsZoomLevel));
		// Set the outline width based on the zoom level
		g.setStroke(calculateStroke(Prefs.visReadsZoomLevel, oldStroke));

		ArrayList<Feature> cigarI = getTrackByName(vContig, "CIGAR-I", xS, xE);
		renderCigar(cigarI, g);

		ArrayList<Feature> cigarD = getTrackByName(vContig, "CIGAR-D", xS, xE);
		g.setPaint(Color.BLUE);
		renderCigar(cigarD, g);

		g.setStroke(oldStroke);
	}

	private ArrayList<Feature> getTrackByName(VisualContig vContig, String name, int xS, int xE)
	{
		ArrayList<Feature> features = new ArrayList<Feature>();

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

				for(CigarEvent event : ((CigarFeature)feature).getEvents())
					if(event.getRead().equals(read) || event.getRead().equals(r2))
						g.drawRect(start, row*rCanvas.ntH, length, rCanvas.readH-1);
			}
		}
	}

	@Override
	public void run()
	{
		rCanvas.overlays.addFirst(this);

		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the desired ending alpha)
			if(!hidden)
				alphaEffect = (0 + (i * 5));
			else
				alphaEffect = (200 - (i * 5));

			updateAlphas();

			rCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		if(hidden)
		{
			rCanvas.overlays.remove(this);
			rCanvas.repaint();
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