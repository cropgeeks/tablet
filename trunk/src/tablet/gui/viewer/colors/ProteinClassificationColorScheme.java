package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;

public class ProteinClassificationColorScheme extends ColorScheme
{
	private ArrayList<ColorState> states = new ArrayList<ColorState>();

	private final static Color NONPOLAR = new Color(255, 231, 95);
	private final static Color POLAR = new Color(179, 222, 192);
	private final static Color BASIC = new Color(187, 191, 224);
	private final static Color ACIDIC = new Color(248, 183, 211);
	private final static Color STOP = new Color(255, 105, 105);

	public ProteinClassificationColorScheme(int w, int h)
	{
		super();

		// VERY IMPORTANT: These MUST be in the same order as the codes within
		// the string array from the ProteinTranslator

		String[] codes = ProteinTranslator.codes;

		states.add(new StandardColorState("", Color.white, w, h, false, false));

		for (int i = 0; i < 3; i++)
		{
			// The 21 proteins as images WITH text
			states.add(new ProteinClassificationColorState(codes[1],  NONPOLAR, w, h, i));	// I
			states.add(new ProteinClassificationColorState(codes[2],  NONPOLAR, w, h, i));	// L
			states.add(new ProteinClassificationColorState(codes[3],  NONPOLAR, w, h, i));	// V
			states.add(new ProteinClassificationColorState(codes[4],  NONPOLAR, w, h, i));	// F
			states.add(new ProteinClassificationColorState(codes[5],  NONPOLAR, w, h, i));	// M
			states.add(new ProteinClassificationColorState(codes[6],  POLAR, w, h, i));		// C
			states.add(new ProteinClassificationColorState(codes[7],  NONPOLAR, w, h, i));	// A
			states.add(new ProteinClassificationColorState(codes[8],  POLAR, w, h, i));		// G
			states.add(new ProteinClassificationColorState(codes[9],  NONPOLAR, w, h, i));	// P
			states.add(new ProteinClassificationColorState(codes[10], POLAR, w, h, i));		// T
			states.add(new ProteinClassificationColorState(codes[11], POLAR, w, h, i));		// S
			states.add(new ProteinClassificationColorState(codes[12], POLAR, w, h, i));		// Y
			states.add(new ProteinClassificationColorState(codes[13], NONPOLAR, w, h, i));	// W
			states.add(new ProteinClassificationColorState(codes[14], POLAR, w, h, i));		// Q
			states.add(new ProteinClassificationColorState(codes[15], POLAR, w, h, i));		// N
			states.add(new ProteinClassificationColorState(codes[16], BASIC, w, h, i));		// H
			states.add(new ProteinClassificationColorState(codes[17], ACIDIC, w, h, i));	// E
			states.add(new ProteinClassificationColorState(codes[18], ACIDIC, w, h, i));	// D
			states.add(new ProteinClassificationColorState(codes[19], BASIC, w, h, i));		// K
			states.add(new ProteinClassificationColorState(codes[20], BASIC, w, h, i));		// R
			states.add(new ProteinClassificationColorState("\u25AA", STOP, w, h, i));		// . (codes[21] overridden with a nicer unicode "stop")
		}
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