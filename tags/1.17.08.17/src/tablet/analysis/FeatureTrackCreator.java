// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import scri.commons.gui.*;

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
		vContig.removeTracks();

		// For each type of feature to be given a track...
		for (Feature.VisibleFeature f: Feature.order)
		{
			if (f.isVisible == false)
				continue;

			FeatureTrack track = new FeatureTrack(f.type);
			vContig.addTrack(track);

			String type = f.type.toLowerCase();

			for (Feature feature: contig.getFeatures())
				if (feature.getGFFType().toLowerCase().equals(type))
					track.addFeature(feature);
		}
	}
}