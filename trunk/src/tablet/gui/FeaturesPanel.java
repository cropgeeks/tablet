// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class FeaturesPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;
	private ColumnHighlighter highlighter;

	private JTabbedPane ctrlTabs;
	private FeaturesTableModel model;
	private NBFeaturesPanelControls controls;

	private Contig contig;
	private Consensus consensus;
	private TableRowSorter<FeaturesTableModel> sorter;
	private FeaturesTableRenderer featuresRenderer = new FeaturesTableRenderer();

	FeaturesPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		setLayout(new BorderLayout());
		add(controls = new NBFeaturesPanelControls(this));

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection again
		controls.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processTableSelection();
			}
		});

		controls.table.setDefaultRenderer(Integer.class, featuresRenderer);

		controls.featuresLabel.setText(getTitle(0));

		toggleComponentEnabled(false);
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		// If a contig (in the main contig table) was de-selected or there are
		// no features to actually show, disable the tab
		if (contig == null || contig.getFeatures().size() == 0)
		{
			controls.featuresLabel.setText(getTitle(0));

			model = null;
			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);

			// Clear the reference if it's not going to be used
			consensus = null;
		}

		else
		{
			toggleComponentEnabled(true);
			controls.featuresLabel.setText(getTitle(contig.getFeatures().size()));
			model = new FeaturesTableModel(this, contig);
			sorter = new TableRowSorter<FeaturesTableModel>(model);
			controls.table.setModel(model);
			controls.table.setRowSorter(sorter);

			consensus = contig.getConsensus();
		}
	}

	String getTitle(int count)
	{
		return RB.format("gui.FeaturesPanel.title", count);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		processTableSelection();
	}

	private void processTableSelection()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = (Feature) model.getValueAt(row, 9);

		int start = feature.getDataPS();
		int end   = feature.getDataPE();

		// Override position if we're using unpadded values
		if (Prefs.guiFeaturesArePadded == false)
		{
			// Quick check in case the data isn't actually ready yet!
			if (DisplayData.hasUnpaddedToPadded() == false)
			{
				TaskDialog.info(RB.getString("gui.FeaturesPanel.unavailable"),
					RB.getString("gui.text.close"));

				controls.table.clearSelection();
				return;
			}

			start = DisplayData.unpaddedToPadded(start);
			end = DisplayData.unpaddedToPadded(end);
		}

		aPanel.moveToPosition(-1, start, true);
		highlighter = new ColumnHighlighter(aPanel, start, end);
	}

	void setTableFilter(RowFilter<FeaturesTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
	}

	public void nextFeature()
	{
		controls.nextFeature();
		ctrlTabs.setSelectedComponent(this);
	}

	public void prevFeature()
	{
		controls.prevFeature();
		ctrlTabs.setSelectedComponent(this);
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		controls.toggleComponentEnabled(enabled);
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = (Feature) model.getValueAt(row, 9);

		int p1 = feature.getDataPS();
		int p2 = feature.getDataPE();

		if (feature instanceof CigarFeature == false)
		{
			if (Prefs.guiFeaturesArePadded)
			{
				return RB.format("gui.FeaturesPanel.tooltip.padded",
					feature.getGFFType(), feature.getName(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					getUnpadded(p1), getUnpadded(p2));
			}
			else
			{
				return RB.format("gui.FeaturesPanel.tooltip.unpadded",
					feature.getGFFType(), feature.getName(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					getPadded(p1), getPadded(p2));
			}
		}
		else if(feature instanceof CigarFeature)
		{
			CigarFeature cigarFeature = (CigarFeature)feature;
			int count = cigarFeature.getCount();
			if (Prefs.guiFeaturesArePadded)
			{
				return RB.format("gui.FeaturesPanel.tooltip.padded.cigarFeature",
					feature.getGFFType(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					getUnpadded(p1), getUnpadded(p2), count);
			}
			else
			{
				return RB.format("gui.FeaturesPanel.tooltip.unpadded.cigarFeature",
					feature.getGFFType(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					getPadded(p1), getPadded(p2), count);
			}
		}

		return "";
	}

	private String getUnpadded(int base)
	{
		int unpadded = DisplayData.paddedToUnpadded(base);

		if (unpadded == -1)
			return "" + Sequence.PAD;
		else
			return TabletUtils.nf.format(unpadded+1);
	}

	private String getPadded(int base)
	{
		int padded = DisplayData.unpaddedToPadded(base);

		if (padded == -1)
			return "" + Sequence.PAD;
		else
			return TabletUtils.nf.format(padded+1);
	}

	private class FeaturesTableRenderer extends DefaultTableCellRenderer
	{
		private Color fg = UIManager.getColor("Table.foreground");

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			c.setForeground(fg);
			int pos = (Integer) value;

			// Invalid if the value is lt or gt than the canvas
			if (Prefs.guiFeaturesArePadded)
			{
				if (pos < contig.getDataStart() || pos > contig.getDataEnd())
					c.setForeground(Color.red);
			}
			// Invalid if the value is lt or gt than unpadded consensus length
			else
			{
				if (pos < 0 || pos >= contig.getConsensus().getUnpaddedLength())
					c.setForeground(Color.red);
			}

			return c;
		}
	}
}