// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class JumpToDialog extends JDialog
	implements ActionListener, DocumentListener
{
	private AssemblyPanel aPanel;

	private JButton bClose, bHelp;
	private NBJumpToPanel nbPanel;

	// The indices within the dataset we'll ultimately try to jump to
	private int paddedIndex = 0;
	private int unpaddedIndex = 0;

	public JumpToDialog(WinMain winMain)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.JumpToDialog.title"),
			false
		);

		aPanel = winMain.getAssemblyPanel();
		nbPanel = new NBJumpToPanel(this, Prefs.guiJumpToBase);

//		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);
		addListeners();

		SwingUtils.addCloseHandler(this, bClose);

		// Set the default button
		if (Prefs.guiUsePaddedJumpToBases)
			getRootPane().setDefaultButton(nbPanel.bJumpPadded);
		else
			getRootPane().setDefaultButton(nbPanel.bJumpUnpadded);

		pack();
		setResizable(false);
		setLocationRelativeTo(winMain);

		// Position on screen...
		if (Prefs.guiJumpToX != -9999 || Prefs.guiJumpToY != -9999)
			setLocation(Prefs.guiJumpToX, Prefs.guiJumpToY);
	}

	private void addListeners()
	{
		// Tracks the position of the window if the user moves it
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiJumpToX = getLocation().x;
				Prefs.guiJumpToY = getLocation().y;
			}
		});

		// Updates the button states when the window regains focus
		addWindowFocusListener(new WindowAdapter()
		{
			public void windowGainedFocus(WindowEvent e)
				{ checkControls(); }
		});
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		TabletUtils.setHelp(bHelp, "gui.dialog.JumpToDialog");

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == nbPanel.bJumpPadded)
		{
			getRootPane().setDefaultButton(nbPanel.bJumpPadded);
			Prefs.guiUsePaddedJumpToBases = true;

			jumpToBase(paddedIndex);
		}

		else if (e.getSource() == nbPanel.bJumpUnpadded)
		{
			getRootPane().setDefaultButton(nbPanel.bJumpUnpadded);
			Prefs.guiUsePaddedJumpToBases = false;

			jumpToBase(unpaddedIndex);
		}
	}

	private void jumpToBase(int index)
	{
		if(Assembly.isBam())
		{
			aPanel.getAssembly().getBamBam().setBlockStart(aPanel.getContig(), index);
			aPanel.processBamDataChange();
		}

		aPanel.moveToPosition(-1, index, true);
//		new ColumnHighlighter(aPanel, index, index);
	}

	private void checkControls()
	{
		int base;

		try
		{
			base = Integer.parseInt(nbPanel.getInputText());
			Prefs.guiJumpToBase = base;
		}
		catch (NumberFormatException e)
		{
//			nbPanel.bJumpPadded.setEnabled(false);
//			nbPanel.bJumpUnpadded.setEnabled(false);

			return;
		}

		paddedIndex = unpaddedIndex = base-1;
		if(true)
			return;

		Contig contig = aPanel.getContig();

		// TODO-BAM ???
		// Work out the padded index for this base
//		paddedIndex = base + contig.getConsensusOffset() - 1;
//		if (paddedIndex < 0 || paddedIndex >= contig.getWidth())
//			nbPanel.bJumpPadded.setEnabled(false);
//		else
//			nbPanel.bJumpPadded.setEnabled(true);

		// Convert the unpadded index to a padded index
		unpaddedIndex = DisplayData.unpaddedToPadded(base-1);
//		nbPanel.bJumpUnpadded.setEnabled(unpaddedIndex != -1);
//		unpaddedIndex += contig.getConsensusOffset();
	}

	public void changedUpdate(DocumentEvent e)
		{ checkControls(); }

	public void insertUpdate(DocumentEvent e)
		{ checkControls(); }

	public void removeUpdate(DocumentEvent e)
		{ checkControls(); }
}