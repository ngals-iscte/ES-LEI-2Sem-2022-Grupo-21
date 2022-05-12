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
 * Created on July 26, 2010
 * Author: Mark Chapman
 */

package org.biojava.nbio.core.alignment.matrices;

import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Static utility to access substitution matrices that come bundled with BioJava.  All matrices were downloaded from
 * ftp://ftp.ncbi.nih.gov/blast/matrices/
 *
 * @author Mark Chapman
 * @author Paolo Pavan
 */
public class SubstitutionMatrixHelper implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 148491724604653225L;

	private static Map<String, SubstitutionMatrix<AminoAcidCompound>> aminoAcidMatrices =
			new HashMap<String, SubstitutionMatrix<AminoAcidCompound>>();
	private static Map<String, SubstitutionMatrix<NucleotideCompound>> nucleotideMatrices =
			new HashMap<String, SubstitutionMatrix<NucleotideCompound>>();


	/**
	 * Returns Blosum 65 matrix by Henikoff & Henikoff
	 * @return Blosum 65 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum65() {
		return getAminoAcidMatrix("blosum65");
	}

	/**
	 * Returns Blosum 70 matrix by Henikoff & Henikoff
	 * @return Blosum 70 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum70() {
		return getAminoAcidMatrix("blosum70");
	}

	/**
	 * Returns Blosum 75 matrix by Henikoff & Henikoff
	 * @return Blosum 75 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum75() {
		return getAminoAcidMatrix("blosum75");
	}

	/**
	 * Returns Blosum 80 matrix by Henikoff & Henikoff
	 * @return Blosum 80 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum80() {
		return getAminoAcidMatrix("blosum80");
	}

	/**
	 * Returns Blosum 85 matrix by Henikoff & Henikoff
	 * @return Blosum 85 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum85() {
		return getAminoAcidMatrix("blosum85");
	}

	/**
	 * Returns Blosum 90 matrix by Henikoff & Henikoff
	 * @return Blosum 90 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getBlosum90() {
		return getAminoAcidMatrix("blosum90");
	}

	/**
	 * Returns PAM 250 matrix by Gonnet, Cohen & Benner
	 * @return Gonnet 250 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getGonnet250() {
		return getAminoAcidMatrix("gonnet250");
	}

	/**
	 * Returns Nuc 4.2 matrix by Lowe
	 * Only the first nucleotide sequence to align can contain ambiguous nucleotides
	 * @return Nuc 4.2 matrix
	 */
	public static SubstitutionMatrix<NucleotideCompound> getNuc4_2() {
		return getNucleotideMatrix("nuc-4_2");
	}

	/**
	 * Returns Nuc 4.4 matrix by Lowe
	 * Both of the nucleotide sequences to align can contain ambiguous nucleotides
	 * @return Nuc 4.4 matrix
	 */
	public static SubstitutionMatrix<NucleotideCompound> getNuc4_4() {
		return getNucleotideMatrix("nuc-4_4");
	}

	/**
	 * Returns PAM 250 matrix by Dayhoff
	 * @return PAM 250 matrix
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getPAM250() {
		return getAminoAcidMatrix("pam250");
	}

	// helper methods

	/**
	 * Returns a substitution matrix for {@link AminoAcidCompound amino acids} given by the name {@code name}.
	 * Searches first in the default AAINDEX file (see @link {@link #getMatrixFromAAINDEX(String)}), then in the classpath
	 * in src/main/resources/matrices.
	 * Example names:
	 * <ul>
	 * <li>blosum62</li>
	 * <li>JOND920103</li>
	 * <li>pam250</li>
	 * <li>gonnet250</li>
	 * </ul>
	 * @param name Either a common name or an AAINDEX name
	 * @return a {@code} SubstitutionMatrix {@code} or {@code}null{@code} if no matrix is found
	 */
	public static SubstitutionMatrix<AminoAcidCompound> getAminoAcidSubstitutionMatrix(String name) {
		SubstitutionMatrix<AminoAcidCompound> matrix = getMatrixFromAAINDEX(name);
		if (matrix != null) return matrix;
		return getAminoAcidMatrix(name);
	}

	// reads in an amino acid substitution matrix, if necessary
	private static SubstitutionMatrix<AminoAcidCompound> getAminoAcidMatrix(String file) {
		if (!aminoAcidMatrices.containsKey(file)) {
			InputStreamReader reader = getReader(file);
			if (reader == null) {
				return null;
			}
			aminoAcidMatrices.put(file, new SimpleSubstitutionMatrix<AminoAcidCompound>(
					AminoAcidCompoundSet.getAminoAcidCompoundSet(), reader , file));
		}
		return aminoAcidMatrices.get(file);
	}

	// reads in a nucleotide substitution matrix, if necessary
	private static SubstitutionMatrix<NucleotideCompound> getNucleotideMatrix(String file) {
		if (!nucleotideMatrices.containsKey(file)) {
			nucleotideMatrices.put(file, new SimpleSubstitutionMatrix<NucleotideCompound>(
					AmbiguityDNACompoundSet.getDNACompoundSet(), getReader(file), file));
		}
		return nucleotideMatrices.get(file);
	}

	// reads in a substitution matrix from a resource file
	private static InputStreamReader getReader(String file) {
		String resourcePathPrefix = "matrices/";
		InputStream is = SubstitutionMatrixHelper.class.getResourceAsStream(String.format("/%s.txt",
		resourcePathPrefix+file));
		if (is == null) {
			return null;
		}
		return new InputStreamReader(is);
	}

}
