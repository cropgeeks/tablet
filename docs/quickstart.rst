Quickstart
==========

Here is a brief set of instructions to get you up and running with Tablet in the shortest possible time.

Tablet is designed for the visualization and exploration of sequence assemblies, on data sets containing just a few to many millions of reads.

Opening assemblies
------------------

Tablet currently supports importing assembly data from either ACE, AFG, BAM, MAQ, SAM or SOAPAligner/soap2 file formats. There are two ways to get this data into Tablet:

- By clicking the ``Open Assembly`` button in the Data section of the ribbon menu. This will display the :doc:`open_assembly` dialog.
- By dragging and dropping the file (or files) directly into Tablet. 

If you are trying Tablet for the first time and don't have any assembly data readily available you can load in example datasets from the :doc:`open_assembly` dialog.

Once the data is loaded, a list of all the contigs found within the assembly will be shown in the ``Contigs Browser`` down the left-hand side. This can be used to select a contig for display, or to filter the list down to a smaller size via a range of criteria.

Tablet overview
---------------

Tablet's visualizations are split into several areas. The main display provides a visualization of a single contig at a time, with reads aligned against their consensus sequence. Tablet will lay out the data in either packed (showing as many reads per line as possible without overlap) or stacked (showing one read per line) formats. Paired-end variants are available for both

The read data is supplemented with the consensus sequence and its quality scores, coverage information (per base) and up to six consensus to protein translations (3 reading frames, forward and reverse). All of this information is mapped to a scale bar that shows the current position within the contig. The position is listed twice; giving its padded and unpadded ([x] U[x]) values, along with coverage information for the base currently under the mouse (CV[x]).

The :doc:`Overview Panel <overviews>` located above the consensus can display either a scaled-to-fit summary of all the reads in a contig, or a coverage graph showing average read coverage across the contig. Toggle between the views by right-clicking on the ``Overview Panel``, or by using the ``Show/Hide Overview`` button on the ribbon.

Browsing the data
-----------------

You can move the view around the data by using the scrollbars, or by clicking on the canvas and dragging with the mouse.

An alternative method of moving around the data is to click and drag with the mouse on the ``Overview Panel``. The red rectangle drawn on the ``Overview`` represents the area of the data that the main display currently showing. Drag it with the mouse to quickly move anywhere within the current contig.

Zoom in or out using the ``Zoom slider`` located on the ribbon bar. You can also double-click on main display to zoom into that area. By adjusting the ``Variants slider`` you can modify the intensity of read bases that differ from the consensus to highlight potential variants in the contig.

Interacting with Tablet
-----------------------

Notice that as you move your mouse over the display, information on the read under the mouse is displayed in a tooltip. This includes its name, padded and unpadded start and end points, as well as the padded and unpadded length of the read. The read's orientation is also displayed as a graphical arrow (green for forward/uncomplemented and blue for reverse/complemented). The tooltip also provides a scaled-to-fit graphical representation of all the read's bases.

Right clicking with the mouse on many of the display components will open up additional menus showing options to change the display types, highlight regions of interest, copy data to the clipboard, jump to a read's pair if it is a paired read, etc. 