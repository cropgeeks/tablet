// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.
package tablet.io;

class CigarParser
{
	private int position;

	CigarParser()
	{
	}

	String cigarDecoder(String read, int position, String cigarString)
		throws Exception
	{
		this.position = position;

		StringBuilder readString = new StringBuilder();
		boolean first = true;

		if(cigarString.equals("*"))
			return read;
		else
		{
			String numberString = "";
			for(int i=0; i < cigarString.length(); i++)
			{
				if(Character.isDigit(cigarString.charAt(i)))
					numberString += cigarString.charAt(i);

				else
				{
					switch (cigarString.charAt(i))
					{
						case 'M':
							readString.append(processMatchOrInsertion(numberString, read));
							read = new String(read.substring(Integer.parseInt(numberString)));
							break;
						case 'P':
							readString.append(processPadOrDeletion(numberString));
							break;
						case 'I':
							readString.append(processMatchOrInsertion(numberString, read));
							read = new String(read.substring(Integer.parseInt(numberString)));
							break;
						case 'D':
							readString.append(processPadOrDeletion(numberString));
							break;
						case 'N':
							readString.append(processSkipped(numberString));
							break;
						case 'S':
							readString.append(processSoftClip(numberString, read, first));
							read = new String(read.substring(Integer.parseInt(numberString)));
							break;
						case 'H':
//							System.out.println("Process HardClip");
							break;
					}
					numberString = "";
					if(first)
						first = false;
				}
			}
			return readString.toString();
		}
	}

	private String  processMatchOrInsertion(String numberString, String read)
		throws Exception
	{
		return new String(read.substring(0, (Integer.parseInt(numberString))));
	}

	private String processPadOrDeletion(String numberString)
		throws Exception
	{
		StringBuilder padRead = new StringBuilder();
		int num = (Integer.parseInt(numberString));
		for(int i=0; i < num; i++)
		{
			padRead.append('*');
		}
		return padRead.toString();
	}

	private String processSoftClip(String numberString, String read, boolean first)
		throws Exception
	{
		if(first)
			position -= (Integer.parseInt(numberString));

		return new String(read.substring(0, (Integer.parseInt(numberString))));
	}

	private String processSkipped(String numberString)
		throws Exception
	{
		StringBuilder padRead = new StringBuilder();
		int num = (Integer.parseInt(numberString));
		for(int i=0; i < num; i++)
		{
			padRead.append('N');
		}
		return padRead.toString();
	}

	/**
	 * @return the position
	 */
	public int getPosition()
	{
		return position;
	}
}
