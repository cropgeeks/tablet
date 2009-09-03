// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
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
	private JTabbedPane ctrlTabs;

	private ContigsTableModel model;
	private TableRowSorter<ContigsTableModel> sorter;
	private JTable table;
	private JScrollPane sp;

	ContigsPanel(WinMain winMain, AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.winMain = winMain;
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);

		setLayout(new BorderLayout());
		add(sp = new JScrollPane(table));
		add(controls = new NBContigsPanelControls(this), BorderLayout.SOUTH);
	}

	void setFeaturesPanel(FeaturesPanel featuresPanel)
		{ this.featuresPanel = featuresPanel; }

	String getTitle(int count)
	{
		return RB.format("gui.ContigsPanel.title", count);
	}

	void setTableFilter(RowFilter<ContigsTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
		ctrlTabs.setTitleAt(0, getTitle(table.getRowCount()));
	}

	void setAssembly(Assembly assembly)
	{
		if (assembly == null)
		{
			// This is done to ensure complete removal of all references that
			// might lead back to the Assembly object (and lots of memory)
			model = null;
			sorter = null;

			table.setModel(new DefaultTableModel());
			table.setRowSorter(null);
		}
		else
		{
			model = new ContigsTableModel(assembly, table);
			sorter = new TableRowSorter<ContigsTableModel>(model);

			table.setModel(model);
			table.setRowSorter(sorter);
			sp.getVerticalScrollBar().setValue(0);
		}

		String title = RB.format("gui.ContigsPanel.title", table.getRowCount());
		ctrlTabs.setTitleAt(0, title);
		ctrlTabs.setSelectedIndex(0);

		controls.clearFilter();
		controls.setEnabledState(assembly != null);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		Actions.openedNoContigSelected();

		int row = table.getSelectedRow();

		if (row == -1)
			setNullContig();
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			Contig contig = (Contig) model.getValueAt(row, 0);

			// Attempt to set the contig on the graphical components...
			if (aPanel.setContig(contig))
			{
				featuresPanel.setContig(contig);

				Actions.openedContigSelected();
				winMain.setAssemblyPanelVisible(true);
			}
			// ...but if the set failed, then act the same as a de-selection
			else
				setNullContig();
		}
	}

	private void setNullContig()
	{
		aPanel.setContig(null);
		featuresPanel.setContig(null);

		winMain.setAssemblyPanelVisible(false);
		winMain.getJumpToDialog().setVisible(false);
	}

	// Forces the panel to recreate the table then reselect the row
	void updateTable(Assembly assembly)
	{
		// TODO: Any way to do this without recreating the entire table because
		// that causes the user's custom sorting/filtering to be removed

		int row = table.getSelectedRow();
		if (row != -1)
			row = table.convertRowIndexToModel(row);

		setAssembly(assembly);

		if (row != -1)
			table.setRowSelectionInterval(row, row);
	}
}