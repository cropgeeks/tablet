package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

class ReadsCanvasMenu
{
	private ReadsCanvasInfoPane infoPane;
	private int menuShortcut;

	private AbstractAction aClipboardName;
	private AbstractAction aClipboardData;

	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem mClipboardName;
	private JMenuItem mClipboardData;

	ReadsCanvasMenu(ReadsCanvasInfoPane infoPane)
	{
		this.infoPane = infoPane;

		createActions();

		mClipboardName = getItem(aClipboardName,
			"gui.viewer.ReadsCanvasMenu.mClipboardName", 0, 0);
		mClipboardData = getItem(aClipboardData,
			"gui.viewer.ReadsCanvasMenu.mClipboardData", 0, 0);
	}

	private void createActions()
	{
		aClipboardName = new AbstractAction(
			RB.getString("gui.viewer.ReadsCanvasMenu.mClipboardName"),
			Icons.getIcon("CLIPBOARDNAME"))
		{
			public void actionPerformed(ActionEvent e) {
				infoPane.copyReadNameToClipboard();
			}
		};

		aClipboardData = new AbstractAction(
			RB.getString("gui.viewer.ReadsCanvasMenu.mClipboardData"),
			Icons.getIcon("CLIPBOARD"))
		{
			public void actionPerformed(ActionEvent e) {
				infoPane.copyDataToClipboard();
			}
		};
	}

	private JMenuItem getItem(Action action, String key, int keymask, int modifiers)
	{
		JMenuItem item = new JMenuItem(action);
		RB.setMnemonic(item, key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	void handlePopup(MouseEvent e)
	{
		// Create the menu
		menu = new JPopupMenu();
		menu.add(mClipboardName);
		menu.add(mClipboardData);

		// Check enabled states
		mClipboardName.setEnabled(infoPane.isOverRead());
		mClipboardData.setEnabled(infoPane.isOverRead());

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}