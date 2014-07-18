// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.gui.*;
import tablet.gui.viewer.*;
import tablet.gui.viewer.colors.*;

public class CustomizeColorsDialog extends JDialog implements ActionListener
{
	private AssemblyPanel aPanel;

	private CustomizeColorsPanelNB nbPanel;
	private JButton bClose, bReset, bHelp;

	public CustomizeColorsDialog(WinMain winMain)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.CustomizeColorsDialog.title"),
			true
		);

		aPanel = winMain.getAssemblyPanel();
		nbPanel = new CustomizeColorsPanelNB(aPanel);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bClose);
		getRootPane().setDefaultButton(bClose);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
		setVisible(true);
	}

	/**
	 * Create the button bar which can be seen at the bottom of the dialog.
	 */
	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bReset = SwingUtils.getButton(RB.getString("gui.text.default"));
		bReset.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		bHelp.addActionListener(this);

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bClose);
		p1.add(bReset);
//		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bReset)
		{
			String msg = RB.getString("gui.dialog.CustomizeColorsDialog.confirmReset");
			String[] options = new String[] {
				RB.getString("gui.dialog.CustomizeColorsDialog.reset"),
				RB.getString("gui.text.cancel")
			};

			if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
				return;

			ColorPrefs.resetUserColors();
			nbPanel.initializeList();
			aPanel.forceRedraw();
		}
	}
}