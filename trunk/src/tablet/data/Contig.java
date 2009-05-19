package tablet.data;

import java.util.*;

import tablet.data.cache.*;

public class Contig
{
	private String name;
	private boolean complemented;

	private Consensus consensus;

	private Vector<Read> reads;
	// Starting and ending indices of the leftmost and rightmost reads
	private int lhsOffset, rhsOffset;

	private PackSet packSet;
	private StackSet stackSet;

	private IReadManager readManager;

	public Contig()
	{
	}

	public Contig(String name, boolean complemented, int readCount)
	{
		this.name = name;
		this.complemented = complemented;

		reads = new Vector<Read>(readCount);
	}

	public String getName()
		{ return name; }

	public String toString()
		{ return name; }

//	public void setName(String name)
//		{ this.name = name; }

	public boolean getComplemented()
		{ return complemented; }

//	public void setComplemented(boolean complemented)
//		{ this.complemented = complemented; }

	public Consensus getConsensus()
		{ return consensus; }

	public Vector<Read> getReads()
		{ return reads; }

	public void setConsensusSequence(Consensus consensus)
		{ this.consensus = consensus; }

	public void determineOffsets()
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