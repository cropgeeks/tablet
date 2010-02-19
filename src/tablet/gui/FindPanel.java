package tablet.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.analysis.SimpleJob;
import tablet.data.*;
import tablet.data.auxiliary.DisplayData;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class FindPanel extends JPanel implements ListSelectionListener
{
	private NBFindPanelControls controls;
	private AssemblyPanel aPanel;
	private FindTableModel tableModel;
	private ContigsPanel cPanel;
	private WinMain winMain;
	private int found;
	Finder finder;

	private TableRowSorter<FindTableModel> sorter;

	public FindPanel(AssemblyPanel aPanel, WinMain winMain, final JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.winMain = winMain;
		this.cPanel = winMain.getContigsPanel();

		controls = new NBFindPanelControls(this);

		finder = new Finder();

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
		tableModel = new FindTableModel(results) {
        	public boolean isCellEditable(int rowIndex, int mColIndex) {
        		return false;
        }};

        sorter = new TableRowSorter<FindTableModel>(tableModel);
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

		Read read = (Read) tableModel.getValueAt(row, 8);
		Contig contig = (Contig) tableModel.getValueAt(row, 9);

		updateContigsTable(contig);
		highlightRead(read, contig);
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		return RB.format("gui.NBFindPanelControls.tooltip",
			tableModel.getValueAt(row, 0),
			tableModel.getValueAt(row, 3),
			TabletUtils.nf.format(tableModel.getValueAt(row, 1)),
			TabletUtils.nf.format(
				((Read)tableModel.getValueAt(row, 8)).getEndPosition()+1),
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
				Contig ctg = (Contig) cPanel.getTable().getValueAt(i, 0);
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
	 * @param start The start position of the read as presented in the table.
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

		final int startPos = read.getStartPosition() - contig.getVisualStart();

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				aPanel.moveToPosition(lineIndex, startPos, true);
				new ReadHighlighter(aPanel, read, lineIndex);
			}
		});
	}

	public Finder getFinder()
	{
		return finder;
	}

	public void resetFinder()
	{
		finder = new Finder();
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		controls.toggleComponentEnabled(enabled);
	}

	public void setAssembly(Assembly assembly)
	{
		if(assembly == null)
			toggleComponentEnabled(false);

		finder.results = null;
		controls.table.setModel(new DefaultTableModel());
		controls.table.invalidate();
	}

	/**
	 * Class extends Simplejob such that a search on reads can be run
	 * that keeps track of its progress.
	 */
	class Finder extends SimpleJob
	{
		LinkedList<SearchResult> results;

		private LinkedList<SearchResult> search(String str, int selectedIndex)
		{
			found = 0;
			progress = 0;
			maximum = 0;
			results = new LinkedList<SearchResult>();

			//Work out the total number of reads across all contigs
			for(Contig contig : aPanel.getAssembly())
			{
				maximum += contig.getReads().size();
			}

			//Loop over contigs checking for matches
			for(Contig contig : aPanel.getAssembly())
			{
				if(selectedIndex == 1 || (selectedIndex == 0 && contig == aPanel.getContig()))
				{
					if(selectedIndex == 0)
					{
						maximum = contig.getReads().size();
					}
					for(Read read : contig.getReads())
					{
						if(okToRun)
						{
							checkForMatches(read, str, results, contig);
							//if we've had 500 matches stop searching
							if (results.size() >= 500)
								break;
						}
					}
				}

				if (results.size() >= 500)
				{
					break;
				}
			}
			controls.resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", results.size()));

			return results;
		}

		/**
		 * Method which collates the results which match the search query
		 *
		 * @param read
		 * @param str
		 * @param results
		 * @param contig
		 */
		private void checkForMatches(Read read, String pattern, LinkedList<SearchResult> results, Contig contig)
		{
			ReadMetaData rmd = Assembly.getReadMetaData(read, false);

			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(rmd.getName());

			if ((Prefs.guiRegexSearching && m.matches()) ||
				(!Prefs.guiRegexSearching && rmd.getName().equals(pattern)))
			{
				results.add(new SearchResult(read, contig, rmd));
				found++;
			}
			progress++;
		}

		public void runJob(int jobIndex) throws Exception
		{
			try { Pattern.compile(controls.findCombo.getText()); }
				catch (PatternSyntaxException e)
				{
					TaskDialog.error(
						RB.format("gui.FindPanel.regexError", e),
						RB.getString("gui.text.close"));
					return;
				}

			results = search(controls.findCombo.getText(), controls.findInCombo.getSelectedIndex());

			setTableModel(results);

			//if we've had 500 matches stop searching
			if (results.size() >= 500)
				showWarning();
		}

		public String getMessage()
		{
			return RB.getString("gui.NBFindPanelControls.progressMessage")+ " " + found;
		}

		private void showWarning()
		{
			if (Prefs.guiWarnSearchLimitExceeded)
			{
				String msg = RB.getString("gui.findPanel.guiWarnSearchLimitExceeded");
				JCheckBox checkbox = new JCheckBox();
				RB.setText(checkbox, "gui.findPanel.checkWarning");
				String[] options = new String[]{RB.getString("gui.text.ok")};
				TaskDialog.show(msg, TaskDialog.QST, 0, checkbox, options);
				Prefs.guiWarnSearchLimitExceeded = !checkbox.isSelected();
			}
		}
	}

	class SearchResult
	{
		private Read read;
		private ReadMetaData rmd;
		private Contig contig;

		SearchResult(Read read, Contig contig, ReadMetaData rmd)
		{
			this.read = read;
			this.contig = contig;
			this.rmd = rmd;
		}

		public Read getRead()
		{
			return read;
		}

		public ReadMetaData getReadMetaData()
		{
			return rmd;
		}

		public Contig getContig()
		{
			return contig;
		}
	}
}
