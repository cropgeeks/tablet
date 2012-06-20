// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class EnhancedScheme extends ReadScheme
{
	private HashMap<String, Color> colors = new HashMap<>();

	// Holds the states needed by the reads canvas
	protected ArrayList<Stamp> statesRD = new ArrayList<>();
	// Holds the states needed by the consensus canvas
	protected ArrayList<Stamp> statesCS = new ArrayList<>();

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
		colors.put("?", ColorPrefs.get("User.EnhancedScheme.?"));
		colors.put(Sequence.PAD, ColorPrefs.get("User.EnhancedScheme.PAD"));
		colors.put("N", ColorPrefs.get("User.EnhancedScheme.N"));
		colors.put("A", ColorPrefs.get("User.EnhancedScheme.A"));
		colors.put("C", ColorPrefs.get("User.EnhancedScheme.C"));
		colors.put("G", ColorPrefs.get("User.EnhancedScheme.G"));
		colors.put("T", ColorPrefs.get("User.EnhancedScheme.T"));
	}

	public Image getConsensusImage(int data)
	{
		return statesCS.get(data).getImage();
	}

	public Color getConsensusColor(int data)
	{
		return statesCS.get(data).getColor();
	}

	public Image getImage(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getColor();
	}

	public Color getOverviewColor(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getOverviewColor();
	}

	public Image getPairLink()
		{ return pairLink.getImage(); }
}