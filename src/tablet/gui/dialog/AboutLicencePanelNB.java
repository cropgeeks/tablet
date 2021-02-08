// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

class AboutLicencePanelNB extends JPanel implements ActionListener
{
	public AboutLicencePanelNB()
	{
		initComponents();

		RB.setText(label, "gui.dialog.AboutPanelNB.label");

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement("Tablet");
		model.addElement("Flamingo");
		model.addElement("Rebase");
		model.addElement("Samtools");
		model.addElement("SQLite JDBC");

		combo.setModel(model);
		combo.addActionListener(this);

		htmlPane.setPreferredSize(new Dimension(0, 0));
		displayLicence("tablet.html");
	}

	private void displayLicence(String filename)
	{
		try
		{
			htmlPane.setPage(getClass().getResource("/installer/licence/" + filename));
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (combo.getSelectedItem().equals("Tablet"))
			displayLicence("tablet.html");
		else if (combo.getSelectedItem().equals("Flamingo"))
			displayLicence("flamingo.txt");
		else if (combo.getSelectedItem().equals("Rebase"))
			displayLicence("rebase.txt");
		else if (combo.getSelectedItem().equals("Samtools"))
			displayLicence("samtools.txt");
		else if (combo.getSelectedItem().equals("SQLite JDBC"))
			displayLicence("xerial-sqlite.txt");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        htmlPane = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox<String>();

        htmlPane.setEditable(false);
        jScrollPane1.setViewportView(htmlPane);

        label.setLabelFor(combo);
        label.setText("View licence details for:");
        jPanel1.add(label);
        jPanel1.add(combo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combo;
    private javax.swing.JEditorPane htmlPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}