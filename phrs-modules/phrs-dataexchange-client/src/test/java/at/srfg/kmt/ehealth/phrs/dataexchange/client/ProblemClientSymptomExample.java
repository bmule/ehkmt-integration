package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.ihe.ProblemEntryPCC10;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.QUPCAR004030UVUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;

/**
 * Runnable class able to add  one (or more) problem entry.<br/>
 * More precisely this class adds a <i>Symptom</i> with the estabilish code
 * <i>Fever</i> to the underlying persistence layer; after this it generates 
 * PCC10 conform message and serialize it in to a file named 
 * <i>problem-enrty.xml</i> stored in to the temporary directory. </br> 
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemClientSymptomExample -Dexec.classpathScope=test</br>
 * 
 * Take care the does not compile the classes.
 * @author mradules
 */
public class ProblemClientSymptomExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {
        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final ProblemEntryClient client = new ProblemEntryClient(triplestore);

        // this adds a problem-symptom named fever
        client.addProblemEntry(
                owner,
                Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "Free text note for the problem.",
                Constants.HL7V3_FEVER);

        final Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pCC10Message = ProblemEntryPCC10.getPCC10Message(beans);
        QUPCAR004030UVUtil.toWriteInTemp(pCC10Message, "problem-enrty");



        // TAKE CARE !!!!!!
        // This lines wipe out everithing alfter the client example ends its 
        // main method.
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();

    }
}
