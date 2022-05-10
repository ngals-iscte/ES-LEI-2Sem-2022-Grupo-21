package org.biojava.nbio.core.sequence.transcription;


import org.biojava.nbio.core.sequence.io.template.SequenceCreatorInterface;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.CompoundSet;
import org.biojava.nbio.core.sequence.transcription.Table.Codon;
import org.biojava.nbio.core.sequence.transcription.TranscriptionEngine.Builder;
import org.biojava.nbio.core.sequence.io.RNASequenceCreator;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;

public class BuilderProduct {
	private RNAToAminoAcidTranslator rnaAminoAcidTranslator;
	private DNAToRNATranslator dnaRnaTranslator;
	private SequenceCreatorInterface<AminoAcidCompound> proteinSequenceCreator;
	private SequenceCreatorInterface<NucleotideCompound> rnaSequenceCreator;
	private CompoundSet<NucleotideCompound> dnaCompounds;
	private CompoundSet<NucleotideCompound> rnaCompounds;
	private CompoundSet<AminoAcidCompound> aminoAcidCompounds;
	private boolean initMet = true;
	private boolean trimStop = true;
	private boolean translateNCodons = true;
	private boolean decorateRna = false;
	private boolean stopAtStopCodons = false;
	private boolean waitForStartCodon = false;

	public boolean getInitMet() {
		return initMet;
	}

	public boolean getTrimStop() {
		return trimStop;
	}

	public boolean getTranslateNCodons() {
		return translateNCodons;
	}

	public boolean getDecorateRna() {
		return decorateRna;
	}

	public boolean getStopAtStopCodons() {
		return stopAtStopCodons;
	}

	public boolean getWaitForStartCodon() {
		return waitForStartCodon;
	}

	public Builder rnaAminoAcidTranslator(RNAToAminoAcidTranslator translator, Builder builder) {
		this.rnaAminoAcidTranslator = translator;
		return builder;
	}

	public Builder initMet(boolean initMet, Builder builder) {
		this.initMet = initMet;
		return builder;
	}

	public Builder trimStop(boolean trimStop, Builder builder) {
		this.trimStop = trimStop;
		return builder;
	}

	public Builder translateNCodons(boolean translateNCodons, Builder builder) {
		this.translateNCodons = translateNCodons;
		return builder;
	}

	/**
	* If set, then the last codon translated in the resulting peptide sequence will be the stop codon
	*/
	public Builder stopAtStopCodons(boolean stopAtStopCodons, Builder builder) {
		this.stopAtStopCodons = stopAtStopCodons;
		return builder;
	}

	/**
	* If set, then translation will not start until a start codon is encountered
	*/
	public Builder waitForStartCodon(boolean waitForStartCodon, Builder builder) {
		this.waitForStartCodon = waitForStartCodon;
		return builder;
	}

	public Builder dnaRnaTranslator(DNAToRNATranslator translator, Builder builder) {
		this.dnaRnaTranslator = translator;
		return builder;
	}

	public DNAToRNATranslator getDnaRnaTranslator() {
		if (dnaRnaTranslator != null) {
			return dnaRnaTranslator;
		}
		return new DNAToRNATranslator(new RNASequenceCreator(getRnaCompounds()), getDnaCompounds(), getRnaCompounds(),
				decorateRna);
	}

	/**
	* Performs an optimisation where RNASequences are not translated into their own objects but are views onto the base DNA sequence.
	*/
	public Builder decorateRna(boolean decorateRna, Builder builder) {
		this.decorateRna = decorateRna;
		return builder;
	}

	/**
	* The method to finish any calls to the builder with which returns a transcription engine. The engine is designed to provide everything required for transcription to those classes which will do the transcription.
	*/
	public TranscriptionEngine build(Builder builder) {
		return new TranscriptionEngine(builder.getTable(), getRnaAminoAcidTranslator(builder), getDnaRnaTranslator(),
				getProteinCreator(), getRnaCreator(), getDnaCompounds(), getRnaCompounds(), getAminoAcidCompounds());
	}

	public RNAToAminoAcidTranslator getRnaAminoAcidTranslator(Builder builder) {
		if (rnaAminoAcidTranslator != null) {
			return rnaAminoAcidTranslator;
		}
		return new RNAToAminoAcidTranslator(getProteinCreator(), getRnaCompounds(), getCodons(builder),
				getAminoAcidCompounds(), builder.getTable(), trimStop, initMet, translateNCodons, stopAtStopCodons,
				waitForStartCodon);
	}

	public CompoundSet<Codon> getCodons(Builder builder) {
		return builder.getTable().getCodonCompoundSet(getRnaCompounds(), getAminoAcidCompounds());
	}

	public Builder proteinCreator(SequenceCreatorInterface<AminoAcidCompound> creator, Builder builder) {
		this.proteinSequenceCreator = creator;
		return builder;
	}

	public SequenceCreatorInterface<AminoAcidCompound> getProteinCreator() {
		if (proteinSequenceCreator != null) {
			return proteinSequenceCreator;
		}
		return new ProteinSequenceCreator(getAminoAcidCompounds());
	}

	public Builder aminoAcidsCompounds(CompoundSet<AminoAcidCompound> compounds, Builder builder) {
		this.aminoAcidCompounds = compounds;
		return builder;
	}

	public CompoundSet<AminoAcidCompound> getAminoAcidCompounds() {
		if (aminoAcidCompounds != null) {
			return aminoAcidCompounds;
		}
		return AminoAcidCompoundSet.getAminoAcidCompoundSet();
	}

	public Builder rnaCreator(SequenceCreatorInterface<NucleotideCompound> creator, Builder builder) {
		this.rnaSequenceCreator = creator;
		return builder;
	}

	public SequenceCreatorInterface<NucleotideCompound> getRnaCreator() {
		if (rnaSequenceCreator != null) {
			return rnaSequenceCreator;
		}
		return new RNASequenceCreator(getRnaCompounds());
	}

	public Builder rnaCompounds(CompoundSet<NucleotideCompound> compounds, Builder builder) {
		this.rnaCompounds = compounds;
		return builder;
	}

	public CompoundSet<NucleotideCompound> getRnaCompounds() {
		if (rnaCompounds != null) {
			return rnaCompounds;
		}
		return AmbiguityRNACompoundSet.getRNACompoundSet();
	}

	public Builder dnaCompounds(CompoundSet<NucleotideCompound> compounds, Builder builder) {
		this.dnaCompounds = compounds;
		return builder;
	}

	public CompoundSet<NucleotideCompound> getDnaCompounds() {
		if (dnaCompounds != null) {
			return dnaCompounds;
		}
		return AmbiguityDNACompoundSet.getDNACompoundSet();
	}
}