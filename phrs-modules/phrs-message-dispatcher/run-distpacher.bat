cls

call mvn compile
call mvn test-compile

call mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dispatch.impl.DispatcherImplExample -Dexec.classpathScope=test
