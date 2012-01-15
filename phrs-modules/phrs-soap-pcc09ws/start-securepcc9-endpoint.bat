cls

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"


