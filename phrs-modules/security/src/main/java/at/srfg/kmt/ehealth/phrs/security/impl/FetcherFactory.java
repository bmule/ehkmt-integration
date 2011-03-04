/*
 * Project :iCardea
 * File : FetcherFactory.java
 * Date : Feb 14, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.model.Fetcher;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import java.util.Set;

/**
 * Used to build different kinds of <code>Fetcher</code> instances.
 * Available fetches :
 * <ul>
 * <li> PhrGroupFetcher - all the lazy initialized relations for a PhrGroup
 * <li> PhrRoleFetcher - all the lazy initialized relations for a PhrRole
 * </ul>
 * 
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
public final class FetcherFactory {

    /**
     * The only one instance for <code>PhrGroupFetcher</code>.
     */
    private final static Fetcher<PhrGroup> GROUP_FETCHER = new PhrGroupFetcher();

    /**
     * The only one instance for <code>PhrRoleFetcher</code>.
     */
    private final static Fetcher<PhrRole> ROLE_FETCHER = new PhrRoleFetcher();

    /**
     * 
     */
    private FetcherFactory() {
        // UNIMPLEMENTED
    }

    /**
     * Return a <code>Fetcher</code> instance able to fetch all the lazy 
     * initialized relations for a <code>PhrGroup</code>. 
     * If this fetcher is used then the <code>PhrGroup</code> instance obtained
     * from the <code>GroupManagerBean</code> will has all the lazy initialized
     * relation loaded. </br>
     * This method returns always the same instance.
     * 
     * @return a <code>Fetcher</code> instance able to fetch all the lazy 
     * initialized relations.
     * @see FetcherFactory#GROUP_FETCHER
     */
    public static Fetcher<PhrGroup> getGroupFetcher() {
        return GROUP_FETCHER;
    }

    /**
     * Return a <code>Fetcher</code> instance able to fetch all the lazy 
     * initialized relations for a <code>PhrRole</code>. 
     * If this fetcher is used then the <code>PhrRole</code> instance obtained
     * from the <code>RoleManagerBean</code> will has all the lazy initialized
     * relation loaded. </br>
     * This method returns always the same instance.
     * 
     * @return a <code>Fetcher</code> instance able to fetch all the lazy 
     * initialized relations.
     * @see FetcherFactory#ROLE_FETCHER
     */
    public static Fetcher<PhrRole> getRoleFetcher() {
        return ROLE_FETCHER;
    }

    /**
     * Used to fetch all the PhrGroup related relations.
     * 
     * @see PhrGroup
     */
    private static final class PhrGroupFetcher implements Fetcher<PhrGroup> {

        /**
         * Don't let anybody to instantiate this class outside of the enclosing
         * class.
         */
        private PhrGroupFetcher() {
            // UNIMPLEMENTD
        }

        /**
         * Fetch all the required lazy initialized relation for a given group.
         * 
         * @param toFetch the group that lazy initialized are to be loaded,
         * if is null then the method has no effect.
         * 
         */
        @Override
        public void fetch(PhrGroup toFetch) {

            if (toFetch == null) {
                return;
            }

            final Set<PhrActor> members = toFetch.getMembers();

            if (members == null) {
                return;
            }

            for (PhrActor actor : members) {
                final Set<PhrRole> phrRoles = actor.getPhrRoles();
                if (phrRoles != null) {
                    // here I fetch all the roles.
                    phrRoles.size();
                }
            }
        }
    }

    /**
     * Used to fetch all the PhrRole related relations.
     * 
     * @see PhrRole
     */
    private static final class PhrRoleFetcher implements Fetcher<PhrRole> {

        /**
         * Don't let anybody to instantiate this class outside of the enclosing
         * class.
         */
        private PhrRoleFetcher() {
            // UNIMPLEMENTD
        }

        /**
         * Fetch all the required lazy initialized relation for a given role.
         * 
         * @param toFetch the group that lazy initialized are to be loaded,
         * if is null then the method has no effect.
         * 
         */
        @Override
        public void fetch(PhrRole toFetch) {

            if (toFetch == null) {
                return;
            }
            final Set<PhrActor> actors = toFetch.getPhrActors();

            if (actors == null) {
                return;
            }

            for (PhrActor actor : actors) {
                final Set<PhrRole> phrRoles = actor.getPhrRoles();
                if (phrRoles != null) {
                    // here I fetch all the roles.
                    phrRoles.size();
                }

            }

        }
    }
}
