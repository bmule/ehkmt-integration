cls
call title Start Socketlistener

rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener -Dexec.classpathScope=test -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass
rem srfg-phrs-core-keystore.ks icardea
rem set THE_DIR=%CD%
rem set PHR_TRUST_STORE=%THE_DIR%\src\main\resources\srfg-phrs-core-keystore.ks
rem echo ** Trust store= %PHR_TRUST_STORE%
rem  -Djavax.net.ssl.trustStore=%PHR_TRUST_STORE% -Djavax.net.ssl.trustStorePassword=icardea

rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass

rem using the pcc09 truststore

 if "%COMPUTERNAME%" == "SRDC-ICARDEA" (
     echo ** SRDC machine **
     call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=C:\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass

rem local truststore
rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=C:\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks -Djavax.net.ssl.trustStorePassword=icardea

 )  else (
     echo ** salk machine **
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass

rem local truststore
rem      call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks -Djavax.net.ssl.trustStorePassword=icardea

 )

pause
