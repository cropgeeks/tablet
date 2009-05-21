package tablet.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.data.*;

import scri.commons.gui.*;

/**
 * Table model class for displaying all the contigs within an assembly.
 */
class ContigTableModel extends AbstractTableModel
{
	private Assembly assembly;

	private JTable table;
	private String[] columnNames;

	ContigTableModel(Assembly assembly, JTable table)
	{
		this.assembly = assembly;
		this.table = table;

		String col1 = RB.getString("gui.ContigTableModel.col1");
		String col2 = RB.getString("gui.ContigTableModel.col2");
		String col3 = RB.getString("gui.ContigTableModel.col3");

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
		if (assembly == null)
			return 0;

		return assembly.contigCount();
	}

	public Class getColumnClass(int col)
	{
		if (col == 0)
			return Contig.class;
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
		}

		return null;
	}
}