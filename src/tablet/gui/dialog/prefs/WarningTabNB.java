// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog.prefs;

import java.awt.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

class WarningTabNB extends JPanel
{
	public WarningTabNB()
    {
        initComponents();

        TabletUtils.setPanelColor(this, false);
        TabletUtils.setPanelColor(panel, false);

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBWarningPanel.panelTitle")));

		RB.setText(warnOnClose, "gui.dialog.prefs.NBWarningPanel.warnOnClose");
		RB.setText(warnOnExit, "gui.dialog.prefs.NBWarningPanel.warnOnExit");
		RB.setText(warnNoRef, "gui.dialog.prefs.NBWarningPanel.warnNoRef");
		RB.setText(warnOnPaddedFeatureToggle, "gui.dialog.prefs.NBWarningPanel.warnOnPaddedFeatureToggle");
		RB.setText(warnSearchLimitExceeded, "gui.dialog.prefs.NBWarningPanel.warnSearchLimitExceeded");
		RB.setText(warnRefLengths, "gui.dialog.prefs.NBWarningPanel.warnRefLengths");

		warnOnClose.setSelected(Prefs.guiWarnOnClose);
		warnOnExit.setSelected(Prefs.guiWarnOnExit);
		warnNoRef.setSelected(Prefs.guiWarnNoRef);
		warnOnPaddedFeatureToggle.setSelected(Prefs.guiWarnOnPaddedFeatureToggle);
		warnSearchLimitExceeded.setSelected(Prefs.guiWarnSearchLimitExceeded);
		warnRefLengths.setSelected(Prefs.guiWarnRefLengths);
    }

	public void applySettings()
	{
		Prefs.guiWarnOnClose = warnOnClose.isSelected();
		Prefs.guiWarnOnExit = warnOnExit.isSelected();
		Prefs.guiWarnNoRef = warnNoRef.isSelected();
		Prefs.guiWarnOnPaddedFeatureToggle = warnOnPaddedFeatureToggle.isSelected();
		Prefs.guiWarnSearchLimitExceeded = warnSearchLimitExceeded.isSelected();
		Prefs.guiWarnRefLengths = warnRefLengths.isSelected();
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        warnOnClose = new javax.swing.JCheckBox();
        warnOnExit = new javax.swing.JCheckBox();
        warnOnPaddedFeatureToggle = new javax.swing.JCheckBox();
        warnNoRef = new javax.swing.JCheckBox();
        warnSearchLimitExceeded = new javax.swing.JCheckBox();
        warnRefLengths = new javax.swing.JCheckBox();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Inform me:"));

        warnOnClose.setText("Before closing an assembly if a file is already in memory");

        warnOnExit.setText("Before exiting Tablet if a file is still in memory");

        warnOnPaddedFeatureToggle.setText("When switching between features being treated as padded or unpadded");

        warnNoRef.setText("When attempting to load an assembly if no reference file was included");

        warnSearchLimitExceeded.setText("When a search has stopped after finding 500 results");

        warnRefLengths.setText("If sequence lengths specified in SAM/BAM files don't match those imported");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(warnOnClose)
                    .addComponent(warnOnExit)
                    .addComponent(warnNoRef)
                    .addComponent(warnOnPaddedFeatureToggle)
                    .addComponent(warnSearchLimitExceeded)
                    .addComponent(warnRefLengths))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warnOnClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warnOnExit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warnNoRef)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warnOnPaddedFeatureToggle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warnSearchLimitExceeded)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warnRefLengths)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    private javax.swing.JCheckBox warnNoRef;
    private javax.swing.JCheckBox warnOnClose;
    private javax.swing.JCheckBox warnOnExit;
    private javax.swing.JCheckBox warnOnPaddedFeatureToggle;
    private javax.swing.JCheckBox warnRefLengths;
    private javax.swing.JCheckBox warnSearchLimitExceeded;
    // End of variables declaration//GEN-END:variables
}