package av.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import av.data.*;

public class StandardColorScheme extends ColorScheme
{
	private Vector<ColorState> states = new Vector<ColorState>();

	public StandardColorScheme(Contig contig, int w, int h)
	{
		super(contig);

		// Sequence.NOTUSED
		states.add(new StandardColorState(" ", Color.lightGray, w, h, false));
		// Sequence.UNKNOWN
		states.add(new StandardColorState("?", Color.lightGray, w, h, false));

		// Sequence.P
		states.add(new StandardColorState("*", Color.lightGray, w, h, false));
		// Sequence.dP
		states.add(new StandardColorState("*", Color.lightGray, w, h, true));

		// Sequence.A
		states.add(new StandardColorState("A", new Color(120, 255, 120), w, h, false));
		// Sequence.dA
		states.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true));

		// Sequence.T
		states.add(new StandardColorState("T", new Color(120, 120, 255), w, h, false));
		// Sequence.dT
		states.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true));

		// Sequence.C
		states.add(new StandardColorState("C", new Color(255, 160, 120), w, h, false));
		// Sequence.dC
		states.add(new StandardColorState("C", new Color(255, 160, 120), w, h, true));

		// Sequence.G
		states.add(new StandardColorState("G", new Color(255, 120, 120), w, h, false));
		// Sequence.dG
		states.add(new StandardColorState("G", new Color(255, 120, 120), w, h, true));

		// Sequence.N
		states.add(new StandardColorState("N", Color.lightGray, w, h, false));
		// Sequence.dN
		states.add(new StandardColorState("N", Color.lightGray, w, h, false));
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