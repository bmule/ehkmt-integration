package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;


/**
 * Runnable class able to add and list on the standard output vital signs.
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemClientExample -Dexec.classpathScope=test</br>
 * 
 * Take care the does not compile the classes.
 * @author mradules
 */
public class ProblemClientExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException {
        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final ProblemEntryClient client = new ProblemEntryClient();

        // this adds a problem-symptom named fever
        client.addProblemEntry(owner, 
                Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201007010000",
                "Free text note for the problem.",
                Constants.HL7V3_FEVER
                );

        // this adds a problem-symptom named temp loss of speech
        client.addProblemEntry(owner, Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201007010000",
                "Free text note for the problem.",
                Constants.HL7V3_TEMPORRALY_LOSS_OF_SPEECH);

        final Iterable<Triple> problems = client.getProblemEntries();

        for (Triple problem : problems) {
            System.out.println(problem);
        }

        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();

    }
}
