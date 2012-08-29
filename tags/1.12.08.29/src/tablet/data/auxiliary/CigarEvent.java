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