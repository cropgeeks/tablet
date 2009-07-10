package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;

import tablet.data.*;

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

		mFindStart = new JMenuItem("");
		RB.setText(mFindStart, "gui.viewer.ReadsCanvasMenu.mFindStart");
		mFindStart.addActionListener(this);

		mFindEnd = new JMenuItem("");
		RB.setText(mFindEnd, "gui.viewer.ReadsCanvasMenu.mFindEnd");
		mFindEnd.addActionListener(this);
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
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	void handlePopup(MouseEvent e)
	{
		// Create the menu
		menu = new JPopupMenu();
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

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}