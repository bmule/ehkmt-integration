The jndi.properties is required by the Arquillian test framework.
Note : this jndi are JBoss specific, if you use other Application Servers you must
adjust it according with your needs (Application Servers).

This file is also placed in the classpath and it it loaded and used when you use
the new instance from InitialContext or every time when you use the JNDI in a
direct way lookup method or indirect (e.g. dependency injection).