// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.scanner;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import scri.commons.gui.*;

public class ResultsTableModel extends DefaultTableModel
{
	private String[] columns = { "File", "Folder", "Size", "Date", "Contigs", "Reads", "Type", "Paired-end", "Compressed" };

	static NumberFormatCellRenderer nfRenderer = new NumberFormatCellRenderer();
	static SizeRenderer sizeRenderer = new SizeRenderer();
	static DateRenderer dateRenderer = new DateRenderer();

	ResultsTableModel()
	{
		for (String colName: columns)
			addColumn(colName);
	}

	public Class getColumnClass(int col)
	{
		switch (col)
		{
			case 0: return String.class;
			case 1: return String.class;
			case 2: return Long.class;
			case 3: return Long.class;
			case 4: return Integer.class;
			case 5: return Integer.class;
			case 6: return String.class;
			case 7: return Boolean.class;
			case 8: return Boolean.class;

			default: return Object.class;
		}
	}

	public boolean isCellEditable(int row, int col)
		{ return false; }

	public void addNewResult(File file, String type, Integer contigCount, Integer readCount, boolean isPaired, boolean isCompressed)
	{
		final Object[] data = new Object[] {
			file.getName(),
			file.getParent(),
			file.length(),
			file.lastModified(),
			contigCount,
			readCount,
			type,
			isPaired,
			isCompressed
		};

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addRow(data);
			}
		});
	}

	// Custom cell renderer for displaying right-aligned file sizes (in KB)
	static class SizeRenderer extends DefaultTableCellRenderer
	{
		NumberFormat nf = NumberFormat.getInstance();

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			long size = (Long) value;

			setText(nf.format(size / 1024) + " KB");
			setHorizontalAlignment(JLabel.RIGHT);

			return c;
		}
	}

	static class DateRenderer extends DefaultTableCellRenderer
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			long modified = (Long) value;

			setText(df.format(new Date(modified)));

			return c;
		}
	}
}