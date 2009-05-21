package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

class FeaturesPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;
	private JTabbedPane ctrlTabs;

	private FeaturesTableModel model;
	private JTable table;

	FeaturesPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);

		setLayout(new BorderLayout());
		add(new JScrollPane(table));
	}

	void setContig(Contig contig)
	{
		table.setModel(new DefaultTableModel());
		if (contig == null)
			return;

		model = new FeaturesTableModel(contig, table);

		table.setModel(model);
		table.setRowSorter(new TableRowSorter<FeaturesTableModel>(model));
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		int row = table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = (Feature) model.getValueAt(row, 9);

		aPanel.moveToPosition(-1, feature.getP1(), true);
	}
}