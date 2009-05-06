package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;

class ContigPanel extends JPanel implements ListSelectionListener
{
	private AssemblyPanel aPanel;

	private DefaultListModel model;
	private JList list;

	ContigPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;

		model = new DefaultListModel();
		list = new JList(model);
		list.addListSelectionListener(this);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Contigs:"));
		add(new JScrollPane(list));
	}

	void setAssembly(Assembly assembly)
	{
		model.clear();

		if (assembly == null)
			return;

		for (Contig contig: assembly.getContigs())
			model.addElement(contig);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		aPanel.setContig((Contig) list.getSelectedValue());
	}
}