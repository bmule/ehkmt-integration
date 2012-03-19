cls
call title Send  pcc09


call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srfg\\new-phrs\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea COBSCAT 191 Suzie Mayr F"
call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srfg\\new-phrs\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDLIST 191 Suzie Mayr F"
rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="https://icardea-server.lksdom21.lks.local/ehrif/pcc/ https://icardea-server.lksdom21.lks.local:8989/testws/pcc10 D:\\srfg\\new-phrs\\icardea-phrs\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks icardea MEDCCAT 191 Suzie Mayr F"

pause