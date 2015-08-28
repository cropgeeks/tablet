// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter.*;

import tablet.data.auxiliary.*;

import scri.commons.gui.*;


class FeaturesPanelNB extends JPanel
	implements ActionListener, DocumentListener
{
	private FeaturesPanel panel;

    /** Creates new form NBFeaturesPanelControls */
    public FeaturesPanelNB(FeaturesPanel panel)
	{
        initComponents();

		this.panel = panel;

		// i18n text
		RB.setText(filterLabel, "gui.NBFeaturesPanelControls.filterLabel");
		RB.setText(checkPadded, "gui.NBFeaturesPanelControls.checkPadded");
		RB.setText(linkEdit, "gui.NBFeaturesPanelControls.linkEdit");
		checkPadded.setToolTipText(RB.getString("gui.NBFeaturesPanelControls.checkPaddedTooltip"));

		for (int i = 0; i < 6; i++)
			combo.addItem(RB.getString("gui.NBFeaturesPanelControls.combo" + i));

		combo.setSelectedIndex(Prefs.guiFeaturesFilter);

		checkPadded.setSelected(Prefs.guiFeaturesArePadded);

		textField.getDocument().addDocumentListener(this);
		checkPadded.addActionListener(this);
		linkEdit.addActionListener(this);
		combo.addActionListener(this);

		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(panel);

		toggleComponentEnabled(false);
    }

	public void changedUpdate(DocumentEvent e)
		{ filter(); }

	public void insertUpdate(DocumentEvent e)
		{ filter(); }

	public void removeUpdate(DocumentEvent e)
		{ filter(); }

	private void filter()
	{
		RowFilter<FeaturesTableModel, Object> rf = null;
		NumberFormat nf = NumberFormat.getNumberInstance();

		try
		{
			int index = combo.getSelectedIndex();

			// Filter by type
			if (index == 0)
				rf = RowFilter.regexFilter(textField.getText().toUpperCase(), 0);

			// Filter by name
			else if (index == 1)
				rf = RowFilter.regexFilter(textField.getText(), 1);

			// Min start position
			else if (index == 2)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 2);
			}

			// Max start position
			else if (index == 3)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number-1, 2);
			}

			// Min end position
			else if (index == 4)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.AFTER, number-1, 3);
			}

			// Max end position
			else if (index == 5)
			{
				int number = Integer.parseInt(textField.getText());
				rf = RowFilter.numberFilter(ComparisonType.BEFORE, number-1, 3);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		panel.setTableFilter(rf);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == checkPadded)
		{
			if (Prefs.guiWarnOnPaddedFeatureToggle)
			{
				String msg = RB.getString("gui.NBFeaturesPanelControls.checkMessage");
				JCheckBox checkbox = new JCheckBox();
				RB.setText(checkbox, "gui.NBFeaturesPanelControls.checkWarning");

				TaskDialog.info(msg, RB.getString("gui.text.close"), checkbox);

				Prefs.guiWarnOnPaddedFeatureToggle = !checkbox.isSelected();
			}

			Prefs.guiFeaturesArePadded = checkPadded.isSelected();
			Feature.ISPADDED = Prefs.guiFeaturesArePadded;

			Tablet.winMain.repaint();
		}

		else if (e.getSource() == linkEdit)
			panel.editFeatures();

		else if (e.getSource() == combo)
		{
			Prefs.guiFeaturesFilter = combo.getSelectedIndex();

			textField.setText("");
			textField.requestFocus();
		}
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		checkPadded.setEnabled(enabled);
		linkEdit.setEnabled(enabled);
		filterLabel.setEnabled(enabled);
		combo.setEnabled(enabled);
		textField.setEnabled(enabled);
		featuresLabel.setEnabled(enabled);
		table.getTableHeader().setVisible(enabled);
	}

	public void nextFeature()
	{
		if(table.getSelectedRow() < table.getRowCount()-1)
			table.setRowSelectionInterval(table.getSelectedRow()+1, table.getSelectedRow()+1);

		table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true));
	}

	public void prevFeature()
	{
		if(table.getSelectedRow() == -1)
			table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
		else if(table.getSelectedRow() > 0)
			table.setRowSelectionInterval(table.getSelectedRow()-1, table.getSelectedRow()-1);

		table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true));
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkPadded = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new JTable() {
            public String getToolTipText(MouseEvent e) {
                return panel.getTableToolTip(e);
            }
        };
        featuresLabel = new javax.swing.JLabel();
        linkEdit = new scri.commons.gui.matisse.HyperLinkLabel();
        filterLabel = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox<String>();
        textField = new javax.swing.JTextField();

        checkPadded.setText("Feature values are padded");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        featuresLabel.setText("Features (0):");

        linkEdit.setForeground(new java.awt.Color(68, 106, 156));
        linkEdit.setText("Select tracks");

        filterLabel.setText("Filter by:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(featuresLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                        .addComponent(linkEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo, 0, 273, Short.MAX_VALUE))
                    .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkPadded)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(featuresLabel)
                    .addComponent(linkEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkPadded)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkPadded;
    private javax.swing.JComboBox<String> combo;
    public javax.swing.JLabel featuresLabel;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private scri.commons.gui.matisse.HyperLinkLabel linkEdit;
    public javax.swing.JTable table;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables

}