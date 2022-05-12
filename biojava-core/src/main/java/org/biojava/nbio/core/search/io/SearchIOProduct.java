package org.biojava.nbio.core.search.io;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class SearchIOProduct {
	private final ResultFactory factory;
	private final File file;
	private double evalueThreshold = Double.MAX_VALUE;

	public SearchIOProduct(ResultFactory guessFactory, File f) {
		factory = guessFactory;
		file = f;
	}

	public SearchIOProduct(File f, ResultFactory factory) {
		file = f;
		this.factory = factory;
	}


	public File getFile() {
		return file;
	}

	public double getEvalueThreshold() {
		return evalueThreshold;
	}

	public void setEvalueThreshold(double evalueThreshold) {
		this.evalueThreshold = evalueThreshold;
	}

	/**
	* This method is declared private because it is the default action of constructor when file exists
	* @throws java.io.IOException  for file access related issues
	* @throws java.text.ParseException  for file format related issues
	*/
	public void readResults(SearchIO searchIO) throws IOException, ParseException {
		factory.setFile(file);
		searchIO.setResults(factory.createObjects(evalueThreshold));
	}

	/**
	* used to write a search report using the guessed or specified factory
	* @throws java.io.IOException  for file access related issues
	* @throws java.text.ParseException  for file format related issues
	*/
	public void writeResults() throws IOException, ParseException {
		factory.setFile(file);
		factory.createObjects(evalueThreshold);
	}
}