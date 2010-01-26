// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

/**
 * A feature is a region of interest from point p1 to p2 (inclusive) in
 * consensus space (including offset positions).
 */
public class Feature implements Comparable<Feature>
{
	// Defined "Tablet Types" for supported featured
	public final static byte UNKNOWN = 0;
	public final static byte GFF3 = 1;
	public final static byte ROW_OUTLINE = 100;
	public final static byte COL_OUTLINE = 101;

	protected byte tabletType = UNKNOWN;

	// The GFF3 type of the feature
	protected String gffType;
	// The name of the feature (if it exists)
	protected String name;

	// Start and end position information
	protected int p1;
	protected int p2;

	public Feature(byte tabletType, String gffType, String name, int p1, int p2)
	{
		this.tabletType = tabletType;

		this.gffType = gffType;
		this.name = name;
		this.p1 = p1;
		this.p2 = p2;
	}

	public int compareTo(Feature other)
	{
		if (p1 < other.p1)
			return -1;

		else if (p1 == other.p1)
		{
			if (p2 < other.p2)
				return -1;
			else if (p2 == other.p2)
				return 0;
			else
				return 1;
		}

		else
			return 1;
	}

	public int getTabletType()
		{ return tabletType; }

	public String getGFFType()
		{ return gffType; }

	public String getName()
		{ return name; }

	public int getP1()
		{ return p1; }

	public int getP2()
		{ return p2; }
}