cls
call title Step 11 Problems example
rem call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProblemsStep11ClientExample -Dexec.classpathScope=test
pause