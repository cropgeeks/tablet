// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.HashMap;

import tablet.data.Contig;

/**
 * Class which could potentially be fleshed out to store some visual assembly
 * concepts. Perhaps something to do with scaffolds and paired-end reads. Currently
 * the class just stores the hashmap of visual contigs which is populated as contigs
 * are selected by the user.
 */
public class VisualAssembly
{
	private HashMap<Contig, VisualContig> visualContigs;

	public VisualAssembly()
	{
		visualContigs = new HashMap<Contig, VisualContig>();
	}

	public HashMap<Contig, VisualContig> getVisualContigs()
		{	return visualContigs;	}
}