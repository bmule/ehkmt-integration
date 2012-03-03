set PHRS_HOME=D:\srfg\new-phrs\icardea-phrs
set SESAME_TOMCAT=D:\srfg\tomcat\phrs-tomcat-6
set CATALINA_HOME=%SESAME_TOMCAT%
set mypwd=%CD%

call %SESAME_TOMCAT%\startup.bat
pause
cd %PHRS_HOME%\phrs-soap-pcc09ws\
start start-securepcc9-endpoint.bat

cd %PHRS_HOME%\phrs-soap-pcc10ws\
start start-securepcc10-endpoint.bat

start start-socketlistener.bat

cd %mypwd%