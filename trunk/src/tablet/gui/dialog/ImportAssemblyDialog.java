package tablet.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

import tablet.gui.*;

import scri.commons.gui.*;

public class ImportAssemblyDialog extends JDialog
	implements ActionListener, ItemListener
{
	private JPanel cardsPanel;
	private NBImportAssemblyACEPanel acePanel;
	private NBImportAssemblyAFGPanel afgPanel;
	private NBImportAssemblySOAPPanel soapPanel;

	private static final String ACEPANEL = "ACE", AFGPANEL = "AFG", SOAPPANEL = "SOAP";
	
	private JButton bCancel, bHelp, bOpen;
	
	private NBImportAssemblyPanel nbPanel;

	String [] filenames;
	
	public ImportAssemblyDialog(WinMain winMain)
	{
		super(
			Tablet.winMain,
			RB.getString("gui.dialog.ImportAssemblyDialog.title"),
			true
		);
		
		nbPanel = new NBImportAssemblyPanel(this);
		cardsPanel = new JPanel(new CardLayout());

		acePanel = new NBImportAssemblyACEPanel(this);
		cardsPanel.add(acePanel, ACEPANEL);

		afgPanel = new NBImportAssemblyAFGPanel(this);
		cardsPanel.add(afgPanel, AFGPANEL);

		soapPanel = new NBImportAssemblySOAPPanel(this);
		cardsPanel.add(soapPanel, SOAPPANEL);

		add(nbPanel, BorderLayout.NORTH);
		add(cardsPanel);
		add(createButtons(), BorderLayout.SOUTH);
		
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setResizable(false);
		setLocationRelativeTo(winMain);

		// Position on screen...
		if (Prefs.guiJumpToX != -9999 || Prefs.guiJumpToY != -9999)
			setLocation(Prefs.guiJumpToX, Prefs.guiJumpToY);

		//populate combo boxes with recent file lists from XML
		setupComboBox(acePanel.aceComboBox, acePanel.recentFiles);
		setupComboBox(afgPanel.afgComboBox, afgPanel.recentFiles);
		setupComboBox(soapPanel.soapComboBox, soapPanel.recentFilesSoap);
		setupComboBox(soapPanel.soapComboBox2, soapPanel.recentFilesFastA);
	}

	private JPanel createButtons()
	{
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		bOpen = SwingUtils.getButton(RB.getString("gui.text.open"));
		bOpen.addActionListener(this);

		JPanel p1 = TabletUtils.getButtonPanel();
		p1.add(bOpen);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCancel)
			setVisible(false);

		else if(e.getSource() == bOpen)
		{
			//set the filenames to be loaded
			//update the recent files
			//load the file
			if(nbPanel.assemblyComboBox.getSelectedItem() == ACEPANEL && acePanel.aceComboBox.getSelectedItem() != null)
			{
				filenames = new String[1];
				filenames[0] = acePanel.aceComboBox.getSelectedItem().toString();
				updateRecentFiles(acePanel.recentFiles, Prefs.aceRecentDocs);
				loadFile();
			}
			else if(nbPanel.assemblyComboBox.getSelectedItem() == AFGPANEL && afgPanel.afgComboBox.getSelectedItem() != null)
			{
				filenames = new String[1];
				filenames[0] = afgPanel.afgComboBox.getSelectedItem().toString();
				updateRecentFiles(afgPanel.recentFiles, Prefs.afgRecentDocs);
				loadFile();
			}
			else if(nbPanel.assemblyComboBox.getSelectedItem() == SOAPPANEL && soapPanel.soapComboBox.getSelectedItem() != null && soapPanel.soapComboBox2.getSelectedItem() != null)
			{
				filenames = new String[2];
				filenames[0] = soapPanel.soapComboBox.getSelectedItem().toString();
				filenames[1] = soapPanel.soapComboBox2.getSelectedItem().toString();

				LinkedList<String> soapFiles = new LinkedList<String>();

				int length;
				if(soapPanel.recentFilesSoap.size() > soapPanel.recentFilesFastA.size())
				{
				    length = soapPanel.recentFilesSoap.size();
				}
				else if(soapPanel.recentFilesFastA.size() > soapPanel.recentFilesSoap.size())
				{
				    length = soapPanel.recentFilesFastA.size();
				}
				else
				{
				    length = soapPanel.recentFilesSoap.size();
				}

				for(int i=0; i < length; i++)
				{
					soapFiles.add(soapPanel.recentFilesSoap.get(i) + "<!TABLET!>" + soapPanel.recentFilesFastA.get(i));
				}
				System.out.println("UPDATE RECENT FILES");
				updateRecentFiles(soapFiles, Prefs.soapRecentDocs);
				loadFile();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Please select a file to be loaded.");
			}

		}

		else if(e.getSource() == acePanel.bBrowse)
		{
			updateComboBox(acePanel.aceComboBox, acePanel.recentFiles);
		}

		else if(e.getSource() == afgPanel.bBrowse)
		{
			updateComboBox(afgPanel.afgComboBox, afgPanel.recentFiles);
		}
		else if(e.getSource() == soapPanel.bBrowse)
		{
			updateComboBox(soapPanel.soapComboBox, soapPanel.recentFilesSoap);
			TabletUtils.dirChanged = true;
		}
		else if(e.getSource() == soapPanel.bBrowse2)
		{
			updateComboBox(soapPanel.soapComboBox2, soapPanel.recentFilesFastA);
			TabletUtils.dirChanged = true;
		}
	}

	public void itemStateChanged(ItemEvent e)
	{
		CardLayout cl = (CardLayout)(cardsPanel.getLayout());
		cl.show(cardsPanel, (String)e.getItem());
	}

	private void loadFile()
	{
		setVisible(false);
		Tablet.winMain.getCommands().fileOpen(filenames);
		TabletUtils.dirChanged = false;
	}

	//update the preferences file with recent file list
	private void updateRecentFiles(LinkedList<String> files, String[] recentDocs)
	{
		if(files.size() > 10)
		{
			String [] filePaths = new String[10];
			Prefs.setRecentFiles((files.subList(0, 10)).toArray(filePaths), recentDocs);
		}
		else
		{
			String [] filePaths = new String[files.size()];
			Prefs.setRecentFiles(files.toArray(filePaths), recentDocs);
		}
	}

	//update the combobox and list of recent files fromt he new input
	private void updateComboBox(JComboBox combo, LinkedList<String> recentFiles)
	{
		String name;
		if(combo.getSelectedItem() != null)
		{
			name = TabletUtils.getFilename("Choose a file", combo.getSelectedItem().toString());
		}
		else
		{
			name = TabletUtils.getFilename("Choose a file", "");
		}

		System.out.println("Supplied name");
		System.out.println(name);

		if(recentFiles.contains(name))
		{
		    recentFiles.remove(name);
		    recentFiles.addFirst(name);
		    combo.removeItem(name);
		    combo.addItem(name);
		    combo.setSelectedItem(name);
		}
		else
		{
		    recentFiles.addFirst(name);
		    combo.addItem(name);
		    combo.setSelectedItem(name);
		}
	}

	private void setupComboBox(JComboBox combo, LinkedList<String> recentFiles)
	{
		if(!recentFiles.isEmpty())
		{
		    for(String item : recentFiles)
		    {
			combo.addItem(item);
		    }
		}
	}
}
