// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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