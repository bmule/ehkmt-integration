package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
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
 * Runnable class able to add multiple medication entries.<br/>
 * More precisely this class adds a <i>8 Medications</i> to the underlying
 * persistence layer; after this it generates PCC10 conform message and
 * serialize it in to a file named <i>medication.xml</i> stored in to the
 * temporary directory. The exact location for this file is listed in the log
 * file (located in target/log.out)<br/>
 * To run this class from maven environment use :
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.MedicationsStep8ClientExample -Dexec.classpathScope=test
 * </pre>
 * 
 * Take care this command does not compile the classes. <br/>
 * <b>NB: </b> this class will remove/clean the triplestore related files. The
 * location for this files is configured via the configuration file named
 * generic_triplestore.xml. <br/>
 * 
 * @author fstrohmeier
 * @version 0.1
 * @since 0.1
 */
public class MedicationsStep8ClientExample {

	public static void main(String... args) throws GenericRepositoryException,
			TripleException, IllegalAccessException, InstantiationException,
			JAXBException {
            
		final String owner = Constants.PROTOCOL_ID_UNIT_TEST;
		final TriplestoreConnectionFactory connectionFactory = TriplestoreConnectionFactory
				.getInstance();
		final GenericTriplestore triplestore = connectionFactory
				.getTriplestore();

		final MedicationClient client = new MedicationClient(triplestore);
		client.addMedicationSign(owner, "Free text note for the medication 1.",
				Constants.STATUS_COMPELETE, "200812010000", "201106101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "25", Constants.MILLIGRAM,
				"Prednisone", "C0032952");
		
		client.addMedicationSign(owner, "Free text note for the medication 2.",
				Constants.STATUS_COMPELETE, "200812010000", "201106101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "40", Constants.MILLIGRAM,
				"Pantoprazole (Pantoloc)", "C0081876");

		client.addMedicationSign(owner, "Free text note for the medication 3.",
				Constants.STATUS_COMPELETE, "199910101010", "201106101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "5", Constants.MILLIGRAM,
				"Concor", "C0110591");

		client.addMedicationSign(owner, "Free text note for the medication 4.",
				Constants.STATUS_COMPELETE, "199910101010", "201010101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.DROPS,
				"Psychopax (Diazepam)", "C0012010");

		client.addMedicationSign(owner, "Free text note for the medication 5.",
				Constants.STATUS_COMPELETE, "198010101010", "20110601010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "300", Constants.MILLIGRAM,
				"Convulex", "C0591288");

		client.addMedicationSign(owner, "Free text note for the medication 6.",
				Constants.STATUS_COMPELETE, "20090101010", "201106101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "20", Constants.MILLIGRAM,
				"Ebetrexat(Methotrexate)", "C0025677");

		client.addMedicationSign(owner, "Free text note for the medication 7.",
				Constants.STATUS_COMPELETE, "20090101010", "201106101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "10", Constants.MILLIGRAM,
				"Folsan(Folic Acid)", "C0016410");

		client.addMedicationSign(owner, "Free text note for the medication 8.",
				Constants.STATUS_COMPELETE, "199910101010", "201010101010",
				client.buildNullFrequency(),
				Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
				"Magnosolv(Magnesium)", "C0024467");

        client.addMedicationSign(owner, "Free text note for the medication 8.",
                Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                client.buildNullFrequency(),
                Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                "BOB Magnosolv(No CODE)");

		final Iterable<String> uris = client.getMedicationURIsForUser(owner);
		final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
		final Set<DynaBean> beans = new HashSet<DynaBean>();
		for (String uri : uris) {
			final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
			beans.add(dynaBean);
		}

		final QUPCIN043200UV01 pCC10Message = MedicationSignPCC10
				.getPCC10Message(owner,beans);
		QUPCAR004030UVUtil.toWriteInTemp(pCC10Message, "medications-step8");

		// TAKE CARE !!!!!!
		// This lines wipe out everything after the client example ends its
		// main method.
		((GenericTriplestoreLifecycle) triplestore).shutdown();
		((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
	}
}
