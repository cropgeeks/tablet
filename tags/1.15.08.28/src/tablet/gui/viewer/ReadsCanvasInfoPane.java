// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.CigarFeature.*;

/** Manages and paints the advanced tool tips for the reads canvas. */
class ReadsCanvasInfoPane
{
	private ReadsCanvasInfoPaneRenderer renderer;

	private AssemblyPanel aPanel;
	private OverviewCanvas oCanvas;
	private ScaleCanvas sCanvas;
	private ReadsCanvas rCanvas;

	// readA = read under the mouse (if any); readB = readA's mate (if any)
	private Read readA, readB;
	private BoxData box;

	ReadsCanvasInfoPane(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;

		oCanvas = aPanel.overviewCanvas;
		sCanvas = aPanel.scaleCanvas;
		rCanvas = aPanel.readsCanvas;

		renderer = new ReadsCanvasInfoPaneRenderer(rCanvas);
	}

	IOverlayRenderer getRenderer()
	{
		return renderer;
	}

	public void setMousePosition(Point mouse, int lineIndex, int rowIndex)
	{
		renderer.setMousePosition(mouse);

		updateReadData(lineIndex, rowIndex);

		updateOverviewCanvas(lineIndex, rowIndex);
	}

	// Assuming we're over a read, tells the Overview canvas to paint an outline
	// around the read
	void updateOverviewCanvas(int lineIndex, int rowIndex)
	{
		Read read = rCanvas.reads.getReadAt(lineIndex, rowIndex);

		if (read == null || !read.isNotMateLink())
			oCanvas.updateRead(-1, -1, -1);

		else
		{
			// Tell the overview canvas to paint this read too
			int offset = -rCanvas.offset;
			int start = read.s()+offset;
			int end = read.e()+offset;

			oCanvas.updateRead(lineIndex, start, end);
		}
	}

	boolean isOverRead()
		{ return readA != null; }

	// Set the data for rendering the tooltip based on the Read and ReadMetaData
	// provided. This sets up the string values for display.
	void updateReadData(int lineIndex, int rowIndex)
	{
		readA = readB = null;
		renderer.box1 = null;
		renderer.box2 = null;

		// Is there a read under the mouse?
		readA = rCanvas.reads.getReadAt(lineIndex, rowIndex);

		if (readA instanceof MateLink)
			readA = null;

		// And if so, does it have a mate?
		if (readA instanceof MatedRead)
			readB = ((MatedRead)readA).getMate();


		if (readA != null)
		{
			renderer.box1 = getReadData(readA, 1);
			box = renderer.box1;
		}

		if (readB != null)
			renderer.box2 = getReadData(readB, 2);

		else if (readA instanceof MatedRead)
			renderer.box2 = getMissingMateData(readA);


		// Special case where we turn off box2 because the mate is unmapped
		if (renderer.box1 != null && renderer.box1.mateStatus != null)
			renderer.box2 = null;
	}

	private BoxData getReadData(Read read, int boxNum)
	{
		BoxData box = new BoxData();

		ReadNameData rnd = Assembly.getReadNameData(read);
		ReadMetaData rmd = Assembly.getReadMetaData(read, false);


		// Start and ending positions (against consensus)
		int readS = read.s();
		int readE = read.e();

		// Name
		box.readName = truncate(rnd.getName());
		if (boxNum == 2)
			box.readName += " " + RB.getString("gui.viewer.ReadsCanvasInfoPane.mateRead");

		// Position data
		box.posData = RB.format("gui.viewer.ReadsCanvasInfoPane.from",
			(TabletUtils.nf.format(readS+1) + sCanvas.getUnpadded(readS)),
			(TabletUtils.nf.format(readE+1) + sCanvas.getUnpadded(readE)));

		// Length
		if (Prefs.visHideUnpaddedValues)
			box.lengthData = RB.format("gui.viewer.ReadsCanvasInfoPane.length",
				TabletUtils.nf.format(read.length()));
		else
			box.lengthData = RB.format("gui.viewer.ReadsCanvasInfoPane.lengthUnpadded",
				TabletUtils.nf.format(read.length()),
				TabletUtils.nf.format(rnd.getUnpaddedLength()));

		int mm = rmd.getMismatches();
		box.lengthData += " (" + mm + " "
			+ (mm == 1 ? RB.getString("gui.viewer.ReadsCanvasInfoPane.mismatch1") : RB.getString("gui.viewer.ReadsCanvasInfoPane.mismatch0")) + ")";

		// Cigar
		if (Assembly.hasCigar())
			box.cigar = RB.format("gui.viewer.ReadsCanvasInfoPane.cigar", truncate(rnd.getCigar()));

		// Inserted bases
		getInsertedBases(read, box);

		// ReadGroup
		if (rmd.getReadGroup() != 0)
			box.readGroupInfo = RB.format("gui.viewer.ReadsCanvasInfoPane.readGroupInfo",
				Assembly.getReadGroups().get(rmd.getReadGroup()-1).getID());

		// Pair info
		if (read instanceof MatedRead && rmd.getIsPaired() && rmd.getMateMapped())
		{
			String num = "";

			if (rmd.getNumberInPair() == 1)
				num = "(1/2)";
			else if(rmd.getNumberInPair() == 2)
				num = "(2/2)";

			if (rnd.isProperPair())
				box.pairInfo = RB.format("gui.viewer.ReadsCanvasInfoPane.pairedProper",
					num, rnd.getInsertSize());
			else
				box.pairInfo = RB.format("gui.viewer.ReadsCanvasInfoPane.pairedBad",
					num, rnd.getInsertSize());
		}

		// Mate status
		if (read instanceof MatedRead && rmd.getIsPaired())
			if (rmd.getMateMapped() == false)
				box.mateStatus = RB.getString("gui.viewer.ReadsCanvasInfoPane.mateUnmapped");


		box.read = read;
		box.rmd = rmd;

		return box;
	}

	private String truncate(String str)
	{
		if (str.length() < Prefs.visToolTipLimit)
			return str;

		else
			return str.substring(0, Prefs.visToolTipLimit) + "...";
	}

	private BoxData getMissingMateData(Read read)
	{
		BoxData box = new BoxData();

		ReadNameData rnd = Assembly.getReadNameData(read);

		// Name
		box.readName = truncate(rnd.getName()) + " "
			+ RB.getString("gui.viewer.ReadsCanvasInfoPane.mateRead");

		// Position data left blank to create some space in the box onscreen
		box.posData = "";

		// Mate status and position
		// Mate is not in the current contig...
		if (((MatedRead)read).isMateContig() == false)
		{
			box.mateStatus = RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithContig");
			box.matePosition = RB.format("gui.viewer.ReadsCanvasInfoPane.locatedInContig",
				rnd.getMateContig(), ((MatedRead)read).getMatePos() +1);
		}

		// Mate is in this contig, but not loaded int the current BAM window...
		else
		{
			box.mateStatus = RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithBamWindow");
			box.matePosition = RB.format("gui.viewer.ReadsCanvasInfoPane.positionInContig",
				((MatedRead)read).getMatePos() +1);
		}

		return box;
	}

	void getInsertedBases(Read read, BoxData box)
	{
		// TODO
		if(aPanel.getVisualContig().getTrackCount() <= 0)
			return;

		ArrayList<Feature> features = aPanel.getVisualContig().getTrack(0).getFeatures(read.s(), read.e());
		for (Feature feature : features)
		{
			if (!(feature.getGFFType().equals("CIGAR-I")))
				continue;

			CigarFeature cigarFeature = (CigarFeature) feature;
			for (CigarEvent insert : cigarFeature.getEvents())
			{
				if (insert.getRead().equals(read))
				{
					if (box.insertedBases == null)
						box.insertedBases = ((CigarInsertEvent)insert).getInsertedBases();
					else
						box.insertedBases += " - " + ((CigarInsertEvent)insert).getInsertedBases();
				}
			}
		}

		if (box.insertedBases != null)
		{
			box.insertedBases = truncate(box.insertedBases);
			box.insertedBases = RB.format("gui.viewer.ReadsCanvasInfoPane.inserted", box.insertedBases);
		}
	}

	void copyReadNameToClipboard()
	{
		String readName = box.readName;

		StringSelection selection = new StringSelection(readName);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	void copyDataToClipboard()
	{
		String lb = System.getProperty("line.separator");
		String seq = box.rmd.toString();

		StringBuilder text = new StringBuilder(seq.length() + 500);
		text.append(box.readName).append(lb).append(box.posData).append(lb).append(box.lengthData).append(lb);
		if(Assembly.hasCigar())
			text.append(box.cigar).append(lb);

		if (box.readGroupInfo != null)
			text.append(box.readGroupInfo).append(lb);

		if (box.rmd.isComplemented())
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionReverse")).append(lb).append(lb);
		else
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionForward")).append(lb).append(lb);

		// Produce a FASTA formatted string
		text.append(TabletUtils.formatFASTA(box.readName, seq));

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	static class BoxData
	{
		String readName, posData, lengthData;
		String cigar;
		String pairInfo;
		String insertedBases;
		String mateStatus, matePosition;
		String readGroupInfo;

		Read read;
		ReadMetaData rmd;
	}
}