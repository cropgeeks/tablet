package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import tablet.gui.dialog.*;
import tablet.gui.dialog.prefs.*;
import tablet.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.JCommandButton.CommandButtonKind;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class ApplicationMenu extends RibbonApplicationMenu
	implements ActionListener, RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback
{
	private WinMain winMain;

	private ResizableIcon iOpen;
	private RibbonApplicationMenuEntryPrimary mOpen;
	private ResizableIcon iClose;
	public static RibbonApplicationMenuEntryPrimary mSave;
	public static JCommandButton bSave;
	private ResizableIcon iSave;
	public static RibbonApplicationMenuEntryPrimary mSaveAs;
	private ResizableIcon iSaveAs;
	public static RibbonApplicationMenuEntryPrimary mClose;

	private ResizableIcon iAbout;
	private RibbonApplicationMenuEntryFooter mAbout;
	private ResizableIcon iOptions;
	private RibbonApplicationMenuEntryFooter mOptions;
	private ResizableIcon iExit;
	private RibbonApplicationMenuEntryFooter mExit;

	ApplicationMenu(WinMain winMain)
	{
		this.winMain = winMain;

		// Primary menu options
		iOpen = RibbonController.getIcon("FILEOPEN32", 32);
		mOpen = new RibbonApplicationMenuEntryPrimary(iOpen,
			RB.getString("gui.ribbon.ApplicationMenu.mOpen"), this,
			CommandButtonKind.ACTION_ONLY);
		mOpen.setRolloverCallback(this);
		mOpen.setActionKeyTip("O");

		iSave = RibbonController.getIcon("FILESAVE32", 32);
		mSave = new RibbonApplicationMenuEntryPrimary(iSave,
			RB.getString("gui.ribbon.ApplicationMenu.mSave"), this,
			CommandButtonKind.ACTION_ONLY);
		mSave.setRolloverCallback(this);
		mSave.setActionKeyTip("S");

		bSave = new JCommandButton("",
			RibbonController.getIcon("FILESAVE16", 16));
		Actions.applicationMenuSave16 = new ActionRepeatableButtonModel(bSave);
		Actions.applicationMenuSave16.addActionListener(this);
		bSave.setActionModel(Actions.applicationMenuSave16);
		bSave.setActionKeyTip("2");
		bSave.setActionRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.ApplicationMenu.bSave.tooltip", Tablet.winKey),
			RB.getString("gui.ribbon.ApplicationMenu.bSave.richtip")));

		iSaveAs = RibbonController.getIcon("FILESAVEAS32", 32);
		mSaveAs = new RibbonApplicationMenuEntryPrimary(iSaveAs,
			RB.getString("gui.ribbon.ApplicationMenu.mSaveAs"), this,
			CommandButtonKind.ACTION_ONLY);
		mSaveAs.setRolloverCallback(this);
		mSaveAs.setActionKeyTip("A");

		iClose = RibbonController.getIcon("FILECLOSE32", 32);
		mClose = new RibbonApplicationMenuEntryPrimary(iClose,
			RB.getString("gui.ribbon.ApplicationMenu.mClose"), this,
			CommandButtonKind.ACTION_ONLY);
		mClose.setRolloverCallback(this);
		mClose.setActionKeyTip("C");


		addMenuEntry(mOpen);
		addMenuEntry(mSave);
		addMenuEntry(mSaveAs);
		addMenuEntry(mClose);


		// Footer menu options
		iAbout = RibbonController.getIcon("APPICON16", 16);
		mAbout = new RibbonApplicationMenuEntryFooter(iAbout,
			RB.getString("gui.ribbon.ApplicationMenu.mAbout"), this);
		mAbout.setActionKeyTip("TA");

		iOptions = RibbonController.getIcon("OPTIONS16", 16);
		mOptions = new RibbonApplicationMenuEntryFooter(iOptions,
			RB.getString("gui.ribbon.ApplicationMenu.mOptions"), this);
		mOptions.setActionKeyTip("TO");

		iExit = RibbonController.getIcon("EXIT16", 16);
		mExit = new RibbonApplicationMenuEntryFooter(iExit,
			RB.getString("gui.ribbon.ApplicationMenu.mExit"), this);
		mExit.setActionKeyTip("X");

		if (SystemUtils.isMacOS() == false)
		{
			addFooterEntry(mAbout);
			addFooterEntry(mOptions);
		}

		addFooterEntry(mExit);

		winMain.getRibbon().addTaskbarComponent(bSave);
	}

	// Creates the application menu's list of recently opened documents
	public void menuEntryActivated(JPanel targetPanel)
	{
		targetPanel.removeAll();

		JCommandButtonPanel recentPanel = new JCommandButtonPanel(CommandButtonDisplayState.MEDIUM);
		String groupName = "Recent Documents";
		recentPanel.addButtonGroup(groupName);

		// Parse the list of recent documents
		for (final String path: Prefs.guiRecentDocs)
		{
			// Ignore any that haven't been set yet
			if (path == null || path.equals(" "))
				continue;

			File file = new File(path);

			// Make the button
			JCommandButton button = new JCommandButton(file.getName(),
				RibbonController.getIcon("DOCUMENTS16", 16));
			button.setHorizontalAlignment(SwingUtilities.LEFT);
			recentPanel.addButtonToLastGroup(button);

			// And give it an action
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					winMain.getCommands().fileOpen(new String[] { path });
				}
			});
		}

		recentPanel.setMaxButtonColumns(1);
		targetPanel.setLayout(new BorderLayout());
		targetPanel.add(recentPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e)
	{
		// Because of the way the ribbon API works, you can't easily get back
		// to the source component from this event (we create a MenuEntry but
		// the event generates a JCommandButton). The work-a-round way is to
		// check the icon for the button against the original that was created
		ResizableIcon icon = ((JCommandButton)e.getSource()).getIcon();

		if (icon == iOpen)
		{
			// Work-around for OS X not liking its heavyweight file dialog being
			// opened on top of the ribbon's application menu
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					winMain.getCommands().fileOpen(null);
			}});
		}

		else if (icon == iSave || e.getSource() == Actions.applicationMenuSave16)
			System.out.println("Save");

		else if (icon == iSaveAs)
			System.out.println("Save As");

		else if (icon == iClose)
			winMain.closeAssembly();

		else if (icon == iAbout)
			new AboutDialog();

		else if (icon == iOptions)
			new PreferencesDialog();

		else if (icon == iExit)
		{
			if (winMain.okToExit())
				winMain.exit();
		}
	}
}