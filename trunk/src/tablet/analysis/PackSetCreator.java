package tablet.analysis;

import tablet.data.*;

/**
 * Runs through every contig in an assembly and builds its pack sets for it.
 */
public class PackSetCreator extends SimpleJob
{
	private Assembly assembly;

	public PackSetCreator(Assembly assembly)
	{
		this.assembly = assembly;
	}

	public void runJob(int jobIndex)
	{
		long s = System.currentTimeMillis();

		// How many reads do we have to deal with?
		for (Contig contig: assembly)
			maximum += contig.readCount();

		for (Contig contig: assembly)
		{
			PackSet packSet = new PackSet();

			for (Read read: contig.getReads())
			{
				// Check for quit/cancel on the job...
				if (okToRun == false)
					return;

				boolean added = false;

				// Can this read be added to any of the existing pack lines?
				for (Pack pack: packSet)
				{
					if (added = pack.addRead(read))
						break;
				}

				// If not, create a new pack and add it there
				if (added == false)
				{
					Pack newPack = new Pack();
					newPack.addRead(read);

					added = packSet.addPack(newPack);
				}

				progress++;
			}

			contig.setPackSet(packSet);
		}

		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");
	}
}