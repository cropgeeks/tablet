// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;

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

					String[] filenames = new String[list.size()];
					for (int fn = 0; fn < filenames.length; fn++)
						filenames[fn] = list.get(fn).toString();

					winMain.getCommands().fileOpen(filenames);
					dtde.dropComplete(true);

					return;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}
}