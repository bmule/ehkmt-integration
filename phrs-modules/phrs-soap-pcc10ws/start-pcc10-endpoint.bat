cls
call title Start PCC10 endpoint Simulation 
call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10EndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8989 testws/pcc10" -Dpcc10.process="true"
pause