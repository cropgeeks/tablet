package tablet.gui.dialog;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import tablet.gui.Prefs;

import scri.commons.gui.*;

class NBImportAssemblyAFGPanel extends javax.swing.JPanel
{
    LinkedList<String> recentFiles = new LinkedList<String>();

    public NBImportAssemblyAFGPanel(ImportAssemblyDialog parent)
	{
		initComponents();

		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		RB.setText(bBrowse, "gui.text.browse");
		RB.setText(afgLabel, "gui.dialog.NBImportAssemblyAFGPanel.afgLabel");

		// Parse out the tab-delimited list of files
		StringTokenizer st = new StringTokenizer(Prefs.afgRecentDocs, "\t");
		while (st.hasMoreTokens())
			recentFiles.add(st.nextToken());

		parent.updateComboBox(null, afgComboBox, recentFiles);

		bBrowse.addActionListener(parent);
		afgComboBox.addActionListener(parent);
	}

	String[] getFilenames()
	{
		return new String[] { recentFiles.get(0) };
	}

	boolean isOK()
	{
		// Save the list back to the preferences
		Prefs.afgRecentDocs = "";
		for (String str: recentFiles)
			Prefs.afgRecentDocs += str + "\t";

		return afgComboBox.getSelectedItem() != null;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        afgLabel = new javax.swing.JLabel();
        afgComboBox = new javax.swing.JComboBox();
        bBrowse = new javax.swing.JButton();

        afgLabel.setLabelFor(afgComboBox);
        afgLabel.setText("AFG input file:");

        afgComboBox.setEditable(true);

        bBrowse.setText("Browse...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(afgLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(afgComboBox, 0, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bBrowse)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(afgComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse)
                    .addComponent(afgLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox afgComboBox;
    private javax.swing.JLabel afgLabel;
    javax.swing.JButton bBrowse;
    // End of variables declaration//GEN-END:variables

}