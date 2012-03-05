cls
call title Step 10 Problems example
rem call mvn clean compile
rem call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProblemsStep10ClientExample -Dexec.classpathScope=test
pause