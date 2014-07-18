// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
	private JumpToPanelNB nbPanel;

	// The indices within the dataset we'll ultimately try to jump to
	private int padded = 0;
	private int unpadded = 0;

	private Contig contigToJumpTo;

	public JumpToDialog(WinMain winMain)
	{
		super(
			winMain,
			RB.getString("gui.dialog.JumpToDialog.title"),
			false
		);

		aPanel = winMain.getAssemblyPanel();
		nbPanel = new JumpToPanelNB(this, Prefs.guiJumpToBase);

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

		SwingUtils.positionWindow(
				this, winMain, Prefs.guiJumpToX, Prefs.guiJumpToY);
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

			jumpToPosition(padded);
		}

		else if (e.getSource() == nbPanel.bJumpUnpadded)
		{
			getRootPane().setDefaultButton(nbPanel.bJumpUnpadded);
			Prefs.guiUsePaddedJumpToBases = false;

			jumpToPosition(unpadded);
		}
	}

	private void jumpToPosition(int index)
	{
		if (contigToJumpTo != aPanel.getContig())
			Tablet.winMain.getContigsPanel().setContigInTable(contigToJumpTo.getName());

		aPanel.highlightColumn(index);
	}

	private void checkControls()
	{
		int base;
		String inputText = nbPanel.getInputText();
		contigToJumpTo = parseContig(inputText);

		if (contigToJumpTo != null)
			inputText = inputText.substring(inputText.lastIndexOf(":") + 1, inputText.length());

		try
		{
			base = Integer.parseInt(inputText);
			Prefs.guiJumpToBase = base;

			base = base - 1;
		}
		catch (NumberFormatException e)
		{
			nbPanel.bJumpPadded.setEnabled(false);
			nbPanel.bJumpUnpadded.setEnabled(false);

			return;
		}

		if (contigToJumpTo == null)
			contigToJumpTo = aPanel.getContig();

		padded = base;

		// Is it a valid padded index?
		if (padded < contigToJumpTo.getDataStart() || padded > contigToJumpTo.getDataEnd())
			nbPanel.bJumpPadded.setEnabled(false);
		else
			nbPanel.bJumpPadded.setEnabled(true);

		// Is it a valid unpadded index?
		unpadded = DisplayData.unpaddedToPadded(base);
		nbPanel.bJumpUnpadded.setEnabled(unpadded != -1 && unpadded < contigToJumpTo.getConsensus().getUnpaddedLength());
	}

	private Contig parseContig(String contigPosition)
	{
		String contigName = "";

		int index = contigPosition.lastIndexOf(":");
		// Check if there were any colons
		if (index != -1)
			// The rest of the string up to the colon must be the contig name
			contigName = contigPosition.substring(0, index);

		for (Contig contig : aPanel.getAssembly())
			if (contig.getName().equals(contigName))
				return contig;

		return null;
	}

	public void changedUpdate(DocumentEvent e)
		{ checkControls(); }

	public void insertUpdate(DocumentEvent e)
		{ checkControls(); }

	public void removeUpdate(DocumentEvent e)
		{ checkControls(); }
}