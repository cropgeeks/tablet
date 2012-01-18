// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.analysis.Finder.*;
import tablet.data.*;

/**
 * Class which provides the table model for the find reads table.
 */
public class FindTableModel extends AbstractTableModel
{
	private ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	private String[] columnNames = new String[0];

	FindTableModel()
	{
	}

	void clear()
	{
		results.clear();

		fireTableChanged(new TableModelEvent(this));
	}

	void setResults(ArrayList<SearchResult> results)
	{
		this.results = results;

		String col1 = RB.getString("gui.FindTableModel.name");
		String col2 = RB.getString("gui.FindTableModel.position");
		String col3 = RB.getString("gui.FindTableModel.length");
		String col4 = RB.getString("gui.FindTableModel.contig");

		if(results != null && !results.isEmpty() && results.get(0) instanceof SubsequenceSearchResult)
		{
			String col5 = RB.getString("gui.FindTableModel.start");
			String col6 = RB.getString("gui.FindTableModel.end");
			columnNames = new String[] { col1, col2, col3, col4, col5, col6 };
		}
		else if(results != null && !results.isEmpty() && !(results.get(0) instanceof SubsequenceSearchResult) && !(results.get(0) instanceof ReadSearchResult))
		{
			col1 = col4;
			columnNames = new String[] { col1, col2, col3 };
		}
		else
		{
			columnNames = new String[] { col1, col2, col3, col4 };
		}

		fireTableStructureChanged();
	}

	public String getColumnName(int col)
		{ return columnNames[col]; }

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
		if(!(results.get(row) instanceof SubsequenceSearchResult) && !(results.get(row) instanceof ReadSearchResult))
			return getSearchResultValue(row, col);

		else if(!(results.get(row) instanceof SubsequenceSearchResult))
			return getReadSearchResultValue(row, col);

		else
			return getSubsequenceSearchResultValue(row, col);
	}

	private Object getSearchResultValue(int row, int col)
	{
		SearchResult result = results.get(row);
		switch (col)
		{
			case 0:		return result.getContig().getName();
			case 1:		return result.getPosition() + 1;
			case 2:		return result.getLength();
			case 9:		return result.getContig();
		}
		return null;
	}

	private Object getReadSearchResultValue(int row, int col)
	{
		ReadSearchResult result = (ReadSearchResult) results.get(row);
		switch (col)
		{
			case 0:		return result.getName();
			case 1:		return result.getPosition() + 1;
			case 2:		return result.getLength();
			case 3:		return result.getContig().getName();
			case 9:		return result.getContig();
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
			case 4:		return result.getStartIndex()+1;
			case 5:		return result.getEndIndex()+1;
			case 9:		return result.getContig();
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int mColIndex)
		{ return false; }
}