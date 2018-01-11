// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

import scri.commons.gui.*;

/**
 * Table model class for displaying all the contigs within an assembly.
 */
class FeaturesTableModel extends AbstractTableModel
{
	private Contig contig;

	private ArrayList<Feature> features = new ArrayList<>();

	private String[] columnNames;

	FeaturesTableModel()
	{
		String col1 = RB.getString("gui.FeaturesTableModel.col1");
		String col2 = RB.getString("gui.FeaturesTableModel.col2");
		String col3 = RB.getString("gui.FeaturesTableModel.col3");
		String col4 = RB.getString("gui.FeaturesTableModel.col4");

		columnNames = new String[] { col1, col2, col3, col4 };
	}

	void setContig(Contig contig)
	{
		this.contig = contig;
		features = contig.getFeatures();

		fireTableChanged(new TableModelEvent(this));
	}

	void clear()
	{
		features = new ArrayList<Feature>();

		fireTableChanged(new TableModelEvent(this));
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
		}

		return null;
	}

	Feature getFeature(int row)
		{ return features.get(row); }

	int indexOf(Feature feature)
	{
		return features.indexOf(feature);
	}

	private Contig getContig()
		{ return contig; }

	static class FeaturesTableRenderer extends NumberFormatCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSel, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSel, hasFocus,
				row, column);

			Contig contig = ((FeaturesTableModel)table.getModel()).getContig();

			setForeground(isSel ? Color.white : Color.black);
			int pos = (Integer) value;

			// Invalid if the value is lt or gt than the canvas
			if (Prefs.guiFeaturesArePadded)
			{
				if (pos < contig.getDataStart() || pos > contig.getDataEnd())
					setForeground(isSel ? TabletUtils.red2 : TabletUtils.red1);
			}
			// Invalid if the value is lt or gt than unpadded consensus length
			else
			{
				if (pos < 0 || pos >= contig.getConsensus().getUnpaddedLength())
					setForeground(isSel ? TabletUtils.red2 : TabletUtils.red1);
			}

			return this;
		}
	}
}