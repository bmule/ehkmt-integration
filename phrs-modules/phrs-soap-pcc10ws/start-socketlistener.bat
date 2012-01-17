cls

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener -Dexec.classpathScope=test -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.ssl.trustStore=dddd -Djavax.net.ssl.trustStorePassword=icardea


