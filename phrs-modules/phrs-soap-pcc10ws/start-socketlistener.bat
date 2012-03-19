cls
call title Start Socketlistener

rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener -Dexec.classpathScope=test -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass
rem srfg-phrs-core-keystore.ks icardea
set THE_DIR=%CD%
set PHR_TRUST_STORE=%THE_DIR%\src\main\resources\srfg-phrs-core-keystore.ks
echo ** Trust store= %PHR_TRUST_STORE%
rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=%PHR_TRUST_STORE% -Djavax.net.ssl.trustStorePassword=icardea

call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener  -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=D:\\srdc\\codes\\icardea-google\\icardea\\icardea-caremanager-ws\\src\\test\\resources\\jssecacerts -Djavax.net.ssl.trustStorePassword=srdcpass



pause
