/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ihe;

import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.HashSet;
import java.util.Set;
import static at.srfg.kmt.ehealth.phrs.dataexchange.util.QUPCAR004030UVUtil.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.QUPCIN043200UV01;
import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01ControlActProcess;
import org.hl7.v3.QUPCIN043200UV01MFMIMT700712UV01Subject5;
import org.hl7.v3.REPCMT004000UV01CareProvisionEvent;
import org.hl7.v3.REPCMT004000UV01PertinentInformation5;

/**
 *
 * @author mradules
 */
public class VitalSignPCC10 {

    private final VitalSignClient vitalSignClient;

    /**
     * Builds a <code>ProblemClient</code> instance for a given triplestrore.
     * 
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the <code>triplestore</code> 
     * argument is null. 
     */
    VitalSignPCC10(GenericTriplestore triplestore) {

        if (triplestore == null) {
            throw new NullPointerException("The triplestore");
        }

        vitalSignClient = new VitalSignClient(triplestore);
    }

    public String getPCC10Message() throws TripleException {

        final Iterable<Triple> vitalSigns = vitalSignClient.getVitalSigns();
        final Set<String> rootIds = new HashSet<String>();
        String subject = "";
        for (Triple triple : vitalSigns) {
            final String s = triple.getSubject();
            if (!subject.equals(s)) {
                
                final String predicate = triple.getPredicate();
                final String value = triple.getValue();
            }
        }

        return null;
    }
}
