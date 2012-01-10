cls

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.PCC9EndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8989 testws/pcc9"


