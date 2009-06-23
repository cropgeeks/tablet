package tablet.gui;

import java.awt.*;
import java.awt.event.*;
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

	String getTitle()
	{
		return RB.format("gui.ContigsPanel.title", 0);
	}

	void setTableFilter(RowFilter<ContigsTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);

		String title = RB.format("gui.ContigsPanel.title", table.getRowCount());
		ctrlTabs.setTitleAt(0, title);
	}

	void setAssembly(Assembly assembly)
	{
		controls.clearFilter();

		if (assembly != null)
		{
			model = new ContigsTableModel(assembly, table);
			sorter = new TableRowSorter<ContigsTableModel>(model);

			table.setModel(model);
			table.setRowSorter(sorter);
			sp.getVerticalScrollBar().setValue(0);

			controls.setEnabledState(true);
		}
		else
		{
			table.setModel(new DefaultTableModel());
			table.setRowSorter(null);
			controls.setEnabledState(false);
		}

		String title = RB.format("gui.ContigsPanel.title", table.getRowCount());
		ctrlTabs.setTitleAt(0, title);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		Actions.openedNoContigSelected();

		int row = table.getSelectedRow();

		if (row == -1)
		{
			aPanel.setContig(null);
			featuresPanel.setContig(null);

			winMain.setAssemblyPanelVisible(false);
		}
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			Contig contig = (Contig) model.getValueAt(row, 0);
			aPanel.setContig(contig);
			featuresPanel.setContig(contig);

			Actions.openedContigSelected();
			winMain.setAssemblyPanelVisible(true);
		}
	}
}