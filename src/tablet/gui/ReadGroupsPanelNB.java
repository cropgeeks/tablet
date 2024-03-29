// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.data.auxiliary.*;

import scri.commons.gui.*;

public class ReadGroupsPanelNB extends JPanel
{
	private ReadGroupsPanel panel;

	/** Creates new form ReadGroupsPanelNB */
	public ReadGroupsPanelNB(ReadGroupsPanel panel)
	{
		this.panel = panel;

		initComponents();

		table.getTableHeader().setReorderingAllowed(false);

		RB.setText(readGroupLabel, "gui.ReadGroupsPanelNB.readGroupLabel");
		RB.setText(colorAll, "gui.ReadGroupsPanelNB.colorAll");
		RB.setText(colorNone, "gui.ReadGroupsPanelNB.colorNone");
		RB.setText(reset, "gui.ReadGroupsPanelNB.reset");

		colorAll.addActionListener(panel);
		colorNone.addActionListener(panel);
		reset.addActionListener(panel);

		toggleComponentEnabled(false);
	}

	private JTable createTable()
	{
		return new JTable()
		{
			@Override
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = ReadGroupsTableModel.getCellRenderer(col);
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}

			@Override
			public String getToolTipText(MouseEvent e)
			{
				return panel.getTableToolTip(e);
			}
		};
	}

	String displayToolTip(ReadGroup readGroup)
	{
		return RB.format("gui.ReadGroupsPanelNB.tooltip",
			readGroup.getID(), readGroup.getCN(), readGroup.getDS(),
			readGroup.getDT(), readGroup.getFO(), readGroup.getKS(),
			readGroup.getLB(), readGroup.getPI(), readGroup.getPL(),
			readGroup.getPU(), readGroup.getSM());
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		readGroupLabel.setEnabled(enabled);
		colorAll.setEnabled(enabled);
		colorNone.setEnabled(enabled);
		reset.setEnabled(enabled);

		table.setEnabled(enabled);
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

        readGroupLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        jLabel1 = new javax.swing.JLabel();
        colorAll = new scri.commons.gui.matisse.HyperLinkLabel();
        colorNone = new scri.commons.gui.matisse.HyperLinkLabel();
        reset = new scri.commons.gui.matisse.HyperLinkLabel();

        readGroupLabel.setText("Read groups: 0");

        table.setModel(new DefaultTableModel());
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);

        jLabel1.setText("|");

        colorAll.setForeground(new java.awt.Color(68, 106, 156));
        colorAll.setText("Colour All");

        colorNone.setForeground(new java.awt.Color(68, 106, 156));
        colorNone.setText("None");

        reset.setForeground(new java.awt.Color(68, 106, 156));
        reset.setText("Reset");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readGroupLabel)
                .addContainerGap(122, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(colorAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(reset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readGroupLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colorAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(colorNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    scri.commons.gui.matisse.HyperLinkLabel colorAll;
    scri.commons.gui.matisse.HyperLinkLabel colorNone;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLabel readGroupLabel;
    scri.commons.gui.matisse.HyperLinkLabel reset;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}