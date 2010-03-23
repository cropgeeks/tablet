package tablet.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.data.*;
import tablet.gui.viewer.*;
import tablet.analysis.*;
import tablet.analysis.Finder.*;
import tablet.gui.dialog.*;

public class FindPanel extends JPanel implements ListSelectionListener, ActionListener
{
	private NBFindPanelControls controls;
	private AssemblyPanel aPanel;
	private AbstractTableModel tableModel;
	private ContigsPanel cPanel;
	private int found;
	private Finder finder;
	private TableRowSorter<AbstractTableModel> sorter;

	public FindPanel(AssemblyPanel aPanel, WinMain winMain, final JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.cPanel = winMain.getContigsPanel();

		controls = new NBFindPanelControls(this);

		finder = new Finder(aPanel);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection again
		controls.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processTableSelection();
			}
		});

		setLayout(new BorderLayout());
		add(controls = new NBFindPanelControls(this));

		//Keyboard shortcut code
		Action openFind = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ctrlTabs.setSelectedIndex(2);
				controls.toggleComponentEnabled(true);}
		};

		ctrlTabs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, Tablet.menuShortcut), "find");
		ctrlTabs.getActionMap().put("find", openFind);

		controls.toggleComponentEnabled(false);
	}

	public NBFindPanelControls getFindPanel()
	{
		return controls;
	}

	void setTableModel(LinkedList<SearchResult> results)
	{
		// Note: the model is created to be non-editable
		tableModel = new FindTableModel(results, this) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
		}};

		sorter = new TableRowSorter<AbstractTableModel>(tableModel);
		controls.table.setModel(tableModel);
		controls.table.setRowSorter(sorter);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		processTableSelection();
	}

	/**
	 * Carries out the operations required to process the selection of a read
	 * from the table and the subsequent move to, and highlighting of, that read
	 * in the view.
	 */
	public void processTableSelection()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);
		Contig contig = (Contig) tableModel.getValueAt(row, 9);
		int pos = (Integer)tableModel.getValueAt(row, 1)-1;

		updateContigsTable(contig);

		// If we are searching for reads
		if((Integer) tableModel.getValueAt(row, 4) == null)
		{
			// If we are searching a BAM assembly, we need to load up the correct
			// section of the BAM file before we can search for the read.
			if (aPanel.getAssembly().getBamBam() != null)
				aPanel.moveToPosition(0, pos, true);

			Read read = getRead(pos, (String)tableModel.getValueAt(row, 0));
			if(read != null)
				highlightRead(read, contig);
		}
		// If we are searching for read subsequences
		else
		{
			int sPos = (Integer)tableModel.getValueAt(row, 4);
			int ePos = (Integer)tableModel.getValueAt(row, 5)-1;
			// If we are searching a BAM assembly, we need to load up the correct
			// section of the BAM file before we can search for the read.
			if (aPanel.getAssembly().getBamBam() != null)
				aPanel.moveToPosition(0, sPos, true);

			Read read = getRead(pos, (String)tableModel.getValueAt(row, 0));

			if(read != null)
				highlightSubsequence(read, contig, sPos, ePos);
		}
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		int end = ((Integer)tableModel.getValueAt(row, 1)+(Integer)tableModel.getValueAt(row, 2));

		return RB.format("gui.NBFindPanelControls.tooltip",
			tableModel.getValueAt(row, 0),
			tableModel.getValueAt(row, 3),
			TabletUtils.nf.format(tableModel.getValueAt(row, 1)),
			TabletUtils.nf.format(end),
			tableModel.getValueAt(row, 2));
	}

	/**
	 * Update the contigs table such that the correct contig is selected.
	 *
	 * @param contig
	 */
	public void updateContigsTable(Contig contig)
	{
		boolean foundInTable = false;
		for(int i=0; i < cPanel.getTable().getRowCount(); i++)
		{
			if(cPanel.getTable().getValueAt(i, 0).equals(contig))
			{
				cPanel.getTable().setRowSelectionInterval(i, i);
				foundInTable = true;
				break;
			}
		}

		if (!foundInTable)
			cPanel.setDisplayedContig(contig);
	}

	/**
	 * Carries out the steps required to highlight and move to a read in the
	 * dataset.
	 *
	 * @param read The read object itself.
	 * @param contig The contig associated with this read.
	 */
	private void highlightRead(final Read read, final Contig contig)
	{
		final int lineIndex;

		if(Prefs.visPacked == false)
			lineIndex = contig.getStackSetManager().getLineForRead(read);
		else
			lineIndex = contig.getPackSetManager().getLineForRead(read);

		final int startPos = read.getStartPosition()/* - contig.getVisualStart()*/;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				aPanel.moveToPosition(lineIndex, startPos, true);
				new ReadHighlighter(aPanel, read, lineIndex);
			}
		});
	}

	/**
	 * Carries out the steps required to highlight and move to a subsequence of
	 * a read in the dataset.
	 *
	 * @param read	The read which the subsequence is part of.
	 * @param contig	The contig the read is part of.
	 * @param sPos	The starting position of the subsequence.
	 * @param ePos	The ending position of the subsequence.
	 */
	private void highlightSubsequence(final Read read, final Contig contig, final int sPos, final int ePos)
	{
		final int lineIndex;

		if(Prefs.visPacked == false)
			lineIndex = contig.getStackSetManager().getLineForRead(read);
		else
			lineIndex = contig.getPackSetManager().getLineForRead(read);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				aPanel.moveToPosition(lineIndex, sPos, true);
				new ReadHighlighter(aPanel, lineIndex, sPos, ePos);
			}
		});
	}

	public Finder getFinder()
	{
		return finder;
	}

	public void resetFinder()
	{
		if(aPanel.getAssembly().getBamBam() == null)
			finder = new Finder(aPanel);
		else
			finder = new BamFinder(aPanel);
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		controls.toggleComponentEnabled(enabled);
	}

	public void setAssembly(Assembly assembly)
	{
		if(assembly == null)
			toggleComponentEnabled(false);

		finder.setResults(null);
		controls.table.setModel(new DefaultTableModel());
		controls.table.invalidate();
	}
	
	public void runSearch()
	{
		try
		{
			resetFinder();
			setTableModel(null);

			finder.setSearchTerm(controls.findCombo.getText());
			// Set if we are searching the current contig, or all contigs
			Prefs.guiFindPanelSelectedIndex = controls.findInCombo.getSelectedIndex();
			// Set if we are searching for reads, or subsequences
			finder.setSearchReads((String)controls.searchTypeCombo.getSelectedItem());

			if (controls.findCombo.getText() != null)
			{
				controls.findCombo.updateComboBox((String) controls.findCombo.getSelectedItem());
				Prefs.recentSearches = controls.findCombo.getHistory();
			}

			ProgressDialog dialog = new ProgressDialog(finder, RB.getString("gui.NBFindPanelControls.progressTitle"), RB.getString("gui.NBFindPanelControls.progressLabel"));
			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
					System.out.println(dialog.getException());

				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		LinkedList<SearchResult> results = finder.getResults();
		controls.resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", results.size()));
		setTableModel(results);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFind)
		{
			if(Prefs.guiFindPanelSelectedIndex == Finder.CURRENT_CONTIG && aPanel.getContig() == null)
			{
				TaskDialog.error("A Contig must be selected before searching over a single contig can occur.", "OK");
				return;
			}
			runSearch();
		}

		// TODO: This should be a link to a section of Tablet help
		else if(e.getSource() == controls.helpLabel)
			TabletUtils.visitURL("http://java.sun.com/javase/7/docs/api/java/util/regex/Pattern.html#sum");

		else if (e.getSource() == controls.checkUseRegex)
			Prefs.guiRegexSearching = controls.checkUseRegex.isSelected();

		else if (e.getSource() == controls.searchTypeCombo)
		{
			Prefs.guiFindPanelSearchType = controls.searchTypeCombo.getSelectedIndex();
			if(controls.searchTypeCombo.getSelectedItem().equals(RB.getString("gui.NBFindPanelControls.findLabel1")))
				controls.checkUseRegex.setEnabled(true);
			else
				controls.checkUseRegex.setEnabled(false);
		}
	}

	public Read getRead(int position, String name)
	{
		IReadManager manager;

		// Get the pack or stack set for searching in
		if(Prefs.visPacked)
			manager = aPanel.getContig().getPackSetManager();
		else
			manager = aPanel.getContig().getStackSetManager();

		int i = 0;
		// Loop over the rows of the packset until we find out read (with an
		// insurance step to ensure that we never search for a number of times
		// greater than the packset size.
		while(i < manager.size())
		{
			// Get the read at this row and position.
			Read read = manager.getReadAt(i, position);
			if(read != null)
			{
				ReadMetaData rmd = Assembly.getReadMetaData(read, false);
				// Check if this is the read we are looking for
				if(rmd.getName().equals(name))
					return read;
			}
			i++;
		}
		return null;
	}
}
