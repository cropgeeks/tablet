// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ColorPrefs
{
	private static HashMap<String,Color> colors = new HashMap<>();

	private static File file;
	private static Properties p = new Properties();

	/**
	 * Returns a color (via a name key) from the database.
	 */
	public static Color get(String key)
	{
		try
		{
			return colors.get(key);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static HashMap<String,Color> getColors()
	{
		return colors;
	}

	public static void resetUserColors()
	{
		// Search (and clear) all colours beginning with "User." from the hash
		ArrayList<String> toRemove = new ArrayList<>();
		for (String key: colors.keySet())
			if (key.startsWith("User."))
				toRemove.add(key);
		for (String key: toRemove)
			colors.remove(key);

		// Then rebuild them
		initializeColors();
	}

	private static void initializeColors()
	{
		// ENHANCED scheme
		initColor("User.EnhancedScheme.?", Color.lightGray);
		initColor("User.EnhancedScheme.PAD", Color.lightGray);
		initColor("User.EnhancedScheme.N", Color.lightGray);
		initColor("User.EnhancedScheme.A", new Color(120, 255, 120));
		initColor("User.EnhancedScheme.C", new Color(255, 160, 120));
		initColor("User.EnhancedScheme.G", new Color(255, 120, 120));
		initColor("User.EnhancedScheme.T", new Color(120, 120, 255));

		// READ LENGTH scheme
		initColor("User.ReadLengthScheme.50",  new Color(120, 255, 120));
		initColor("User.ReadLengthScheme.100", new Color(255, 160, 120));
		initColor("User.ReadLengthScheme.150", new Color(255, 120, 120));
		initColor("User.ReadLengthScheme.250", new Color(120, 120, 255));
		initColor("User.ReadLengthScheme.500", new Color(165, 200, 175));
		initColor("User.ReadLengthScheme.750", new Color(070, 116, 162));
		initColor("User.ReadLengthScheme.Other", new Color(116, 162, 070));

		// READ TYPE scheme
		initColor("User.ReadTypeScheme.Unpaired", new Color(255, 160, 120));
		initColor("User.ReadTypeScheme.FirstInP", new Color(120, 255, 120));
		initColor("User.ReadTypeScheme.SecndInP", new Color(120, 120, 255));
		initColor("User.ReadTypeScheme.Orphaned", new Color(255, 120, 120));
		initColor("User.ReadTypeScheme.FInPDiff", new Color(255, 255, 120));
		initColor("User.ReadTypeScheme.SInPDiff", new Color(255, 120, 255));

		// CONCORDANCE scheme
		initColor("User.ConcordanceScheme.Read1Forward", new Color(120, 255, 120));
		initColor("User.ConcordanceScheme.Read1Reverse", new Color(255, 120, 120));
		initColor("User.ConcordanceScheme.Read2Forward", new Color(255, 120, 120).darker());
		initColor("User.ConcordanceScheme.Read2Reverse", new Color(120, 255, 120).darker());
		initColor("User.ConcordanceScheme.Orphaned", Color.lightGray);

		// READ GROUP scheme
		initColor("User.ReadGroupScheme.Disabled", Color.LIGHT_GRAY);

		// DIRECTION scheme
		initColor("User.DirectionScheme.Forward", new Color(165, 200, 175));
		initColor("User.DirectionScheme.Reverse", new Color(70, 116, 162));

		// VARIANTS scheme
		initColor("User.VariantsScheme.Variant", new Color(255, 120, 120));
		initColor("User.VariantsScheme.Normal", Color.LIGHT_GRAY);

		// PROTEIN scheme
		initColor("User.ProteinScheme.NonPolar", new Color(255, 231, 95));
		initColor("User.ProteinScheme.Polar", new Color(179, 222, 192));
		initColor("User.ProteinScheme.Basic", new Color(187, 191, 224));
		initColor("User.ProteinScheme.Acidic", new Color(248, 183, 211));
		initColor("User.ProteinScheme.Stop", new Color(255, 105, 105));
		initColor("User.ProteinScheme.XXX", new Color(160, 160, 160));

		// Outlines a read when the mouse is over it
		initColor("User.OutlinerOverlay.ReadOutliner", new Color(169, 46, 34));
		// Outlines rows and columns
		initColor("User.OutlinerOverlay.RowColOutliner", new Color(10, 10, 100));

		// ScaleBar font color
		initColor("User.ScaleBar.Text", Color.red);

		// Font colour for rendering on top of nucleotides
		initColor("User.Protein.Text", Color.black);
		initColor("User.Nucleotides.Text", Color.black);
		initColor("User.Nucleotides.DeltaText", Color.red);

		// Overview window
		initColor("User.Overview.Outline", new Color(169, 46, 34));
		initColor("User.Overview.ReadHighlight", new Color(169, 46, 34));
	}


	/**
	 * Ensures the given color (via its key) is in the database, setting it to
	 * the supplied default if it's not.
	 */
	private static void initColor(String key, Color defaultColor)
	{
		Color color = get(key);

		if (color == null)
			colors.put(key, defaultColor);
	}

	// Helper methods used by the ReadGroupScheme for direct access to the DB
	////////////////////////////////////////////////////////////////////////////
	static void setColor(String key, Color color)
	{
		colors.put(key, color);
	}

	static void removeColor(String key)
	{
		colors.remove(key);
	}
	////////////////////////////////////////////////////////////////////////////

	public static void setFile(File colorFile)
	{
		file = colorFile;
	}

	public static void load()
		throws Exception
	{
		Properties p = new Properties();

		try
		{
			// Load in the color (strings) from the file
			BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(file));
			p.loadFromXML(in);
			in.close();
		}
		catch (Throwable t) {}

		// Assign them to the main hashmap
		for (Enumeration<?> keys = p.keys(); keys.hasMoreElements();)
		{
			try
			{
				String key = (String) keys.nextElement();
				String[] s = p.getProperty(key).split(",");

				Color c = new Color(
					Integer.parseInt(s[0]),
					Integer.parseInt(s[1]),
					Integer.parseInt(s[2]));

				colors.put(key, c);
			}
			catch (Exception e) {}
		}

		initializeColors();
	}

	public static void save()
	{
		try
		{
			Properties p = new Properties();

			// Dump the hashmap back into a properties object
			for (String key: colors.keySet())
			{
				Color c = colors.get(key);
				p.setProperty(key, c.getRed() + "," + c.getGreen() + "," + c.getBlue());
			}

			// Then stream it to disk
			BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(file));
			p.storeToXML(os, null);
			os.close();
		}
		catch (Throwable t) {}
	}
}