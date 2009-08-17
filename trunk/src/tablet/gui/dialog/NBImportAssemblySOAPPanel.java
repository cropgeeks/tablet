package tablet.gui.dialog;

import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import javax.swing.*;

import scri.commons.gui.*;
import tablet.gui.Prefs;

public class NBImportAssemblySOAPPanel extends javax.swing.JPanel
{
    LinkedList<String> recentFilesSoap;
    LinkedList<String> recentFilesFastA;
    
    /** Creates new form NBImportAssemblySOAPPanel */
    public NBImportAssemblySOAPPanel(ImportAssemblyDialog parent)
    {
	    initComponents();

	    bBrowse.addActionListener(parent);
	    bBrowse2.addActionListener(parent);
	    soapComboBox.addActionListener(parent);

	    recentFilesSoap  = new LinkedList<String>();
	    recentFilesFastA  = new LinkedList<String>();

	    RB.setText(bBrowse, "gui.text.browse");
	    RB.setText(soapLabel, "gui.dialog.NBImportAssemblySOAPPanel.label");
	    RB.setText(bBrowse2, "gui.text.browse");
	    RB.setText(soapLabel2, "gui.dialog.NBImportAssemblySOAPPanel.labelFile2");

	    for(final String path : Prefs.soapRecentDocs)
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
		    for(int i=0; i < files.length; i+=2)
		    {
			    String text = files[i].getPath();
			    if(!recentFilesSoap.contains(text))
				recentFilesSoap.add(text);
		    }
		    for (int i = 1; i < files.length; i+=2)
		    {
			    String text = files[i].getPath();
			    recentFilesFastA.add(text);
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

        soapLabel = new javax.swing.JLabel();
        soapComboBox = new javax.swing.JComboBox();
        bBrowse = new javax.swing.JButton();
        soapLabel2 = new javax.swing.JLabel();
        soapComboBox2 = new javax.swing.JComboBox();
        bBrowse2 = new javax.swing.JButton();

        soapLabel.setText("Choose SOAP file:");
        soapLabel.setMinimumSize(new java.awt.Dimension(80, 14));
        soapLabel.setPreferredSize(new java.awt.Dimension(94, 14));

        soapComboBox.setEditable(true);
        soapComboBox.setMinimumSize(new java.awt.Dimension(0, 0));
        soapComboBox.setPreferredSize(new java.awt.Dimension(70, 20));

        bBrowse.setText("Browse...");

        soapLabel2.setText("Choose second file:");
        soapLabel2.setMinimumSize(new java.awt.Dimension(80, 14));

        soapComboBox2.setEditable(true);
        soapComboBox2.setMinimumSize(new java.awt.Dimension(0, 0));
        soapComboBox2.setPreferredSize(new java.awt.Dimension(70, 20));

        bBrowse2.setText("Browse...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(soapLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(soapLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soapComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bBrowse, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bBrowse2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(soapLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soapLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(soapComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bBrowse;
    javax.swing.JButton bBrowse2;
    javax.swing.JComboBox soapComboBox;
    javax.swing.JComboBox soapComboBox2;
    private javax.swing.JLabel soapLabel;
    private javax.swing.JLabel soapLabel2;
    // End of variables declaration//GEN-END:variables

}
