@ECHO OFF
CALL libraries.bat

java -Xmx1200m -cp .;classes;%avcp% av.io.TestReader %1