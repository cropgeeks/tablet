// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import tablet.analysis.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.Feature.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class RestrictionEnzymeDialog extends JDialog implements ActionListener
{
	private File file;
	private InputStream is;
	private AssemblyPanel aPanel;
	private RestrictionEnzymePanelNB panel;

	private DefaultTableModel model;

	private JButton bCancel;
	private JButton bSelect;

	public RestrictionEnzymeDialog()
	{
		super(Tablet.winMain, RB.getString("gui.dialog.RestrictionEnzymeDialog.title"), true);

		aPanel = Tablet.winMain.getAssemblyPanel();

		try
		{
			Class c = this.getClass();
			is = c.getResourceAsStream("/res/link_staden.txt");
			// Get file located in .scri-bioinf folder
//			File root = new File(System.getProperty("user.home"));
//			File folder = new File(root, ".scri-bioinf");
//			file = new File(folder, "link_staden.txt");
		}
		catch(Exception e) { e.printStackTrace(); }

		if (is != null)
		{
			try
			{
				setupDialog();
			}
			catch(Exception ex) { ex.printStackTrace(); }
		}

		panel = new RestrictionEnzymePanelNB(this);
		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		SwingUtils.addCloseHandler(this, bCancel);
		getRootPane().setDefaultButton(bSelect);

		panel.enzymeTable.setModel(model);
		// Custom renderers for the string based multi-variable data
		panel.enzymeTable.getColumnModel().getColumn(1).setCellRenderer(new SequenceRenderer());
		panel.enzymeTable.getColumnModel().getColumn(2).setCellRenderer(new CutPointRenderer());

		pack();
		setResizable(false);
		setLocationRelativeTo(Tablet.winMain);
	}

	private JPanel createButtons()
	{
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		bSelect = new JButton(RB.getString("gui.dialog.RestrictionEnzymeDialog.select"));
		bSelect.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bSelect);
		p1.add(bCancel);

		return p1;
	}

	private void setupDialog() throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		// Set up column names for the table model
		String[] names = {
			RB.getString("gui.dialog.RestrictionEnzymeDialog.model.name"),
			RB.getString("gui.dialog.RestrictionEnzymeDialog.model.sequences"),
			RB.getString("gui.dialog.RestrictionEnzymeDialog.model.cutPoints")
		};

		// Setup our default table model
		model = new DefaultTableModel(names, 0)
		{
			public boolean isCellEditable(int rowIndex, int mColIndex) {
        		return false;
			}
		};

		// Parse the enzymes out of the input file
		String str;
		while ((str = reader.readLine()) != null)
		{
			if (str.endsWith("//"))
				parseEnzyme(str);
		}

		reader.close();
	};

	// Parse an enzyme from a Staden format string
	private void parseEnzyme(String enzyme)
	{
		String[] tokens = enzyme.split("/");
		String name = tokens[0];

		// Parse sequence strings and cut points from string input
		ArrayList<String> sequences = new ArrayList<>();
		ArrayList<Integer> cutPoints = new ArrayList<>();

		for (int i=1; i < tokens.length; i++)
		{
			cutPoints.add(tokens[i].indexOf('\''));
			sequences.add(tokens[i].replaceAll("'", "").toUpperCase());
		}

		// Create new strings for display of sequences and cut points
		StringBuilder cuts = new StringBuilder();
		StringBuilder seqs = new StringBuilder();

		for (int i=0; i < cutPoints.size(); i++)
		{
			if (i == 0)
			{
				cuts.append(cutPoints.get(i));
				seqs.append(sequences.get(i));
			}
			else
			{
				cuts.append(" - ").append(cutPoints.get(i));
				seqs.append(" - ").append(sequences.get(i));
			}
		}

		// Only add the enzyme to our data objects if it isn't already there
		if (EnzymeFeature.getEnzymes().contains(name) == false)
		{
			EnzymeFeature.getEnzymes().add(name);
			model.addRow(new Object[] { name, seqs.toString(), cuts.toString() });
		}
	}

	private void addEnzymeToTable()
	{
		if (panel.customCombo.getText() == null)
			return;

		// Work around for history combo box bug when using return to select
		// an item
		String custom = (String) panel.customCombo.getEditor().getItem();
		panel.customCombo.updateComboBox(custom);
		// Store history in xml
		Prefs.recentCustomEnzymes = panel.customCombo.getHistory();

		if (custom.endsWith("//"))
		{
			parseEnzyme(custom);
			// Scroll the table to show the most recently added enzyme (make
			// sure this happens on the EDT
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					int rowCount = model.getRowCount()-1;
					Rectangle r = panel.enzymeTable.getCellRect(rowCount, 0,
						false);
					panel.enzymeTable.scrollRectToVisible(r);
					// Highlight added row
					panel.enzymeTable.setRowSelectionInterval(rowCount, rowCount);
				}

			});
		}
	}

	private void selectEnzyme()
	{
		int[] rows = panel.enzymeTable.getSelectedRows();
		for (int row : rows)
		{
			int modelRow = panel.enzymeTable.convertRowIndexToModel(row);

			// Get the enzyme's data from the table
			String name = (String) panel.enzymeTable.getValueAt(modelRow, 0);
			String seqs = (String) panel.enzymeTable.getValueAt(modelRow, 1);
			String cuts = (String) panel.enzymeTable.getValueAt(modelRow, 2);

			ArrayList<String> sequences = new ArrayList<>();
			ArrayList<Integer> cutPoints = new ArrayList<>();

			// Parse the sequences out of the string and place them in an ArrayList
			String[] s = seqs.split(" - ");
			sequences.addAll(Arrays.asList(s));

			// Parse the cut points out of the string and place them in an ArrayList
			String[] c = cuts.split(" - ");
			for (String b : c)
				cutPoints.add(Integer.parseInt(b));

			boolean alreadyAdded = false;

			for (VisibleFeature f : Feature.order)
				if (f.type.equals(name))
					alreadyAdded = true;

			if (alreadyAdded == false)
			{
				Feature.order.add(new VisibleFeature(name, true));

				// Let the DDC know it needs to add this when adding enzymes in future
				EnzymeHandler.addRestrictionEnzyme(new RestrictionEnzyme(name, cutPoints, sequences));
			}
		}

		panel.enzymeTable.clearSelection();

		// Kick the DDC
		aPanel.forceRedraw();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == panel.addButton)
			addEnzymeToTable();

		else if (e.getSource() == bSelect)
		{
			selectEnzyme();
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	// Look into other ways of doing the custom rendering for this (or not doing it?)

	// Renderer for the QTL name column of the table
	class CutPointRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			String cutPoints = (String) table.getValueAt(row, 2);
			String[] cuts = cutPoints.split(" - ");

			String text = "[";
			for (int i=0; i < cuts.length; i++)
			{
				if (Integer.parseInt(cuts[i]) == -1)
					text += "N/A";
				else
					text += cuts[i];

				if (i < cuts.length -1)
					text += ", ";
			}
			text += "]";

			setText(text);

			return this;
		}
	}

	// Renderer for the QTL name column of the table
	class SequenceRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			String sequences = (String) table.getValueAt(row, 1);
			String[] seqs = sequences.split(" - ");

			String text = "[";
			for (int i=0; i < seqs.length; i++)
			{
				text += seqs[i];

				if (i < seqs.length -1)
					text += ", ";
			}
			text += "]";

			setText(text);

			return this;
		}
	}

	/**
	 * Wrapper class for the component parts of a restriction enzyme for use in
	 * generating RestrictionEnzyme features for display on the feature tracks.
	 */
	public class RestrictionEnzyme
	{
		private String name;
		private ArrayList<Integer> cutPoints;
		private ArrayList<String> sequences;

		public RestrictionEnzyme(String name, ArrayList<Integer> cutPoints, ArrayList<String> sequences)
		{
			this.name = name;
			this.cutPoints = cutPoints;
			this.sequences = sequences;
		}

		public String getName()
			{ return name; }

		public ArrayList<Integer> getCutPoints()
			{ return cutPoints; }

		public ArrayList<String> getSequences()
			{ return sequences; }
	}
}