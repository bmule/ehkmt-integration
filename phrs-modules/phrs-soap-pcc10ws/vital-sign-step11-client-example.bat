cls
call title Step 11 Vital sign example
rem call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.VitalSignStep11ClientExample -Dexec.classpathScope=test


pause