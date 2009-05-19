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
{
	private static DecimalFormat d = new DecimalFormat("0.00");

	private NBProgressPanel nbPanel;

	// Runnable object that will be active while the dialog is visible
	private ITrackableJob job;
	private boolean jobOK = true;

	// And the messages it will need for each job part
	private String[] msgs;

	// A reference to any exception thrown while the job was active
	private Exception exception = null;

	public ProgressDialog(final ITrackableJob job, String title, String label, String[] msgs)
	{
		super(Tablet.winMain, "", true);

		this.job = job;
		this.msgs = msgs;

		nbPanel = new NBProgressPanel(job, label);
		add(nbPanel);

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)	{
				runJob();
			}
			public void windowClosing(WindowEvent e)
			{
				job.cancelJob();
				jobOK = false;
			}
		});

		pack();
		setTitle(title);
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}

	// Starts the job running in its own thread, catching any exceptions that
	// may occur as it runs.
	private void runJob()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					for (int i = 0; i < job.getJobCount(); i++)
					{
						nbPanel.msgLabel.setText(msgs[i]);
						job.runJob(i);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();

					exception = e;
					jobOK = false;
				}

				setVisible(false);
			}
		};

		new MonitorThread().start();
		new Thread(r).start();
	}

	public boolean jobOK()
		{ return jobOK; }

	public Exception getException()
		{ return exception; }

	// Simple monitor thread that tracks the progress of the job, updating the
	// progress bar as it goes
	private class MonitorThread extends Thread
	{
		public void run()
		{
			Runnable r = new Runnable() {
				public void run()
				{
					nbPanel.pBar.setIndeterminate(job.isIndeterminate());

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
			};

			while (isVisible())
			{
				SwingUtilities.invokeLater(r);

				try { Thread.sleep(100); }
				catch (InterruptedException e) {}
			}
		}
	}
}