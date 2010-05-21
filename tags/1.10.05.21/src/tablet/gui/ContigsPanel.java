// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import tablet.analysis.*;
import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.gui.dialog.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class ContigsPanel extends JPanel implements ListSelectionListener
{
	private WinMain winMain;
	private AssemblyPanel aPanel;
	private FeaturesPanel featuresPanel;
	private NBContigsPanelControls controls;
	private FindPanel findPanel;
	private JTabbedPane ctrlTabs;

	private ContigsTableModel model;
	private TableRowSorter<ContigsTableModel> sorter;

	private Contig prevContig = null;

	private DecimalFormatRenderer floatRenderer = new DecimalFormatRenderer();

	ContigsPanel(WinMain winMain, AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.winMain = winMain;
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		setLayout(new BorderLayout());
		add(controls = new NBContigsPanelControls(this));

		controls.table.addMouseListener(new TableMouseListener());
		controls.table.setDefaultRenderer(Float.class, floatRenderer);
	}

	void setFeaturesPanel(FeaturesPanel featuresPanel)
		{ this.featuresPanel = featuresPanel; }

	void setFindPanel(FindPanel findPanel)
	{
		this.findPanel = findPanel;
	}

	String getTitle(int count)
	{
		return RB.format("gui.ContigsPanel.title", count);
	}

	void setTableFilter(RowFilter<ContigsTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
		controls.contigsLabel.setText(getTitle(controls.table.getRowCount()));
	}

	void setAssembly(Assembly assembly)
	{
		setNullContig();

		if (assembly == null)
		{
			// This is done to ensure complete removal of all references that
			// might lead back to the Assembly object (and lots of memory)
			model = null;
			sorter = null;

			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);
			featuresPanel.toggleComponentEnabled(false);

		}
		else
		{
			model = new ContigsTableModel(assembly, controls.table);
			sorter = new TableRowSorter<ContigsTableModel>(model);

			controls.table.setModel(model);
			controls.table.setRowSorter(sorter);
			featuresPanel.toggleComponentEnabled(true);
		}

		String title = RB.format("gui.ContigsPanel.title", controls.table.getRowCount());
		controls.contigsLabel.setText(title);
		ctrlTabs.setSelectedIndex(0);

		controls.clearFilter();
		controls.setEnabledState(assembly != null);

		findPanel.setAssembly(assembly);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		Actions.openedNoContigSelected();
		TaskManager.cancelAll();

		int row = controls.table.getSelectedRow();

		if (row == -1)
		{
			setNullContig();
			prevContig = null;
		}
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = controls.table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			Contig contig = (Contig) model.getValueAt(row, 0);
			if(contig != prevContig)
			{
				setDisplayedContig(contig);
				prevContig = contig;
			}
		}
	}

	void setDisplayedContig(Contig contig)
	{
		// Attempt to set the contig on the graphical components...
		if (aPanel.setContig(contig))
		{
			featuresPanel.setContig(contig);

			Actions.openedContigSelected();

			if(contig.getFeatures().size() == 0)
			{
				Actions.navigateNextFeature.setEnabled(false);
				Actions.navigatePrevFeature.setEnabled(false);
			}

			winMain.setAssemblyPanelVisible(true);
		}

		// If the set failed, then act the same as a table de-selection
		else
			setNullContig();

		winMain.getJumpToDialog().setVisible(false);
	}

	public void setNullContig()
	{
		aPanel.setContig(null);
		featuresPanel.setContig(null);
		findPanel.toggleComponentEnabled(true);

		winMain.setAssemblyPanelVisible(false);
	}

	// Forces the panel to recreate the table then reselect the row
	void updateTable(Assembly assembly)
	{
		// TODO: Any way to do this without recreating the entire table because
		// that causes the user's custom sorting/filtering to be removed

		int row = controls.table.getSelectedRow();
		if (row != -1)
			row = controls.table.convertRowIndexToModel(row);

		setAssembly(assembly);

		if (row != -1)
			controls.table.setRowSelectionInterval(row, row);
	}

	public JTable getTable()
	{
		return controls.table;
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		Contig contig = (Contig) model.getValueAt(row, 0);
		NumberFormat nf = TabletUtils.nf;
		nf.setMaximumFractionDigits(1);

		return RB.format("gui.ContigsPanel.tooltip",
			contig.getName(),
			TabletUtils.nf.format(contig.getConsensus().length()),
			TabletUtils.nf.format(contig.readCount()),
			TabletUtils.nf.format(contig.getFeatures().size()),
			TabletUtils.nf.format(contig.getMismatchPercentage()));
	}

	private void copyTableToClipboard()
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");

		for (int i = 0; i < controls.table.getRowCount(); i++)
		{
			int row = controls.table.convertRowIndexToModel(i);
			Contig contig = (Contig) model.getValueAt(row, 0);

			text.append(contig.getName() + "\t"
				+ contig.getConsensus().length() + "\t"
				+ contig.readCount() + "\t"
				+ contig.getFeatures().size() + "\t"
				+ contig.getMismatchPercentage());
			text.append(newline);
		}

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	private void saveReadsSummary()
	{
		int row = controls.table.getSelectedRow();
		row = controls.table.convertRowIndexToModel(row);
		Contig contig = (Contig) model.getValueAt(row, 0);

		File saveAs = new File(Prefs.guiCurrentDir, contig.getName() + ".txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the data to
		String filename = TabletUtils.getSaveFilename(
			RB.getString("gui.ContigsPanel.saveReads.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		ReadsSummarySaver summary = new ReadsSummarySaver(new File(filename), contig);

		ProgressDialog dialog = new ProgressDialog(summary,
			RB.getString("gui.ContigsPanel.saveReads.title"),
			RB.getString("gui.ContigsPanel.saveReads.label"));

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED &&
			dialog.getResult() == ProgressDialog.JOB_FAILED)
		{
			dialog.getException().printStackTrace();
			TaskDialog.error(
				RB.format("gui.ContigsPanel.saveReads.exception",
				dialog.getException()),
				RB.getString("gui.text.close"));
		}
		else
			TaskDialog.info(
				RB.format("gui.ContigsPanel.saveReads.success", filename),
				RB.getString("gui.text.close"));
	}

	private void displayMenu(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		controls.table.setRowSelectionInterval(row, row);

		JMenuItem mSaveReads = new JMenuItem("", Icons.getIcon("FILESAVE16"));
		RB.setText(mSaveReads, "gui.ContigsPanel.mSaveReads");
		mSaveReads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveReadsSummary();
			}
		});

		JMenuItem mTableCopy = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mTableCopy, "gui.ContigsPanel.mTableCopy");
		mTableCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyTableToClipboard();
			}
		});


		JPopupMenu menu = new JPopupMenu();
		menu.add(mSaveReads);
		menu.addSeparator();
		menu.add(mTableCopy);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private class TableMouseListener extends MouseInputAdapter
	{
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

	static class DecimalFormatRenderer extends DefaultTableCellRenderer
	{
		private static final DecimalFormat formatter = new DecimalFormat( "#0.0" );

		DecimalFormatRenderer()
		{
			setHorizontalAlignment(JLabel.RIGHT);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

				setText(formatter.format((Number)value));

			return this;
		}

	}

}