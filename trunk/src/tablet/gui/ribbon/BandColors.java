// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import java.util.*;

import tablet.gui.dialog.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;


public class BandColors extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	static JCommandToggleButton bStandard;
	static JCommandToggleButton bDirection;
	static JCommandToggleButton bReadType;
	static JCommandToggleButton bReadGroup;
	static JCommandToggleButton bReadLength;
	static JCommandToggleButton bVariants;
	static JCommandToggleButton bAtAllZooms;

	private JCommandButton bColors;

	private StyleListener styleListener = new StyleListener();

	BandColors(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandColors.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		createRibbonGallery();

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));
	}

	private void createRibbonGallery()
	{
		List<JCommandToggleButton> styleButtons = new ArrayList<>();

		// Standard ("enhanced") colour scheme button
		boolean standardOn = Prefs.visColorScheme == ReadScheme.STANDARD;

		bStandard = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bStandard"),
			RibbonController.getIcon("ENHANCED32", 32));
		Actions.colorsStandard = new ActionToggleButtonModel(false);
		Actions.colorsStandard.setSelected(standardOn);
		Actions.colorsStandard.addActionListener(this);
		bStandard.setActionModel(Actions.colorsStandard);
		bStandard.setActionKeyTip("E");
		bStandard.addMouseListener(styleListener);
		bStandard.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bStandard.tooltip"),
			RB.getString("gui.ribbon.BandColors.bStandard.richtip")));
		styleButtons.add(bStandard);


		// Orientation/direction colour scheme button
		boolean directionOn = Prefs.visColorScheme == ReadScheme.DIRECTION;

		bDirection = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bDirection"),
			RibbonController.getIcon("DIRECTION32", 32));
		Actions.colorsDirection = new ActionToggleButtonModel(false);
		Actions.colorsDirection.setSelected(directionOn);
		Actions.colorsDirection.addActionListener(this);
		bDirection.setActionModel(Actions.colorsDirection);
		bDirection.setActionKeyTip("D");
		bDirection.addMouseListener(styleListener);
		bDirection.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bDirection.tooltip"),
			RB.getString("gui.ribbon.BandColors.bDirection.richtip")));
		styleButtons.add(bDirection);


		// Paired read type colour scheme button
		boolean readtypeOn = Prefs.visColorScheme == ReadScheme.READTYPE;

		bReadType = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bReadType"),
			RibbonController.getIcon("READTYPE32", 32));
		Actions.colorsReadType = new ActionToggleButtonModel(false);
		Actions.colorsReadType.setSelected(readtypeOn);
		Actions.colorsReadType.addActionListener(this);
		bReadType.setActionModel(Actions.colorsReadType);
		bReadType.setActionKeyTip("T");
		bReadType.addMouseListener(styleListener);
		bReadType.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bReadType.tooltip"),
			RB.getString("gui.ribbon.BandColors.bReadType.richtip")));
		styleButtons.add(bReadType);


		// Read group colour scheme button
		boolean readGroupOn = Prefs.visColorScheme == ReadScheme.READGROUP;

		bReadGroup = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bReadGroup"),
			RibbonController.getIcon("READGROUP32", 32));
		Actions.colorsReadGroup = new ActionToggleButtonModel(false);
		Actions.colorsReadGroup.setSelected(readGroupOn);
		Actions.colorsReadGroup.addActionListener(this);
		bReadGroup.setActionModel(Actions.colorsReadGroup);
		bReadGroup.setActionKeyTip("G");
		bReadGroup.addMouseListener(styleListener);
		bReadGroup.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bReadGroup.tooltip"),
			RB.getString("gui.ribbon.BandColors.bReadGroup.richtip")));
		styleButtons.add(bReadGroup);

		// Read length colour scheme button
		boolean readLengthOn = Prefs.visColorScheme == ReadScheme.READLENGTH;

		bReadLength = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bReadLength"),
			RibbonController.getIcon("READLENGTH32", 32));
		Actions.colorsReadLength = new ActionToggleButtonModel(false);
		Actions.colorsReadLength.setSelected(readLengthOn);
		Actions.colorsReadLength.addActionListener(this);
		bReadLength.setActionModel(Actions.colorsReadLength);
		bReadLength.setActionKeyTip("L");
		bReadLength.addMouseListener(styleListener);
		bReadLength.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bReadLength.tooltip"),
			RB.getString("gui.ribbon.BandColors.bReadLength.richtip")));
		styleButtons.add(bReadLength);


		// Variants colour scheme button
		boolean variantsOn = Prefs.visColorScheme == ReadScheme.VARIANTS;

		bVariants = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bVariants"),
			RibbonController.getIcon("CLASSIC32", 32));
		Actions.colorsVariants = new ActionToggleButtonModel(false);
		Actions.colorsVariants.setSelected(variantsOn);
		Actions.colorsVariants.addActionListener(this);
		bVariants.setActionModel(Actions.colorsVariants);
		bVariants.setActionKeyTip("C");
		bVariants.addMouseListener(styleListener);
		bVariants.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bVariants.tooltip"),
			RB.getString("gui.ribbon.BandColors.bVariants.richtip")));
		styleButtons.add(bVariants);


		// Customise colours option
		bColors = new JCommandButton(
			RB.getString("gui.ribbon.BandColors.bColors"),
			RibbonController.getIcon("COLORIZE", 32));
		Actions.colorsCustom = new ActionRepeatableButtonModel(bColors);
		Actions.colorsCustom.addActionListener(this);
		bColors.setActionModel(Actions.colorsCustom);
		bColors.setActionKeyTip("C");
		bColors.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandColors.bColors.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandColors.bColors.richtip")));


		// Colors at all zooms toggle
		bAtAllZooms = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandColors.bAtAllZooms"),
			RibbonController.getIcon("COLORALLZOOMS32", 32));
		Actions.colorsAtAllZooms = new ActionToggleButtonModel(false);
		Actions.colorsAtAllZooms.setSelected(Prefs.visColorsAtAllZooms);
		Actions.colorsAtAllZooms.addActionListener(this);
		bAtAllZooms.setActionModel(Actions.colorsAtAllZooms);
		bAtAllZooms.setActionKeyTip("C");
		bAtAllZooms.addMouseListener(styleListener);
		bAtAllZooms.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandColors.bAtAllZooms.tooltip"),
			RB.getString("gui.ribbon.BandColors.bAtAllZooms.richtip")));



		// Set up the ribbon gallery (gawd knows what this code is doing)
		Map<RibbonElementPriority, Integer> counts = new HashMap<>();
		counts.put(RibbonElementPriority.LOW, 6);
		counts.put(RibbonElementPriority.MEDIUM, 6);
		counts.put(RibbonElementPriority.TOP, 6);

		List<StringValuePair<List<JCommandToggleButton>>> galleryButtons =
			new ArrayList<>();

		addCommandButton(bAtAllZooms, RibbonElementPriority.TOP);
		addCommandButton(bColors, RibbonElementPriority.TOP);
		galleryButtons.add(
			new StringValuePair<List<JCommandToggleButton>>(null, styleButtons));
		addRibbonGallery(
			"Style", galleryButtons, counts, 6, 2, RibbonElementPriority.TOP);
	}

	// The listeners for the "live preview" styles track the previous style so
	// that if a new style wasn't selected, the original can be reinstated

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.colorsStandard)
		{
			setColorScheme(ReadScheme.STANDARD);
			styleListener.previousScheme = ReadScheme.STANDARD;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsStandard.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsDirection)
		{
			setColorScheme(ReadScheme.DIRECTION);
			styleListener.previousScheme = ReadScheme.DIRECTION;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsDirection.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsReadType)
		{
			setColorScheme(ReadScheme.READTYPE);
			styleListener.previousScheme = ReadScheme.READTYPE;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsReadType.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsReadGroup)
		{
			setColorScheme(ReadScheme.READGROUP);
			styleListener.previousScheme = ReadScheme.READGROUP;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsReadGroup.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsReadLength)
		{
			setColorScheme(ReadScheme.READLENGTH);
			styleListener.previousScheme = ReadScheme.READLENGTH;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsReadLength.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsVariants)
		{
			setColorScheme(ReadScheme.VARIANTS);
			styleListener.previousScheme = ReadScheme.VARIANTS;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.colorsVariants.setSelected(true);
		}

		else if (e.getSource() == Actions.colorsCustom)
			customizeColors();

		else if (e.getSource() == Actions.colorsAtAllZooms)
		{
			Prefs.visColorsAtAllZooms = !Prefs.visColorsAtAllZooms;
			winMain.getAssemblyPanel().updateColorScheme();
		}
	}

	public void customizeColors()
	{
		new CustomizeColorsDialog(Tablet.winMain);
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
			if (e.getSource() == bStandard && Actions.colorsStandard.isEnabled() &&
				previousScheme != ReadScheme.STANDARD)
				setColorScheme(ReadScheme.STANDARD);

			else if (e.getSource() == bDirection && Actions.colorsDirection.isEnabled() &&
				previousScheme != ReadScheme.DIRECTION)
				setColorScheme(ReadScheme.DIRECTION);

			else if (e.getSource() == bReadType && Actions.colorsReadType.isEnabled() &&
				previousScheme != ReadScheme.READTYPE)
				setColorScheme(ReadScheme.READTYPE);

			else if (e.getSource() == bReadGroup && Actions.colorsReadGroup.isEnabled() &&
				previousScheme != ReadScheme.READGROUP)
				setColorScheme(ReadScheme.READGROUP);

			else if (e.getSource() == bReadLength && Actions.colorsReadLength.isEnabled() &&
				previousScheme != ReadScheme.READLENGTH)
				setColorScheme(ReadScheme.READLENGTH);

			else if (e.getSource() == bVariants && Actions.colorsVariants.isEnabled() &&
				previousScheme != ReadScheme.VARIANTS)
				setColorScheme(ReadScheme.VARIANTS);
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