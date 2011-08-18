package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;



/**
 * Runnable class able to add and list on the standard output vital signs.
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClientExample -Dexec.classpathScope=test</br>
 * 
 * Take care the does not compile the classes.
 * @author mradules
 */
public class VitalSignClientExample {

    public static void main(String... args) throws GenericRepositoryException, TripleException {
        
        final VitalSignClient client = new VitalSignClient();
        
        client.addVitalSign(Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                "Free text note for systolic.",
                "201006010000",
                "100",
                Constants.MM_HG);

        client.addVitalSign(Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE,
                "Free text note for diasystolic.",
                "201006010000",
                "80",
                Constants.MM_HG);
        
        client.addVitalSign(Constants.ICARDEA_INSTANCE_BODY_HEIGHT,
                "Free text note for body height.",
                "201006010000",
                "180",
                Constants.CENTIMETER);
        
        client.addVitalSign(Constants.ICARDEA_INSTANCE_BODY_WEIGHT,
                "Free text note for body weight.",
                "201006010000",
                "80",
                Constants.KILOGRAM);
        
        final Iterable<Triple> vitalSigns = client.getVitalSigns();
        for (Triple vitalSign : vitalSigns ) {
            System.out.println(vitalSign);
        }
    }
}
