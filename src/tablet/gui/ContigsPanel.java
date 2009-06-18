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
	private JTabbedPane ctrlTabs;

	private ContigsTableModel model;
	private JTable table;

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
		add(new JScrollPane(table));
	}

	void setFeaturesPanel(FeaturesPanel featuresPanel)
		{ this.featuresPanel = featuresPanel; }

	String getTitle()
	{
		return RB.format("gui.ContigsPanel.title", 0);
	}

	void setAssembly(Assembly assembly)
	{
		model = new ContigsTableModel(assembly, table);

		table.setModel(model);
		table.setRowSorter(new TableRowSorter<ContigsTableModel>(model));

		String title = RB.format("gui.ContigsPanel.title", assembly.contigCount());
		ctrlTabs.setTitleAt(0, title);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		Actions.resetActions();

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

			Actions.contigSelected();
			winMain.setAssemblyPanelVisible(true);
		}
	}
}