// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import scri.commons.gui.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.viewer.*;

public class ReadsPanel extends JPanel implements ListSelectionListener, ActionListener
{
	private ReadsPanelNB controls;
	private ReadsTableModel tableModel;
	private TableRowSorter<AbstractTableModel> sorter;
	private AssemblyPanel aPanel;
	
	private JMenuItem mClipboardName, mClipboardData;
	private JMenuItem mFindStart, mFindEnd, mJumpToPair;

	ReadsPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		controls = new ReadsPanelNB(this);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection againm
		controls.table.addMouseListener(new TableMouseListener());

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
			controls.setReadInfoToDefaults();

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
	
	public void setContig(Contig contig)
	{
		if (contig != null)
		{
			controls.readsLabel.setEnabled(true);
			controls.exportLinkLabel.setEnabled(true);
			controls.table.setEnabled(true);
			controls.labelPanel.setVisible(true);
			controls.setLabelStates(Assembly.hasCigar());
			controls.setPairLabelStates(Assembly.isPaired());
		}
		else
		{
			clear();
			controls.table.setModel(new DefaultTableModel());
			controls.table.setRowSorter(null);
			controls.table.setEnabled(false);
			controls.exportLinkLabel.setEnabled(false);
			controls.labelPanel.setVisible(false);
			controls.readsLabel.setEnabled(false);
			controls.readsLabel.setText(RB.format("gui.ReadsPanel.readsLabel", 0));
		}
	}

	/**
	 * Clears out the table of all read information and sets the labels to their
	 * default state.
	 */
	public void clear()
	{
		if (tableModel != null)
			tableModel.clear();
		
		controls.setReadInfoToDefaults();
		controls.readsLabel.setText(RB.format("gui.ReadsPanel.readsLabel", RB.getString("gui.readsPanel.readsLabel.waiting")));
		
		controls.table.repaint();
	}
	
	/**
	 * Retrieves a read from the currently selected row of the table.
	 */
	private Read getReadFromTable()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
			return null;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);

		Read read = (Read)tableModel.getValueAt(row, 3);
		
		return read;
	}
	
	/**
	 * Copies the name of the given read to the clipboard.
	 */
	void copyReadNameToClipboard(Read read)
	{
		StringSelection selection = new StringSelection(Assembly.getReadNameData(read).getName());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	/**
	 * Copies the data relating to the given read to the clipboard.
	 */
	void copyDataToClipboard(Read read)
	{
		ReadMetaData metaData = Assembly.getReadMetaData(read, false);
		String name = Assembly.getReadName(read);
		int position = read.getStartPosition();
		int length = read.length();
		String cigar = Assembly.getReadNameData(read).getCigar();
		
		String lb = System.getProperty("line.separator");
		String seq = metaData.toString();

		StringBuilder text = new StringBuilder(seq.length() + 500);
		text.append(name).append(lb).append(position).append(lb).append(length).append(lb);
		if(Assembly.hasCigar())
			text.append(cigar).append(lb);

		if (metaData.isComplemented())
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionReverse")).append(lb).append(lb);
		else
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionForward")).append(lb).append(lb);

		// Produce a FASTA formatted string
		text.append(TabletUtils.formatFASTA(Assembly.getReadNameData(read).getName(), seq));

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.exportLinkLabel)
		{
			try
			{
				ReadsCanvas rCanvas = aPanel.getReadsCanvas();
				Tablet.winMain.getCommands().exportScreenData(aPanel.getContig().getReadManager(), rCanvas.getXS(), rCanvas.getXE(), rCanvas.getYS(), rCanvas.getYE());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		else if (e.getSource() == mClipboardName)
		{
			Read read = getReadFromTable();
			if (read != null)
				copyReadNameToClipboard(read);
		}
		
		else if (e.getSource() == mClipboardData)
		{
			Read read = getReadFromTable();
			if (read != null)
				copyDataToClipboard(read);
		}
		
		else if (e.getSource() == mFindStart)
		{
			Read read = getReadFromTable();
			if (read == null)
				return;
			
			int position = read.getStartPosition();
			
			int lineIndex = aPanel.getContig().getReadManager().getLineForRead(read);

			aPanel.moveToPosition(lineIndex, position, true);
			new ReadHighlighter(aPanel, read, lineIndex);
		}
		
		else if (e.getSource() == mFindEnd)
		{
			Read read = getReadFromTable();
			if (read == null)
				return;
			
			int position = read.getEndPosition();
			
			int lineIndex = aPanel.getContig().getReadManager().getLineForRead(read);

			aPanel.moveToPosition(lineIndex, position, true);
			new ReadHighlighter(aPanel, read, lineIndex);
		}
		
		else if (e.getSource() == mJumpToPair)
		{
			Read read = getReadFromTable();
			if (read == null)
				return;
			
			if(read instanceof MatedRead)
			{
				final MatedRead mr = (MatedRead)read;
				
				aPanel.moveToPosition(0, mr.getMatePos(), true);

				PairSearcher pairSearcher = new PairSearcher(aPanel.getContig());

				try
				{
					// Must use searchForPair instead of search as DB query 
					// cannot be relied upon once the reads have been sorted
					final Read foundRead = pairSearcher.searchForPair(Assembly.getReadName(read), mr.getMatePos());
					final int lineIndex = aPanel.getContig().getReadManager().getLineForRead(foundRead);

					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							aPanel.moveToPosition(lineIndex, foundRead.getStartPosition(), true);
							new ReadHighlighter(aPanel, foundRead, lineIndex);
						}
					});
				}
				catch(Exception ex) { };
			}
		}
	}

	private void displayMenu(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		controls.table.setRowSelectionInterval(row, row);
		
		mClipboardName = new JMenuItem("", Icons.getIcon("CLIPBOARDNAME"));
		RB.setText(mClipboardName, "gui.viewer.ReadsCanvasMenu.mClipboardName");
		mClipboardName.addActionListener(this);

		mClipboardData = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboardData, "gui.viewer.ReadsCanvasMenu.mClipboardData");
		mClipboardData.addActionListener(this);
		
		JMenu mJumpTo = new JMenu("");
		RB.setText(mJumpTo, "gui.viewer.ReadsCanvasMenu.mJumpTo");
		
		mFindStart = new JMenuItem("", Icons.getIcon("START16"));
		RB.setText(mFindStart, "gui.viewer.ReadsCanvasMenu.mJumpToReadStart");
		mFindStart.addActionListener(this);

		mFindEnd = new JMenuItem("", Icons.getIcon("END16"));
		RB.setText(mFindEnd, "gui.viewer.ReadsCanvasMenu.mJumpToReadEnd");
		mFindEnd.addActionListener(this);

		mJumpToPair = new JMenuItem("");
		RB.setText(mJumpToPair, "gui.viewer.ReadsCanvasMenu.mJumpToPair");
		mJumpToPair.addActionListener(this);

		mJumpTo.add(mFindStart);
		mJumpTo.add(mFindEnd);
		mJumpTo.add(mJumpToPair);
		
		JPopupMenu menu = new JPopupMenu();
		menu.add(mClipboardName);
		menu.add(mClipboardData);
		menu.addSeparator();
		menu.add(mJumpTo);
		
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