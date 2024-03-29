// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

public class EnzymeFeature extends Feature
{
	public static ArrayList<String> enzymes = new ArrayList<>();
	private int cutPoint;

	public EnzymeFeature(String gffType, String name, int p1, int p2, int cutPoint)
	{
		super(gffType, name, p1, p2);

		this.cutPoint = cutPoint;
	}

	public int getCutPoint()
		{ return cutPoint; }

	public void setCutPoint(int cutPoint)
		{ this.cutPoint = cutPoint; }

	public static ArrayList<String> getEnzymes()
		{ return enzymes; }
}