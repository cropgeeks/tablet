Import Data
===========

The ``Import Data`` dialog is used to provide information on data files should be used to import data into Flapjack. 


The ``Maps and Genotypes`` tab is used to specify the map file and genotype file to load into Flapjack. With the ``Import from text files`` radio button selected, use the browse buttons to locate and select the map and genotype files you wish to load into Flapjack.

 |DataImportDialog|

The **map file** should contain information on the markers, the chromosome they are on, and their position within that chromosome. The markers do not need to be in any particular order as Flapjack will group and sort them by chromosome and distance once they are loaded. A short example is shown below:

::

 # fjFile = MAP
 Marker1      1H     32.5
 Marker2      1H     45.0
 Marker3      2H     23.9

The **genotype file** should contain a list of variety lines, with allele data per marker for that line. It also requires a header line specifying the marker information for each column.

::

 # fjFile = GENOTYPE
 Marker1   Marker2   Marker3
 Line1        A         G         G
 Line2        A         -         G/T
 Line3        T         A         C

Both the map file and the genotype file must be in plain-text, tab-delimited format. The ``\# fjFile =`` header lines are optional (but recommended) as they allow the files to be loaded into Flapjack via drag
and drop. Once you have specified the map and genotype file you wish to load click the ``Import map/genotypes`` button to import your data.

Clicking the ``Advanced options...`` button opens the [Advanced Data Import Options] dialog.

Flapjack will display the progress of the import operation as the data is read from the files.

.. |DataImportDialog| image:: images/DataImportDialog.png