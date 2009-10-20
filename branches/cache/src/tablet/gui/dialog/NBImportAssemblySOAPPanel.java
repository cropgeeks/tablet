// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import tablet.gui.Prefs;

import scri.commons.gui.*;

class NBImportAssemblySOAPPanel extends javax.swing.JPanel
{
    LinkedList<String> recentFilesSoap = new LinkedList<String>();
    LinkedList<String> recentFilesFasta = new LinkedList<String>();

	public NBImportAssemblySOAPPanel(ImportAssemblyDialog parent)
	{
		initComponents();

		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		RB.setText(bBrowse1, "gui.text.browse");
		RB.setText(bBrowse2, "gui.text.browse");
		RB.setText(soapLabel, "gui.dialog.NBImportAssemblySOAPPanel.soapLabel");
		RB.setText(fastaLabel, "gui.dialog.NBImportAssemblySOAPPanel.fastaLabel");

		// Parse out the tab-delimited list of files
		StringTokenizer st = new StringTokenizer(Prefs.soapRecentDocs, "\t");
		while (st.hasMoreTokens())
			recentFilesSoap.add(st.nextToken());

		st = new StringTokenizer(Prefs.fastaRecentDocs, "\t");
		while (st.hasMoreTokens())
			recentFilesFasta.add(st.nextToken());

		parent.updateComboBox(null, soapComboBox, recentFilesSoap);
		parent.updateComboBox(null, fastaComboBox, recentFilesFasta);

		bBrowse1.addActionListener(parent);
		bBrowse2.addActionListener(parent);
		soapComboBox.addActionListener(parent);
		fastaComboBox.addActionListener(parent);
	}

	String[] getFilenames()
	{
		return new String[] {
			recentFilesSoap.get(0), recentFilesFasta.get(0) };
	}

	boolean isOK()
	{
		// Save the list back to the preferences
		Prefs.soapRecentDocs = "";
		for (String str: recentFilesSoap)
			Prefs.soapRecentDocs += str + "\t";

		Prefs.fastaRecentDocs = "";
		for (String str: recentFilesFasta)
			Prefs.fastaRecentDocs += str + "\t";

		return soapComboBox.getSelectedItem() != null &&
			fastaComboBox.getSelectedItem() != null;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        soapLabel = new javax.swing.JLabel();
        soapComboBox = new javax.swing.JComboBox();
        bBrowse1 = new javax.swing.JButton();
        fastaLabel = new javax.swing.JLabel();
        fastaComboBox = new javax.swing.JComboBox();
        bBrowse2 = new javax.swing.JButton();

        soapLabel.setLabelFor(soapComboBox);
        soapLabel.setText("SOAP input file:");

        soapComboBox.setEditable(true);

        bBrowse1.setText("Browse...");

        fastaLabel.setLabelFor(fastaComboBox);
        fastaLabel.setText("FASTA input file:");

        fastaComboBox.setEditable(true);

        bBrowse2.setText("Browse...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soapLabel)
                    .addComponent(fastaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(soapComboBox, 0, 249, Short.MAX_VALUE)
                    .addComponent(fastaComboBox, 0, 249, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bBrowse1)
                    .addComponent(bBrowse2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soapLabel)
                    .addComponent(soapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fastaLabel)
                    .addComponent(fastaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bBrowse1;
    javax.swing.JButton bBrowse2;
    javax.swing.JComboBox fastaComboBox;
    private javax.swing.JLabel fastaLabel;
    javax.swing.JComboBox soapComboBox;
    private javax.swing.JLabel soapLabel;
    // End of variables declaration//GEN-END:variables

}