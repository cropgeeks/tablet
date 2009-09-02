package tablet.data.auxiliary;

/**
 * A feature is a region of interest from point p1 to p2 (inclusive) in
 * consensus space (including offset positions).
 */
public class Feature implements Comparable<Feature>
{
	public final static byte UNKNOWN = 0;
	public final static byte GFF3 = 1;
	public final static byte ROW_OUTLINE = 100;
	public final static byte COL_OUTLINE = 101;

	protected String name;
	protected byte type = UNKNOWN;

	protected int p1;
	protected int p2;

	public Feature(String name, byte type, int p1, int p2)
	{
		this.name = name;
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

	public String getName()
		{ return name; }

	public int getType()
		{ return type; }

	public int getP1()
		{ return p1; }

	public int getP2()
		{ return p2; }
}