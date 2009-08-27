package tablet.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import scri.commons.gui.*;
import tablet.gui.Install4j;

public class NBStartWelcomePanel extends javax.swing.JPanel implements ActionListener
{

    /** Creates new form NBStartWelcomePanel */
    public NBStartWelcomePanel()
	{
        initComponents();
		setOpaque(false);

		tabletLabel.setText("<html>" + RB.format("gui.NBStartWelcomePanel.panel.label", Install4j.VERSION));
		feedbackLabel.setText(RB.getString("gui.NBStartWelcomePanel.panel.feedback"));

		feedbackLabel.setIcon(Icons.getIcon("FEEDBACK"));
		feedbackLabel.addActionListener(this);
    }

	public void actionPerformed(ActionEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabletLabel = new javax.swing.JLabel();
        feedbackLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        tabletLabel.setText("<html>Tablet x.xx.xx.xx - &copy; Plant Bioinformatics Group, SCRI.");

        feedbackLabel.setForeground(new java.awt.Color(68, 106, 156));
        feedbackLabel.setText("Send feedback");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabletLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabletLabel)
                    .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel feedbackLabel;
    private javax.swing.JLabel tabletLabel;
    // End of variables declaration//GEN-END:variables


}
