package net.mortalsilence.indiepim.server.utils;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;

public class UnknownCharsetProvider extends CharsetProvider {
	private static final String badCharset = "x-unknown";
	private static final String goodCharset = "iso-8859-1";

	public Charset charsetForName(String charset) {
		if (charset.equalsIgnoreCase(badCharset))
			return Charset.forName(goodCharset);
		return null;
	}

	public Iterator<Charset> charsets() {
		return null;      
	} 
}