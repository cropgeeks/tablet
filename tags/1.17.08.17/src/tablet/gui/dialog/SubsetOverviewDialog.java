// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class SubsetOverviewDialog extends JDialog implements ActionListener
{
	private SpinnerNumberModel fromModel, toModel;
	private SubsetOverviewPanelNB panel;
	private AssemblyPanel aPanel;
	private JButton bOK, bClose, bHelp;

	public SubsetOverviewDialog(WinMain winMain)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.SubsetOverviewDialog.title"),
			true
		);

		aPanel = winMain.getAssemblyPanel();

		// Setup the models with the appropriate values (translated into display coordinates)
		fromModel = new SpinnerNumberModel(aPanel.getOverviewCanvas().getOS()+1, aPanel.getContig().getVisualStart()+1, aPanel.getContig().getVisualEnd()+1, 1);
		toModel = new SpinnerNumberModel(aPanel.getOverviewCanvas().getOE()+1, aPanel.getContig().getVisualStart()+1, aPanel.getContig().getVisualEnd()+1, 1);

		// Setup the matisse panel for the dialog
		panel = new SubsetOverviewPanelNB(this, aPanel);
		panel.fromSpinner.setModel(fromModel);
		panel.toSpinner.setModel(toModel);

		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bClose);
		getRootPane().setDefaultButton(bOK);

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
		setVisible(true);
	}

	/**
	 * Create the button bar which can be seen at the bottom of the dialog.
	 */
	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.cancel"));
		bClose.addActionListener(this);
		bOK = new JButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		bHelp.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bOK);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == bOK && validates())
		{
			// translate display values back to data values and subset the overview
			aPanel.getOverviewCanvas().setSubset((Integer)panel.fromSpinner.getValue()-1, (Integer)panel.toSpinner.getValue()-1);
			setVisible(false);
		}

		else if(e.getSource() == bClose)
			setVisible(false);

		else if(e.getSource() == panel.bReset)
		{
			// Reset the overview using the initial values from the contig.
			panel.fromSpinner.setValue(aPanel.getContig().getVisualStart()+1);
			panel.toSpinner.setValue(aPanel.getContig().getVisualEnd()+1);
		}
	}

	boolean validates()
	{
		if((Integer)fromModel.getValue() > (Integer)toModel.getValue())
		{
			TaskDialog.error("You must ensure that the \"display bases from\" value is less than the \"to\" value.",
				RB.getString("gui.text.close"));
			return false;
		}
		return true;
	}
}