// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class EnhancedScheme extends ReadScheme
{
	private HashMap<String, Color> colors = new HashMap<String, Color>();

	// Holds the states needed by the reads canvas
	protected ArrayList<Stamp> statesRD = new ArrayList<Stamp>();
	// Holds the states needed by the consensus canvas
	protected ArrayList<Stamp> statesCS = new ArrayList<Stamp>();

	// Holds the image used to draw a "link" between two reads
	protected LinkStamp pairLink;

	public EnhancedScheme(int w, int h, boolean createCS, boolean createRD)
	{
		createColors();

		// Create the images used by the consensus canvas (if needed)
		if (createCS)
		{
			for (String base: Sequence.getStates())
			{
				Color c = colors.get(base);

				statesCS.add(new ColorStamp(base, c, w, h, false, false));
				statesCS.add(null);
			}
		}

		// Create the images used by the main (reads) canvas (if needed)
		if (createRD)
		{
			for (String base: Sequence.getStates())
			{
				Color c = colors.get(base);

				statesRD.add(new ColorStamp(base, c, w, h, true, false));
				statesRD.add(new ColorStamp(base, c, w, h, true, true));
			}
		}

		// Create the image used for linking pairs
		pairLink = new LinkStamp(new Color(180, 180, 180), w, h);
	}

	private void createColors()
	{
		colors.put("?", ColorPrefs.getColor("EnhancedScheme.?", Color.lightGray));
		colors.put(Sequence.PAD, ColorPrefs.getColor("EnhancedScheme.PAD", Color.lightGray));
		colors.put("N", ColorPrefs.getColor("EnhancedScheme.N", Color.lightGray));
		colors.put("A", ColorPrefs.getColor("EnhancedScheme.A", new Color(120, 255, 120)));
		colors.put("C", ColorPrefs.getColor("EnhancedScheme.C", new Color(255, 160, 120)));
		colors.put("G", ColorPrefs.getColor("EnhancedScheme.G", new Color(255, 120, 120)));
		colors.put("T", ColorPrefs.getColor("EnhancedScheme.T", new Color(120, 120, 255)));
	}

	public Image getConsensusImage(int data)
	{
		return statesCS.get(data).getImage();
	}

	public Image getImage(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getColor();
	}

	public Image getPairLink()
		{ return pairLink.getImage(); }
}