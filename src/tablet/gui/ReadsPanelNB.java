// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import scri.commons.gui.*;

public class ReadsPanelNB extends javax.swing.JPanel
{
	private ReadsPanel panel;

    /** Creates new form NBReadsPanelControls */
    public ReadsPanelNB(ReadsPanel panel)
	{
        initComponents();

		this.panel = panel;

		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(panel);
		exportLinkLabel.addActionListener(panel);
		exportLinkLabel.setEnabled(false);

		readsLabel.setText(RB.format("gui.ReadsPanel.readsLabel", 0));

		// Ensures the labels aren't visible at startup
		setLabelStates(false, false);
		toggleComponentEnabled(false);
    }

	private JTable createTable()
	{
		return new JTable()
		{
			@Override
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = ReadsTableModel.getCellRenderer(col);
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}

			@Override
			public String getToolTipText(MouseEvent e)
			{
				return panel.getTableToolTip(e);
			}
		};
	}

	public void setLabelStates(boolean cigar, boolean isPaired)
	{
		// Set visible if we have cigar strings for reads
		labelPanel.setVisible(cigar);
		cigarLabel.setVisible(cigar);
		cLabel.setVisible(cigar);

		// Set visible if we have paired data
		properlyPairedLabel.setVisible(isPaired);
		pairLabel.setVisible(isPaired);
		numberInPairLabel.setVisible(isPaired);
		noInPairLabel.setVisible(isPaired);
		insertSizeLabel.setVisible(isPaired);
		iSizeLabel.setVisible(isPaired);
		matePosLabel.setVisible(isPaired);
		mPosLabel.setVisible(isPaired);
		mateContigLabel.setVisible(isPaired);
		mContigLabel.setVisible(isPaired);
	}

	void setReadInfoToDefaults()
	{
		cigarLabel.setText("");
		properlyPairedLabel.setText("");
		numberInPairLabel.setText("");
		insertSizeLabel.setText("");
		matePosLabel.setText("");
		mateContigLabel.setText("");
	}

	void toggleComponentEnabled(boolean enabled)
	{
		cLabel.setEnabled(enabled);
		cigarLabel.setEnabled(enabled);
		exportLinkLabel.setEnabled(enabled);
		iSizeLabel.setEnabled(enabled);
		insertSizeLabel.setEnabled(enabled);
		labelPanel.setEnabled(enabled);
		mContigLabel.setEnabled(enabled);
		mPosLabel.setEnabled(enabled);
		mateContigLabel.setEnabled(enabled);
		matePosLabel.setEnabled(enabled);
		noInPairLabel.setEnabled(enabled);
		numberInPairLabel.setEnabled(enabled);
		pairLabel.setEnabled(enabled);
		properlyPairedLabel.setEnabled(enabled);
		readsLabel.setEnabled(enabled);
		table.getTableHeader().setVisible(enabled);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        readsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        labelPanel = new javax.swing.JPanel();
        mContigLabel = new javax.swing.JLabel();
        properlyPairedLabel = new javax.swing.JLabel();
        mPosLabel = new javax.swing.JLabel();
        pairLabel = new javax.swing.JLabel();
        insertSizeLabel = new javax.swing.JLabel();
        matePosLabel = new javax.swing.JLabel();
        numberInPairLabel = new javax.swing.JLabel();
        noInPairLabel = new javax.swing.JLabel();
        mateContigLabel = new javax.swing.JLabel();
        iSizeLabel = new javax.swing.JLabel();
        cigarLabel = new javax.swing.JLabel();
        cLabel = new javax.swing.JLabel();
        exportLinkLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        readsLabel.setText("Visible reads (0):");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        labelPanel.setAutoscrolls(true);

        mContigLabel.setLabelFor(mateContigLabel);
        mContigLabel.setText("Mate contig:");

        properlyPairedLabel.setForeground(new java.awt.Color(255, 0, 0));
        properlyPairedLabel.setText(" ");

        mPosLabel.setLabelFor(matePosLabel);
        mPosLabel.setText("Mate pos:");

        pairLabel.setLabelFor(properlyPairedLabel);
        pairLabel.setText("Properly paired:");

        insertSizeLabel.setForeground(new java.awt.Color(255, 0, 0));
        insertSizeLabel.setText(" ");

        matePosLabel.setForeground(new java.awt.Color(255, 0, 0));
        matePosLabel.setText(" ");

        numberInPairLabel.setForeground(new java.awt.Color(255, 0, 0));
        numberInPairLabel.setText(" ");

        noInPairLabel.setLabelFor(numberInPairLabel);
        noInPairLabel.setText("Number in pair:");

        mateContigLabel.setForeground(new java.awt.Color(255, 0, 0));
        mateContigLabel.setText(" ");

        iSizeLabel.setLabelFor(insertSizeLabel);
        iSizeLabel.setText("Insert size:");

        cigarLabel.setForeground(new java.awt.Color(255, 0, 0));
        cigarLabel.setText(" ");

        cLabel.setLabelFor(cigarLabel);
        cLabel.setText("Cigar:");

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noInPairLabel)
                    .addComponent(pairLabel)
                    .addComponent(cLabel)
                    .addComponent(iSizeLabel)
                    .addComponent(mPosLabel)
                    .addComponent(mContigLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(properlyPairedLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numberInPairLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(insertSizeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(matePosLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cigarLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mateContigLabel, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(335, Short.MAX_VALUE))
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(cLabel)
                    .addComponent(cigarLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(pairLabel)
                    .addComponent(properlyPairedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(noInPairLabel)
                    .addComponent(numberInPairLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, labelPanelLayout.createSequentialGroup()
                        .addComponent(iSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mPosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, labelPanelLayout.createSequentialGroup()
                        .addComponent(insertSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(matePosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(mContigLabel)
                    .addComponent(mateContigLabel))
                .addContainerGap())
        );

        exportLinkLabel.setForeground(new java.awt.Color(68, 106, 156));
        exportLinkLabel.setText("Export data");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
                .addComponent(exportLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
            .addComponent(labelPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(readsLabel)
                    .addComponent(exportLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel cLabel;
    javax.swing.JLabel cigarLabel;
    scri.commons.gui.matisse.HyperLinkLabel exportLinkLabel;
    javax.swing.JLabel iSizeLabel;
    javax.swing.JLabel insertSizeLabel;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JPanel labelPanel;
    javax.swing.JLabel mContigLabel;
    javax.swing.JLabel mPosLabel;
    javax.swing.JLabel mateContigLabel;
    javax.swing.JLabel matePosLabel;
    javax.swing.JLabel noInPairLabel;
    javax.swing.JLabel numberInPairLabel;
    javax.swing.JLabel pairLabel;
    javax.swing.JLabel properlyPairedLabel;
    javax.swing.JLabel readsLabel;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}