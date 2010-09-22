// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.RowFilter.*;
import javax.swing.table.*;

import scri.commons.gui.*;

class NBContigsPanelControls extends JPanel implements ActionListener, DocumentListener
{
    private ContigsPanel panel;

    public NBContigsPanelControls(ContigsPanel panel)
	{
		this.panel = panel;

		// NetBeans GUI setup code
		initComponents();
		setEnabledState(false);

		// i18n text
		RB.setText(filterLabel, "gui.NBContigsPanelControls.filterLabel");

		for (int i = 0; i < 9; i++)
			combo.addItem(RB.getString("gui.NBContigsPanelControls.combo" + i));

		combo.setSelectedIndex(Prefs.guiContigsFilter);

		// Event handlers
		textField.getDocument().addDocumentListener(this);
		combo.addActionListener(this);

		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(panel);
	}

	public void actionPerformed(ActionEvent e)
	{
		Prefs.guiContigsFilter = combo.getSelectedIndex();

		textField.setText("");
		textField.requestFocus();
	}

	void clearFilter()
		{ textField.setText(""); }

	public void changedUpdate(DocumentEvent e)
		{ filter(); }

	public void insertUpdate(DocumentEvent e)
		{ filter(); }

	public void removeUpdate(DocumentEvent e)
		{ filter(); }

	private void filter()
	{
		RowFilter<ContigsTableModel, Object> rf = null;
		NumberFormat nf = NumberFormat.getNumberInstance();

		try
		{
			int index = combo.getSelectedIndex();

			// Filter by name
			if (index == 0)
				rf = RowFilter.regexFilter(textField.getText(), 0);

			// Filter by minimum contig length
			else if (index == 1)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 1);
			}

			// Filter by maximum contig length
			else if (index == 2)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number+1, 1);
			}

			// Filter by minimum read length
			else if (index == 3)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 2);
			}

			// Filter by maximum read length
			else if (index == 4)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number+1, 2);
			}

			// Filter by minimum feature count
			else if (index == 5)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 3);
			}

			// Filter by maximum feature count
			else if (index == 6)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number+1, 3);
			}

			// Filter by minimum mismatch %
			else if (index == 7)
			{
				float number = nf.parse(textField.getText()).floatValue();
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 4);
			}

			// Filter by maximum mismatch %
			else if (index == 8)
			{
				float number = nf.parse(textField.getText()).floatValue();
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number+1, 4);
			}

			panel.setTableFilter(rf);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	void setEnabledState(boolean state)
	{
		filterLabel.setEnabled(state);
		combo.setEnabled(state);
		textField.setEnabled(state);
		contigsLabel.setEnabled(state);
	}

	private JTable createTable()
	{
		return new JTable()
		{
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = ContigsTableModel.getCellRenderer(this, row, col);

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

        combo = new javax.swing.JComboBox();
        textField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = createTable();
        contigsLabel = new javax.swing.JLabel();

        filterLabel.setLabelFor(combo);
        filterLabel.setText("Filter by:");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        contigsLabel.setText("Contigs (0):");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contigsLabel)
                .addContainerGap(317, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo, 0, 317, Short.MAX_VALUE))
                    .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contigsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    public javax.swing.JLabel contigsLabel;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable table;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables

}