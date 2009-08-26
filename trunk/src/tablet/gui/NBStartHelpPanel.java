package tablet.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import scri.commons.gui.Icons;
import scri.commons.gui.RB;
import scri.commons.gui.matisse.HyperLinkLabel;

public class NBStartHelpPanel extends javax.swing.JPanel implements ActionListener
{

   private HyperLinkLabel[] labels = new HyperLinkLabel[8];

	private static String home =
		"http://bioinf.scri.ac.uk/flapjack/help";

    public NBStartHelpPanel()
	{
		initComponents();
		setOpaque(false);

		RB.setText(homeLabel, "gui.navpanel.NBStartHelpPanel.homeLabel");

		homeLabel.setIcon(Icons.getIcon("BOOK"));
		homeLabel.addActionListener(this);

		labels[0] = link1; labels[1] = link2;
		labels[2] = link3; labels[3] = link4;
		labels[4] = link5; labels[5] = link6;
		labels[6] = link7; labels[7] = link8;

		for (int i = 0; i < labels.length; i++)
		{
			//RB.setText(labels[i], "gui.navpanel.NBStartHelpPanel.link" + (i+1));
			labels[i].addActionListener(this);
		}
    }

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == homeLabel)
		{

		}
		else
		{
			for (int i = 0; i < labels.length; i++)
				if (e.getSource() == labels[i])
				{
				}
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        homeLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        link1 = new scri.commons.gui.matisse.HyperLinkLabel();
        link2 = new scri.commons.gui.matisse.HyperLinkLabel();
        link3 = new scri.commons.gui.matisse.HyperLinkLabel();
        link4 = new scri.commons.gui.matisse.HyperLinkLabel();
        link5 = new scri.commons.gui.matisse.HyperLinkLabel();
        link6 = new scri.commons.gui.matisse.HyperLinkLabel();
        link7 = new scri.commons.gui.matisse.HyperLinkLabel();
        link8 = new scri.commons.gui.matisse.HyperLinkLabel();

        homeLabel.setForeground(new java.awt.Color(68, 106, 156));
        homeLabel.setText("Visit the online Tablet user manual");

        link1.setForeground(new java.awt.Color(68, 106, 156));
        link1.setIcon(Icons.getIcon("BUTTON"));
        link1.setText("link1");

        link2.setForeground(new java.awt.Color(68, 106, 156));
        link2.setIcon(Icons.getIcon("BUTTON"));
        link2.setText("link2");

        link3.setForeground(new java.awt.Color(68, 106, 156));
        link3.setIcon(Icons.getIcon("BUTTON"));
        link3.setText("link3");

        link4.setForeground(new java.awt.Color(68, 106, 156));
        link4.setIcon(Icons.getIcon("BUTTON"));
        link4.setText("link4");

        link5.setForeground(new java.awt.Color(68, 106, 156));
        link5.setIcon(Icons.getIcon("BUTTON"));
        link5.setText("link5");

        link6.setForeground(new java.awt.Color(68, 106, 156));
        link6.setIcon(Icons.getIcon("BUTTON"));
        link6.setText("link6");

        link7.setForeground(new java.awt.Color(68, 106, 156));
        link7.setIcon(Icons.getIcon("BUTTON"));
        link7.setText("link7");

        link8.setForeground(new java.awt.Color(68, 106, 156));
        link8.setIcon(Icons.getIcon("BUTTON"));
        link8.setText("link8");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(homeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(link1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(link2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(link3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(link4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(link5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(link6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(link7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(link8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel homeLabel;
    private scri.commons.gui.matisse.HyperLinkLabel link1;
    private scri.commons.gui.matisse.HyperLinkLabel link2;
    private scri.commons.gui.matisse.HyperLinkLabel link3;
    private scri.commons.gui.matisse.HyperLinkLabel link4;
    private scri.commons.gui.matisse.HyperLinkLabel link5;
    private scri.commons.gui.matisse.HyperLinkLabel link6;
    private scri.commons.gui.matisse.HyperLinkLabel link7;
    private scri.commons.gui.matisse.HyperLinkLabel link8;
    // End of variables declaration//GEN-END:variables


}
