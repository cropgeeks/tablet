// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.data.auxiliary.*;

public class FeatureTrackCreator extends SimpleJob
{
	private VisualContig vContig;
	private Contig contig;

	public FeatureTrackCreator(VisualContig vContig, Contig contig)
	{
		this.vContig = vContig;
		this.contig = contig;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		DisplayData.getFeatureTypes().clear();
		for (String type: Feature.getTypes())
			DisplayData.getFeatureTypes().add(type);

		vContig.removeTracks();

		// For each type of feature to be given a track...
		for (String type: DisplayData.getFeatureTypes())
		{
			FeatureTrack track = new FeatureTrack(type);
			vContig.addTrack(track);

			type = type.toLowerCase();

			for (Feature feature: contig.getFeatures())
				if (feature.getGFFType().toLowerCase().equals(type))
					track.addFeatureNoSort(feature);

			System.out.println("Added " + type + " track with " + track.getFeatures().size() + " features");
		}

		long e = System.currentTimeMillis();
		System.out.println("Created features track(s) in " + (e-s) + "ms");
	}
}