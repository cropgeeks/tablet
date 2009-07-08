package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;

public class ProteinClassificationColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	private final static Color NONPOLAR = new Color(255, 231, 95);
	private final static Color POLAR = new Color(179, 222, 192);
	private final static Color BASIC = new Color(187, 191, 224);
	private final static Color ACIDIC = new Color(248, 183, 211);
	private final static Color STOP = new Color(105, 105, 105);

	public ProteinClassificationColorScheme(Contig contig, int w, int h)
	{
		super(contig);

		// VERY IMPORTANT: These MUST be in the same order as the codes within
		// the string array from the ProteinTranslator

		String[] codes = ProteinTranslator.codes;

		states.add(new StandardColorState("", Color.white, w, h, false, false));

		// The 21 proteins as images WITH text
		states.add(new StandardColorState(codes[1],  NONPOLAR, w, h, false, false));	// I
		states.add(new StandardColorState(codes[2],  NONPOLAR, w, h, false, false));	// L
		states.add(new StandardColorState(codes[3],  NONPOLAR, w, h, false, false));	// V
		states.add(new StandardColorState(codes[4],  NONPOLAR, w, h, false, false));	// F
		states.add(new StandardColorState(codes[5],  NONPOLAR, w, h, false, false));	// M
		states.add(new StandardColorState(codes[6],  POLAR, w, h, false, false));		// C
		states.add(new StandardColorState(codes[7],  NONPOLAR, w, h, false, false));	// A
		states.add(new StandardColorState(codes[8],  POLAR, w, h, false, false));		// G
		states.add(new StandardColorState(codes[9],  NONPOLAR, w, h, false, false));	// P
		states.add(new StandardColorState(codes[10], POLAR, w, h, false, false));		// T
		states.add(new StandardColorState(codes[11], POLAR, w, h, false, false));		// S
		states.add(new StandardColorState(codes[12], POLAR, w, h, false, false));		// Y
		states.add(new StandardColorState(codes[13], NONPOLAR, w, h, false, false));	// W
		states.add(new StandardColorState(codes[14], POLAR, w, h, false, false));		// Q
		states.add(new StandardColorState(codes[15], POLAR, w, h, false, false));		// N
		states.add(new StandardColorState(codes[16], BASIC, w, h, false, false));		// H
		states.add(new StandardColorState(codes[17], ACIDIC, w, h, false, false));		// E
		states.add(new StandardColorState(codes[18], ACIDIC, w, h, false, false));		// D
		states.add(new StandardColorState(codes[19], BASIC, w, h, false, false));		// K
		states.add(new StandardColorState(codes[20], BASIC, w, h, false, false));		// R
		states.add(new StandardColorState("\u00F8", STOP, w, h, false, false));			// . (codes[21] overridden with a nicer unicode "stop")

		// The 21 proteins as images WITHOUT text
		// We can reuse the same image for each group
		StandardColorState nonPolar = new StandardColorState("", NONPOLAR, w, h, false, false);
		StandardColorState polar = new StandardColorState("", POLAR, w, h, false, false);
		StandardColorState basic = new StandardColorState("", BASIC, w, h, false, false);
		StandardColorState acidic = new StandardColorState("", ACIDIC, w, h, false, false);
		StandardColorState stop = new StandardColorState("", STOP, w, h, false, false);

		states.add(nonPolar);
		states.add(nonPolar);
		states.add(nonPolar);
		states.add(nonPolar);
		states.add(nonPolar);
		states.add(polar);
		states.add(nonPolar);
		states.add(polar);
		states.add(nonPolar);
		states.add(polar);
		states.add(polar);
		states.add(polar);
		states.add(nonPolar);
		states.add(polar);
		states.add(polar);
		states.add(basic);
		states.add(acidic);
		states.add(acidic);
		states.add(basic);
		states.add(basic);
		states.add(stop);
	}

	public BufferedImage getImage(int data)
	{
		return states.get(data).getImage();
	}

	public BufferedImage getConsensusImage(int data)
	{
		return states.get(data).getImage();
	}

	public Color getColor(int data)
	{
		return states.get(data).getColor();
	}
}