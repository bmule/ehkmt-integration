cls
call title Send Vital Sign pcc10
call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.VitalSignClientExample -Dexec.classpathScope=test
pause


