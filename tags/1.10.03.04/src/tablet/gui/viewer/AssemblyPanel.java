// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.dialog.*;
import tablet.gui.ribbon.*;

import scri.commons.gui.*;

public class AssemblyPanel extends JPanel implements AdjustmentListener
{
	private WinMain winMain;
	private Assembly assembly;
	private Contig contig;

	OverviewCanvas overviewCanvas;
	ScaleCanvas scaleCanvas;
	ConsensusCanvas consensusCanvas;
	ProteinCanvas proteinCanvas;
	CoverageCanvas coverageCanvas;
	BamBamBar bambamBar;
	ReadsCanvas readsCanvas;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	// Normal or click zooming (affects which base to zoom in on)
	private boolean isZooming = false;
	private boolean isClickZooming = false;
	// Tracks the base to zoom in on
	private float ntCenterX, ntCenterY;

	NameOverlayer nameOverlayer;

	public AssemblyPanel(WinMain winMain)
	{
		this.winMain = winMain;

		createControls();
		setVisibilities();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));

		JPanel consensusPanel = new JPanel(new BorderLayout());
		consensusPanel.add(proteinCanvas, BorderLayout.NORTH);
		consensusPanel.add(consensusCanvas);
		consensusPanel.add(coverageCanvas, BorderLayout.SOUTH);

		JPanel overCanvas = new JPanel(new BorderLayout());
		overCanvas.add(bambamBar, BorderLayout.NORTH);
		overCanvas.add(overviewCanvas, BorderLayout.CENTER);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(overCanvas, BorderLayout.NORTH);
		topPanel.add(consensusPanel, BorderLayout.CENTER);
		topPanel.add(scaleCanvas, BorderLayout.SOUTH);

		JPanel visPanel = new JPanel(new BorderLayout());
		visPanel.add(topPanel, BorderLayout.NORTH);
		visPanel.add(sp, BorderLayout.CENTER);

		add(visPanel);
	}

	private void createControls()
	{
		readsCanvas = new ReadsCanvas();
		overviewCanvas = new OverviewCanvas();
		consensusCanvas = new ConsensusCanvas();
		scaleCanvas = new ScaleCanvas();
		proteinCanvas = new ProteinCanvas();
		coverageCanvas = new CoverageCanvas();
		bambamBar = new BamBamBar();

		// Passing 'this' to the canvas classes can't happen in their
		// constructors because they often need to refer to each other too, so
		// we have to ensure that they've all been created first
		readsCanvas.setAssemblyPanel(this);
		overviewCanvas.setAssemblyPanel(this);
		consensusCanvas.setAssemblyPanel(this);
		scaleCanvas.setAssemblyPanel(this);
		proteinCanvas.setAssemblyPanel(this);
		coverageCanvas.setAssemblyPanel(this);
		bambamBar.setAssemblyPanel(this);

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

	public Assembly getAssembly()
	{
		return assembly;
	}

	public void updateContigInformation()
	{
		// Set the summary label at the top of the screen
		if (contig != null)
		{
			String length = TabletUtils.nf.format(
				contig.getConsensus().length()) + " ("
				+ TabletUtils.nf.format(
					contig.getConsensus().getUnpaddedLength()) + ")";
			if (Prefs.visHideUnpaddedValues)
				length = TabletUtils.nf.format(contig.getConsensus().length());

			String label = RB.format("gui.viewer.AssemblyPanel.summaryLabel",
				contig.getName(),
				length,
				TabletUtils.nf.format(contig.readCount()),
				TabletUtils.nf.format(contig.featureCount()));
			RibbonController.setTitleLabel(label);
		}
		else
			RibbonController.setTitleLabel("");
	}

	public boolean setContig(Contig contig)
	{
		// Clear data from the PREVIOUS contig
		if (this.contig != null)
			this.contig.clearContigData(assembly.getBamBam() != null);

		DisplayData.clearDisplayData(true);

		this.contig = contig;
		boolean setContigOK = true;

		// Fresh BAM assembly: reset the loaded data to the start
		if (contig != null && assembly.getBamBam() != null)
		{
			assembly.getBamBam().reset(Prefs.bamSize);
			assembly.getBamBam().setBlockStart(contig, 0);
		}

		if (contig != null && ((setContigOK = updateDisplayData(true)) == false))
			this.contig = contig = null;

		// Pass the contig to the other components for rendering
		consensusCanvas.setContig(contig);
		scaleCanvas.setContig(contig);
		proteinCanvas.setContig(contig);
		coverageCanvas.setContig(contig);
		bambamBar.setContig(contig);

		forceRedraw();

		return setContigOK;
	}

	public Contig getContig()
		{ return contig; }

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		if (isZooming == false)
			readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}

	void setScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement);
	}

	void updateOverview(int xIndex, int xNum, int yIndex, int yNum)
	{
		overviewCanvas.updateOverview(xIndex, xNum, yIndex, yNum);
		repaint();
	}

	void clickZoom(MouseEvent e)
	{
		isClickZooming = true;

		ntCenterX = (e.getX() / readsCanvas.ntW);
		ntCenterY = (e.getY() / readsCanvas.ntH);

		HomeAdjustBand.zoomIn(6);

		isClickZooming = false;
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	// Jumps to a position relative to the given row and column
	void moveTo(int rowIndex, int colIndex, boolean centre)
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

	/**
	 * Force the panel to recalculate/update/etc when the colour scheme or the
	 * layout manager changes.
	 */
	public void forceRedraw()
	{
		readsCanvas.setContig(contig);

		computePanelSizes();
		// adjustmentValueChanged needed to deal with contigs that don't (need
		// to) force the scrollbars to change meaning the panel sizes are wrong
		adjustmentValueChanged(null);
		overviewCanvas.createImage();

		updateContigInformation();

		repaint();
	}

	public void computePanelSizes()
	{
		// Track the center of the screen (before the zoom)
		if (isClickZooming == false)
		{
			ntCenterX = readsCanvas.ntCenterX;
			ntCenterY = readsCanvas.ntCenterY;
		}

		// This is needed because for some crazy reason the moveToPosition call
		// further down will not work correctly until after Swing has stopped
		// generating endless resize events that affect the scrollbars
		Runnable r = new Runnable() {
			public void run() {
				moveTo(Math.round(ntCenterY), Math.round(ntCenterX), true);
			}
		};
		SwingUtilities.invokeLater(r);

		isZooming = true;

		int zoom = Prefs.visReadsCanvasZoom;
		readsCanvas.setDimensions(zoom, zoom);
		consensusCanvas.setDimensions();
		proteinCanvas.setDimensions();

		// Then after the zoom, try to get back to that position
		isZooming = false;
		moveTo(Math.round(ntCenterY), Math.round(ntCenterX), true);
	}

	// Jumps the screen left by one "page"
	public void pageLeft()
	{
		int jumpTo = scaleCanvas.ntL - (readsCanvas.ntOnScreenX);

		moveToPosition(-1, jumpTo, false);
	}

	// Jumps the screen right by one "page"
	public void pageRight()
	{
		int jumpTo = scaleCanvas.ntR + 1;

		moveToPosition(-1, jumpTo, false);
	}

	public void setVisibilities()
	{
		overviewCanvas.setVisible(!Prefs.guiHideOverview);
		consensusCanvas.setVisible(!Prefs.guiHideConsensus);
		scaleCanvas.setVisible(!Prefs.guiHideScaleBar);
		coverageCanvas.setVisible(!Prefs.guiHideCoverage);
	}

	public void displayProteinOptions(JComponent button)
	{
		proteinCanvas.mouseListener.displayMenu(button, null);
	}

	public void displayOverviewOptions(JComponent button)
	{
		overviewCanvas.displayMenu(button, null);
	}

	private boolean updateDisplayData(boolean doAll)
	{
		String title = RB.getString("gui.viewer.assemblyPanel.progressDialog.title");
		String label = RB.getString("gui.viewer.assemblyPanel.progressDialog.label");

		// Run the job...
		DisplayDataCalculator ddc = new DisplayDataCalculator(assembly, contig, doAll);
		ProgressDialog dialog = new ProgressDialog(ddc, title, label);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				String msg = RB.format("gui.viewer.assemblyPanel.ddcError",
					dialog.getException());
				TaskDialog.error(msg, RB.getString("gui.text.close"));
			}

			return false;
		}

		return true;
	}

	// Returns a reference to the current back-buffer in use by the reads canvas
	public BufferedImage getBackBuffer()
	{
		return readsCanvas.buffer;
	}

	public void toggleNameOverlay()
	{
		//start fade in animation for overlay
		if(Prefs.visOverlayNames)
		{
			nameOverlayer = new NameOverlayer(this.readsCanvas, false);
			nameOverlayer.start();
		}
		//start fade out animation for overlay
		else
		{
			if(nameOverlayer != null)
			{
				nameOverlayer = new NameOverlayer(this.readsCanvas, true);
				nameOverlayer.start();
			}
		}
	}

	public boolean processBamDataChange()
	{
		DisplayData.clearDisplayData(false);

		if (updateDisplayData(false) == false)
		{
			winMain.getContigsPanel().setNullContig();
			return false;
		}

		// Special case to force the coverage (tooltip) information to update
		coverageCanvas.setContig(contig);
		// Finally force the main canvas to update/change
		forceRedraw();

		// And this'll force a repaint of any table's that might need to update
		winMain.repaint();

		return true;
	}

	public void moveToPosition(int rowIndex, int colIndex, final boolean centre)
	{
		// If it's a BAM assembly, we might need to load in a different block
		// of data before the view can be moved to that position
		if (assembly.getBamBam() != null)
		{
			BamBam bambam = assembly.getBamBam();
			int newIndex = colIndex;

			// Load a new block, offset by 2/3rds to the left
			if (colIndex < bambam.getS())
				newIndex -= (int) (bambam.getSize() * 4f/5f);
			// Load a new block, offset by 2/3rds to the right
			else if (colIndex > bambam.getE())
				newIndex -= (int) (bambam.getSize() * 1f/5f);

			if (newIndex != colIndex)
			{
				assembly.getBamBam().setBlockStart(contig, newIndex);
				if (processBamDataChange() == false)
					return;
			}

		}

		final int row = rowIndex;
		// Adjust the colIndex so that it is valid for the current (visual) data
		final int col = colIndex += (-contig.getVisualStart());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(row, col, centre);
			}
		});
	}
}