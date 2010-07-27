// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.auxiliary.*;
import tablet.gui.*;

import scri.commons.gui.*;

public class FeaturesDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private NBFeaturesPanel nbPanel;

	public FeaturesDialog()
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.FeaturesDialog.title"),
			false
		);

		nbPanel = new NBFeaturesPanel();

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bCancel);
		getRootPane().setDefaultButton(bOK);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		TabletUtils.setHelp(bHelp, "gui.dialog.JumpToDialog");

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK && nbPanel.isOK())
			setVisible(false);

		else if (e.getSource() == bCancel)
			setVisible(false);
	}
}