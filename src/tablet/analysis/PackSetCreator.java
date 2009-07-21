package tablet.analysis;

import java.util.*;
import java.util.concurrent.atomic.*;

import tablet.data.*;

/**
 * Runs through every contig in an assembly and builds its pack sets for it.
 * Note that this class is multi-core aware, running threads that will process
 * an entire contig at a time.
 */
public class PackSetCreator extends SimpleJob
{
	private Assembly assembly;

	private static final int cores = Runtime.getRuntime().availableProcessors();
	private AtomicInteger progress = new AtomicInteger();

	public PackSetCreator(Assembly assembly)
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

		ThreadRunner[] threads = new ThreadRunner[cores];

		// Start the threads...
		for (int i = 0; i < threads.length; i++)
			threads[i] = new ThreadRunner(i);
		// ...and then wait on them to finish
		for (int i = 0; i < threads.length; i++)
			threads[i].join();

		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");
	}

	public int getValue()
		{ return progress.get(); }

	// Performs the actual packing of the data. This class is told to run on
	// the set of contigs, but starting from a given index, and incrementing at
	// a given value. So for a 2-core machine, thread 1 will run on contigs 0,
	// 2, 4, 6, etc, while thread-2 is running on contigs 1, 3, 5, 7, etc.
	private class ThreadRunner extends Thread
	{
		private int startIndex, increment;

		ThreadRunner(int startIndex)
		{
			this.startIndex = startIndex;

			setName("PackSetCreator-" + startIndex);
			start();
		}

		public void run()
		{
			Vector<Contig> contigs = assembly.getContigs();

			for (int i = startIndex; i < contigs.size(); i += cores)
			{
				Contig contig = contigs.get(i);
				PackSet packSet = new PackSet();

				for (Read read: contig.getReads())
				{
					// Check for quit/cancel on the job...
					if (okToRun == false)
						return;

					boolean added = false;

					// Can this read be added to any of the existing pack lines?
					for (Pack pack: packSet)
						if (added = pack.addRead(read))
							break;

					// If not, create a new pack and add it there
					if (added == false)
					{
						Pack newPack = new Pack();
						added = newPack.addRead(read);

						packSet.addPack(newPack);
					}

					progress.addAndGet(1);

				}

				contig.setPackSet(packSet);
			}
		}
	}
}