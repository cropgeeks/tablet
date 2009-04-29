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

	private ContigPanel contigPanel;
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

		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(consensusPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);

		JPanel visPanel = new JPanel(new BorderLayout());
		visPanel.add(topPanel, BorderLayout.NORTH);
		visPanel.add(centerPanel, BorderLayout.CENTER);
		visPanel.add(statusPanel, BorderLayout.SOUTH);

		add(visPanel);
		add(contigPanel, BorderLayout.WEST);
	}

	private void createControls()
	{
		contigPanel = new ContigPanel(this);
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
		contigPanel.setAssembly(assembly);
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		consensusCanvas.setContig(contig);
		readsCanvas.setContig(contig);

		computePanelSizes();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());

		consensusCanvas.repaint();
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
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