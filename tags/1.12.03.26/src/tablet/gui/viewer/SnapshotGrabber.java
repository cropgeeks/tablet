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

		int colIndex = rCanvas.contig.getVisualStart() + (rCanvas.pX1 / rCanvas.ntW);
		int rowIndex = rCanvas.pY1 / rCanvas.ntH;

		Snapshot snapshot = new Snapshot(colIndex, rowIndex, Prefs.visReadsCanvasZoom);

		controller.addSnapshot(snapshot);
	}

	public class Snapshot
	{
		private int x;
		private int y;
		private int zoom;

		Snapshot(int x, int y, int zoom)
		{
			this.x = x;
			this.y = y;
			this.zoom = zoom;
		}

		public int getX()
			{ return x; }

		public int getY()
			{ return y; }

		public int getZoom()
			{ return zoom; }

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;

			if (other instanceof Snapshot == false)
				return false;

			Snapshot o = (Snapshot)other;

			return this.x == o.x && this.y == o.y && this.zoom == o.zoom;
		}

		public String toString()
		{
			return "x: " + x + " zoom: " + zoom;
		}
	}
}
