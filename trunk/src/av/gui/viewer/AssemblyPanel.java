package av.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import av.data.*;
import av.gui.*;

public class AssemblyPanel extends JPanel implements AdjustmentListener
{
	private Assembly assembly;
	private Contig contig;

	private ConsensusCanvas consensusCanvas;
	private ReadsCanvas readsCanvas;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	public AssemblyPanel(WinMain winMain)
	{
		createControls();

		JPanel consensusPanel = new JPanel(new BorderLayout());
		consensusPanel.add(consensusCanvas);


		setLayout(new BorderLayout());
		add(consensusPanel, BorderLayout.NORTH);
		add(sp);

	}

	private void createControls()
	{
		consensusCanvas = new ConsensusCanvas();
		readsCanvas = new ReadsCanvas();

		sp = new JScrollPane();
		viewport = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		sp.setViewportView(readsCanvas);
	}

	public void setAssembly(Assembly assembly)
	{
		this.assembly = assembly;

		contig = assembly.getContigs().get(0);
		consensusCanvas.setConsensusSequence(contig.getConsensus());

		readsCanvas.setContig(contig);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}
}