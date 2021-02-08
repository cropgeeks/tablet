// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.data.*;

import scri.commons.gui.*;

/**
 * Table model class for displaying all the contigs within an assembly.
 */
class ContigsTableModel extends AbstractTableModel
{
	private Assembly assembly;

	private JTable table;
	private String[] columnNames;

	ContigsTableModel(Assembly assembly, JTable table)
	{
		this.assembly = assembly;
		this.table = table;

		String col1 = RB.getString("gui.ContigsTableModel.col1");
		String col2 = RB.getString("gui.ContigsTableModel.col2");
		String col3 = RB.getString("gui.ContigsTableModel.col3");
		String col4 = RB.getString("gui.ContigsTableModel.col4");
		String col5 = RB.getString("gui.ContigsTableModel.col5");

		columnNames = new String[] { col1, col2, col3, col4, col5 };
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		return assembly.size();
	}

	public Class getColumnClass(int col)
	{
		if (col == 0)
			return Contig.class;
		else if(col == 4)
			return Float.class;
		else
			return Integer.class;
	}

	public Object getValueAt(int row, int col)
	{
		Contig contig = assembly.getContig(row);

		switch (col)
		{
			case 0: return contig;
			case 1: return contig.getTableData().consensusLength();
			case 2: return contig.getTableData().readCount();
			case 3: return contig.getTableData().featureCount();
			case 4: return contig.getTableData().mismatchPercentage();
		}

		return null;
	}

	static TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 1: return new LengthRenderer();
			case 2: return new NumberFormatCellRenderer();
			case 3: return new NumberFormatCellRenderer();
			case 4: return new MismatchRenderer();

			default: return null;
		}
	}

	private Assembly getAssembly()
		{ return assembly; }

	// Custom cell renderer for showing the contig length column
	private static class LengthRenderer extends NumberFormatCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Assembly assembly = ((ContigsTableModel)table.getModel()).getAssembly();
			Contig contig = assembly.getContig(row);

			// The consensus exists and has a length
			if (contig.getTableData().consensusDefined)
				setForeground(isSelected ? Color.white : Color.black);

			else
			{
				setForeground(isSelected ? TabletUtils.red2 : TabletUtils.red1);

				// The consensus doesn't exist, but we know its length (BAM),
				// then setText is called by the call to super() above

				// Else if consensus doesn't exist, nor do we know its length
				if (((Integer) value) <= 0)
					setText("?");
			}

			return this;
		}
	}

	// Custom cell renderer for showing the Mismatch (%) column
	private static class MismatchRenderer extends NumberFormatCellRenderer
	{
		MismatchRenderer()
		{
			nf.setMaximumFractionDigits(1);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Assembly assembly = ((ContigsTableModel)table.getModel()).getAssembly();
			Contig contig = assembly.getContig(row);

			if (contig.getTableData().readsDefined)
				setForeground(isSelected ? Color.white : Color.black);
			else
				setForeground(isSelected ? TabletUtils.red2 : TabletUtils.red1);

			if (value == null)
				setText("?");

			return this;
		}
	}
}