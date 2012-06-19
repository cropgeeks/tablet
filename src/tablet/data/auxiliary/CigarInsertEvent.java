package tablet.data.auxiliary;

import tablet.data.*;

public class CigarInsertEvent extends CigarEvent
{
	private String insertedBases;

	public CigarInsertEvent(Read read, String insertedBases)
	{
		super(read);
		this.insertedBases = insertedBases;
	}

	public String getInsertedBases()
		{ return insertedBases; }
}