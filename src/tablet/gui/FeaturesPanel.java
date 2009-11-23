// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private TableRowSorter<FeaturesTableModel> sorter;

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

		//titlePanel = new TitlePanel3(getTitle(0));

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
			ctrlTabs.setEnabledAt(1, true);
			controls.featuresLabel.setText(getTitle(0));

			model = null;
			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);
		}

		else
		{
			ctrlTabs.setEnabledAt(1, true);
			toggleComponentEnabled(true);
			controls.featuresLabel.setText(getTitle(contig.getFeatures().size()));
			model = new FeaturesTableModel(contig);
			sorter = new TableRowSorter<FeaturesTableModel>(model);
			controls.table.setModel(model);
			controls.table.setRowSorter(sorter);
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

		int start = feature.getP1();
		int end   = feature.getP2();

		// Override position if we're using unpadded values
		if (Prefs.guiFeaturesArePadded == false)
		{
			start = DisplayData.unpaddedToPadded(start);
			end = DisplayData.unpaddedToPadded(end);
		}

		start = start + contig.getConsensusOffset();
		end = end + contig.getConsensusOffset();

		aPanel.moveToPosition(-1, start, true);
		highlighter = new ColumnHighlighter(aPanel, start, end);
	}

	void setTableFilter(RowFilter<FeaturesTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
		ctrlTabs.setTitleAt(1, getTitle(controls.table.getRowCount()));
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
		controls.table.setEnabled(enabled);
		controls.toggleComponentEnabled(enabled);
	}
}