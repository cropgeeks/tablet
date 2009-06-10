package tablet.data;

import junit.framework.*;

public class ConsensusTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.data.ConsensusTest");
	}

	public void testCalculatingUnpaddedPositions()
		throws Exception
	{
		Consensus c = null;

		c = new Consensus();
		c.setData("*AC*T");
		c.calculateUnpaddedIndices();

		assertEquals(-1, c.getUnpaddedPosition(0));
		assertEquals(0, c.getUnpaddedPosition(1));
		assertEquals(1, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(2, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("**A**");
		c.calculateUnpaddedIndices();

		assertEquals(-1, c.getUnpaddedPosition(0));
		assertEquals(-1, c.getUnpaddedPosition(1));
		assertEquals(0, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(-1, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("AC*TG");
		c.calculateUnpaddedIndices();

		assertEquals(0, c.getUnpaddedPosition(0));
		assertEquals(1, c.getUnpaddedPosition(1));
		assertEquals(-1, c.getUnpaddedPosition(2));
		assertEquals(2, c.getUnpaddedPosition(3));
		assertEquals(3, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("ACT**");
		c.calculateUnpaddedIndices();

		assertEquals(0, c.getUnpaddedPosition(0));
		assertEquals(1, c.getUnpaddedPosition(1));
		assertEquals(2, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(-1, c.getUnpaddedPosition(4));
	}
}