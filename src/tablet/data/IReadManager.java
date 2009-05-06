package tablet.data;

public interface IReadManager
{
	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * for the given line between the points start and end.
	 */
	public byte[] getValues(int line, int start, int end);

	public int size();

	public Read getReadAt(int line, int nucleotidePosition);
}