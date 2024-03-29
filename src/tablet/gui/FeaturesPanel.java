// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.dialog.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

public class FeaturesPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;

	private JTabbedPane ctrlTabs;
	private FeaturesTableModel model;
	private FeaturesPanelNB controls;

	private TableRowSorter<FeaturesTableModel> sorter;

	private JMenuItem mClipboard;

	FeaturesPanel(AssemblyPanel aPanel, JTabbedPane ctrlTabs)
	{
		this.aPanel = aPanel;
		this.ctrlTabs = ctrlTabs;

		setLayout(new BorderLayout());
		add(controls = new FeaturesPanelNB(this));

		createTableModel();

		controls.featuresLabel.setText(getTitle(0));
	}

	private void createTableModel()
	{
		model = new FeaturesTableModel();

		sorter = new TableRowSorter<FeaturesTableModel>(model);
		controls.table.setModel(model);
		controls.table.setRowSorter(sorter);

		// Additional (duplicate) table-clicked handler to catch the user
		// re-clicking on the same row. This doesn't generate a table event, but
		// we still want to respond to it and highlight the selection again
		controls.table.addMouseListener(new TableMouseListener());

		// Set the custom renderer on the table
		controls.table.setDefaultRenderer(Integer.class,
			new FeaturesTableModel.FeaturesTableRenderer());
	}

	public void setContig(Contig contig)
	{
		if (contig == null)
		{
			controls.featuresLabel.setText(getTitle(0));

			model.clear();
			controls.toggleComponentEnabled(false);
		}

		else
		{
			model.setContig(contig);

			controls.featuresLabel.setText(getTitle(contig.getFeatures().size()));
			controls.toggleComponentEnabled(true);
		}
	}

	String getTitle(int count)
	{
		return RB.format("gui.FeaturesPanel.title", count);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		processTableSelection();
	}

	private void processTableSelection()
	{
		int row = controls.table.getSelectedRow();

		if (row == -1)
			return;

		// Convert from view->model (deals with user-sorted table)
		row = controls.table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = model.getFeature(row);

		int start = feature.getDataPS();
		int end   = feature.getDataPE();

		// Override position if we're using unpadded values
		if (Prefs.guiFeaturesArePadded == false)
		{
			// Quick check in case the data isn't actually ready yet!
			if (DisplayData.hasUnpaddedToPadded() == false)
			{
				TaskDialog.info(RB.getString("gui.FeaturesPanel.unavailable"),
					RB.getString("gui.text.close"));

				controls.table.clearSelection();
				return;
			}

			start = DisplayData.unpaddedToPadded(start);
			end = DisplayData.unpaddedToPadded(end);
		}

		aPanel.moveToPosition(-1, start, true);
		new ColumnHighlighter(aPanel, start, end);

		fixBamWindowChange(row, feature);
	}

	// Ensures the correct row is selected after a bam window change. Due to
	// addition and removal of CIGAR features, the index of a feature in the
	// table can change on a bam window change.
	private void fixBamWindowChange(int modelIndex, Feature feature)
	{
		if (model.getFeature(modelIndex) != feature)
			modelIndex = model.indexOf(feature);

		// Convert our model index to a view index
		int viewIndex = controls.table.convertRowIndexToView(modelIndex);
		controls.table.setRowSelectionInterval(viewIndex, viewIndex);
	}

	void setTableFilter(RowFilter<FeaturesTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
	}

	public void nextFeature()
	{
		controls.nextFeature();
		ctrlTabs.setSelectedComponent(this);
	}

	public void prevFeature()
	{
		controls.prevFeature();
		ctrlTabs.setSelectedComponent(this);
	}

	String getTableToolTip(MouseEvent e)
	{
		int row = controls.table.rowAtPoint(e.getPoint());
		row = controls.table.convertRowIndexToModel(row);

		// Pull the feature out of the model
		Feature feature = model.getFeature(row);

		int p1 = feature.getDataPS();
		int p2 = feature.getDataPE();

		if(feature instanceof CigarFeature)
		{
			int count = ((CigarFeature)feature).count();

			if (Prefs.guiFeaturesArePadded)
			{
				return RB.format("gui.FeaturesPanel.tooltip.padded.cigarFeature",
					feature.getGFFType(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					DisplayData.getUnpadded(p1), DisplayData.getUnpadded(p2), count);
			}
			else
			{
				return RB.format("gui.FeaturesPanel.tooltip.unpadded.cigarFeature",
					feature.getGFFType(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					DisplayData.getPadded(p1), DisplayData.getPadded(p2), count);
			}
		}
		else
		{
			if (Prefs.guiFeaturesArePadded)
			{
				return RB.format("gui.FeaturesPanel.tooltip.padded",
					feature.getGFFType(), feature.getName(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					DisplayData.getUnpadded(p1), DisplayData.getUnpadded(p2),
					feature.getTagsAsHTMLString());
			}
			else
			{
				return RB.format("gui.FeaturesPanel.tooltip.unpadded",
					feature.getGFFType(), feature.getName(),
					TabletUtils.nf.format(p1+1), TabletUtils.nf.format(p2+1),
					DisplayData.getPadded(p1), DisplayData.getPadded(p2),
					feature.getTagsAsHTMLString());
			}
		}
	}

	public void editFeatures()
	{
		FeaturesDialog dialog = new FeaturesDialog();

		if (dialog.isOK())
		{
			aPanel.getFeaturesCanvas().setContig(aPanel.getContig());
			aPanel.getFeaturesCanvas().revalidate();

			aPanel.repaint();
		}
	}

	private void displayMenu(MouseEvent e)
	{
		mClipboard = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mClipboard, "gui.viewer.FeaturesPanel.mClipboard");
		mClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabletUtils.copyTableToClipboard(controls.table, model);
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(mClipboard);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private final class TableMouseListener extends MouseInputAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (!e.isPopupTrigger())
				processTableSelection();
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}
	}
}