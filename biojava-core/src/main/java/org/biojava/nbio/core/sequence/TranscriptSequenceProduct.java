package org.biojava.nbio.core.sequence;


public class TranscriptSequenceProduct {
	private StartCodonSequence startCodonSequence = null;
	private StopCodonSequence stopCodonSequence = null;

	public StartCodonSequence getStartCodonSequence() {
		return startCodonSequence;
	}

	public StopCodonSequence getStopCodonSequence() {
		return stopCodonSequence;
	}

	/**
	* Sets the start codon sequence at given begin /  end location. Note that calling this method multiple times will replace any existing value.
	* @param accession
	* @param begin
	* @param end
	*/
	public void addStartCodonSequence(AccessionID accession, int begin, int end,
			TranscriptSequence transcriptSequence) {
		this.startCodonSequence = new StartCodonSequence(transcriptSequence, begin, end);
		startCodonSequence.setAccession(accession);
	}

	/**
	* Sets the stop codon sequence at given begin /  end location. Note that calling this method multiple times will replace any existing value.
	* @param accession
	* @param begin
	* @param end
	*/
	public void addStopCodonSequence(AccessionID accession, int begin, int end, TranscriptSequence transcriptSequence) {
		this.stopCodonSequence = new StopCodonSequence(transcriptSequence, begin, end);
		stopCodonSequence.setAccession(accession);
	}
}