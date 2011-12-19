This module contains all the classes (and auxiliary resources) related with the
PHR portal.


How to pack the PHR web portal ?

To build a  PHR war file that contains all the PHR classes (and related
resources) use the following command : 

./package.bash

This script will gain all the required resources and packs them together with
the already compiled classes located in the archive named classes.tgz located
in the directory ...src/main/assembly. The result is singular war file that
contains all the classes and required resources.
This script is maven based. 
The new created war file is named phrweb.war and it is located in the target
directory. This file can be deployed in any tomcat 6x web container. 


How to Deploy the PHR web portal ?

The PHR war file can be de

To deploy a PHR war in to an existent web contain use the following command : 

./deploy.bash

This bash script builds the  PHR war file (previous explained) and deployed in to
an existent web container. The location for the web container can be specified
with the "catalina.home" maven property - for more details consult the deploy
script. 

Alternative you can use the following command :

mvn package cargo:deploy -Dcatalina.home=<YOUR TOMCAT HOME>

This command it only package and deploy the PHR war file, it does not compile
the any PHR classes. It is recommended to use the package.bash script to prepare
the PHR war file before the deploy take pace.    


Why I don't use mvn compile to compile PHR related classes ?

The PHR web uses byte-code originated from groovy - this classes are compiled
with a extern compiler (not maven) and after this the compiled classes are used
together with maven to build (and deploy) the PHR war file. All the compiled
(java and groovy) classes are archived in the file named "classes.tgz" located
in the "...src/main/assembly" directory.
The actual implementation uses the maven repository transitive dependencies
management abilities to collect all the required dependencies (libraries).

Note : because the generated classes originate from a separate system (where
are compiled according with groovy needs) there is API compatibility can not be
validated during the compile step; in this way classes that are incompatible 
are assambled in to the final war.

