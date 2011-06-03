package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

import scri.commons.gui.*;

public class ReadGroupsPanel extends JPanel implements ActionListener
{
	private ReadGroupsPanelNB controls;

	ReadGroupsPanel()
	{
		setLayout(new BorderLayout());
		add(controls = new ReadGroupsPanelNB(this));

		// Add handling for double click event
		controls.colourList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
					selectColor();
			}
		});
	}

	public void setAssembly(Assembly assembly)
	{
		toggleComponentEnabled(false);

		controls.model.clear();
		controls.colourList.removeAll();

		controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel", ""));
	}

	// Handle contig changes
	void setContig(Contig contig)
	{
		if (contig == null)
		{
			controls.model.clear();
			controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel", ""));
			toggleComponentEnabled(false);
		}

		else
		{
			controls.updateModel();

			if (Assembly.getReadGroups().isEmpty() == false && Prefs.visColorScheme == ReadScheme.READGROUP)
			{
				toggleComponentEnabled(true);
				controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel",
					Assembly.getReadGroups().size()));
			}
			else
			{
				toggleComponentEnabled(false);
				controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel", ""));
			}
		}
	}

	// Display colour chooser to select colour for read group
	private void selectColor()
	{
		// Display colour chooser dialog (defaulting to the current ReadGroup colour)
		Color newColor = JColorChooser.showDialog(this,
			RB.getString("gui.ReadsGroupsPanel.colourChooser"),
			((ReadGroupScheme.ColorInfo)controls.colourList.getSelectedValue()).color);

		// If a colour was chosen, set that colour in the ReadGroupScheme
		if (newColor != null)
		{
			int row = controls.colourList.getSelectedIndex();
			ColorPrefs.setColor(Assembly.getReadGroups().get(row), newColor);
		}

		// Update displays to reflect new colour
		Tablet.winMain.getAssemblyPanel().updateColorScheme();
		// Update model after the color scheme has been upadted
		controls.updateModel();
	}

	public void toggleComponentEnabled(boolean enabled)
	{
		controls.readGroupLabel.setEnabled(enabled);
		controls.colourList.setEnabled(enabled);
		controls.resetLabel.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.resetLabel)
		{
			for (int i=0; i < controls.model.size(); i++)
				ColorPrefs.removeColor(Assembly.getReadGroups().get(i));

			// Update displays to reflect new colour
			Tablet.winMain.getAssemblyPanel().updateColorScheme();
			// Update model after the color scheme has been upadted
			controls.updateModel();
		}
	}

	public void updateModel()
	{
		controls.updateModel();
		controls.readGroupLabel.setText(RB.format("gui.ReadGroupsPanelNB.readGroupLabel",
			Assembly.getReadGroups().size()));
	}
}
