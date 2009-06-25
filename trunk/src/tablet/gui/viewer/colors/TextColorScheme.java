package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class TextColorScheme extends ColorScheme
{
	// Holds the states needed by the reads canvas
	private Vector<ColorState> statesRD = new Vector<ColorState>();
	// Holds the states needed by the consensus canvas
	private Vector<ColorState> statesCS = new Vector<ColorState>();

	public TextColorScheme(Contig contig, int w, int h)
	{
		super(contig);

		// Sequence.NOTUSED
		statesRD.add(null);
		statesCS.add(null);


		// Sequence.UNKNOWN
		statesRD.add(new TextState("?", w, h, true, false));
		statesCS.add(new TextState("?", w, h, false, false));
		// Sequence.dUNKNOWN
		statesRD.add(new TextState("?", w, h, true, true));
		statesCS.add(null);

		// Sequence.P
		statesRD.add(new TextState("*", w, h, true, false));
		statesCS.add(new TextState("*", w, h, false, false));
		// Sequence.dP
		statesRD.add(new TextState("*", w, h, true, true));
		statesCS.add(null);

		// Sequence.A
		statesRD.add(new TextState("A", w, h, true, false));
		statesCS.add(new TextState("A", w, h, false, false));
		// Sequence.dA
		statesRD.add(new TextState("A", w, h, true, true));
		statesCS.add(null);

		// Sequence.T
		statesRD.add(new TextState("T", w, h, true, false));
		statesCS.add(new TextState("T", w, h, false, false));
		// Sequence.dT
		statesRD.add(new TextState("T", w, h, true, true));
		statesCS.add(null);

		// Sequence.C
		statesRD.add(new TextState("C", w, h, true, false));
		statesCS.add(new TextState("C", w, h, false, false));
		// Sequence.dC
		statesRD.add(new TextState("C", w, h, true, true));
		statesCS.add(null);

		// Sequence.G
		statesRD.add(new TextState("G", w, h, true, false));
		statesCS.add(new TextState("G", w, h, false, false));
		// Sequence.dG
		statesRD.add(new TextState("G", w, h, true, true));
		statesCS.add(null);

		// Sequence.N
		statesRD.add(new TextState("N", w, h, true, false));
		statesCS.add(new TextState("N", w, h, false, false));
		// Sequence.dN
		statesRD.add(new TextState("N", w, h, true, true));
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