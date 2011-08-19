/*
 * Project :iCardea
 * File : MultiIterator.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.util;


import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Allows iteration over more iterators. More precisely this class can 
 * iterate over a list of iterators like it iterate over a single iterator. <br/>
 * This class was not designed to be extended. <br/>
 * 
 * <b>Note :</b> This class can iterate only over <code>Iterable</code> for 
 * <code>Triple</code>.
 * 
 * @version 0.1
 * @since 0.1
 * @author The Mihai
 */
public final class MultiIterable implements Iterable<Triple> {

    /**
     * Holds the list of iterators, this <code>Iterable</code> will iterate 
     * over all iterators from this <code>List<code/>.
     */
    private final List<Iterable<Triple>> iterables;

    /**
     * Builds a <code>MultiIterable</code> instance.
     */
    public MultiIterable() {
        iterables = new LinkedList<Iterable<Triple>>();
    }

    /**
     * Registers a new <code>Iterable<code/> 
     * 
     * @param iterable the <code>Iterable<code/>  instace to be register,
     * it can not be null.
     * @throws NullPointerException if the <code>iterable<code/> argument is 
     * null.
     */
    public void addIterable(Iterable<Triple> iterable) {
       
        if (iterable == null) {
            throw new NullPointerException("The iterable argument can not be null.");
        }
        
        iterables.add(iterable);
    }

    @Override
    public Iterator<Triple> iterator() {
        final InternIterator result = new InternIterator(iterables);
        return result;
    }

    /**
     * Composite iterator used to iterate over the iterators of iterators.
     */
    private final class InternIterator implements Iterator<Triple> {

        /**
         * Holds all the iterators.
         */
        private final Iterator<Iterable<Triple>> mainIterator;

        /**
         * The current iterator. This is used for the current iteration.
         */
        private Iterator<Triple> currentIterator;

        /**
         * Don't let anyone to instantiate this class out side of the enclosing
         * class.
         * 
         * @param iterables the all the iterators to iterate over, it can not 
         * be null.
         * @throws NullPointerException if the <code>iterable<code/> argument is 
         * null.
         */
        private InternIterator(List<Iterable<Triple>> iterables) {
            
            if (iterables == null) {
                throw  new NullPointerException("The iterable can not be null");
            }
            
            mainIterator = iterables.iterator();
            final Iterable<Triple> nextIterable = mainIterator.next();
            currentIterator = nextIterable.iterator();
        }

        @Override
        public boolean hasNext() {
            if (mainIterator == null) {
                return false;
            }

            final boolean currentHasNext = currentIterator.hasNext();
            if (currentHasNext) {
                return true;
            }


            final boolean moreIterators = mainIterator.hasNext();
            if (!moreIterators) {
                // the current interator has no more elements and 
                // the main iterator has no more intertors. 
                // This is the end.
                return false;
            }

            final Iterable<Triple> nextIterable = mainIterator.next();
            currentIterator = nextIterable.iterator();

            return currentIterator.hasNext();
        }

        @Override
        public Triple next() {
            if (mainIterator == null) {
                return null;
            }

            return currentIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }
}
