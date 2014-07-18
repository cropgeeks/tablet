// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog.prefs;

import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

class GeneralTabNB extends JPanel implements ActionListener
{
	private DefaultComboBoxModel<String> displayModel;
	private DefaultComboBoxModel<String> updateModel;

    public GeneralTabNB()
    {
        initComponents();

        TabletUtils.setPanelColor(this, false);
        TabletUtils.setPanelColor(generalPanel, false);
        TabletUtils.setPanelColor(cachePanel, false);

		// Interface settings
		generalPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBGeneralPanel.generalPanelTitle")));
		RB.setText(displayLabel, "gui.dialog.prefs.NBGeneralPanel.displayLabel");

        displayModel = new DefaultComboBoxModel<String>();
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.auto"));
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.en_GB"));
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.en_US"));
        displayCombo.setModel(displayModel);
		displayCombo.setSelectedIndex(getLocaleIndex());

        // Update settings
        RB.setText(updateLabel, "gui.dialog.prefs.NBGeneralPanel.updateLabel");

        updateModel = new DefaultComboBoxModel<String>();
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateNever"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateStartup"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateDaily"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateWeekly"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateMonthly"));
        updateCombo.setModel(updateModel);
        updateCombo.setSelectedIndex(Prefs.guiUpdateSchedule);

        // Cache settings
        cachePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBGeneralPanel.cachePanelTitle")));
        RB.setText(cacheLabel, "gui.dialog.prefs.NBGeneralPanel.cacheLabel");
        RB.setText(bBrowse, "gui.text.browse");

		cacheSizeLabel.setText(RB.format("gui.dialog.prefs.NBGeneralPanel.cacheSizeLabel", getCacheDirSize()));
		RB.setText(lblClearCache, "gui.dialog.prefs.NBGeneralPanel.lblClearCache");

        cacheField.setText(Prefs.cacheFolder);
        bBrowse.addActionListener(this);

		lblClearCache.addActionListener(this);
	}

    private int getLocaleIndex()
	{
		if (Prefs.localeText.equals("en_GB"))
			return 1;
		else if (Prefs.localeText.equals("en_US"))
			return 2;
		else
			return 0;
	}

	public void applySettings()
	{
		switch (displayCombo.getSelectedIndex())
		{
			case 1:  Prefs.localeText = "en_GB"; break;
			case 2:  Prefs.localeText = "en_US"; break;
			default: Prefs.localeText = "auto";
		}

		Prefs.guiUpdateSchedule = updateCombo.getSelectedIndex();

		if (cacheField.getText().length() > 0)
			Prefs.cacheFolder = cacheField.getText();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bBrowse)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("");
			fc.setCurrentDirectory(new File(cacheField.getText()));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (fc.showOpenDialog(Tablet.winMain) == JFileChooser.APPROVE_OPTION)
			{
				if (fc.getSelectedFile().listFiles().length > 0)
				{
					String[] options = new String[] {
						RB.getString("gui.text.yes"),
						RB.getString("gui.text.no") };

					int response = TaskDialog.show(
						RB.getString("gui.dialog.prefs.NBGeneralPanel.cachePrompt"),
						TaskDialog.WAR, 1, options);

					if (response != 0)
						return;
				}

				cacheField.setText(fc.getSelectedFile().getPath());
			}
		}

		else if (e.getSource() == lblClearCache)
			clearCache();
	}

	private void clearCache()
	{
		String[] options = new String[] {
			RB.getString("gui.text.ok"),
			RB.getString("gui.text.cancel")
		};

		// Warn the user about the consequences of emptying Tablet's cache
		int result = TaskDialog.show(RB.getString("gui.dialog.prefs.NBGeneralPanel.warning"),
			TaskDialog.WAR, 0, options);

		// If the user goes through with emptying the cache, empty it and update
		// the displayed cache size
		if (result == 0)
		{
			emptyCacheDir();
			cacheSizeLabel.setText(RB.format("gui.dialog.prefs.NBGeneralPanel.cacheSizeLabel", getCacheDirSize()));
		}
	}

	private String getCacheDirSize()
	{
		try {
			String cacheFolder = Prefs.cacheFolder;
			File folder = new File(cacheFolder);
			float size = 0;
			for (File file : folder.listFiles())
				size += file.length();

			String sizeString = getStringForSize(size);

			return sizeString;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return "0.00 MB";
		}
	}

	private String getStringForSize(float size)
	{
		float KB = 1024;
		float MB = 1048576;
		float GB = 1073741824;

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		String sizeString;
		if (size < KB)
			sizeString = (int)size + " B";
		else if (size < MB)
			sizeString = (int)(size / KB) + " KB";
		else if (size < GB)
			sizeString = nf.format(size / MB) + " MB";
		else
			sizeString = nf.format(size / GB) + "GB";

		return sizeString;
	}

	private void emptyCacheDir()
	{
		Assembly assembly = Tablet.winMain.getAssemblyPanel().getAssembly();

		try
		{
			File dir = new File(Prefs.cacheFolder);
			File[] files = dir.listFiles();

			for (File f: files)
			{
				if (assembly == null || f.getAbsolutePath().contains(assembly.getCacheID()) == false)
					f.delete();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        generalPanel = new javax.swing.JPanel();
        displayCombo = new javax.swing.JComboBox<String>();
        updateCombo = new javax.swing.JComboBox<String>();
        displayLabel = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();
        cachePanel = new javax.swing.JPanel();
        cacheLabel = new javax.swing.JLabel();
        cacheField = new javax.swing.JTextField();
        bBrowse = new javax.swing.JButton();
        cacheSizeLabel = new javax.swing.JLabel();
        lblOpenB = new javax.swing.JLabel();
        lblClearCache = new scri.commons.gui.matisse.HyperLinkLabel();
        lblCloseB = new javax.swing.JLabel();

        generalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General options (restart to apply):"));

        displayLabel.setLabelFor(displayCombo);
        displayLabel.setText("Interface display language:");

        updateLabel.setLabelFor(updateCombo);
        updateLabel.setText("Check for newer Tablet versions:");

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayLabel)
                    .addComponent(updateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayCombo, 0, 217, Short.MAX_VALUE)
                    .addComponent(updateCombo, 0, 217, Short.MAX_VALUE))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(displayLabel)
                    .addComponent(displayCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateLabel)
                    .addComponent(updateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cachePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Cache options:"));

        cacheLabel.setLabelFor(cacheField);
        cacheLabel.setText("Tablet requires a local directory for storing temporary files:");

        bBrowse.setText("Browse...");

        cacheSizeLabel.setText("Cache size: {0} MB");

        lblOpenB.setText("(");

        lblClearCache.setText("Clear cache");

        lblCloseB.setText(")");

        javax.swing.GroupLayout cachePanelLayout = new javax.swing.GroupLayout(cachePanel);
        cachePanel.setLayout(cachePanelLayout);
        cachePanelLayout.setHorizontalGroup(
            cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cachePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cacheField)
                    .addGroup(cachePanelLayout.createSequentialGroup()
                        .addGroup(cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cacheLabel)
                            .addGroup(cachePanelLayout.createSequentialGroup()
                                .addComponent(cacheSizeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblOpenB)
                                .addGap(1, 1, 1)
                                .addComponent(lblClearCache, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addComponent(lblCloseB)))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bBrowse)
                .addContainerGap())
        );
        cachePanelLayout.setVerticalGroup(
            cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cachePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cacheLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cacheField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cachePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cacheSizeLabel)
                    .addComponent(lblOpenB)
                    .addComponent(lblClearCache, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCloseB))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cachePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generalPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cachePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.JTextField cacheField;
    private javax.swing.JLabel cacheLabel;
    private javax.swing.JPanel cachePanel;
    private javax.swing.JLabel cacheSizeLabel;
    private javax.swing.JComboBox<String> displayCombo;
    private javax.swing.JLabel displayLabel;
    private javax.swing.JPanel generalPanel;
    private scri.commons.gui.matisse.HyperLinkLabel lblClearCache;
    private javax.swing.JLabel lblCloseB;
    private javax.swing.JLabel lblOpenB;
    private javax.swing.JComboBox<String> updateCombo;
    private javax.swing.JLabel updateLabel;
    // End of variables declaration//GEN-END:variables

}