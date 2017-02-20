// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

public class ReadGroupScheme extends EnhancedScheme
{
	// One ArrayList of ColorStamp objects for each ReadGroup
	private ArrayList<ArrayList<ColorStamp>> statesRG;

	// A reference to the list of read groups held in Assembly
	private static ArrayList<ReadGroup> readGroups;
	// Which are mirrored in the ColorInfo objects; one per read group
	private static ColorInfo[] colorInfos;

	private static Color[] palette;

	static { setupPalette(); }

	public ReadGroupScheme(int w, int h)
	{
		super(w, h, true, false);

		Color cDisabled = ColorPrefs.get("User.ReadGroupScheme.Disabled");

		statesRG = new ArrayList<ArrayList<ColorStamp>>();

		getColourInfos();

		// Initialize a set of "grey" stamps for reads that don't have a sample
		// name associated (ReadMetaData.readGroup == 0)
		initStates(cDisabled, w, h);

		for (short i=0; i < colorInfos.length; i++)
			if (colorInfos[i].enabled)
				initStates(colorInfos[i].color, w, h);
			else
				initStates(cDisabled, w, h);
	}

	private void initStates(Color color, int w, int h)
	{
		ArrayList<ColorStamp> rgStamps = new ArrayList<>();

		for (String base: Sequence.getStates())
		{
			// Add the normal image
			rgStamps.add(new ColorStamp(base, color, w, h, true, false));
			// Add the delta image
			rgStamps.add(new ColorStamp(base, color, w, h, true, true));
		}

		// Add the stamps this read group's stamps as an emelment of statesRG
		statesRG.add(rgStamps);
	}

	@Override
	public Image getImage(ReadMetaData rmd, int index)
	{
		ArrayList<ColorStamp> stamps = statesRG.get(rmd.getReadGroup());
		return stamps.get(rmd.getStateAt(index)).getImage();
	}

	@Override
	public Color getColor(ReadMetaData rmd, int index)
	{
		ArrayList<ColorStamp> stamps = statesRG.get(rmd.getReadGroup());
		return stamps.get(rmd.getStateAt(index)).getColor();
	}

	@Override
	public Color getOverviewColor(ReadMetaData rmd, int index)
	{
		ArrayList<ColorStamp> stamps = statesRG.get(rmd.getReadGroup());
		return stamps.get(rmd.getStateAt(index)).getOverviewColor();
	}

	private static void setupPalette()
	{
		palette = new Color[20];

		palette[0] = new Color(182, 189, 112).darker();
		palette[1] = new Color(244, 233, 154).darker();
		palette[2] = new Color(222, 147, 136).darker();
		palette[3] = new Color(202, 184, 136).darker();
		palette[4] = new Color(174, 197, 223).darker();
		palette[5] = new Color(158, 192, 187).darker();
		palette[6] = new Color(241, 194, 132).darker();
		palette[7] = new Color(120, 144, 120).darker();
		palette[8] = new Color(168, 120, 120).darker();
		palette[9] = new Color(216, 216, 168).darker();
		palette[10] = new Color(230, 198, 163).darker();
		palette[11] = new Color(91, 171, 163).darker();
		palette[12] = new Color(153, 99, 131).darker();
		palette[13] = new Color(222, 171, 201).darker();
		palette[14] = new Color(106, 100, 209).darker();
		palette[15] = new Color(90, 189, 219).darker();
		palette[16] = new Color(222, 197, 207).darker();
		palette[17] = new Color(118, 222, 182).darker();
		palette[18] = new Color(219, 198, 189).darker();
		palette[19] = new Color(107, 86, 87).darker();
	}

	private static Color getPaletteColor(int index)
	{
		if (index < 0)
			index *= -1;

		return palette[index % palette.length];
	}

	private static void setupColors()
	{

		// For each read group name, find (or create) its colour
		for (int i=0; i < colorInfos.length; i++)
		{
			ReadGroup readGroup = readGroups.get(i);

			String id = readGroup.getID();
			String prefsID = "ReadGroup_" + id;

			// Attempt to load color for read group from prefs file
			Color color = ColorPrefs.get(prefsID);

			if (color != null)
				colorInfos[i] = new ColorInfo(color, readGroup);

			// If colour doesn't exist assign a color and set this in the prefs file
			else
			{
				colorInfos[i] = new ColorInfo(getPaletteColor(id.hashCode()), readGroup);
				ColorPrefs.setColor(prefsID, colorInfos[i].color);
			}
		}
	}

	// Update the color in the array, ColorInfo and the colour prefs xml.
	public static void setColor(int index, Color color)
	{
		ColorPrefs.setColor(
			"ReadGroup_" + colorInfos[index].readGroup.getID(), color);

		colorInfos[index].color = color;
	}

	public static void resetColors()
	{
		// Remove the current set of colours from the preferences file
		for (ColorInfo info: colorInfos)
			ColorPrefs.removeColor("ReadGroup_" + info.readGroup.getID());

		// And then let them get recreated
		setupColors();
	}

	public static ColorInfo[] getColourInfos()
	{
		if (readGroups != Assembly.getReadGroups())
		{
			readGroups = Assembly.getReadGroups();
			colorInfos = new ColorInfo[readGroups.size()];

			setupColors();
		}

		return colorInfos;
	}

	// For use in the JList in ReadGroupsPanelNB
	public static class ColorInfo
	{
		public Color color;
		public ReadGroup readGroup;
		public boolean enabled;

		ColorInfo(Color colour, ReadGroup readGroup)
		{
			this.color = colour;
			this.readGroup = readGroup;

			enabled = true;
		}
	}
}