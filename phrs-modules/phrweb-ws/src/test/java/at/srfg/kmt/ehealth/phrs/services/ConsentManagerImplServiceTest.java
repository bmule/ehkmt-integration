package at.srfg.kmt.ehealth.phrs.services;

    /*
     *  ConsentManagerImplServiceTest Junit test case
    */

import org.junit.Ignore;
//import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;
//import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.*;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.*;

public class ConsentManagerImplServiceTest extends junit.framework.TestCase{

     


//        public  void testgetResources() throws java.lang.Exception{
//
//        ConsentManagerImplServiceStub stub =
//                    new ConsentManagerImplServiceStub();//the default implementation should point to the right endpoint
//
//           ConsentManagerImplServiceStub.GetResources getResources16=
//                                                        (ConsentManagerImplServiceStub.GetResources)getTestObject(ConsentManagerImplServiceStub.GetResources.class);
//
//            ConsentManagerImplServiceStub.GetResourcesResponse response= stub.getResources(
//                                getResources16);
//
//            assertNotNull(response);
//            ConsentManagerImplServiceStub.ArrayOf_xsd_anyType result = response.getGetResourcesReturn();
//            assertNotNull("response.getGetResourcesReturn null "+result);
//            System.out.println(result.toString());
//
//
//            System.out.println("Resources");
//            for (Object o : result.getItem()) {
//                System.out.println("Subject Code: " + o);
//            }
//
//
//        }
//    public  void testgetSubjects() throws java.lang.Exception{
//
//        ConsentManagerImplServiceStub stub =
//                new ConsentManagerImplServiceStub();//the default implementation should point to the right endpoint
//
//        ConsentManagerImplServiceStub.GetSubjects getSubjects22=
//                (ConsentManagerImplServiceStub.GetSubjects)getTestObject(ConsentManagerImplServiceStub.GetSubjects.class);
//
//        ConsentManagerImplServiceStub.GetSubjectsResponse response=stub.getSubjects(getSubjects22);
//
//        assertNotNull(response);
//        ConsentManagerImplServiceStub.ArrayOf_xsd_anyType result = response.getGetSubjectsReturn();
//        assertNotNull("response.getGetSubjectsReturn null "+result);
//        System.out.println(result.toString());
//        //grr.getGetResourcesReturn()
//
//        System.out.println("Resources");
//        for (Object o : result.getItem()) {
//            System.out.println("Subject Code: " + o);
//        }
//
//
//    }

//        public  void testStartgetResources() throws java.lang.Exception{
//            ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub();
//             ConsentManagerImplServiceStub.GetResources getResources16=
//                                                        (ConsentManagerImplServiceStub.GetResources)getTestObject(ConsentManagerImplServiceStub.GetResources.class);
//                    // TODO : Fill in the getResources16 here
//
//
//                stub.startgetResources(
//                         getResources16,
//                    new tempCallbackN1000C()
//                );
//
//
//
//        }
//    @Ignore
//        private class tempCallbackN1000C  extends ConsentManagerImplServiceCallbackHandler{
//            public tempCallbackN1000C(){ super(null);}
//
//            public void receiveResultgetResources(
//                         ConsentManagerImplServiceStub.GetResourcesResponse result
//                            ) {
//
//            }
//
//            public void receiveErrorgetResources(java.lang.Exception e) {
//                fail();
//            }
//
//        }

//        /**
//         * Auto generated test method
//         */
//        @Ignore
//        public  void testgetDecision() throws java.lang.Exception{
//
//        ConsentManagerImplServiceStub stub =
//                    new ConsentManagerImplServiceStub();//the default implementation should point to the right endpoint
//
//           ConsentManagerImplServiceStub.GetDecision getDecision18=
//                                                        (ConsentManagerImplServiceStub.GetDecision)getTestObject(ConsentManagerImplServiceStub.GetDecision.class);
//                    // TODO : Fill in the getDecision18 here
//
//                        assertNotNull(stub.getDecision(
//                        getDecision18));
//
//
//
//
//        }
//
//         /**
//         * Auto generated test method
//         */
//         @Ignore
//        public  void testStartgetDecision() throws java.lang.Exception{
//            ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub();
//             ConsentManagerImplServiceStub.GetDecision getDecision18=
//                                                        (ConsentManagerImplServiceStub.GetDecision)getTestObject(ConsentManagerImplServiceStub.GetDecision.class);
//                    // TODO : Fill in the getDecision18 here
//
//
//                stub.startgetDecision(
//                         getDecision18,
//                    new tempCallbackN10033()
//                );
//
//
//
//        }
//    @Ignore
//        private class tempCallbackN10033  extends ConsentManagerImplServiceCallbackHandler{
//            public tempCallbackN10033(){ super(null);}
//
//            public void receiveResultgetDecision(
//                         ConsentManagerImplServiceStub.GetDecisionResponse result
//                            ) {
//
//            }
//
//            public void receiveErrorgetDecision(java.lang.Exception e) {
//                fail();
//            }
//
//        }
//
//        /**
//         * Auto generated test method
//         */
//        @Ignore
//        public  void testgenerateRequest() throws java.lang.Exception{
//
//        ConsentManagerImplServiceStub stub =
//                    new ConsentManagerImplServiceStub();//the default implementation should point to the right endpoint
//
//           ConsentManagerImplServiceStub.GenerateRequest generateRequest20=
//                                                        (ConsentManagerImplServiceStub.GenerateRequest)getTestObject(ConsentManagerImplServiceStub.GenerateRequest.class);
//                    // TODO : Fill in the generateRequest20 here
//
//                        assertNotNull(stub.generateRequest(
//                        generateRequest20));
//
//
//
//
//        }
//
//         /**
//         * Auto generated test method
//         */
//         @Ignore
//        public  void testStartgenerateRequest() throws java.lang.Exception{
//            ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub();
//             ConsentManagerImplServiceStub.GenerateRequest generateRequest20=
//                                                        (ConsentManagerImplServiceStub.GenerateRequest)getTestObject(ConsentManagerImplServiceStub.GenerateRequest.class);
//                    // TODO : Fill in the generateRequest20 here
//
//
//                stub.startgenerateRequest(
//                         generateRequest20,
//                    new tempCallbackN1005A()
//                );
//
//
//
//        }

//        private class tempCallbackN1005A  extends ConsentManagerImplServiceCallbackHandler{
//            public tempCallbackN1005A(){ super(null);}
//
//            public void receiveResultgenerateRequest(
//                         ConsentManagerImplServiceStub.GenerateRequestResponse result
//                            ) {
//
//            }
//
//            public void receiveErrorgenerateRequest(java.lang.Exception e) {
//                fail();
//            }
//
//        }
//
//
//
//         /**
//         * Auto generated test method
//         */
//        public  void testStartgetSubjects() throws java.lang.Exception{
//            ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub();
//             ConsentManagerImplServiceStub.GetSubjects getSubjects22=
//                                                        (ConsentManagerImplServiceStub.GetSubjects)getTestObject(ConsentManagerImplServiceStub.GetSubjects.class);
//                    // TODO : Fill in the getSubjects22 here
//
//
//                stub.startgetSubjects(
//                         getSubjects22,
//                    new tempCallbackN10081()
//                );
//
//
//
//        }

//        private class tempCallbackN10081  extends ConsentManagerImplServiceCallbackHandler{
//            public tempCallbackN10081(){ super(null);}
//
//            public void receiveResultgetSubjects(
//                         ConsentManagerImplServiceStub.GetSubjectsResponse result
//                            ) {
//
//            }
//
//            public void receiveErrorgetSubjects(java.lang.Exception e) {
//                fail();
//            }
//
//        }
      
        //Create an ADBBean and provide it as the test object
        public org.apache.axis2.databinding.ADBBean getTestObject(java.lang.Class type) throws java.lang.Exception{
           return (org.apache.axis2.databinding.ADBBean) type.newInstance();
        }

        
        

    }
    