package tablet.data.cache;

import tablet.data.*;

public interface IReadCache
{
	public ReadMetaData getReadMetaData(int id);

	public int setReadMetaData(ReadMetaData readMetaData)
		throws Exception;

	public void close()
		throws Exception;
}