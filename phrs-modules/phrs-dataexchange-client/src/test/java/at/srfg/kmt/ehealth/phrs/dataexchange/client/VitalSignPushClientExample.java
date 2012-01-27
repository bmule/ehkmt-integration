/*
 * Project :iCardea
 * File : VitalSignPushClientExample.java
 * Encoding : UTF-8
 * Date : Jan 27, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import javax.xml.bind.JAXBException;


/**
 * Runnable class able to push more vital signs to to the PHRS repository.<br/>
 * More precisely this class adds four <i>Vital Sign</i> to the underlying
 * persistence layer.<br/> To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignPushClientExample -Dexec.classpathScope=test<br/>
 * </pre> Take care this command does not compile the classes. <br/> <b>Nota
 * Bene : </b> the repository URI and reposiotry ID are configured with the file
 * named "generic_triplestore.xml" placed in classpath.
 *
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class VitalSignPushClientExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException {

        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final VitalSignClient client = new VitalSignClient(triplestore);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                "Free text note for systolic.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "100",
                Constants.MM_HG);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE,
                "Free text note for diasystolic.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "80",
                Constants.MM_HG);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_BODY_HEIGHT,
                "Free text note for body height.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "180",
                Constants.CENTIMETER);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_BODY_WEIGHT,
                "Free text note for body weight.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "80",
                Constants.KILOGRAM);

        System.out.println("Vital Signs information was push to the reposiotry.");
    }
}
