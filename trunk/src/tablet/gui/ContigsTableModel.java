// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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
	private static Assembly assembly;

	private JTable table;
	private String[] columnNames;

	// Custom renderers for the data
	private static LengthRenderer lengthRenderer = new LengthRenderer();
	private static ReadsRenderer readsRenderer = new ReadsRenderer();
	private static NumberFormatCellRenderer nfRenderer = new NumberFormatCellRenderer();
	private static MismatchRenderer mismatchRenderer = new MismatchRenderer();

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

	static TableCellRenderer getCellRenderer(JTable table, int row, int col)
	{
		switch (col)
		{
			case 1: return lengthRenderer;
			case 2: return readsRenderer;
			case 3: return nfRenderer;
			case 4: return mismatchRenderer;

			default: return null;
		}
	}

	// Custom cell renderer for showing the contig length column
	private static class LengthRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Contig contig = assembly.getContig(row);

			// The consensus exists and has a length
			if (contig.getTableData().consensusDefined)
			{
				setForeground(Color.black);
				setText(TabletUtils.nf.format((Integer)value));
			}
			else
			{
				setForeground(TabletUtils.nimbusRed);

				// The consensus doesn't exist, but we know its length (BAM)
				if (((Integer)value) > 0)
					setText(TabletUtils.nf.format((Integer)value));
				// The consensus doesn't exist, nor do we know its length
				else
					setText("?");
			}

			setHorizontalAlignment(JLabel.RIGHT);

			return this;
		}
	}

	// Custom cell renderer for showing the reads count column
	private static class ReadsRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Contig contig = assembly.getContig(row);

			if (contig.getTableData().readsDefined)
			{
				setForeground(Color.black);
				setText(TabletUtils.nf.format((Integer)value));
			}
			else
			{
				setForeground(TabletUtils.nimbusRed);

				if (((Integer)value) > 0)
					setText(TabletUtils.nf.format((Integer)value));
				else
					setText("?");
			}

			setHorizontalAlignment(JLabel.RIGHT);

			return this;
		}
	}

	// Custom cell renderer for showing the Mismatch (%) column
	private static class MismatchRenderer extends DefaultTableCellRenderer
	{
		private static final NumberFormat nf = NumberFormat.getInstance();

		MismatchRenderer()
		{
			nf.setMaximumFractionDigits(1);
			setHorizontalAlignment(JLabel.RIGHT);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Contig contig = assembly.getContig(row);

			if (contig.getTableData().readsDefined)
				setForeground(Color.black);
			else
				setForeground(TabletUtils.nimbusRed);

			if (value != null)
				setText(nf.format((Float)value));
			else
				setText("?");

			return this;
		}
	}
}