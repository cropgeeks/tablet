// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private PreparedStatement psGetName;
	private PreparedStatement psGetReadsByName;

	private File file;

	boolean first = true;

	public ReadSQLCache(File file)
		throws Exception
	{
		this.file = file;
		Class.forName("org.sqlite.JDBC");

		c =	DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

		Statement s = c.createStatement();

		// Set up the sqlite modes
		s.execute("PRAGMA locking_mode = EXCLUSIVE;");
		s.execute("PRAGMA journal_mode = OFF;");
		s.execute("PRAGMA synchronous = OFF;");
		s.execute("PRAGMA count_changes = false;");

		// Create the database table
		s.executeUpdate("CREATE TABLE reads (id INTEGER PRIMARY KEY, name TEXT, name_postfix TEXT, unpaddedlength INTEGER, cigar TEXT, matecontig TEXT, insertsize INTEGER, isproperpair INTEGER, numberinpair INTEGER);");

		s.close();

		// Create the prepared statement for inserting into the database
		ips = c.prepareStatement("INSERT INTO reads (id, name, name_postfix, unpaddedlength, cigar, matecontig, insertsize, isproperpair, numberinpair) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

		// Create prepared statements for reading from the database
		for (int i = 0; i < 10; i++)
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM reads WHERE id=?;");
			gpsAll.push(ps);
		}

		psGetName = c.prepareStatement("SELECT name FROM reads WHERE id=?");

		psGetReadsByName = c.prepareStatement("SELECT id FROM reads WHERE name=?;");
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
		s.close();
	}

	public void indexNames()
		throws Exception
	{
		Statement s = c.createStatement();
		s.executeUpdate("CREATE INDEX names ON reads(name);");
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
				rnd = new ReadNameData(rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getBoolean(8), rs.getInt(9));

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

	public String getReadName(int id)
	{
		String name = null;

		try
		{
			psGetName.setInt(1, id);

			ResultSet rs = psGetName.executeQuery();
			while(rs.next())
				name = rs.getString(1);

			rs.close();
			return name;
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
		ips.setString(2, readNameData.getNamePrefix());
		ips.setString(3, readNameData.getNamePostfix());
		ips.setInt(4, readNameData.getUnpaddedLength());
		ips.setString(5, readNameData.getCigar());
		ips.setString(6, readNameData.getMateContig());
		ips.setInt(7, readNameData.getInsertSize());
		ips.setBoolean(8, readNameData.isProperPair());
		ips.setInt(9, readNameData.getNumberInPair());

		ips.addBatch();

		if (id % 10001 == 0)
			runInsertBatch();
	}

	private void runInsertBatch()
		throws SQLException
	{
		ips.executeBatch();
	}

	public ReadSQLCache resetCache()
		throws IOException, Exception
	{
		// Deal with situations where errors have interrupted the connection
		c.close();
		c =	DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

		Statement s = c.createStatement();
		s.execute("drop table if exists reads;");
		s.close();
		c.close();
		return new ReadSQLCache(file);
	}

	public ArrayList<Integer> getReadsByName(String name)
		throws SQLException
	{
		ArrayList<Integer> reads = new ArrayList<Integer>();

		psGetReadsByName.setString(1, name);

		ResultSet rs = psGetReadsByName.executeQuery();

		while (rs.next())
			reads.add(rs.getInt(1));

		rs.close();

		return reads;
	}
}