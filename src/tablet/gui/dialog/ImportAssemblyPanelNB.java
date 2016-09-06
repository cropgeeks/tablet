// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import tablet.gui.*;
import tablet.io.*;
import static tablet.io.AssemblyFile.*;

import scri.commons.gui.*;

class ImportAssemblyPanelNB extends JPanel implements DocumentListener
{
	private Document doc1, doc2;

	private FileProcessor fileProcessor;

    ImportAssemblyPanelNB(ImportAssemblyDialog parent)
	{
		initComponents();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		TabletUtils.setPanelColor(this, true);
		TabletUtils.setPanelColor(panel1, false);
		TabletUtils.setPanelColor(panel2, false);

		// Apply i18n text to the controls
		panel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBImportAssemblyPanel.panel1")));
		panel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBImportAssemblyPanel.panel2")));
		RB.setText(note1, "gui.dialog.NBImportAssemblyPanel.note1");
		RB.setText(note2, "gui.dialog.NBImportAssemblyPanel.note2");
		RB.setText(assLabel, "gui.dialog.NBImportAssemblyPanel.assLabel");
		RB.setText(refLabel, "gui.dialog.NBImportAssemblyPanel.refLabel");
		RB.setText(bBrowse1, "gui.text.browse");
		RB.setText(bBrowse2, "gui.text.browse");
		RB.setText(statusLabel, "gui.dialog.NBImportAssemblyPanel.statusLabel");
		RB.setText(exampleLabel, "gui.dialog.NBImportAssemblyPanel.exampleLabel");

		bBrowse1.addActionListener(parent);
		bBrowse2.addActionListener(parent);
		exampleLabel.addActionListener(parent);

		file1Combo.setHistory(TabletFileHandler.getAssemblyHistory());
		file2Combo.setHistory(TabletFileHandler.getReferenceHistory());
		file1Combo.setPrototypeDisplayValue("");
		file2Combo.setPrototypeDisplayValue("");

		if (TabletFileHandler.mruHasReference() == false)
			((JTextComponent) file2Combo.getEditor().getEditorComponent()).setText("");

		doc1 = ((JTextComponent) file1Combo.getEditor().getEditorComponent()).getDocument();
		doc2 = ((JTextComponent) file2Combo.getEditor().getEditorComponent()).getDocument();
		doc1.addDocumentListener(this);
		doc2.addDocumentListener(this);

		processFiles();
    }

	// Returns true if using a reference sequence is POSSIBLE and PROVIDED
    boolean isUsingReference()
    {
    	return (file2Combo.isEnabled() && doc2.getLength() > 0);
    }

    // Returns true if using a reference sequence is POSSIBLE but IGNORED
    boolean isIgnoringReference()
    {
    	return (file2Combo.isEnabled() && doc2.getLength() == 0);
    }

    private void setReferenceControls(boolean state)
    {
    	refLabel.setEnabled(state);
    	file2Combo.setEnabled(state);
    	bBrowse2.setEnabled(state);
    }

    private void processFiles()
    {
    	if (fileProcessor != null)
    	{
    		fileProcessor.setPriority(Thread.MIN_PRIORITY);
    		fileProcessor.okToRun = false;
    	}

    	statusText.setText(
    		RB.getString("gui.dialog.NBImportAssembly.ass.status") + "  |  " +
    		RB.getString("gui.dialog.NBImportAssembly.ref.status"));

    	fileProcessor = new FileProcessor();
    	fileProcessor.start();
    }

    private class FileProcessor extends Thread
    {
    	Boolean okToRun = true;

    	public void run()
    	{
	    	int t1 = UNKNOWN, t2 = UNKNOWN;

	    	try
	    	{
	    		// Determine the types of the two files (regardless of whether we'll
	    		// actually accept them or not
	    		String filename1 = doc1.getText(0, doc1.getLength());
	    		t1 = AssemblyFileHandler.getType(filename1, okToRun);
	    	}
	    	catch (Exception e) {}

	    	if (fileProcessor != this) return;

	    	// The assembly file must be ACE, AFG, SAM, MAQ, or SOAP
	    	if (t1 >= FASTA)
	    		t1 = UNKNOWN;

	    	// The reference option is only needed for SAM onwards
	    	setReferenceControls(t1 >= SAM && t1 < FASTA);

	    	String str1 = RB.getString("gui.dialog.NBImportAssembly.ass.status" + t1);
	    	String str2 = RB.getString("gui.dialog.NBImportAssembly.ref.status");

	    	if (refLabel.isEnabled())
				statusText.setText(str1 + "  |  " + str2);
			else
			{
				statusText.setText(str1);
				return;
			}

			try
			{
				String filename2 = doc2.getText(0, doc2.getLength());
			    	t2 = AssemblyFileHandler.getType(filename2, okToRun);
	    	}
	    	catch (Exception e) {}

	    	if (fileProcessor != this) return;

	    	// The reference file must be FASTA or FASTQ
	    	if (t2 < FASTA)
		    	t2 = UNKNOWN;

		    str2 = RB.getString("gui.dialog.NBImportAssembly.ref.status" + t2);

	    	statusText.setText(str1 + "  |  " + str2);
    	}
    }

    public void insertUpdate(DocumentEvent e)
    	{ processFiles(); }

    public void removeUpdate(DocumentEvent e)
    	{ processFiles(); }

    public void changedUpdate(DocumentEvent e)
    	{}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        bBrowse2 = new javax.swing.JButton();
        bBrowse1 = new javax.swing.JButton();
        statusText = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        refLabel = new javax.swing.JLabel();
        assLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        file1Combo = new scri.commons.gui.matisse.HistoryComboBox();
        jPanel2 = new javax.swing.JPanel();
        file2Combo = new scri.commons.gui.matisse.HistoryComboBox();
        panel2 = new javax.swing.JPanel();
        note1 = new javax.swing.JLabel();
        note2 = new javax.swing.JLabel();
        exampleLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select assembly files:"));

        bBrowse2.setText("Browse...");

        bBrowse1.setText("Browse...");

        statusText.setText("<status>");

        statusLabel.setText("Current Status:");

        refLabel.setLabelFor(file2Combo);
        refLabel.setText("Reference/consensus file or URL (optional):");

        assLabel.setLabelFor(file1Combo);
        assLabel.setText("Primary assembly file or URL:");

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout());

        jPanel1.add(file1Combo);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout());

        jPanel2.add(file2Combo);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refLabel)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusText))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(assLabel)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 445, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(bBrowse2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bBrowse1))))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(assLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(refLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(bBrowse2)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes:"));

        note1.setText("Tablet currently supports ACE, AFG, MAQ (text), SOAP, SAM and (indexed) BAM assemblies.");

        note2.setText("Reference files (if needed for MAQ, SOAP, SAM and BAM) can be in FASTA or FASTQ format.");

        exampleLabel.setText("exampleLabel");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(note1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(note2, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(exampleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(note1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(note2)
                .addGap(18, 18, 18)
                .addComponent(exampleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel assLabel;
    javax.swing.JButton bBrowse1;
    javax.swing.JButton bBrowse2;
    scri.commons.gui.matisse.HyperLinkLabel exampleLabel;
    scri.commons.gui.matisse.HistoryComboBox file1Combo;
    scri.commons.gui.matisse.HistoryComboBox file2Combo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel note1;
    private javax.swing.JLabel note2;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JLabel refLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusText;
    // End of variables declaration//GEN-END:variables

}