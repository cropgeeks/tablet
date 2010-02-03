// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.nio.*;

/**
 * Tracks and maintains the index (on disk) to a FileCache object.
 */
class ReadFileCacheIndex extends TabletCache
{
	ReadFileCacheIndex(File cacheFile)
	{
		super.cacheFile = cacheFile;
	}

	// Jump id*<long 8?> bytes into the file and return the byte found there
	long getSeekPosition(int id)
		throws IOException
	{
		rnd.seek(id * 8);

		return rnd.readLongFromBuffer();
	}

	void setNextSeekPosition(long byteCount)
		throws IOException
	{
		byte[] array = new byte[8];

		ByteBuffer buffer = ByteBuffer.wrap(array);
		buffer.putLong(byteCount);

		out.write(array);
	}
}