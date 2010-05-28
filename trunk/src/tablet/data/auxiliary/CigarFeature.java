// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

public class CigarFeature extends Feature
{
	private int count;

	public CigarFeature(String gffType, String name, int p1, int p2, int count)
	{
		super(Feature.GFF3, gffType, name, p1, p2);
		this.count = count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getCount()
	{
		return count;
	}
}