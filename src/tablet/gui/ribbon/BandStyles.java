// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

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
	private JCommandToggleButton bReadType;
	private JCommandToggleButton bReadGroup;
	private JCommandToggleButton bText;

	private JCommandToggleButton bPacked, bStacked, bPairPacked, bPairStacked;

	private JCommandToggleButton bTagVariants;
	private JCommandButton bSort;
	private JCommandButton bPackStyle;

	private JCheckBoxMenuItem mPacked, mStacked, mPairPacked, mPairStacked;

	private JPopupMenu packMenu = new JPopupMenu();

	private StyleListener styleListener = new StyleListener();

	BandStyles(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandStyles.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		createRibbonGallery();
		createOptionButtons();
		createPackingPopupMenu();

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));
	}

	private void createOptionButtons()
	{
		bPackStyle = new JCommandButton("Pack Style", RibbonController.getIcon("PACKED16", 16));
		Actions.stylesPackStyles = new ActionRepeatableButtonModel(bPackStyle);
		Actions.stylesPackStyles.addActionListener(this);
		bPackStyle.setActionModel(Actions.stylesPackStyles);

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

		addCommandButton(bPackStyle, RibbonElementPriority.MEDIUM);
		addCommandButton(bTagVariants, RibbonElementPriority.MEDIUM);
//		addCommandButton(bSort, RibbonElementPriority.MEDIUM);
	}

	private void createRibbonGallery()
	{
		List<JCommandToggleButton> styleButtons = new ArrayList<JCommandToggleButton>();

		// Standard ("enhanced") colour scheme button
		boolean standardOn = Prefs.visColorScheme == ReadScheme.STANDARD;

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
		boolean directionOn = Prefs.visColorScheme == ReadScheme.DIRECTION;

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


		// Paired read type colour scheme button
		boolean readtypeOn = Prefs.visColorScheme == ReadScheme.READTYPE;

		bReadType = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bReadType"),
			RibbonController.getIcon("READTYPE32", 32));
		Actions.stylesReadType = new ActionToggleButtonModel(false);
		Actions.stylesReadType.setSelected(readtypeOn);
		Actions.stylesReadType.addActionListener(this);
		bReadType.setActionModel(Actions.stylesReadType);
		bReadType.setActionKeyTip("R");
		bReadType.addMouseListener(styleListener);
		bReadType.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bReadType.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bReadType.richtip")));
		styleButtons.add(bReadType);

		boolean readGroupOn = Prefs.visColorScheme == ReadScheme.READGROUP;

		bReadGroup = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandStyles.bReadGroup"),
			RibbonController.getIcon("READTYPE32", 32));
		Actions.stylesReadGroup = new ActionToggleButtonModel(false);
		Actions.stylesReadGroup.setSelected(readGroupOn);
		Actions.stylesReadGroup.addActionListener(this);
		bReadGroup.setActionModel(Actions.stylesReadGroup);
		bReadGroup.setActionKeyTip("R");
		bReadGroup.addMouseListener(styleListener);
		bReadGroup.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandStyles.bReadGroup.tooltip"),
			RB.getString("gui.ribbon.BandStyles.bReadGroup.richtip")));
		styleButtons.add(bReadGroup);


		// Text ("classic") colour scheme button
		boolean textOn = Prefs.visColorScheme == ReadScheme.CLASSIC;

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
		counts.put(RibbonElementPriority.LOW, 5);
		counts.put(RibbonElementPriority.MEDIUM, 5);
		counts.put(RibbonElementPriority.TOP, 5);

		List<StringValuePair<List<JCommandToggleButton>>> galleryButtons =
			new ArrayList<StringValuePair<List<JCommandToggleButton>>>();

		galleryButtons.add(
			new StringValuePair<List<JCommandToggleButton>>(null, styleButtons));
		addRibbonGallery(
			"Style", galleryButtons, counts, 6, 2, RibbonElementPriority.TOP);
	}

	// The listeners for the "live preview" styles track the previous style so
	// that if a new style wasn't selected, the original can be reinstated

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.stylesStandard)
		{
			setColorScheme(ReadScheme.STANDARD);
			styleListener.previousScheme = ReadScheme.STANDARD;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesStandard.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesDirection)
		{
			setColorScheme(ReadScheme.DIRECTION);
			styleListener.previousScheme = ReadScheme.DIRECTION;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesDirection.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesReadType)
		{
			setColorScheme(ReadScheme.READTYPE);
			styleListener.previousScheme = ReadScheme.READTYPE;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesReadType.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesReadGroup)
		{
			setColorScheme(ReadScheme.READGROUP);
			styleListener.previousScheme = ReadScheme.READGROUP;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesReadGroup.setSelected(true);
		}

		else if (e.getSource() == Actions.stylesText)
		{
			setColorScheme(ReadScheme.CLASSIC);
			styleListener.previousScheme = ReadScheme.CLASSIC;

			// BUG: Workaround for API allowing toggle groups to be unselected
			Actions.stylesText.setSelected(true);
		}

		else if(e.getSource() == Actions.stylesPackStyles)
		{
			handlePopup();
		}

		else if (e.getSource() == mPacked || e.getSource() == bPacked)
		{
			Prefs.visPacked = true;
			Prefs.visPaired = false;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mStacked || e.getSource() == bStacked)
		{
			Prefs.visPacked = false;
			Prefs.visPaired = false;
			Actions.overlayReadNames.setEnabled(true);

			winMain.getAssemblyPanel().updateDisplayData(false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mPairPacked || e.getSource() == bPairPacked)
		{
			Prefs.visPacked = true;
			Prefs.visPaired = true;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mPairStacked || e.getSource() == bPairStacked)
		{
			Prefs.visPacked = false;
			Prefs.visPaired = true;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false);
			winMain.getAssemblyPanel().forceRedraw();
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

	private void createPackingPopupMenu()
	{
		mPacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandStyles.bPacked"));
		mPacked.addActionListener(this);

		mStacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandStyles.bStacked"));
		mStacked.addActionListener(this);

		mPairPacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandStyles.bPairPacked"));
		mPairPacked.addActionListener(this);

		mPairStacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandStyles.bPairStacked"));
		mPairStacked.addActionListener(this);

		packMenu.add(mPacked);
		packMenu.add(mStacked);
		packMenu.addSeparator();
		packMenu.add(mPairPacked);
		packMenu.add(mPairStacked);
	}

	private void handlePopup()
	{
		mPacked.setSelected(Prefs.visPacked && !Prefs.visPaired);
		mStacked.setSelected(!Prefs.visPacked && !Prefs.visPaired);
		mPairPacked.setSelected(Prefs.visPacked && Prefs.visPaired);
		mPairStacked.setSelected(!Prefs.visPacked && Prefs.visPaired);

		packMenu.show(bPackStyle, 0, bPackStyle.getHeight());
	}

	public JCommandToggleButton getBStandard()
	{
		return bStandard;
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
				previousScheme != ReadScheme.STANDARD)
				setColorScheme(ReadScheme.STANDARD);

			else if (e.getSource() == bDirection && Actions.stylesDirection.isEnabled() &&
				previousScheme != ReadScheme.DIRECTION)
				setColorScheme(ReadScheme.DIRECTION);

			else if (e.getSource() == bReadType && Actions.stylesReadType.isEnabled() &&
				previousScheme != ReadScheme.READTYPE)
				setColorScheme(ReadScheme.READTYPE);

			else if (e.getSource() == bReadGroup && Actions.stylesReadGroup.isEnabled() &&
				previousScheme != ReadScheme.READGROUP)
				setColorScheme(ReadScheme.READGROUP);

			else if (e.getSource() == bText && Actions.stylesText.isEnabled() &&
				previousScheme != ReadScheme.CLASSIC)
				setColorScheme(ReadScheme.CLASSIC);
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