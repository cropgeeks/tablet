// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ColorPrefs
{
	private static File file;
	private static Properties p = new Properties();

	static Color getColor(String key)
	{
		try
		{
			String[] s = p.getProperty(key).split(",");

			return new Color(
				Integer.parseInt(s[0]),
				Integer.parseInt(s[1]),
				Integer.parseInt(s[2]));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static void setColor(String key, Color color)
	{
		p.setProperty(key,
			color.getRed() + "," + color.getGreen() + "," + color.getBlue());
	}

	public static void setFile(File colorFile)
	{
		file = colorFile;
	}

	public static void removeColor(String key)
		{ p.remove(key); }

	public static void load()
		throws Exception
	{
		try
		{
			BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(file));
			p.loadFromXML(in);
			in.close();
		}
		catch (Throwable t) {}
	}

	public static void save()
	{
		try
		{
			BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(file));
			p.storeToXML(os, null);
			os.close();
		}
		catch (Throwable t) {}
	}
}