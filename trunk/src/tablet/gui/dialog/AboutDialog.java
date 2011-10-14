// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

public class AboutDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private AboutPanelNB nbPanel;

	public AboutDialog()
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.AboutDialog.title"),
			true
		);

		nbPanel = new AboutPanelNB();

		AvatarPanel avatars = new AvatarPanel();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab1"), nbPanel);
		tabs.add(RB.format("gui.dialog.AboutDialog.tab2", "\u0026"), avatars);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	private class AvatarPanel extends JPanel
	{
		AvatarPanel()
		{
			TabletUtils.setPanelColor(this, false);
			add(new JLabel(Icons.getIcon("AVATARS")));

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseMoved(MouseEvent e)
				{
					int x = e.getX();

					String tooltip = "<html>";

					if (x < 105)
						tooltip += "Iain Milne";
					else if (x >= 100 && x < 185)
						tooltip += "Gordon Stephen";
					else if (x >= 185 && x < 285)
						tooltip += "Micha Bayer";
					else if (x >= 285 && x < 380)
						tooltip += "Linda Cardle";
					else if (x >= 380 && x < 475)
						tooltip += "Paul Shaw";
					else
						tooltip += "David Marshall";

					setToolTipText(tooltip);
				}
			});
		}
	}
}