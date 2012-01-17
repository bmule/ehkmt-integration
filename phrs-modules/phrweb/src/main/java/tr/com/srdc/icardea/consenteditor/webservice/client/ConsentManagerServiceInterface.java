/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.com.srdc.icardea.consenteditor.webservice.client;

import java.rmi.RemoteException;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GenerateRequest;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GenerateRequestResponse;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetDecision;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetDecisionResponse;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetResources;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetResourcesResponse;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetSubjects;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub.GetSubjectsResponse;


public interface ConsentManagerServiceInterface {

    /**
     * Auto generated method signature
     *
     * @see tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplService#generateRequest
     * @param generateRequest
     */
    GenerateRequestResponse generateRequest(GenerateRequest generateRequest) throws RemoteException;

    /**
     * Auto generated method signature
     *
     * @see tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplService#getDecision
     * @param getDecision
     */
    GetDecisionResponse getDecision(GetDecision getDecision) throws RemoteException;

    /**
     * Auto generated method signature
     *
     * @see tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplService#getResources
     * @param getResources
     */
    GetResourcesResponse getResources(GetResources getResources) throws RemoteException;

    /**
     * Auto generated method signature
     *
     * @see tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplService#getSubjects
     * @param getSubjects
     */
    GetSubjectsResponse getSubjects(GetSubjects getSubjects) throws RemoteException;
    
}
