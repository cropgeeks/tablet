// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

import tablet.data.*;

import scri.commons.gui.*;

class Breakout //extends MouseInputAdapter implements IOverlayRenderer, Runnable
{
/*	LinkedList<IOverlayRenderer> overlays;
	Robot botboy;

	boolean isRunning = false;

	int paddleDirection;

	ReadsCanvas rCanvas;
	AssemblyPanel aPanel;

	Thread thread;

	int paddleX;
	Point ball;
	int ballH;
	int ballV;

	int[][] grid;

	int ntW;
	int ntH;
	int xS;
	int yS;

	int ballRadius;

	int paddleHeight;


	Breakout(ReadsCanvas rCanvas, AssemblyPanel aPanel)
	{
		this.rCanvas = rCanvas;
		this.aPanel = aPanel;

		try { botboy = new Robot(); }
		catch (Exception e) { System.out.println(e); }

		overlays = rCanvas.overlays;
		rCanvas.overlays = new LinkedList<>();
		rCanvas.overlays.add(this);
		rCanvas.removeMouseListener(rCanvas.readsCanvasML);
		rCanvas.removeMouseListener(rCanvas.readsCanvasML.dragHandler);
		rCanvas.removeMouseMotionListener(rCanvas.readsCanvasML);
		rCanvas.removeMouseMotionListener(rCanvas.readsCanvasML.dragHandler);
		rCanvas.removeMouseWheelListener(rCanvas.readsCanvasML);

		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);

		rCanvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
			Icons.getIcon("INVISIBLE").getImage(), new Point(0, 0), ""));

		grid = new int[rCanvas.ntOnScreenY][rCanvas.ntOnScreenX];

		ntW = rCanvas.ntW;
		ntH = rCanvas.ntH;
		xS = rCanvas.xS;
		yS = rCanvas.yS;

		ballRadius = 5;

		paddleHeight = rCanvas.pY2 - 15 - 2;

		// Fill in the grid with the data values (or -1 if no nucleotide)
		int toHide = (int) (0.5 * grid.length);
		System.out.println("TOHIDE: " +toHide);
		for (int i = 0; i < grid.length; i++)
		{
			int dataY = yS + i;

			for (int j = 0; j < grid[i].length; j++)
			{
				int dataX = xS + j;

				Read read = rCanvas.reads.getReadAt(dataY, dataX);
				if (read != null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, true);
					int value = rmd.getStateAt(dataX-read.s());

					if (i < toHide)
						grid[i][j] = value;
					else
						grid[i][j] = -2;
				}
				else
					grid[i][j] = -1;
			}
		}


		// Determine ball start (y)
		ball = new Point(0, rCanvas.pY2 - 15 - 2 -5);
		ballP = ball;
	}

	public void mouseClicked(MouseEvent e)
	{
		if (isRunning == false)
		{
			isRunning = true;
			startGame();
		}

		else if (isRunning)
		{
			isRunning = false;

			rCanvas.overlays = overlays;
			rCanvas.addMouseListener(rCanvas.readsCanvasML);
			rCanvas.addMouseListener(rCanvas.readsCanvasML.dragHandler);
			rCanvas.addMouseMotionListener(rCanvas.readsCanvasML);
			rCanvas.addMouseMotionListener(rCanvas.readsCanvasML.dragHandler);
			rCanvas.addMouseWheelListener(rCanvas.readsCanvasML);
			rCanvas.removeMouseListener(this);
			rCanvas.removeMouseMotionListener(this);

			rCanvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void mouseExited(MouseEvent e)
	{
		Point p = rCanvas.getLocationOnScreen();

		botboy.mouseMove(p.x + paddleX, p.y+10);
	}

	private void startGame()
	{
		if (paddleDirection <= 0)
			ballH = -1;
		else
			ballH = 1;
		ballV = -1;

		thread = new Thread(this);
		thread.start();
	}

	public void mouseMoved(MouseEvent e)
	{
		int x = e.getX();
		if (x >= rCanvas.pX1 && x <= rCanvas.pX2)
		{
			if (x > paddleX)
				paddleDirection = 1;
			else if (x < paddleX)
				paddleDirection = -1;

			paddleX = x;


			if (isRunning == false)
				ball.x = x;
		}

		rCanvas.repaint();
	}

	public void render(Graphics2D g)
	{
		int y = ntH*yS;

		// Overlay the grid to hide any destroyed blocks
		g.setColor(Color.white);
		for (int i = 0; i < grid.length; i++, y += ntH)
			for (int j = 0, x = (ntW*xS); j < grid[i].length; j++, x += ntW)
			{
				// -2 means block destroyed
				if (grid[i][j] == -2)
				{
					g.setColor(Color.white);
					g.fillRect(x, y, ntW, ntH);
				}

				if (ballP != null && ballP.x == j && ballP.y == i)
				{
					g.setColor(Color.red);
					g.fillRect(x, y, ntW, ntH);
				}

//				if (ballC != null && ballC.x == j && ballC.y == i)
//				{
//					g.setColor(Color.blue);
//					g.fillRect(x, y, ntW, ntH);
//				}
			}

		// Paint the paddle
		g.setColor(Color.blue);
		y = rCanvas.pY2 - 15 - 2;
		g.fillRect(paddleX-35, y, 70, 10);

		g.setColor(Color.green);
		g.fillRect(ball.x-5, ball.y-5, 10, 10);

	}


	Point ballP = null;
	//Point ballC = null;

	public void run()
	{

		while (isRunning)
		{
			for (int i=0; i < 20; i++)
			{
				// Detect collisions
				try
				{
					int offset = rCanvas.contig.getVisualStart();
					int gridX = (ball.x / ntW) + offset - xS;
					int gridY = (ball.y / ntH) - yS;

					int ballLeft = ball.x - ballRadius;
					int ballRight = ball.x + ballRadius;
					int ballTop = ball.y - ballRadius;
					int ballBottom = ball.y + ballRadius;

					int ballPX = (ballP.x / ntW) + offset - xS;
					int ballPY = (ballP.y / ntH) - yS;



					if (grid[gridY][gridX] >= 0)
					{
						int value = grid[gridY][gridX];

						grid[gridY][gridX] = -2;

						int left = gridX-1;
						int right = gridX+1;

						while (left >= 0 && grid[gridY][left] == value)
						{
							grid[gridY][left] = -2;
							left--;
						}

						while (right < grid[gridY].length && grid[gridY][right] == value)
						{
							grid[gridY][right] = -2;
							right++;
						}

						if (ballP != null)
						{
							// TL
							if (ballPX < gridX && ballPY < gridY)
							{
								ballH *= -1;
								ballV *= -1;
							}
							// T
							else if (ballPX == gridX && ballPY < gridY)
							{
								ballV *= -1;
							}
							// TR
							else if (ballPX > gridX && ballPY < gridY)
							{
								ballH *= -1;
								ballV *= -1;
							}
							// R
							else if (ballPX > gridX && ballPY == gridY)
							{
								ballH *= -1;
							}
							// BR
							else if (ballPX > gridX && ballPY > gridY)
							{
								ballH *= -1;
								ballV *= -1;
							}
							// B
							else if (ballPX == gridX && ballPY > gridY)
							{
								ballV *= -1;
							}
							// BL
							else if (ballPX < gridX && ballPY > gridY)
							{
								ballH *= -1;
								ballV *= -1;
							}
							// L
							else if (ballPX < gridX && ballPY == gridY)
							{
								ballH *= -1;
							}

						}

					}

					if (ballV > 0 && ballRight > paddleX-35 && ballLeft < paddleX+35 && ballBottom == paddleHeight)
						ballV *= -1;
					else if (ballV > 0 && ballRight > paddleX-35 && ballLeft < paddleX+35 && ballBottom > paddleHeight)
						ballH *= -1;

					ballP = new Point(ball.x, ball.y);


				}
				catch (Exception e) { System.out.println(e);}

				// Move the ball
				ball.y += ballV * (1);
				ball.x += ballH * (1);

				// Ball about to leave the canvas detection...
				if (ball.x <= rCanvas.pX1)
				{
					ballH = 1;
					ball.x = rCanvas.pX1;
				}
				else if (ball.x >= rCanvas.pX2)
				{
					ballH = -1;
					ball.x = rCanvas.pX2;
				}
				if (ball.y <= rCanvas.pY1)
				{
					ballV = 1;
					ball.y = rCanvas.pY1;
				}

				// YOU FAIL!
				if (ball.y > rCanvas.pY2+ballRadius)
					ballV = 0;
			}


			rCanvas.repaint();

			try { Thread.sleep(75); }
			catch (InterruptedException e) {}
		}
	}
	*/
}