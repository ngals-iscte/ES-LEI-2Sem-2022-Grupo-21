/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on 01-21-2010
 */
package org.biojava.nbio.core.sequence.transcription;

import org.biojava.nbio.core.sequence.compound.*;
import org.biojava.nbio.core.sequence.io.IUPACParser;
import org.biojava.nbio.core.sequence.io.IUPACParser.IUPACTable;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.biojava.nbio.core.sequence.io.RNASequenceCreator;
import org.biojava.nbio.core.sequence.io.template.SequenceCreatorInterface;
import org.biojava.nbio.core.sequence.template.CompoundSet;
import org.biojava.nbio.core.sequence.template.Sequence;
import org.biojava.nbio.core.sequence.transcription.Table.Codon;

import java.util.EnumMap;
import java.util.Map;

/**
 * Used as a way of encapsulating the data structures required to parse DNA to a
 * Protein sequence.
 *
 * In order to build one look at @ TranscriptionEngine.Builder} which provides
 * intelligent defaults & allows you to build an engine which is nearly the same
 * as the default one but with a few changes. All of the engine is customisable.
 *
 * By default the code will attempt to:
 *
 * <ul>
 * <li>Trim Stops</li>
 * <li>Convert initiating codons to M</li>
 * <li>Allow for the fuzzy translation of Codons i.e. if it contains an N that
 * produces a {@link Sequence}&lt;{@link{AminoAcidCompound}&gt; with an X at
 * that position
 * </ul>
 *
 * @author ayates
 */
public class TranscriptionEngine {

	private static final class IOD {

		public static final TranscriptionEngine INSTANCE = new TranscriptionEngine.Builder()
				.build();
	}

	/**
	 * Default instance to use when Transcribing from DNA -&gt; RNA -&gt;
	 * Protein. If you require anything that is not a default setting then look
	 * at @ TranscriptionEngine.Builder} for customisation options.
	 */
	public static TranscriptionEngine getDefault() {
		return IOD.INSTANCE;
	}

	private final Table table;
	private final RNAToAminoAcidTranslator rnaAminoAcidTranslator;
	private final DNAToRNATranslator dnaRnaTranslator;
	private final SequenceCreatorInterface<AminoAcidCompound> proteinSequenceCreator;
	private final SequenceCreatorInterface<NucleotideCompound> rnaSequenceCreator;
	private final CompoundSet<NucleotideCompound> dnaCompounds;
	private final CompoundSet<NucleotideCompound> rnaCompounds;
	private final CompoundSet<AminoAcidCompound> aminoAcidCompounds;

	TranscriptionEngine(Table table,
			RNAToAminoAcidTranslator rnaAminoAcidTranslator,
			DNAToRNATranslator dnaRnaTranslator,
			SequenceCreatorInterface<AminoAcidCompound> proteinSequenceCreator,
			SequenceCreatorInterface<NucleotideCompound> rnaSequenceCreator,
			CompoundSet<NucleotideCompound> dnaCompounds,
			CompoundSet<NucleotideCompound> rnaCompounds,
			CompoundSet<AminoAcidCompound> aminoAcidCompounds) {
		this.table = table;
		this.rnaAminoAcidTranslator = rnaAminoAcidTranslator;
		this.dnaRnaTranslator = dnaRnaTranslator;
		this.proteinSequenceCreator = proteinSequenceCreator;
		this.rnaSequenceCreator = rnaSequenceCreator;
		this.dnaCompounds = dnaCompounds;
		this.rnaCompounds = rnaCompounds;
		this.aminoAcidCompounds = aminoAcidCompounds;
	}

	/**
	 * Quick method to let you go from a CDS to a Peptide quickly. It assumes
	 * you are translating only in the first frame
	 *
	 * @param dna
	 *            The CDS to translate
	 * @return The Protein Sequence
	 */
	public Sequence<AminoAcidCompound> translate(
			Sequence<NucleotideCompound> dna) {
		Map<Frame, Sequence<AminoAcidCompound>> trans = multipleFrameTranslation(
				dna, Frame.ONE);
		return trans.get(Frame.ONE);
	}

	/**
	 * A way of translating DNA in a number of frames
	 *
	 * @param dna
	 *            The CDS to translate
	 * @param frames
	 *            The Frames to translate in
	 * @return All generated protein sequences in the given frames. Can have
	 *         null entries
	 */
	public Map<Frame, Sequence<AminoAcidCompound>> multipleFrameTranslation(
			Sequence<NucleotideCompound> dna, Frame... frames) {
		Map<Frame, Sequence<AminoAcidCompound>> results = new EnumMap<Frame, Sequence<AminoAcidCompound>>(
				Frame.class);
		for (Frame frame : frames) {
			Sequence<NucleotideCompound> rna = getDnaRnaTranslator()
					.createSequence(dna, frame);
			Sequence<AminoAcidCompound> peptide = getRnaAminoAcidTranslator()
					.createSequence(rna);
			results.put(frame, peptide);
		}
		return results;
	}

	public Table getTable() {
		return table;
	}

	public RNAToAminoAcidTranslator getRnaAminoAcidTranslator() {
		return rnaAminoAcidTranslator;
	}

	public DNAToRNATranslator getDnaRnaTranslator() {
		return dnaRnaTranslator;
	}

	public SequenceCreatorInterface<AminoAcidCompound> getProteinSequenceCreator() {
		return proteinSequenceCreator;
	}

	public SequenceCreatorInterface<NucleotideCompound> getRnaSequenceCreator() {
		return rnaSequenceCreator;
	}

	public CompoundSet<NucleotideCompound> getDnaCompounds() {
		return dnaCompounds;
	}

	public CompoundSet<NucleotideCompound> getRnaCompounds() {
		return rnaCompounds;
	}

	public CompoundSet<AminoAcidCompound> getAminoAcidCompounds() {
		return aminoAcidCompounds;
	}

	/**
	 * This class is the way to create a {@link TranslationEngine}.
	 */
	public static class Builder {

		private BuilderProduct builderProduct = new BuilderProduct();
		private Table table;
		/**
		 * The method to finish any calls to the builder with which returns a
		 * transcription engine. The engine is designed to provide everything
		 * required for transcription to those classes which will do the
		 * transcription.
		 */
		public TranscriptionEngine build() {
			return builderProduct.build(this);
		}

		// ---- START OF BUILDER METHODS
		/**
		 * Uses the static instance of {@link IUPACParser} to find instances of
		 * {@link IUPACTable}s by ID.
		 */
		public Builder table(Integer id) {
			table = IUPACParser.getInstance().getTable(id);
			return this;
		}

		/**
		 * Uses the static instance of {@link IUPACParser} to find instances of
		 * {@link IUPACTable}s by its String name
		 */
		public Builder table(String name) {
			table = IUPACParser.getInstance().getTable(name);
			return this;
		}

		public Builder table(Table table) {
			this.table = table;
			return this;
		}

		public Builder dnaCompounds(CompoundSet<NucleotideCompound> compounds) {
			return builderProduct.dnaCompounds(compounds, this);
		}

		public Builder rnaCompounds(CompoundSet<NucleotideCompound> compounds) {
			return builderProduct.rnaCompounds(compounds, this);
		}

		public Builder aminoAcidsCompounds(
				CompoundSet<AminoAcidCompound> compounds) {
			return builderProduct.aminoAcidsCompounds(compounds, this);
		}

		public Builder dnaRnaTranslator(DNAToRNATranslator translator) {
			return builderProduct.dnaRnaTranslator(translator, this);
		}

		public Builder rnaAminoAcidTranslator(
				RNAToAminoAcidTranslator translator) {
			return builderProduct.rnaAminoAcidTranslator(translator, this);
		}

		public Builder proteinCreator(
				SequenceCreatorInterface<AminoAcidCompound> creator) {
			return builderProduct.proteinCreator(creator, this);
		}

		public Builder rnaCreator(
				SequenceCreatorInterface<NucleotideCompound> creator) {
			return builderProduct.rnaCreator(creator, this);
		}

		public Builder initMet(boolean initMet) {
			return builderProduct.initMet(initMet, this);
		}

		public Builder trimStop(boolean trimStop) {
			return builderProduct.trimStop(trimStop, this);
		}

		public Builder translateNCodons(boolean translateNCodons) {
			return builderProduct.translateNCodons(translateNCodons, this);
		}

		/**
		 * If set, then the last codon translated in the resulting peptide
		 * sequence will be the stop codon
		 */
		public Builder stopAtStopCodons(boolean stopAtStopCodons) {
			return builderProduct.stopAtStopCodons(stopAtStopCodons, this);
		}

		/**
		 * If set, then translation will not start until a start codon is
		 * encountered
		 */
		public Builder waitForStartCodon(boolean waitForStartCodon) {
			return builderProduct.waitForStartCodon(waitForStartCodon, this);
		}

		/**
		 * Performs an optimisation where RNASequences are not translated into
		 * their own objects but are views onto the base DNA sequence.
		 */
		public Builder decorateRna(boolean decorateRna) {
			return builderProduct.decorateRna(decorateRna, this);
		}

		public Table getTable() {
			if (table != null) {
				return table;
			}
			table(1); // Will set table to default IUPAC codee
			return table;
		}

		private boolean isInitMet() {
			return builderProduct.getInitMet();
		}

		private boolean isTrimStop() {
			return builderProduct.getTrimStop();
		}

		private boolean isTranslateNCodons() {
			return builderProduct.getTranslateNCodons();
		}

		private boolean isDecorateRna() {
			return builderProduct.getDecorateRna();
		}

		private boolean isStopAtStopCodons() {
			return builderProduct.getStopAtStopCodons();
		}

		private boolean isWaitForStartCodon() {
			return builderProduct.getWaitForStartCodon();
		}
	}
}
