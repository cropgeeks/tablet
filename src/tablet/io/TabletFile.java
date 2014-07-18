// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

/**
 * Represents a .tablet file, that is a simple xml wrapper around actual
 * references to assembly, reference, feature, etc files or URLs.
 */
public class TabletFile //implements Comparable<TabletFile>
{
	public AssemblyFile assembly;
	public AssemblyFile reference;
	public ArrayList<AssemblyFile> annotations = new ArrayList<>();
	public String contig;
	public Integer position;

	public boolean hasDeterminedTypes = false;

	// Keep with default access so the GUI code can't make a TabletFile object
	// as we always want to create them via TabletFileHandler instead
	TabletFile()
	{
	}

	public boolean hasAssembly()
		{ return assembly != null; }

	public boolean hasReference()
		{ return reference != null; }

	public boolean hasAnnotations()
		{ return annotations.size() > 0; }

	public AssemblyFile[] getFileList()
	{
		ArrayList<AssemblyFile> list = new ArrayList<>();

		if (assembly != null)
			list.add(assembly);
		if (reference != null)
			list.add(reference);
		for (AssemblyFile annotation: annotations)
			list.add(annotation);

		return list.toArray(new AssemblyFile[] {});
	}

	public void determineFileTypes()
	{
		if (assembly != null)
			assembly.canDetermineType();
		if (reference != null)
			reference.canDetermineType();
		for (AssemblyFile annotation: annotations)
			annotation.canDetermineType();
	}

	public boolean equals(Object obj)
	{
		if (obj == null || obj.getClass() != getClass())
            return false;

		TabletFile o = (TabletFile) obj;

		if ((assembly == null ? o.assembly == null : assembly.equals(o.assembly)) == false)
			return false;
		if ((reference == null ? o.reference == null : reference.equals(o.reference)) == false)
			return false;
		if (annotations.equals(o.annotations) == false)
			return false;
		if ((contig == null ? o.contig == null : contig.equals(o.contig)) == false)
			return false;
		if ((position == null ? o.position == null : position.equals(o.position)) == false)
			return false;

		return true;
	}
}