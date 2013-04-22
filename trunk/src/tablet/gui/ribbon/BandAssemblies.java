// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;
import tablet.gui.scanner.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;
import org.jvnet.flamingo.ribbon.resize.*;

import scri.commons.gui.*;

class BandAssemblies extends JRibbonBand implements ActionListener
{
	private WinMain winMain;
	private JCommandButton bOpen16, bOpen32;
	private JCommandButton bImportFeatures;
	private JCommandButton bImportEnzymes;
	private JCommandButton bScanner;

	BandAssemblies(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandAssemblies.title"),
			new EmptyResizableIcon(32));

		setResizePolicies(CoreRibbonResizePolicies.getCorePoliciesRestrictive(this));

		this.winMain = winMain;

		// Open an assembly (32x32 main button)
		bOpen32 = new JCommandButton(
			RB.getString("gui.ribbon.BandAssemblies.bOpen"),
			RibbonController.getIcon("FILEOPEN32", 32));
		Actions.assembliesOpen32 = new ActionRepeatableButtonModel(bOpen32);
		Actions.assembliesOpen32.addActionListener(this);
		bOpen32.setActionModel(Actions.assembliesOpen32);
		bOpen32.setActionKeyTip("O");
		bOpen32.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandAssemblies.bOpen.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandAssemblies.bOpen.richtip")));

		// Open an assembly (16x16 shortcut button)
		bOpen16 = new JCommandButton("",
			RibbonController.getIcon("FILEOPEN16", 16));
		Actions.assembliesOpen16 = new ActionRepeatableButtonModel(bOpen16);
		Actions.assembliesOpen16.addActionListener(this);
		bOpen16.setActionModel(Actions.assembliesOpen16);
		bOpen16.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandAssemblies.bOpen.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.BandAssemblies.bOpen.richtip")));
		bOpen16.setActionKeyTip("1");

		// Import features
		bImportFeatures = new JCommandButton(
			RB.getString("gui.ribbon.BandAssemblies.bImportFeatures"),
			RibbonController.getIcon("ATTACH32", 32));
		Actions.assembliesImportFeatures = new ActionRepeatableButtonModel(bImportFeatures);
		Actions.assembliesImportFeatures.addActionListener(this);
		bImportFeatures.setActionModel(Actions.assembliesImportFeatures);
		bImportFeatures.setActionKeyTip("F");
		bImportFeatures.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandAssemblies.bImportFeatures.tooltip"),
			RB.getString("gui.ribbon.BandAssemblies.bImportFeatures.richtip")));

		bScanner = new JCommandButton(
			RB.getString("gui.ribbon.BandAssemblies.bScanner"),
			RibbonController.getIcon("SCANNER16", 16));
		Actions.assembliesScanner = new ActionRepeatableButtonModel(bScanner);
		Actions.assembliesScanner.addActionListener(this);
		bScanner.setActionModel(Actions.assembliesScanner);
		bScanner.setActionKeyTip("AB");
		bScanner.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandAssemblies.bScanner.tooltip"),
			RB.getString("gui.ribbon.BandAssemblies.bScanner.richtip")));

		bImportEnzymes = new JCommandButton(
			RB.getString("gui.ribbon.BandAssemblies.bImportEnzymes"),
			RibbonController.getIcon("IMPORTENZYMES32", 32));
		Actions.assembliesImportEnzymes = new ActionRepeatableButtonModel(bImportEnzymes);
		Actions.assembliesImportEnzymes.addActionListener(this);
		bImportEnzymes.setActionModel(Actions.assembliesImportEnzymes);
		bImportEnzymes.setActionKeyTip("E");
		bImportEnzymes.setActionRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandAssemblies.bImportEnzymes.tooltip"),
			RB.getString("gui.ribbon.BandAssemblies.bImportEnzymes.richtip")));


		addCommandButton(bOpen32, RibbonElementPriority.TOP);
		addCommandButton(bImportFeatures, RibbonElementPriority.TOP);
		addCommandButton(bImportEnzymes, RibbonElementPriority.TOP);
//		addCommandButton(bScanner, RibbonElementPriority.MEDIUM);

		winMain.getRibbon().addTaskbarComponent(bOpen16);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == Actions.assembliesOpen32 ||
			e.getSource() == Actions.assembliesOpen16)
		{
			openAssembly();
		}

		else if (e.getSource() == Actions.assembliesImportFeatures)
			winMain.getCommands().importFeatures(null, true);

		else if (e.getSource() == Actions.assembliesScanner)
			new ScannerFrame();

		else if (e.getSource() == Actions.assembliesImportEnzymes)
			winMain.getRestrictionEnzymeDialog().setVisible(true);
	}

	void openAssembly()
	{
		winMain.getCommands().fileOpen(null);
	}
}