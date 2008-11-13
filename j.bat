@ECHO OFF
CALL libraries.bat

java -Xmx1200m -cp .;classes;%avcp% av.gui.AssemblyViewer %1