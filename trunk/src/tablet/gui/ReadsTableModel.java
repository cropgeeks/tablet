// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.data.*;

class ReadsTableModel extends AbstractTableModel
{
	private List<Read> reads = new ArrayList<>();
	private String[] columnNames;

	ReadsTableModel()
	{
		String col1 = RB.getString("gui.ReadsTableModel.name");
		String col2 = RB.getString("gui.ReadsTableModel.position");
		String col3 = RB.getString("gui.ReadsTableModel.length");

		columnNames = new String[] { col1, col2, col3 };
	}

	void setReads(List<Read> reads)
	{
		this.reads = reads;
//		fireTableChanged(new TableModelEvent(this));
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col)
		{ return columnNames[col]; }

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		return reads != null ? reads.size() : 0;
	}

	public Object getValueAt(int row, int col)
	{
		Read read = reads.get(row);
		switch (col)
		{
			case 0:	return Assembly.getReadName(read);
			case 1: return read.s() + 1;
			case 2: return read.length();
		}
		return null;
	}

	Read getRead(int row)
		{ return reads.get(row); }

	void clear()
	{
		reads.clear();
		fireTableDataChanged();
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;
		else if (col == 1 || col == 2)
			return Integer.class;
		else
			return null;
	}

	static TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 1: return new NumberFormatCellRenderer();
			case 2: return new NumberFormatCellRenderer();

			default: return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex)
		{ return false; }
}