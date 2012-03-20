call title Start SECURE PCC09 Endpoint

cls

call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.args="icardea-server.lksdom21.lks.local 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"
pause