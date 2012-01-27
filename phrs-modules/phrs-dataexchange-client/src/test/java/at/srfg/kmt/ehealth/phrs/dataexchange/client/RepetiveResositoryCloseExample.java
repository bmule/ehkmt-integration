/*
 * Project :iCardea
 * File : RepetiveResositoryCloseExample.java
 * Encoding : UTF-8
 * Date : Jan 27, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.apache.commons.beanutils.DynaBean;


/**
 * This examples open and close the Client connection to the its underlying
 * store.
 *
 * @author M1s
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class RepetiveResositoryCloseExample {

    public static void main(String... args) throws TripleException, IllegalAccessException, InstantiationException, GenericRepositoryException {

        
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        // Here I obtain a triplestore instance.
        GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final String owner = "testOwner";
        // Here I build a a client for the connection
        ProblemEntryClient client = new ProblemEntryClient(triplestore);

        // Here I use the client
        client.addProblemEntry(
                owner,
                Constants.HL7V3_COMPILANT,
                Constants.STATUS_COMPELETE,
                "201007120000",
                Constants.HL7V3_SYMPTOM,
                "Free text note.",
                Constants.HL7V3_FATIQUE);

        Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        System.out.println("First iteration");
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            final String toString = DynaBeanUtil.toString(dynaBean);
            System.out.println(toString);
        }

        // Here I close the connection for the upper defined client.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        
        triplestore = connectionFactory.getTriplestore();
        
        client = new ProblemEntryClient(triplestore);

        // Here I use the client closed before
        client.addProblemEntry(
                owner,
                Constants.HL7V3_COMPILANT,
                Constants.STATUS_COMPELETE,
                "201007120000",
                Constants.HL7V3_SYMPTOM,
                "Free text note.",
                Constants.HL7V3_FEVER);
        
        uris = client.getProblemEntriesURIForUser(owner);
        dynaBeanClient = new DynaBeanClient(triplestore);
        System.out.println("Second iteration");
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            final String toString = DynaBeanUtil.toString(dynaBean);
            System.out.println(toString);
        }
    }
}
