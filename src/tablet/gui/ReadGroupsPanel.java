// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.colors.*;

import scri.commons.gui.*;

/**
 * Table control class for the Read Groups panel.
 */
public class ReadGroupsPanel extends JPanel implements ActionListener, ListSelectionListener
{
	private ReadGroupsPanelNB controls;
	private ReadGroupsLabelNB labels;
	private ReadGroupsTableModel model;
	private TableRowSorter<AbstractTableModel> sorter;

	private JMenuItem mClipboardData;

	ReadGroupsPanel()
	{
		setLayout(new BorderLayout());
		add(controls = new ReadGroupsPanelNB(this));

		labels = new ReadGroupsLabelNB();

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
		controls.table.addMouseListener(new TableMouseListener());
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

		processTableSelection();
	}

	private void processTableSelection()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
		{
			labels.clearLabels();

			remove(labels);
			validate();
		}

		else
		{
			row = sorter.convertRowIndexToModel(row);
			ReadGroupScheme.ColorInfo info = model.getItem(row);

			labels.setLabels(info.readGroup);

			add(labels, BorderLayout.SOUTH);
			validate();
		}
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = sorter.convertRowIndexToModel(row);

		ReadGroupScheme.ColorInfo info = model.getItem(row);
		return controls.displayToolTip(info.readGroup);
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

	private void copyToClipboard()
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");

		// Column headers
		for (int c = 0; c < 13; c++)
		{
			text.append(RB.getString("gui.ReadsGroupTableModel.col" + (c+1)));
			text.append(c < 12 ? "\t" : newline);
		}

		// Each row
		for (int r = 0; r < controls.table.getRowCount(); r++)
		{
			int row = sorter.convertRowIndexToModel(r);
			ReadGroup rg = model.getItem(row).readGroup;

			text.append(rg.getID() + "\t");
			text.append(rg.getCN() + "\t");
			text.append(rg.getDS() + "\t");
			text.append(rg.getDT() + "\t");
			text.append(rg.getFO() + "\t");
			text.append(rg.getKS() + "\t");
			text.append(rg.getLB() + "\t");
			text.append(rg.getPG() + "\t");
			text.append(rg.getPI() + "\t");
			text.append(rg.getPL() + "\t");
			text.append(rg.getPU() + "\t");
			text.append(rg.getSM() + "\t");

			Color c = model.getItem(row).color;
			text.append(c.getRed() + "," + c.getGreen() + "," + c.getBlue());
			text.append(newline);
		}

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
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

		else if (e.getSource() == mClipboardData)
			copyToClipboard();

		Tablet.winMain.getAssemblyPanel().forceRedraw();
	}

	private void displayMenu(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		controls.table.setRowSelectionInterval(row, row);

		JPopupMenu menu = new JPopupMenu();
		mClipboardData = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboardData, "gui.ReadsGroupsPanel.mClipboardData");
		mClipboardData.addActionListener(this);


		menu.add(mClipboardData);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private class TableMouseListener extends MouseInputAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (!e.isPopupTrigger())
				processTableSelection();
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}
	}
}
