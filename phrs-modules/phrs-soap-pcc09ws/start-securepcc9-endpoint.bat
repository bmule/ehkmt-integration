call title Start SECURE PCC09 Endpoint

cls
if "%COMPUTERNAME%" == "SRDC-ICARDEA" (
    echo ** SRDC ICARDEA machine   using icardea-server.lksdom21.lks.local, not 10.0.2.15 **
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.args="icardea-server.lksdom21.lks.local 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"

)  else (
    echo ** SALK machine **
    call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.args="icardea-server.lksdom21.lks.local 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"

)
pause