// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class BamWindowDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private BamWindowPanelNB nbPanel;

	private boolean isOK = false;

	public BamWindowDialog(WinMain winMain)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.BamWindowDialog.title"),
			true
		);

		nbPanel = new BamWindowPanelNB();

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setResizable(false);
		setLocationRelativeTo(winMain);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = new JButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		TabletUtils.setHelp(bHelp, "gui.dialog.BamWindowSize");

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public boolean isOK()
		{ return isOK; }

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == bOK)
		{
			Prefs.bamSize = (Integer) nbPanel.bamSpinner.getValue();

			isOK = true;
			setVisible(false);
		}
	}
}