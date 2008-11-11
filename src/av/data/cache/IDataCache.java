package av.data.cache;

public interface IDataCache
{
	public String getName(int id);

	public int setName(String name)
		throws Exception;

	public void close()
		throws Exception;
}