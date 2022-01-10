// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import javax.swing.*;

public class TitlePanel3 extends JPanel
{
	private static final Color lineColor = new Color(207, 219, 234);
	private static final Color textColor = new Color(75, 105, 150);

	private static final int h = 30;

	private String title;

	public TitlePanel3(String title)
	{
		this.title = title;
		setOpaque(false);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(50, h);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		int w = getWidth();

		g.setFont(new Font("Dialog", Font.BOLD, 13));
		g.setColor(textColor);
		g.drawString(title, 10, 18);

		g.setPaint(new GradientPaint(0, h, lineColor, w, h, Color.white));
		g.setStroke(new BasicStroke(3));
		g.drawLine(10, 26, w-10, 26);
	}
}