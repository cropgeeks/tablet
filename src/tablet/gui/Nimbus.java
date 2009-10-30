// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import javax.swing.*;

class Nimbus
{
	private static final Color BACKGROUND = new Color(240, 240, 240);
	//private static final Font FONT = new Font("Tahoma", Font.PLAIN, 11);

	static void customizeNimbus()
		throws Exception
	{
//		UIManager.put("nimbusBase", BACKGROUND);
//		UIManager.put("nimbusBlueGrey", BACKGROUND);
		UIManager.put("control", BACKGROUND);

		// TODO: imilne 04/SEP/2009 - No longer working since JRE 1.6.0_u13
/*		UIManager.put("defaultFont", FONT);
		UIManager.put("Label[Enabled].font", FONT);
		UIManager.put("Table[Enabled].font", FONT);
		UIManager.put("TableHeader[Enabled].font", FONT);
		UIManager.put("TabbedPane[Enabled].font", FONT);
		UIManager.put("ComboBox[Enabled].font", FONT);
		UIManager.put("Button[Enabled].font", FONT);
		UIManager.put("TextField[Enabled].font", FONT);
*/


		for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
			if (laf.getName().equals("Nimbus"))
				UIManager.setLookAndFeel(laf.getClassName());



		UIManager.put("SplitPane[Enabled].size", 8);

		UIManager.put("nimbusOrange", new Color(51, 98, 140));
//		UIManager.put("nimbusOrange", new Color(57, 105, 138));
//		UIManager.put("nimbusOrange", new Color(115, 164, 209));
	}
}