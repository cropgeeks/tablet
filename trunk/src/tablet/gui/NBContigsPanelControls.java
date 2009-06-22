package tablet.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.RowFilter.*;
import javax.swing.event.*;

import scri.commons.gui.*;

class NBContigsPanelControls extends JPanel implements ActionListener, DocumentListener
{
    private ContigsPanel panel;

    public NBContigsPanelControls(ContigsPanel panel)
	{
		this.panel = panel;

		// NetBeans GUI setup code
		initComponents();

		// i18n text
		RB.setText(filterLabel, "gui.NBContigsPanelControls.filterLabel");

		for (int i = 0; i < 7; i++)
			combo.addItem(RB.getString("gui.NBContigsPanelControls.combo" + i));

		// Event handlers
		textField.getDocument().addDocumentListener(this);
		combo.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		textField.setText("");
		textField.requestFocus();
	}

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(filterLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(combo, 0, 317, Short.MAX_VALUE))
                    .add(textField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(combo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(filterLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables

}