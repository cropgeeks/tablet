package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class ReadGroupScheme extends EnhancedScheme
{
	// One ArrayList of ColorStamp objects for each ReadGroup
	private ArrayList<ArrayList<ColorStamp>> statesRG;

	// A reference to the list of sample names held in Assembly
	private static ArrayList<String> readGroups;
	// Which are mirrored in the ColorInfo objects; one per sample name
	private static ColorInfo[] colorInfos;

	public ReadGroupScheme(int w, int h)
	{
		super(w, h, true, false);

		statesRG = new ArrayList<ArrayList<ColorStamp>>();

		getColourInfos();

		// Initialize a set of "grey" stamps for reads that don't have a sample
		// name associated (ReadMetaData.readGroup == 0)
		initStates(Color.LIGHT_GRAY, w, h);

		for (short i=0; i < colorInfos.length; i++)
			if (colorInfos[i].enabled)
				initStates(colorInfos[i].color, w, h);
			else
				initStates(getGreyScale(colorInfos[i].color), w, h);
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

	private Color getGreyScale(Color color)
	{
		// Cheap and simple conversion - average of the three colours
//		int gs = (int) ((color.getRed()+color.getGreen()+color.getBlue())/3);

		// Luminance conversion - reflects human vision better (apparently)
		int gs = (int) (0.3*color.getRed()+0.59*color.getGreen()+0.11*color.getBlue());

		return new Color(gs, gs, gs);


		// For future reference: color-convert op that modifies an existing
		// image and changes it to greyscale - SLOW

//		ColorConvertOp op = new ColorConvertOp(
//			ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//		image = op.filter(image, null);
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

	private static void setupColors()
	{
		// For each sample name, find (or create) its colour
		for (int i=0; i < colorInfos.length; i++)
		{
			// Attempt to load color for read group from prefs file
			Color color = ColorPrefs.getColor("Sample_" + readGroups.get(i));

			if (color != null)
				colorInfos[i] = new ColorInfo(color, readGroups.get(i));

			// If colour doesn't exist assign a color and set this in the prefs file
			else
			{
				colorInfos[i] = new ColorInfo(WebsafePalette.getColor(i),
					readGroups.get(i));
				ColorPrefs.setColor("Sample_" + colorInfos[i].name, colorInfos[i].color);
			}
		}
	}

	// Update the color in the array, ColorInfo and the colour prefs xml.
	public static void setColor(int index, Color color)
	{
		ColorPrefs.setColor("Sample_" + colorInfos[index].name, color);
		colorInfos[index].color = color;
	}

	public static void resetColors()
	{
		// Remove the current set of colours from the preferences file
		for (ColorInfo info: colorInfos)
			ColorPrefs.removeColor("Sample_" + info.name);

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
		public boolean enabled;

		ColorInfo(Color colour, String name)
		{
			this.color = colour;
			this.name = name;

			enabled = true;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
