package tablet.analysis;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import tablet.data.*;

/**
 * Given an assembly, this class will compare every read in each contig against
 * that contig's consensus sequence. Comparing each base position, differences
 * between the read and consensus are then encoded back into the read's data,
 * so that these comparisons do not have to be done in real-time when the data
 * is used for rendering. Note that this class is multi-core aware, running
 * threads that will independently process sets of reads.
 */
public class BasePositionComparator extends SimpleJob
{
	private Assembly assembly;

	private static int cores = Runtime.getRuntime().availableProcessors();

	private AtomicInteger progress = new AtomicInteger();

	public BasePositionComparator(Assembly assembly)
	{
		this.assembly = assembly;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		for (Contig contig: assembly)
			maximum += contig.readCount();

		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Future[] tasks = new Future[cores];

		// Start the threads...
		for (int i = 0; i < tasks.length; i++)
			tasks[i] = executor.submit(new ThreadRunner(i));
		// ...and then wait on them to finish
		for (Future task: tasks)
			task.get();

		executor.shutdown();

		System.out.println(maximum + " reads base compared in "
			+ (System.currentTimeMillis()-s) + "ms");
	}

	public int getValue()
		{ return progress.get(); }

	private class ThreadRunner implements Runnable
	{
		private int startIndex;

		ThreadRunner(int startIndex)
			{ this.startIndex = startIndex; }

		public void run()
		{
			Thread.currentThread().setName("BasePositionComparator-" + startIndex);

			byte NOTUSED = Sequence.NOTUSED;

			// The thread will process every contig...
			for (Contig contig: assembly)
			{
				Consensus consensus = contig.getConsensus();
				ArrayList<Read> reads = contig.getReads();
				int readCount = reads.size();

				// But will only deal with every [cores] read within it, so on
				// a 2-core machine, a thread will deal with every other read
				for (int i = startIndex; i < readCount && okToRun; i += cores)
				{
					Read read = reads.get(i);

					// Index start position within the consensus sequence
					int c = read.getStartPosition();
					int cLength = consensus.length();
					int rLength = read.length();

					for (int r = 0; r < rLength; r++, c++)
					{
						byte value = read.getStateAt(r);

						if (c < 0 || c >= cLength)
						{
							// Out of bounds means that this base on the read does
							// not have a corresponding position on the consensus
							// (and must therefore be different from it)
							read.setStateAt(r, (byte)(value+1));
						}
						else
						{
							// The DNATable encodes its states so that A and dA are
							// only ever 1 byte apart, meaning we can change quickly
							// by just incrementing the value by one
							if (consensus.getStateAt(c) != value && value > NOTUSED)
								read.setStateAt(r, (byte)(value+1));
						}
					}

					progress.addAndGet(1);
				}
			}
		}
	}
}