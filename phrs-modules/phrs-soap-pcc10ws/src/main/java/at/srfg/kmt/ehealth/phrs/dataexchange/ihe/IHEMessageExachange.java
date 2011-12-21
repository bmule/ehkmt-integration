/*
 * Project :iCardea
 * File : IHEMessageExachange.java
 * Encoding : UTF-8
 * Date : Aug 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ihe;

/**
 *
 * @author Mihai
 */
public final class IHEMessageExachange extends Exception {

    IHEMessageExachange() {
    }

    IHEMessageExachange(String string) {
        super(string);
    }

    IHEMessageExachange(Throwable thrwbl) {
        super(thrwbl);
    }

    IHEMessageExachange(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
