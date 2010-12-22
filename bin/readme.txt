Contains a lot of common used scripts.
Most of the scripts paced in this direcotry must be run in the same direcotry with your pom.xml file, for better results place this (bin) directroy in to your PATH.
Under unix like system you can do this kike this :

PATH=$PATH:`pwd`
export $PATH




Directory content

phrs-create-osgibundle.bash - create a maven related directory structure for an OSGI bundle. This scripts requires the apache felix maven plugin  (see the http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html fro more details)

phrs-create-plainjar.bash - create a maven related directory structure deployable like a plain jar file. 
phrs-create-webapp.bash - create a maven related directory structure deployable like a war file. 
phrs-deploy-geronimo.bash - deploy a resource on geronimo server.
