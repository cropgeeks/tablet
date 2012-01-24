// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.analysis.*;
import tablet.analysis.Finder.*;
import tablet.data.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class FindPanel extends JPanel implements ListSelectionListener, ActionListener
{
	private FindPanelNB controls;
	private AssemblyPanel aPanel;
	private FindTableModel tableModel;
	private ContigsPanel cPanel;
	private Finder finder;
	private TableRowSorter<AbstractTableModel> sorter;

	private JMenuItem mClipboard;

	public FindPanel(AssemblyPanel aPanel, WinMain winMain)
	{
		this.aPanel = aPanel;
		this.cPanel = winMain.getContigsPanel();

		setLayout(new BorderLayout());
		add(controls = new FindPanelNB(this));

		createTableModel();
	}

	private void createTableModel()
	{
		tableModel = new FindTableModel();

		sorter = new TableRowSorter<AbstractTableModel>(tableModel);
		controls.table.setModel(tableModel);
		controls.table.setRowSorter(sorter);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection again
		controls.table.addMouseListener(new TableMouseListener());

		controls.table.setDefaultRenderer(Number.class,
			new NumberFormatCellRenderer());
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		processTableSelection();
	}

	// Carries out the operations required to process the selection of a read
	// from the table and the subsequent move to, and highlighting of, that read
	// in the view.
	public void processTableSelection()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);
		Contig contig = (Contig) tableModel.getValueAt(row, 9);

		updateContigsTable(contig);


		// If we are searching in the consensus / reference
		if(Finder.CURRENT_TYPE == Finder.CON_SEQUENCE)
		{
			int pos = (Integer)tableModel.getValueAt(row, 1)-1;
			int length = (Integer) tableModel.getValueAt(row, 2);

			if (aPanel.getAssembly().getBamBam() != null)
				aPanel.moveToPosition(0, pos, true);

			highlightReference(pos, length);
		}
		// If we are searching for reads
		else if(Finder.CURRENT_TYPE == Finder.READ_NAME)
		{
			int pos = (Integer)tableModel.getValueAt(row, 2)-1;

			if (aPanel.getAssembly().getBamBam() != null)
				aPanel.moveToPosition(0, pos, true);

			Read r = getRead(pos, (String)tableModel.getValueAt(row, 0));
			if(r != null)
				highlightRead(r, contig.getReadManager(), r.getStartPosition(), r.getEndPosition());
		}
		// If we are searching for read subsequences
//		else
//		{
//			int sPos = (Integer)tableModel.getValueAt(row, 4)-1;
//			int ePos = (Integer)tableModel.getValueAt(row, 5)-1;
//
//			Read r = getRead(pos, (String)tableModel.getValueAt(row, 0));
//
//			if(r != null)
//				highlightRead(r, contig.getReadManager(), sPos, ePos);
//		}
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		if (Finder.CURRENT_TYPE == Finder.READ_NAME)
		{
			int end = ((Integer)tableModel.getValueAt(row, 2) + (Integer)tableModel.getValueAt(row, 3));

			return RB.format("gui.NBFindPanelControls.tooltip",
				tableModel.getValueAt(row, 0),
				tableModel.getValueAt(row, 1),
				TabletUtils.nf.format(tableModel.getValueAt(row, 2)),
				TabletUtils.nf.format(end),
				tableModel.getValueAt(row, 3));
		}
		else
		{
			int end = ((Integer)tableModel.getValueAt(row, 1) + (Integer)tableModel.getValueAt(row, 2));

			return RB.format("gui.NBFindPanelControls.consensusTooltip",
				tableModel.getValueAt(row, 0),
				TabletUtils.nf.format(tableModel.getValueAt(row, 1)),
				TabletUtils.nf.format(end),
				tableModel.getValueAt(row, 2),
				tableModel.getValueAt(row, 3));
		}

	}

	// Ensure the correct contig is selected in the contigs table.
	public void updateContigsTable(Contig contig)
	{
		boolean foundInTable = false;
		JTable table = cPanel.getTable();

		for(int i=0; i < table.getRowCount(); i++)
		{
			if(table.getValueAt(i, 0).equals(contig))
			{
				table.setRowSelectionInterval(i, i);
				Rectangle r = table.getCellRect(i, 0, true);
				table.scrollRectToVisible(r);
				foundInTable = true;
				break;
			}
		}

		if (!foundInTable)
			cPanel.setDisplayedContig(contig);
	}

	// Move to and highlight a read, or subsequence of a read
	private void highlightRead(final Read read, final IReadManager manager, final int sPos, final int ePos)
	{
		final int lineIndex = manager.getLineForRead(read);

		aPanel.moveToPosition(lineIndex, sPos, true);
		new ReadHighlighter(aPanel, lineIndex, sPos, ePos);
	}

	private void highlightReference(final int pos, final int length)
	{
		aPanel.moveToPosition(0, pos, true);
		new ConsensusHighlighter(aPanel, pos, length);
	}

	public void resetFinder()
	{
		// Work around for History Combo Box return selection bug.
		String searchTerm = (String) controls.findCombo.getEditor().getItem();

		// Setup the search variables for the Finder
		boolean searchAllContigs = !controls.findInCheckBox.isSelected();
		int searchType = controls.searchTypeCombo.getSelectedIndex();

		if (aPanel.getAssembly().getBamBam() == null)
			finder = new Finder(aPanel, searchTerm, searchAllContigs, searchType);

		else
		{
			try
			{
				finder = new BamFinder(aPanel, searchTerm, searchAllContigs, searchType);
			}
			catch (Exception e)
			{
				TaskDialog.error(e.getMessage(), RB.getString("gui.findPanel.fileNotFound"));
			}
		}
	}

	public void setAssembly(Assembly assembly)
	{
		if (assembly == null)
			controls.toggleComponentEnabled(false);
		else
			controls.toggleComponentEnabled(true);

		if (finder != null)
			finder.setResults(null);

		tableModel.clear();

		controls.resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", 0));
	}

	public void runSearch()
	{
		if(Prefs.guiFindPanelSearchCurrentContig && aPanel.getContig() == null)
		{
			TaskDialog.error(RB.getString("gui.findPanel.noContigError"),
				RB.getString("gui.text.close"));
			return;
		}

		resetFinder();
		tableModel.clear();

		if (controls.findCombo.getText() != null)
		{
			controls.findCombo.updateComboBox((String) controls.findCombo.getEditor().getItem());
			Prefs.recentSearches = controls.findCombo.getHistory();
		}

		setupDialog();

		ArrayList<SearchResult> results = finder.getResults();

		if(results != null && !results.isEmpty())
		{
			controls.resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", results.size()));
			tableModel.setResults(results);
		}

		else
			controls.resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", 0));
	}

	private void setupDialog()
	{
		String title, label;

		if (controls.searchTypeCombo.getSelectedIndex() == Finder.READ_NAME)
		{
			title = RB.getString("gui.NBFindPanelControls.progressReadsTitle");
			label = RB.getString("gui.NBFindPanelControls.progressReadsLabel");
		}
		else if (controls.searchTypeCombo.getSelectedIndex() == Finder.READ_SEQUENCE)
		{
			title = RB.getString("gui.NBFindPanelControls.progressSubsequenceTitle");
			label = RB.getString("gui.NBFindPanelControls.progressSubsequenceLabel");
		}
		else
		{
			title = RB.getString("gui.NBFindPanelControls.progressReferenceTitle");
			label = RB.getString("gui.NBFindPanelControls.progressReferenceLabel");
		}

		ProgressDialog dialog = new ProgressDialog(finder, title, label, Tablet.winMain);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				System.out.println(dialog.getException());
		}

		aPanel.validateConsensusCache();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFind)
			runSearch();

		// TODO: This should be a link to a section of Tablet help
		else if(e.getSource() == controls.helpLabel)
			TabletUtils.visitURL("http://java.sun.com/javase/7/docs/api/java/util/regex/Pattern.html#sum");

		else if (e.getSource() == controls.checkUseRegex)
			Prefs.guiRegexSearching = controls.checkUseRegex.isSelected();

		else if (e.getSource() == controls.checkIgnorePads)
			Prefs.guiSearchIgnorePads = controls.checkIgnorePads.isSelected();

		else if (e.getSource() == controls.findInCheckBox)
			Prefs.guiFindPanelSearchCurrentContig = controls.findInCheckBox.isSelected();

		else if (e.getSource() == controls.searchTypeCombo)
		{
			Prefs.guiFindPanelSearchType = controls.searchTypeCombo.getSelectedIndex();
			if(controls.searchTypeCombo.getSelectedIndex() == Finder.READ_NAME)
			{
				controls.checkUseRegex.setEnabled(true);
				controls.checkIgnorePads.setEnabled(false);
			}
			else
			{
				controls.checkIgnorePads.setEnabled(true);
				controls.checkUseRegex.setEnabled(false);
			}
		}
	}

	public Read getRead(int position, String name)
	{
		IReadManager manager = aPanel.getContig().getReadManager();

		int i = 0;
		// Loop over the rows of the packset until we find out read (with an
		// insurance step to ensure that we never search for a number of times
		// greater than the packset size.
		while (i < manager.size())
		{
			// Get the read at this row and position.
			Read read = manager.getReadAt(i, position);
			if (read != null && read.getStartPosition() == position)
			{
				ReadNameData rnd = Assembly.getReadNameData(read);
				// Check if this is the read we are looking for
				if (rnd.getName().equals(name))
					return read;
			}
			i++;
		}
		return null;
	}

	private void displayMenu(MouseEvent e)
	{
		mClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboard, "gui.viewer.FeaturesPanel.mClipboard");
		mClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabletUtils.copyTableToClipboard(controls.table, tableModel);
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(mClipboard);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private final class TableMouseListener extends MouseInputAdapter
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