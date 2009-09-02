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

class FeaturesPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;
	private ColumnHighlighter highlighter;

	private JTabbedPane ctrlTabs;
	private FeaturesTableModel model;
	private JTable table;

	private Contig contig;

	FeaturesPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection again
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processTableSelection();
			}
		});

		setLayout(new BorderLayout());
		add(new JScrollPane(table));
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		// If a contig (in the main contig table) was de-selected or there are
		// no features to actually show, disable the tab
		if (contig == null || contig.getFeatures().size() == 0)
		{
			ctrlTabs.setEnabledAt(1, false);
			ctrlTabs.setTitleAt(1, getTitle(0));

			model = null;
			table.setModel(new DefaultTableModel());
			table.setRowSorter(null);
		}

		else
		{
			ctrlTabs.setEnabledAt(1, true);
			ctrlTabs.setTitleAt(1, getTitle(contig.getFeatures().size()));

			model = new FeaturesTableModel(contig, table);
			table.setModel(model);
			table.setRowSorter(new TableRowSorter<FeaturesTableModel>(model));
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
		int row = table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = (Feature) model.getValueAt(row, 9);

		int start = feature.getP1();
		int end   = feature.getP2();

		start = start + contig.getConsensusOffset();
		end = end + contig.getConsensusOffset();

		aPanel.moveToPosition(-1, start, true);
		highlighter = new ColumnHighlighter(aPanel, start, end);
	}
}