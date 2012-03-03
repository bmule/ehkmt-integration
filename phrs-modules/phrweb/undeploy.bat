cls

rem set tomcat_home="D:\tomcat\apache-tomcat-6.0.20"

set tomcat_home="D:\\srfg\\tomcat\\phrs-tomcat-6"
call mvn  cargo:undeploy  -Dcatalina.home=%tomcat_home%

echo ""
echo "The phrweb is undeployed(removed) from web container located here :"%tomcat_home%
echo ""
