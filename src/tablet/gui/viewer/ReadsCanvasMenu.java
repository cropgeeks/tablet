package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

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

		mOutline.add(mOutlineRow);
		mOutline.add(mOutlineCol);
		mOutline.addSeparator();
		mOutline.add(mOutlineClear);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mClipboardName)
			infoPane.copyReadNameToClipboard();
		else if (e.getSource() == mClipboardData)
			infoPane.copyDataToClipboard();

		else if (e.getSource() == mFindStart)
		{
			int position = infoPane.read.getStartPosition() + rCanvas.offset;

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, infoPane.read, infoPane.lineIndex);
		}
		else if (e.getSource() == mFindEnd)
		{
			int position = infoPane.read.getEndPosition() + rCanvas.offset;

			aPanel.moveToPosition(-1, position, true);
			new ReadHighlighter(aPanel, infoPane.read, infoPane.lineIndex);
		}

		else if (e.getSource() == mOutlineCol)
		{
			rCanvas.contig.addOutline(
				new Feature(null, Feature.COL_OUTLINE, colIndex, colIndex));
		}
		else if (e.getSource() == mOutlineRow)
		{
			rCanvas.contig.addOutline(
				new Feature(null, Feature.ROW_OUTLINE, rowIndex, rowIndex));
		}
		else if (e.getSource() == mOutlineClear)
			rCanvas.contig.getOutlines().clear();
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	void handlePopup(MouseEvent e)
	{
		rowIndex = (e.getY() / rCanvas.ntH);
		colIndex = (e.getX() / rCanvas.ntW) - rCanvas.offset;

		// Create the menu
		menu = new JPopupMenu();
		menu.add(mOutline);
		menu.addSeparator();
		menu.add(mClipboardName);
		menu.add(mClipboardData);
		menu.addSeparator();
		menu.add(mFindStart);
		menu.add(mFindEnd);

		// Check enabled states
		boolean isOverRead = infoPane.isOverRead();
		mClipboardName.setEnabled(isOverRead);
		mClipboardData.setEnabled(isOverRead);
		mFindStart.setEnabled(isOverRead);
		mFindEnd.setEnabled(isOverRead);
		mOutlineClear.setEnabled(rCanvas.contig.getOutlines().size() > 0);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}