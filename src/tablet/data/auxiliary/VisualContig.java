// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

/**
 * The VisualContig stores data which relates to contigs, but is visual in nature.
 * Elements stored in the visual contig are likely to be user defined.
 */
public class VisualContig
{
	private Integer lockedBase = null;

	private ArrayList<FeatureTrack> tracks = new ArrayList<>();


	public void setLockedBase(Integer lockedBase)
		{	this.lockedBase = lockedBase;	}

	public Integer getLockedBase()
		{	return lockedBase;	}

	public int getTrackCount()
		{ return tracks.size(); }

	public void removeTracks()
	{
		tracks.clear();
	}

	public void addTrack(FeatureTrack track)
	{
		if(track.getName().equals("CIGAR-I"))
			tracks.add(0, track);
		else
			tracks.add(track);
	}

	public FeatureTrack getTrack(int index)
	{
		return tracks.get(index);
	}
}