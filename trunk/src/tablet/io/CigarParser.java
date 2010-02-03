// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.
package tablet.io;

import java.util.HashMap;

/**
 * Class which contains the logic for parsing the CIGAR strings found in BAM to
 * give correct read strings and also an updated consensus sequence to reflect
 * any insertions which may have occurred.
 */
class CigarParser
{
	private int position;
	private int readPos;
	private String currentContigName;
	private HashMap<String, Integer> featureMap = new HashMap<String, Integer>();

	/**
	 * Constructor for CIGAR parser. Takes the reference sequence that was passed
	 * in and uses it to create a stringbuilder (allowing us to deal with insertions)
	 * and also a padding array (again to deal with insertions, but this time from
	 * the point of view of read positioning.
	 */
	CigarParser()
	{
	}

	/**
	 * The method to call when attempting to parse a CIGAR string.
	 *
	 * @param read The read string as presented in BAM.
	 * @param position the position given by BAM.
	 * @param cigarString the CIGAR string to decode.
	 * @return
	 * @throws Exception
	 */
	String parse(String read, int position, String cigarString)
		throws Exception
	{
		this.position = position;
		readPos = position;

		StringBuilder readString = new StringBuilder();
		boolean first = true;

		// If we have been presented with an empty cigar string
		if(cigarString.equals("*"))
			return read;
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
							readString.append(processMatchOrMismatch(operationLength, read));
							read = new String(read.substring(operationLength));
							readPos += operationLength;
							break;
						case '=':
							readString.append(processMatchOrMismatch(operationLength, read));
							read = new String(read.substring(operationLength));
							readPos += operationLength;
							break;
						case 'X':
							readString.append(processMatchOrMismatch(operationLength, read));
							read = new String(read.substring(operationLength));
							readPos += operationLength;
							break;
						case 'P':
							//readString.append(processPad(operationLength));
							break;
						case 'I':
							read = processInsertion(operationLength, read);
							break;
						case 'D':
							readString.append(processDeletion(operationLength));
							readPos += operationLength;
							break;
						case 'N':
							readString.append(processSkipped(operationLength));
							readPos += operationLength;
							break;
						case 'S':
							read = processSoftClip(operationLength, read);
							break;
						case 'H':
//							System.out.println("Process HardClip");
							break;
					}
					numberString = "";
				}
			}

			return readString.toString();
		}
	}

	private String  processMatchOrMismatch(int operationLength, String read)
		throws Exception
	{
		return read.substring(0, operationLength);
	}

	private String processInsertion(int operationLength, String read)
	{
		//String hashMap
//		if(featureMap.get() == null)
//		{
//			featureMap.put(currentContigName + "*_*_*" + readPos+"-"+(readPos+1), 1);
//		}
//		else
//		{
//			int value = featureMap.get(currentContigName + "*_*_*" + readPos+"-"+(readPos+1));
//			featureMap.put(currentContigName + "*_*_*" + readPos+"-"+(readPos+1), ++value);
//		}
		return new String(read.substring(operationLength));
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

	public HashMap<String, Integer> getFeatureMap()
	{
		return featureMap;
	}
}
