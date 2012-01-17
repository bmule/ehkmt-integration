cls

call mvn clean compile
call mvn test-compile
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.StartSocketListener -Dexec.classpathScope=test -Dexec.args="icardea-server.lksdom21.lks.local icardea"


