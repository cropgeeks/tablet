// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import tablet.data.*;
import tablet.io.Cigar.*;

/**
 * Class which contains the logic for parsing the CIGAR strings found in BAM and SAM to give correct read strings.
 */
public class CigarParser
{
	private static final char SKIPPED_BASE = 'N';
	private static final char DELETED_BASE = '*';

	// We allow loading of assemblys without references so this can't be final
	private String consensus;

	/**
	 * Takes the contig currently being parsed. This is used to obtain the consensus
	 * which is required to build up read strings where the SEQ field of the read
	 * in the BAM file contains '=' characters and when the SEQ field == *.
	 */
	public CigarParser(Contig contig)
	{
		// Get sequence forces the reference to be loaded from disk cache
		contig.getConsensus().getSequence();
		if (contig.getConsensus() != null)
			this.consensus = contig.getConsensus().toString();
	}

	public String parse(String bases, int position, String cigarString, Read read)
		throws Exception
	{
		// If the CIGAR string is empty we simply return the bases as given by the SEQ field of the SAM/BAM record.
		if (cigarString.equals("*"))
			return bases;

		Cigar cigar = new Cigar(cigarString);

		// There are two different types of parsing for CIGAR strings. This is based on whether the SEQ field in the
		// SAM/BAM record contains read bases, or the marker for missing data (*).
		String parsedSequence;
		if (bases.equals("*") == false)
			parsedSequence =  parseCigar(cigar, position, bases);
		else
			parsedSequence = parseMissingSeq(cigar, position);

		return parsedSequence;
	}

	// The common case for CIGAR parsing. We have base data from the SEQ field of the SAM/BAM file and can use this to
	// build up the bases of our read by carrying out the operations specified by the CIGAR string. For out purposes we
	// ignore the CIGAR H and P operations as they don't affect the bases of our read.
	private String parseCigar(Cigar cigar, int readStart, String bases)
	{
		StringBuilder builder = new StringBuilder();
		int readIndex = 0;
		int position = readStart;

		for (CigarParserEvent event : cigar.events())
		{
			int length = event.length();
			switch (event.type())
			{
				// In the normal case M, = and X boil down to the same case so we deliberately fall through cases M and
				// = to case X.
				case Cigar.MATCH: case Cigar.EQUAL: case Cigar.MISMATCH:
				builder.append(processMatchOrMismatch(bases.substring(readIndex, readIndex+length), position));
				position += length;
				readIndex += length;
				break;
				// Deliberately fall through case I to case S
				case Cigar.INSERTION: case Cigar.SOFT_CLIP:
				readIndex += length;
				break;
				case Cigar.DELETION:
					builder.append(addSequence(DELETED_BASE, length));
					position += length;
					break;
				case Cigar.SKIPPED:
					builder.append(addSequence(SKIPPED_BASE, length));
					position += length;
					break;
			}
		}

		return builder.toString();
	}

	// The other case for CIGAR parsing. The SEQ field in a SAM/BAM record is set to '*' (meaning missing). In this case
	// we can either infer from the reference (where the CIGAR operations allow for that) or markup the bases as
	// unknown. Don't need CIGAR S, P, H in case of missing SEQ.
	private String parseMissingSeq(Cigar cigar, int readStart)
	{
		StringBuilder missing = new StringBuilder();
		int position = readStart;

		for (CigarParserEvent event : cigar.events())
		{
			int length = event.length();

			switch (event.type())
			{
				case Cigar.EQUAL:
					missing.append(addUnknownSequence(position, length));
					break;
				// M (Match or Mismatch) is unknown due to the lack of read data
				case Cigar.MATCH: case Cigar.MISMATCH:
					missing.append(addSequence('?', length));
					break;
				case Cigar.DELETION:
					missing.append(addSequence(DELETED_BASE, length));
					break;
				case Cigar.SKIPPED:
					missing.append(addSequence(SKIPPED_BASE, length));
					break;
			}
			position += length;
		}
		return missing.toString();
	}

	// Processes a M or = in the normal case. If M it consumes the data provided in the stirng read. If = it queries the
	// reference (if we have one).
	private StringBuilder processMatchOrMismatch(String read, int position)
	{
		StringBuilder builder = new StringBuilder();
		for (int i=0; i < read.length(); i++, position++)
		{
			char base = read.charAt(i);

			if (canReplaceEqualsSeq(base))
				base = consensus.charAt(position);

			builder.append(base);
		}
		return builder;
	}

	private boolean canReplaceEqualsSeq(char base)
	{
		return base == Cigar.EQUAL && consensus != null;
	}

	// In the case where SEQ == * we need to either fill in data from the consensus, or add sequence to represent
	// unknown bases.
	private StringBuilder addUnknownSequence(int start, int length)
	{
		StringBuilder builder = new StringBuilder();

		if (consensus != null && consensus.isEmpty() == false)
		{
			for (int i=start; i < start+length; i++)
				builder.append(consensus.charAt(i));
		}
		else
			builder = addSequence('?', length);

		return builder;
	}

	private StringBuilder addSequence(char base, int length)
	{
		StringBuilder builder = new StringBuilder();

		for (int i =0; i < length; i++)
			builder.append(base);

		return builder;
	}
}