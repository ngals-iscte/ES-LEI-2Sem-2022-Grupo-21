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
 */

package org.biojava.nbio.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Implementation of XMLWriter which emits nicely formatted documents
 * to a PrintWriter.
 *
 * @author Thomas Down
 * @since 1.3
 */

public class PrettyXMLWriter implements XMLWriter {
	PrettyXMLWriterProduct2 prettyXMLWriterProduct2 = new PrettyXMLWriterProduct2();

	PrettyXMLWriterProduct prettyXMLWriterProduct = new PrettyXMLWriterProduct();

	private int indentUnit = 2;

	PrintWriter writer;
	boolean isOpeningTag = false;
	boolean afterNewline = true;
	int indent = 0;

	Map<String, String> namespacePrefixes = new HashMap<String, String>();
	List<String> namespacesDeclared = new ArrayList<String>();

	public PrettyXMLWriter(PrintWriter writer) {
		this.writer = writer;
	}

	/**
	 * Declare a namespace for current and following elements
	 * 'prefixHint' is ignored entirely in this implementation	
	 * 
	 */
	@Override
	public void declareNamespace(String nsURI, String prefixHint)
		throws IOException
	{
		if (!namespacePrefixes.containsKey(nsURI)) {
			if (isOpeningTag) {
				String prefix = prettyXMLWriterProduct2.allocPrefix(nsURI, this.namespacePrefixes);
				attribute("xmlns:" + prefix, nsURI);
			} else {
				namespacesDeclared.add(nsURI);
			}
		}
	}

	protected void writeIndent()
		throws IOException
	{
		for (int i = 0; i < indent * indentUnit; ++i) {
			writer.write(' ');
		}
	}

	void _openTag()
		throws IOException
	{
		if (isOpeningTag) {
			writer.println('>');
			afterNewline = true;
		}
		if (afterNewline) {
			writeIndent();
		}
		indent++;
		isOpeningTag = true;
		afterNewline = false;
		prettyXMLWriterProduct2.getNamespaceBindings().add(null);
	}

	/**
	 * @deprecated Use {@link org.biojava.nbio.core.util.PrettyXMLWriterProduct2#openTag(org.biojava.nbio.core.util.PrettyXMLWriter,String,String)} instead
	 */
	@Override
	public void openTag(String nsURI, String localName)
		throws IOException
	{
		prettyXMLWriterProduct2.openTag(this, nsURI, localName);
	}

	@Override
	public void openTag(String qName)
		throws IOException
	{
		_openTag();
		writer.print('<');
		writer.print(qName);
		prettyXMLWriterProduct.handleDeclaredNamespaces(this);
	}

	@Override
	public void attribute(String nsURI, String localName, String value)
		throws IOException
	{
		if (! isOpeningTag) {
			throw new IOException("attributes must follow an openTag");
		}

		String prefix = namespacePrefixes.get(nsURI);
		if (prefix == null) {
			prefix = prettyXMLWriterProduct2.allocPrefix(nsURI, this.namespacePrefixes);
			attribute("xmlns:" + prefix, nsURI);
		}

		writer.print(' ');
		writer.print(prefix);
		writer.print(':');
		writer.print(localName);
		writer.print("=\"");
		prettyXMLWriterProduct.printAttributeValue(value, this.writer);
		writer.print('"');
	}

	@Override
	public void attribute(String qName, String value)
		throws IOException
	{
		if (! isOpeningTag) {
			throw new IOException("attributes must follow an openTag");
		}

		writer.print(' ');
		writer.print(qName);
		writer.print("=\"");
		prettyXMLWriterProduct.printAttributeValue(value, this.writer);
		writer.print('"');
	}

	void _closeTag() {
		isOpeningTag = false;
		afterNewline = true;
		List<String> hereBindings = prettyXMLWriterProduct2.getNamespaceBindings().removeLast();
		if (hereBindings != null) {
			for (Iterator<String> bi = hereBindings.iterator(); bi.hasNext(); ) {
				namespacePrefixes.remove(bi.next());
			}
		}
	}

	/**
	 * @deprecated Use {@link org.biojava.nbio.core.util.PrettyXMLWriterProduct2#closeTag(org.biojava.nbio.core.util.PrettyXMLWriter,String,String)} instead
	 */
	@Override
	public void closeTag(String nsURI, String localName)
		throws IOException
	{
		prettyXMLWriterProduct2.closeTag(this, nsURI, localName);
	}

	@Override
	public void closeTag(String qName)
		throws IOException
	{
		indent--;

		if (isOpeningTag) {
			writer.println(" />");
		} else {
			if (afterNewline) {
				writeIndent();
			}
			writer.print("</");
			writer.print(qName);
			writer.println('>');
		}
		_closeTag();
	}

	@Override
	public void println(String data)
		throws IOException
	{
	if (isOpeningTag) {
		writer.println('>');
		isOpeningTag = false;
	}
	prettyXMLWriterProduct.printChars(data, this.writer);
	writer.println();
	afterNewline = true;
	}

	@Override
	public void print(String data)
		throws IOException
	{
	if (isOpeningTag) {
		writer.print('>');
		isOpeningTag = false;
	}
	prettyXMLWriterProduct.printChars(data, this.writer);
	afterNewline = false;
	}

	// does not work for adding literal XML elements.
	@Override
	public void printRaw(String data)
		throws IOException
	{
	writer.println(data);
	}

	protected void printChars(String data)
		throws IOException
	{
	prettyXMLWriterProduct.printChars(data, this.writer);
	}

	protected void printAttributeValue(String data)
		throws IOException
	{
	prettyXMLWriterProduct.printAttributeValue(data, this.writer);
	}

	protected void numericalEntity(char c)
		throws IOException
	{
	prettyXMLWriterProduct.numericalEntity(c, this.writer);
	}

	@Override
	public void close()
		throws IOException
	{
		writer.close();
	}
}
