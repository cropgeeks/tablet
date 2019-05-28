@ECHO OFF

REM CALL libraries.bat
REM C:\Java\JDK8\bin\java  -Xmx6000m -cp .;classes;%avcp% tablet.gui.Tablet %1 %2 %3 %4 %5

C:\Java\JDK11\bin\java -Xmx6g -cp .;res;classes;lib\* tablet.gui.Tablet %1 %2 %3 %4 %5
