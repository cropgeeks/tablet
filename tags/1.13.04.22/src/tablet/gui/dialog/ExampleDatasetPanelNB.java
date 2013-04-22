// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import javax.swing.BorderFactory;

import tablet.gui.*;

import scri.commons.gui.*;

public class ExampleDatasetPanelNB extends javax.swing.JPanel
{
    /** Creates new form NBExampleDataset */
    public ExampleDatasetPanelNB(ExampleDatasetDialog parent)
	{
        initComponents();

		TabletUtils.setPanelColor(this, true);
		TabletUtils.setPanelColor(panel, false);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.ExampleDatasetDialog.panel.title")));

		RB.setText(lblT, "gui.dialog.ExampleDatasetDialog.lblT");
		RB.setText(lblD, "gui.dialog.ExampleDatasetDialog.lblD");
		RB.setText(labelMore, "gui.dialog.ExampleDatasetDialog.labelMore");

		datasetCombo.addActionListener(parent);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        datasetCombo = new javax.swing.JComboBox<String>();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        lblT = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        lblD = new javax.swing.JLabel();
        lblDetails = new javax.swing.JLabel();
        labelMore = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select an example assembly:"));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(2);
        descriptionTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(descriptionTextArea);

        lblT.setText("Type:");

        lblType.setText("jLabel1");

        lblD.setText("Details:");

        lblDetails.setText("jLabel1");

        labelMore.setText("More example files may be made available at a later date.");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(datasetCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 409, Short.MAX_VALUE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblD)
                            .addComponent(lblT))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblType)
                            .addComponent(lblDetails)))
                    .addComponent(labelMore))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(datasetCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblT)
                    .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblD)
                    .addComponent(lblDetails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelMore)
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
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox<String> datasetCombo;
    javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelMore;
    javax.swing.JLabel lblD;
    javax.swing.JLabel lblDetails;
    javax.swing.JLabel lblT;
    javax.swing.JLabel lblType;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}