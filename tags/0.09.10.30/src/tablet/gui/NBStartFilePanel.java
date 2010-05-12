// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import java.io.File;
import scri.commons.gui.*;
import scri.commons.gui.matisse.HyperLinkLabel;

/**
 *
 * @author gsteph
 */
public class NBStartFilePanel extends javax.swing.JPanel implements ActionListener
{
	private HyperLinkLabel[] labels = new HyperLinkLabel[4];
	private String[] files = new String[4];
	private String[] filenames = new String[4];

	public NBStartFilePanel()
	{
		initComponents();
		setOpaque(false);

		RB.setText(importLabel, "gui.NBStartFilePanel.importLabel");
		RB.setText(openLabel, "gui.NBStartFilePanel.openLabel");
		RB.setText(rateLabel, "gui.NBStartFilePanel.rateLabel");

		importLabel.setIcon(Icons.getIcon("FILEOPEN16"));
		importLabel.addActionListener(this);

		// Create the labels array
		labels[0] = project0; labels[1] = project1;
		labels[2] = project2; labels[3] = project3;

		int j=0;
		// Parse the list of recent documents
		for (final String path: Prefs.guiRecentDocs)
		{
			// Ignore any that haven't been set yet
			if (path == null || path.equals(" "))
				continue;

			// Split multi-file inputs
			final String[] paths = path.split("<!TABLET!>");

			File[] tempFiles = new File[paths.length];
			for (int i = 0; i < tempFiles.length; i++)
				tempFiles[i] = new File(paths[i]);

			// Button text will be "name" (or "name1" | "name2")
			String text = tempFiles[0].getName();
			String filePath = tempFiles[0].getPath();
			for (int i = 1; i < tempFiles.length; i++)
			{
				text += "&nbsp;&nbsp;~&nbsp;&nbsp;" + tempFiles[i].getName();
				filePath += " ~ " + tempFiles[i].getPath();
			}

			if(j < filenames.length)
			{
				filenames[j] = text;
				files[j] = filePath;
			}
			j++;
		}

		for (int i = 0; i < labels.length; i++)
		{
			if (filenames[i] != null)
			{
				labels[i].addActionListener(this);
				labels[i].setText(filenames[i]);
			}
			else
				labels[i].setVisible(false);
		}

		ratingsPanel.doSetup(Prefs.rating,
			Icons.getIcon("STARON"), Icons.getIcon("STAROFF"));
		ratingsPanel.addActionListener(this);

		ratingsPanel.setVisible(false);
		rateLabel.setVisible(false);
    }

	public void actionPerformed(ActionEvent e)
	{
		WinMain wm = Tablet.winMain;

		if(e.getSource() == importLabel)
		{
			wm.getCommands().fileOpen(null);
		}

		for (int i = 0; i < labels.length; i++)
			if (e.getSource() == labels[i])
				wm.getCommands().fileOpen(files[i].split(" ~ "));

//		if (e.getSource() == ratingsPanel)
//			Prefs.rating = ratingsPanel.getRating();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        openLabel = new javax.swing.JLabel();
        project0 = new scri.commons.gui.matisse.HyperLinkLabel();
        project1 = new scri.commons.gui.matisse.HyperLinkLabel();
        project2 = new scri.commons.gui.matisse.HyperLinkLabel();
        project3 = new scri.commons.gui.matisse.HyperLinkLabel();
        rateLabel = new javax.swing.JLabel();
        ratingsPanel = new scri.commons.gui.matisse.RatingsPanel();

        importLabel.setForeground(new java.awt.Color(68, 106, 156));
        importLabel.setText("Import data into Flapjack");

        openLabel.setText("Open previously accessed files:");

        project0.setForeground(new java.awt.Color(68, 106, 156));
        project0.setText("<project0>");

        project1.setForeground(new java.awt.Color(68, 106, 156));
        project1.setText("<project1>");

        project2.setForeground(new java.awt.Color(68, 106, 156));
        project2.setText("<project2>");

        project3.setForeground(new java.awt.Color(68, 106, 156));
        project3.setText("<project3>");

        rateLabel.setText("Click to rate Tablet:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openLabel)
                    .addComponent(importLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ratingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(openLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(project0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(project1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(project2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(project3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rateLabel)
                    .addComponent(ratingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel importLabel;
    private javax.swing.JLabel openLabel;
    private scri.commons.gui.matisse.HyperLinkLabel project0;
    private scri.commons.gui.matisse.HyperLinkLabel project1;
    private scri.commons.gui.matisse.HyperLinkLabel project2;
    private scri.commons.gui.matisse.HyperLinkLabel project3;
    private javax.swing.JLabel rateLabel;
    private scri.commons.gui.matisse.RatingsPanel ratingsPanel;
    // End of variables declaration//GEN-END:variables

}