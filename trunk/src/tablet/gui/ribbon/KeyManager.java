// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

public class KeyManager
{
	static JComponent manager;

	static void createKeyboardShortcuts(final WinMain winMain)
	{
		// The "manager" just needs to be a component that is visible onscreen
		// at all times
		manager = RibbonController.titleLabel;
		int ctrl = Tablet.menuShortcut;


		// Open assembly
		Action open = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				RibbonController.bandAssemblies.openAssembly();
			}
		};
		map(open, KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrl), "open");



		// Zoom-in
		Action zoomIn = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				RibbonController.bandAdjust.zoomIn(1);
			}
		};
		map(zoomIn, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ctrl), "zoomInMain");
		map(zoomIn, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, ctrl), "zoomInNumPad");



		// Zoom-out
		Action zoomOut = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				RibbonController.bandAdjust.zoomOut(1);
			}
		};
		map(zoomOut, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ctrl), "zoomOutMain");
		map(zoomOut, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, ctrl), "zoomOutNumPad");



		// Zoom-reset
		Action zoomReset = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				RibbonController.bandAdjust.zoomReset();
			}
		};
		map(zoomReset, KeyStroke.getKeyStroke(KeyEvent.VK_0, ctrl), "zoomResetMain");
		map(zoomReset, KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, ctrl), "zoomResetNumPad");



		// Page left
		Action pageLeft = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (Actions.navigatePageLeft.isEnabled())
					RibbonController.bandNavigate.pageLeft();
			}
		};
		map(pageLeft, KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0), "pageLeft");



		// Page right
		Action pageRight = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (Actions.navigatePageRight.isEnabled())
					RibbonController.bandNavigate.pageRight();
			}
		};
		map(pageRight, KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0), "pageRight");



		// Jump to base
		Action jumpToBase = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (Actions.navigateJumpTo.isEnabled())
					RibbonController.bandNavigate.jumpToBase();
			}
		};
		map(jumpToBase, KeyStroke.getKeyStroke(KeyEvent.VK_J, ctrl), "jumpToBase");



		// Overlay read names
		Action readNames = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (Actions.overlayReadNames.isEnabled())
					RibbonController.bandOverlays.actionReadNames();
			}
		};
		map(readNames, KeyStroke.getKeyStroke(KeyEvent.VK_N, ctrl), "readNames");



		// Activate search panel
		Action find = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				winMain.focusOnFindPanel();
			}
		};
		map(find, KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl), "find");

		// Set up some keyboard navigation
		Action previousSnapshot = new AbstractAction() {
			public void actionPerformed(ActionEvent e)
				{ winMain.getAssemblyPanel().getSnapshotController().previousSnapshot(); }
		};
		map(previousSnapshot, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "previous");

		Action nextSnapshot = new AbstractAction() {
			public void actionPerformed(ActionEvent e)
				{ winMain.getAssemblyPanel().getSnapshotController().nextSnapshot(); }
		};
		map(nextSnapshot, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), "next");
	}

	public static void map(Action action, KeyStroke keyStroke, String command)
	{
		manager.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			keyStroke, command);
		manager.getActionMap().put(command, action);
	}
}