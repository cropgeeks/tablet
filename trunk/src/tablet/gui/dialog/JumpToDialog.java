package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class JumpToDialog extends JDialog
	implements ActionListener, DocumentListener
{
	private AssemblyPanel aPanel;

	private JButton bClose, bHelp;
	private NBJumpToPanel nbPanel;

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

		SwingUtils.addCloseHandler(this, bClose);

		// Set the default button (not always the same)
		if (Prefs.guiUsePaddedJumpToBases)
			getRootPane().setDefaultButton(nbPanel.bJumpPadded);
		else
			getRootPane().setDefaultButton(nbPanel.bJumpUnpadded);

		// TODO: position

		pack();
		setLocationRelativeTo(Tablet.winMain);
		setResizable(false);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "gui.dialog.NewViewDialog");

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

			jumpToBase();
		}

		else if (e.getSource() == nbPanel.bJumpUnpadded)
		{
			getRootPane().setDefaultButton(nbPanel.bJumpUnpadded);
			Prefs.guiUsePaddedJumpToBases = false;

			jumpToBase();
		}
	}

	private void jumpToBase()
	{
		int index = Prefs.guiJumpToBase - 1;

		// Move the base value relative to the start of the contig
		Contig contig = aPanel.getContig();
		Consensus con = contig.getConsensus();

//		if (Prefs.guiUsePaddedJumpToBases == false)
//			index = con.getUnpaddedPosition(index);

		index += contig.getConsensusOffset();

		aPanel.moveToPosition(-1, index, true);
		new ColumnHighlighter(aPanel, index);
	}

	private void checkControls()
	{
		// 0-indexed base to (attempt to) jump to
		int base;

		try
		{
			base = Integer.parseInt(nbPanel.getInputText());
		}
		catch (NumberFormatException e)
		{
			nbPanel.bJumpPadded.setEnabled(false);
			nbPanel.bJumpUnpadded.setEnabled(false);

			return;
		}

		Contig contig = aPanel.getContig();
		Consensus con = contig.getConsensus();

		// Work out the actual index within the data for this base
		int index = base + contig.getConsensusOffset() - 1;


//		// Certain unpadded jump values are always invalid
//		if (con.getUnpaddedPosition(base-1) == -1)
//			nbPanel.bJumpUnpadded.setEnabled(false);
//		else
//			nbPanel.bJumpUnpadded.setEnabled(true);

		// A padded jump will work, so long as it's within limits
		if (index < 0 || index >= contig.getWidth())
			nbPanel.bJumpPadded.setEnabled(false);
		else
			nbPanel.bJumpPadded.setEnabled(true);

		Prefs.guiJumpToBase = base;
	}

	public void changedUpdate(DocumentEvent e)
		{ checkControls(); }

	public void insertUpdate(DocumentEvent e)
		{ checkControls(); }

	public void removeUpdate(DocumentEvent e)
		{ checkControls(); }
}