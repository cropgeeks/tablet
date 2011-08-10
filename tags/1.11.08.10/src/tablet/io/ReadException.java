// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import scri.commons.gui.*;

public class ReadException extends Exception
{
	/** Thrown when attempting to parse a file that cannot be understood. */
	public static final int UNKNOWN_FORMAT = 10;
	public static final int TOKEN_COUNT_WRONG = 20;

	private int lineNumber;
	private AssemblyFile file;
	private String message;

	ReadException(AssemblyFile file, int lineNumber, Exception exception)
	{
		this.file = file;
		this.lineNumber = lineNumber;
		this.message =  RB.format(
			"io.ReadException.UNKNOWN_ERROR", lineNumber, file.getName(), exception.toString());
	}

	ReadException(AssemblyFile file, int lineNumber, int error)
	{
		this.file = file;
		this.lineNumber = lineNumber;

		message = formatMessage(error);
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	public String formatMessage(int error)
	{
		switch (error)
		{
			case UNKNOWN_FORMAT: return RB.getString(
				"io.ReadException.UNKNOWN_FORMAT");

			case TOKEN_COUNT_WRONG:	return RB.format(
				"io.ReadException.TOKEN_COUNT_WRONG", lineNumber, file.getName());
		}

		return "tablet.io.ReadException";
	}
}