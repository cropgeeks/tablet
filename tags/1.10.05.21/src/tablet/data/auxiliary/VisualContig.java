package tablet.data.auxiliary;

/**
 * The VisualContig stores data which relates to contigs, but is visual in nature.
 * Elements stored in the visual contig are likely to be user defined.
 */
public class VisualContig
{
	private Integer lockedBase = null;

	public void setLockedBase(Integer lockedBase)
		{	this.lockedBase = lockedBase;	}

	public Integer getLockedBase()
		{	return lockedBase;	}
}
