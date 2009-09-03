// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Represents and holds additional meta data about a read. Chances are this is
 * data that was cached elsewhere (eg on disk) because it uses too much memory
 * to store this meta data inside the actual Read class. It can be held here
 * because it isn't needed at all times and can be fetched from the cache as and
 * when it *is* needed.
 */
public class ReadMetaData
{
	private String name;

	// Is the read complemented or uncomplemented
	private boolean isComplemented;

	private int unpaddedLength;

	public ReadMetaData(String name, boolean isComplemented, int unpaddedLength)
	{
		this.name = name;
		this.isComplemented = isComplemented;
		this.unpaddedLength = unpaddedLength;
	}

	public String getName()
		{ return name; }

	public boolean isComplemented()
		{ return isComplemented; }

	public int getUnpaddedLength()
		{ return unpaddedLength; }
}