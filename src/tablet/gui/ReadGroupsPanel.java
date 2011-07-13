package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

import scri.commons.gui.*;

/**
 * Table control class for the Read Groups panel.
 */
public class ReadGroupsPanel extends JPanel implements ActionListener
{
	private ReadGroupsPanelNB controls;
	private ReadGroupsTableModel tableModel;
	private TableRowSorter<AbstractTableModel> sorter;

	ReadGroupsPanel()
	{
		setLayout(new BorderLayout());
		add(controls = new ReadGroupsPanelNB(this));

		createTableModel();

		controls.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
					selectColor();
			}
		});
	}

	private void createTableModel()
	{
		tableModel = new ReadGroupsTableModel();

		sorter = new TableRowSorter<AbstractTableModel>(tableModel);
		controls.table.setModel(tableModel);
		controls.table.setRowSorter(sorter);

		controls.table.getColumnModel().getColumn(1).setPreferredWidth(15);
	}

	// Handle contig changes
	void setContig(Contig contig)
	{
		if (contig == null)
		{
			tableModel.clear();
			controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel", "0"));
			controls.toggleComponentEnabled(false);
		}

		else
		{
			tableModel.setColors();
			controls.readGroupLabel.setText(
				RB.format("gui.ReadGroupsPanelNB.readGroupLabel", tableModel.getRowCount()));
			controls.toggleComponentEnabled(true);
		}
	}

	// Display colour chooser to select colour for read group
	private void selectColor()
	{
		int row = controls.table.getSelectedRow();
		if (row == -1)
			return;

		// Convert the selection from a "sorter" to "model" index
		row = sorter.convertRowIndexToModel(row);

		ReadGroupScheme.ColorInfo info =
			(ReadGroupScheme.ColorInfo) tableModel.getValueAt(row, 0);

		// Display colour chooser dialog (defaulting to the current colour)
		Color newColor = JColorChooser.showDialog(Tablet.winMain,
			RB.getString("gui.ReadsGroupsPanel.colourChooser"), info.color);

		// If a colour was chosen, set that colour in the ReadGroupScheme
		if (newColor != null)
		{
			ReadGroupScheme.setColor(row, newColor);

			Tablet.winMain.getAssemblyPanel().forceRedraw();
			tableModel.fireTableDataChanged();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Select all entries in the table...
		if (e.getSource() == controls.colorAll)
		{
			for (int i = 0; i < tableModel.getRowCount(); i++)
				tableModel.setValueAt(true, i, 1);
		}

		// Select no entries in the table...
		else if (e.getSource() == controls.colorNone)
		{
			for (int i = 0; i < tableModel.getRowCount(); i++)
				tableModel.setValueAt(false, i, 1);
		}

		// Reset the colours to their defaults...
		else if (e.getSource() == controls.reset)
		{
			String msg = RB.getString("gui.ReadsGroupsPanel.confirmReset");
			String[] options = new String[] {
				RB.getString("gui.ReadGroupsPanelNB.reset"),
				RB.getString("gui.text.cancel")
			};

			if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
				return;

			ReadGroupScheme.resetColors();
			tableModel.fireTableDataChanged();
		}

		Tablet.winMain.getAssemblyPanel().forceRedraw();
	}
}