/*
 * Project  :iCardea
 * File     : ActorClientExample.java
 * Encoding : UTF-8
 * Date     : Dec 6, 2011
 * User     : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.security.services;


import at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;

/**
 * This runnable class register a relation between a name-space, a PHRS id and
 * a protocol id, query for the protocol id and displays it.
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClientExample -Dexec.classpathScope=test<br/>
 * </pre> Take care this command does not compile the classes.
 *
 * @author m1s
 * @version 0.1
 * @since 0.1
 */
public final class ActorClientExample {

    /**
     * The unique name space identifier used in this test.
     */
    private static final String NAME_SPACE = "MY NAME SPACE";

    /**
     * The unique PHR System identifier used in this test.
     */
    private static final String PHRS_ID = "MY PHRS_ID";

    /**
     * The unique Protocol Id identifier used in this test.
     */
    private static final String PROTOCOL_ID = "MY PROTOCOL_ID";
    
    /**
     * Don't let anyone to instantiate this class.
     */
    private ActorClientExample() {
        // UNIMPLEMETNED
    }

    /**
     * Run this class from the command line.
     *
     * @param args the command lines arguments - no arguments are required.
     */
    public static void main(String[] args) throws TripleException {

        //final TriplestoreConnectionFactory connectionFactory =
        //        TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = PhrsStoreClient.getInstance().getTripleStore();//connectionFactory.getTriplestore();
        
        
        final ActorClient nameSpaceClient = new ActorClient(triplestore);
        // I register the relation between the name space, phrs id and 
        // protocol id.
        nameSpaceClient.register(NAME_SPACE, PHRS_ID, PROTOCOL_ID);


        // I register the relation between the name space, phrs id and 
        // protocol id.
        final String protolId =
                nameSpaceClient.getProtocolId(NAME_SPACE, PHRS_ID);
        final String msg =
                String.format("The protocol id for name space : %s and phrs id : %s is %s", NAME_SPACE, PHRS_ID, protolId);
        System.out.println(msg);
    }
}
