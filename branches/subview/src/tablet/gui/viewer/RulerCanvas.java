package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import tablet.data.Contig;

public class RulerCanvas extends JPanel
{
	int contigS, contigE;
	int visualS, visualE;

	int mappedStart, mappedEnd;

	private ReadsCanvas rCanvas;
	private AssemblyPanel aPanel;

	private Dimension dimension = new Dimension();

	boolean dragging = false;
	boolean resizeLeft = false;
	boolean resizeRight = false;
	int prevPos;
	int width;
	SubsetOverlayer subsetOverlayer;


	RulerCanvas()
	{
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 15));

		setBorder(BorderFactory.createLineBorder(new Color(167, 166, 170)));

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				if(e.getX() >= mappedStart-5 && e.getX() <= mappedEnd+5)
				{
					if(e.getX() <= mappedStart && e.getX() >= mappedStart-5)
					{
						dragging = false;
						resizeLeft = true;
						resizeRight = false;
					}
					else if(e.getX() >= mappedEnd && e.getX() <= mappedEnd+5)
					{
						dragging = false;
						resizeLeft = false;
						resizeRight = true;
					}
					else
						dragging = true;
					prevPos = e.getX();
					rCanvas.overlays.addFirst(subsetOverlayer);
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e)
			{
				if(dragging || resizeLeft || resizeRight)
				{
					visualS = (mappedStart - 5) * (contigE - contigS) / (width-5 - 5) + contigS;
					aPanel.getContig().setVisualS(visualS);
					visualE = (mappedEnd - 5) * (contigE - contigS) / (width-5 - 5) + contigS;
					aPanel.getContig().setVisualE(visualE);
					aPanel.setContig(aPanel.getContig());
					dragging = false;
					resizeLeft = false;
					resizeRight = false;
					rCanvas.overlays.remove(subsetOverlayer);
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e)
			{
				int diff = prevPos - e.getX();
				int w = mappedEnd - mappedStart;
				prevPos = e.getX();

				if(dragging)
				{
					mappedStart -= diff;
					mappedEnd -= diff;
					if(mappedStart >= 5 && mappedEnd <= width-5)
						repaint();
					else if(mappedStart < 5)
					{
						mappedStart = 5;
						mappedEnd = 5+w;
						repaint();
					}
					else if(mappedEnd > width-5)
					{
						mappedEnd = width-5;
						mappedStart = mappedEnd - w;
						repaint();
					}
				}
				else if(resizeLeft)
				{
					mappedStart -= diff;
					if(mappedStart > mappedEnd)
						mappedStart = mappedEnd-1;
					if(mappedStart < 5)
					{
						mappedStart = 5;
					}
					repaint();
				}
				else if(resizeRight)
				{
					mappedEnd -= diff;
					if(mappedEnd < mappedStart)
						mappedEnd = mappedStart+1;
					if(mappedEnd > width-5)
					{
						mappedEnd = width-5;
					}
					repaint();
				}

				visualS = (mappedStart - 5) * (contigE - contigS) / (width-5 - 5) + contigS;
				visualE = (mappedEnd - 5) * (contigE - contigS) / (width-5 - 5) + contigS;
				rCanvas.repaint();
				aPanel.overviewCanvas.repaint();
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e)
			{
				if( e.getX() > mappedStart && e.getX() < mappedEnd)
					aPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				else if(e.getX() <= mappedStart && e.getX() >= mappedStart-5)
					aPanel.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				else if( e.getX() >= mappedEnd && e.getX() <= mappedEnd+5)
				{
					aPanel.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				}
				else
					aPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e)
			{
				aPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				width = getSize().width;
				mappedStart = (visualS - contigS) * (width-5 -5) / (contigE - contigS) + 5;
				mappedEnd = (visualE - contigS) * (width-5 -5) / (contigE - contigS) + 5;
			}
		});
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		Color color = new Color(160, 183, 209);
		Color c2 = new Color(57, 105, 138);
		g.setPaint(new GradientPaint(0, 0, c2, 0, 7.5f, color, true));
		g.fillRect(0, 0, mappedStart-5, 15);
		g.fillRect(mappedEnd+5, 0, width-(mappedEnd+5), 15);
		g.setPaint(new GradientPaint(0, 0, Color.white.darker(), 0, 7.5f,Color.white.brighter(), true));
		g.fillRect(mappedStart-5, 0, ((mappedEnd+5)-(mappedStart-5)), 15);

//		g.setColor(Color.WHITE);
//		g.fillRect(0, 0, 5, 15);
//		g.fillRect(width-5, 0, width, 15);

		g.setColor(Color.gray);

		for(int i=5; i < width-5; i++)
		{
			if (i % 10 == 0)
				g.drawLine(i, 5, i, 9);

			else if (i % 5 == 0)
				g.drawLine(i, 7, i, 8);
		}

		g.setColor(Color.black);

		int [] yPoints = { 2, 8, 13};

		int [] xPoints = { mappedStart-5, mappedStart, mappedStart-5};
		int [] xP2 = { mappedEnd+5, mappedEnd, mappedEnd+5 };

		g.fillPolygon(xPoints, yPoints, 3);

		g.fillPolygon(xP2, yPoints, 3);
		
		//g.setFont(new Font("Monospaced", Font.PLAIN, 9));
		//g.drawString(""+(aPanel.getContig().getContigS()+1), 0, 13);

		//String rhsStr = ""+(aPanel.getContig().getContigE()+1);
		//int pos = getPosition(getSize().width-5, g.getFontMetrics().stringWidth(rhsStr));
		//g.drawString(rhsStr, pos, 13);
	}

	// Computes the best position to draw a string onscreen, assuming an optimum
	// start position that *may* be adjusted if the text ends up partially drawn
	// offscreen on either the LHS or the RHS
	private int getPosition(int pos, int strWidth)
	{
		// Work out where the left and right hand edges of the text will be
		int leftPos = pos-(int)(strWidth/2f);
		int rghtPos = pos+(int)(strWidth/2f);

		// Similarly if we're offscreen to the right...
		if (rghtPos > width)
			leftPos = width-strWidth-3;

		return leftPos;
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
		this.aPanel = aPanel;
		subsetOverlayer = new SubsetOverlayer(rCanvas);
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void setDimensions()
	{
		dimension = new Dimension((rCanvas.canvasW), 15);
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			visualS = contig.getVisualS();
			visualE = contig.getVisualE();
			contigS = contig.getContigS();
			contigE = contig.getContigE();
			mappedStart = (visualS - contigS) * (width-5 - 5) / (contigE - contigS) + 5;
			mappedEnd = (visualE - contigS) * (width-5 - 5) / (contigE - contigS) + 5;
		}
		else
		{
			mappedStart = 5;
			mappedEnd = width-5;
		}
	}

	public int getVisualS()
	{
		return visualS;
	}

	public int getVisualE()
	{
		return visualE;
	}
}
