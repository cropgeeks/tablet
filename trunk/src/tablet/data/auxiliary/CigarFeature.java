package tablet.data.auxiliary;

import java.util.*;

public class CigarFeature extends Feature
{
	private ArrayList<CigarEvent> events;

	public CigarFeature(String gffType, String name, int p1, int p2)
	{
		super(gffType, name, p1, p2);
		events = new ArrayList<CigarEvent>();
	}

	public void addEvent(CigarEvent event)
	{
		events.add(event);
	}

	public int getCount()
	{
		return events.size();
	}

	public ArrayList<CigarEvent> getEvents()
	{
		return events;
	}
}