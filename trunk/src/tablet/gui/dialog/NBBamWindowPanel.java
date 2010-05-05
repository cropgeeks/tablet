package tablet.gui.dialog;

import java.awt.*;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

class NBBamWindowPanel extends JPanel
{
	public NBBamWindowPanel()
	{
		initComponents();

		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		RB.setText(bamLabel, "gui.dialog.NBBamWindowPanel.bamLabel");
		RB.setText(label1, "gui.dialog.NBBamWindowPanel.label1");
		RB.setText(label2, "gui.dialog.NBBamWindowPanel.label2");

		bamSpinner.setValue(Prefs.bamSize);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bamLabel = new javax.swing.JLabel();
        bamSpinner = new javax.swing.JSpinner();
        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();

        bamLabel.setText("BAM data window size (in base pairs):");

        label1.setText("A large window allows you to see more data at once, at the expense of load");

        label2.setText("time and memory usage.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bamLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bamSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addComponent(label1)
                    .addComponent(label2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bamLabel)
                    .addComponent(bamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(label1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bamLabel;
    private javax.swing.JSpinner bamSpinner;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    // End of variables declaration//GEN-END:variables

}