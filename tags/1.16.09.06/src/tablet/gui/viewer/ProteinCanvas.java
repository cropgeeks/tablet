// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.util.*;

import tablet.analysis.*;
import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

class ProteinCanvas extends TrackingCanvas implements ITaskListener
{
	private Contig contig;
	private Consensus consensus;

	boolean[] enabled = new boolean[6];
	ArrayList<short[]> translations;

	private Dimension dimension = new Dimension();

	// Menu items that appear on the popup menu for this canvas
	ProteinCanvasML mouseListener;

	ProteinCanvas()
	{
		String[] enabledStates = Prefs.visProteins.split("\\s+");
		for (int i = 0; i < enabledStates.length; i++)
			enabled[i] = enabledStates[i].equals("1");
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;

		mouseListener = new ProteinCanvasML(aPanel);
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			consensus = contig.getConsensus();

			createTranslations();
		}

		// Remove tablet.data references if nothing is going to be displayed
		else
			consensus = null;

		this.contig = contig;
	}

	void setDimensions()
	{
		// How many tracks need to be shown?
		int count = 0;
		for (boolean b: enabled)
			if (b) count++;

		// Because every row has a 1px gap after it, we only need to +4 for the
		// total gap between this panel and the one below it
		if (count > 0)
			dimension = new Dimension(0, ((rCanvas.ntH+1) * count) + 4);
		else
			dimension = new Dimension(0, 0);

		setPreferredSize(dimension);
		revalidate();
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (translations == null || rCanvas.ntW < 1)
			return;

		offset = contig.getVisualStart();

		int ntW = (int) rCanvas.ntW;
		int ntH = rCanvas.ntH;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

		ProteinScheme colors = rCanvas.proteins;

		for (int t = 0, count = 0; t < translations.size(); t++)
		{
			// If this translation isn't needed, skip it...
			if (enabled[t] == false || translations.get(t) == null)
				continue;

			int y = (ntH+1) * count;
			count++;

			short[] data = getRegion(translations.get(t), xS+offset, xE+offset);

			for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
				if (data[i] > 0)
					g.drawImage(colors.getImage(data[i]), x, y, null);
		}
	}

	// Strips out a region of a protein array to contain just the data needed
	// to draw that region to the screen
	private short[] getRegion(short[] translation, int start, int end)
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

	void createTranslations()
	{
		translations = new ArrayList<short[]>(6);

		// Forward and reverse...
		for (int i = 0, tIndex = 0; i < 2; i++)
		{
			ProteinTranslator.Direction direction = i == 0 ?
				ProteinTranslator.Direction.FORWARD :
				ProteinTranslator.Direction.REVERSE;

			// ...three reading frames in each direction
			for (int j = 1; j <= 3; j++, tIndex++)
			{
				translations.add(null);

				// Cancel any previous invocation
				String name = "ProteinTranslator:" + tIndex;
				TaskManager.cancel(name);

				// Only do the translation if it needs to be shown
				if (enabled[tIndex])
				{
					ProteinTranslator pt = new ProteinTranslator(
						tIndex, consensus.getSequence(), consensus.length(), direction, j);

					pt.addTaskListener(this);
					TaskManager.submit(name, pt);
				}
			}
		}
	}

	public void taskCompleted(EventObject e)
	{
		if (e.getSource() instanceof ProteinTranslator)
		{
			ProteinTranslator pt = (ProteinTranslator) e.getSource();

			translations.set(pt.getIndex(), pt.getTranslation());

			repaint();
		}
	}
}