Open Assembly
=============

The ``Open Assembly`` dialog is used to load assemblies into Tablet for viewing.

Access the dialog by selecting either ``Open Assembly`` from the ``Data`` tab of the :doc:`ribbon_bar` or by selecting ``Open`` from the :doc:`application_menu`.

 |ImportAssemblyDialog|

Tablet can currently view assemblies/alignments which are stored in the ACE, AFG, MAQ, SOAP, SAM or BAM file formats, with accompanying reference/consensus data (if needed) being read from a FASTA file.

The ``Primary assembly file`` refers to the main file containing your assembly or alignment data. The ``Reference/consensus file`` refers to any additional reference file that contains reference/consensus data and is needed with assembly formats that do not include this data in the primary file (such as SAM, BAM, MAQ, and SOAP). This additional data is not needed by Tablet, but it is advisable to include it if you have it, otherwise Tablet cannot provide a visualization of the reference/consensus sequence within each contig.

Tablet can load files locally from disk, or remotely from a web server. The files can either be uncompressed or compressed with gzip. (BAM files are already compressed, and should be provided as is).

Importing ACE files
-------------------

An ACE formatted file includes information on each contig, its consensus sequence, and the reads that are aligned against it. A single ACE file provides all the information that Tablet requires.

Importing AFG files
-------------------

An AFG formatted file includes information on each contig, its consensus sequence, and the reads that are aligned against it. A single AFG file provides all the information that Tablet requires.

Importing MAQ files
-------------------

The MAQ assembler ultimately generates a binary-formatted map file (with a .map extension). To be readable by Tablet, this file must be converted into a text-based file containing the read information.

Using the command line MAQ assembler tools, run: ``maq mapview input.map > output.txt``. This file must be provided to Tablet.

Importing SOAPAligner output
----------------------------

The output from the SOAPAligner mapping tool is a text-based alignment file that includes the read data only. A separate FASTA formatted file containing the reference sequence(s) can be provided separately but this is optional. SOAPDenovo output is currently not supported.

Importing SAM files
-------------------

Tablet will attempt to load a text-based .sam file, with or without SAM headers. A SAM file does not include reference/consensus information, so if it is to be included, it must be provided in a separate fasta/fastq file.

When processing the CIGAR information from a SAM file, Tablet will create a list of features (that will be shown in the Features Table) for each CIGAR insertion, deletion, skip, and clip event that is found. Each feature gives the position of the insertion and the number of reads that have an insertion at that position.

Importing BAM files
-------------------

Tablet supports BAM in its native (indexed) format. It is important to note that the BAM file must be sorted and indexed, with an associated .bai file located in the same directory as the .bam file (named either <assembly_name>.bam.bai or <assembly_name>.bai).

When processing the CIGAR information from a BAM file, Tablet will create a list of features (that will be shown in the Features Table) for each CIGAR insertion, deletion, skip, and clip event that is found. Each feature gives the position of the insertion and the number of reads that have an insertion at that position.

Importing example data
----------------------

The ``Unsure how to get started? Click here to open an assembly.`` link brings up the dialog to load example datasets. This contains a drop down menu which allows for selection between the various example datasets available. Below the drop down menu is a description of the currently selected example dataset. To open a dataset select it from the drop down menu and click the ``Open`` button.

 |ImportAssemblyExampleDataset|

.. |ImportAssemblyDialog| image:: images/Tablet-gui.dialog.ImportAssemblyDialog.png

.. |ImportAssemblyExampleDataset| image:: images/Tablet-gui.dialog.ImportAssemblyDialog.exampledataset.png
