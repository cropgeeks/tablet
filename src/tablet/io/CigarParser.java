// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;


	/**
 * Class which contains the logic for parsing the CIGAR strings found in BAM to
 * give correct read strings and also an updated consensus sequence to reflect
 * any insertions which may have occurred.
 */
public class CigarParser
{
	private int position;
	private int readPos;
	private String currentContigName;
	private HashMap<String, CigarFeature> insertionMap;
	private HashMap<String, CigarFeature> deletionMap;
	private HashMap<String, CigarFeature> skippedMap;
	private LinkedList<HashMap<String, CigarFeature>> featureMaps;
	private static final String CIGAR_D = "CIGAR-D";
	private static final String CIGAR_I = "CIGAR-I";
	private static final String CIGAR_N = "CIGAR-N";

	/**
	 * Constructor for CIGAR parser. Takes the reference sequence that was passed
	 * in and uses it to create a stringbuilder (allowing us to deal with insertions)
	 * and also a padding array (again to deal with insertions, but this time from
	 * the point of view of read positioning.
	 */
	public CigarParser()
	{
		setup();
	}

	CigarParser(String currentContigName)
	{
		this.currentContigName = currentContigName;
		setup();
	}

	private void setup()
	{
		insertionMap = new HashMap<>();
		deletionMap = new HashMap<>();
		skippedMap = new HashMap<>();
		featureMaps = new LinkedList<>();
		featureMaps.add(insertionMap);
		featureMaps.add(deletionMap);
		featureMaps.add(skippedMap);
	}

	public String parse(String readString, int position, String cigarString, Read read)
		throws Exception
	{
		this.position = position;
		readPos = position;

		StringBuilder builder = new StringBuilder();

		// If we have been presented with an empty cigar string
		if(cigarString.equals("*"))
			return readString;
		else
		{
			String numberString = "";
			int operationLength = 0;
			// Loop over the cigarString
			for(int i=0; i < cigarString.length(); i++)
			{
				// If the character is a digit
				if(Character.isDigit(cigarString.charAt(i)))
				{
					numberString += cigarString.charAt(i);
					operationLength = Integer.parseInt(numberString);
				}
				// If the character is any other character
				else
				{
					switch (cigarString.charAt(i))
					{
						// For M, = and X treat as Match/Mismatch.
						case 'M':
							builder.append(processMatchOrMismatch(operationLength, readString));
							readString = new String(readString.substring(operationLength));
							readPos += operationLength;
							break;
						case '=':
							builder.append(processMatchOrMismatch(operationLength, readString));
							readString = new String(readString.substring(operationLength));
							readPos += operationLength;
							break;
						case 'X':
							builder.append(processMatchOrMismatch(operationLength, readString));
							readString = new String(readString.substring(operationLength));
							readPos += operationLength;
							break;
						case 'P':
							//readString.append(processPad(operationLength));
							break;
						case 'I':
							readString = processInsertion(operationLength, readString, read);
							break;
						case 'D':
							builder.append(processDeletion(operationLength, read));
							readPos += operationLength;
							break;
						case 'N':
							builder.append(processSkipped(operationLength, read));
							readPos += operationLength;
							break;
						case 'S':
							readString = processSoftClip(operationLength, readString);
							break;
						case 'H':
//							System.out.println("Process HardClip");
							break;
					}
					numberString = "";
				}
			}

			return builder.toString();
		}
	}

	private String  processMatchOrMismatch(int operationLength, String read)
		throws Exception
	{
		return read.substring(0, operationLength);
	}

	private String processInsertion(int operationLength, String readString, Read read)
		throws Exception
	{
		String insertion = new String(readString.substring(0, operationLength));
		CigarEvent event = new CigarInsertEvent(read, insertion);
		addCigarEventToMap(insertionMap, CIGAR_I, operationLength, event);

		return new String(readString.substring(operationLength));
	}

	private String processDeletion(int operationLength, Read read)
		throws Exception
	{
		StringBuilder pad = new StringBuilder();

		for(int i=0; i < operationLength; i++)
			pad.append('*');

		CigarEvent event = new CigarEvent(read);
		addCigarEventToMap(deletionMap, CIGAR_D, operationLength, event);

		return pad.toString();
	}

	private String processPad(int operationLength)
	{
		StringBuilder pad = new StringBuilder();
		for(int i=0; i < operationLength; i++)
			pad.append('*');

		return pad.toString();
	}

	private String processSoftClip(int operationLength, String read)
		throws Exception
	{
		return new String(read.substring(operationLength));
	}

	private String processSkipped(int operationLength, Read read)
		throws Exception
	{
		StringBuilder skip = new StringBuilder();
		for(int i=0; i < operationLength; i++)
			skip.append('N');

		CigarEvent event = new CigarEvent(read);
		addCigarEventToMap(skippedMap, CIGAR_N, operationLength, event);

		return skip.toString();
	}

	private void addCigarEventToMap(HashMap<String, CigarFeature> map, String cigarType, int operationLength, CigarEvent event)
	{
		String key = currentContigName + "Tablet-Separator" + readPos + "-" + operationLength;
		CigarFeature feature;
		if (cigarType.equals(CIGAR_I))
			feature = new CigarFeature(cigarType, "", readPos-1, readPos);
		else
			feature = new CigarFeature(cigarType, "", readPos, readPos + operationLength - 1);

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

	void processCigarFeatures(Contig contig)
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

	public int getPosition()
		{ return position; }

	public String getCurrentContigName()
		{ return currentContigName; }

	public void setCurrentContigName(String currentContigName)
	{
		this.currentContigName = currentContigName;
	}

	LinkedList<HashMap<String, CigarFeature>> getFeatureMaps()
		{ return featureMaps; }
}