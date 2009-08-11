package tablet.gui.dialog;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;

import tablet.gui.*;

/**
 * Common class used by most of the trackable job types as they run to display
 * a dialog with a tracking progress bar.
 */
public class ProgressDialog extends JDialog
	implements Runnable, ActionListener
{
	public static final int JOB_COMPLETED = 0;
	public static final int JOB_CANCELLED = 1;
	public static final int JOB_FAILED = 2;

	private static DecimalFormat d = new DecimalFormat("0.00");

	private NBProgressPanel nbPanel;

	// Runnable object that will be active while the dialog is visible
	private ITrackableJob job;
	private int jobStatus = JOB_COMPLETED;

	private Timer timer;

	// And the messages it will need for each job part
	private String[] msgs;

	// A reference to any exception thrown while the job was active
	private Exception exception = null;

	public ProgressDialog(ITrackableJob job, String title, String label, String[] msgs)
	{
		super(Tablet.winMain, title, true);

		this.job = job;
		this.msgs = msgs;

		nbPanel = new NBProgressPanel(job, label);
		new Thread(this).start();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelJob();
			}
		});

		add(nbPanel);
		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);

		// Only show the dialog if the job hasn't finished yet
		if (this.job != null)
			setVisible(true);
	}

	public int getResult()
		{ return jobStatus; }

	public Exception getException()
		{ return exception; }

	// Called every 100ms to update the status of the progress bar
	public void actionPerformed(ActionEvent e)
	{
		// The job will be null when it's finished/failed or was cancelled
		if (job == null)
		{
			timer.stop();
			setVisible(false);
			return;
		}

		nbPanel.pBar.setIndeterminate(job.isIndeterminate());
		nbPanel.pBar.setStringPainted(!job.isIndeterminate());

		int val = job.getValue();
		int max = job.getMaximum();
		nbPanel.pBar.setMaximum(max);
		nbPanel.pBar.setValue(val);

		// If the job doesn't know its maximum (yet), this would
		// have caused a 0 divided by 0 which isn't pretty
		if (max == 0)
			nbPanel.pBar.setString(d.format(0) + "%");
		else
		{
			float value = ((float) val / (float) max) * 100;
			nbPanel.pBar.setString(d.format(value) + "%");
		}
	}

	private void startJob()
		{ new Thread(this).start(); }

	private void cancelJob()
	{
		job.cancelJob();
		jobStatus = JOB_CANCELLED;
	}

	// Starts the job running in its own thread
	public void run()
	{
		Thread.currentThread().setName("ProgressDialog-ITrackableJob");

		timer = new Timer(100, this);
		timer.start();

		try
		{
			for (int i = 0; i < job.getJobCount() && jobStatus == 0; i++)
			{
				nbPanel.msgLabel.setText(msgs[i]);
				job.runJob(i);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			exception = e;
			jobStatus = JOB_FAILED;
		}

		// Remove all references to the job once completed, because this window
		// never seems to get garbage-collected meaning its references (which
		// include a reference to the assembly) never die
		job = null;
	}
}