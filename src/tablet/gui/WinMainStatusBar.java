package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

class WinMainStatusBar extends JPanel implements ActionListener
{
	private JLabel tipsLabel, helpLabel;
	private ArrayList<String> helpHints = new ArrayList<String>();

	private int bgColor;

	WinMainStatusBar()
	{
		// Scan the properties file looking for tip strings to add
		for (int i = 1; i < 1000; i++)
		{
			if (RB.exists("gui.StatusBar.help" + i) == false)
				break;

			// Format them based on shortcuts for OS X or Windows/Linux
			if (SystemUtils.isMacOS())
				helpHints.add(RB.format(
					"gui.StatusBar.help" + i, RB.getString("gui.text.cmnd")));
			else
				helpHints.add(RB.format(
					"gui.StatusBar.help" + i, RB.getString("gui.text.ctrl")));
		}

		bgColor = getBackground().getRed();

		tipsLabel = new JLabel(RB.getString("gui.StatusBar.helpText"));
		helpLabel = new JLabel();

		JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
		helpPanel.add(tipsLabel);
		helpPanel.add(helpLabel);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
		add(helpPanel, BorderLayout.WEST);

		// Start the timer
		javax.swing.Timer timer = new javax.swing.Timer(30000, this);
		timer.setInitialDelay(0);
		timer.start();
	}

	public void actionPerformed(ActionEvent e)
	{
		new TipsThread().start();
	}

	private class TipsThread extends Thread
	{
		public void run()
		{
			setName("TipsThread");

			float step = (bgColor / 15f);
			float fontColor = 0;

			// Fade from black to bgColor
			for (int i = 0; i < 16 || fontColor < bgColor; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor += step;

				try { Thread.sleep(100); }
				catch (Exception ex) {}
			}

			// Then wait for a second
			try { Thread.sleep(1000); }
			catch (Exception ex) {}

			// Before picking a new help string...
			int index = new Random().nextInt(helpHints.size());
			helpLabel.setText(helpHints.get(index));
			fontColor = bgColor;

			// ...and then fadding the font back to black
			for (int i = 0; i < 16 || fontColor > 0f; i++)
			{
				int c = (int) fontColor;
				helpLabel.setForeground(new Color(c, c, c));

				fontColor -= step;

				try { Thread.sleep(100); }
				catch (Exception ex) {}
			}
		}
	}
}