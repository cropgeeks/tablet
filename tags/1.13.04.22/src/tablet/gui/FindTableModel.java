// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.analysis.Finder.*;

/**
 * Class which provides the table model for the find reads table.
 */
public class FindTableModel extends AbstractTableModel
{
	private ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	private String[] columnNames = new String[0];

	void clear()
	{
		results.clear();

		fireTableChanged(new TableModelEvent(this));
	}

	void setResults(ArrayList<SearchResult> results)
	{
		this.results = results;

		String col1 = RB.getString("gui.FindTableModel.name");
		String col2 = RB.getString("gui.FindTableModel.contig");
		String col3 = RB.getString("gui.FindTableModel.position");
		String col4 = RB.getString("gui.FindTableModel.length");

		if(results != null && !results.isEmpty() && !(results.get(0) instanceof ReadSearchResult))
		{
			col1 = RB.getString("gui.FindTableModel.contig");
			col2 = RB.getString("gui.FindTableModel.position");
			col3 = RB.getString("gui.FindTableModel.length");
			col4 = RB.getString("gui.FindTableModel.direction");
		}

		columnNames = new String[] { col1, col2, col3, col4 };

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

	public Object getValueAt(int row, int col)
	{
		if (results.get(row) instanceof ReadSearchResult)
			return getReadSearchResultValue(row, col);

		else
			return getSearchResultValue(row, col);
	}

	private Object getSearchResultValue(int row, int col)
	{
		SearchResult result = results.get(row);
		switch (col)
		{
			case 0:		return result.getContig().getName();
			case 1:		return result.getPosition() + 1;
			case 2:		return result.getLength();
			case 3:		return result.isForward() ?
					RB.getString("gui.FindTableModel.forward")
					: RB.getString("gui.FindTableModel.reverse");
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
			case 1:		return result.getContig().getName();
			case 2:		return result.getPosition() + 1;
			case 3:		return result.getLength();
			case 9:		return result.getContig();
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int mColIndex)
		{ return false; }
}