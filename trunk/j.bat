@ECHO OFF
CALL libraries.bat

java -Xmx1024m -Djava.library.path=lib\sqlite -cp .;classes;%avcp% tablet.gui.Tablet %1 %2 %3 %4 %5