// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;

import scri.commons.gui.*;

class FeaturesCanvasML extends MouseInputAdapter implements ActionListener
{
	private FeaturesCanvas fCanvas;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	FeaturesCanvasML(AssemblyPanel aPanel)
	{
		fCanvas = aPanel.featuresCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		fCanvas.addMouseListener(this);
		fCanvas.addMouseMotionListener(this);

		new ReadsCanvasDragHandler(aPanel, fCanvas);
	}

	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null);
	}

	public void mouseMoved(MouseEvent e)
	{
		if (rCanvas.contig == null)
			return;

		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + rCanvas.offset;
		sCanvas.setMouseBase(xIndex);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	private void displayMenu(MouseEvent e)
	{
/*		JPopupMenu menu = new JPopupMenu();

		mClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboard, "gui.viewer.ConsensusCanvasML.mClipboard");
		mClipboard.addActionListener(this);
		menu.add(mClipboard);

		menu.show(e.getComponent(), e.getX(), e.getY());
*/
	}

	public void actionPerformed(ActionEvent e)
	{

	}
}