// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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
	private static CigarOverlayer previous;

	private Color cigarColor;
	private Color washedOut;
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

		int ntW = rCanvas.ntW;
		int pX1 = rCanvas.pX1;
		int pX2Max = rCanvas.pX2Max;
		int xS = pX1 / ntW;
		int xE = pX2Max / ntW;

		Stroke oldStroke = g.getStroke();


		g.setPaint(currentColor(Prefs.visReadsCanvasZoom));
		// Set the outline width based on the zoom level
		g.setStroke(calculateStroke(Prefs.visReadsCanvasZoom, oldStroke));

		ArrayList<Feature> features = new ArrayList<Feature>();

		for (int i=0; i < vContig.getTrackCount(); i++)
		{
			FeatureTrack track = vContig.getTrack(i);
			if (track.getName().equals("CIGAR-I"))
				features = track.getFeatures(xS, xE);
		}

		for (Feature feature : features)
		{
			CigarFeature cigarFeature = (CigarFeature)feature;

			int insertBase = cigarFeature.getVisualPS()+1;

			int yS = rCanvas.pY1 / rCanvas.ntH;
			int yE = rCanvas.pY2 / rCanvas.ntH;
			int ntH = rCanvas.ntH;
			int offset = rCanvas.offset;

			int topLeft = (insertBase -1 -offset) * ntW;
			int length = (ntW * 2) -1;

			for (int row = yS; row <= yE; row++)
			{
				Read read = rCanvas.reads.getReadAt(row, insertBase);

				for(Insert insert : cigarFeature.getInserts())
					if(insert.getRead().equals(read))
						g.drawRect(topLeft, row*ntH, length, ntH-1);
			}
		}
		g.setStroke(oldStroke);
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
		washedOut = new Color(123, 85, 81, alphaEffect);
		vibrant = new Color(104, 20, 0, alphaEffect);
	}

	private void defaultColors()
	{
		cigarColor = new Color(169, 46, 34);
		washedOut = new Color(123, 85, 81);
		vibrant = new Color(204, 20, 0);
	}

	private Color currentColor(int zoom)
	{
		Color current;

		if (zoom > 5)
			current = cigarColor;
		else if (zoom > 13)
			current = vibrant;
		else
			current = washedOut;

		return current;
	}

	private Stroke calculateStroke(int zoom, Stroke oldStroke)
	{
		Stroke stroke;

		if (zoom > 18)
			stroke = new BasicStroke(3);
		else if (zoom > 8)
			stroke = new BasicStroke(2);
		else
			stroke = oldStroke;

		return stroke;
	}
}