call title Start PCC9-Endpoint

cls

rem call mvn clean compile
rem call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9EndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8089 testws/pcc9"


pause