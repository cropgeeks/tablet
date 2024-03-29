// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.text.*;

import tablet.gui.*;
import tablet.io.samtools.*;

import scri.commons.gui.*;

class AboutPanelNB extends javax.swing.JPanel implements ActionListener
{
	public AboutPanelNB()
	{
		initComponents();

		initWebStuff();

		TabletUtils.setPanelColor(this, false);
		TabletUtils.setPanelColor(p2, false);

		webLabel.addActionListener(this);

		String javaVer = System.getProperty("java.version");
		String samtoolsVer = SamtoolsHelper.getVersion();
		long freeMem = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
				- ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
		NumberFormat nf = NumberFormat.getInstance();

		versionLabel.setText(RB.format("gui.dialog.NBAboutPanel.versionLabel", Install4j.VERSION));
		copyrightLabel.setText(RB.format("gui.dialog.NBAboutPanel.copyrightLabel", "\u0026"));
		RB.setText(nameLabel, "gui.dialog.NBAboutPanel.nameLabel");
		javaLabel.setText(RB.format("gui.dialog.NBAboutPanel.javaLabel", javaVer));
		samtoolsLabel.setText(RB.format("gui.dialog.NBAboutPanel.samtoolsLabel", samtoolsVer));
		memLabel.setText(RB.format("gui.dialog.NBAboutPanel.memLabel", nf.format((long)(freeMem/1024f/1024f)) + "MB"));
		localeLabel.setText(RB.format("gui.dialog.NBAboutPanel.localeLabel", java.util.Locale.getDefault()));
		idLabel.setText(RB.format("gui.dialog.NBAboutPanel.idLabel", Prefs.tabletID));

		scriIcon.setText("");
		scriIcon.setIcon(Icons.getIcon("ABOUT"));
	}

	private void initWebStuff()
	{
		final String scriHTML = "https://www.hutton.ac.uk";

		scriIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		scriIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				TabletUtils.visitURL(scriHTML);
			}
		});
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == webLabel)
			TabletUtils.visitURL("https://ics.hutton.ac.uk/tablet");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        p2 = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        localeLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        copyrightLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        javaLabel = new javax.swing.JLabel();
        memLabel = new javax.swing.JLabel();
        samtoolsLabel = new javax.swing.JLabel();
        webLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        scriIcon = new javax.swing.JLabel();

        idLabel.setForeground(java.awt.Color.gray);
        idLabel.setText("Tablet ID:");

        localeLabel.setForeground(java.awt.Color.gray);
        localeLabel.setText("Current Locale:");

        nameLabel.setText("Iain Milne, Gordon Stephen, Micha Bayer, Linda Cardle, Paul Shaw, David Marshall");

        copyrightLabel.setText("Copyright (C) 2009, Plant Bioinformatics Group, JHI");

        versionLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        versionLabel.setText("Tablet - x.xx.xx.xx");

        javaLabel.setForeground(java.awt.Color.gray);
        javaLabel.setText("Java version:");

        memLabel.setForeground(java.awt.Color.gray);
        memLabel.setText("Memory available to JVM:");

        samtoolsLabel.setForeground(java.awt.Color.gray);
        samtoolsLabel.setText("Samtools version:");

        webLabel.setText("https://ics.hutton.ac.uk/tablet");

        scriIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scriIcon.setText("SCRI LOGO");

        javax.swing.GroupLayout p2Layout = new javax.swing.GroupLayout(p2);
        p2.setLayout(p2Layout);
        p2Layout.setHorizontalGroup(
            p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(memLabel)
                    .addGroup(p2Layout.createSequentialGroup()
                        .addGroup(p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionLabel)
                            .addComponent(webLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(copyrightLabel)
                            .addComponent(nameLabel)
                            .addComponent(javaLabel)
                            .addComponent(scriIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(localeLabel)
                            .addComponent(idLabel)
                            .addGroup(p2Layout.createSequentialGroup()
                                .addComponent(samtoolsLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        p2Layout.setVerticalGroup(
            p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(copyrightLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel)
                .addGap(18, 18, 18)
                .addComponent(javaLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(samtoolsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(scriIcon)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(p2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(p2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel copyrightLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel javaLabel;
    private javax.swing.JLabel localeLabel;
    private javax.swing.JLabel memLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel p2;
    private javax.swing.JLabel samtoolsLabel;
    private javax.swing.JLabel scriIcon;
    private javax.swing.JLabel versionLabel;
    private scri.commons.gui.matisse.HyperLinkLabel webLabel;
    // End of variables declaration//GEN-END:variables

}