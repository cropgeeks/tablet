// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;
import static tablet.data.ReadMetaData.*;

public class ReadConcordanceScheme extends EnhancedScheme
{
	// {(Read1 + Reverse orientation) or (Read2 + forward)} = Red,
	// {(Read2 + Reverse orientation) or (Read1 + forward)} = Green.

	// The reds
	private ArrayList<ColorStamp> read1R = new ArrayList<>();
	private ArrayList<ColorStamp> read2F = new ArrayList<>();
	// The greens
	private ArrayList<ColorStamp> read1F = new ArrayList<>();
	private ArrayList<ColorStamp> read2R = new ArrayList<>();
	// Orphaned
	private ArrayList<ColorStamp> oprhps = new ArrayList<>();

	public ReadConcordanceScheme(int w, int h)
	{
		super(w, h, true, false);

		Color c_read1F = ColorPrefs.get("User.ConcordanceScheme.Read1Forward");
		Color c_read1R = ColorPrefs.get("User.ConcordanceScheme.Read1Reverse");
		Color c_read2F = ColorPrefs.get("User.ConcordanceScheme.Read2Forward");
		Color c_read2R = ColorPrefs.get("User.ConcordanceScheme.Read2Reverse");
		Color c_oprhps = ColorPrefs.get("User.ConcordanceScheme.Orphaned");

		initStates(read1F, c_read1F, w, h);
		initStates(read1R, c_read1R, w, h);
		initStates(read2F, c_read2F, w, h);
		initStates(read2R, c_read2R, w, h);
		initStates(oprhps, c_oprhps, w, h);
	}

	private void initStates(ArrayList<ColorStamp> states, Color c, int w, int h)
	{
		for (String base: Sequence.getStates())
		{
			// Add the normal image
			states.add(new ColorStamp(base, c, w, h, true, false));
			// Add the delta image
			states.add(new ColorStamp(base, c, w, h, true, true));
		}
	}

	private ColorStamp getStamp(ReadMetaData rmd, int index)
	{
		// Deal with unpaired data:
		if (rmd.getIsPaired() == false)
		{
			if (rmd.isComplemented())
				return read1R.get(rmd.getStateAt(index));
			else
				return read1F.get(rmd.getStateAt(index));
		}

		// Otherwise, it's paired-end:
		int read = 0; // 0=orphaned, 1=read1, 2=read2
		boolean isForwd = !rmd.isComplemented();

		switch (rmd.getPairedType())
		{
			case FIRSTINP:
			case DFRSTINP: read = 1; break;
			case SECNDINP:
			case DSCNDINP: read = 2; break;
		}

		if (read == 1 && isForwd)
			return read1F.get(rmd.getStateAt(index));
		else if (read == 1 && !isForwd)
			return read1R.get(rmd.getStateAt(index));
		else if (read == 2 && isForwd)
			return read2F.get(rmd.getStateAt(index));
		else if (read == 2 && !isForwd)
			return read2R.get(rmd.getStateAt(index));

		return oprhps.get(rmd.getStateAt(index));
	}

	public Image getImage(ReadMetaData rmd, int index)
	{
		return getStamp(rmd, index).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		return getStamp(rmd, index).getColor();
	}

	public Color getOverviewColor(ReadMetaData rmd, int index)
	{
		return getStamp(rmd, index).getOverviewColor();
	}
}