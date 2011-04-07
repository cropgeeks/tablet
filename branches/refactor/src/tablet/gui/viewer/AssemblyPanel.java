// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.ribbon.*;

import scri.commons.gui.*;

public class AssemblyPanel extends JPanel
{
	private WinMain winMain;
	private Assembly assembly;
	private Contig contig;

	private JScrollPane sp;
	OverviewCanvas overviewCanvas;
	ScaleCanvas scaleCanvas;
	ConsensusCanvas consensusCanvas;
	ProteinCanvas proteinCanvas;
	CoverageCanvas coverageCanvas;
	FeaturesCanvas featuresCanvas;
	BamBamBar bambamBar;
	ReadsCanvas readsCanvas;

	private CanvasController controller;

	private NameOverlayer nameOverlayer;

	private VisualAssembly visualAssembly;

	private CigarIHighlighter cigarIHighlighter;

	public AssemblyPanel(WinMain winMain)
	{
		this.winMain = winMain;

		createControls();
		setVisibilities();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));

		JPanel conPanel1 = new JPanel(new BorderLayout());
		conPanel1.add(proteinCanvas, BorderLayout.NORTH);
		conPanel1.add(consensusCanvas);
		conPanel1.add(featuresCanvas, BorderLayout.SOUTH);

		JPanel conPanel2 = new JPanel(new BorderLayout());
		conPanel2.add(conPanel1, BorderLayout.NORTH);
		conPanel2.add(coverageCanvas, BorderLayout.SOUTH);

		JPanel overCanvas = new JPanel(new BorderLayout());
		overCanvas.add(bambamBar, BorderLayout.NORTH);
		overCanvas.add(overviewCanvas, BorderLayout.CENTER);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(overCanvas, BorderLayout.NORTH);
		topPanel.add(conPanel2, BorderLayout.CENTER);
		topPanel.add(scaleCanvas, BorderLayout.SOUTH);

		JPanel visPanel = new JPanel(new BorderLayout());
		visPanel.add(topPanel, BorderLayout.NORTH);
		visPanel.add(sp, BorderLayout.CENTER);

		add(visPanel);
		cigarIHighlighter = new CigarIHighlighter(this);
	}

	private void createControls()
	{
		readsCanvas = new ReadsCanvas();
		overviewCanvas = new OverviewCanvas();
		consensusCanvas = new ConsensusCanvas();
		scaleCanvas = new ScaleCanvas();
		proteinCanvas = new ProteinCanvas();
		coverageCanvas = new CoverageCanvas();
		featuresCanvas = new FeaturesCanvas();
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
		featuresCanvas.setAssemblyPanel(this);
		bambamBar.setAssemblyPanel(this);

		sp = new JScrollPane();
		sp.setViewportView(readsCanvas);
		sp.getViewport().setBackground(Color.white);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);
	}

	public void setAssembly(Assembly assembly)
	{
		this.assembly = assembly;

		if(assembly != null)
			visualAssembly = new VisualAssembly();
		else
			visualAssembly = null;
	}

	public Assembly getAssembly()
	{
		return assembly;
	}

	public CanvasController getController()
		{ return controller; }

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
				TabletUtils.nf.format(contig.getFeatures().size()));
			RibbonController.setTitleLabel(label);
		}
		else
			RibbonController.setTitleLabel("");
	}

	private void clearContig()
	{
		// Clear data from the PREVIOUS contig
		if (contig != null)
			contig.clearContigData(assembly.getBamBam() != null);

		DisplayData.clearDisplayData(true);

		// Forces the contigs table to update its numbers properly
		winMain.getContigsPanel().repaint();
	}

	public boolean setContig(Contig newContig)
	{
		clearContig();
		contig = newContig;

		boolean setContigOK = true;

		// Fresh BAM assembly: reset the loaded data to the start
		if (contig != null && assembly.getBamBam() != null)
		{
			assembly.getBamBam().reset(Prefs.bamSize);
			assembly.getBamBam().setBlockStart(contig, 0);
		}

		if (contig != null && ((setContigOK = updateDisplayData(true)) == false))
		{
			clearContig();
			contig = null;
		}

		if (contig != null)
		{
			if(getVisualContig() == null)
				visualAssembly.getVisualContigs().put(contig, new VisualContig());
		}

		// Pass the contig to the other components for rendering
		consensusCanvas.setContig(contig);
		featuresCanvas.setContig(contig);
		scaleCanvas.setContig(contig);
		proteinCanvas.setContig(contig);
		coverageCanvas.setContig(contig);
		bambamBar.setContig(contig);

		if (contig != null)
			overviewCanvas.setSubset(contig.getVisualStart(), contig.getVisualEnd());

		forceRedraw();

		return setContigOK;
	}

	public Contig getContig()
		{ return contig; }


	void canvasViewChanged(int xIndex, int xNum, int yIndex, int yNum)
	{
		// Tell the overview canvas to track the new position
		overviewCanvas.updateOverview(xIndex, xNum, yIndex, yNum);

		// A repaint() call will also update the consensus, etc canvases that
		// need to maintain their synchronization with the reads canvas
		repaint();
	}

	/**
	 * Force the panel to recalculate/update/etc when the colour scheme or the
	 * layout manager changes.
	 */
	public void forceRedraw()
	{
		controller.forceRedraw();

		overviewCanvas.createImage();
		updateContigInformation();
	}

	public void setVisibilities()
	{
		overviewCanvas.setVisible(!Prefs.guiHideOverview);
		consensusCanvas.setVisible(!Prefs.guiHideConsensus);
		scaleCanvas.setVisible(!Prefs.guiHideScaleBar);
		coverageCanvas.setVisible(!Prefs.guiHideCoverage);
	}

	public boolean updateDisplayData(boolean doAll)
	{
		String title = RB.getString("gui.viewer.assemblyPanel.progressDialog.title");
		String label = RB.getString("gui.viewer.assemblyPanel.progressDialog.label");

		// Run the job...
		DisplayDataCalculator ddc = new DisplayDataCalculator(assembly, contig, doAll);
		ProgressDialog dialog = new ProgressDialog(ddc, title, label, Tablet.winMain);
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				String msg = RB.format("gui.viewer.assemblyPanel.ddcError",
					dialog.getException());

				TaskDialog.showFileOpen(msg, TaskDialog.ERR, 1,
					new String[] { RB.getString("gui.text.openLog"),
					RB.getString("gui.text.close") }, new boolean[] { true, true },
					Tablet.getLogFile().getAbsolutePath());
			}

			System.out.println("contig load failed for " + contig.getName());

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
			nameOverlayer = new NameOverlayer(readsCanvas, false);
			nameOverlayer.start();
		}
		//start fade out animation for overlay
		else
		{
			if(nameOverlayer != null)
			{
				nameOverlayer = new NameOverlayer(readsCanvas, true);
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

		winMain.getAssemblyPanel().getOverviewCanvas().resetOverview();
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

		controller.moveToLater(row, col, centre);
	}

	public void highlightColumn(int index)
	{
		moveToPosition(-1, index, true);
		new ColumnHighlighter(this, index, index);
	}

	public VisualContig getVisualContig()
	{
		return visualAssembly.getVisualContigs().get(contig);
	}


	// Methods called by Ribbon controls to interact with the display

	public void setProteinStates(boolean[] states)
	{
		proteinCanvas.mouseListener.setStates(states);
	}

	public void displayOverviewOptions(JComponent button)
	{
		overviewCanvas.displayMenu(button, null);
	}

	public void bamPrevious()
	{
		bambamBar.bamPrevious();
	}

	public void updateColorScheme()
		{ readsCanvas.updateColorScheme(); }

	public OverviewCanvas getOverviewCanvas()
		{ return overviewCanvas; }

	public FeaturesCanvas getFeaturesCanvas()
		{ return featuresCanvas; }

	public CigarIHighlighter getCigarIHighlighter()
		{ return cigarIHighlighter; }

	public ReadsCanvas getReadsCanvas()
	{
		return readsCanvas;
	}
}