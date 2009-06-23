package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.JCommandButton.CommandButtonKind;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;

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

	private ResizableIcon iOptions;
	private RibbonApplicationMenuEntryFooter mOptions;
	private ResizableIcon iExit;
	private RibbonApplicationMenuEntryFooter mExit;

	ApplicationMenu(WinMain winMain)
	{
		this.winMain = winMain;

		// Primary menu options
		iOpen = RibbonController.getIcon("FILEOPEN32", 32);
		mOpen = new RibbonApplicationMenuEntryPrimary(iOpen, "Open", this,
			CommandButtonKind.ACTION_ONLY);
		mOpen.setRolloverCallback(this);
		mOpen.setActionKeyTip("O");

		iSave = RibbonController.getIcon("FILESAVE32", 32);
		mSave = new RibbonApplicationMenuEntryPrimary(iSave, "Save", this,
			CommandButtonKind.ACTION_ONLY);
		mSave.setRolloverCallback(this);
		mSave.setActionKeyTip("S");

		bSave = new JCommandButton("Save", RibbonController.getIcon("FILESAVE16", 16));
		Actions.applicationMenuSave16 = new ActionRepeatableButtonModel(bSave);
		Actions.applicationMenuSave16.addActionListener(this);
		bSave.setActionModel(Actions.applicationMenuSave16);
		bSave.setActionKeyTip("2");

		iSaveAs = RibbonController.getIcon("FILESAVEAS32", 32);
		mSaveAs = new RibbonApplicationMenuEntryPrimary(iSaveAs, "Save As", this,
			CommandButtonKind.ACTION_ONLY);
		mSaveAs.setRolloverCallback(this);
		mSaveAs.setActionKeyTip("A");

		iClose = RibbonController.getIcon("FILECLOSE32", 32);
		mClose = new RibbonApplicationMenuEntryPrimary(iClose, "Close", this,
			CommandButtonKind.ACTION_ONLY);
		mClose.setRolloverCallback(this);
		mClose.setActionKeyTip("C");


		addMenuEntry(mOpen);
		addMenuEntry(mSave);
		addMenuEntry(mSaveAs);
		addMenuEntry(mClose);


		// Footer menu options
		iOptions = RibbonController.getIcon("OPTIONS16", 16);
		mOptions = new RibbonApplicationMenuEntryFooter(iOptions, "Tablet Options", this);
		mOptions.setActionKeyTip("TO");
		mOptions.setEnabled(false);

		iExit = RibbonController.getIcon("EXIT16", 16);
		mExit = new RibbonApplicationMenuEntryFooter(iExit, "Exit Tablet", this);
		mExit.setActionKeyTip("X");

		addFooterEntry(mOptions);
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
		for (final String path: Prefs.getRecentDocuments())
		{
			// Ignore any that haven't been set yet
			if (path.length() == 0)
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
					winMain.getCommands().fileOpen(path);
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
			winMain.getCommands().fileOpen(null);

		else if (icon == iSave || e.getSource() == Actions.applicationMenuSave16)
			System.out.println("Save");

		else if (icon == iSaveAs)
			System.out.println("Save As");

		else if (icon == iClose)
			winMain.closeAssembly();

		else if (icon == iExit)
		{
			if (winMain.okToExit())
				winMain.exit();
		}
	}
}