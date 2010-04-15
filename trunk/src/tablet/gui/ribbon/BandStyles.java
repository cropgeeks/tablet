// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import java.util.*;

import tablet.gui.*;
import tablet.gui.viewer.colors.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

public class BandStyles extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	private JCommandToggleButton bStandard;
	private JCommandToggleButton bDirection;
	private JCommandToggleButton bText;

	private CommandToggleButtonGroup group;
	private JCommandToggleButton bPacked;
	private JCommandToggleButton bStacked;
	private JCommandToggleButton bTagVariants;
	private JCommandButton bSort;

	private StyleListener styleListener = new StyleListener();

	BandStyles(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandStyles.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		createRibbonGallery();
		createOptionButtons();

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));
	}

	private void createOptionButtons()
	{
		// Set the display to use a packed layout
		bPacked = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bPacked"),
			RibbonController.getIcon("PACKED16", 16));
		Actions.stylesPacked = new ActionToggleButtonModel(false);
		Actions.stylesPacked.setSelected(Prefs.visPacked);
		Actions.stylesPacked.addActionListener(this);
		bPacked.setActionModel(Actions.stylesPacked);
		bPacked.setActionKeyTip("P");
		bPacked.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bPacked.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bPacked.richtip")));

		// Set the display to use a stacked layout
		bStacked = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bStacked"),
			RibbonController.getIcon("STACKED16", 16));
		Actions.stylesStacked = new ActionToggleButtonModel(false);
		Actions.stylesStacked.setSelected(!Prefs.visPacked);
		Actions.stylesStacked.addActionListener(this);
		bStacked.setActionModel(Actions.stylesStacked);
		bStacked.setActionKeyTip("S");
		bStacked.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bStacked.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bStacked.richtip")));

		// "Tag" variants in the overviews
		bTagVariants = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bTagVariants"),
			RibbonController.getIcon("TAGVARIANTS16", 16));
		Actions.stylesTagVariants = new ActionToggleButtonModel(false);
		Actions.stylesTagVariants.setSelected(Prefs.visTagVariants);
		Actions.stylesTagVariants.addActionListener(this);
		bTagVariants.setActionModel(Actions.stylesTagVariants);
		bTagVariants.setActionKeyTip("T");
		bTagVariants.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bTagVariants.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bTagVariants.richtip")));

		// TODO: Sort
/*		bSort = new JCommandButton(
			RB.getString("gui.ribbon.BandStyles.bSort"),
			RibbonController.getIcon("SORT16", 16));
		bSort.setActionKeyTip("SO");
		bSort.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bSort.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bSort.richtip")));
		bSort.setEnabled(false);
*/
		group = new CommandToggleButtonGroup();
		group.add(bPacked);
		group.add(bStacked);

//		startGroup();
		addCommandButton(bPacked, RibbonElementPriority.MEDIUM);
		addCommandButton(bStacked, RibbonElementPriority.MEDIUM);
		addCommandButton(bTagVariants, RibbonElementPriority.MEDIUM);
//		addCommandButton(bSort, RibbonElementPriority.MEDIUM);
	}

	private void createRibbonGallery()
	{
		List<JCommandToggleButton> styleButtons = new ArrayList<JCommandToggleButton>();

		// Standard ("enhanced") colour scheme button
		boolean standardOn = Prefs.visColorScheme == ColorScheme.STANDARD;

		bStandard = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bStandard"),
			RibbonController.getIcon("ENHANCED32", 32));
		Actions.stylesStandard = new ActionToggleButtonModel(false);
		Actions.stylesStandard.setSelected(standardOn);
		Actions.stylesStandard.addActionListener(this);
		bStandard.setActionModel(Actions.stylesStandard);
		bStandard.setActionKeyTip("E");
		bStandard.addMouseListener(styleListener);
		bStandard.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bStandard.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bStandard.richtip")));
		styleButtons.add(bStandard);


		// Orientation/direction colour scheme button
		boolean directionOn = Prefs.visColorScheme == ColorScheme.DIRECTION;

		bDirection = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bDirection"),
			RibbonController.getIcon("DIRECTION32", 32));
		Actions.stylesDirection = new ActionToggleButtonModel(false);
		Actions.stylesDirection.setSelected(directionOn);
		Actions.stylesDirection.addActionListener(this);
		bDirection.setActionModel(Actions.stylesDirection);
		bDirection.setActionKeyTip("D");
		bDirection.addMouseListener(styleListener);
		bDirection.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bDirection.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bDirection.richtip")));
		styleButtons.add(bDirection);


		// Text ("classic") colour scheme button
		boolean textOn = Prefs.visColorScheme == ColorScheme.CLASSIC;

		bText = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bText"),
			RibbonController.getIcon("CLASSIC32", 32));
		Actions.stylesText = new ActionToggleButtonModel(false);
		Actions.stylesText.setSelected(textOn);
		Actions.stylesText.addActionListener(this);
		bText.setActionModel(Actions.stylesText);
		bText.setActionKeyTip("C");
		bText.addMouseListener(styleListener);
		bText.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bText.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bText.richtip")));
		styleButtons.add(bText);


		// Set up the ribbon gallery (gawd knows what this code is doing)
		Map<RibbonElementPriority, Integer> counts =
			new HashMap<RibbonElementPriority, Integer>();
		counts.put(RibbonElementPriority.LOW, 3);
		counts.put(RibbonElementPriority.MEDIUM, 3);
		counts.put(RibbonElementPriority.TOP, 3);

		List<StringValuePair<List<JCommandToggleButton>>> galleryButtons =
			new ArrayList<StringValuePair<List<JCommandToggleButton>>>();

		galleryButtons.add(
			new StringValuePair<List<JCommandToggleButton>>(null, styleButtons));
		addRibbonGallery(
			"Style", galleryButtons, counts, 4, 4, RibbonElementPriority.TOP);
	}


	// The listeners for the "live preview" styles track the previous style so
	// that if a new style wasn't selected, the original can be reinstated

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.stylesStandard)
		{
			setColorScheme(ColorScheme.STANDARD);
			styleListener.previousScheme = ColorScheme.STANDARD;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesStandard.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesDirection)
		{
			setColorScheme(ColorScheme.DIRECTION);
			styleListener.previousScheme = ColorScheme.DIRECTION;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesDirection.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesText)
		{
			setColorScheme(ColorScheme.CLASSIC);
			styleListener.previousScheme = ColorScheme.CLASSIC;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesText.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesPacked)
		{
			Prefs.visPacked = true;
			Actions.optionsOverlayReadNames.setEnabled(false);
			winMain.getAssemblyPanel().forceRedraw();

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesPacked.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesStacked)
		{
			Prefs.visPacked = false;
			Actions.optionsOverlayReadNames.setEnabled(true);
			winMain.getAssemblyPanel().forceRedraw();

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesStacked.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesTagVariants)
		{
			Prefs.visTagVariants = !Prefs.visTagVariants;
			winMain.getAssemblyPanel().forceRedraw();
		}
	}

	private void setColorScheme(int scheme)
	{
		Prefs.visColorScheme = scheme;
		winMain.getAssemblyPanel().forceRedraw();
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
			if (e.getSource() == bStandard && Actions.stylesStandard.isEnabled() &&
				previousScheme != ColorScheme.STANDARD)
				setColorScheme(ColorScheme.STANDARD);

			else if (e.getSource() == bDirection && Actions.stylesDirection.isEnabled() &&
				previousScheme != ColorScheme.DIRECTION)
				setColorScheme(ColorScheme.DIRECTION);

			else if (e.getSource() == bText && Actions.stylesText.isEnabled() &&
				previousScheme != ColorScheme.CLASSIC)
				setColorScheme(ColorScheme.CLASSIC);
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