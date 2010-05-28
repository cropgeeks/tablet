// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.ArrayList;

/**
 * An interface for accessing read data that is required for visual display.
 */
public interface IReadManager
{
	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * for the given line between the points start and end.
	 */
	public byte[] getValues(int line, int start, int end);

	public int size();

	public Read getReadAt(int line, int nucleotidePosition);

	public int getLineForRead(Read read);

	public ArrayList<Read> getReadNames(int startIndex, int endIndex);
}