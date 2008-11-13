package av.io;

import java.util.*;

import av.analysis.*;
import av.data.*;

class PostImportOperations
{
	private Assembly assembly;

	PostImportOperations(Assembly assembly)
	{
		this.assembly = assembly;
	}

	/**
	 * Sorts all the reads within each contig of the assembly so that they are
	 * listed in ascending start-position order.
	 */
	void sortReads()
	{
		for (Contig contig: assembly.getContigs())
			Collections.sort(contig.getReads());
	}

	void compareBases()
	{
		BasePositionComparator comp = new BasePositionComparator(assembly);
		comp.doComparisons();
	}
}