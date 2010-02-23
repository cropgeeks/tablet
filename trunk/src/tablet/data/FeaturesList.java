package tablet.data;

import java.util.ArrayList;
import tablet.data.auxiliary.Feature;

public class FeaturesList
{
	// Main set of features
	private ArrayList<Feature> features = new ArrayList<Feature>();

	public void addFeature(Feature newFeature)
	{
		boolean found = false;
		// Check it doesn't already exist
		for (int i = 0; i < features.size(); i++)
			if (features.get(i).isSameAs(newFeature))
				found = true;

		if(!found)
			features.add(newFeature);
	}
	
	public int size()
	{
		return features.size();
	}

	public ArrayList<Feature> getFeatures()
	{
		return features;
	}
}
