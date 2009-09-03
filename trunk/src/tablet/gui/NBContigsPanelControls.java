// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.RowFilter.*;
import javax.swing.event.*;

import tablet.gui.*;

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

		for (int i = 0; i < 7; i++)
			combo.addItem(RB.getString("gui.NBContigsPanelControls.combo" + i));

		combo.setSelectedIndex(Prefs.guiContigsFilter);

		// Event handlers
		textField.getDocument().addDocumentListener(this);
		combo.addActionListener(this);
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
		}
		catch (Exception e)
		{
			System.out.println(e);
		}


		panel.setTableFilter(rf);
	}

	void setEnabledState(boolean state)
	{
		filterLabel.setEnabled(state);
		combo.setEnabled(state);
		textField.setEnabled(state);
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

        filterLabel.setLabelFor(combo);
        filterLabel.setText("Filter by:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables

}