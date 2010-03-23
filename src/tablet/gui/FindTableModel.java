package tablet.gui;

import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;
import scri.commons.gui.RB;
import tablet.analysis.Finder.*;
import tablet.data.*;

/**
 * Class which provides the table model for the find reads table.
 */
public class FindTableModel extends AbstractTableModel
{
	private LinkedList<SearchResult> results;

	private String[] columnNames;

	private FindPanel parent;

	FindTableModel(LinkedList<SearchResult> results, FindPanel parent)
	{
		this.results = results;
		this.parent = parent;

		String col1 = RB.getString("gui.FindTableModel.name");
		String col2 = RB.getString("gui.FindTableModel.position");
		String col3 = RB.getString("gui.FindTableModel.length");
		String col4 = RB.getString("gui.FindTableModel.contig");

		columnNames = new String[] { col1, col2, col3, col4 };

		if(results != null && results.get(0) instanceof SubsequenceSearchResult)
		{
			String col5 = "Start Index";
			String col6 = "End Index";
			columnNames = new String[] { col1, col2, col3, col4, col5, col6 };
		}
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		if(results != null)
			return results.size();
		else
			return 0;
	}

	public Class getColumnClass(int col)
	{
		if (col == 0 || col == 3)
			return String.class;
		else if(col == 1 || col == 2 || col == 6 || col == 7)
			return Integer.class;
		else if(col == 4)
			return Read.class;
		else if(col == 5)
			return Contig.class;
		else
			return null;
	}

	public Object getValueAt(int row, int col)
	{
		if(!(results.get(row) instanceof SubsequenceSearchResult))
			return getSearchResultValue(row, col);

		else
			return getSubsequenceSearchResultValue(row, col);
	}

	private Object getSearchResultValue(int row, int col)
	{
		SearchResult result = results.get(row);
		switch (col)
		{
			case 0:		return result.getName();
			case 1:		return result.getPosition() + 1;
			case 2:		return result.getLength();
			case 3:		return result.getContig().getName();
			case 9:		return result.getContig();
			case 10:	return parent.getFinder();
		}
		return null;
	}

	private Object getSubsequenceSearchResultValue(int row, int col)
	{
		SubsequenceSearchResult result = (SubsequenceSearchResult) results.get(row);
		switch (col)
		{
			case 0:		return result.getName();
			case 1:		return result.getPosition() + 1;
			case 2:		return result.getLength();
			case 3:		return result.getContig().getName();
			case 4:		return result.getStartIndex();
			case 5:		return result.getEndIndex();
			case 9:		return result.getContig();
			case 10:	return parent.getFinder();
		}
		return null;
	}
}