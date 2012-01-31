cls
call title Step 6 Problems example
call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProblemsStep6ClientExample -Dexec.classpathScope=test
pause