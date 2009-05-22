package tablet.io;

public class ReadException extends Exception
{
	/** Thrown when attempting to parse a file that cannot be understood. */
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