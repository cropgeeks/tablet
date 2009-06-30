package tablet.gui.viewer;

import java.awt.*;
import javax.swing.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.viewer.colors.*;

class ProteinCanvas extends JPanel
{
	private Contig contig;
	private Consensus consensus;
	private ReadsCanvas rCanvas;

	private short[] translation;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	private Dimension dimension = new Dimension();

	ProteinCanvas()
	{
		setOpaque(false);

	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
		{
			consensus = contig.getConsensus();
			offset = contig.getConsensusOffset();

			try
			{
				ProteinTranslator pt = new ProteinTranslator(
					consensus, ProteinTranslator.Direction.FORWARD, 1);
				pt.runJob(0);

				translation = pt.getTranslation();
			}
			catch (Exception e) {}
		}
	}

	void setDimensions()
	{
		dimension = new Dimension(0, rCanvas.ntH);

		setPreferredSize(dimension);
		revalidate();
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (contig == null)
			return;

		// Determine lhs and rhs of canvas
		int x1 = rCanvas.pX1;
		int x2 = rCanvas.pX2;
		int width = (x2-x1+1);

		// Clip to only draw what's needed (mainly ignoring what would appear
		// above the vertical scrollbar of the reads canvas)
		g.setClip(3, 0, width, getHeight());
		g.translate(3-x1, 0);


		int ntW = rCanvas.ntW;
		int ntH = rCanvas.ntH;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

		ColorScheme colors = rCanvas.proteins;

		short[] data = getTranslation(translation, xS-offset, xE-offset);

		int y = 0;

		for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
		{
			if (data[i] > 0)
				g.drawImage(colors.getImage(data[i]), x, y, null);
		}
	}

	// Strips out a region of a protein array to contain just the data needed
	// to draw that region to the screen
	private short[] getTranslation(short[] translation, int start, int end)
	{
		short[] data = new short[end-start+1];

		int i = 0, d = 0;

		// Pre protein data (ie, before consensus starts)
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Protein data
		for (i = i; i <= end && i < translation.length; i++, d++)
			data[d] = translation[i];

		// Post protein data (ie, after consensus ends)
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}
}