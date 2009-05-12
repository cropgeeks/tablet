package tablet.io;

public class ReadException extends Exception
{
	public static final int UNKNOWN_FORMAT = 10;


	private int error;

	ReadException(int error)
	{
		this.error = error;
	}

	public int getError()
	{
		return error;
	}
}