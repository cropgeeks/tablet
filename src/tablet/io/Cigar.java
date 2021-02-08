// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

/**
 * An object based representation of a CIGAR string. It consists of a list of CigarParserEvents which are objects of an
 * inner class which represent a single CIGAR event (i.e. pair of digit and character / operator).
  */
public class Cigar
{
	public static final char MATCH = 'M';
	public static final char EQUAL = '=';
	public static final char MISMATCH = 'X';
	public static final char DELETION = 'D';
	public static final char INSERTION = 'I';
	public static final char SOFT_CLIP = 'S';
	public static final char HARD_CLIP = 'H';
	public static final char SKIPPED = 'N';

	private final List<CigarParserEvent> events;

	public Cigar(String cigar)
	{
		// For easy iteration over the elements of the CIGAR string, parse the string into a list of CigarParserEvents
		// which are a pair of a character (one of the constants defined in this class) and an int length representing
		// the length of the operation
		String noString = "";
		events = new ArrayList<>();

		for (char cigChar : cigar.toCharArray())
		{
			if (Character.isDigit(cigChar))
				noString += cigChar;
			else
			{
				events.add(new CigarParserEvent(cigChar, Integer.parseInt(noString)));
				noString = "";
			}
		}
	}

	public List<CigarParserEvent> events()
	{
		return events;
	}

	// Calculates the length of a read from its CIGAR string. Can be used in
	// situations where we don't have another means to calculate the length
	// of a read (e.g. in the case of creating dummy features from reads in a
	// SAM file.
	public int calculateLength()
	{
		return events.stream()
			.filter(e -> e.type() == MATCH || e.type() == EQUAL || e.type() == MISMATCH || e.type() == DELETION)
			.mapToInt(CigarParserEvent::length)
			.sum();
	}

	/**
	 * Represents a single CIGAR event. The char type is the operator and the int length is the length of the CIGAR
	 * operation.
	 */
	public class CigarParserEvent
	{
		private final char type;
		private final int length;

		public CigarParserEvent(char type, int length)
		{
			this.type = type;
			this.length = length;
		}

		public char type()
		{
			return type;
		}

		public int length()
		{
			return length;
		}
	}
}