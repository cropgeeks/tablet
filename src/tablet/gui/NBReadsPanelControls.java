package tablet.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class NBReadsPanelControls extends javax.swing.JPanel
{
	private ReadsPanel panel;

    /** Creates new form NBReadsPanelControls */
    public NBReadsPanelControls(ReadsPanel panel)
	{
        initComponents();

		this.panel = panel;

		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(panel);
    }

	private JTable createTable()
	{
		return new JTable()
		{
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = ReadsTableModel.getCellRenderer(this, row, col);

				if (tcr != null)
					return tcr;

				return super.getCellRenderer(row, col);
			}
			
			public String getToolTipText(MouseEvent e)
			{
				return panel.getTableToolTip(e);
			}
		};
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
        cLabel = new javax.swing.JLabel();
        pairLabel = new javax.swing.JLabel();
        noInPairLabel = new javax.swing.JLabel();
        iSizeLabel = new javax.swing.JLabel();
        mPosLabel = new javax.swing.JLabel();
        mContigLabel = new javax.swing.JLabel();
        numberInPairLabel = new javax.swing.JLabel();
        properlyPairedLabel = new javax.swing.JLabel();
        cigarLabel = new javax.swing.JLabel();
        insertSizeLabel = new javax.swing.JLabel();
        matePosLabel = new javax.swing.JLabel();
        mateContigLabel = new javax.swing.JLabel();

        readsLabel.setText("Visible reads (0):");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        cLabel.setLabelFor(cigarLabel);
        cLabel.setText("Cigar:");

        pairLabel.setLabelFor(properlyPairedLabel);
        pairLabel.setText("Properly paired:");

        noInPairLabel.setLabelFor(numberInPairLabel);
        noInPairLabel.setText("Number in pair:");

        iSizeLabel.setLabelFor(insertSizeLabel);
        iSizeLabel.setText("Insert size:");

        mPosLabel.setLabelFor(matePosLabel);
        mPosLabel.setText("Mate pos:");

        mContigLabel.setLabelFor(mateContigLabel);
        mContigLabel.setText("Mate contig:");

        numberInPairLabel.setForeground(new java.awt.Color(255, 0, 0));
        numberInPairLabel.setText(" ");

        properlyPairedLabel.setForeground(new java.awt.Color(255, 0, 0));
        properlyPairedLabel.setText(" ");

        cigarLabel.setForeground(new java.awt.Color(255, 0, 0));
        cigarLabel.setText(" ");

        insertSizeLabel.setForeground(new java.awt.Color(255, 0, 0));
        insertSizeLabel.setText(" ");

        matePosLabel.setForeground(new java.awt.Color(255, 0, 0));
        matePosLabel.setText(" ");

        mateContigLabel.setForeground(new java.awt.Color(255, 0, 0));
        mateContigLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readsLabel)
                .addContainerGap(310, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noInPairLabel)
                    .addComponent(pairLabel)
                    .addComponent(cLabel)
                    .addComponent(iSizeLabel)
                    .addComponent(mPosLabel)
                    .addComponent(mContigLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(properlyPairedLabel)
                    .addComponent(numberInPairLabel)
                    .addComponent(insertSizeLabel)
                    .addComponent(matePosLabel)
                    .addComponent(mateContigLabel)
                    .addComponent(cigarLabel))
                .addContainerGap(300, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cLabel)
                    .addComponent(cigarLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pairLabel)
                    .addComponent(properlyPairedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noInPairLabel)
                    .addComponent(numberInPairLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mPosLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mContigLabel)
                            .addComponent(mateContigLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(insertSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(matePosLabel)
                        .addGap(20, 20, 20)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel cLabel;
    javax.swing.JLabel cigarLabel;
    javax.swing.JLabel iSizeLabel;
    javax.swing.JLabel insertSizeLabel;
    private javax.swing.JScrollPane jScrollPane1;
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
