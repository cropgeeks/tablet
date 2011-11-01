// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.ArrayList;
import tablet.data.Read;

public class CigarFeature extends Feature
{
	private ArrayList<Insert> inserts;

	public CigarFeature(String gffType, String name, int p1, int p2)
	{
		super(gffType, name, p1, p2);

		inserts = new ArrayList<Insert>();
	}

	public int getCount()
	{
		return inserts.size();
	}

	public ArrayList<Insert> getInserts()
	{
		return inserts;
	}

	public void addInsert(Read read, String insertion)
	{
		inserts.add(new Insert(read, insertion));
	}

	public class Insert
	{
		Read read;
		String insertedBases;

		public Insert(Read read, String insertedBases)
		{
			this.read = read;
			this.insertedBases = insertedBases;
		}

		public Read getRead()
		{
			return read;
		}

		public void setRead(Read read)
		{
			this.read = read;
		}

		public String getInsertedBases()
		{
			return insertedBases;
		}

		public void setInsertedBases(String insertedBases)
		{
			this.insertedBases = insertedBases;
		}
	}
}