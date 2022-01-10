// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.util.*;

import tablet.gui.*;
import tablet.gui.ribbon.*;
import tablet.gui.viewer.SnapshotGrabber.*;

public class SnapshotController
{
	private AssemblyPanel aPanel;
	private ArrayList<Snapshot> snaps = new ArrayList<>();
	private int snapPtr = -1;

	public SnapshotController(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
	}

	public ArrayList<Snapshot> getSnapshots()
		{ return snaps; }

	public boolean canAddSnapshot(Snapshot snapshot)
	{
		return snaps.isEmpty() ||
			(snaps.get(snaps.size()-1).equals(snapshot) == false
			&& snaps.get(snapPtr).equals(snapshot) == false);
	}

	public void addSnapshot(Snapshot snapshot)
	{
		if (canAddSnapshot(snapshot))
		{
			// Remove snapshots which are beyond the current add point
			while (snaps.size()-1 > snapPtr)
				snaps.remove(snaps.size()-1);

			// Add the snapshot, increment pointer to point to the new snapshot
			snaps.add(snapshot);
			snapPtr++;

			updateIcons();
		}
	}

	public void previousSnapshot()
	{
		if (hasPrevSnapshot())
			moveToSnapshot(--snapPtr);
	}

	public void nextSnapshot()
	{
		if (hasNextSnapshot())
			moveToSnapshot(++snapPtr);
	}

	private void moveToSnapshot(int snapPtr)
	{
		Snapshot snapshot = snaps.get(snapPtr);

		BandAdjust.setZoom(snapshot.getZoom());
		aPanel.moveToPosition(snapshot.getY(), snapshot.getX(), false);

		updateIcons();
	}

	public boolean hasPrevSnapshot()
	{
		return (snaps.isEmpty() || snapPtr <= 0) == false;
	}

	public boolean hasNextSnapshot()
	{
		return (snaps.isEmpty() || snapPtr == snaps.size()-1) == false;
	}

	public void updateIcons()
	{
		boolean hasPrev = hasPrevSnapshot();
		boolean hasNext = hasNextSnapshot();

		// Must set rollover to false before setting enabled state, this
		// prevents a graphical glitch
		if (!hasPrev)
			Actions.navigatePrevView.setRollover(false);

		if (!hasNext)
			Actions.navigateNextView.setRollover(false);

		Actions.navigatePrevView.setEnabled(hasPrev);
		Actions.navigateNextView.setEnabled(hasNext);
	}

	public void reset()
	{
		snaps = new ArrayList<Snapshot>();
		snapPtr = -1;
		updateIcons();
	}
}