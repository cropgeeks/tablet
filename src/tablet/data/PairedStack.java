package tablet.data;

import java.util.*;

/**
 * Holds the list of PairedStack elements and is used for access to the reads
 * when utilising the Paired StackSet viewing mode.
 */
public class PairedStack implements IReadManager
{
	private ArrayList<ReadPair> stack = new ArrayList<ReadPair>();

	/**
	 * Get the values for the current screen of data so rendering can be carried
	 * out.
	 */
	public byte[] getValues(int line, int start, int end)
	{
		ReadPair readPair = stack.get(line);

		return readPair.getValues(start, end);
	}

	public int size()
	{
		return stack.size();
	}

	public Read getReadAt(int line, int nucleotidePosition)
	{
		if (line < 0 || line >= stack.size())
			return null;

		ReadPair readPair = stack.get(line);

		return readPair.getReadAt(nucleotidePosition);
	}

	/**
	 * Get the line of the display the given read can be found on.
	 */
	public int getLineForRead(Read read)
	{
		for(ReadPair readPair : stack)
		{
			Read found = readPair.getReadAt(read.getStartPosition());
			
			if(found != null && found.getID() == read.getID())
				return stack.indexOf(readPair);
		}
		return -1;
	}

	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		return null;
	}

	public void addPairedStack(ReadPair pairedStack)
	{
		stack.add(pairedStack);
	}

	/**
	 * Return the pair of reads that can be found at the given line (and near
	 * the given column) in the display.
	 */
	public Read[] getPairAtLine(int lineIndex, int colIndex)
	{
		if (lineIndex < 0 || lineIndex >= stack.size())
			return null;

		return stack.get(lineIndex).getPair(colIndex);
	}
}
