package com.bbb.seo;

import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

import com.bbb.utils.BBBUtility;

public class BBBThirdPartyIndirectUrlTemplate extends IndirectUrlTemplate {
private static final String AMPERSAND_HTML_CODE = "&amp;";
private static final String AMPERSAND = "&";
private boolean escapeCharecters;
private Map<String,String> listOfEscapeCharecters =new TreeMap<String,String>();
	public String formatUrl(UrlParameter[] pUrlParams, WebApp pDefaultWebApp)
			throws ItemLinkException {
		String formatUrl = null;
		if (pUrlParams != null && BBBUtility.isNotEmpty(pUrlParams[0].getValue())) {
			int length = pUrlParams.length;
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					if(isLoggingDebug()){
						logDebug("Current Processing URL : "  + pUrlParams[i].toString());
					}
					String decodedUrl = URLDecoder.decode(pUrlParams[i].toString());
					if(isLoggingDebug()){
						logDebug("Decoded Url : " + decodedUrl);
					}
					pUrlParams[i].setValue(decodedUrl);
				}
			}
			formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
			/*
			 * Condition to replace below special charecter's & — The ampersand
			 * character must always be written as &amp; ' — The single quote
			 * character must always be written as &apos; " — The double quote
			 * character must always be written as &quot; < — The less than
			 * symbol must always be written as &lt; > — The greater than symbol
			 * must always be written as &gt;
			 */
			if (isEscapeCharecters()) {
				if (formatUrl!=null && formatUrl.contains(AMPERSAND)){
					formatUrl = formatUrl
							.replace(AMPERSAND,AMPERSAND_HTML_CODE);
				}
				if (listOfEscapeCharecters != null
						&& !listOfEscapeCharecters.isEmpty()
						&& formatUrl != null) {
					Iterator<Entry<String, String>> iterator = listOfEscapeCharecters
							.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator
								.next();
						if (formatUrl.contains(mapEntry.getKey().toString())) {
							formatUrl = formatUrl
									.replace(mapEntry.getKey().toString(),
											mapEntry.getValue().toString());
						}
					}
				}

			}
			if(isLoggingDebug()){
				logDebug("formatUrl : " + formatUrl);
			}
			if (BBBUtility.isNotEmpty(formatUrl)) {
				formatUrl = formatUrl.toLowerCase();
			}
		}
		return formatUrl;

	}
	public boolean isEscapeCharecters() {
		return escapeCharecters;
	}
	public void setEscapeCharecters(boolean escapeCharecters) {
		this.escapeCharecters = escapeCharecters;
	}
	public Map<String,String> getListOfEscapeCharecters() {
		return listOfEscapeCharecters;
	}
	public void setListOfEscapeCharecters(Map<String,String> listOfEscapeCharecters) {
		this.listOfEscapeCharecters = listOfEscapeCharecters;
	}

}
