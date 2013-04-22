// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.analysis.*;

public class ProteinEnhancedScheme extends ProteinScheme
{
	private ArrayList<Stamp> states = new ArrayList<Stamp>();

	public ProteinEnhancedScheme(int w, int h)
	{
		Color NONPOLAR = ColorPrefs.get("User.ProteinScheme.NonPolar");
		Color POLAR = ColorPrefs.get("User.ProteinScheme.Polar");
		Color BASIC = ColorPrefs.get("User.ProteinScheme.Basic");
		Color ACIDIC = ColorPrefs.get("User.ProteinScheme.Acidic");
		Color STOP = ColorPrefs.get("User.ProteinScheme.Stop");
		Color XXX = ColorPrefs.get("User.ProteinScheme.XXX");

		// VERY IMPORTANT: These MUST be in the same order as the codes within
		// the string array from the ProteinTranslator

		String[] codes = new ProteinTranslator().codes;

		states.add(new ColorStamp("", Color.white, w, h, false, false));

		for (int i = 0; i < 3; i++)
		{
			// The 22 proteins as images WITH text
			states.add(new ProteinStamp(codes[1],  NONPOLAR, w, h, i));	// I
			states.add(new ProteinStamp(codes[2],  NONPOLAR, w, h, i));	// L
			states.add(new ProteinStamp(codes[3],  NONPOLAR, w, h, i));	// V
			states.add(new ProteinStamp(codes[4],  NONPOLAR, w, h, i));	// F
			states.add(new ProteinStamp(codes[5],  NONPOLAR, w, h, i));	// M
			states.add(new ProteinStamp(codes[6],  POLAR, w, h, i));	// C
			states.add(new ProteinStamp(codes[7],  NONPOLAR, w, h, i));	// A
			states.add(new ProteinStamp(codes[8],  POLAR, w, h, i));	// G
			states.add(new ProteinStamp(codes[9],  NONPOLAR, w, h, i));	// P
			states.add(new ProteinStamp(codes[10], POLAR, w, h, i));	// T
			states.add(new ProteinStamp(codes[11], POLAR, w, h, i));	// S
			states.add(new ProteinStamp(codes[12], POLAR, w, h, i));	// Y
			states.add(new ProteinStamp(codes[13], NONPOLAR, w, h, i));	// W
			states.add(new ProteinStamp(codes[14], POLAR, w, h, i));	// Q
			states.add(new ProteinStamp(codes[15], POLAR, w, h, i));	// N
			states.add(new ProteinStamp(codes[16], BASIC, w, h, i));	// H
			states.add(new ProteinStamp(codes[17], ACIDIC, w, h, i));	// E
			states.add(new ProteinStamp(codes[18], ACIDIC, w, h, i));	// D
			states.add(new ProteinStamp(codes[19], BASIC, w, h, i));	// K
			states.add(new ProteinStamp(codes[20], BASIC, w, h, i));	// R
			states.add(new ProteinStamp(codes[21], STOP, w, h, i));		// .
			states.add(new ProteinStamp(codes[22], XXX, w, h, i));		// X
		}
	}

	public Image getImage(int data)
	{
		return states.get(data).getImage();
	}
}