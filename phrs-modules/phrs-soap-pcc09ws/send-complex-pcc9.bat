cls

call mvn compile
call mvn test-compile

call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.SendComplexPcc09MessageExample -Dexec.classpathScope=test -Dexec.args="http://localhost:8989/testws/pcc9"
