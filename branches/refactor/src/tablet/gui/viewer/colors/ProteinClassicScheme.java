// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.analysis.*;

public class ProteinClassicScheme extends ProteinScheme
{
	private ArrayList<Stamp> states = new ArrayList<Stamp>();

	public ProteinClassicScheme(int w, int h)
	{
		String[] codes = new ProteinTranslator().codes;

		states.add(new ColorStamp("", Color.white, w, h, false, false));

		// The 22 proteins as images WITH text
		states.add(new GrayscaleStamp(codes[1],  w, h, false, false));	// I
		states.add(new GrayscaleStamp(codes[2],  w, h, false, false));	// L
		states.add(new GrayscaleStamp(codes[3],  w, h, false, false));	// V
		states.add(new GrayscaleStamp(codes[4],  w, h, false, false));	// F
		states.add(new GrayscaleStamp(codes[5],  w, h, false, false));	// M
		states.add(new GrayscaleStamp(codes[6],  w, h, false, false));	// C
		states.add(new GrayscaleStamp(codes[7],  w, h, false, false));	// A
		states.add(new GrayscaleStamp(codes[8],  w, h, false, false));	// G
		states.add(new GrayscaleStamp(codes[9],  w, h, false, false));	// P
		states.add(new GrayscaleStamp(codes[10], w, h, false, false));	// T
		states.add(new GrayscaleStamp(codes[11], w, h, false, false));	// S
		states.add(new GrayscaleStamp(codes[12], w, h, false, false));	// Y
		states.add(new GrayscaleStamp(codes[13], w, h, false, false));	// W
		states.add(new GrayscaleStamp(codes[14], w, h, false, false));	// Q
		states.add(new GrayscaleStamp(codes[15], w, h, false, false));	// N
		states.add(new GrayscaleStamp(codes[16], w, h, false, false));	// H
		states.add(new GrayscaleStamp(codes[17], w, h, false, false));	// E
		states.add(new GrayscaleStamp(codes[18], w, h, false, false));	// D
		states.add(new GrayscaleStamp(codes[19], w, h, false, false));	// K
		states.add(new GrayscaleStamp(codes[20], w, h, false, false));	// R
		states.add(new GrayscaleStamp(codes[21], w, h, false, true));	// .
		states.add(new GrayscaleStamp(codes[22], w, h, false, false));	// X

		// The 22 proteins as images WITHOUT text
		// We can reuse the same image for each group
		GrayscaleStamp empty = new GrayscaleStamp("", w, h, false, false);
		for (int i = 0; i < ProteinTranslator.RBASE; i++)
			states.add(empty);
	}

	public Image getImage(int data)
	{
		return states.get(data).getImage();
	}
}