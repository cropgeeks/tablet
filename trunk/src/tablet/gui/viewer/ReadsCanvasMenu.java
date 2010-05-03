// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;

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

	private JMenu mShadowing;
	private JCheckBoxMenuItem mShadowingOff;
	private JCheckBoxMenuItem mShadowingCenter;
	private JCheckBoxMenuItem mShadowingCustom;
	private JCheckBoxMenuItem mShadowingLock;
	private JMenuItem mShadowingJump;

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

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}