/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablet.gui;

import java.awt.*;
import javax.swing.*;
import scri.commons.gui.*;

/**
 *
 * @author gsteph
 */
public class NavPanel extends JPanel
{
	NavPanel(WinMain winMain)
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(new Color(119, 126, 143), 3));

		JPanel panel = new LogoPanel(new BorderLayout(0, 0));

		JPanel welcomePanel = new JPanel(new BorderLayout());
		welcomePanel.setOpaque(false);
		welcomePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		welcomePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartWelcomePanel.title")), BorderLayout.NORTH);
		welcomePanel.add(new NBStartWelcomePanel());

		JPanel filePanel = new JPanel(new BorderLayout());
		filePanel.setOpaque(false);
		filePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		filePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartFilePanel.title")), BorderLayout.NORTH);
		filePanel.add(new NBStartFilePanel());

		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.setOpaque(false);
		helpPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		helpPanel.add(new TitlePanel3(
			RB.getString("gui.NBStartHelpPanel.title")), BorderLayout.NORTH);
		helpPanel.add(new NBStartHelpPanel());

		JPanel centrePanel = new JPanel(new GridLayout(1, 2, 0, 0));
		centrePanel.setOpaque(false);
		centrePanel.add(filePanel);
		centrePanel.add(helpPanel);

		panel.add(welcomePanel, BorderLayout.NORTH);
		panel.add(centrePanel, BorderLayout.CENTER);

		add(panel);
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
			int h = getHeight();

			g.drawImage(logo.getImage(), 0, 0, w, w, null);

			g.setColor(Color.lightGray);
			g.setFont(new Font("Dialog", Font.BOLD, 14));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}
}
