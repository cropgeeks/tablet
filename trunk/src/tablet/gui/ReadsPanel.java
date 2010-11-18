package tablet.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.RB;

import tablet.data.*;
import tablet.gui.viewer.*;

public class ReadsPanel extends JPanel implements ListSelectionListener
{
	private NBReadsPanelControls controls;
	private ReadsTableModel tableModel;
	private TableRowSorter<AbstractTableModel> sorter;
	private AssemblyPanel aPanel;

	ReadsPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		controls = new NBReadsPanelControls(this);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection againm
		controls.table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				processTableSelection();
			}
		});

		setLayout(new BorderLayout());
		add(controls);
	}

	public void setTableModel(List<Read> reads)
	{
		// Note: the model is created to be non-editable
		tableModel = new ReadsTableModel(reads);

		sorter = new TableRowSorter<AbstractTableModel>(tableModel);
		controls.table.setModel(tableModel);
		controls.table.setRowSorter(sorter);

		controls.readsLabel.setText(RB.format("gui.ReadsPanel.readsLabel", reads.size()));
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		processTableSelection();
	}

	/**
	 * Deals with highlighting reads on their selection from the table.
	 */
	private void processTableSelection()
	{
		if (controls.table.getModel().getRowCount() == 0)
			return;

		int row = controls.table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);

		Read read = (Read)tableModel.getValueAt(row, 3);
		if(read != null)
			highlightRead(read, aPanel.getContig());

		updateReadInfo(read);
	}

	/**
	 * Carries out the steps required to highlight a read on the screen.
	 *
	 * @param read The read object itself.
	 * @param contig The contig associated with this read.
	 */
	private void highlightRead(final Read read, final Contig contig)
	{
		final int lineIndex = contig.getReadManager().getLineForRead(read);

		new ReadHighlighter(aPanel, read, lineIndex);
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		String name = (String) controls.table.getValueAt(row, 0);

		int position = (Integer) controls.table.getValueAt(row, 1);
		String posString = TabletUtils.nf.format(position);

		int length = (Integer) controls.table.getValueAt(row, 2);
		String lenString = TabletUtils.nf.format(length);

		return RB.format("gui.ReadsPanel.tooltip", name, posString, lenString);
	}

	/**
	 * Update the labels displaying information on the currently highlighted read
	 * in the Reads table.
	 */
	private void updateReadInfo(Read read)
	{
		if (read == null)
			setReadInfoToDefaults();

		ReadNameData rnd = Assembly.getReadNameData(read);
		ReadMetaData rmd = Assembly.getReadMetaData(read, false);

		String cigar = Assembly.hasCigar() ? rnd.getCigar() : "";
		controls.cigarLabel.setText(cigar);

		if(read instanceof MatedRead)
		{
			MatedRead mr = (MatedRead)read;

			String properlyPaired = rnd.isProperPair() ? RB.getString("gui.ReadsPanel.properlyPaired.yes") : RB.getString("gui.ReadsPanel.properlyPaired.no");
			controls.properlyPairedLabel.setText(properlyPaired);

			String numberInPair = (rnd.getNumberInPair() == 1) ? RB.getString("gui.ReadsPanel.numberInPair.one") : RB.getString("gui.ReadsPanel.numberInPair.two");
			controls.numberInPairLabel.setText(numberInPair);

			String insertSize = (mr.getMatePos() != -1) ? TabletUtils.nf.format(rnd.getInsertSize()) : "";
			controls.insertSizeLabel.setText(insertSize);

			String matePos = (mr.getMatePos() != -1) ? TabletUtils.nf.format(mr.getMatePos()) : "";
			controls.matePosLabel.setText(matePos);

			String mateContig = rnd.getMateContig();
			controls.mateContigLabel.setText(mateContig);
			
			if (rmd.getIsPaired() && !rmd.getMateMapped())
			{
				controls.matePosLabel.setText("");
				controls.numberInPairLabel.setText("");
				controls.mateContigLabel.setText(RB.getString("gui.ReadsPanel.mContigLabel.mateUnmapped"));
			}
		}
	}

	private void setReadInfoToDefaults()
	{
		controls.cigarLabel.setText("");
		controls.properlyPairedLabel.setText("");
		controls.numberInPairLabel.setText("");
		controls.insertSizeLabel.setText("");
		controls.matePosLabel.setText("");
		controls.mateContigLabel.setText("");
	}

	public void clear()
	{
		if (tableModel != null)
			tableModel.clear();

		controls.readsLabel.setText(RB.format("gui.ReadsPanel.readsLabel", "Waiting"));
		
		controls.table.repaint();
		setReadInfoToDefaults();
	}

}
