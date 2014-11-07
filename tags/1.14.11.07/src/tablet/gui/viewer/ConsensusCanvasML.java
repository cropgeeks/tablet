// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;

import scri.commons.gui.*;

class ConsensusCanvasML extends MouseInputAdapter implements ActionListener
{
	private ConsensusCanvas cCanvas;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	private JMenuItem mClipboard, mSubsequenceClipboard;

	ConsensusCanvasML(AssemblyPanel aPanel)
	{
		cCanvas = aPanel.consensusCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		cCanvas.addMouseListener(this);
		cCanvas.addMouseMotionListener(this);

		new ReadsCanvasDragHandler(aPanel, cCanvas);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (rCanvas.contig == null)
			return;

		int x = cCanvas.getMouseX(e);
		int ntIndex = rCanvas.getBaseForPixel(rCanvas.pX1 + x);

		sCanvas.setMouseBase(ntIndex);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	private void displayMenu(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		mClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboard, "gui.viewer.ConsensusCanvasML.mClipboard");
		mClipboard.addActionListener(this);
		menu.add(mClipboard);

		mSubsequenceClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mSubsequenceClipboard, "gui.viewer.ConsensusCanvasML.mSubsequenceClipboard");
		mSubsequenceClipboard.addActionListener(this);
		menu.add(mSubsequenceClipboard);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mClipboard)
		{
			String name = rCanvas.contig.getName();
			String seq = rCanvas.contig.getConsensus().toString();

			// Produce a FASTA formatted string
			String text = TabletUtils.formatFASTA(name, seq);

			StringSelection selection = new StringSelection(text);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}

		else if (e.getSource() == mSubsequenceClipboard)
		{
			if (rCanvas.contig.getConsensus().length() > 0)
				Tablet.winMain.getConsensusSubsequenceDialog().setVisible(true);
		}
	}
}