Command Line Options
====================

Tablet supports command line options for automatically loading and navigating to locations in an assembly.

Loading assembly files
----------------------

Provide your assembly file - and reference file if one is required - as an argument after tablet.exe on the command line. Tablet automatically works out which is the assembly file and which is the reference file, so you can either run:

::

	tablet.exe assembly_file reference_file

or

::

	tablet.exe reference_file assembly_file

Automatically open contig
-------------------------

Tablet can automatically open a contig in a provided assembly from the command line. To do this simply specify the name of a contig from your assembly file along with the assembly file when running Tablet from the command line prefixed with *view*:

::

	tablet.exe assembly_file reference_file view:contig_name

It is also possible to move to a location in the given contig when loading from the command line by specifying the base position after a colon, e.g.

::

	tablet.exe assembly_file reference_file view:contig_name:200
