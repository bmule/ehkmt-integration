cls
call title Drone UI Medcatlist 

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.DroneUI -Dexec.classpathScope=test -Dexec.args="MEDLIST"

pause
