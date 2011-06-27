This directory contains a set of curl based test files use to trigger different 
Controlled Vocabulary related WEB services.


curl-load-tags.bash
Used to trigger the default volcabulary items load process. Use this if you don't
have any kind of controlled terms in your vocabulary. 


curl-get-for-tag.bash
Used to get all the items tagged with a certain tag. The syntax for the tag is :
"code system code : tag code".


curl-get-contr-items.bash
Used to get all the items with a certain code system.
