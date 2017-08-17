// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.analysis.*;

public class FindPanelNB extends javax.swing.JPanel
{
	private FindPanel panel;

    /** Creates new form NBFindPanel */
    public FindPanelNB(final FindPanel panel)
	{
        initComponents();

		this.panel = panel;

		// Setup the various visual components and fill with the correct data / options
		searchTypeCombo.addItem(RB.getString("gui.NBFindPanelControls.findLabel1"));
		searchTypeCombo.addItem(RB.getString("gui.NBFindPanelControls.findInConsensus"));
		searchTypeCombo.setSelectedIndex(Prefs.guiSearchType);
		searchTypeCombo.addActionListener(panel);

		resultsLabel.setText(RB.format("gui.NBFindPanelControls.resultsLabel", 0));
		RB.setText(helpLabel, "gui.NBFindPanelControls.helpLabel");
		RB.setText(label, "gui.NBFindPanelControls.label");

		findCombo.setHistory(Prefs.recentSearches);
		findCombo.setPrototypeDisplayValue("");

		findInCheckBox.setSelected(Prefs.guiFindPanelSearchCurrentContig);
		findInCheckBox.addActionListener(panel);

		RB.setText(checkUseRegex, "gui.NBFindPanelControls.checkUseRegex");
		checkUseRegex.setSelected(Prefs.guiRegexSearching);
		checkUseRegex.addActionListener(panel);

		RB.setText(checkIgnorePads, "gui.NBFindPanelControls.checkIgnorePads");
		checkIgnorePads.setSelected(Prefs.guiSearchIgnorePads);
		checkIgnorePads.addActionListener(panel);

		bFind.setText("");
		bFind.setIcon(Icons.getIcon("FIND"));
		bFind.addActionListener(panel);
		if(!SystemUtils.isMacOS())
			bFind.setBorder(BorderFactory.createEmptyBorder(0, 11, 0, 11));

		helpLabel.addActionListener(panel);

		// Setup the table with the desired properties
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(panel);

		// Setup a keyboard listener on  the findCombo combo box. This was to allow
		// the user to hit return from the combo box to run the search.
		findCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					panel.runSearch();
			}
		});

		toggleComponentEnabled(false);
    }

	// Toggle which components are enabled to ensure that the correct components
	// are enabled / disabled at any given time.
	void toggleComponentEnabled(boolean enabled)
	{
		label.setEnabled(enabled);
		bFind.setEnabled(enabled);
		findCombo.setEnabled(enabled);
		searchTypeCombo.setEnabled(enabled);
		resultsLabel.setEnabled(enabled);
//		table.getTableHeader().setVisible(enabled);
		findInCheckBox.setEnabled(enabled);

		if(searchTypeCombo.getSelectedIndex() != Finder.READ_NAME)
			checkUseRegex.setEnabled(false);
		else
			checkUseRegex.setEnabled(enabled);

		if (searchTypeCombo.getSelectedIndex() == Finder.READ_NAME)
			checkIgnorePads.setEnabled(false);
		else
			checkIgnorePads.setEnabled(enabled);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultsLabel = new javax.swing.JLabel();
        bFind = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new JTable() {
            public String getToolTipText(MouseEvent e) {
                return panel.getTableToolTip(e);
            }
        };
        helpLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        checkUseRegex = new javax.swing.JCheckBox();
        searchTypeCombo = new javax.swing.JComboBox<String>();
        label = new javax.swing.JLabel();
        checkIgnorePads = new javax.swing.JCheckBox();
        findInCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        findCombo = new scri.commons.gui.matisse.HistoryComboBox();

        resultsLabel.setText("Results:");

        bFind.setText("Find");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table);

        helpLabel.setForeground(new java.awt.Color(68, 106, 156));
        helpLabel.setText("Get help with searching");

        checkUseRegex.setText("Use Java regular expressions");

        label.setText("Search:");

        checkIgnorePads.setText("Ignore pads when searching");

        findInCheckBox.setText("Search in current contig only");

        jPanel1.setLayout(new java.awt.GridLayout());

        jPanel1.add(findCombo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(findInCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(helpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkUseRegex, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFind, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(searchTypeCombo, 0, 187, Short.MAX_VALUE)
                    .addComponent(checkIgnorePads)
                    .addComponent(resultsLabel))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(helpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bFind, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addComponent(searchTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(findInCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkUseRegex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkIgnorePads)
                .addGap(13, 13, 13)
                .addComponent(resultsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton bFind;
    javax.swing.JCheckBox checkIgnorePads;
    javax.swing.JCheckBox checkUseRegex;
    public scri.commons.gui.matisse.HistoryComboBox findCombo;
    javax.swing.JCheckBox findInCheckBox;
    scri.commons.gui.matisse.HyperLinkLabel helpLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
    public javax.swing.JLabel resultsLabel;
    javax.swing.JComboBox<String> searchTypeCombo;
    public javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

}