package org.biojava.nbio.core.sequence;


import java.util.ArrayList;
import org.biojava.nbio.core.sequence.transcription.TranscriptionEngine;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

public class TranscriptSequenceProduct2 {
	private final ArrayList<CDSSequence> cdsSequenceList = new ArrayList<CDSSequence>();

	public ArrayList<CDSSequence> getCdsSequenceList() {
		return cdsSequenceList;
	}

	public String codingSequence(int i, TranscriptSequence transcriptSequence) {
		CDSSequence cdsSequence = cdsSequenceList.get(i);
		String codingSequence = cdsSequence.getCodingSequence();
		if (transcriptSequence.getStrand() == Strand.NEGATIVE) {
			codingSequence = checkCodingSequence(i, cdsSequence, codingSequence);
		} else {
			codingSequence = checkCodingSequence(i, cdsSequence, codingSequence);
		}
		return codingSequence;
	}

	public String checkCodingSequence(int i, CDSSequence cdsSequence, String codingSequence) {
		if (cdsSequence.phase == 1) {
			codingSequence = codingSequence.substring(1, codingSequence.length());
		} else if (cdsSequence.phase == 2) {
			codingSequence = codingSequence.substring(2, codingSequence.length());
		}
		if (i < cdsSequenceList.size() - 1) {
			CDSSequence nextCDSSequence = cdsSequenceList.get(i + 1);
			if (nextCDSSequence.phase == 1) {
				String nextCodingSequence = nextCDSSequence.getCodingSequence();
				codingSequence = codingSequence + nextCodingSequence.substring(0, 1);
			} else if (nextCDSSequence.phase == 2) {
				String nextCodingSequence = nextCDSSequence.getCodingSequence();
				codingSequence = codingSequence + nextCodingSequence.substring(0, 2);
			}
		}
		return codingSequence;
	}

	public ProteinSequence proteinSequence(int i, TranscriptSequence transcriptSequence) {
		DNASequence dnaCodingSequence = dnaCodingSequence(i, transcriptSequence);
		CDSSequence cdsSequence = cdsSequenceList.get(i);
		RNASequence rnaCodingSequence = dnaCodingSequence.getRNASequence(TranscriptionEngine.getDefault());
		ProteinSequence proteinSequence = rnaCodingSequence.getProteinSequence(TranscriptionEngine.getDefault());
		proteinSequence.setAccession(new AccessionID(cdsSequence.getAccession().getID()));
		return proteinSequence;
	}

	public DNASequence dnaCodingSequence(int i, TranscriptSequence transcriptSequence) {
		String codingSequence = codingSequence(i, transcriptSequence);
		DNASequence dnaCodingSequence = null;
		try {
			dnaCodingSequence = new DNASequence(codingSequence.toUpperCase());
		} catch (CompoundNotFoundException e) {
			TranscriptSequence.logger.error("Could not create DNA coding sequence, {}. This is most likely a bug.",
					e.getMessage());
		}
		return dnaCodingSequence;
	}
}