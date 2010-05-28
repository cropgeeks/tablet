// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

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
	private JButton bCancel;

	// Runnable object that will be active while the dialog is visible
	private ITrackableJob job;
	private int jobStatus = JOB_COMPLETED;

	private Timer timer;

	// A reference to any exception thrown while the job was active
	private Exception exception = null;

	public ProgressDialog(ITrackableJob job, String title, String label)
	{
		super(Tablet.winMain, title, true);

		this.job = job;

		nbPanel = new NBProgressPanel(job, label);
		new Thread(this).start();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelJob();
			}
		});

		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelJob();
			}
		});

		if (SystemUtils.isMacOS() == false)
			bCancel.setVisible(false);

		JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		cancelPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		cancelPanel.add(bCancel);

		add(nbPanel);
		add(cancelPanel, BorderLayout.SOUTH);

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

		try
		{
			nbPanel.pBar.setIndeterminate(job.isIndeterminate());
			nbPanel.pBar.setStringPainted(!job.isIndeterminate());

			int val = job.getValue();
			int max = job.getMaximum();
			nbPanel.pBar.setMaximum(max);
			nbPanel.pBar.setValue(val);

			String message = job.getMessage();
			if (message != null)
				nbPanel.msgLabel.setText(message);

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
		catch (Exception ex) {}
	}

	private void cancelJob()
	{
		job.cancelJob();
		jobStatus = JOB_CANCELLED;
	}

	// Starts the job running in its own thread
	public void run()
	{
		Thread.currentThread().setName("ProgressDialog-ITrackableJob");

		if (SystemUtils.isMacOS() == false)
			createCancelTimer();

		timer = new Timer(100, this);
		timer.start();

		try
		{
			for (int i = 0; i < job.getJobCount() && jobStatus == 0; i++)
				job.runJob(i);
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

	private void createCancelTimer()
	{
		Timer timer = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (isVisible() == false)
					return;

				Runnable r = new Runnable() {
					public void run()
					{
						for (int i = 0; i < 25; i++)
						{
							setSize(getWidth(), getHeight()+1);

							try { Thread.sleep(25); }
							catch (InterruptedException e) {}
						}

						bCancel.setVisible(true);
					}
				};

				new Thread(r).start();
			}
		});

		timer.setRepeats(false);
		timer.start();
	}
}