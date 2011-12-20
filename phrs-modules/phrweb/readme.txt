This module contains all the classes (and auxiliary resources) related with the
PHR portal.



I.How to pack the PHR web portal ?

As the name suggest the package script is used to assemble the PHR System web
application. The result for this script is a singular war file that can be
deployed in any web container (2.5. Servlet API compatible). The name and the 
file location will be described in the next sections.

To build a  PHR war file that contains all the PHR classes (and related
resources) use the following script : 

./package.bash for UNIX like environment

or

package.bat for MS environment

Preconditions :
1.The package script is Maven based, please ensure that Maven 2.2 (or 3.0) is
installed and configurated.
2.The package script requires internet access (it gains all the dependencies
from public repositories).
3.The QUPC_AR004040UV_Service and QUPC_AR004030UV_Service dependencies must be 
installed in the local maven repository. For more details about this please
consult the ../etc/readme.txt file. Those dependencies must be installed only
once.

Functionality : 
This script will gain all the required resources and packs them together with
the already compiled classes located in the archive named "classes.tgz" located
in the directory ...src/main/assembly. The result is singular war file that
contains all the classes and required resources.
The new created war file is named "phrweb.war" and it is located in the "target"
directory. This file can be deployed in any web container that Servlet 2.5. API
 compatible (e.g. Tomcat 6x). 



II.How to Deploy the PHR web portal ?

To deploy a PHR war in to an existent web contain use the following command : 


./deploy.bash for UNIX like environment

or

deploy.bat for MS environment


Preconditions :
1.The package script is Maven based, please ensure that Maven 2.2 (or 3.0) is
installed and configurated.
2.This scripts builds a PHRS war file before it deploy it, this is the reason
why all the preconditions from the previous section ("I.How to pack the PHR web
portal ?") are also valid here. Please consult the previous section for more 
details about preconditions.

This bash script builds the  PHR war file (previous explained) and deployed in to
an existent web container. The location for the web container can be specified
with the "catalina.home" Maven property, you need to adjust this value to proper
one according with your system.

Manual deployment
 
Alternative you can use the following maven(cargo) command :

mvn package cargo:deploy -Dcatalina.home=<YOUR TOMCAT HOME>

This command it only package and deploy the PHR war file, it does not compile
the any PHR classes and it also does not claims any resources; the classes and
related resources are solved in a previous "packing" step. You can red more 
details about packing in the previous section 
("I.How to pack the PHR web portal ?").


III.Why I don't use mvn compile to compile PHR related classes ?

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

