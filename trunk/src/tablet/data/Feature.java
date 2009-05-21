package tablet.data;

import java.util.*;

/**
 * A feature is a region of interest from point p1 to p2 (inclusive) in
 * consensus space.
 */
public class Feature implements Comparable<Feature>
{
	public final static byte UNKNOWN = 0;
	public final static byte SNP = 1;

	protected byte type = UNKNOWN;

	protected int p1;
	protected int p2;

	public Feature(byte type, int p1, int p2)
	{
		this.type = type;
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

	public int getType()
		{ return type; }

	public int getP1()
		{ return p1; }

	public int getP2()
		{ return p2; }
}