@ECHO OFF
CALL libraries.bat

java -Xmx1024m -cp .;classes;%avcp% tablet.gui.Tablet %1 %2 %3 %4 %5