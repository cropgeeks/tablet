// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

import scri.commons.gui.*;

/**
 * Table model class for displaying all the contigs within an assembly.
 */
class FeaturesTableModel extends AbstractTableModel
{
	private FeaturesPanel panel;
	ArrayList<Feature> features;

	private String[] columnNames;

	FeaturesTableModel(FeaturesPanel panel, Contig contig)
	{
		this.panel = panel;
		features = contig.getFeatures();

		String col1 = RB.getString("gui.FeaturesTableModel.col1");
		String col2 = RB.getString("gui.FeaturesTableModel.col2");
		String col3 = RB.getString("gui.FeaturesTableModel.col3");
		String col4 = RB.getString("gui.FeaturesTableModel.col4");

		columnNames = new String[] { col1, col2, col3, col4 };
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
		if (col <= 1)
			return String.class;
		else
			return Integer.class;
	}

	public Object getValueAt(int row, int col)
	{
		Feature feature = features.get(row);

		switch (col)
		{
			case 0: return feature.getGFFType();
			case 1: return feature.getName();
			case 2: return feature.getDataPS()+1;  // +1 back into consensus space
			case 3: return feature.getDataPE()+1;  // +1 back into consensus space

			case 9: return feature;
		}

		return null;
	}
}