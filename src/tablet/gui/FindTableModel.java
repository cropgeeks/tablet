package tablet.gui;

import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

/**
 * Class which provides the table model for the find reads table.
 */
public class FindTableModel extends AbstractTableModel
{

	private LinkedList<FindPanel.SearchResult> results;

	private String[] columnNames;

	FindTableModel(LinkedList<FindPanel.SearchResult> results)
	{
		this.results = results;

		String col1 = "Name";
		String col2 = "Position";
		String col3 = "Length";
		String col4 = "Contig";

		columnNames = new String[] { col1, col2, col3, col4 };
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		return results.size();
	}

	public Class getColumnClass(int col)
	{
		if (col == 0 || col == 4)
			return String.class;
		else
			return Integer.class;
	}

	public Object getValueAt(int row, int col)
	{
		FindPanel.SearchResult result = results.get(row);

		switch (col)
		{
			case 0: return result.getReadMetaData().getName();
			case 1: return (result.getRead().getStartPosition() + 1);
			case 2: return result.getReadMetaData().calculateUnpaddedLength();
			case 3: return result.getContig().getName();
			case 4: return result.getRead();
			case 5: return result.getContig();
		}

		return null;
	}
}
