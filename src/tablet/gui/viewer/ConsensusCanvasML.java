package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.event.*;

class ConsensusCanvasML extends MouseInputAdapter
{
	private AssemblyPanel aPanel;
	private ConsensusCanvas cCanvas;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	ConsensusCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		cCanvas = aPanel.consensusCanvas;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		cCanvas.addMouseListener(this);
		cCanvas.addMouseMotionListener(this);
	}

	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null);
	}

	public void mouseMoved(MouseEvent e)
	{
		if (rCanvas.contig == null)
			return;

		int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) - rCanvas.offset;
		sCanvas.setMouseBase(xIndex);
	}
}