cls
call title Start SECURE PCC10 Endpoint SIMULATION
rem call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="icardea-server.lksdom21.lks.local 8989 testws/pcc10 srfg-phrs-core-keystore.ks icardea"
pause