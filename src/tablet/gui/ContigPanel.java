package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.*;

class ContigPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;

	private ContigTableModel model;
	private JTable table;

	ContigPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Contigs:"));
		add(new JScrollPane(table));
	}

	void setAssembly(Assembly assembly)
	{
		table.setModel(new DefaultTableModel());

		if (assembly == null)
			return;

		model = new ContigTableModel(assembly, table);

		table.setModel(model);
		table.setRowSorter(new TableRowSorter<ContigTableModel>(model));
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