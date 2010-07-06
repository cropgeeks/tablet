@ECHO OFF
CALL libraries.bat

SET avcp=%avcp%;%lib%\sqlite-jdbc-3.6.19-win32.jar

java -Xmx1024m -cp .;classes;%avcp% tablet.gui.Tablet %1 %2 %3 %4 %5