package tablet.gui;

import java.awt.*;
import javax.swing.*;

public class TabletUtils
{
	public static JPanel getButtonPanel()
	{
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		p1.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
			BorderFactory.createEmptyBorder(10, 0, 10, 5)));

		return p1;
	}

	/** Produces a FASTA formatted version of a sequence. */
	public static String formatFASTA(String title, String sequence)
	{
		String lb = System.getProperty("line.separator");

		StringBuffer text = new StringBuffer(sequence.length());
		text.append(">" + title + lb);

		for (int i = 0; i < sequence.length(); i += 50)
		{
			try	{
				text.append(sequence.substring(i, i+50) + lb);
			}
			catch (Exception e)	{
				text.append(sequence.substring(i, sequence.length()) + lb);
			}
		}

		return text.toString();
	}
}