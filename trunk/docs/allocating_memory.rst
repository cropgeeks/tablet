Allocating Memory
=================

Tablet is written in Java, and due to way the Java Runtime works the amount of memory available for use by it must be defined before the application is started. The default value set at install time is one gigabyte (1024MB). If you need to allocate more (or less) memory than this, the setting can be adjusted by following the relevant instructions below.

Note though, that for any 32-bit system, the maximum amount of memory Tablet will be able to use will be somewhere between 1.5GB and 2GB, regardless of the total amount of memory installed. If you have data sets requiring more memory than this then you must run a 64-bit copy of Tablet on a 64-bit operating system.

Windows & Linux
---------------

Navigate to the directory in which Tablet is installed and locate the file **tablet.vmoptions** and open it with a text editor. You will see a line containing **-Xmx1024m** or **-Xmx4096m** - replace '1024' or '4096' with a memory allocation value (in MB) of your choice.

macOS
-----

Navigate to Tablet's application icon (usually located in /Applications) and CTRL/right-click the icon, selecting **Show Package Contents** from the popup menu. Open /Contents/vmoptions.txt and replace the **4096** part of the line containing **-Xmx4096m** with a value (in MB) of your choice. The default is 4GB of memory.
