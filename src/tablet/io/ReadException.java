// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import scri.commons.gui.*;

public class ReadException extends Exception
{
	/** Thrown when attempting to parse a file that cannot be understood. */
	public static final int UNKNOWN_ERROR = 0;
	public static final int UNKNOWN_FORMAT = 10;
	public static final int TOKEN_COUNT_WRONG = 20;

	private int error = UNKNOWN_ERROR;
	private int lineNumber;

	ReadException(String message, int lineNumber)
	{
		super(message);
		this.lineNumber = lineNumber;
	}

	ReadException(int error, int lineNumber)
	{
		this.error = error;
		this.lineNumber = lineNumber;
	}

	public int getError()
		{ return error; }

	public int getLineNumber()
		{ return lineNumber; }

	public String toString()
	{
		switch (error)
		{
			case UNKNOWN_ERROR: return RB.format(
				"io.ReadException.UNKNOWN_ERROR", lineNumber, getMessage());

			case UNKNOWN_FORMAT: return RB.format(
				"io.ReadException.UNKNOWN_FORMAT", lineNumber);

			case TOKEN_COUNT_WRONG:	return RB.format(
				"io.ReadException.TOKEN_COUNT_WRONG", lineNumber);
		}

		return "tablet.io.ReadException";
	}
}