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
		setBorder(BorderFactory.createTitledBorder("Assembly Panel:"));
		add(consensusPanel, BorderLayout.NORTH);
		add(sp);

	}

	private void createControls()
	{
		readsCanvas = new ReadsCanvas(this);
		consensusCanvas = new ConsensusCanvas(readsCanvas);


		sp = new JScrollPane();
		viewport = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		sp.setViewportView(readsCanvas);
		sp.getViewport().setBackground(Color.white);
	}

	public void setAssembly(Assembly assembly)
	{
		this.assembly = assembly;

		contig = assembly.getContigs().get(0);

		consensusCanvas.setContig(contig);
		readsCanvas.setContig(contig);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());

		consensusCanvas.repaint();
	}
}