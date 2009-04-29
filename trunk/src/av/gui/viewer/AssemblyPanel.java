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
	NBStatusPanel statusPanel;

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

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(consensusPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);

		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
	}

	private void createControls()
	{
		readsCanvas = new ReadsCanvas(this);
		consensusCanvas = new ConsensusCanvas(readsCanvas);
		statusPanel = new NBStatusPanel(this);

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

	void computePanelSizes()
	{
		int zoomX = statusPanel.getZoomX();
		int zoomY = statusPanel.getZoomY();

		readsCanvas.computeDimensions(zoomX, zoomY);
	}

	Assembly getAssembly()
	{
		return assembly;
	}
}