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



Install the phrs enterprise archive

This steep requires the packaging, please be sure that the packaging sequence is done
before you can proceed with this.
The enterprise (archive)  deployment requires information about your JBoss location, 
you can specify them by altering the file : 

<PHRS-SSOURCES>/phrs-modules/phrs-ear/pom.xml

or you can specify this properties like command line parameters. This parameters are :
-Djboss.home=<MY-JBOSS HOME> 
and 

-Djboss.domain.home=<MY-JBOSS APPLCATION DOMAIN>

, both parameters are required.

The maven deploy command (for the case with command line parameters) looks like this :

mvn cargo:deploy -Djboss.home=<YOUR JBOSS HOME> -Djboss.domain.home=<YOUR JBOSS APPLCATION DOMAIN>

If you choose to alter the pom file (not recommended) the command like will look like this :

mvn cargo:deploy



Extra configurations

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

