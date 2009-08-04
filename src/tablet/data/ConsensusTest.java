/*package tablet.data;

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
		c.calculatePaddedMappings();

		assertEquals(-1, c.getUnpaddedPosition(0));
		assertEquals(0, c.getUnpaddedPosition(1));
		assertEquals(1, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(2, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("**A**");
		c.calculatePaddedMappings();

		assertEquals(-1, c.getUnpaddedPosition(0));
		assertEquals(-1, c.getUnpaddedPosition(1));
		assertEquals(0, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(-1, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("AC*TG");
		c.calculatePaddedMappings();

		assertEquals(0, c.getUnpaddedPosition(0));
		assertEquals(1, c.getUnpaddedPosition(1));
		assertEquals(-1, c.getUnpaddedPosition(2));
		assertEquals(2, c.getUnpaddedPosition(3));
		assertEquals(3, c.getUnpaddedPosition(4));


		c = new Consensus();
		c.setData("ACT**");
		c.calculatePaddedMappings();

		assertEquals(0, c.getUnpaddedPosition(0));
		assertEquals(1, c.getUnpaddedPosition(1));
		assertEquals(2, c.getUnpaddedPosition(2));
		assertEquals(-1, c.getUnpaddedPosition(3));
		assertEquals(-1, c.getUnpaddedPosition(4));
	}

	// Tests the mapping between an unpadded value and where it actually lies
	// within the original data array
	public void testCalculatingPaddedPositions()
		throws Exception
	{
		Consensus c = null;

		c = new Consensus();
		c.setData("*AC*T");
		c.calculatePaddedMappings();

		assertEquals(1, c.getPaddedPosition(0));
		assertEquals(2, c.getPaddedPosition(1));
		assertEquals(4, c.getPaddedPosition(2));
		assertEquals(-1, c.getPaddedPosition(3));
		assertEquals(-1, c.getPaddedPosition(4));


		c = new Consensus();
		c.setData("*****");
		c.calculatePaddedMappings();

		assertEquals(-1, c.getPaddedPosition(0));
		assertEquals(-1, c.getPaddedPosition(1));
		assertEquals(-1, c.getPaddedPosition(2));
		assertEquals(-1, c.getPaddedPosition(3));
		assertEquals(-1, c.getPaddedPosition(4));


		c = new Consensus();
		c.setData("AGTCA");
		c.calculatePaddedMappings();

		assertEquals(0, c.getPaddedPosition(0));
		assertEquals(1, c.getPaddedPosition(1));
		assertEquals(2, c.getPaddedPosition(2));
		assertEquals(3, c.getPaddedPosition(3));
		assertEquals(4, c.getPaddedPosition(4));


		c = new Consensus();
		c.setData("A*TCA");
		c.calculatePaddedMappings();

		assertEquals(0, c.getPaddedPosition(0));
		assertEquals(2, c.getPaddedPosition(1));
		assertEquals(3, c.getPaddedPosition(2));
		assertEquals(4, c.getPaddedPosition(3));
		assertEquals(-1, c.getPaddedPosition(4));
	}
}
*/