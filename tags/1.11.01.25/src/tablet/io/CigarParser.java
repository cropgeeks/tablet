// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.HashMap;
import tablet.data.Read;
import tablet.data.auxiliary.CigarFeature;

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
	private HashMap<String, CigarFeature> featureMap = new HashMap<String, CigarFeature>();

	boolean first = true;

	/**
	 * Constructor for CIGAR parser. Takes the reference sequence that was passed
	 * in and uses it to create a stringbuilder (allowing us to deal with insertions)
	 * and also a padding array (again to deal with insertions, but this time from
	 * the point of view of read positioning.
	 */
	public CigarParser()
	{
	}

	CigarParser(String currentContigName)
	{
		this.currentContigName = currentContigName;
	}

	/**
	 * The method to call when attempting to parse a CIGAR string.
	 *
	 * @param readString The read string as presented in BAM.
	 * @param position the position given by BAM.
	 * @param cigarString the CIGAR string to decode.
	 * @return
	 * @throws Exception
	 */
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
							builder.append(processDeletion(operationLength));
							readPos += operationLength;
							break;
						case 'N':
							builder.append(processSkipped(operationLength));
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
		String hashMap = currentContigName + "Tablet-Separator" + readPos;
		if(featureMap.get(hashMap) == null)
		{
			CigarFeature cigarFeature = new CigarFeature("CIGAR-I", "", readPos-1, readPos);
			cigarFeature.addInsert(read, insertion);
			featureMap.put(hashMap, cigarFeature);
		}
		else
		{
			CigarFeature cigarFeature = featureMap.get(hashMap);
			cigarFeature.addInsert(read, insertion);
			featureMap.put(hashMap, cigarFeature);
		}
		return new String(readString.substring(operationLength));
	}

	private String processDeletion(int operationLength)
		throws Exception
	{
		StringBuilder pad = new StringBuilder();
		for(int i=0; i < operationLength; i++)
		{
			pad.append('*');
		}
		return pad.toString();
	}

	private String processPad(int operationLength)
	{
		StringBuilder pad = new StringBuilder();
		for(int i=0; i < operationLength; i++)
		{
			pad.append('*');
		}
		return pad.toString();
	}

	private String processSoftClip(int operationLength, String read)
		throws Exception
	{
		return new String(read.substring(operationLength));
	}

	private String processSkipped(int operationLength)
		throws Exception
	{
		StringBuilder skip = new StringBuilder();
		for(int i=0; i < operationLength; i++)
		{
			skip.append('N');
		}
		return skip.toString();
	}

	/**
	 * @return the position
	 */
	public int getPosition()
	{
		return position;
	}

	public String getCurrentContigName()
	{
		return currentContigName;
	}

	public void setCurrentContigName(String currentContigName)
	{
		this.currentContigName = currentContigName;
	}

	public HashMap<String, CigarFeature> getFeatureMap()
	{
		return featureMap;
	}

}