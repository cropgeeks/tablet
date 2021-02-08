// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.io.Cigar.*;

/**
 * Parses cigar strings and creates CIGAR features from events such as insertion, skipping, deletion, soft and hard
 * clipping CIGAR operators. It then associates these features with the supplied contig for later display on the
 * graphical features track
 */
public class CigarFeatureParser
{
	private static final int INSERT_LENGTH = 1;

	private final HashMap<String, CigarFeature> featureMap = new HashMap<>();

	private final Contig contig;

	public CigarFeatureParser(Contig contig)
	{
		this.contig = contig;
	}

	public void parseFeatures(String bases, int readStart, String cigarString, Read read)
	{
		int readIndex = 0;
		int position = readStart;

		Cigar cigar = new Cigar(cigarString);

		for (CigarParserEvent event : cigar.events())
		{
			int length = event.length();
			switch (event.type())
			{
				// In the normal case M, = and X boil down to the same case so we deliberately fall through cases M and
				// = to case X.
				case Cigar.MATCH: case Cigar.EQUAL: case Cigar.MISMATCH:
					position += length;
					readIndex += length;
					break;
				case Cigar.INSERTION:
					String insert = getInsertedSequence(bases, readIndex, length);
					addCigarInsertFeatureToMap(Feature.CIGAR_I, read, insert, position);
					readIndex += length;
					break;
				case Cigar.DELETION:
					addCigarEventToMap(Feature.CIGAR_D, length, read, position);
					position += length;
					break;
				case Cigar.SKIPPED:
					addCigarEventToMap(Feature.CIGAR_SKIPPED, length, read, position);
					position += length;
					break;
				case Cigar.SOFT_CLIP:
					addClipEvent(position, readStart, read);
					readIndex += length;
					break;
				case Cigar.HARD_CLIP:
					addClipEvent(position, readStart, read);
					break;
			}
		}
	}

	// Either gets the inserted bases from the read sequence, or if the read
	// sequence hasn't been provided, inserts the appropriate number of '?' bases
	private String getInsertedSequence(String bases, int readIndex, int length)
	{
		if (bases == null || bases.isEmpty() || bases.equals("*"))
			return String.join("", Collections.nCopies(length, "?"));

		return bases.substring(readIndex, readIndex+length);
	}

	private void addClipEvent(int position, int readStart, Read read)
	{
		if (position == readStart)
			addCigarEventToMap(Feature.CIGAR_LEFT_CLIP, 1, read, position);

		// We use position -1 as the we want the event to appear over the final base of the read, otherwise it would
		// show one position past the end of the read and look odd
		else
			addCigarEventToMap(Feature.CIGAR_RIGHT_CLIP, 1, read, position-1);
	}

	// Takes one of the CigarFeature maps stored in this class, a CIGAR-Event type, the length of the operation, the
	// event itself and the position and either adds this event to an existing feature within the map, or adds a new
	// feature to the map.
	private void addCigarEventToMap(String type, int length, Read read, int position)
	{
		String key = contig.getName() + type + position + "-" + length;

		// If a map contains a key, get and update the corresponding value. Otherwise add this key and new value to the
		// map. Method created to minimise repetition of code for processing inserts, deletions and skips.
		if(featureMap.get(key) == null)
		{
			CigarFeature feature = new CigarFeature(type, position, position + length -1);
			feature.addEvent(new CigarEvent(read));
			featureMap.put(key, feature);
		}
		else
		{
			CigarFeature cigarFeature = featureMap.get(key);
			cigarFeature.addEvent(new CigarEvent(read));
			featureMap.put(key, cigarFeature);
		}
	}

	private void addCigarInsertFeatureToMap(String type, Read read, String insert, int position)
	{
		String key = contig.getName() + type + position + "-" + INSERT_LENGTH;

		// If a map contains a key, get and update the corresponding value. Otherwise add this key and new value to the
		// map. Method created to minimise repetition of code for processing inserts, deletions and skips.
		if(featureMap.get(key) == null)
		{
			CigarFeature feature = new CigarFeature(type, position-1, position);
			feature.addEvent(new CigarInsertEvent(read, insert));
			featureMap.put(key, feature);
		}
		else
		{
			CigarFeature cigarFeature = featureMap.get(key);
			cigarFeature.addEvent(new CigarInsertEvent(read, insert));
			featureMap.put(key, cigarFeature);
		}
	}

	// Takes the supplied contig and adds the found CigarFeatures to that contig.
	void processCigarFeatures()
	{
		featureMap.values().stream()
			.filter(this::isFeatureVisible)
			.filter(contig::addFeature)
			.forEach(Feature::verifyType);
	}

	private boolean isFeatureVisible(CigarFeature feature)
	{
		return feature.count() >= Prefs.visCigarInsertMinimum;
	}
}