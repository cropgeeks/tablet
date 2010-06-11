// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.scanner;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import tablet.gui.*;
import tablet.io.*;

import scri.commons.gui.*;

public class ScannerFrame extends JFrame implements ActionListener
{
	private NBScannerPanel nbPanel;

	private File targetFile;
	private JTable table;
	private ResultsTableModel model;

	private ScanAnalysis scanner;

	public ScannerFrame()
	{
		nbPanel = new NBScannerPanel(this);

		add(nbPanel);
		pack();

		setTitle("Tablet Assembly Scanner");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == nbPanel.bScan)
		{
			if (scanner == null)
				scan();
			else
				scanner.cancelJob();
		}
	}

	void scan()
	{
		model = new ResultsTableModel();
		nbPanel.table.setModel(model);
		nbPanel.bScan.setText("Cancel");

		targetFile = new File(nbPanel.fileCombo.getText());
		Prefs.guiScannerRecent = nbPanel.fileCombo.getHistory();

		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					scanner = new ScanAnalysis(targetFile, model);

					Timer timer = new Timer(100, new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							nbPanel.scanLabel.setText(
								RB.format("gui.scanner.NBScannerPanel.scanLabel2",
								scanner.getScanCount(),
								scanner.getAssemblyCount(),
								scanner.getMessage()));
						}
					});

					timer.start();
					scanner.runJob(0);
					timer.stop();

					nbPanel.scanLabel.setText(
						RB.format("gui.scanner.NBScannerPanel.scanLabel1",
						scanner.getScanCount(), scanner.getAssemblyCount()));

					scanner = null;
					nbPanel.bScan.setText("Run scan");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		new Thread(r).start();

		setTitle("Tablet Assembly Scanner - " + targetFile);
	}
}