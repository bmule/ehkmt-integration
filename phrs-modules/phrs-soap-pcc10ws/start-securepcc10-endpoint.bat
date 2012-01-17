cls

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="icardea-server.lksdom21.lks.local testws/pcc10 srfg-phrs-core-keystore.ks icardea"
