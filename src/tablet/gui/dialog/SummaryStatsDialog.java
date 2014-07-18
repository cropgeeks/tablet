// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

public class SummaryStatsDialog extends JDialog implements ActionListener
{
	private boolean isOK;

	private SummaryStatsTableModel model;

	public SummaryStatsDialog(AssemblySummary statistics)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.SummaryStatsDialog.title"),
			true
		);

		isOK = false;

		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		model = new SummaryStatsTableModel(statistics);
		statsTable.setModel(model);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
		RB.setText(bClose, "gui.text.close");
		bClose.addActionListener(this);

		RB.setText(bCopy, "gui.dialog.SummaryStatsDialog.bCopy");
		bCopy.addActionListener(this);
	}

	public boolean isOK()
		{ return isOK; }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bCopy)
			TabletUtils.copyTableToClipboard(statsTable, model);
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = statsTable.rowAtPoint(e.getPoint());
		row = statsTable.convertRowIndexToModel(row);

		String stat = (String) model.getValueAt(row, 0);
		String value = (String) model.getValueAt(row, 1);

		return stat + ": " + value;
	}

	private JTable createTable()
	{
		return new JTable()
		{
			@Override
			public String getToolTipText(MouseEvent e)
			{
				return getTableToolTip(e);
			}
		};
	}

	class SummaryStatsTableModel extends AbstractTableModel
	{
		private Object[][] data;
		private String[] columnNames;

		SummaryStatsTableModel(AssemblySummary statistics)
		{
			data = new Object[][]
			{
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.contigCount"), statistics.getContigCount() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.contigLength"), statistics.getAverageContigLen() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.readCount"), statistics.getReadCount() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.readsPerContig"), statistics.getAverageReads() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.n50"), statistics.getN50() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.n90"), statistics.getN90() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.assemblyName"), statistics.getAssemblyName() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.assemblySize"), statistics.getAssemblySize() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.referenceName"), statistics.getReferenceName() },
				{ RB.getString("gui.dialog.SummaryStatsDialog.table.referenceSize"), statistics.getReferenceSize() },
			};

			columnNames = new String[] {
				RB.getString("gui.dialog.SummaryStatsDialog.table.header.col1"),
				RB.getString("gui.dialog.SummaryStatsDialog.table.header.col2")
			};
		}

		@Override
		public int getRowCount()
		{
			return data.length;
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return data[rowIndex][columnIndex];
		}

		@Override
		public String getColumnName(int column)
		{
			return columnNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bCopy = new javax.swing.JButton();
        bClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        statsTable = createTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bCopy.setText("Copy to clipboard");
        dialogPanel1.add(bCopy);

        bClose.setText("Close");
        dialogPanel1.add(bClose);

        statsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        jScrollPane1.setViewportView(statsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bClose;
    private javax.swing.JButton bCopy;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable statsTable;
    // End of variables declaration//GEN-END:variables

}