package org.biojava.nbio.core.sequence;


import java.util.LinkedHashMap;

public class GeneSequenceProduct {
	private final LinkedHashMap<String, TranscriptSequence> transcriptSequenceHashMap = new LinkedHashMap<String, TranscriptSequence>();

	public LinkedHashMap<String, TranscriptSequence> getTranscriptSequenceHashMap() {
		return transcriptSequenceHashMap;
	}

	/**
	* Get the transcript sequence by accession
	* @param accession
	* @return  the transcript
	*/
	public TranscriptSequence getTranscript(String accession) {
		return transcriptSequenceHashMap.get(accession);
	}

	/**
	* Remove the transcript sequence from the gene
	* @param accession
	* @return  transcriptsequence
	*/
	public TranscriptSequence removeTranscript(String accession) {
		return transcriptSequenceHashMap.remove(accession);
	}

	/**
	* Add a transcription sequence to a gene which describes a ProteinSequence
	* @param accession
	* @param begin
	* @param end
	* @return  transcript sequence
	* @throws Exception  If the accession id is already used
	*/
	public TranscriptSequence addTranscript(AccessionID accession, int begin, int end, GeneSequence geneSequence)
			throws Exception {
		if (transcriptSequenceHashMap.containsKey(accession.getID())) {
			throw new Exception("Duplicate accesion id " + accession.getID());
		}
		TranscriptSequence transcriptSequence = new TranscriptSequence(geneSequence, begin, end);
		transcriptSequence.setAccession(accession);
		transcriptSequenceHashMap.put(accession.getID(), transcriptSequence);
		return transcriptSequence;
	}
}