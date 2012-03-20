cls
call title Send SECURE PCC09 MEDLIST
rem call mvn compile
rem call mvn test-compile
if "%COMPUTERNAME%" == "SRDC-ICARDEA" (
    echo ** SRDC machine **
	call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 C:\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDLIST 191 Suzie Mayr F"
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 C:\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea COBSCAT 191 Suzie Mayr F"

)  else (
    echo ** salk machine **
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srdc\\codes\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDLIST 191 Suzie Mayr F"
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srdc\\codes\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea COBSCAT 191 Suzie Mayr F"
)

rem mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="https://localhost:8089/testws/pcc9 https://localhost:8989/testws/pcc10  d:\\srfg-phrs-core-keystore.ks icardea"
pause