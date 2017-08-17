Overviews
=========

Tablet provides two different methods of visualizing overview information about a contig and its data - the ``Scaled-to-Fit`` overview and the ``Coverage`` overview, both selectable from the ``Options`` tab of the :doc:`ribbon_bar`.

In either case, along with the overview visualization itself, the ``Overview Panel`` also provides a means for fast navigation within a contig, simple by clicking and dragging its red viewing rectangle. This rectangle represents the portion of the entire contig that is currently visible within the main display area.

Scaled-to-fit overviews
-----------------------

|TabletOverviewScaled|

.. |TabletOverviewScaled| image:: images/Tablet-overview-scaled.png

|TabletOverviewScaledClassic|

.. |TabletOverviewScaledClassic| image:: images/Tablet-overview-scaled-classic.png

This overview type attempts to fit all of the read data from the current contig into the available window size. This type of view may be less useful on larger contigs or contigs where read coverage is very sparse, as the proportion of empty space to read data obviously tends to favour the former.

The colour scheme in use (``Enhanced`` or ``Classic`` will affect how this overview is rendered too.

Coverage overviews
------------------

|TabletOverviewCoverage|

.. |TabletOverviewCoverage| image:: images/Tablet-overview-coverage.png

This overview displays average coverage depth over the entire contig as a histogram. The bar depth represents the average coverage for that region as a proportion of the maximum coverage for the contig. Colour intensity is used to show how the maximum depth within that averaged region relates to the overall maximum too, with darker shades representing areas with deeper coverage.

A short bar, but with very dark colouring can mean that although the average coverage for that region may be very low, it must have some columns where the coverage is very deep. These areas often warrant closer inspection.
