package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

class ContigPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;
	private JTabbedPane ctrlTabs;

	private ContigTableModel model;
	private JTable table;

	ContigPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
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

	String getTitle(Assembly assembly)
	{
		if (assembly != null)
			return RB.format("gui.ContigPanel.title", assembly.contigCount());
		else
			return RB.format("gui.ContigPanel.title", 0);
	}

	void setAssembly(Assembly assembly)
	{
		table.setModel(new DefaultTableModel());

		if (assembly == null)
		{
			setBorder(BorderFactory.createTitledBorder("Contigs:"));
			return;
		}

		model = new ContigTableModel(assembly, table);

		table.setModel(model);
		table.setRowSorter(new TableRowSorter<ContigTableModel>(model));

		ctrlTabs.setTitleAt(0, getTitle(assembly));
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		int row = table.getSelectedRow();

		if (row == -1)
		{
			// TODO: Update winMain with blank RHS split pane instead
			aPanel.setContig(null);
		}
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			aPanel.setContig((Contig) model.getValueAt(row, 0));
		}
	}
}