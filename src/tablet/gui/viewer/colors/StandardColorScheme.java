package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class StandardColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	public static int alpha = 0;

	// useEffects = true means alpha value will be applied to final image
	//   reads canvas: useEffects = true
	//   consensus canvas: useEfects = false
	public StandardColorScheme(Contig contig, int w, int h, boolean useEffects)
	{
		super(contig);

		// Sequence.NOTUSED
		states.add(new StandardColorState(" ", Color.lightGray, w, h, useEffects, false));

		// Sequence.UNKNOWN
		states.add(new StandardColorState("?", Color.lightGray, w, h, useEffects, false));
		// Sequence.dUNKNOWN
		states.add(new StandardColorState("?", Color.lightGray, w, h, useEffects, true));

		// Sequence.P
		states.add(new StandardColorState("*", Color.lightGray, w, h, useEffects, false));
		// Sequence.dP
		states.add(new StandardColorState("*", Color.lightGray, w, h, useEffects, true));

		// Sequence.A
		states.add(new StandardColorState("A", new Color(120, 255, 120), w, h, useEffects, false));
		// Sequence.dA
		states.add(new StandardColorState("A", new Color(120, 255, 120), w, h, useEffects, true));

		// Sequence.T
		states.add(new StandardColorState("T", new Color(120, 120, 255), w, h, useEffects, false));
		// Sequence.dT
		states.add(new StandardColorState("T", new Color(120, 120, 255), w, h, useEffects, true));

		// Sequence.C
		states.add(new StandardColorState("C", new Color(255, 160, 120), w, h, useEffects, false));
		// Sequence.dC
		states.add(new StandardColorState("C", new Color(255, 160, 120), w, h, useEffects, true));

		// Sequence.G
		states.add(new StandardColorState("G", new Color(255, 120, 120), w, h, useEffects, false));
		// Sequence.dG
		states.add(new StandardColorState("G", new Color(255, 120, 120), w, h, useEffects, true));

		// Sequence.N
		states.add(new StandardColorState("N", Color.lightGray, w, h, useEffects, false));
		// Sequence.dN
		states.add(new StandardColorState("N", Color.lightGray, w, h, useEffects, false));
	}

	public BufferedImage getImage(byte data)
	{
		return states.get(data).getImage();
	}

	public Color getColor(byte data)
	{
		return states.get(data).getColor();
	}
}