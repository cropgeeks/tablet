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
			ctrlTabs.setTitleAt(1, getTitle());
		}

		else
		{
			ctrlTabs.setEnabledAt(1, true);

			model = new FeaturesTableModel(contig, table);

			table.setModel(model);
			table.setRowSorter(new TableRowSorter<FeaturesTableModel>(model));

			String title = RB.format("gui.FeaturesPanel.title", contig.getFeatures().size());
			ctrlTabs.setTitleAt(1, title);
		}
	}

	String getTitle()
	{
		return RB.format("gui.FeaturesPanel.title", 0);
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

		int position = feature.getP1();
		position = position + contig.getConsensusOffset();

		aPanel.moveToPosition(-1, position, true);
		highlighter = new ColumnHighlighter(aPanel, position, highlighter);
	}
}