
rem this bash script is used to pack all the phrweb project in to an
rem singular war file. The location of the file is target/ directory. 

cls

rem del /s target

call mvn clean

echo "--------------------------------------------------------------------"
echo "The environment is clean now (all the previous changes are removed)."
echo "--------------------------------------------------------------------"

mkdir   target

call tartool  zxf src\main\assembly\classes.tgz
move /y classes  target\classes\

call mvn package -DskipTests=true


echo "--------------------------"
echo "The phrs war file is ready."
echo "---------------------------"
