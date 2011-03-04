/*
 * Project :iCardea
 * File : MessageStack.java
 * Date : Jan 21, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.dataexchange.api;

/**
 *
 * @author Mihai
 */
public interface MessageStack {
    void addMessage(Runnable runnable); 
}
