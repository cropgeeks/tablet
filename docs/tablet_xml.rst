Tablet XML
==========

Tablet supports Tablet XML files, or .tablet files, for specifying sets of data which can be loaded together. If you have Tablet installed you can double click a .tablet file to launch Tablet with the data specified in that file. Alternatively you can drag and drop a .tablet file onto an already open instance of Tablet to load the associated data, or load data using a .tablet file as the argument to Tablet on the command line.

.. note::
  There is no way to open a Tablet XML (.tablet) file from within Tablet, other than by dragging and dropping the file onto Tablet.

Tablet XML Elements
-------------------
Tablet XML files allow you to specify the following elements, an assembly file, a reference file, an annotation file, a contig and a position. The only required element is an assembly file. The position element only works when paired with a contig element. Below are examples of how to specify each element:

* ``Assembly file`` (required)

	This can be in any of the assembly file formats that Tablet :doc:`supports <quickstart>`.

	.. code-block:: xml

		<assembly>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.bam</assembly>

* ``Reference file`` (optional)

	A fasta formatted reference file.

	.. code-block:: xml

		<reference>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.fasta</reference>

* ``Annotation file`` (optional)

	A gff3 formatted annotation file.

	.. code-block:: xml

 		<annotation>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.gff</annotation>

* ``Contig`` (optional)

	This should be the name of a contig in the assembly file.

	.. code-block:: xml

 		<contig>contig_53395</contig>

* ``Position`` (optional)

	A position within the specified contig.

	.. code-block:: xml

		<position>14000</position>


Example
-------

.. code-block:: xml

	<tablet>
		<assembly>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.bam</assembly>
		<reference>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.fasta</reference>
		<annotation>https://bioinf.hutton.ac.uk/tablet/sample-data/book/example4/Example4.gff</annotation>
		<contig>contig_53395</contig>
		<position>14000</position>
	</tablet>

This example file can be downloaded by clicking here_.

.. _here: https://bioinf.hutton.ac.uk/tablet/sample-data/example4.tablet