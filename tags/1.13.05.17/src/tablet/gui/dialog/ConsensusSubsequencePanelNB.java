// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

public class ConsensusSubsequencePanelNB extends javax.swing.JPanel
{
    public ConsensusSubsequencePanelNB(ConsensusSubsequenceDialog parent)
	{
        initComponents();

		TabletUtils.setPanelColor(this, true);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		RB.setText(lblInfo, "gui.dialog.ConsensusSubsequenceDialog.lblInfo");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInfo = new javax.swing.JLabel();
        lblStartBase = new javax.swing.JLabel();
        spinnerStartBase = new javax.swing.JSpinner();
        lblEndBase = new javax.swing.JLabel();
        spinnerEndBase = new javax.swing.JSpinner();

        lblInfo.setText("Select a subsequence of the reference/consensus to copy:");

        lblStartBase.setLabelFor(spinnerStartBase);
        lblStartBase.setText("Start base:");

        spinnerStartBase.setModel(new javax.swing.SpinnerNumberModel());

        lblEndBase.setLabelFor(spinnerEndBase);
        lblEndBase.setText("End base:");

        spinnerEndBase.setModel(new javax.swing.SpinnerNumberModel());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo)
                        .addContainerGap(107, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStartBase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerStartBase, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblEndBase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerEndBase, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spinnerEndBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStartBase)
                    .addComponent(spinnerStartBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEndBase))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel lblEndBase;
    javax.swing.JLabel lblInfo;
    javax.swing.JLabel lblStartBase;
    javax.swing.JSpinner spinnerEndBase;
    javax.swing.JSpinner spinnerStartBase;
    // End of variables declaration//GEN-END:variables

}