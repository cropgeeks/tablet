// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

import tablet.data.*;

public class VariantsScheme extends EnhancedScheme
{
	public VariantsScheme(int w, int h)
	{
		super(w, h, true, false);

		initStates(w, h);
	}

	private void initStates(int w, int h)
	{
		Color variant = new Color(255, 120, 120);
		Color normal = Color.LIGHT_GRAY;

		for (String base: Sequence.getStates())
		{
			// Add the normal image
			statesRD.add(new ColorStamp(base, normal, w, h, true, false));
			// Add the delta image
			statesRD.add(new ColorStamp(base, variant, w, h, false, true));
		}
	}


	public Image getImage(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		return statesRD.get(rmd.getStateAt(index)).getColor();
	}
}