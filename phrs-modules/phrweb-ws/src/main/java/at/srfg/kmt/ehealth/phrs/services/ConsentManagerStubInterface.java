package at.srfg.kmt.ehealth.phrs.services;

import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GenerateRequest;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GenerateRequestResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecision;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResources;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjects;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse;
import java.rmi.RemoteException;


public interface ConsentManagerStubInterface {

    /**
     *
     * @param generateRequest
     */
    GenerateRequestResponse generateRequest(GenerateRequest generateRequest) throws RemoteException;

    /**
     * Auto generated method signature
     * @param getDecision
     */
    GetDecisionResponse getDecision(GetDecision getDecision) throws RemoteException;

    /**
     * @param getResources
     */
    GetResourcesResponse getResources(GetResources getResources) throws RemoteException;

    /**
     * @param getSubjects
     */
    GetSubjectsResponse getSubjects(GetSubjects getSubjects) throws RemoteException;
    
}
