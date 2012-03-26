// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import net.sf.samtools.*;
import static net.sf.samtools.SAMReadGroupRecord.*;

// Provides a wrapper around the Samtools/Picard SAMReadGroupRecord class, so
// that simple getXXX() methods can be called that always return valid strings,
// even if that attribute hasn't been set.
public class ReadGroup
{
	private SAMReadGroupRecord record;

	public ReadGroup()
	{
		record = new SAMReadGroupRecord("");
	}

	public ReadGroup(SAMReadGroupRecord record)
	{
		this.record = record;
	}

	public SAMReadGroupRecord getRecord()
		{ return record; }

	// Should never be null!
	public String getID()
	{
		return record.getId();
	}

	private String getAttribute(String tag)
	{
		if (record.getAttribute(tag) != null)
			return record.getAttribute(tag);

		return "";
	}

	public String getAttributeByTag(String tag)
	{
		if (tag == "CN") return getCN();
		if (tag == "DS") return getDS();
		if (tag == "DT") return getDT();
		if (tag == "FO") return getFO();
		if (tag == "KS") return getKS();
		if (tag == "LB") return getLB();
		if (tag == "PG") return getPG();
		if (tag == "PI") return getPI();
		if (tag == "PL") return getPL();
		if (tag == "PU") return getPU();
		if (tag == "SM") return getSM();

		return null;
	}

	public String getCN()
		{ return getAttribute(SEQUENCING_CENTER_TAG); }

	public String getDS()
		{ return getAttribute(DESCRIPTION_TAG); }

	public String getDT()
		{ return getAttribute(DATE_RUN_PRODUCED_TAG); }

	public String getFO()
		{ return getAttribute(FLOW_ORDER_TAG); }

	public String getKS()
		{ return getAttribute(KEY_SEQUENCE_TAG); }

	public String getLB()
		{ return getAttribute(LIBRARY_TAG); }

	// TODO: Tag not yet handled by Picard
	public String getPG()
		{ return ""; }

	public String getPI()
		{ return getAttribute(PREDICTED_MEDIAN_INSERT_SIZE_TAG); }

	public String getPL()
		{ return getAttribute(PLATFORM_TAG); }

	public String getPU()
		{ return getAttribute(PLATFORM_UNIT_TAG); }

	public String getSM()
		{ return getAttribute(READ_GROUP_SAMPLE_TAG); }
}