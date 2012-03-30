// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Represents a .tablet file, that is a simple xml wrapper around actual
 * references to assembly, reference, feature, etc files or URLs.
 */
public class TabletFile
{
	private AssemblyFile file;

	private String assembly;
	private String reference;
	private String gff3;

	public TabletFile(AssemblyFile file)
	{
		this.file = file;
	}

	public String[] process(String[] filenames, int index)
	{
		System.out.println("TabletFile: " + file.getPath());

		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("assembly");
			// For now, we are assuming only one assembly element
			if (list.getLength() == 1)
			{
				Node node = list.item(0);
				assembly = node.getTextContent();
			}


			list = doc.getElementsByTagName("reference");
			// For now, we are assuming only one reference element
			if (list.getLength() == 1)
			{
				Node node = list.item(0);
				reference = node.getTextContent();
			}


			list = doc.getElementsByTagName("gff3");
			// For now, we are assuming only one gff3 element
			if (list.getLength() == 1)
			{
				Node node = list.item(0);
				gff3 = node.getTextContent();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Assembly:  " + assembly);
		System.out.println("Reference: " + reference);

		// Convert the original array of filenames into a workable list
		ArrayList<String> list = new ArrayList<String>();
		for (String filename: filenames)
			list.add(filename);

		// Remove the TabletFile from it
		list.remove(index);

		// Now add whatever files the TabletFile referenced within it
		if (assembly != null)
			list.add(assembly);
		if (reference != null)
			list.add(reference);
		if (gff3 != null)
			list.add(gff3);


		return list.toArray(filenames);
	}
}