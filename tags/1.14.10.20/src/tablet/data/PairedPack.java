// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

public class PairedPack extends Pack
{
	public PairedPack()
	{
		super();
	}

	@Override
	public PackRow addNewRow()
	{
		PairedPackRow newRow = new PairedPackRow();
		packRows.add(newRow);

		return newRow;
	}

	public LineData getPixelData(int line, int start, int end, float scale, boolean getMetaData)
	{
		PairedPackRow packRow = (PairedPackRow) packRows.get(line);
		return packRow.getPixelData(start, end, scale, getMetaData);
	}

	public Read[] getPairForLink(int rowIndex, int colIndex)
	{
		// Are we over a valid row in the pack?
		if (rowIndex < 0 || rowIndex >= packRows.size())
			return null;

		PairedPackRow packRow = (PairedPackRow) packRows.get(rowIndex);
		return packRow.getPairForLinkPosition(colIndex);
	}
}