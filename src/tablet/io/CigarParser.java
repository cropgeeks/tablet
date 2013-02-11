// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;


/**
 * Class which contains the logic for parsing the CIGAR strings found in BAM to
 * give correct read strings.
 */
public class CigarParser
{
	private Contig contig;
	private String consensus;

	private HashMap<String, CigarFeature> insertionMap;
	private HashMap<String, CigarFeature> deletionMap;
	private HashMap<String, CigarFeature> skippedMap;
	private LinkedList<HashMap<String, CigarFeature>> featureMaps;

	/**
	 * Takes the contig currently being parsed. This is used to obtain the consensus
	 * which is required to build up read strings where the SEQ field of the read
	 * in the BAM file contains '=' characters and when the SEQ field == *.
	 */
	public CigarParser(Contig contig)
	{
		this.contig = contig;
		contig.getConsensus().getSequence();
		if (contig.getConsensus() != null)
			this.consensus = contig.getConsensus().toString();

		insertionMap = new HashMap<String, CigarFeature>();
		deletionMap = new HashMap<String, CigarFeature>();
		skippedMap = new HashMap<String, CigarFeature>();
		featureMaps = new LinkedList<HashMap<String, CigarFeature>>();
		featureMaps.add(insertionMap);
		featureMaps.add(deletionMap);
		featureMaps.add(skippedMap);
	}

	public String parse(String bases, int position, String cigar, Read read)
		throws Exception
	{
		// If the CIGAR string is empty we simply return the bases as given by
		// the SEQ field of the SAM/BAM record.
		if(cigar.equals("*"))
			return bases;

		// For easy iteration over the elements of the CIGAR string, parse out
		// two equally lengthed ArrayLists of Integers (for operation lengths)
		// and Characters (for operation types).
		String noString = "";
		ArrayList<Integer> lengths = new ArrayList<Integer>();
		ArrayList<Character> chars = new ArrayList<Character>();

		for (char cigChar : cigar.toCharArray())
		{
			if (Character.isDigit(cigChar))
				noString += cigChar;
			else
			{
				lengths.add(Integer.parseInt(noString));
				chars.add(cigChar);
				noString = "";
			}
		}

		// There are two different types of parsing for CIGAR strings. This is
		// based on whether the SEQ field in the SAM/BAM record contains read
		// bases, or the marker for missing data (*).
		if (bases.equals("*") == false)
			return parseCigar(lengths, chars, position, read, bases);
		else
			return parseMissingSeq(lengths, chars, position, read);
	}

	// The common case for CIGAR parsing. We have base data from the SEQ field
	// of the SAM/BAM file and can use this to build up the bases of our read
	// by carrying out the operations specified by the CIGAR string.
	// For out purposes we ignore the CIGAR H and P operations as they don't
	// affect the bases of our read.
	private String parseCigar(ArrayList<Integer> lengths, ArrayList<Character> chars, int position, Read read, String bases)
	{
		StringBuilder builder = new StringBuilder();
		int readIndex = 0;

		for (int i=0; i < chars.size(); i++)
		{
			int length = lengths.get(i);
			switch (chars.get(i))
			{
				// In the normal case M and = boil down to the same case
				case 'M':
				case '=':
					builder.append(processMatchOrMismatch(bases.substring(readIndex, readIndex+length), position));
					position += length;
					readIndex += length;
					break;
				case 'X':
					builder.append(addSequence('?', length));
					break;
				case 'I':
					addCigarEventToMap(insertionMap, "CIGAR-I", length, new CigarInsertEvent(read, bases.substring(readIndex, readIndex+length)), position);
					readIndex += length;
					break;
				case 'D':
					builder.append(addSequence('*', length));
					addCigarEventToMap(deletionMap, "CIGAR-D", length, new CigarEvent(read), position);
					position += length;
					break;
				case 'N':
					builder.append(addSequence('N', length));
					addCigarEventToMap(skippedMap, "CIGAR-N", length, new CigarEvent(read), position);
					position += length;
					break;
				case 'S':
					readIndex += length;
					break;
			}
		}
		return builder.toString();
	}

	// The other case for CIGAR parsing. The SEQ field in a SAM/BAM record is
	// set to '*' (meaning missing). In this case we can either infer from the
	// reference (where the CIGAR operations allow for that) or markup the bases
	// as unknown.
	// Don't need CIGAR S, P, H in case of missing SEQ.
	private String parseMissingSeq(ArrayList<Integer> lengths, ArrayList<Character> chars, int position, Read read)
	{
		StringBuilder missing = new StringBuilder();

		for (int index=0; index < chars.size(); index++)
		{
			int length = lengths.get(index);

			switch (chars.get(index))
			{
				case '=':
					missing.append(addUnknownSequence(position, length));
					break;
				// M (Match or Mismatch) is unknown due to the lack of read data
				case 'M':
				case 'X':
					missing.append(addSequence('?', length));
					break;
				case 'I':
					StringBuilder insertion = addUnknownSequence(position, length);
					addCigarEventToMap(insertionMap, "CIGAR-I", length, new CigarInsertEvent(read, insertion.toString()), position);
					break;
				case 'D':
					missing.append(addSequence('*', length));
					addCigarEventToMap(deletionMap, "CIGAR-D", length, new CigarEvent(read), position);
					break;
				case 'N':
					missing.append(addSequence('N', length));
					addCigarEventToMap(skippedMap, "CIGAR-N", length, new CigarEvent(read), position);
					break;
			}
			position += length;
		}
		return missing.toString();
	}

	// Processes a M or = in the normal case. If M it consumes the data provided
	// in the stirng read. If = it queries the reference (if we have one).
	private StringBuilder processMatchOrMismatch(String read, int position)
	{
		StringBuilder builder = new StringBuilder();
		for (int i=0; i < read.length(); i++)
		{
			char base = read.charAt(i);
			if (base == '=' && consensus != null)
				builder.append(consensus.charAt(position+i));
			else
				builder.append(read.charAt(i));
		}
		return builder;
	}

	// In the case where SEQ == * we need to either fill in data from the
	// consensus, or add sequence to represent unknown bases.
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

	// Takes one of the CigarFeature maps stored in this class, a CIGAR-Event
	// type, the length of the operation, the event itself and the position
	// and either adds this event to an existing feature within the map, or
	// adds a new feature to the map.
	private void addCigarEventToMap(HashMap<String, CigarFeature> map, String type, int length, CigarEvent event, int position)
	{
		String key;
		CigarFeature feature;
		if (event instanceof CigarInsertEvent)
		{
			// Don't use length as part of key for Cigar-I as you can have many
			// different events at the same position, so they need to be bound
			// up in the feature at that position. This isn't the case for
			// Cigar-D where different events are guaranteed to be in different
			// features.
			key = contig.getName() + "Tablet-Separator" + position;
			feature = new CigarFeature(type, "", position-1, position);
		}
		else
		{
			key = contig.getName() + "Tablet-Separator" + position + "-" + length;
			feature = new CigarFeature(type, "", position, position + length - 1);
		}

		// If a map contains a key, get and update the corresponding value. Otherwise
		// add this key and new value to the map. Method created to minimise repetition
		// of code for processing inserts, deletions and skips.
		if(map.get(key) == null)
		{
			feature.addEvent(event);
			map.put(key, feature);
		}
		else
		{
			CigarFeature cigarFeature = map.get(key);
			cigarFeature.addEvent(event);
			map.put(key, cigarFeature);
		}
	}

	// Takes the supplied contig and adds the found CigarFeatures to that contig.
	// TODO: Would ideally find a more logical place to store this functionality
	// this is the only common point between the BAM and SAM parsers.
	void processCigarFeatures()
	{
		for (HashMap<String, CigarFeature> map : featureMaps)
		{
			for (String feature : map.keySet())
			{
				CigarFeature cigarFeature = map.get(feature);

				if (cigarFeature.getCount() >= Prefs.visCigarInsertMinimum)
					if (contig.addFeature(cigarFeature))
						cigarFeature.verifyType();
			}
		}
	}

	// Calculates the length of a read from its CIGAR string. Can be used in
	// situations where we don't have another means to calculate the length
	// of a read (e.g. in the case of creating dummy features from reads in a
	// SAM file.
	public int calculateLength(String cigar)
	{
		int length = 0;

		String noString = "";
		for (char c : cigar.toCharArray())
		{
			if (Character.isDigit(c))
				noString += c;
			else
			{
				if (c == 'M' || c == '=' || c == 'X' || c == 'D')
					length += Integer.parseInt(noString);

				noString = "";
			}
		}
		return length;
	}

	LinkedList<HashMap<String, CigarFeature>> getFeatureMaps()
		{ return featureMaps; }
}