package tablet.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
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
				if (dataFlavors[i].getRepresentationClass().equals(
						Class.forName("java.util.List")))
				{
					List<?> list = (List<?>) t.getTransferData(dataFlavors[i]);

					if (list.size() == 1)
					{
						String filename = list.get(0).toString();

						winMain.getCommands().fileOpen(filename);
						dtde.dropComplete(true);
						return;
					}

					break;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}

	/*
	 * public void dropActionChanged(DropTargetDragEvent dtde) { }
	 *
	 * public void dragEnter(DropTargetDragEvent dtde) { }
	 *
	 * public void dragExit(DropTargetEvent dte) { }
	 *
	 * public void dragOver(DropTargetDragEvent dtde) { }
	 */
}
