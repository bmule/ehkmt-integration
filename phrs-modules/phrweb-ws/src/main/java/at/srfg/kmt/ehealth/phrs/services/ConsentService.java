package at.srfg.kmt.ehealth.phrs.services;

import  at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;
import org.apache.axis2.AxisFault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timeout is set in maven wsdl plugin
 *  stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout)
 */
public class ConsentService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsentService.class);
    private ConsentManagerImplServiceStub stub;
    private int timeout=5000;
    public ConsentService() throws AxisFault{
        init();
    }

    public ConsentService(int timeout) throws AxisFault{
        init();
    }
     private void init() throws AxisFault{

         stub = new ConsentManagerImplServiceStub();

         stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout);

     }
    public  ConsentManagerImplServiceStub getStub(){
             return stub;
    }
    /*
  http://axis.apache.org/axis2/java/core/docs/http-transport.html#timeout_config
   long timeout = 2 * 60 * 1000; // 2 min

stub._getServiceClient().getOptions().setProperty(
                 HTTPConstants.SO_TIMEOUT, new Integer(timeOutInMilliSeconds));
stub._getServiceClient().getOptions().setProperty(
                 HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeOutInMilliSeconds));
     */

}
