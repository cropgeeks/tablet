// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.text.NumberFormat;
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
			case 1: return contig.getConsensus().length();
			case 2: return contig.readCount();
			case 3: return contig.getFeatures().size();
			case 4: return contig.getMismatchPercentage();
		}

		return null;
	}
}