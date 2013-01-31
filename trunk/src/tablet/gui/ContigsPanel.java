// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import tablet.analysis.*;
import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class ContigsPanel extends JPanel implements ListSelectionListener
{
	private WinMain winMain;
	private AssemblyPanel aPanel;
	private FeaturesPanel featuresPanel;
	private ContigsPanelNB controls;
	private FindPanel findPanel;
	private ReadsPanel readsPanel;
	private ReadGroupsPanel readGroupsPanel;
	private JTabbedPane ctrlTabs;

	private ContigsTableModel model;
	private TableRowSorter<ContigsTableModel> sorter;

	private Contig prevContig = null;

	ContigsPanel(WinMain winMain, AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.winMain = winMain;
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		setLayout(new BorderLayout());
		add(controls = new ContigsPanelNB(this));

		controls.table.addMouseListener(new TableMouseListener());
	}

	void setFeaturesPanel(FeaturesPanel featuresPanel)
		{ this.featuresPanel = featuresPanel; }

	void setFindPanel(FindPanel findPanel)
		{ this.findPanel = findPanel; }

	void setReadsPanel(ReadsPanel readsPanel)
		{ this.readsPanel = readsPanel; }

	void setReadGroupsPanel(ReadGroupsPanel readGroupsPanel)
		{ this.readGroupsPanel = readGroupsPanel; }

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

		// Need to clear the filter before resetting the rest of the table,
		// otherwise you get a NullPointerException
		controls.clearFilter();

		if (assembly == null)
		{
			// This is done to ensure complete removal of all references that
			// might lead back to the Assembly object (and lots of memory)
			model = null;
			sorter = null;

			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);
		}
		else
		{
			model = new ContigsTableModel(assembly, controls.table);
			sorter = new TableRowSorter<ContigsTableModel>(model);

			controls.table.setModel(model);
			controls.table.setRowSorter(sorter);
		}



		String title = RB.format("gui.ContigsPanel.title", controls.table.getRowCount());
		controls.contigsLabel.setText(title);

		long count = calculateTotalReadCount();
		String readCount = RB.format("gui.ContigsPanel.readCount", count);
		controls.readCountLabel.setText(readCount);
		if(controls.table.getRowCount() != 0)
		{
			String averageReadCount = RB.format("gui.ContigsPanel.readCount.tooltip", (count/controls.table.getRowCount()));
			controls.readCountLabel.setToolTipText(averageReadCount);
		}

		ctrlTabs.setSelectedIndex(0);

		controls.setEnabledState(assembly != null);

		findPanel.setAssembly(assembly);
	}

	private long calculateTotalReadCount()
	{
		long readCount = 0;
		for (int row = 0; row < controls.table.getRowCount(); row++)
		{
			readCount += (Integer)controls.table.getValueAt(row, 2);
		}
		return readCount;
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		int row = controls.table.getSelectedRow();

		// 31/01/2013 Originally Actions.openedNoContigSelected and
		// TaskManager.cancelAll happened outside of the if statements, but this
		// method also gets called on a table row sort, where we don't want
		// those methods to be called.

		// Contig de-selection
		if (row == -1)
		{
			Actions.openedNoContigSelected();
			TaskManager.cancelAll();
			setNullContig();
			prevContig = null;
		}
		// Check for selection of a new contig
		else
		{
			// Convert from view->model (deals with user-sorted table)
			row = controls.table.convertRowIndexToModel(row);

			// Then pull the contig out of the model and set...
			Contig contig = (Contig) model.getValueAt(row, 0);

			if(contig != prevContig)
			{
				Actions.openedNoContigSelected();
				TaskManager.cancelAll();
				setDisplayedContig(contig);
				prevContig = contig;
			}
		}
	}

	public void setContigInTable(String contigName)
	{
		Contig contig = null;
		int row;
		// Attempt to get a contig object matching the given name
		for (row = 0; row < controls.table.getRowCount(); row++)
		{
			contig = (Contig) controls.table.getValueAt(row, 0);

			if (contig.getName().equals(contigName))
				controls.table.setRowSelectionInterval(row, row);
		}
	}

	// ONLY TO BE USED BY POST_LOAD OPERATIONS - otherwise tweaks will need to be
	// made to cope with position-1.
	public void moveToContigPosition(String contigName, Integer position)
	{
		setContigInTable(contigName);

		// If we can, move to the position within the contig and highlight it
		if (position != null)
			winMain.getAssemblyPanel().highlightColumn(position-1);
	}

	public void setDisplayedContig(Contig contig)
	{
		// Attempt to set the contig on the graphical components...
		if (aPanel.setContig(contig))
		{
			featuresPanel.setContig(contig);
			readsPanel.setContig(contig);
			readGroupsPanel.setContig(contig);

			Actions.openedContigSelected();

			if(contig.getFeatures().isEmpty())
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
		readsPanel.setContig(null);
		readGroupsPanel.setContig(null);

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
		String UNKNOWN = RB.getString("gui.ContigsPanel.unknown");

		// Decide how to format the length, based on the available data
		int length = contig.getTableData().consensusLength();
		String lengthStr = TabletUtils.nf.format(length);
		if (contig.getTableData().consensusDefined == false && length == 0)
			lengthStr = UNKNOWN;

		// Decide how to format the read count, based on the available data
		Integer rc = contig.getTableData().readCount();
		String rdsStr = rc != null ? TabletUtils.nf.format(rc) : UNKNOWN;

		// Decide how to format the mismatch value
		Float mf = contig.getTableData().mismatchPercentage();
		String mm = mf != null ? TabletUtils.nf.format(mf) + "%" : UNKNOWN;

		return RB.format("gui.ContigsPanel.tooltip",
			contig.getName(), lengthStr, rdsStr,
			TabletUtils.nf.format(contig.getTableData().featureCount()), mm);
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
			RB.getString("gui.ContigsPanel.saveReads.label"),
			Tablet.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();

				TaskDialog.showOpenLog(RB.format("gui.ContigsPanel.saveReads.exception",
					dialog.getException()), Tablet.getLogFile());
			}

			return;
		}

		// Decide which message to display, based on whether we're dealing with
		// a BAM assembly or not
		String msg = "gui.ContigsPanel.saveReads.success";
		if (aPanel.getAssembly().getBamBam() != null)
			msg += "BAM";

		TaskDialog.showFileOpen(
			RB.format(msg, filename), TaskDialog.INF, new File(filename));
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
				TabletUtils.copyTableToClipboard(controls.table, model);
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
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}
	}
}