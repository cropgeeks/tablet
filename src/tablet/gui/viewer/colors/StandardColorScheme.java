package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class StandardColorScheme extends ColorScheme
{
	// Holds the states needed by the reads canvas
	private Vector<ColorState> statesRD = new Vector<ColorState>();
	// Holds the states needed by the consensus canvas
	private Vector<ColorState> statesCS = new Vector<ColorState>();

	public StandardColorScheme(Contig contig, int w, int h)
	{
		super(contig);

		// Sequence.NOTUSED
		statesRD.add(null);
		statesCS.add(null);


		// Sequence.UNKNOWN
		statesRD.add(new StandardColorState("?", Color.lightGray, w, h, true, false));
		statesCS.add(new StandardColorState("?", Color.lightGray, w, h, false, false));
		// Sequence.dUNKNOWN
		statesRD.add(new StandardColorState("?", Color.lightGray, w, h, true, true));
		statesCS.add(null);

		// Sequence.P
		statesRD.add(new StandardColorState("*", Color.lightGray, w, h, true, false));
		statesCS.add(new StandardColorState("*", Color.lightGray, w, h, false, false));
		// Sequence.dP
		statesRD.add(new StandardColorState("*", Color.lightGray, w, h, true, true));
		statesCS.add(null);

		// Sequence.A
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, false));
		statesCS.add(new StandardColorState("A", new Color(120, 255, 120), w, h, false, false));
		// Sequence.dA
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, true));
		statesCS.add(null);

		// Sequence.T
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, false));
		statesCS.add(new StandardColorState("T", new Color(120, 120, 255), w, h, false, false));
		// Sequence.dT
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, true));
		statesCS.add(null);

		// Sequence.C
		statesRD.add(new StandardColorState("C", new Color(255, 160, 120), w, h, true, false));
		statesCS.add(new StandardColorState("C", new Color(255, 160, 120), w, h, false, false));
		// Sequence.dC
		statesRD.add(new StandardColorState("C", new Color(255, 160, 120), w, h, true, true));
		statesCS.add(null);

		// Sequence.G
		statesRD.add(new StandardColorState("G", new Color(255, 120, 120), w, h, true, false));
		statesCS.add(new StandardColorState("G", new Color(255, 120, 120), w, h, false, false));
		// Sequence.dG
		statesRD.add(new StandardColorState("G", new Color(255, 120, 120), w, h, true, true));
		statesCS.add(null);

		// Sequence.N
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, false));
		statesCS.add(new StandardColorState("N", Color.lightGray, w, h, false, false));
		// Sequence.dN
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, true));
		statesCS.add(null);
	}

	public BufferedImage getImage(byte data)
	{
		return statesRD.get(data).getImage();
	}

	public BufferedImage getConsensusImage(byte data)
	{
		return statesCS.get(data).getImage();
	}

	public Color getColor(byte data)
	{
		return statesRD.get(data).getColor();
	}
}