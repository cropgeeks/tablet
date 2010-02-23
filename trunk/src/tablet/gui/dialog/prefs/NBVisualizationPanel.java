// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog.prefs;

import java.awt.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

class NBVisualizationPanel extends JPanel
{
	private DefaultComboBoxModel dnaModel;
	private DefaultComboBoxModel proteinModel;

	public NBVisualizationPanel()
    {
        initComponents();

        setBackground(Color.white);

		// Top panel
        panel1.setBackground(Color.white);
		panel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBVisualizationPanel.panel1Title")));

		RB.setText(dnaLabel, "gui.dialog.prefs.NBVisualizationPanel.dnaLabel");
		RB.setText(proteinLabel, "gui.dialog.prefs.NBVisualizationPanel.proteinLabel");

		dnaModel = new DefaultComboBoxModel();
        dnaModel.addElement("*");
		dnaModel.addElement("-");
        dnaCombo.setModel(dnaModel);
		dnaCombo.setSelectedIndex(Prefs.visPadCharType);

		proteinModel = new DefaultComboBoxModel();
        proteinModel.addElement(".");
		proteinModel.addElement("*");
        proteinCombo.setModel(proteinModel);
		proteinCombo.setSelectedIndex(Prefs.visStopCharType);


		// Bottom panel
		panel2.setBackground(Color.white);
		panel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBVisualizationPanel.panel2Title")));

		RB.setText(cacheReads, "gui.dialog.prefs.NBVisualizationPanel.cacheReads");
		RB.setText(cacheMappings, "gui.dialog.prefs.NBVisualizationPanel.cacheMappings");
		RB.setText(bamLabel, "gui.dialog.prefs.NBVisualizationPanel.bamLabel");

		cacheReads.setSelected(Prefs.cacheReads);
		cacheMappings.setSelected(Prefs.cacheMappings);
		bamSpinner.setValue(Prefs.bamSize);

    }

	public void applySettings()
	{
		Prefs.visPadCharType = dnaCombo.getSelectedIndex();
		Prefs.visStopCharType = proteinCombo.getSelectedIndex();

		Prefs.cacheReads = cacheReads.isSelected();
		Prefs.cacheMappings = cacheMappings.isSelected();
		Prefs.bamSize = (Integer) bamSpinner.getValue();

		reloadBam();

		// This will force the visualization area to recreate its color schemes
		// which will change the rendered characters to their new states
		Tablet.winMain.getAssemblyPanel().forceRedraw();
		Tablet.winMain.getAssemblyPanel().repaint();
	}

	// TODO: I don't like this: it's messy and I don't trust it
	private void reloadBam()
	{
		AssemblyPanel aPanel = Tablet.winMain.getAssemblyPanel();
		Assembly assembly = aPanel.getAssembly();
		Contig contig = aPanel.getContig();

		if (contig != null)
		{
			BamBam bambam = assembly.getBamBam();

			if (bambam != null)
			{
				int s = bambam.getS();
				bambam.reset(Prefs.bamSize);
				bambam.setBlockStart(contig, s);

				aPanel.processBamDataChange();
			}
		}
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        dnaLabel = new javax.swing.JLabel();
        dnaCombo = new javax.swing.JComboBox();
        proteinLabel = new javax.swing.JLabel();
        proteinCombo = new javax.swing.JComboBox();
        panel2 = new javax.swing.JPanel();
        cacheReads = new javax.swing.JCheckBox();
        cacheMappings = new javax.swing.JCheckBox();
        bamLabel = new javax.swing.JLabel();
        bamSpinner = new javax.swing.JSpinner();

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Display options:"));

        dnaLabel.setLabelFor(dnaCombo);
        dnaLabel.setText("Character to display for DNA pad/gap bases:");

        proteinLabel.setLabelFor(proteinCombo);
        proteinLabel.setText("Character to display for Protein stop codons:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proteinLabel)
                    .addComponent(dnaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dnaCombo, 0, 105, Short.MAX_VALUE)
                    .addComponent(proteinCombo, 0, 105, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dnaLabel)
                    .addComponent(dnaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proteinLabel)
                    .addComponent(proteinCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Performance options:"));

        cacheReads.setText("Cache read data to disk");

        cacheMappings.setText("Cache padded/unpadded mapping data to disk");

        bamLabel.setText("Default BAM data window size (in bp):");

        bamSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(100), null, Integer.valueOf(1000)));

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cacheReads)
                    .addComponent(cacheMappings)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addComponent(bamLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bamSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bamLabel)
                    .addComponent(bamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cacheReads)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cacheMappings)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bamLabel;
    private javax.swing.JSpinner bamSpinner;
    private javax.swing.JCheckBox cacheMappings;
    private javax.swing.JCheckBox cacheReads;
    private javax.swing.JComboBox dnaCombo;
    private javax.swing.JLabel dnaLabel;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JComboBox proteinCombo;
    private javax.swing.JLabel proteinLabel;
    // End of variables declaration//GEN-END:variables
}