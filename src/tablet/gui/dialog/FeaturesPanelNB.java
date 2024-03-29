// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.auxiliary.*;
import tablet.gui.*;

import scri.commons.gui.*;

class FeaturesPanelNB extends JPanel implements ActionListener, ListSelectionListener
{
	private ArrayList<Feature.VisibleFeature> order = Feature.order;

	FeaturesPanelNB()
	{
		initComponents();

		TabletUtils.setPanelColor(this, true);
		TabletUtils.setPanelColor(panel, false);

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBFeaturesPanel.panel")));
		RB.setText(label, "gui.dialog.NBFeaturesPanel.label");
		RB.setText(bUp, "gui.dialog.NBFeaturesPanel.bUp");
		RB.setText(bDown, "gui.dialog.NBFeaturesPanel.bDown");
		RB.setText(selectAll, "gui.dialog.NBFeaturesPanel.selectAll");
		RB.setText(selectNone, "gui.dialog.NBFeaturesPanel.selectNone");
		RB.setText(addEnzyme, "gui.dialog.NBFeaturesPanel.addEnzyme");

		createTable();

		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().addListSelectionListener(this);

		bUp.addActionListener(this);
		bDown.addActionListener(this);
		selectAll.addActionListener(this);
		selectNone.addActionListener(this);
	}

	void updateList()
	{
		order.clear();

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < model.getRowCount(); i++)
		{
			order.add(new Feature.VisibleFeature(
				(String) model.getValueAt(i, 1),
				(Boolean) model.getValueAt(i, 0)));
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		// Move Up
		if (e.getSource() == bUp)
		{
			int row = table.getSelectedRow();
			((DefaultTableModel)table.getModel()).moveRow(row, row, row-1);
			table.setRowSelectionInterval(row-1, row-1);
			table.scrollRectToVisible(table.getCellRect(row-1, 0, false));
		}

		// Move Down
		else if (e.getSource() == bDown)
		{
			int row = table.getSelectedRow();
			((DefaultTableModel)table.getModel()).moveRow(row, row, row+1);
			table.setRowSelectionInterval(row+1, row+1);
			table.scrollRectToVisible(table.getCellRect(row+1, 0, false));
		}

		// Select All
		else if (e.getSource() == selectAll)
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(true, i, 0);

		// Select None
		else if (e.getSource() == selectNone)
			for (int i = 0; i < table.getRowCount(); i++)
				table.setValueAt(false, i, 0);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		int row = table.getSelectedRow();
		bUp.setEnabled(row > 0);
		bDown.setEnabled(row >= 0 && row < table.getRowCount()-1);
	}

	void createTable()
	{
		String[] columnNames = { "Enabled", "Feature type" };

		Object[][] data = new Object[order.size()][2];

		for (int i = 0; i < order.size(); i++)
		{
			data[i][0] = order.get(i).isVisible;
			data[i][1] = order.get(i).type;
		}

		table.setModel(getModel(data, columnNames));
		table.getColumnModel().getColumn(0).setMaxWidth(60);
	}

	private DefaultTableModel getModel(Object[][] data, String[] columnNames)
	{
		return new DefaultTableModel(data, columnNames)
		{
			public Class getColumnClass(int c)
			{
				if (c == 0)
					return Boolean.class;

				return String.class;
			}

			public boolean isCellEditable(int row, int col) {
				return col == 0;
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

        panel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        sp = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        bUp = new javax.swing.JButton();
        bDown = new javax.swing.JButton();
        selectAll = new scri.commons.gui.matisse.HyperLinkLabel();
        label2 = new javax.swing.JLabel();
        selectNone = new scri.commons.gui.matisse.HyperLinkLabel();
        addEnzyme = new scri.commons.gui.matisse.HyperLinkLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select feature tracks:"));

        label.setLabelFor(table);
        label.setText("Feature tracks can be enabled, disabled, and reordered using the controls below:");

        table.setModel(new DefaultTableModel());
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp.setViewportView(table);

        bUp.setText("Move up");
        bUp.setEnabled(false);

        bDown.setText("Move down");
        bDown.setEnabled(false);

        selectAll.setForeground(new java.awt.Color(68, 106, 156));
        selectAll.setText("Select all");

        label2.setText("|");

        selectNone.setForeground(new java.awt.Color(68, 106, 156));
        selectNone.setText("Select none");

        addEnzyme.setForeground(new java.awt.Color(68, 106, 156));
        addEnzyme.setText("Add restriction enzyme");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                .addComponent(selectAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addComponent(addEnzyme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sp, 0, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(bUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        panelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bDown, bUp});

        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(bUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bDown))
                    .addComponent(sp, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label2)
                    .addComponent(selectAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEnzyme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    scri.commons.gui.matisse.HyperLinkLabel addEnzyme;
    private javax.swing.JButton bDown;
    private javax.swing.JButton bUp;
    private javax.swing.JLabel label;
    private javax.swing.JLabel label2;
    private javax.swing.JPanel panel;
    private scri.commons.gui.matisse.HyperLinkLabel selectAll;
    private scri.commons.gui.matisse.HyperLinkLabel selectNone;
    private javax.swing.JScrollPane sp;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}