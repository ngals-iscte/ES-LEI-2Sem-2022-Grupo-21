package org.biojava.nbio.core.util;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class PrettyXMLWriterProduct {
	public void printChars(String data, PrintWriter thisWriter) throws IOException {
		if (data == null) {
			printChars("null", thisWriter);
			return;
		}
		for (int pos = 0; pos < data.length(); ++pos) {
			char c = data.charAt(pos);
			if (c == '<' || c == '>' || c == '&') {
				numericalEntity(c, thisWriter);
			} else {
				thisWriter.write(c);
			}
		}
	}

	public void numericalEntity(char c, PrintWriter thisWriter) throws IOException {
		thisWriter.print("&#");
		thisWriter.print((int) c);
		thisWriter.print(';');
	}

	public void printAttributeValue(String data, PrintWriter thisWriter) throws IOException {
		if (data == null) {
			printAttributeValue("null", thisWriter);
			return;
		}
		for (int pos = 0; pos < data.length(); ++pos) {
			char c = data.charAt(pos);
			if (c == '<' || c == '>' || c == '&' || c == '"') {
				numericalEntity(c, thisWriter);
			} else {
				thisWriter.write(c);
			}
		}
	}

	void handleDeclaredNamespaces(PrettyXMLWriter prettyXMLWriter)
		throws IOException
	{
		if (prettyXMLWriter.namespacesDeclared.size() == 0) {
			for (Iterator<String> nsi = prettyXMLWriter.namespacesDeclared.iterator(); nsi.hasNext(); ) {
				String nsURI = nsi.next();
				if (!prettyXMLWriter.namespacePrefixes.containsKey(nsURI)) {
					String prefix = prettyXMLWriter.prettyXMLWriterProduct2.allocPrefix(nsURI, prettyXMLWriter.namespacePrefixes);
					prettyXMLWriter.attribute("xmlns:" + prefix, nsURI);
				}
			}
			prettyXMLWriter.namespacesDeclared.clear();
		}
	}
}