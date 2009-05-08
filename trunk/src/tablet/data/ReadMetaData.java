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

	public ReadMetaData(String name, boolean isComplemented)
	{
		this.name = name;
		this.isComplemented = isComplemented;
	}

	public String getName()
		{ return name; }

	public boolean isComplemented()
		{ return isComplemented; }
}