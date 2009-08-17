package tablet.gui.dialog;

import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import javax.swing.*;

import scri.commons.gui.*;
import tablet.gui.Prefs;

public class NBImportAssemblyAFGPanel extends javax.swing.JPanel
{
    LinkedList<String> recentFiles;
    
    /** Creates new form NBImportAssemblyAFGPanel */
    public NBImportAssemblyAFGPanel(ImportAssemblyDialog parent)
    {
	    initComponents();

	    bBrowse.addActionListener(parent);
	    afgComboBox.addActionListener(parent);

	    recentFiles  = new LinkedList<String>();

	    RB.setText(bBrowse, "gui.text.browse");
	    RB.setText(afgLabel, "gui.dialog.NBImportAssemblyAFGPanel.label");

	    for(final String path : Prefs.afgRecentDocs)
	    {
		    // Ignore any that haven't been set yet
		    if (path == null || path.equals(" "))
			    continue;

		    // Split multi-file inputs
		    final String[] paths = path.split("<!TABLET!>");

		    File[] files = new File[paths.length];
		    for (int i = 0; i < files.length; i++)
			    files[i] = new File(paths[i]);

		    // Button text will be "name" (or "name1" | "name2")
		    for(int i=0; i < files.length; i++)
		    {
			String text = files[i].getPath();
			if(!recentFiles.contains(text))
			    recentFiles.add(text);
		    }
	    }

	    setBackground(Color.white);
	    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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

        setPreferredSize(new java.awt.Dimension(431, 45));

        afgLabel.setText("Choose AFG file:");
        afgLabel.setPreferredSize(new java.awt.Dimension(94, 14));

        afgComboBox.setEditable(true);
        afgComboBox.setMinimumSize(new java.awt.Dimension(0, 0));
        afgComboBox.setPreferredSize(new java.awt.Dimension(70, 20));

        bBrowse.setText("Browse...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(afgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(afgComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bBrowse)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(afgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(afgComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox afgComboBox;
    private javax.swing.JLabel afgLabel;
    javax.swing.JButton bBrowse;
    // End of variables declaration//GEN-END:variables

}
