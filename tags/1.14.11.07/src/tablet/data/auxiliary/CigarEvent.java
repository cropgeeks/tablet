// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import tablet.data.*;

public class CigarEvent
{
	private Read read;

	public CigarEvent(Read read)
	{
		this.read = read;
	}

	public Read getRead()
		{ return read; }
}