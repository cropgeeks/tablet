// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

/**
 * Represents a .tablet file, that is a simple xml wrapper around actual
 * references to assembly, reference, feature, etc files or URLs.
 */
public class TabletFileHandler
{
	// A list of recently accessed TabletFile objects
	public static ArrayList<TabletFile> recentFiles = new ArrayList<>();

	/**
	 * Skims through the list of recent objects and builds a list of assembly
	 * files as a string array.
	 */
	public static String getAssemblyHistory()
	{
		StringBuilder sb = new StringBuilder();
		// The hash is used to remove duplicates at the assembly-only level
		HashMap<String,String> hash = new HashMap<>();

		for (TabletFile tabletFile: recentFiles)
			if (tabletFile.hasAssembly())
			{
				if (!hash.containsKey(tabletFile.assembly.getPath()))
					sb.append(tabletFile.assembly.getPath() + "\t");
				hash.put(tabletFile.assembly.getPath(), "");
			}

		return sb.toString();
	}

	/**
	 * Skims through the list of recent objects and builds a list of reference
	 * files as a string array.
	 */
	public static String getReferenceHistory()
	{
		StringBuilder sb = new StringBuilder();
		// The hash is used to remove duplicates at the reference-only level
		HashMap<String,String> hash = new HashMap<>();

		for (TabletFile tabletFile: recentFiles)
			if (tabletFile.hasReference())
			{
				if (!hash.containsKey(tabletFile.reference.getPath()))
					sb.append(tabletFile.reference.getPath() + "\t");
				hash.put(tabletFile.reference.getPath(), "");
			}

		return sb.toString();
	}

	/**
	 * Returns true if the most recently used TabletFile object contains a file
	 * pointer to reference information.
	 */
	public static boolean mruHasReference()
	{
		if (recentFiles.size() == 0)
			return false;

		return recentFiles.get(0).hasReference();
	}

	public static void addAsMostRecent(TabletFile tabletFile)
	{
		while (recentFiles.contains(tabletFile))
			recentFiles.remove(tabletFile);
		recentFiles.add(0, tabletFile);

		// Restrict the MRU list to no more than fifty entries
		while (recentFiles.size() > 50)
			recentFiles.remove(recentFiles.size()-1);
	}

	/**
	 * Parses a list of filenames to determine their type and ultimately builds
	 * and returns a TabletFile object that encapsulates this information.
	 */
	public static TabletFile createFromFileList(String[] filenames)
	{
		TabletFile tabletFile = new TabletFile();

		// Now that we have filenames, convert them into AssemblyFile objects
		AssemblyFile[] files = new AssemblyFile[filenames.length];

		// Work backwards so that the first elements end up taking priority
		for (int i=filenames.length-1; i >= 0; i--)
		{
			files[i] = new AssemblyFile(filenames[i]);
			files[i].canDetermineType();

			// Special processing for a .tablet file
			if (files[i].isTabletFile())
			{
				tabletFile = createFromXML(files[i]);

				// A TabletFile takes preference over any other type (so if a
				// .tablet and a .bam were handed in, we'll ignore the .bam)
				return tabletFile;
			}

			if (files[i].isAssemblyFile())
				tabletFile.assembly = files[i];

			if (files[i].isReferenceFile())
				tabletFile.reference = files[i];

			if (files[i].isAnnotationFile())
				tabletFile.annotations.add(files[i]);
		}

		return tabletFile;
	}

	public static TabletFile createFromXML(AssemblyFile file)
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("tablet");
			Element eTablet = (Element) list.item(0);

			return readTabletElement(eTablet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static TabletFile readTabletElement(Element eTablet)
		throws Exception
	{
		TabletFile tabletFile = new TabletFile();

		NodeList list = eTablet.getElementsByTagName("assembly");
		if (list.getLength() == 1)
		{
			Node node = list.item(0);
			tabletFile.assembly = new AssemblyFile(node.getTextContent());
		}

		list = eTablet.getElementsByTagName("reference");
		if (list.getLength() == 1)
		{
			Node node = list.item(0);
			tabletFile.reference = new AssemblyFile(node.getTextContent());
		}


		list = eTablet.getElementsByTagName("annotation");
		for (int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			tabletFile.annotations.add(new AssemblyFile(node.getTextContent()));
		}


		// Contig name (if any)
		list = eTablet.getElementsByTagName("contig");
		if (list.getLength() == 1)
		{
			Node node = list.item(0);
			tabletFile.contig = node.getTextContent();
		}


		// Position within the contig (if any)
		list = eTablet.getElementsByTagName("position");
		if (list.getLength() == 1)
		{
			Node node = list.item(0);

			try { tabletFile.position = Integer.parseInt(node.getTextContent()); }
			catch (Exception e) {}
		}

		return tabletFile;
	}

	public static void loadMRUList(File file)
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new FileInputStream(file));
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("tablet");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element eTablet = (Element) list.item(i);
				TabletFile tabletFile = readTabletElement(eTablet);

				if (recentFiles.contains(tabletFile) == false)
					recentFiles.add(tabletFile);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void saveMRUList(File file)
	{
		try
		{
			// Create an XML document from our recent files list
			Document doc = createXMLDoc();
			DOMSource documentSource = new DOMSource(doc);

			// Setup transformer so that it indents our XML file correctly
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// Save our document to file using by transforming the document into
			// a stream result.
			StreamResult result = new StreamResult(file);
			transformer.transform(documentSource, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static Document createXMLDoc()
		throws DOMException, ParserConfigurationException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = docFactory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("tablet-mru");
		doc.appendChild(root);

		for (TabletFile tabletFile: recentFiles)
		{
			Element tablet = doc.createElement("tablet");
			root.appendChild(tablet);

			if (tabletFile.assembly != null)
				tablet.appendChild(makeTabletElement(doc, "assembly", tabletFile.assembly.getPath()));

			if (tabletFile.reference != null)
				tablet.appendChild(makeTabletElement(doc, "reference", tabletFile.reference.getPath()));

			for (AssemblyFile annotation: tabletFile.annotations)
				tablet.appendChild(makeTabletElement(doc, "annotation", annotation.getPath()));

			if (tabletFile.contig != null)
				tablet.appendChild(makeTabletElement(doc, "contig", tabletFile.contig));

			if (tabletFile.position != null)
				tablet.appendChild(makeTabletElement(doc, "reference", tabletFile.position.toString()));
		}

		return doc;
	}

	private static Element makeTabletElement(Document doc, String tag, String content)
	{
		Element element = doc.createElement(tag);
		element.appendChild(doc.createTextNode(content));

		return element;
	}
}