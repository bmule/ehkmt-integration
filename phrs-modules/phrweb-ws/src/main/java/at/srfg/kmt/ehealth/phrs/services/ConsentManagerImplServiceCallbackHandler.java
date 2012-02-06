
/**
 * ConsentManagerImplServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.3
 * Built on : Nov 12, 2010 (02:24:07 CET)
 */

package at.srfg.kmt.ehealth.phrs.services;

    /**
     *  ConsentManagerImplServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ConsentManagerImplServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ConsentManagerImplServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ConsentManagerImplServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getResources method
            * override this method for handling normal response from getResources operation
            */
           public void receiveResultgetResources(
                    at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getResources operation
           */
            public void receiveErrorgetResources(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getDecision method
            * override this method for handling normal response from getDecision operation
            */
           public void receiveResultgetDecision(
                    at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDecision operation
           */
            public void receiveErrorgetDecision(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for generateRequest method
            * override this method for handling normal response from generateRequest operation
            */
           public void receiveResultgenerateRequest(
                    at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GenerateRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from generateRequest operation
           */
            public void receiveErrorgenerateRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSubjects method
            * override this method for handling normal response from getSubjects operation
            */
           public void receiveResultgetSubjects(
                    at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSubjects operation
           */
            public void receiveErrorgetSubjects(java.lang.Exception e) {
            }
                


    }
    