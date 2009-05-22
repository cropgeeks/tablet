package tablet.data;

import java.util.*;

import tablet.data.cache.*;

/**
 * The contig class holds the consensus sequence along with a list of reads and
 * features.
 */
public class Contig
{
	private String name;
	private Consensus consensus;
	private boolean complemented;

	private Vector<Read> reads;

	// Starting and ending indices of the leftmost and rightmost reads
	private int lhsOffset, rhsOffset;

	// Objects for handling the ordering of reads (for display)
	private IReadManager readManager;
	private PackSet packSet;
	private StackSet stackSet;

	private Vector<Feature> features = new Vector<Feature>();

	/** Constructs a new, empty contig. */
	public Contig()
	{
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

		reads = new Vector<Read>(readCount);
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
		{ this.consensus = consensus; }

	/**
	 * Returns the reads held by this contig as a vector.
	 * @return the reads held by this contig as a vector
	 */
	public Vector<Read> getReads()
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
	public Vector<Feature> getFeatures()
		{ return features; }

	/**
	 * Returns a count of the number of features held within this contig.
	 * @return a count of the number of features held within this contig
	 */
	public int featureCount()
		{ return features.size(); }

	public void calculateOffsets()
	{
		// Set the rhsOffset to the final index position in the consensus seq
		rhsOffset = consensus.length() - 1;

		// Now scan all the reads and see if any of them extend beyond the lhs
		// or the rhs of the consensus...
		for (Read read: reads)
		{
			if (read.getStartPosition() < lhsOffset)
				lhsOffset = read.getStartPosition();
			if (read.getEndPosition() > rhsOffset)
				rhsOffset = read.getEndPosition();
		}
	}

	public void setPackSet(PackSet packSet)
	{
		this.packSet = packSet;
		stackSet = new StackSet(reads);
	}

	public int getConsensusOffset()
		{ return -lhsOffset; }

	/**
	 * Returns the width of this contig, that is, the total number of
	 * nucleotides that span from the beginning of the left most read to the end
	 * of the right most read, including any gaps inbetween. In most cases,
	 * the width will probably be equal to the length of the consensus sequence
	 * but if any reads extend before or after the consensus, they will affect
	 * the overall width.
	 */
	public int getWidth()
		{ return rhsOffset - lhsOffset + 1; }

	/**
	 * Returns the height of this contig, that is, the total number of lines of
	 * data from top to bottom, including reads, but excluding the consensus.
	 */
	public int getHeight()
		{ return readManager.size(); }

	public IReadManager getPackSetManager()
		{ return (readManager = packSet); }

	public IReadManager getStackSetManager()
		{ return (readManager = stackSet); }

	void print(IReadCache cache)
	{
		System.out.println();
		System.out.println("Contig " + name + " (" + complemented + ")");

		consensus.print();

		for (Read read: reads)
			read.print(cache);
	}
}