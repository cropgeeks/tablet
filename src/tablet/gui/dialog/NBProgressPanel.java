package tablet.gui.dialog;

import tablet.gui.*;

class NBProgressPanel extends javax.swing.JPanel
{
	NBProgressPanel(ITrackableJob job, String labelString)
	{
		initComponents();

		mainLabel.setText(labelString);

		pBar.setMaximum(job.getMaximum());
		pBar.setIndeterminate(job.isIndeterminate());
	}

	void setMessage(String message)
	{
		msgLabel.setText(message);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pBar = new javax.swing.JProgressBar();
        mainLabel = new javax.swing.JLabel();
        msgLabel = new javax.swing.JLabel();

        pBar.setStringPainted(true);

        mainLabel.setText("mainLabel");

        msgLabel.setText("msgLabel");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .add(mainLabel)
                    .add(msgLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mainLabel)
                .add(11, 11, 11)
                .add(pBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(msgLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mainLabel;
    javax.swing.JLabel msgLabel;
    javax.swing.JProgressBar pBar;
    // End of variables declaration//GEN-END:variables

}