// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;
import tablet.gui.viewer.colors.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;


public class BandLayout extends JRibbonBand implements ActionListener
{
	private WinMain winMain;

	// Button for the Pack Styles and its drop-down menu options
	private JCommandButton bPackStyle;
	private JCheckBoxMenuItem mPacked, mStacked, mPairPacked, mPairStacked;

	// Button for the Color Schemes and its drop-down menu options
	private JCommandButton bColorScheme;
	private JCheckBoxMenuItem mEnhanced, mDirection, mReadType, mReadGroup;
	private JCheckBoxMenuItem mReadLength, mConcordance, mVariants;
	private JCheckBoxMenuItem mAtAllZooms;
	private JMenuItem mCustomize;

	private JCommandToggleButton bTagVariants;
//	private JCommandButton bSort;

	private JPopupMenu packMenu = new JPopupMenu();
	private JPopupMenu colorMenu = new JPopupMenu();


	BandLayout(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandLayout.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		createOptionButtons();

		createPackingPopupMenu();
		createColorsPopupMenu();

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));
	}

	private void createOptionButtons()
	{
		// Pack Style
		bPackStyle = new JCommandButton(
			RB.getString("gui.ribbon.BandLayout.bPackStyle"),
			RibbonController.getIcon("STACKED16", 16));
		Actions.stylesPackStyles = new ActionRepeatableButtonModel(bPackStyle);
		Actions.stylesPackStyles.addActionListener(this);
		bPackStyle.setActionModel(Actions.stylesPackStyles);
		bPackStyle.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandLayout.bPackStyle.tooltip"),
			RB.getString("gui.ribbon.BandLayout.bPackStyle.richtip")));

		// Color Scheme
		bColorScheme = new JCommandButton(
			RB.getString("gui.ribbon.BandLayout.bColorScheme"),
			RibbonController.getIcon("COLORS16", 16));
		Actions.stylesColorSchemes = new ActionRepeatableButtonModel(bColorScheme);
		Actions.stylesColorSchemes.addActionListener(this);
		bColorScheme.setActionModel(Actions.stylesColorSchemes);
		bColorScheme.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandLayout.bColorScheme.tooltip"),
			RB.getString("gui.ribbon.BandLayout.bColorScheme.richtip")));

		// "Tag" variants in the overviews
		bTagVariants = new JCommandToggleButton(
			RB.getString("gui.ribbon.BandLayout.bTagVariants"),
			RibbonController.getIcon("TAGVARIANTS16", 16));
		Actions.stylesTagVariants = new ActionToggleButtonModel(false);
		Actions.stylesTagVariants.setSelected(Prefs.visTagVariants);
		Actions.stylesTagVariants.addActionListener(this);
		bTagVariants.setActionModel(Actions.stylesTagVariants);
		bTagVariants.setActionKeyTip("T");
		bTagVariants.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandLayout.bTagVariants.tooltip"),
			RB.getString("gui.ribbon.BandLayout.bTagVariants.richtip")));

		// TODO: Sort
/*		bSort = new JCommandButton(
			RB.getString("gui.ribbon.BandLayout.bSort"),
			RibbonController.getIcon("SORT16", 16));
		bSort.setActionKeyTip("SO");
		bSort.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandLayout.bSort.tooltip"),
			RB.getString("gui.ribbon.BandLayout.bSort.richtip")));
		bSort.setEnabled(false);
*/

		addCommandButton(bPackStyle, RibbonElementPriority.MEDIUM);
		addCommandButton(bTagVariants, RibbonElementPriority.MEDIUM);
		addCommandButton(bColorScheme, RibbonElementPriority.MEDIUM);
//		addCommandButton(bSort, RibbonElementPriority.MEDIUM);
	}

	public void actionPerformed(ActionEvent e)
	{
		// Pack Menu Options
		if (e.getSource() == Actions.stylesPackStyles)
			handlePackPopup();

		else if (e.getSource() == mPacked)
		{
			Prefs.visPacked = true;
			Prefs.visPaired = false;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false, false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mStacked)
		{
			Prefs.visPacked = false;
			Prefs.visPaired = false;
			Actions.overlayReadNames.setEnabled(true);

			winMain.getAssemblyPanel().updateDisplayData(false, false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mPairPacked)
		{
			Prefs.visPacked = true;
			Prefs.visPaired = true;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false, false);
			winMain.getAssemblyPanel().forceRedraw();
		}

		else if (e.getSource() == mPairStacked)
		{
			Prefs.visPacked = false;
			Prefs.visPaired = true;
			Actions.overlayReadNames.setEnabled(false);

			winMain.getAssemblyPanel().updateDisplayData(false, false);
			winMain.getAssemblyPanel().forceRedraw();
		}


		// Tag Variants
		else if (e.getSource() == Actions.stylesTagVariants)
		{
			Prefs.visTagVariants = !Prefs.visTagVariants;
			winMain.getAssemblyPanel().forceRedraw();
		}


		// Colour scheme options
		else if (e.getSource() == Actions.stylesColorSchemes)
			handleColorsPopup();

		else if (e.getSource() == mEnhanced)
			BandColors.bStandard.doActionClick();
		else if (e.getSource() == mDirection)
			BandColors.bDirection.doActionClick();
		else if (e.getSource() == mReadType)
			BandColors.bReadType.doActionClick();
		else if (e.getSource() == mConcordance)
			BandColors.bConcordance.doActionClick();
		else if (e.getSource() == mReadGroup)
			BandColors.bReadGroup.doActionClick();
		else if (e.getSource() == mReadLength)
			BandColors.bReadLength.doActionClick();
		else if (e.getSource() == mVariants)
			BandColors.bVariants.doActionClick();
		else if (e.getSource() == mAtAllZooms)
			BandColors.bAtAllZooms.doActionClick();
		else if (e.getSource() == mCustomize)
			RibbonController.bandStyles.customizeColors();
	}

	private void createPackingPopupMenu()
	{
		mPacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandLayout.bPacked"));
		mPacked.addActionListener(this);

		mStacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandLayout.bStacked"));
		mStacked.addActionListener(this);

		mPairPacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandLayout.bPairPacked"));
		mPairPacked.addActionListener(this);

		mPairStacked = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandLayout.bPairStacked"));
		mPairStacked.addActionListener(this);

		packMenu.add(mPacked);
		packMenu.add(mStacked);
		packMenu.addSeparator();
		packMenu.add(mPairPacked);
		packMenu.add(mPairStacked);
	}

	private void handlePackPopup()
	{
		mPacked.setSelected(Prefs.visPacked && !Prefs.visPaired);
		mStacked.setSelected(!Prefs.visPacked && !Prefs.visPaired);
		mPairPacked.setSelected(Prefs.visPacked && Prefs.visPaired);
		mPairStacked.setSelected(!Prefs.visPacked && Prefs.visPaired);

		packMenu.show(bPackStyle, 0, bPackStyle.getHeight());
	}

	private void createColorsPopupMenu()
	{
		mEnhanced = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bStandard"));
		mEnhanced.addActionListener(this);

		mDirection = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bDirection"));
		mDirection.addActionListener(this);

		mReadType = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bReadType"));
		mReadType.addActionListener(this);

		mConcordance = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bConcordance"));
		mConcordance.addActionListener(this);

		mReadGroup = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bReadGroup"));
		mReadGroup.addActionListener(this);

		mReadLength = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bReadLength"));
		mReadLength.addActionListener(this);

		mVariants = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bVariants"));
		mVariants.addActionListener(this);

		mAtAllZooms = new JCheckBoxMenuItem(RB.getString("gui.ribbon.BandColors.bAtAllZooms2"));
		mAtAllZooms.addActionListener(this);

		mCustomize = new JMenuItem(RB.getString("gui.ribbon.BandColors.bColorsMenu"));
		mCustomize.addActionListener(this);

		colorMenu.add(mEnhanced);
		colorMenu.add(mDirection);
		colorMenu.add(mReadType);
		colorMenu.add(mReadGroup);
		colorMenu.add(mReadLength);
		colorMenu.add(mConcordance);
		colorMenu.add(mVariants);
		colorMenu.addSeparator();
		colorMenu.add(mAtAllZooms);
		colorMenu.addSeparator();
		colorMenu.add(mCustomize);
	}

	private void handleColorsPopup()
	{
		mEnhanced.setSelected(Prefs.visColorScheme == ReadScheme.STANDARD);
		mDirection.setSelected(Prefs.visColorScheme == ReadScheme.DIRECTION);
		mReadType.setSelected(Prefs.visColorScheme == ReadScheme.READTYPE);
		mConcordance.setSelected(Prefs.visColorScheme == ReadScheme.CONCORDANCE);
		mReadGroup.setSelected(Prefs.visColorScheme == ReadScheme.READGROUP);
		mReadLength.setSelected(Prefs.visColorScheme == ReadScheme.READLENGTH);
		mVariants.setSelected(Prefs.visColorScheme == ReadScheme.VARIANTS);
		mAtAllZooms.setSelected(Prefs.visColorsAtAllZooms);

		colorMenu.show(bColorScheme, 0, bColorScheme.getHeight());
	}
}