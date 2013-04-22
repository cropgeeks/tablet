// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog.prefs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

class FormatsTabNB extends JPanel implements ActionListener
{
	public FormatsTabNB()
    {
        initComponents();

        TabletUtils.setPanelColor(this, false);
        TabletUtils.setPanelColor(panel1, false);
        TabletUtils.setPanelColor(panel2, false);

		panel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBFormatsPanel.panel1Title")));
		RB.setText(checkAmbiguousToN, "gui.dialog.prefs.NBFormatsPanel.checkAmbiguousToN");
		RB.setText(checkAceQA, "gui.dialog.prefs.NBFormatsPanel.checkAceQA");
		RB.setText(checkBamValidation, "gui.dialog.prefs.NBFormatsPanel.checkBamValidation");
		RB.setText(cacheReads, "gui.dialog.prefs.NBFormatsPanel.cacheReads");
		RB.setText(neverCacheBAM, "gui.dialog.prefs.NBFormatsPanel.neverCacheBAM");

		panel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBFormatsPanel.panel2Title")));
		RB.setText(stLabel1, "gui.dialog.prefs.NBFormatsPanel.stLabel1");
		RB.setText(stLabel2, "gui.dialog.prefs.NBFormatsPanel.stLabel2");
		RB.setText(bBrowse, "gui.dialog.prefs.NBFormatsPanel.bBrowse");
		stPath.setText(Prefs.ioSamtoolsPath);

		checkAmbiguousToN.setSelected(Prefs.ioAmbiguousToN);
		checkAceQA.setSelected(Prefs.ioAceProcessQA);
		checkBamValidation.setSelected(Prefs.ioBamValidationIsLenient);
		cacheReads.setSelected(Prefs.ioCacheReads);
		neverCacheBAM.setSelected(Prefs.ioNeverCacheBAM);
		checkStates();

		bBrowse.addActionListener(this);
		cacheReads.addActionListener(this);
    }

	private void checkStates()
	{
		neverCacheBAM.setEnabled(cacheReads.isSelected());
	}

	public void applySettings()
	{
		Prefs.ioAmbiguousToN = checkAmbiguousToN.isSelected();
		Prefs.ioAceProcessQA = checkAceQA.isSelected();
		Prefs.ioBamValidationIsLenient = checkBamValidation.isSelected();
		Prefs.ioCacheReads = cacheReads.isSelected();
		Prefs.ioNeverCacheBAM = neverCacheBAM.isSelected();

		Prefs.ioSamtoolsPath = stPath.getText();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bBrowse)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("");
			fc.setSelectedFile(new File(stPath.getText()));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (fc.showOpenDialog(Tablet.winMain) == JFileChooser.APPROVE_OPTION)
				stPath.setText(fc.getSelectedFile().getPath());
		}

		else if (e.getSource() == cacheReads)
			checkStates();
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        checkAmbiguousToN = new javax.swing.JCheckBox();
        checkAceQA = new javax.swing.JCheckBox();
        checkBamValidation = new javax.swing.JCheckBox();
        cacheReads = new javax.swing.JCheckBox();
        neverCacheBAM = new javax.swing.JCheckBox();
        panel2 = new javax.swing.JPanel();
        stLabel1 = new javax.swing.JLabel();
        stPath = new javax.swing.JTextField();
        bBrowse = new javax.swing.JButton();
        stLabel2 = new javax.swing.JLabel();

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Assembly import options:"));

        checkAmbiguousToN.setText("Convert all DNA ambuiguity codes to 'N', rather than treating as unknowns");

        checkAceQA.setText("Trim poor quality reads using QA tags (ACE only)");

        checkBamValidation.setText("Set BAM validation stringency to lenient rather than strict (BAM only)");

        cacheReads.setText("Always cache read data to disk while importing");

        neverCacheBAM.setText("Bypass read disk caching for BAM files");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkAmbiguousToN)
                    .addComponent(checkAceQA)
                    .addComponent(checkBamValidation)
                    .addComponent(cacheReads)
                    .addComponent(neverCacheBAM))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkAmbiguousToN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAceQA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBamValidation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cacheReads)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neverCacheBAM)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Samtools:"));

        stLabel1.setLabelFor(stPath);
        stLabel1.setText("Alternative path to samtools:");

        bBrowse.setText("Browse...");

        stLabel2.setText("(Leave blank to allow Tablet to try to use its own copy)");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addComponent(stPath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bBrowse))
                    .addComponent(stLabel1)
                    .addComponent(stLabel2))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(stPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.JCheckBox cacheReads;
    private javax.swing.JCheckBox checkAceQA;
    private javax.swing.JCheckBox checkAmbiguousToN;
    private javax.swing.JCheckBox checkBamValidation;
    private javax.swing.JCheckBox neverCacheBAM;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JLabel stLabel1;
    private javax.swing.JLabel stLabel2;
    private javax.swing.JTextField stPath;
    // End of variables declaration//GEN-END:variables
}