// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.analysis.*;
import static tablet.analysis.ProteinTranslator.*;
import tablet.gui.*;

import scri.commons.gui.*;

class ProteinCanvasML extends MouseInputAdapter implements ActionListener
{
	private ProteinCanvas pCanvas;
	private ScaleCanvas sCanvas;
	private ReadsCanvas rCanvas;

	private JMenuItem mShowAll, mShowNone, mClipboard;
	private JCheckBoxMenuItem[] mToggleTracks;

	// Track selected at the time the menu pops up
	private int track;

	ProteinCanvasML(AssemblyPanel aPanel)
	{
		pCanvas = aPanel.proteinCanvas;
		sCanvas = aPanel.scaleCanvas;
		rCanvas = aPanel.readsCanvas;

		pCanvas.addMouseListener(this);
		pCanvas.addMouseMotionListener(this);

		new ReadsCanvasDragHandler(aPanel, pCanvas);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(null, e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(null, e);
	}

	public void mouseExited(MouseEvent e)
	{
		sCanvas.setMouseBase(null, null);
	}

	// Tracks the mouse over the protein canvas, showing the position and
	// the full name of the protein under the mouse
	public void mouseMoved(MouseEvent e)
	{
		int x = pCanvas.getMouseX(e);
		int ntIndex = rCanvas.getBaseForPixel(rCanvas.pX1 + x);

		try
		{
			// Work out which track the mouse is over
			int track = e.getY() / (rCanvas.ntH+1);
			track = getActualTrack(track);

			// And get the data value at that point
			short value = pCanvas.translations.get(track)[(ntIndex)];

			// Values greater than LBASE/RBASE are positions without the text
			if (value > RBASE) value -= RBASE;
			if (value > LBASE) value -= LBASE;

			// Values equal to zero don't have a value that can be displayed
			if (value > 0)
			{
				String msg = RB.getString("gui.viewer.ProteinCanvasML.p" + value);
				sCanvas.setMouseBase(ntIndex, msg);
			}
			else
				sCanvas.setMouseBase(ntIndex, null);
		}
		catch (Exception exception)
		{
			sCanvas.setMouseBase(ntIndex, null);
		}
	}

	// Create the popup menu items
	private void createPopupMenuItems(JPopupMenu menu, boolean clipboard)
	{
		if (clipboard)
		{
			mClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
			RB.setText(mClipboard, "gui.viewer.ProteinCanvasML.mClipboard");
			mClipboard.addActionListener(this);
			menu.add(mClipboard);
			menu.addSeparator();
		}

		mShowAll = new JMenuItem();
		RB.setText(mShowAll, "gui.viewer.ProteinCanvasML.mShowAll");
		mShowAll.addActionListener(this);
		mShowNone = new JMenuItem();
		RB.setText(mShowNone, "gui.viewer.ProteinCanvasML.mShowNone");
		mShowNone.addActionListener(this);

		menu.add(mShowAll);
		menu.add(mShowNone);
		menu.addSeparator();

		mToggleTracks = new JCheckBoxMenuItem[6];

		for (int i = 0; i < 6; i++)
		{
			mToggleTracks[i] = new JCheckBoxMenuItem("", pCanvas.enabled[i]);
			RB.setText(mToggleTracks[i],
				"gui.viewer.ProteinCanvasML.mToggle" + (i+1));
			mToggleTracks[i].addActionListener(this);
			menu.add(mToggleTracks[i]);

			if (i == 2)
				menu.addSeparator();
		}
	}

	void displayMenu(JComponent button, MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();
		createPopupMenuItems(menu, button == null);

		if (button != null)
		{
			int x = button.getX() - button.getWidth();
			int y = button.getY() + button.getHeight();
			menu.show(button, x, y);
		}
		else
		{
			track = e.getY() / (rCanvas.ntH+1);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mShowAll)
			for (int i = 0; i < pCanvas.enabled.length; i++)
				pCanvas.enabled[i] = true;

		else if (e.getSource() == mShowNone)
			for (int i = 0; i < pCanvas.enabled.length; i++)
				pCanvas.enabled[i] = false;

		for (int i = 0; i < 6; i++)
			if (e.getSource() == mToggleTracks[i])
				pCanvas.enabled[i] = !pCanvas.enabled[i];

		if (e != null && e.getSource() == mClipboard)
			copyToClipboard();

		else
			updateCanvas();
	}

	private void updateCanvas()
	{
		pCanvas.createTranslations();
		pCanvas.setDimensions();

		// Update the preferences string that tracks the enabled states
		Prefs.visProteins = new String();
		for (int i = 0; i < pCanvas.enabled.length; i++)
		{
			Prefs.visProteins += pCanvas.enabled[i] ? "1 " : "0 ";
			Actions.proteinEnable[i].setSelected(pCanvas.enabled[i]);
		}
	}

	// Called by the ribbon bar when the button states have changed
	void setStates(boolean[] states)
	{
		for (int i = 0; i < states.length; i++)
			pCanvas.enabled[i] = states[i];

		updateCanvas();
	}

	// Works out what the actual track (from 1-6) is when an onscreen track
	// is clicked on, as there can be any combination of onscreen tracks
	private int getActualTrack(int track)
	{
		int count = -1;

		for (int i = 0; i < pCanvas.enabled.length; i++)
		{
			if (pCanvas.enabled[i])
			{
				count++;
				if (count == track)
					return i;
			}
		}

		return 0;
	}

	private void copyToClipboard()
	{
		// Stores the symbol table -> human readable translation
		String[] codes = new ProteinTranslator().codes;

		// Work out what track was actually clicked on
		int actualTrack = getActualTrack(track);
		short[] translation = pCanvas.translations.get(actualTrack);

		StringBuilder str = new StringBuilder(translation.length/3);

		// NOTE: the translation array holds different numbers for the same
		// protein depending on whether it is the 1st/3rd digit or the 2nd.
		// Below, we are checking on the third which will be a value 22 higher
		// than the actual ProteinTranslator.codes[] mapping.

		// Translate forwards...
		if (actualTrack < 3)
		{
			for (int i = 0, found = 0; i < translation.length; i++)
			{
				if (translation[i] > 0)
					found++;

				if (found == 3)
				{
					str.append(codes[translation[i] - RBASE]);
					found = 0;
				}
			}
		}
		// Translate in reverse...
		else
		{
			for (int i = translation.length-1, found = 0; i >= 0; i--)
			{
				if (translation[i] > 0)
					found++;

				if (found == 3)
				{
					str.append(codes[translation[i] - LBASE]);
					found = 0;
				}
			}
		}

		// Get a suitable title for this translation
		String title = RB.format("gui.viewer.ProteinCanvasML.track"
			+ (actualTrack+1), rCanvas.contig.getName());

		// Convert the human-readable translation into FASTA format
		String text = TabletUtils.formatFASTA(title, str.toString());

		// And finally, copy to the clipboard
		StringSelection selection = new StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}
}