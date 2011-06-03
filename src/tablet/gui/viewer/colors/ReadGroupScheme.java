package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class ReadGroupScheme extends EnhancedScheme
{
	// One ArrayList of ColorStamp objects for each ReadGroup
	private ArrayList<ArrayList<ColorStamp>> statesRG;

	private static ColorInfo[] colorInfos;

	public ReadGroupScheme(int w, int h)
	{
		super(w, h, true, false);

		statesRG = new ArrayList<ArrayList<ColorStamp>>();

		// Setup the colours then set up the ColorStamp arrays in initStates
		setupColors();

		for (short i=0; i < colorInfos.length; i++)
			initStates(colorInfos[i].color, w, h);
	}

	private void initStates(Color color, int w, int h)
	{
		ArrayList<ColorStamp> rgStamps = new ArrayList<ColorStamp>();

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

	private void setupColors()
	{
		ArrayList<String> readGroups = Assembly.getReadGroups();
		colorInfos = new ColorInfo[readGroups.size()];

		for (int i=0; i < colorInfos.length; i++)
		{
			// Attempt to load color for read group from prefs file
			Color color = ColorPrefs.getColor(readGroups.get(i));

			if (color != null)
				colorInfos[i] = new ColorInfo(color, readGroups.get(i));

			// If colour doesn't exist assign a color and set this in the prefs file
			else
			{
				colorInfos[i] = new ColorInfo(WebsafePalette.getColor(i),
					readGroups.get(i));
				ColorPrefs.setColor(colorInfos[i].name, colorInfos[i].color);
			}
		}
	}

	// Update the color in the array, ColorInfo and the colour prefs xml.
	public void setColor(int index, Color color)
	{
		ColorPrefs.setColor(colorInfos[index].name, color);
		colorInfos[index].color = color;
	}

	public static ColorInfo[] getColourInfos()
	{
		if (Assembly.getReadGroups().isEmpty())
			colorInfos = new ColorInfo[0];
		
		return colorInfos;
	}

	// A collection of 216 "web safe" colors
	private static class WebsafePalette
	{
		private static Color[] colors;

		static
		{
			int c = 0;
			colors = new Color[125];

			for (int r = 51; r < 256; r += 51)
				for (int g = 51; g < 256; g += 51)
					for (int b = 51; b < 256; b += 51)
					{
						colors[c] = new Color(r, g, b);
						c++;
					}
		}

		public static int getColorCount()
			{ return colors.length; }

		public static Color getColor(int index)
		{
			if (index < 0)
				index *= -1;

			return colors[index % colors.length].brighter();
		}
	}


	// For use in the JList in ReadGroupsPanelNB
	public static class ColorInfo
	{
		public Color color;
		public String name;

		ColorInfo(Color colour, String name)
		{
			this.color = colour;
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
