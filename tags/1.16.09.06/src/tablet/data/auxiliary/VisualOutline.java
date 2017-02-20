// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

public class VisualOutline
{
	public final static byte READ = 100;
	public final static byte ROW = 101;
	public final static byte COL = 102;

	public byte type;

	public int value1, value2, value3;

	public VisualOutline(byte type, int value1)
	{
		this.type = type;

		this.value1 = value1;
	}

	public VisualOutline(byte type, int value1, int value2, int value3)
	{
		this.type = type;

		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}
}