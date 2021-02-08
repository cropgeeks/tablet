// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class FeaturesDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private FeaturesPanelNB nbPanel;

	private boolean isOK = false;

	public FeaturesDialog()
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.FeaturesDialog.title"),
			true
		);

		nbPanel = new FeaturesPanelNB();

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bCancel);
		getRootPane().setDefaultButton(bOK);

		nbPanel.addEnzyme.addActionListener(this);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
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
		TabletUtils.setHelp(bHelp, "importing_features.html");

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			nbPanel.updateList();
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == nbPanel.addEnzyme)
		{
			Tablet.winMain.getRestrictionEnzymeDialog().setVisible(true);
			nbPanel.createTable();
		}
	}

	public boolean isOK()
		{ return isOK; }
}