// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import scri.commons.gui.*;

class Nimbus
{
	private static final Color BACKGROUND = new Color(240, 240, 240);
	private static final Font FONT = new Font("Tahoma", Font.PLAIN, 11);

	static void customizeNimbus()
		throws Exception
	{
//		UIManager.put("nimbusBase", BACKGROUND);
//		UIManager.put("nimbusBlueGrey", BACKGROUND);
		UIManager.put("control", BACKGROUND);

		// TODO: imilne 04/SEP/2009 - No longer working since JRE 1.6.0_u13
		if (SystemUtils.isWindows())
		{
			UIManager.put("defaultFont", FONT);
			UIManager.put("Label[Enabled].font", FONT);
			UIManager.put("Table[Enabled].font", FONT);
			UIManager.put("TableHeader[Enabled].font", FONT);
			UIManager.put("TabbedPane[Enabled].font", FONT);
			UIManager.put("ComboBox[Enabled].font", FONT);
			UIManager.put("Button[Enabled].font", FONT);
			UIManager.put("ToggleButton[Enabled].font", FONT);
			UIManager.put("TextField[Enabled].font", FONT);
			UIManager.put("CheckBox[Enabled].font", FONT);
		}

		for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
			if (laf.getName().equals("Nimbus"))
				UIManager.setLookAndFeel(laf.getClassName());


		UIManager.put("SplitPane[Enabled].size", 8);

		UIManager.put("nimbusOrange", new Color(51, 98, 140));
//		UIManager.put("nimbusOrange", new Color(57, 105, 138));
//		UIManager.put("nimbusOrange", new Color(115, 164, 209));


		// Reset non-Aqua look and feels to use CMD+C/X/V rather than CTRL for copy/paste stuff
		if (SystemUtils.isMacOS())
		{
			System.out.println("Setting OS X keyboard shortcuts");

			InputMap textField = (InputMap) UIManager.get("TextField.focusInputMap");
			textField.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			textField.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			textField.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			textField.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);

			InputMap textArea = (InputMap) UIManager.get("TextArea.focusInputMap");
			textArea.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			textArea.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			textArea.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			textArea.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
		}
	}
}