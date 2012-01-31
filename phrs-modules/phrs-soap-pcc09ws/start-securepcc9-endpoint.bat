cls
call title Start SECURE PCC09 Endpoint
call mvn clean compile
call mvn test-compile
rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="icardea-server.lksdom21.lks.local 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"
pause