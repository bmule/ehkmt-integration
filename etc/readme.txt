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
