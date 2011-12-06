/*
 * Project  :iCardea
 * File     : NameSpaceClientExample.java
 * Encoding : UTF-8
 * Date     : Dec 6, 2011
 * User     : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;

/**
 *
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.NameSpaceClientExample -Dexec.classpathScope=test<br/>
 * </pre> Take care this command does not compile the classes.
 *
 * @author m1s
 * @version 0.1
 * @since 0.1
 */
public class NameSpaceClientExample {

    /**
     * Run this class from the command line.
     *
     * @param args the command lines arguments - no arguments are required.
     */
    public static void main(String[] args) {

        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();
        final NameSpaceClient nameSpaceClient = new NameSpaceClient(triplestore);

    }
}
