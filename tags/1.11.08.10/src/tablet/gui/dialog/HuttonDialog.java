// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import javax.swing.*;

import tablet.gui.*;
import scri.commons.gui.SwingUtils;

public class HuttonDialog extends JDialog
{
	private HuttonDialogNB nbPanel;

	public HuttonDialog()
	{
		super(
			Tablet.winMain,
			"Important News",
			true
		);

		add(new HuttonDialogNB());
		SwingUtils.addCloseHandler(this, new JButton());

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}
}