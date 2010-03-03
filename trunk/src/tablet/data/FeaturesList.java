package tablet.data;

import java.util.*;

import tablet.data.auxiliary.Feature;

public class FeaturesList
{
	private ArrayList<Feature> features = new ArrayList<Feature>();

	public void addFeature(Feature newFeature)
	{
		int result = Collections.binarySearch(features, newFeature);

		// If result >= 0 we've found a duplicat and don't add. Otherwise add.
		if(result < 0)
			features.add((-result)-1, newFeature);
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
