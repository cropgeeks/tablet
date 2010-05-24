package tablet.data.auxiliary;

import java.util.*;

/**
 * The VisualContig stores data which relates to contigs, but is visual in nature.
 * Elements stored in the visual contig are likely to be user defined.
 */
public class VisualContig
{
	private Integer lockedBase = null;

	private ArrayList<FeatureTrack> tracks = new ArrayList<FeatureTrack>();


	public void setLockedBase(Integer lockedBase)
		{	this.lockedBase = lockedBase;	}

	public Integer getLockedBase()
		{	return lockedBase;	}

	public void removeTracks()
	{
		tracks.clear();
	}

	public void addTrack(FeatureTrack track)
	{
		tracks.add(track);
	}

	public FeatureTrack getTrack(int index)
	{
		return tracks.get(index);
	}
}

