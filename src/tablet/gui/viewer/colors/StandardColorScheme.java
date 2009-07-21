package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class StandardColorScheme extends ColorScheme
{
	// Holds the states needed by the reads canvas
	private ArrayList<ColorState> statesRD = new ArrayList<ColorState>();
	// Holds the states needed by the consensus canvas
	private ArrayList<ColorState> statesCS = new ArrayList<ColorState>();

	public StandardColorScheme(int w, int h)
	{
		super();

		// VERY IMPORTANT: These MUST be in the same order as the sequential
		// values within the data.Sequence class, eg, unknown, P, N, A, C, G, T

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

		// Sequence.N
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, false));
		statesCS.add(new StandardColorState("N", Color.lightGray, w, h, false, false));
		// Sequence.dN
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, true));
		statesCS.add(null);

		// Sequence.A
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, false));
		statesCS.add(new StandardColorState("A", new Color(120, 255, 120), w, h, false, false));
		// Sequence.dA
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, true));
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

		// Sequence.T
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, false));
		statesCS.add(new StandardColorState("T", new Color(120, 120, 255), w, h, false, false));
		// Sequence.dT
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, true));
		statesCS.add(null);
	}

	public BufferedImage getImage(int data)
	{
		return statesRD.get(data).getImage();
	}

	public BufferedImage getConsensusImage(int data)
	{
		return statesCS.get(data).getImage();
	}

	public Color getColor(int data)
	{
		return statesRD.get(data).getColor();
	}
}