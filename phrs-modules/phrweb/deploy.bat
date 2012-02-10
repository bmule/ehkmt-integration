cls

rem call package.bat
rem icardeahome ... \\tools\\apache-tomcat-6.0.20
rem apache-tomcat-6.0.35
rem set tomcat_home=D:\\srdc\\codes\\icardea-google\\icardea\\tools\\apache-tomcat-6.0.20


set tomcat_home="D:\\srfg\\tomcat\\phrs-tomcat-6"

call mvn clean
rem Must install, not just compile
call mvn install -DskipTests=true

rem cargo
call mvn cargo:deploy  -Dcatalina.home=%tomcat_home%


echo "------------------------------------------"
echo "The phrweb is deployed on :"%tomcat_home%
echo "------------------------------------------"
