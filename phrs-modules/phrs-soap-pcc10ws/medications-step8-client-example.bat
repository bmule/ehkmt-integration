cls
call title Step 8 Medications client example
rem call mvn clean compile
rem call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.MedicationsStep8ClientExample -Dexec.classpathScope=test

pause
