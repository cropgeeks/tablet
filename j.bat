@ECHO OFF
CALL libraries.bat

java -Xmx1200m -cp .;classes;%avcp% tablet.gui.Tablet %1