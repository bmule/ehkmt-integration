cls
call title Start SECURE PCC10 Endpoint

rem call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="icardea-server.lksdom21.lks.local 8989 testws/pcc10 srfg-phrs-core-keystore.ks icardea"

  if "%COMPUTERNAME%" == "SRDC-ICARDEA" (
      echo ** SRDC machine  using 10.0.2.15 **
      call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample  -Dexec.args="10.0.2.15 8989 testws/pcc10 srfg-phrs-core-keystore.ks icardea" -Dpcc10.process="true"

  ) else (
      echo ** SALK machine **
      call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample  -Dexec.args="icardea-server.lksdom21.lks.local 8989 testws/pcc10 srfg-phrs-core-keystore.ks icardea" -Dpcc10.process="true"

  )
pause