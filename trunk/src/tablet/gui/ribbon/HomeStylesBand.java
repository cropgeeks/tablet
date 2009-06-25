package tablet.gui.ribbon;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import tablet.gui.viewer.*;
import tablet.gui.viewer.colors.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class HomeStylesBand extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bStandard;
	private JCommandToggleButton bText;

	private StyleListener styleListener = new StyleListener();

	HomeStylesBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeStylesBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		createRibbonGallery();
	}

	private void createRibbonGallery()
	{
		List<JCommandToggleButton> styleButtons = new ArrayList<JCommandToggleButton>();

		// Standard ("enhanced") colour scheme button
		boolean standardOn = Prefs.visColorScheme == ColorScheme.STANDARD;

		bStandard = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bStandard"),
			RibbonController.getIcon("ENHANCED32", 32));
		Actions.homeStylesStandard = new ActionToggleButtonModel(false);
		Actions.homeStylesStandard.setSelected(standardOn);
		Actions.homeStylesStandard.addActionListener(this);
		bStandard.setActionModel(Actions.homeStylesStandard);
		bStandard.setActionKeyTip("E");
		bStandard.addMouseListener(styleListener);
//		bStandard.setActionRichTooltip(new RichTooltip(
//			RB.getString("gui.ribbon.HomeStylesBand.bStandard.tooltip"),
//			RB.getString("gui.ribbon.HomeStylesBand.bStandard.richtip")));
		styleButtons.add(bStandard);


		// Text ("classic" colour scheme button
		boolean textOn = Prefs.visColorScheme == ColorScheme.TEXT;

		bText = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bText"),
			RibbonController.getIcon("CLASSIC32", 32));
		Actions.homeStylesText = new ActionToggleButtonModel(false);
		Actions.homeStylesText.setSelected(textOn);
		Actions.homeStylesText.addActionListener(this);
		bText.setActionModel(Actions.homeStylesText);
		bText.setActionKeyTip("C");
		bText.addMouseListener(styleListener);
//		bText.setActionRichTooltip(new RichTooltip(
//			RB.getString("gui.ribbon.HomeStylesBand.bText.tooltip"),
//			RB.getString("gui.ribbon.HomeStylesBand.bText.richtip")));
		styleButtons.add(bText);


		// Set up the ribbon gallery (gawd knows what this code is doing)
		Map<RibbonElementPriority, Integer> counts =
			new HashMap<RibbonElementPriority, Integer>();
		counts.put(RibbonElementPriority.LOW, 2);
		counts.put(RibbonElementPriority.MEDIUM,2);
		counts.put(RibbonElementPriority.TOP, 2);

		List<StringValuePair<List<JCommandToggleButton>>> galleryButtons =
			new ArrayList<StringValuePair<List<JCommandToggleButton>>>();

		galleryButtons.add(
			new StringValuePair<List<JCommandToggleButton>>(null, styleButtons));
		addRibbonGallery(
			"Style", galleryButtons, counts, 3, 1, RibbonElementPriority.TOP);
	}


	// The listeners for the "live preview" styles track the previous style so
	// that if a new style wasn't selected, the original can be reinstated

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.homeStylesStandard)
		{
			setColorScheme(ColorScheme.STANDARD);
			styleListener.previousScheme = ColorScheme.STANDARD;
		}

		else if (e.getSource() == Actions.homeStylesText)
		{
			setColorScheme(ColorScheme.TEXT);
			styleListener.previousScheme = ColorScheme.TEXT;
		}
	}

	private void setColorScheme(int scheme)
	{
		Prefs.visColorScheme = scheme;
		winMain.getAssemblyPanel().colorSchemeChanged();
	}

	// Tracks the mouse entering or exiting the colour scheme style buttons
	private class StyleListener extends MouseAdapter
	{
		int previousScheme = Prefs.visColorScheme;

		// On mouse enter we want to check that the button is enabled and that
		// the scheme is actually different, otherwise it's a waste of time
		// changing to it (and would result in the overview redrawing)
		public void mouseEntered(MouseEvent e)
		{
			if (e.getSource() == bStandard && bStandard.isEnabled() &&
				previousScheme != ColorScheme.STANDARD)
				setColorScheme(ColorScheme.STANDARD);

			else if (e.getSource() == bText && bText.isEnabled() &&
				previousScheme != ColorScheme.TEXT)
				setColorScheme(ColorScheme.TEXT);
		}

		// On mouse exit we can just reinstate the previous scheme (if it's not
		// already the scheme in use)
		public void mouseExited(MouseEvent e)
		{
			if (Prefs.visColorScheme != previousScheme)
				setColorScheme(previousScheme);
		}
	}
}