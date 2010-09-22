// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import tablet.data.*;
import tablet.data.cache.*;

public class MappingData
{
	private IArrayIntCache paddedToUnpadded;
	private IArrayIntCache unpaddedToPadded;
	
	private Consensus consensus;

	public MappingData(Consensus consensus)
	{
		this.consensus = consensus;
	}

	/**
	 * Returns the equivalent unpadded base given a padded base.
	 */
	public int getPaddedToUnpadded(int value)
			throws Exception
	{
		if(value < 0 || value >= consensus.length())
			return -1;
		// If there are no pads, return the unpadded base
		if(paddedToUnpadded.length() == 0)
			return value;

		int result = searchPaddingCache(value, paddedToUnpadded);

		// If we found a pad return -1
		if(paddedToUnpadded.getValue(result*2) == value)
			return -1;
		// If not we need to work out the value to return
		else if(paddedToUnpadded.getValue(result*2) > value)
		{
			if(result*2 > 0)
				return value - paddedToUnpadded.getValue((result*2)-1);
			else
				return value;
		}
		// In the case that the value we are looking for is greater than the
		// largest key in the array
		else
		{
			return value - paddedToUnpadded.getValue((result*2)+1);
		}
	}

	/**
	 * Returns a padded value given an unpadded one. When using this you need to
	 * check if this value is within the dataset. (i.e. less than consensus length)
	 */
	public int getUnpaddedToPadded(int value)
			throws Exception
	{
		if(value < 0 || value >= consensus.getUnpaddedLength())
			return -1;
		// If there are no pads, return the unpadded base
		if(unpaddedToPadded.length() == 0)
			return value;
		
		int result =  searchPaddingCache(value, unpaddedToPadded);

		// If we didn't find the value, look for the closest value lower than it
		if(unpaddedToPadded.getValue(result*2) > value)
		{
			// If the result is greater than 0 return the value one lower than
			// this key
			if(result*2 > 0)
				return value + unpaddedToPadded.getValue((result*2)-1);
			return value;
		}
		// In the case that the value we are looking for is greater than the
		// largest key in the array
		else
		{
			return value + unpaddedToPadded.getValue((result*2)+1);
		}
	}

	/**
	 * Binary searches the cache provided as a parameter, only looking at the
	 * even elements as the keys for the data are stored in the even elements
	 * with the number of pads for that base position being stored in the odd
	 * elements.
	 */
	private int searchPaddingCache(int value, IArrayIntCache array)
			throws Exception
	{
		// Effectively chop the array in half, we are only searching on half of
		// the data
		int high = (array.length()/2)-1;
		int low = 0;
		int mid = 0;

		while(low <= high)
		{
			// Avoids possible integer overflow in traditional binSearch
			mid = low + ((high-low) /2);

			if (array.getValue(mid*2) < value)
			{
				low = mid + 1;
			}
			else if(array.getValue(mid*2) > value)
			{
				high = mid -1;
			}
			// We've found what we're looking for, return the index to it
			else
			{
				return mid;
			}
		}
		// This is a special case of a binary search where we want to return mid
		// even if we haven't found what we were looking for there.
		return mid;
	}


	public IArrayIntCache getPaddedToUnpaddedCache()
	{
		return paddedToUnpadded;
	}
	public void setPaddedToUnpaddedCache(IArrayIntCache paddedToUnpadded)
		{	this.paddedToUnpadded = paddedToUnpadded;	}

	public IArrayIntCache getUnpaddedToPaddedCache()
		{	return unpaddedToPadded;	}

	public void setUnpaddedToPaddedCache(IArrayIntCache unpaddedToPadded)
		{	this.unpaddedToPadded = unpaddedToPadded;	}
}