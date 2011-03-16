// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.Component;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.data.*;

public class ReadsTableModel extends AbstractTableModel
{
	private List<Read> reads;

	private String[] columnNames;

	ReadsTableModel(List<Read> reads)
	{
		this.reads = reads;

		String col1 = RB.getString("gui.ReadsTableModel.name");
		String col2 = RB.getString("gui.ReadsTableModel.position");
		String col3 = RB.getString("gui.ReadsTableModel.length");

		columnNames = new String[] { col1, col2, col3 };
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		if(reads != null)
			return reads.size();
		else
			return 0;
	}

	public Class getColumnClass(int col)
	{
		if (col == 0)
			return String.class;
		else if (col == 1 || col == 2)
			return Integer.class;
		else if (col == 3)
			return Read.class;
		else
			return null;
	}

	public Object getValueAt(int row, int col)
	{
		Read read = reads.get(row);
		switch (col)
		{
			case 0:	return Assembly.getReadName(read);
			case 1: return read.getStartPosition() + 1;
			case 2: return read.length();
			case 3: return read;
		}
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

	public boolean isCellEditable(int rowIndex, int mColIndex)
	{
		return false;
	}

	public void clear()
	{
		reads.clear();

		fireTableChanged(new TableModelEvent(this));
	}
}