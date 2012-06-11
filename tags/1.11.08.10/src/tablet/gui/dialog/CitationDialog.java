// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

public class CitationDialog extends JDialog implements ActionListener
{
	private CitationDialogNB panel;
	private Timer timer;
	private short increments = 0;

	public CitationDialog()
	{
		super(
			Tablet.winMain,
			"Citing Tablet",
			true
		);

		add(panel = new CitationDialogNB());

		timer = new Timer(1000, this);
		timer.setInitialDelay(1000);
		timer.start();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		increments++;
		if (10-increments == 1)
			panel.link1.setText("You can close this window in " + (10-increments)  + " second");
		else
			panel.link1.setText("You can close this window in " + (10-increments)  + " seconds");

		if (increments == 10)
		{
			panel.link1.setEnabled(true);
			timer.stop();
			panel.link1.setText("Click here to close this window");
			setDefaultCloseOperation(HIDE_ON_CLOSE);
		}
	}
}