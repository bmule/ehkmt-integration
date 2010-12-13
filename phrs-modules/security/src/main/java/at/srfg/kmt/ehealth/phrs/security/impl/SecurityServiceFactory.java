package at.srfg.kmt.ehealth.phrs.security.impl;

import at.srfg.kmt.ehealth.phrs.security.api.SecurityService;

public final class SecurityServiceFactory {

    private static final SecurityServiceFactory THIS =
            new SecurityServiceFactory();

    private SecurityServiceFactory() {
        // UNIMPLEMENTED
    }

    public static SecurityServiceFactory getInstance() {
        return THIS;
    }

    public SecurityService buildSecurityService(SecurityServiceType type) {

        if (type == null) {
            throw new NullPointerException("The type can not be null.");
        }

        switch (type) {
            case JAAS:
                final JAASSecurityService result = new JAASSecurityService();
                return result;
            default:
                final String msg = String.format("Type %s not supported.",
                        type.toString());
                final IllegalArgumentException argumentException =
                        new IllegalArgumentException(msg);
                throw argumentException;

        }
    }


}
