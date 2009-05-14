package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;

public class AssemblyPanel extends JPanel implements AdjustmentListener
{
	private Assembly assembly;
	private Contig contig;


	OverviewCanvas overviewCanvas;
	ScaleCanvas scaleCanvas;
	ConsensusCanvas consensusCanvas;
	public ReadsCanvas readsCanvas;
	NBStatusPanel statusPanel;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	public AssemblyPanel(WinMain winMain)
	{
		createControls();

		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel consensusPanel = new JPanel(new BorderLayout(5, 5));
		consensusPanel.add(consensusCanvas);
		consensusPanel.add(scaleCanvas, BorderLayout.SOUTH);

		JPanel topPanel = new JPanel(new BorderLayout(5, 5));
		topPanel.add(overviewCanvas, BorderLayout.NORTH);
		topPanel.add(consensusPanel, BorderLayout.CENTER);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);

		JPanel visPanel = new JPanel(new BorderLayout(5, 5));
		visPanel.add(topPanel, BorderLayout.NORTH);
		visPanel.add(centerPanel, BorderLayout.CENTER);
		visPanel.add(statusPanel, BorderLayout.SOUTH);

		add(visPanel);
	}

	private void createControls()
	{
		statusPanel = new NBStatusPanel(this);

		readsCanvas = new ReadsCanvas();
		overviewCanvas = new OverviewCanvas();
		consensusCanvas = new ConsensusCanvas();
		scaleCanvas = new ScaleCanvas();

		readsCanvas.setAssemblyPanel(this);
		overviewCanvas.setAssemblyPanel(this);
		consensusCanvas.setAssemblyPanel(this);
		scaleCanvas.setAssemblyPanel(this);

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
	}

	Assembly getAssembly()
	{
		return assembly;
	}

	public void setContig(Contig contig)
	{
		this.contig = contig;

		consensusCanvas.setContig(contig);
		scaleCanvas.setContig(contig);
		readsCanvas.setContig(contig);

		computePanelSizes();
		overviewCanvas.createImage();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}

	void updateOverview(int xIndex, int xNum, int yIndex, int yNum)
	{
		overviewCanvas.updateOverview(xIndex, xNum, yIndex, yNum);
		repaint();
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	// Jumps to a position relative to the given row and column
	public void moveToPosition(int rowIndex, int colIndex, boolean centre)
	{
		// If 'centre' is true, offset by half the screen
		int offset = 0;

		if (rowIndex != -1)
		{
			if (centre)
				offset = ((readsCanvas.ntOnScreenY * readsCanvas.ntH) / 2) - readsCanvas.ntH;

			int y = rowIndex * readsCanvas.ntH - offset;
			vBar.setValue(y);
		}

		if (colIndex != -1)
		{
			if (centre)
				offset = ((readsCanvas.ntOnScreenX * readsCanvas.ntW) / 2) - readsCanvas.ntW;

			int x = colIndex * readsCanvas.ntW - offset;
			hBar.setValue(x);
		}
	}

	void computePanelSizes()
	{
		int zoomX = statusPanel.getZoomX();
		int zoomY = statusPanel.getZoomY();

		readsCanvas.setDimensions(zoomX, zoomY);
		consensusCanvas.setDimensions();
	}
}