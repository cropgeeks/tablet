// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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
	private JMenuItem mFindStart, mFindEnd;
	private JMenu mOutline;
	private JMenuItem mOutlineCol;
	private JMenuItem mOutlineRow;
	private JMenuItem mOutlineClear;

	private JMenuItem mExportColumn;
	private JMenuItem mExportScreen;
	//private JMenuItem mExportContig;

	private JMenu mShadowing;
	private JCheckBoxMenuItem mShadowingOff;
	private JCheckBoxMenuItem mShadowingCenter;
	private JCheckBoxMenuItem mShadowingCustom;
	private JCheckBoxMenuItem mShadowingLock;
	private JMenuItem mShadowingJump;

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
		RB.setText(mFindStart, "gui.viewer.ReadsCanvasMenu.mFindStart");
		mFindStart.addActionListener(this);

		mFindEnd = new JMenuItem("", Icons.getIcon("END16"));
		RB.setText(mFindEnd, "gui.viewer.ReadsCanvasMenu.mFindEnd");
		mFindEnd.addActionListener(this);

		mOutline = new JMenu("");
		RB.setText(mOutline, "gui.viewer.ReadsCanvasMenu.mOutline");

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

		mJumpToPair = new JMenuItem("");
		RB.setText(mJumpToPair, "gui.viewer.ReadsCanvasMenu.mJumpToPair");
		mJumpToPair.addActionListener(this);

		mJumpToLeftRead = new JMenuItem("");
		RB.setText(mJumpToLeftRead, "gui.viewer.ReadsCanvasMenu.mJumpToLeftRead");
		mJumpToLeftRead.addActionListener(this);

		mJumpToRightRead = new JMenuItem("");
		RB.setText(mJumpToRightRead, "gui.viewer.ReadsCanvasMenu.mJumpToRightRead");
		mJumpToRightRead.addActionListener(this);

		mExportColumn = new JMenuItem("");
		RB.setText(mExportColumn, "gui.viewer.ReadsCanvasMenu.mExportColumn");
		mExportColumn.addActionListener(this);

		mExportScreen = new JMenuItem("");
		RB.setText(mExportScreen, "gui.viewer.ReadsCanvasMenu.mExportScreen");
		mExportScreen.addActionListener(this);

//		mExportContig = new JMenuItem("");
//		RB.setText(mExportContig, "gui.viewer.ReadsCanvasMenu.mExportScreen");
//		mExportContig.addActionListener(this);


		// Create the menu
		menu = new JPopupMenu();

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

		menu.add(mOutline);
		menu.add(mShadowing);
		menu.addSeparator();
		menu.add(mClipboardName);
		menu.add(mClipboardData);
		menu.addSeparator();
		menu.add(mFindStart);
		menu.add(mFindEnd);
		menu.addSeparator();
		menu.add(mJumpToPair);
		menu.add(mJumpToLeftRead);
		menu.add(mJumpToRightRead);

		menu.addSeparator();
		menu.add(mExportColumn);
		menu.add(mExportScreen);
		//menu.add(mExportContig);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mClipboardName)
			infoPane.copyReadNameToClipboard();
		else if (e.getSource() == mClipboardData)
			infoPane.copyDataToClipboard();

		else if (e.getSource() == mFindStart)
		{
			int position = infoPane.read.getStartPosition();

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, infoPane.read, infoPane.lineIndex);
		}
		else if (e.getSource() == mFindEnd)
		{
			int position = infoPane.read.getEndPosition();

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, infoPane.read, infoPane.lineIndex);
		}

		else if (e.getSource() == mOutlineCol)
		{
			rCanvas.contig.addOutline(
				new Feature(Feature.COL_OUTLINE, null, null, colIndex, colIndex));
		}
		else if (e.getSource() == mOutlineRow)
		{
			rCanvas.contig.addOutline(
				new Feature(Feature.ROW_OUTLINE, null, null, rowIndex, rowIndex));
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

					final Read r = pairSearcher.searchForPair(readData.getName(), pr.getMatePos());
					final int lineIndex = rCanvas.reads.getLineForRead(r);

					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							aPanel.moveToPosition(lineIndex, r.getStartPosition(), true);
							new ReadHighlighter(aPanel, r, lineIndex);
						}
					 });
				}
				catch(Exception ex)
				{

				}
			}
		}

		else if (e.getSource() == mJumpToLeftRead)
		{
			Read[] pair = null;

			if(rCanvas.reads instanceof PairedStack)
			{
				PairedStack set = (PairedStack)rCanvas.reads;
				pair = set.getPairAtLine(rowIndex, colIndex);
			}
			else if(rCanvas.reads instanceof PackSet)
			{
				PackSet set = (PackSet)rCanvas.reads;
				pair = set.getPairAtLine(rowIndex, colIndex);
			}

			if(pair != null && pair[0] != null)
			{
				aPanel.moveToPosition(-1, pair[0].getStartPosition(), true);
				new ReadHighlighter(aPanel, pair[0], rowIndex);
			}
		}

		else if (e.getSource() == mJumpToRightRead)
		{
			Read[] pair = null;

			if(rCanvas.reads instanceof PairedStack)
			{
				PairedStack set = (PairedStack)rCanvas.reads;
				pair = set.getPairAtLine(rowIndex, colIndex);
			}
			else if(rCanvas.reads instanceof PackSet)
			{
				PackSet set = (PackSet)rCanvas.reads;
				pair = set.getPairAtLine(rowIndex, colIndex);
			}

			if(pair != null && pair[1] != null)
			{
				aPanel.moveToPosition(-1, pair[1].getStartPosition(), true);
				new ReadHighlighter(aPanel, pair[1], rowIndex);
			}
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
				int xS = (rCanvas.pX1 - rCanvas.offset) / rCanvas.ntW;
				int xE = (rCanvas.pX2 - rCanvas.offset) / rCanvas.ntW;
				Tablet.winMain.getCommands().exportScreenData(rCanvas.reads, xS, xE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
//
//		else if (e.getSource() == mExportContig)
//		{
//			try
//			{
//				Tablet.winMain.getCommands().exportContigData(rCanvas.reads);
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
//		}
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	void handlePopup(MouseEvent e)
	{
		rowIndex = (e.getY() / rCanvas.ntH);
		colIndex = (e.getX() / rCanvas.ntW) + rCanvas.offset;

		// Check enabled states
		boolean isOverRead = infoPane.isOverRead();
		mClipboardName.setEnabled(isOverRead);
		mClipboardData.setEnabled(isOverRead);
		mFindStart.setEnabled(isOverRead);
		mFindEnd.setEnabled(isOverRead);
		mOutlineClear.setEnabled(rCanvas.contig.getOutlines().size() > 0);

		// Shadowing options
		mShadowingOff.setSelected(Prefs.visReadShadowing == 0);
		mShadowingCenter.setSelected(Prefs.visReadShadowing == 1);
		mShadowingCustom.setSelected(Prefs.visReadShadowing == 2);
		Integer base = aPanel.getVisualContig().getLockedBase();
		mShadowingLock.setSelected(base != null);
		mShadowingLock.setEnabled(Prefs.visReadShadowing == 2);
		mShadowingJump.setEnabled(Prefs.visReadShadowing == 2 && base != null);

		// Paired end states
		Read[] pair = null;
		Read read = null;

		if(rCanvas.reads instanceof PairedStack)
		{
			PairedStack set = (PairedStack)rCanvas.reads;
			pair = set.getPairAtLine(rowIndex, colIndex);
			read = set.getReadAt(rowIndex, colIndex);
		}
		else if(rCanvas.reads instanceof PackSet)
		{
			PackSet set = (PackSet)rCanvas.reads;
			pair = set.getPairAtLine(rowIndex, colIndex);
			read = set.getReadAt(rowIndex, colIndex);
		}

		ReadNameData rnd = null;
		ReadMetaData rmd = null;
		if(read != null )
		{
			rnd = Assembly.getReadNameData(read);
			rmd = Assembly.getReadMetaData(read, false);
		}

		if(read instanceof MatedRead && rnd != null && rmd.getMateMapped())
		{
			mJumpToPair.setEnabled(isOverRead);
			mJumpToPair.setText(RB.format("gui.viewer.ReadsCanvasMenu.mJumpToPairInContig", rnd.getName(), rnd.getMateContig()));
		}
		else
		{
			mJumpToPair.setEnabled(false);
			RB.setText(mJumpToPair, "gui.viewer.ReadsCanvasMenu.mJumpToPair");
		}

		mJumpToLeftRead.setEnabled(!isOverRead && pair != null);
		mJumpToRightRead.setEnabled(!isOverRead && pair != null);

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
		{
			cPanel.setDisplayedContig(contig);
		}
	}

}