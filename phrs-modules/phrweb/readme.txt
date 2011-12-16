This module contains all the classes (and dependencies) the PHR portal.

To generate a PHR war can be generated with the following command : 
mvn clean package -DskipTests=true

To deploy a PHR war can be generated with the following command : 
mvn clean package -DskipTests=truemvn cargo:deploy -Dcatalina.home=<YOUR TOMCAT HOME>
Note : please note that "catalina.home" property is pointing to the your tomcat
local instalation.
Note : the PHR web portal requires a tomcat 6x instance.


