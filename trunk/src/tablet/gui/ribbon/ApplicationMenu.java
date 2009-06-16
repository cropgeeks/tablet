package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.JCommandButton.CommandButtonKind;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.ribbon.*;

class ApplicationMenu extends RibbonApplicationMenu implements ActionListener
{
	private WinMain winMain;

	private ResizableIcon iNew;
	private RibbonApplicationMenuEntryPrimary mNew;
	private ResizableIcon iOpen;
	private RibbonApplicationMenuEntryPrimary mOpen;
	private ResizableIcon iClose;
	private RibbonApplicationMenuEntryPrimary mClose;

	private ResizableIcon iOptions;
	private RibbonApplicationMenuEntryFooter mOptions;
	private ResizableIcon iExit;
	private RibbonApplicationMenuEntryFooter mExit;

	ApplicationMenu(WinMain winMain)
	{
		this.winMain = winMain;

		this.

		// Primary menu options
		iNew = RibbonController.getIcon("FILENEW32", 32);
		mNew = new RibbonApplicationMenuEntryPrimary(iNew, "New", this,
			CommandButtonKind.ACTION_ONLY);
		mNew.setActionKeyTip("N");

		iOpen = RibbonController.getIcon("FILEOPEN32", 32);
		mOpen = new RibbonApplicationMenuEntryPrimary(iOpen, "Open", this,
			CommandButtonKind.ACTION_ONLY);
		mOpen.setActionKeyTip("O");

		iClose = RibbonController.getIcon("FILECLOSE32", 32);
		mClose = new RibbonApplicationMenuEntryPrimary(iClose, "Close", this,
			CommandButtonKind.ACTION_ONLY);
		mClose.setActionKeyTip("C");

		addMenuEntry(mNew);
		addMenuEntry(mOpen);
		addMenuEntry(mClose);


		// Footer menu options
		iOptions = RibbonController.getIcon("OPTIONS16", 16);
		mOptions = new RibbonApplicationMenuEntryFooter(iOptions, "Tablet Options", this);
		mOptions.setActionKeyTip("TO");

		iExit = RibbonController.getIcon("EXIT16", 16);
		mExit = new RibbonApplicationMenuEntryFooter(iExit, "Exit Tablet", this);
		mExit.setActionKeyTip("X");

		addFooterEntry(mOptions);
		addFooterEntry(mExit);
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

		else if (icon == iExit)
		{
			if (winMain.okToExit())
				winMain.exit();
		}
	}
}