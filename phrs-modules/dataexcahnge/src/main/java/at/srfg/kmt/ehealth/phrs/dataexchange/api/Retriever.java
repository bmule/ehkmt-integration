/*
 * Project :iCardea
 * File : Retriver.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */

package at.srfg.kmt.ehealth.phrs.dataexchange.api;

/**
 * Defines a way to obtain (retreive) objects from the underlaying layers.   
 * 
 * @param <QT> the query type used by this retriver.
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface Retriever<QT> {
    
    /**
     * Queries the underlaying layer using a given query. 
     * It is not define here how the method must act if the underlaying layers
     * does not contains any matches for the given query; this will be
     * specified in the implementation.
     * 
     * @param query the query.
     * @return all the objects that match the query.
     * @throws RetreiverException by any problem during the querying.
     */
    Object retrieve(QT query) throws RetrieverException;
}
