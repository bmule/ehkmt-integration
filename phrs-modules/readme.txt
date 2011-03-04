The PHRS is a (multi-module) maven based project. All the project related
actions are done using maven.
This provect uses maven was build and testted with maven 2.2.1.



Compile and Test the PHRS 

Go in the phrs-modules directory and run the command

mvn test


This command will compile and run all the PHRS tests, this command may take a
while.
If the command was successfully you must be able to see on the standard output 
the line :
....
[INFO] BUILD SUCCESSFUL
....

Otherwise this line will be replaced by a 
[INFO] BUILD FAIL !

The source of error will be listed in upper also in the console.

You can you can clean the files generated on the previous tests/compile action
by using the clean command together with the test command

mvn clean test

It is recommendable to use the clean command if you are dealing with repetitive
test.



Packing the PHRS

Go in the phrs-modules directory and run the command

mvn package

If everything works fine you can find the new generated ear file under the
location <PHRS-SSOURCES>/phrs-modules/phrs-ear/target/ the name of the 
ear file name follow the pattern phrs-ear-<VERSION>.ear.
You can deploy this ear under any JEE application server.



Install the PHRS using JBoss and Postgres.

Note : the PHRS is compatible with any other JEE5 compellable 
application servers. It is also possible to use it with any database system (than
Postgres) - we choose jboss/postgres because there are mature products are good
documented, etc.

The PHRS is an enterprise application and it requires an application server.
The actual implementation is uses the JBoss 5.1.0 GA. 
In order to install the PHRS system you need to download the JBoss 5.1.0 GA
on your local machine on a know location. 

After this change the "jboss.home" and "jboss.domain.home" maven properties
from the pom file located <PHRS-SSOURCES>/phrs-modules/phrs-ear/pom.xml
according to your needs.

By example :
    <properties>
        <jboss.home><MY-JBOSS>/jboss-5.1.0.GA</jboss.home>
        <jboss.domain.home><MY-JBOSS>/jboss-5.1.0.GA/server/default</jboss.domain.home>
    </properties>


This configuration anounces maven that my jBoss application server is installed on
'<MY-JBOSS>/jboss-5.1.0.GA' and my application server domain (configuration) is
located in <MY-JBOSS>/jboss-5.1.0.GA/server/default.

The actual implementation requires a JDBC connection accessible under the JNDI 
name : phrs-Datasource.

The current solution uses JBoss 5.1.0.GA and Postgres, the configuration file
for the JDBC connection named phrs-ds.xml can be found in the 
<PHRS-SSOURCES>/etc/. You need to copy this file on your application server
domain (configuration) root; in the previous example the application server
domain root is <MY-JBOSS>/jboss-5.1.0.GA/server/default).

You also need to specify a database driver.
The current solution uses Postgres: in order to make the Postgres driver available
copy the postgres driver located in the 
<PHRS-SSOURCES>/etc/postgresql-9.0-801.jdbc4.jar file 
in to
<MY-JBOSS>/jboss-5.1.0.GA/server/default/lib
directory.


after all this configuration are done run go in to the 
<PHRS-SSOURCES>/phrs-modules/phrs-ear directory and run 

mvn cargo:deploy


