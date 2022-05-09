package org.biojava.nbio.core.util;


import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PrettyXMLWriterProduct2 {
	private int namespaceSeed = 0;
	private LinkedList<List<String>> namespaceBindings = new LinkedList<List<String>>();

	public LinkedList<List<String>> getNamespaceBindings() {
		return namespaceBindings;
	}

	public String allocPrefix(String nsURI, Map<String, String> thisNamespacePrefixes) {
		String prefix = "ns" + (++namespaceSeed);
		thisNamespacePrefixes.put(nsURI, prefix);
		List<String> bindings = namespaceBindings.getLast();
		if (bindings == null) {
			bindings = new ArrayList<String>();
			namespaceBindings.removeLast();
			namespaceBindings.add(bindings);
		}
		bindings.add(nsURI);
		return prefix;
	}

	public void openTag(PrettyXMLWriter prettyXMLWriter, String nsURI, String localName)
		throws IOException
	{
		if (nsURI == null || nsURI.length() == 0)
		{
			throw new IOException("Invalid namespace URI: "+nsURI);
		}
		prettyXMLWriter._openTag();
		boolean alloced = false;
		String prefix = prettyXMLWriter.namespacePrefixes.get(nsURI);
		if (prefix == null) {
			prefix = allocPrefix(nsURI, prettyXMLWriter.namespacePrefixes);
			alloced = true;
		}
		prettyXMLWriter.writer.print('<');
		prettyXMLWriter.writer.print(prefix);
		prettyXMLWriter.writer.print(':');
		prettyXMLWriter.writer.print(localName);
		if (alloced) {
			prettyXMLWriter.attribute("xmlns:" + prefix, nsURI);
		}
		prettyXMLWriter.prettyXMLWriterProduct.handleDeclaredNamespaces(prettyXMLWriter);
	}

	public void closeTag(PrettyXMLWriter prettyXMLWriter, String nsURI, String localName)
		throws IOException
	{
		String prefix = prettyXMLWriter.namespacePrefixes.get(nsURI);
		if (prefix == null) {
			throw new IOException("Assertion failed: unknown namespace when closing tag");
		}
		prettyXMLWriter.indent--;
	
		if (prettyXMLWriter.isOpeningTag) {
			prettyXMLWriter.writer.println(" />");
		} else {
			if (prettyXMLWriter.afterNewline) {
				prettyXMLWriter.writeIndent();
			}
			prettyXMLWriter.writer.print("</");
			prettyXMLWriter.writer.print(prefix);
			prettyXMLWriter.writer.print(':');
			prettyXMLWriter.writer.print(localName);
			prettyXMLWriter.writer.println('>');
		}
		prettyXMLWriter._closeTag();
	}
}