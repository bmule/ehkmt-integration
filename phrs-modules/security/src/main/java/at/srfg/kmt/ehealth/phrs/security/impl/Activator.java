package at.srfg.kmt.ehealth.phrs.security.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    /*
      * (non-Javadoc)
      * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
      */
    public void start(BundleContext context) throws Exception {
        System.out.println("Security Bundle starts");
    }

    /*
      * (non-Javadoc)
      * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
      */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Security Bundle starts");
    }

}
