// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog.prefs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

public class PreferencesDialog extends JDialog implements ActionListener
{
	private static int lastTab = 0;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK;

	private JTabbedPane tabs;
	private NBGeneralPanel generalPanel;
	private NBVisualizationPanel visualizationPanel;
	private NBWarningPanel warningPanel;

	public PreferencesDialog(Integer newTab)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.prefs.PreferencesDialog.title"),
			true
		);

		if (newTab != null)
			lastTab = newTab;

		generalPanel = new NBGeneralPanel();
		visualizationPanel = new NBVisualizationPanel();
		warningPanel = new NBWarningPanel();

		tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.generalTab"),
			Icons.getIcon("GENERALTAB"), generalPanel);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.visualizationTab"),
			Icons.getIcon("VISUALIZATIONTAB"), visualizationPanel);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.warningTab"),
			Icons.getIcon("WARNINGSTAB"), warningPanel);
		tabs.setSelectedIndex(lastTab);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		TabletUtils.setHelp(bHelp, "gui.dialog.prefs.PreferencesDialog");

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			generalPanel.applySettings();
			visualizationPanel.applySettings();
			warningPanel.applySettings();

			lastTab = tabs.getSelectedIndex();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }
}