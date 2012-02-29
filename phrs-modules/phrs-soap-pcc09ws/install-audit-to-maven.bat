cls

echo on
 
call mvn install:install-file   -Dfile=icardea-atnalog-client-1.0-SNAPSHOT.jar -DgroupId=tr.com.srdc.icardea -DartifactId=icardea-atnalog-client -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
echo "Audit icardea-atnalog-client  was install"

pause
dir
