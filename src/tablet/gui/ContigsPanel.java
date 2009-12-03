// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

class ContigsPanel extends JPanel implements ListSelectionListener
{
	private WinMain winMain;
	private AssemblyPanel aPanel;
	private FeaturesPanel featuresPanel;
	private NBContigsPanelControls controls;
	private FindPanel findPanel;
	private JTabbedPane ctrlTabs;

	private ContigsTableModel model;
	private TableRowSorter<ContigsTableModel> sorter;

	ContigsPanel(WinMain winMain, AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.winMain = winMain;
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		setLayout(new BorderLayout());
		add(controls = new NBContigsPanelControls(this));
	}

	void setFeaturesPanel(FeaturesPanel featuresPanel)
		{ this.featuresPanel = featuresPanel; }

	void setFindPanel(FindPanel findPanel)
	{
		this.findPanel = findPanel;
	}

	String getTitle(int count)
	{
		return RB.format("gui.ContigsPanel.title", count);
	}

	void setTableFilter(RowFilter<ContigsTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
		controls.contigsLabel.setText(getTitle(controls.table.getRowCount()));
	}

	void setAssembly(Assembly assembly)
	{
		setNullContig();

		if (assembly == null)
		{
			// This is done to ensure complete removal of all references that
			// might lead back to the Assembly object (and lots of memory)
			model = null;
			sorter = null;

			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);
			featuresPanel.toggleComponentEnabled(false);

		}
		else
		{
			model = new ContigsTableModel(assembly, controls.table);
			sorter = new TableRowSorter<ContigsTableModel>(model);

			controls.table.setModel(model);
			controls.table.setRowSorter(sorter);
			featuresPanel.toggleComponentEnabled(true);
		}

		String title = RB.format("gui.ContigsPanel.title", controls.table.getRowCount());
		controls.contigsLabel.setText(title);
		ctrlTabs.setSelectedIndex(0);

		controls.clearFilter();
		controls.setEnabledState(assembly != null);

		findPanel.setAssembly(assembly);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		Actions.openedNoContigSelected();

		int row = controls.table.getSelectedRow();

		if (row == -1)
			setNullContig();
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = controls.table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			Contig contig = (Contig) model.getValueAt(row, 0);
			setDisplayedContig(contig);
		}
	}

	void setDisplayedContig(Contig contig)
	{
		// Attempt to set the contig on the graphical components...
		if (aPanel.setContig(contig))
		{
			featuresPanel.setContig(contig);

			Actions.openedContigSelected();

			if(contig.getFeatures().size() == 0)
			{
				Actions.homeNavigateNextFeature.setEnabled(false);
				Actions.homeNavigatePrevFeature.setEnabled(false);
			}

			winMain.setAssemblyPanelVisible(true);
		}

		// If the set failed, then act the same as a table de-selection
		else
			setNullContig();
	}

	private void setNullContig()
	{
		aPanel.setContig(null);
		featuresPanel.setContig(null);
		findPanel.toggleComponentEnabled(true);

		winMain.setAssemblyPanelVisible(false);
		winMain.getJumpToDialog().setVisible(false);
	}

	// Forces the panel to recreate the table then reselect the row
	void updateTable(Assembly assembly)
	{
		// TODO: Any way to do this without recreating the entire table because
		// that causes the user's custom sorting/filtering to be removed

		int row = controls.table.getSelectedRow();
		if (row != -1)
			row = controls.table.convertRowIndexToModel(row);

		setAssembly(assembly);

		if (row != -1)
			controls.table.setRowSelectionInterval(row, row);
	}

	public JTable getTable()
	{
		return controls.table;
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		Contig contig = (Contig) model.getValueAt(row, 0);
		NumberFormat nf = TabletUtils.nf;

		return RB.format("gui.ContigsPanel.tooltip",
			contig.getName(),
			TabletUtils.nf.format(contig.getConsensus().length()),
			TabletUtils.nf.format(contig.readCount()),
			TabletUtils.nf.format(contig.getFeatures().size()));
	}
}