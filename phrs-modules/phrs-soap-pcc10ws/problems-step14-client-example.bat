cls
call title Step 14 Problems example
call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProblemsStep14ClientExample -Dexec.classpathScope=test
pause