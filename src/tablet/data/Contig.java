// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

import tablet.data.auxiliary.*;

/**
 * The contig class holds the consensus sequence along with a list of reads and
 * features.
 */
public class Contig
{
	private String name;
	private Consensus consensus;
	private boolean complemented;

	private ArrayList<Read> reads = new ArrayList<Read>();

	private TableData tableData = new TableData();

	// Starting and ending indices of the region of data that is currently
	// viewable. Eg, from -10 to 109 (assuming consensus length 100 and reads
	// overhanging by 10 bases at each end). In the case of BAM subset views,
	// these values will always be the left and right extends of whichever reads
	// are loaded into memory.
	int visualS, visualE;

	// Starting and ending indices of all the viewable data (irrespective of
	// whether it's actually loaded or not). For non-BAM assemblies, these
	// values will be identical to visualS/visualE, but for BAM, they will most
	// likely be equal to 0 (consensus start) and consensus.length()-1
	private int dataS, dataE;


	// Objects for handling the ordering of reads (for display)
	private IReadManager readManager;
	private PackSet packSet;
	private StackSet stackSet;

	// Main set of features associated with this contig (basically every feature
	// held in a single FeatureTrack object)
	private FeatureTrack features = new FeatureTrack();
	// Supplementary set of features used purely for graphical outlining
	private ArrayList<Feature> outlines = new ArrayList<Feature>();




	/** Constructs a new, empty contig. */
	public Contig()
	{
	}

	/**
	 * Constructs an empty contig with the given name, and also attaches an
	 * empty consensus object to it. To be used when reference/consensus data
	 * is not available.
	 */
	public Contig(String name)
	{
		this.name = name;

		consensus = new Consensus();
	}

	/**
	 * Constructs a contig with the given name, marks whether it is complemented
	 * (or not) and initializes the list of reads to hold at least readCount
	 * reads.
	 * @param name the name for this contig
	 * @param complemented true if the consensus sequence is complemented
	 * @param readCount the number of reads that will be stored in this contig
	 */
	public Contig(String name, boolean complemented, int readCount)
	{
		this.name = name;
		this.complemented = complemented;

		reads = new ArrayList<Read>(readCount);
	}

	/**
	 * Returns the name of this contig.
	 * @return the name of this contig
	 */
	public String getName()
		{ return name; }

	/**
	 * Returns a string representation of this contig, which will be its name.
	 * @return a string representation of this contig
	 */
	public String toString()
		{ return name; }

	/**
	 * Returns the consensus sequence object for this contig.
	 * @return the consensus sequence object for this contig
	 */
	public Consensus getConsensus()
		{ return consensus; }

	/**
	 * Sets the consensus sequence object for this contig.
	 * @param consensus the consensus to be set
	 */
	public void setConsensusSequence(Consensus consensus)
	{
		this.consensus = consensus;

		tableData.consensusDefined = true;
	}

	/**
	 * Returns the reads held by this contig as a vector.
	 * @return the reads held by this contig as a vector
	 */
	public ArrayList<Read> getReads()
		{ return reads; }

	/**
	 * Returns a count of the number of reads held within this contig.
	 * @return a count of the number of reads held within this contig
	 */
	public int readCount()
		{ return reads.size(); }

	/**
	 * Returns the features held by this contig as a vector.
	 * @return the features held by this contig as a vector
	 */
	public ArrayList<Feature> getFeatures()
		{ return features.getFeatures(); }

	/**
	 * Returns the supplementary (outliner) features held within this contig.
	 * @return the supplementary (outliner) features held within this contig
	 */
	public ArrayList<Feature> getOutlines()
		{ return outlines; }

	public void calculateOffsets(Assembly assembly)
	{
		dataS = visualS = 0;
		dataE = visualE = tableData.consensusLength - 1;

		if (assembly.getBamBam() == null)
		{
			// Now scan all the reads and see if any of them extend beyond the
			// lhs or the rhs of the consensus...
			for (Read read: reads)
			{
				if (read.getStartPosition() < dataS)
					dataS = visualS = read.getStartPosition();
				if (read.getEndPosition() > dataE)
					dataE = visualE = read.getEndPosition();
			}
		}
		else
		{
			// BAM cannot show beyond the extent of the data block currently
			// loaded into memory
			visualS = assembly.getBamBam().getS();
			visualE = assembly.getBamBam().getE();
		}
	}

	public void setPackSet(PackSet packSet)
	{
		this.packSet = packSet;
		stackSet = new StackSet(reads);
	}

	/**
	 * Clears any references to this contig's pack and stack sets. To be called
	 * when a contig is no longer on screen (as these object's are only needed
	 * at display time). Is also called by the BAM loader as it loads in a new
	 * block of data (with doFullReset=true) to clear some of the stat values.
	 */
	public void clearContigData(boolean doFullReset)
	{
		readManager = null;

		packSet = null;
		stackSet = null;

		if (doFullReset)
		{
			reads.clear();
			tableData.mmTotalBases = 0;
			tableData.mmMismatches = 0;
		}
	}

	/**
	 * Returns the value of the index of the first base to be shown,
	 * that is, the left-most base on the display. This method should be used to
	 * determine the offset between a given base in display coordinates and its
	 * actual index in assembly coordinates. Eg, base 0 (left-most) base on the
	 * display, will not always be the first base of the assembly.
	 */
	public int getVisualStart()
		{ return visualS; }

	public int getVisualEnd()
		{ return visualE; }

	/**
	 * Returns the width of the data that can currently be shown, that is, the
	 * total number of nucleotides that span from the beginning of the block to
	 * the end of the block, including any gaps inbetween. In most cases,
	 * the width will probably be equal to the length of the consensus sequence
	 * but if any reads extend before or after the consensus, they will affect
	 * the overall width. For BAM, the width will always be the width of the
	 * data-block loaded into memory.
	 */
	public int getVisualWidth()
		{ return visualE - visualS + 1; }

	public int getDataStart()
		{ return dataS; }

	public int getDataEnd()
		{ return dataE; }

	/**
	 * Returns the total width of the contig, ie, the extent to which the user
	 * could scroll around the view (for BAM: assuming all the data was actually
	 * loaded).
	 */
	public int getDataWidth()
	{
		return dataE - dataS + 1;
	}

	/**
	 * Returns the height of this contig, that is, the total number of lines of
	 * data from top to bottom, including reads, but excluding the consensus.
	 */
	public int getVisualHeight()
		{ return readManager.size(); }

	public IReadManager getPackSetManager()
		{ return (readManager = packSet); }

	public IReadManager getStackSetManager()
		{ return (readManager = stackSet); }

	public void addOutline(Feature outline)
	{
		outlines.add(outline);

		// If the number of elements is higher than 5, remove the oldest
		if (outlines.size() > 5)
			outlines.remove(0);
	}

	public boolean addFeature(Feature newFeature)
	{
		return features.addFeatureDoSort(newFeature);
	}

	public TableData getTableData()
		{ return tableData; }

	/** Holds information required to properly render the contigs table. */
	public class TableData
	{
		/** True if the consensus data exists (its length > 0). */
		public boolean consensusDefined = false;

		private int consensusLength;

		/**
		 * True if this contig has properly defined reads, that is, it always
		 * has its list of reads and it never changes. A BAM file may cause
		 * a contig to have an undefined read list.
		*/
		public boolean readsDefined = true;

		// A count of how many bases (so far) have had mismatch data counted
		private long mmTotalBases = 0;
		// And the overall average mismatch count
		private long mmMismatches = 0;

		public void incrementMismatchData(long bases, long mismatches)
		{
			mmTotalBases += bases;
			mmMismatches += mismatches;
		}

		public int consensusLength()
		{
			if (consensusDefined)
				return consensus.length();

			return consensusLength;
		}

		public void setConsensusLength(int consensusLength)
			{ this.consensusLength = consensusLength; }


		// TODO: These may be changed to return different values at some point
		// (eg, if we know the actual BAM read count when it differs from
		// reads.size()

		public int readCount()
			{ return reads.size(); }

		public int featureCount()
			{ return getFeatures().size(); }

		public Float mismatchPercentage()
		{
			if (mmTotalBases > 0)
				return mmMismatches / (float) mmTotalBases * 100f;

			else if (readsDefined)
				return 0f;

			else
				return null;
		}
	}
}