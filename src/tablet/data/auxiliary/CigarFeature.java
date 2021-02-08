// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

public class CigarFeature extends Feature
{
	private ArrayList<CigarEvent> events;

	public CigarFeature(String gffType, int p1, int p2)
	{
		super(gffType, "", p1, p2);
		events = new ArrayList<CigarEvent>();
	}

	public void addEvent(CigarEvent event)
	{
		events.add(event);
	}

	public int count()
	{
		return events.size();
	}

	public ArrayList<CigarEvent> getEvents()
	{
		return events;
	}
}