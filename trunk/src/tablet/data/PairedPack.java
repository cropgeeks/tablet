// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

	public Read[] getPairForLink(int rowIndex, int colIndex)
	{
		// Are we over a valid row in the pack?
		if (rowIndex < 0 || rowIndex >= packRows.size())
			return null;

		// Are we over a link-line (test by looking at just one base)
		LineData lineData = getLineData(rowIndex, colIndex, colIndex);
        if (lineData.getIndexes()[0] != LineData.PAIRLINK)
            return null;

		PairedPackRow packRow = (PairedPackRow) packRows.get(rowIndex);

		return packRow.getPairForLinkPosition(colIndex);
	}
}