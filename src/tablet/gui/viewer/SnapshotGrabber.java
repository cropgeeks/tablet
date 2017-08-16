// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import tablet.gui.Prefs;

public class SnapshotGrabber extends Thread
{
	private static SnapshotGrabber previous;

	private Boolean killMe = false;
	private SnapshotController controller;
	private ReadsCanvas rCanvas;

	// SnapshotGrabber is created on every buffer creation of the main canvas
	// A snapshot is only taken if the display remains still for a user selectable
	// period of time
	public SnapshotGrabber(ReadsCanvas rCanvas, SnapshotController controller)
	{
		this.rCanvas = rCanvas;
		this.controller = controller;

		// Cancel any previous instances of SnapshotGrabber
		if (previous != null)
		{
			previous.killMe = true;
			previous.interrupt();
		}

		previous = this;

		this.setName("SnapshotGrabber");
		setPriority(Thread.MIN_PRIORITY);

		start();
	}

	public void run()
	{
		try
		{
			// Wait for a user specified period of time before taking a snapshot
			Thread.sleep(Prefs.snapshotDelay);
		}
		catch (InterruptedException e) {}

		if (killMe)
			return;

		int colIndex = rCanvas.contig.getVisualStart() + (int)(rCanvas.pX1 / rCanvas.ntW);
		int rowIndex = rCanvas.pY1 / rCanvas.ntH;

		Snapshot snapshot = new Snapshot(colIndex, rowIndex, Prefs.visReadsZoomLevel, Prefs.visPacked, Prefs.visPaired);

		controller.addSnapshot(snapshot);
	}

	/**
	 * A snapshot captures all of the information required to recreate a view
	 * of data within the current Contig. As such it stores the x and y
	 * coordinates of the display, the zoom level and current packing mode (due
	 * to historical reasons two booleans indicating if the view is packed and
	 * if the view is paired).
	 */
	public class Snapshot
	{
		private int x;
		private int y;
		private int zoom;
		private boolean packed;
		private boolean paired;

		Snapshot(int x, int y, int zoom, boolean packed, boolean paired)
		{
			this.x = x;
			this.y = y;
			this.zoom = zoom;
			this.packed = packed;
			this.paired = paired;
		}

		public int getX()
			{ return x; }

		public int getY()
			{ return y; }

		public int getZoom()
			{ return zoom; }

		public boolean getPacked()
			{ return packed; }

		public boolean getPaired()
			{ return paired; }

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;

			if (other instanceof Snapshot == false)
				return false;

			Snapshot o = (Snapshot)other;

			return this.x == o.x && this.y == o.y && this.zoom == o.zoom && this.packed == o.packed && this.paired == o.paired;
		}

		@Override
		public String toString()
		{
			return "x: " + x + " zoom: " + zoom;
		}
	}
}