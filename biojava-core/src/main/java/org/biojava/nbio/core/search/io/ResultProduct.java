package org.biojava.nbio.core.search.io;


import java.util.HashMap;
import java.util.Set;

public class ResultProduct {
	private HashMap<String, String> programSpecificParameters;

	public void setProgramSpecificParameters(HashMap<String, String> programSpecificParameters) {
		this.programSpecificParameters = programSpecificParameters;
	}

	public Set<String> getProgramSpecificParametersList() {
		return programSpecificParameters.keySet();
	}

	public String getProgramSpecificParameter(String key) {
		return programSpecificParameters.get(key);
	}
}