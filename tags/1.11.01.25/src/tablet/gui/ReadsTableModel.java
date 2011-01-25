package tablet.gui;

import java.awt.Component;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.data.*;

public class ReadsTableModel extends AbstractTableModel
{
	private List<Read> reads;

	private String[] columnNames;

	private static NumberFormatCellRenderer nfRenderer = new NumberFormatCellRenderer();

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

	static TableCellRenderer getCellRenderer(JTable table, int row, int col)
	{
		switch (col)
		{
			case 1: return nfRenderer;
			case 2: return nfRenderer;

			default: return null;
		}
	}

	// Custom cell renderer for showing the contig length column
	private static class NumberFormatCellRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			setText(TabletUtils.nf.format((Integer)value));

			setHorizontalAlignment(JLabel.RIGHT);

			return this;
		}
	}

	public boolean isCellEditable(int rowIndex, int mColIndex)
	{
		return false;
	}

	public void clear()
	{
		reads.clear();

		for (TableModelListener tml: getTableModelListeners())
			tml.tableChanged(new TableModelEvent(this));
	}

}
