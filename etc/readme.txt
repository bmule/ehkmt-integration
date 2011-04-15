Contains a set common purposed files.



jboss-log4j.xml 

This file is the logging configuration for the JBoss AS.
In order to use it copy it in <JBoss_Home>/server/defaut/conf directory. 
Note : the JBoss brings it own configuration file also named jboss-log4j.xml
and placed in the <JBoss_Home>/server/defaut/conf directory.



phrs-ds.xml

This is the PHR_S datasource file, it defines the PHR_S connection to the
database. 
In order to use it copy it <JBoss_Home>/server/defaut/deploy directory. 



postgresql-9.0-801.jdbc4.jar
This is the PostgreSQL database driver (used by the PHR_S database connection)
in order to use it you must place it the classpath for your application server.
One way to do this is to copy it in <JBoss_Home>/server/defaut/lib directory.



run.conf

The debug configuration for the application server.
In order to use it copy it in <JBoss_Home>/bin/ directory and overwrite the
original configuration. This configuration allows you to debug remote the 
code that is running inside of the application server.
This file is only for developing/testing purpose. 



phrs_mes-service.xml

This file is used to configurate the JBoss application server JMS queue, more preciselly
this file is used to defines a JMS queue named phrs_queue (JNDI : /queue/phrs_queue). 
This file is JBoss 5.1.X  specific.
To use it please copy it in the <JBoss_Home>/server/defaut and restart the Jboss server.



QUPC_AR004040UV_Service.jar and QUPC_AR004040UV_Service-sources.jar
This are the generated classes for the QUPC_AR004040UV_Service, the classes are generated with the wsconsume tool from JBoss distribution.
This file is required like maven depndedecies, to do this you need to import it in your local repository using this command :

mvn install:install-file  -Dfile=QUPC_AR004040UV_Service.jar  -Dsource=QUPC_AR004040UV_Service-sources.jar -DgroupId=org.hl7.v3 -DartifactId=QUPC_AR004040UV_Service -Dversion=0.1 -Dpackaging=jar

mvn install:install-file  -Dfile=QUPC_AR004030UV_Service.jar  -Dsource=QUPC_AR004030UV_Service-sources.jar -DgroupId=org.hl7.v3 -DartifactId=QUPC_AR004030UV_Service -Dversion=0.1 -Dpackaging=jar
