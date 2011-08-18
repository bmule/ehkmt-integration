package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;



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
        
        final ProblemClient client = new ProblemClient();
        
        // this adds a problem-symptom named fever
        client. addProblem(Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201007010000",
                "Free text note for the problem.",
                Constants.HL7V3_FEVER);
        
        // this adds a problem-symptom named temp loss of speech
        client. addProblem(Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201007010000",
                "Free text note for the problem.",
                Constants.HL7V3_TEMPORRALY_LOSS_OF_SPEECH);
        
        final Iterable<Triple> problems = client.getProblems();
        
        for (Triple problem : problems ) {
            System.out.println(problem);
        }
    }
}
