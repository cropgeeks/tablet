// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;

import javax.swing.*;

import tablet.io.*;

class FileDropAdapter extends DropTargetAdapter
{
	private WinMain winMain;

	FileDropAdapter(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void drop(DropTargetDropEvent dtde)
	{
		Transferable t = dtde.getTransferable();

		try
		{
			DataFlavor[] dataFlavors = t.getTransferDataFlavors();

			dtde.acceptDrop(DnDConstants.ACTION_COPY);

			for (int i = 0; i < dataFlavors.length; i++)
			{
				if (dataFlavors[i].isFlavorJavaFileListType())
				{
					List<?> list = (List<?>) t.getTransferData(dataFlavors[i]);

					final String[] filenames = new String[list.size()];
					for (int fn = 0; fn < filenames.length; fn++)
						filenames[fn] = list.get(fn).toString();

					// We thread this off, so that Windows doesn't hang after a
					// drag n drop from Explorer while Tablet actually loads
					// the data
					Runnable r = new Runnable() {
						public void run()
						{
							// Parse the list of filenames and turn them into a usable
							// TabletFile object that encapsulates this information
							TabletFile tabletFile = TabletFileHandler.createFromFileList(filenames);
							winMain.getCommands().fileOpen(tabletFile);
						}
					};

					SwingUtilities.invokeLater(r);

					dtde.dropComplete(true);
					return;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}
}