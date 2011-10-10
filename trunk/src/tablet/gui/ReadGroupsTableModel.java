// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

import scri.commons.gui.*;

class ReadGroupsTableModel extends AbstractTableModel
{
	private ReadGroupScheme.ColorInfo[] colors;
	private String[] columnNames;

	ReadGroupsTableModel()
	{
		String col1 = RB.getString("gui.ReadsGroupTableModel.col1");
		String col2 = RB.getString("gui.ReadsGroupTableModel.col2");
		String col3 = RB.getString("gui.ReadsGroupTableModel.col3");


		columnNames = new String[] { col1, col2, col3 };

		clear();
	}

	void setColors()
	{
		colors = ReadGroupScheme.getColourInfos();
		fireTableDataChanged();
	}

	void clear()
	{
		colors = new ReadGroupScheme.ColorInfo[0];
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col)
		{ return columnNames[col]; }

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		return colors.length;
	}

	public Object getValueAt(int row, int col)
	{
		ReadGroupScheme.ColorInfo info = colors[row];

		switch (col)
		{
			case 0: return info;

			case 1:
				if (info.record.getPlatform() != null)
					return info.record.getPlatform();
				else
					return "";

			case 2:	return info.enabled;
		}

		return null;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		colors[row].enabled = (Boolean) value;

		Tablet.winMain.getAssemblyPanel().updateColorScheme();

		fireTableDataChanged();
	}

	@Override
	public Class getColumnClass(int col)
	{
		if (col == 0)
			return ReadGroupScheme.ColorInfo.class;
		else if (col == 1)
			return String.class;
		else
			return Boolean.class;
	}

	static TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 0: return new ColorListRenderer();
			default: return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int mColIndex)
		{ return mColIndex == 2; }

	static class ColorListRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			ReadGroupScheme.ColorInfo info = (ReadGroupScheme.ColorInfo) value;

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

			g.setColor(info.color);
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();

			setText(info.getDisplayName());
			setIcon(new ImageIcon(image));

			return this;
		}

		@Override
		public Insets getInsets(Insets i)
			{ return new Insets(0, 3, 0, 0); }
	}
}