// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import javax.swing.*;

import tablet.data.*;

import scri.commons.gui.*;

class NavPanel
{
	static JPanel getLinksPanel(WinMain winMain)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(new Color(119, 126, 143), 3));

		JPanel logoPanel = new LogoPanel(new BorderLayout(0, 0));

		JPanel welcomePanel = new JPanel(new BorderLayout());
		welcomePanel.setOpaque(false);
		welcomePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		welcomePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartWelcomePanel.title")), BorderLayout.NORTH);
		welcomePanel.add(new StartPanelWelcomeNB());

		JPanel filePanel = new JPanel(new BorderLayout());
		filePanel.setOpaque(false);
		filePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		filePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartFilePanel.title")), BorderLayout.NORTH);
		filePanel.add(new StartPanelFileNB());

		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.setOpaque(false);
		helpPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		helpPanel.add(new TitlePanel3(
			RB.getString("gui.NBStartHelpPanel.title")), BorderLayout.NORTH);
		helpPanel.add(new StartPanelHelpNB());

		JPanel pubPanel = new JPanel(new BorderLayout());
		pubPanel.setOpaque(false);
		pubPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		pubPanel.add(new TitlePanel3(
			RB.getString("gui.NBStartPublicationPanel.title")), BorderLayout.NORTH);
		pubPanel.add(new StartPanelPublicationNB());

		JPanel centrePanel = new JPanel(new GridLayout(1, 2, 0, 0));
		centrePanel.setOpaque(false);
		centrePanel.add(filePanel);
		centrePanel.add(helpPanel);

		logoPanel.add(welcomePanel, BorderLayout.NORTH);
		logoPanel.add(centrePanel, BorderLayout.CENTER);
		logoPanel.add(pubPanel, BorderLayout.SOUTH);

		panel.add(logoPanel);

		return panel;
	}

	static JPanel getContigsPanel(Assembly assembly)
	{
		JPanel logoPanel = new LogoPanel(new BorderLayout(0, 0));

		String text = RB.getString("gui.NavPanel.message");
		logoPanel.add(new JLabel(text, JLabel.CENTER));

		return logoPanel;
	}

	private static class LogoPanel extends JPanel
	{
		private static ImageIcon logo = Icons.getIcon("SCRILARGE");

		LogoPanel(LayoutManager lm)
		{
			super(lm);
			setBackground(Color.white);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			int w = getWidth();

			g.drawImage(logo.getImage(), 0, 0, w, w, null);
		}
	}
}