package tablet.data;

public class PairedPack extends Pack
{
	public PairedPack()
	{
		super();
	}

	public void addPackRow(PairedPackRow packRow)
		{ packRows.add(packRow); }

	public LineData getLineData(int line, int start, int end)
	{
		PairedPackRow packRow = (PairedPackRow) packRows.get(line);
		return packRow.getLineData(start, end);
	}
	
	/**
	 * Return the pair of reads that can be found at the given line (and near
	 * the given column) in the display.
	 */
	public Read[] getPairAtLine(int lineIndex, int colIndex)
	{
		if (lineIndex < 0 || lineIndex >= packRows.size())
			return null;

		PairedPackRow packRow = (PairedPackRow) packRows.get(lineIndex);
		return packRow.getPair(colIndex);
	}
}
