// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.Toolkit;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

public class ConsensusSubsequenceDialog extends JDialog implements ActionListener
{
	private JButton bCancel, bCopy, bHelp;
	private ConsensusSubsequencePanelNB controls;
	private AssemblyPanel aPanel;

	public ConsensusSubsequenceDialog(WinMain winMain)
	{
		super(
			winMain,
			RB.getString("gui.dialog.ConsensusSubsequenceDialog.title"),
			true
		);

		this.aPanel = winMain.getAssemblyPanel();

		controls = new ConsensusSubsequencePanelNB(this);

		Contig contig = aPanel.getContig();
		updateModel(contig);

		add(controls);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bCancel);
		getRootPane().setDefaultButton(bCopy);

		pack();
		setResizable(false);
		setLocationRelativeTo(winMain);
	}

	/**
	 * Called to update the spinner number models with the start and end
	 * positions of the current contig (or BAM window).
	 */
	public void updateModel(Contig contig)
	{
		int vS = contig.getVisualStart() + 1;
		int start = 1 < vS ? vS : 1;

		int vE = contig.getVisualEnd() + 1;
		int end = contig.getConsensus().length() > vE ? vE : contig.getConsensus().length();
		
		controls.spinnerStartBase.setModel(new SpinnerNumberModel(start, start, end, 1));
		controls.spinnerEndBase.setModel(new SpinnerNumberModel(end, start, end, 1));
	}

	private JPanel createButtons()
	{
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		bCopy = SwingUtils.getButton(RB.getString("gui.dialog.ConsensusSubsequenceDialog"));
		bCopy.addActionListener(this);

		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		TabletUtils.setHelp(bHelp, "gui.dialog.ConsensusSubsequenceDialog");
		
		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bCopy);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCopy)
		{
			SpinnerNumberModel start = (SpinnerNumberModel)controls.spinnerStartBase.getModel();
			SpinnerNumberModel end = (SpinnerNumberModel)controls.spinnerEndBase.getModel();

			// Get contig name and consensus / reference string
			String name = aPanel.getContig().getName();
			String consensus = aPanel.getContig().getConsensus().toString();

			// Carry out the substring operation and format data as fasta
			String seq = consensus.substring((Integer)start.getNumber()-1, (Integer)end.getNumber());
			String text = TabletUtils.formatFASTA(name, seq);

			StringSelection selection = new StringSelection(text);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);

			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}
}