This directory contains scripts used to manage/manipulate keystore and truststore files.

All this scripts requires the JAVA_HOME environment variable set.  

genKeystore.bash - used to generate a keysore. This scripts also shows the new created keystore. 
It requires only one argument the name for the keystore.

importTrustStore.bash - import a given keystore in to a trust store. 
It requires two arguments the keysore (which may be generated with the genKeystore.bash script)


removeKeystore.bash - removes a given keystore from a trust store.


showKeystore.bash - shows a given keysore/truststore.
It requires only one argument the keysore to show.


