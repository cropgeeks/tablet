// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import static tablet.gui.ribbon.RibbonController.*;

import scri.commons.gui.*;


class ReadsCanvasMenu implements ActionListener
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ReadsCanvasInfoPane infoPane;

	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem mClipboardName;
	private JMenuItem mClipboardData;

	private JMenu mOutline;
	private JMenuItem mOutlineRead;
	private JMenuItem mOutlineCol;
	private JMenuItem mOutlineRow;
	private JMenuItem mOutlineClear;

	private JMenuItem mExport;
	private JMenuItem mExportColumn;
	private JMenuItem mExportScreen;
	private JMenuItem mExportContig;

	private JMenu mShadowing;
	private JCheckBoxMenuItem mShadowingOff;
	private JCheckBoxMenuItem mShadowingCenter;
	private JCheckBoxMenuItem mShadowingCustom;
	private JCheckBoxMenuItem mShadowingLock;
	private JMenuItem mShadowingJump;

	private JMenu mJumpTo;
	private JMenuItem mFindStart, mFindEnd;
	private JMenuItem mJumpToPair;
	private JMenuItem mJumpToLeftRead;
	private JMenuItem mJumpToRightRead;

	// Row and column under the mouse at the time the menu appears
	private int rowIndex, colIndex;

	ReadsCanvasMenu(AssemblyPanel aPanel, ReadsCanvasInfoPane infoPane)
	{
		this.aPanel = aPanel;
		this.infoPane = infoPane;
		rCanvas = aPanel.readsCanvas;

		mClipboardName = new JMenuItem("", Icons.getIcon("CLIPBOARDNAME"));
		RB.setText(mClipboardName, "gui.viewer.ReadsCanvasMenu.mClipboardName");
		mClipboardName.addActionListener(this);

		mClipboardData = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboardData, "gui.viewer.ReadsCanvasMenu.mClipboardData");
		mClipboardData.addActionListener(this);

		mFindStart = new JMenuItem("", Icons.getIcon("START16"));
		RB.setText(mFindStart, "gui.viewer.ReadsCanvasMenu.mJumpToReadStart");
		mFindStart.addActionListener(this);

		mFindEnd = new JMenuItem("", Icons.getIcon("END16"));
		RB.setText(mFindEnd, "gui.viewer.ReadsCanvasMenu.mJumpToReadEnd");
		mFindEnd.addActionListener(this);

		mOutline = new JMenu("");
		RB.setText(mOutline, "gui.viewer.ReadsCanvasMenu.mOutline");

		mOutlineRead = new JMenuItem("");
		RB.setText(mOutlineRead, "gui.viewer.ReadsCanvasMenu.mOutlineRead");
		mOutlineRead.addActionListener(this);

		mOutlineRow = new JMenuItem("", Icons.getIcon("ROW16"));
		RB.setText(mOutlineRow, "gui.viewer.ReadsCanvasMenu.mOutlineRow");
		mOutlineRow.addActionListener(this);

		mOutlineCol = new JMenuItem("", Icons.getIcon("COLUMN16"));
		RB.setText(mOutlineCol, "gui.viewer.ReadsCanvasMenu.mOutlineCol");
		mOutlineCol.addActionListener(this);

		mOutlineClear = new JMenuItem("");
		RB.setText(mOutlineClear, "gui.viewer.ReadsCanvasMenu.mOutlineClear");
		mOutlineClear.addActionListener(this);

		mShadowing = new JMenu("");
		RB.setText(mShadowing, "gui.viewer.ReadsCanvasMenu.mShadowing");

		mShadowingOff = new JCheckBoxMenuItem("");
		RB.setText(mShadowingOff, "gui.viewer.ReadsCanvasMenu.mShadowingOff");
		mShadowingOff.addActionListener(this);

		mShadowingCenter = new JCheckBoxMenuItem("");
		RB.setText(mShadowingCenter, "gui.viewer.ReadsCanvasMenu.mShadowingCenter");
		mShadowingCenter.addActionListener(this);

		mShadowingCustom = new JCheckBoxMenuItem("");
		RB.setText(mShadowingCustom, "gui.viewer.ReadsCanvasMenu.mShadowingCustom");
		mShadowingCustom.addActionListener(this);

		mShadowingLock = new JCheckBoxMenuItem("");
		RB.setText(mShadowingLock, "gui.viewer.ReadsCanvasMenu.mShadowingLock");
		mShadowingLock.addActionListener(this);

		mShadowingJump = new JMenuItem("");
		RB.setText(mShadowingJump, "gui.viewer.ReadsCanvasMenu.mShadowingJump");
		mShadowingJump.addActionListener(this);

		mJumpTo = new JMenu("");
		RB.setText(mJumpTo, "gui.viewer.ReadsCanvasMenu.mJumpTo");

		mJumpToPair = new JMenuItem("");
		RB.setText(mJumpToPair, "gui.viewer.ReadsCanvasMenu.mJumpToPair");
		mJumpToPair.addActionListener(this);

		mJumpToLeftRead = new JMenuItem("");
		RB.setText(mJumpToLeftRead, "gui.viewer.ReadsCanvasMenu.mJumpToLeftRead");
		mJumpToLeftRead.addActionListener(this);

		mJumpToRightRead = new JMenuItem("");
		RB.setText(mJumpToRightRead, "gui.viewer.ReadsCanvasMenu.mJumpToRightRead");
		mJumpToRightRead.addActionListener(this);

		mExport = new JMenu("");
		RB.setText(mExport, "gui.viewer.ReadsCanvasMenu.mExport");

		mExportColumn = new JMenuItem("");
		RB.setText(mExportColumn, "gui.viewer.ReadsCanvasMenu.mExportColumn");
		mExportColumn.addActionListener(this);

		mExportScreen = new JMenuItem("");
		RB.setText(mExportScreen, "gui.viewer.ReadsCanvasMenu.mExportScreen");
		mExportScreen.addActionListener(this);

		mExportContig = new JMenuItem("");
		RB.setText(mExportContig, "gui.viewer.ReadsCanvasMenu.mExportContig");
		mExportContig.addActionListener(this);

		// Create the menu
		menu = new JPopupMenu();

		mOutline.add(mOutlineRead);
		mOutline.addSeparator();
		mOutline.add(mOutlineRow);
		mOutline.add(mOutlineCol);
		mOutline.addSeparator();
		mOutline.add(mOutlineClear);

		mShadowing.add(mShadowingOff);
		mShadowing.addSeparator();
		mShadowing.add(mShadowingCenter);
		mShadowing.add(mShadowingCustom);
		mShadowing.addSeparator();
		mShadowing.add(mShadowingLock);
		mShadowing.add(mShadowingJump);

		mJumpTo.add(mFindStart);
		mJumpTo.add(mFindEnd);
		mJumpTo.add(mJumpToPair);
		mJumpTo.addSeparator();
		mJumpTo.add(mJumpToLeftRead);
		mJumpTo.add(mJumpToRightRead);

		mExport.add(mExportColumn);
		mExport.add(mExportScreen);
		mExport.add(mExportContig);

		menu.add(mOutline);
		menu.add(mShadowing);
		menu.addSeparator();
		menu.add(mClipboardName);
		menu.add(mClipboardData);
		menu.addSeparator();
		menu.add(mJumpTo);
		menu.addSeparator();
		menu.add(mExport);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mClipboardName)
			infoPane.copyReadNameToClipboard();
		else if (e.getSource() == mClipboardData)
			infoPane.copyDataToClipboard();

		else if (e.getSource() == mFindStart)
		{
			Read read = rCanvas.reads.getReadAt(rowIndex, colIndex);
			int position = read.s();

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, read, rowIndex);
		}
		else if (e.getSource() == mFindEnd)
		{
			Read read = rCanvas.reads.getReadAt(rowIndex, colIndex);
			int position = read.e();

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, read, rowIndex);
		}

		else if (e.getSource() == mOutlineRead)
		{
			Read read = rCanvas.reads.getReadAt(rowIndex, colIndex);
			int p1 = read.s();
			int p2 = read.e();

			rCanvas.contig.addOutline(
				new VisualOutline(VisualOutline.READ, p1, p2, rowIndex));
		}

		else if (e.getSource() == mOutlineCol)
		{
			rCanvas.contig.addOutline(
				new VisualOutline(VisualOutline.COL, colIndex));
		}
		else if (e.getSource() == mOutlineRow)
		{
			rCanvas.contig.addOutline(
				new VisualOutline(VisualOutline.ROW, rowIndex));
		}
		else if (e.getSource() == mOutlineClear)
			rCanvas.contig.getOutlines().clear();

		else if (e.getSource() == mShadowingOff)
			bandOverlays.actionShadowingOff();

		else if (e.getSource() == mShadowingCenter)
			bandOverlays.actionShadowingCenter();

		else if (e.getSource() == mShadowingCustom)
			bandOverlays.actionShadowingCustom();

		// Toggle shadowing locking on/off
		else if (e.getSource() == mShadowingLock)
		{
			if (aPanel.getVisualContig().getLockedBase() != null)
				aPanel.getVisualContig().setLockedBase(null);
			else
				aPanel.getVisualContig().setLockedBase(ReadShadower.mouseBase);
		}

		// Jump to base...
		else if (e.getSource() == mShadowingJump)
			aPanel.moveToPosition(-1,
				aPanel.getVisualContig().getLockedBase(), true);

		else if (e.getSource() == mJumpToPair)
		{
			Read read = rCanvas.reads.getReadAt(rowIndex, colIndex);
			ReadNameData readData = Assembly.getReadNameData(read);

			MatedRead pr = (MatedRead)read;

			if(!readData.getMateContig().equals(aPanel.getContig().getName()))
			{
				for(Contig contig : aPanel.getAssembly())
				{
					if(contig.getName().equals(readData.getMateContig()))
					{
						//aPanel.setContig(contig);
						updateContigsTable(contig);
						break;
					}
				}
			}

			if(pr.getMatePos() != -1)
			{
				try
				{
					aPanel.moveToPosition(0, pr.getMatePos(), true);

					PairSearcher pairSearcher = new PairSearcher(rCanvas.contig);

					// Must use searchForPair instead of search as DB query
					// cannot be relied upon once the reads have been sorted
					final Read r = pairSearcher.searchForPair(Assembly.getReadName(read), pr.getMatePos());

					final int lineIndex = rCanvas.reads.getLineForRead(r);

					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							aPanel.moveToPosition(lineIndex, r.s(), true);
							new ReadHighlighter(aPanel, r, lineIndex);
						}
					 });
				}
				catch(Exception ex)
				{

				}
			}
		}

		// From a link line, jump to the read on its LHS
		else if (e.getSource() == mJumpToLeftRead)
		{
			Read[] pair = rCanvas.reads.getPairForLink(rowIndex, colIndex);

			aPanel.moveToPosition(-1, pair[0].s(), true);
			new ReadHighlighter(aPanel, pair[0], rowIndex);
		}

		// From a link line, jump to the read on its RHS
		else if (e.getSource() == mJumpToRightRead)
		{
			Read[] pair = rCanvas.reads.getPairForLink(rowIndex, colIndex);

			aPanel.moveToPosition(-1, pair[1].s(), true);
			new ReadHighlighter(aPanel, pair[1], rowIndex);
		}

		else if (e.getSource() == mExportColumn)
		{
			try
			{
				Tablet.winMain.getCommands().exportColumnData(rCanvas.reads, colIndex);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		else if (e.getSource() == mExportScreen)
		{
			try
			{
				Tablet.winMain.getCommands().exportScreenData(rCanvas.reads, rCanvas.xS, rCanvas.xE, rCanvas.yS, rCanvas.yE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		else if (e.getSource() == mExportContig)
		{
			try
			{
				Tablet.winMain.getCommands().exportContigData(rCanvas.reads);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	void handlePopup(MouseEvent e)
	{
		colIndex = rCanvas.getBaseForPixel(e.getX());
		rowIndex = (e.getY() / rCanvas.ntH);

		if (aPanel.getAssembly().getBamBam() == null)
			RB.setText(mExportContig, "gui.viewer.ReadsCanvasMenu.mExportContig");
		else
			RB.setText(mExportContig, "gui.viewer.ReadsCanvasMenu.mExportBamWindow");

		// Check enabled states
		boolean isOverRead = infoPane.isOverRead();
		mClipboardName.setEnabled(isOverRead);
		mClipboardData.setEnabled(isOverRead);
		mFindStart.setEnabled(isOverRead);
		mFindEnd.setEnabled(isOverRead);
		mJumpToPair.setEnabled(false);
		mOutlineRead.setEnabled(isOverRead);
		mOutlineClear.setEnabled(rCanvas.contig.getOutlines().size() > 0);

		// Shadowing options
		mShadowingOff.setSelected(Prefs.visReadShadowing == 0);
		mShadowingCenter.setSelected(Prefs.visReadShadowing == 1);
		mShadowingCustom.setSelected(Prefs.visReadShadowing == 2);
		Integer base = aPanel.getVisualContig().getLockedBase();
		mShadowingLock.setSelected(base != null);
		mShadowingLock.setEnabled(Prefs.visReadShadowing == 2);
		mShadowingJump.setEnabled(Prefs.visReadShadowing == 2 && base != null);

		Read read = null;

		IReadManager manager = rCanvas.reads;
		read = manager.getReadAt(rowIndex, colIndex);

		ReadMetaData rmd = null;

		if (read != null && read.isNotMateLink())
		{
			rmd = Assembly.getReadMetaData(read, false);

			if (read instanceof MatedRead && rmd.getMateMapped())
				mJumpToPair.setEnabled(true);
		}

		// Check if we're over a link line for a pair
		Read[] pair = rCanvas.reads.getPairForLink(rowIndex, colIndex);
		mJumpToLeftRead.setEnabled(pair != null);
		mJumpToRightRead.setEnabled(pair != null);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Update the contigs table such that the correct contig is selected.
	 *
	 * @param contig
	 */
	public void updateContigsTable(Contig contig)
	{
		ContigsPanel cPanel = Tablet.winMain.getContigsPanel();
		boolean foundInTable = false;
		for(int i=0; i < cPanel.getTable().getRowCount(); i++)
		{
			if(cPanel.getTable().getValueAt(i, 0).equals(contig))
			{
				cPanel.getTable().setRowSelectionInterval(i, i);
				Rectangle r = cPanel.getTable().getCellRect(i, 0, true);
				cPanel.getTable().scrollRectToVisible(r);
				foundInTable = true;
				break;
			}
		}

		if (!foundInTable)
			cPanel.setDisplayedContig(contig);
	}
}