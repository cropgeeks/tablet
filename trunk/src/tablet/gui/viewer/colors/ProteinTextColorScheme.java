package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;

public class ProteinTextColorScheme extends ColorScheme
{
	private ArrayList<ColorState> states = new ArrayList<ColorState>();

	public ProteinTextColorScheme(int w, int h)
	{
		super();

		String[] codes = ProteinTranslator.codes;

		states.add(new StandardColorState("", Color.white, w, h, false, false));

		// The 21 proteins as images WITH text
		states.add(new TextState(codes[1],  w, h, false, false));	// I
		states.add(new TextState(codes[2],  w, h, false, false));	// L
		states.add(new TextState(codes[3],  w, h, false, false));	// V
		states.add(new TextState(codes[4],  w, h, false, false));	// F
		states.add(new TextState(codes[5],  w, h, false, false));	// M
		states.add(new TextState(codes[6],  w, h, false, false));	// C
		states.add(new TextState(codes[7],  w, h, false, false));	// A
		states.add(new TextState(codes[8],  w, h, false, false));	// G
		states.add(new TextState(codes[9],  w, h, false, false));	// P
		states.add(new TextState(codes[10], w, h, false, false));	// T
		states.add(new TextState(codes[11], w, h, false, false));	// S
		states.add(new TextState(codes[12], w, h, false, false));	// Y
		states.add(new TextState(codes[13], w, h, false, false));	// W
		states.add(new TextState(codes[14], w, h, false, false));	// Q
		states.add(new TextState(codes[15], w, h, false, false));	// N
		states.add(new TextState(codes[16], w, h, false, false));	// H
		states.add(new TextState(codes[17], w, h, false, false));	// E
		states.add(new TextState(codes[18], w, h, false, false));	// D
		states.add(new TextState(codes[19], w, h, false, false));	// K
		states.add(new TextState(codes[20], w, h, false, false));	// R
		states.add(new TextState("\u25AA", w, h, false, true));	// .

		// The 21 proteins as images WITHOUT text
		// We can reuse the same image for each group
		TextState empty = new TextState("", w, h, false, false);
		for (int i = 0; i < 42; i++)
			states.add(empty);
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