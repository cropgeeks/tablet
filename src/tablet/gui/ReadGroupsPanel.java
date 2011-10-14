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
public class ReadGroupsPanel extends JPanel implements ActionListener, ListSelectionListener
{
	private ReadGroupsPanelNB controls;
	private ReadGroupsTableModel model;
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

		controls.table.getSelectionModel().addListSelectionListener(this);
	}

	private void createTableModel()
	{
		model = new ReadGroupsTableModel();

		sorter = new TableRowSorter<AbstractTableModel>(model);
		controls.table.setModel(model);
		controls.table.setRowSorter(sorter);

		controls.table.getColumnModel().getColumn(2).setPreferredWidth(25);
	}

	// Handle contig changes
	void setContig(Contig contig)
	{
		if (contig == null)
		{
			model.clear();
			controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel", "0"));
			controls.toggleComponentEnabled(false);
		}

		else
		{
			model.setColors();
			controls.readGroupLabel.setText(
				RB.format("gui.ReadGroupsPanelNB.readGroupLabel", model.getRowCount()));
			controls.toggleComponentEnabled(true);
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		int row = controls.table.getSelectedRow();

		if (row == -1)
			controls.clearLabels();

		else
		{
			row = controls.table.convertRowIndexToModel(row);
			ReadGroupScheme.ColorInfo info = model.getItem(row);

			controls.setLabels(info.record);
		}
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		ReadGroupScheme.ColorInfo info = model.getItem(row);
		return controls.displayToolTip(info.record);
	}

	// Display colour chooser to select colour for read group
	private void selectColor()
	{
		int row = controls.table.getSelectedRow();
		if (row == -1)
			return;

		// Convert the selection from a "sorter" to "model" index
		row = sorter.convertRowIndexToModel(row);

		ReadGroupScheme.ColorInfo info = model.getItem(row);

		// Display colour chooser dialog (defaulting to the current colour)
		Color newColor = JColorChooser.showDialog(Tablet.winMain,
			RB.getString("gui.ReadsGroupsPanel.colourChooser"), info.color);

		// If a colour was chosen, set that colour in the ReadGroupScheme
		if (newColor != null)
		{
			ReadGroupScheme.setColor(row, newColor);

			Tablet.winMain.getAssemblyPanel().forceRedraw();
			model.fireTableDataChanged();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Select all entries in the table...
		if (e.getSource() == controls.colorAll)
		{
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(true, i, 1);
		}

		// Select no entries in the table...
		else if (e.getSource() == controls.colorNone)
		{
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(false, i, 1);
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
			model.fireTableDataChanged();
		}

		Tablet.winMain.getAssemblyPanel().forceRedraw();
	}
}
