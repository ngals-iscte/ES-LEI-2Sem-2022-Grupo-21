package org.biojava.nbio.core.sequence.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.biojava.nbio.core.sequence.views.WindowedSequence;

public class SimpleSequenceIterator {

	/**
	 * A basic sequence iterator which iterates over the given Sequence by
	 * biological index. This assumes your sequence supports random access
	 * and performs well when doing these operations.
	 *
	 * @author ayates
	 *
	 * @param <C> Type of compound to return
	 */
	public static class SequenceIterator<C extends Compound>
			implements Iterator<C> {
	
		private final Sequence<C> sequence;
		private final int length;
		private int currentPosition = 0;
	
		public SequenceIterator(Sequence<C> sequence) {
			this.sequence = sequence;
			this.length = sequence.getLength();
		}
	
	
		@Override
		public boolean hasNext() {
			return (currentPosition < length);
		}
	
	
		@Override
		public C next() {
			if(!hasNext()) {
				throw new NoSuchElementException("Exhausted sequence of elements");
			}
			return sequence.getCompoundAt(++currentPosition);
		}
	
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove() on a SequenceIterator");
		}
	}

	/**
	 * Used to generate overlapping k-mers such i.e. ATGTA will give rise to
	 * ATG, TGT & GTA
	 *
	 * @param <C> Compound to use
	 * @param sequence Sequence to build from
	 * @param kmer Kmer size
	 * @return The list of overlapping K-mers
	 */
	public static <C extends Compound> List<SequenceView<C>> overlappingKmers(Sequence<C> sequence, int kmer) {
		List<SequenceView<C>> l = new ArrayList<SequenceView<C>>();
		List<Iterator<SequenceView<C>>> windows
				= new ArrayList<Iterator<SequenceView<C>>>();
	
		SequenceMixin.windowedSequenceIterator(sequence, kmer, windows);
	
		OUTER: while(true) {
			for(int i=0; i<kmer; i++) {
				Iterator<SequenceView<C>> iterator = windows.get(i);
				boolean breakLoop=true;
				if(iterator.hasNext()) {
					l.add(iterator.next());
					breakLoop = false;
				}
				if(breakLoop) {
					break OUTER;
				}
			}
		}
		return l;
	}

	/**
	 * Produces kmers of the specified size e.g. ATGTGA returns two views which
	 * have ATG TGA
	 *
	 * @param <C> Compound to use
	 * @param sequence Sequence to build from
	 * @param kmer Kmer size
	 * @return The list of non-overlapping K-mers
	 */
	public static <C extends Compound> List<SequenceView<C>> nonOverlappingKmers(Sequence<C> sequence, int kmer) {
		List<SequenceView<C>> l = new ArrayList<SequenceView<C>>();
		WindowedSequence<C> w = new WindowedSequence<C>(sequence, kmer);
		for(SequenceView<C> view: w) {
			l.add(view);
		}
		return l;
	}

}
