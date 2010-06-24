// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.sql.*;
import java.util.*;

import tablet.data.*;

public class ReadSQLCache
{
	private Connection c;
	private int id;

	// Statements for getting (g) and inserting (i) data
	private PreparedStatement ips;
	private Stack<PreparedStatement> gpsAll = new Stack<PreparedStatement>();

	public ReadSQLCache(File file)
		throws Exception
	{
		Class.forName("org.sqlite.JDBC");

		c =	DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

		Statement s = c.createStatement();

		s.execute("PRAGMA locking_mode = EXCLUSIVE;");
		s.execute("PRAGMA journal_mode = OFF;");
		s.execute("PRAGMA synchronous = OFF;");
		s.execute("PRAGMA count_changes = false;");

//		s.executeUpdate("drop table if exists reads;");
		s.executeUpdate("CREATE TABLE reads (id INTEGER PRIMARY KEY, name TEXT, unpaddedlength INTEGER, cigar TEXT, matecontig TEXT);");

		s.close();

		ips = c.prepareStatement("INSERT INTO reads (id, name, unpaddedlength, cigar, matecontig) VALUES (?, ?, ?, ?, ?);");

		for (int i = 0; i < 10; i++)
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM reads WHERE id=?;");
			gpsAll.push(ps);
		}
	}

	public void openForWriting()
		throws Exception
	{
		c.setAutoCommit(false);
	}

	public void openForReading()
		throws Exception
	{
		runInsertBatch();
		c.setAutoCommit(true);

		Statement s = c.createStatement();
		s.setFetchSize(100);
		System.out.println("Size: " + s.getFetchSize());
		s.close();
	}

	public void close()
		throws IOException
	{
		try { c.close(); }
		catch (SQLException e) {}
	}

	public ReadNameData getReadNameData(int id)
	{
		ReadNameData rnd = null;

		try
		{
			PreparedStatement ps = gpsAll.pop();
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			while (rs.next())
				rnd = new ReadNameData(rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5));

			rs.close();

			gpsAll.push(ps);

			return rnd;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void setReadNameData(ReadNameData readNameData)
		throws Exception
	{
		ips.setInt(1, id++);
		ips.setString(2, readNameData.getName());
		ips.setInt(3, readNameData.getUnpaddedLength());
		ips.setString(4, readNameData.getCigar());
		ips.setString(5, readNameData.getMateContig());

		ips.addBatch();

		if (id % 10001 == 0)
			runInsertBatch();
	}

	private void runInsertBatch()
		throws SQLException
	{
		ips.executeBatch();
	}
}