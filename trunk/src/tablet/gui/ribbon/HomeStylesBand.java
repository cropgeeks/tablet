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

public class HomeStylesBand extends JRibbonBand implements ActionListener
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

	HomeStylesBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeStylesBand.title"),
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
			RB.getString("gui.ribbon.HomeStylesBand.bPacked"),
			RibbonController.getIcon("PACKED16", 16));
		Actions.homeStylesPacked = new ActionToggleButtonModel(false);
		Actions.homeStylesPacked.setSelected(Prefs.visPacked);
		Actions.homeStylesPacked.addActionListener(this);
		bPacked.setActionModel(Actions.homeStylesPacked);
		bPacked.setActionKeyTip("P");
		bPacked.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bPacked.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bPacked.richtip")));

		// Set the display to use a stacked layout
		bStacked = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bStacked"),
			RibbonController.getIcon("STACKED16", 16));
		Actions.homeStylesStacked = new ActionToggleButtonModel(false);
		Actions.homeStylesStacked.setSelected(!Prefs.visPacked);
		Actions.homeStylesStacked.addActionListener(this);
		bStacked.setActionModel(Actions.homeStylesStacked);
		bStacked.setActionKeyTip("S");
		bStacked.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bStacked.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bStacked.richtip")));

		// "Tag" variants in the overviews
		bTagVariants = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bTagVariants"),
			RibbonController.getIcon("TAGVARIANTS16", 16));
		Actions.homeStylesTagVariants = new ActionToggleButtonModel(false);
		Actions.homeStylesTagVariants.setSelected(Prefs.visTagVariants);
		Actions.homeStylesTagVariants.addActionListener(this);
		bTagVariants.setActionModel(Actions.homeStylesTagVariants);
		bTagVariants.setActionKeyTip("T");
		bTagVariants.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bTagVariants.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bTagVariants.richtip")));

		// TODO: Sort
/*		bSort = new JCommandButton(
			RB.getString("gui.ribbon.HomeStylesBand.bSort"),
			RibbonController.getIcon("SORT16", 16));
		bSort.setActionKeyTip("SO");
		bSort.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bSort.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bSort.richtip")));
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
			RB.getString("gui.ribbon.HomeStylesBand.bStandard"),
			RibbonController.getIcon("ENHANCED32", 32));
		Actions.homeStylesStandard = new ActionToggleButtonModel(false);
		Actions.homeStylesStandard.setSelected(standardOn);
		Actions.homeStylesStandard.addActionListener(this);
		bStandard.setActionModel(Actions.homeStylesStandard);
		bStandard.setActionKeyTip("E");
		bStandard.addMouseListener(styleListener);
		bStandard.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bStandard.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bStandard.richtip")));
		styleButtons.add(bStandard);


		// Orientation/direction colour scheme button
		boolean directionOn = Prefs.visColorScheme == ColorScheme.DIRECTION;

		bDirection = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bDirection"),
			RibbonController.getIcon("DIRECTION32", 32));
		Actions.homeStylesDirection = new ActionToggleButtonModel(false);
		Actions.homeStylesDirection.setSelected(directionOn);
		Actions.homeStylesDirection.addActionListener(this);
		bDirection.setActionModel(Actions.homeStylesDirection);
		bDirection.setActionKeyTip("D");
		bDirection.addMouseListener(styleListener);
		bDirection.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bDirection.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bDirection.richtip")));
		styleButtons.add(bDirection);


		// Text ("classic") colour scheme button
		boolean textOn = Prefs.visColorScheme == ColorScheme.CLASSIC;

		bText = new JCommandToggleButton(
			RB.getString("gui.ribbon.HomeStylesBand.bText"),
			RibbonController.getIcon("CLASSIC32", 32));
		Actions.homeStylesText = new ActionToggleButtonModel(false);
		Actions.homeStylesText.setSelected(textOn);
		Actions.homeStylesText.addActionListener(this);
		bText.setActionModel(Actions.homeStylesText);
		bText.setActionKeyTip("C");
		bText.addMouseListener(styleListener);
		bText.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeStylesBand.bText.tooltip"),
			RB.getString("gui.ribbon.HomeStylesBand.bText.richtip")));
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
		if (e.getSource() == Actions.homeStylesStandard)
		{
			setColorScheme(ColorScheme.STANDARD);
			styleListener.previousScheme = ColorScheme.STANDARD;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.homeStylesStandard.setSelected(true);
		}

		else if (e.getSource() == Actions.homeStylesDirection)
		{
			setColorScheme(ColorScheme.DIRECTION);
			styleListener.previousScheme = ColorScheme.DIRECTION;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.homeStylesDirection.setSelected(true);
		}

		else if (e.getSource() == Actions.homeStylesText)
		{
			setColorScheme(ColorScheme.CLASSIC);
			styleListener.previousScheme = ColorScheme.CLASSIC;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.homeStylesText.setSelected(true);
		}

		else if (e.getSource() == Actions.homeStylesPacked)
		{
			Prefs.visPacked = true;
			Actions.homeOptionsOverlayReadNames.setEnabled(false);
			winMain.getAssemblyPanel().forceRedraw();

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.homeStylesPacked.setSelected(true);
		}

		else if (e.getSource() == Actions.homeStylesStacked)
		{
			Prefs.visPacked = false;
			Actions.homeOptionsOverlayReadNames.setEnabled(true);
			winMain.getAssemblyPanel().forceRedraw();

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.homeStylesStacked.setSelected(true);
		}

		else if (e.getSource() == Actions.homeStylesTagVariants)
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
			if (e.getSource() == bStandard && Actions.homeStylesStandard.isEnabled() &&
				previousScheme != ColorScheme.STANDARD)
				setColorScheme(ColorScheme.STANDARD);

			else if (e.getSource() == bDirection && Actions.homeStylesDirection.isEnabled() &&
				previousScheme != ColorScheme.DIRECTION)
				setColorScheme(ColorScheme.DIRECTION);

			else if (e.getSource() == bText && Actions.homeStylesText.isEnabled() &&
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