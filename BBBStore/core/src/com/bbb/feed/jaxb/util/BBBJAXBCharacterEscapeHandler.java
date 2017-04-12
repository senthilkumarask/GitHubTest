package com.bbb.feed.jaxb.util;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

/**
 * @author akhat1
 *
 */
public class BBBJAXBCharacterEscapeHandler implements CharacterEscapeHandler {

	/*
	 * This method overrides the default jaxb behaviour 
	 * of escaping special character
	 */
	@Override
	public void escape(char[] buf, int start, int len, boolean isAttValue,
			Writer out) throws IOException {

		if (buf != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < start + len; i++) {
				sb.append(buf[i]);
			}
			//do escaping
			String st = StringEscapeUtils.escapeXml(sb.toString());
			// Fix for defect BBBSL-2325
			//remove double escaping of characters that are already escaped by PIM
			st = st.replace("&amp;trade;", "&trade;")
					.replace("&amp;deg;", "&deg;")
					.replace("&amp;reg;", "&reg;");
			//write clean string
			out.write(st);
		}

	}
}
