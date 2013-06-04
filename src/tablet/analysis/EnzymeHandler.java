// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import tablet.analysis.Finder.*;
import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.dialog.*;
import tablet.gui.dialog.RestrictionEnzymeDialog.*;
import tablet.gui.viewer.*;

public class EnzymeHandler implements ITaskListener
{
	private ArrayList<EnzymeFeature> features;
	private static ArrayList<RestrictionEnzyme> enzymes = new ArrayList<>();

	private Contig contig;

	public void setupEnzymeFinderTasks(Contig contig)
	{
		this.contig = contig;
		features = new ArrayList<EnzymeFeature>();

		// Runs finders for all restriction enzymes that have been selected
		EnzymeFinderTask finderTask = new EnzymeFinderTask(enzymes,
			Tablet.winMain.getAssemblyPanel());

		finderTask.addTaskListener(this);
		TaskManager.submit("EnzymeFinder", finderTask);
	}

	// Process the results of the search, turning them into EnzymeFeatures and
	// adding them to the currently selected contig
	public void addFeatures(ArrayList<ArrayList<ArrayList<SearchResult>>> results)
	{
		for (int j=0; j < enzymes.size(); j++)
		{
			RestrictionEnzyme enzyme = enzymes.get(j);
			ArrayList<ArrayList<SearchResult>> enzymeResults = results.get(j);

			for (int i=0; i < enzyme.getSequences().size(); i++)
			{
				for (SearchResult result : enzymeResults.get(i))
				{
					EnzymeFeature feature = new EnzymeFeature(enzyme.getName(),
						"", result.getPosition(),
						result.getPosition()+result.getLength()-1, enzyme.getCutPoints().get(i));

					features.add(feature);
				}
			}
		}

		for (EnzymeFeature feature : features)
			contig.addFeature(feature);

		updateDisplay();
	}

	private void updateDisplay()
	{
		WinMain winMain = Tablet.winMain;
		AssemblyPanel aPanel = winMain.getAssemblyPanel();
		FeaturesCanvas fCanvas = aPanel.getFeaturesCanvas();

		// Kick the features canvas
		fCanvas.setContig(aPanel.getContig());
		fCanvas.revalidate();

		// Kick the features panel
		winMain.getFeaturesPanel().setContig(aPanel.getContig());
	}

	@Override
	public void taskCompleted(EventObject e)
	{
		if (e.getSource() instanceof EnzymeFinderTask)
		{
			EnzymeFinderTask ef = (EnzymeFinderTask) e.getSource();
			addFeatures(ef.getResults());
		}
	}

	public static void addRestrictionEnzyme(RestrictionEnzymeDialog.RestrictionEnzyme enzyme)
		{ enzymes.add(enzyme); }
}