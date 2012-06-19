/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyEnhancer;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.presentation.utils.TimeUtils;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;


public class ToolTests {

    public ToolTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testConfigService() {
        //consent.service.endpoint
        ConfigurationService cs = ConfigurationService.getInstance();
        assertNotNull("testConfigService NULL", cs);
        String value = ConfigurationService.getInstance().getProperty("consent.service.endpoint");
        assertNotNull(value);
        System.out.println("value=" + value);

    }

    @Test
    //@Ignore
    public void testDateFormat_1() {
        String dateStr = HealthyUtils.formatDate(new Date(), null,
                HealthyUtils.DATE_PATTERN_LABEL_YEAR_MONTH_DAY_TIME);

        System.out.println("testDateFormat_1 dateStr=" + dateStr);
        assertNotNull(dateStr);

    }

    @Test
    public void testDateFormat() {

        String dateStr = getDateFormat();
        System.out.println("testDateFormat dateStr=" + dateStr);
        assertNotNull(dateStr);
    }

    public String getDateFormat() {
        String pattern = "yyyy-MM-dd HH:mm";
        String dateStr = CoreTestData.formatTimeDate(new DateTime(), pattern);


        return dateStr;
    }

    @Test
    public void testCoreTestMed() {
        MedicationTreatment med = CoreTestData
                .createMedication("testowner", Constants.STATUS_ACTIVE,
                        "200812010000", "201106101010", "40", Constants.MILLIGRAM, "Pantoprazole (Pantoloc)", "C0081876");
        assertTrue(Constants.STATUS_ACTIVE.equals(med.getStatusStandard()));
        assertTrue("medicationSummary_medicationStatus_true".equals(med.getStatus()));
        //medicationSummary_medicationStatus_true
    }

    @Test
    public void testTime() {
        long x = new Date().getTime();
        System.out.println("x=" + x);
        Date y = new Date(1328713915937l);
        assertEquals("1. equal", 0, TimeUtils.compare(new Date(), new Date()));

        assertEquals("2. 1", 1, TimeUtils.compare(new Date(1428713915937l), new Date(1328713915937l)));

        assertEquals("3. -1 ", -1, TimeUtils.compare(new Date(1328713915937l), new Date(1428713915937l)));

        assertFalse("4. false", TimeUtils.isBeginLess(new Date(), new Date()));

        assertFalse("5. false", TimeUtils.isBeginLess(new Date(1428713915937l), new Date(1328713915937l)));

        assertTrue("6. true", TimeUtils.isBeginLess(new Date(1328713915937l), new Date(1428713915937l)));

    }

    @Test
    public void testVocabEnhancer_URIFlattener() {
        String uri = "http://xxxx/yyy/zzz//aaa#123";
        
        String result=uri;
        if(result.startsWith("http://")) result = result.replace("http://", "");
        else if(result.startsWith("https://")) result = result.replace("https://", "");
 
        result = result.replaceAll("/","_");
        result = result.replaceAll("#","--");
        
        System.out.println("1 input="+uri+" result="+result);     
        assertEquals("xxxx_yyy_zzz__aaa--123", result);
        
        String flattened = VocabularyEnhancer.flattenUri(uri);
        System.out.println("2 input="+uri+" result="+flattened);
        assertEquals("xxxx_yyy_zzz__aaa--123", flattened);
    }
    @Test
    public void testVocabEnhancer_transformToI18term() {
        String expected ="Cholesterol";
        String uri = "http://xxxx/yyy/zzz//"+expected;
        String label= VocabularyEnhancer.transformToI18term(uri);
        System.out.println("testVocabEnhancer_transformToI18term uri="+uri+" label="+label);

        //nothing but with ending slash
        uri = "http://xxxx/yyy/zzz//";
        label= VocabularyEnhancer.transformToI18term(uri);
        System.out.println("testVocabEnhancer_transformToI18term uri="+uri+" label="+label);
        assertEquals(uri, label);

        //only one charater
        uri = "http://xxxx/yyy/z";
        label= VocabularyEnhancer.transformToI18term(uri);
        System.out.println("testVocabEnhancer_transformToI18term uri="+uri+" label="+label);
        assertEquals("z", label);
    }

    
//        @Test
//        public void testHasMedication() {
//            System.out.println("testHasMedication");
//            PhrsStoreClient phrsClient;
//            //PhrsRepositoryClient repos = client.getPhrsRepositoryClient();
//            phrsClient = PhrsStoreClient.getInstance();
//            CommonDao commonDao = phrsClient.getCommonDao();
//            assertNotNull("commonDao is null ",commonDao);
//            String owner="phr555";
//            boolean hasMed = commonDao.hasMedication(owner, "", "C0032952");
//            assertFalse("Expect no medication code for user phr555",hasMed);
//            
//            hasMed = commonDao.hasMedication("phrtest", "", "C0032952");
//            assertTrue("Expect  medication code for user phrtest",hasMed);
//            hasMed = commonDao.hasMedication("user123", "", "C0032952");
//            assertTrue("Expect  medication code for user user123",hasMed);
//    }
}
