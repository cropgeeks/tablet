package tablet.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.data.*;

import scri.commons.gui.*;

/**
 * Table model class for displaying all the contigs within an assembly.
 */
class FeaturesTableModel extends AbstractTableModel
{
	private Contig contig;
	private Vector<Feature> features;

	private JTable table;
	private String[] columnNames;

	FeaturesTableModel(Contig contig, JTable table)
	{
		this.contig = contig;
		this.table = table;

		features = contig.getFeatures();

		String col1 = RB.getString("gui.FeaturesTableModel.col1");
		String col2 = RB.getString("gui.FeaturesTableModel.col2");

		columnNames = new String[] { col1, col2 };
	}

	public String getColumnName(int col)
	{
	    return columnNames[col];
	}

	public int getColumnCount()
		{ return columnNames.length; }

	public int getRowCount()
	{
		return features.size();
	}

	public Class getColumnClass(int col)
	{
		return Integer.class;
	}

	public Object getValueAt(int row, int col)
	{
		Feature feature = features.get(row);

		switch (col)
		{
			case 0: return feature.getType();
			case 1: return feature.getP1() + 1; // +1 back into consensus space

			case 9: return feature;
		}

		return null;
	}
}