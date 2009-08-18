package tablet.gui.dialog;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

class NBImportAssemblyPanel extends javax.swing.JPanel
{
    /** Creates new form NBImportAssembly */
    public NBImportAssemblyPanel(ImportAssemblyDialog parent)
    {
		initComponents();

		assemblyComboBox.addItem(RB.getString("gui.dialog.NBImportAssembly.aceFile"));
		assemblyComboBox.addItem(RB.getString("gui.dialog.NBImportAssembly.afgFile"));
		assemblyComboBox.addItem(RB.getString("gui.dialog.NBImportAssembly.soapFile"));

		assemblyComboBox.addItemListener(parent);

		RB.setText(assemblyLabel, "gui.dialog.NBImportAssembly.assemblyLabel");
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

        assemblyComboBox = new javax.swing.JComboBox();
        assemblyLabel = new javax.swing.JLabel();

        assemblyLabel.setLabelFor(assemblyComboBox);
        assemblyLabel.setText("Open an assembly of type:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(assemblyLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(assemblyComboBox, 0, 75, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(assemblyLabel)
                    .addComponent(assemblyComboBox))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox assemblyComboBox;
    private javax.swing.JLabel assemblyLabel;
    // End of variables declaration//GEN-END:variables

}
