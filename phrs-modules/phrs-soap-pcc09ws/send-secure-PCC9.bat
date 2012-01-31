cls
call title Send SECURE PCC09
call mvn compile
call mvn test-compile

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="https://localhost:8089/testws/pcc9 https://localhost:8989/testws/pcc10  d:\\srfg-phrs-core-keystore.ks icardea"
pause