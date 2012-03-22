cls
call title Send Medlist Cobscat pcc09

if "%COMPUTERNAME%" == "SRDC-ICARDEA" (
    echo ** SRDC ICARDEA machine   using 10.0.2.15 **
	call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://10.0.2.15/ehrif/pcc/ https://10.0.2.15:8989/testws/pcc10 C:\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDLIST 191 Suzie Mayr F"
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://10.0.2.15/ehrif/pcc/ https://10.0.2.15:8989/testws/pcc10 C:\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea COBSCAT 191 Suzie Mayr F"

)  else (
    echo ** SALK machine **
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srdc\\codes\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDLIST 191 Suzie Mayr F"
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srdc\\codes\\icardea-google\\icardea\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea COBSCAT 191 Suzie Mayr F"
)


pause