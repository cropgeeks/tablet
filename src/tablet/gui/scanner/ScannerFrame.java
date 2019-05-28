// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.scanner;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.gui.*;
import tablet.io.*;

import scri.commons.gui.*;

public class ScannerFrame extends JFrame implements ActionListener
{
	private ScannerPanelNB nbPanel;

	private File targetFile;
	private JTable table;
	private ResultsTableModel model;
	private TableRowSorter<ResultsTableModel> sorter;

	private ScanAnalysis scanner;

	public ScannerFrame()
	{
		nbPanel = new ScannerPanelNB(this);

		model = new ResultsTableModel();
		sorter = new TableRowSorter<ResultsTableModel>(model);
		nbPanel.table.setModel(model);
		nbPanel.table.setRowSorter(sorter);

		sorter.toggleSortOrder(1);
		sorter.toggleSortOrder(1);

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
		while (model.getRowCount() > 0)
			model.removeRow(0);

		targetFile = new File(nbPanel.fileCombo.getText());
		Prefs.guiScannerRecent = nbPanel.fileCombo.getHistory();

		Runnable r = new Runnable() {
			public void run()
			{
				long s = System.currentTimeMillis();

				nbPanel.bScan.setText("Cancel");
				nbPanel.bScan.setIcon(Icons.getIcon("TIMERON"));

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

				try { scanner.runJob(0); }
				catch (Exception e)
				{
					e.printStackTrace();
				}

				timer.stop();

				nbPanel.scanLabel.setText(
					RB.format("gui.scanner.NBScannerPanel.scanLabel1",
					scanner.getScanCount(), scanner.getAssemblyCount()));

				scanner = null;

				nbPanel.bScan.setText("Run scan");
				nbPanel.bScan.setIcon(Icons.getIcon("TIMEROFF"));

				long e = System.currentTimeMillis();
				System.out.println("Scan time: " + ((e-s)/1000f) + "s");
			}
		};

		new Thread(r).start();

		setTitle("Tablet Assembly Scanner - " + targetFile);
	}
}