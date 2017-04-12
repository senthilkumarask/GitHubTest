package atg.sitemap;

import atg.core.util.StringUtils;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * The Class MobileSitemapTools.
 */
public class MobileSitemapTools extends SitemapTools{
	
    /**
     * This method is used to append header to mobile sitemap xml.
     * @param pSitemapXML
     */
	@Override
	public void appendSitemapHeader(final StringBuilder pSitemapXML) {
		if(isLoggingDebug()){
			this.logDebug("MobileSitemapTools.appendSitemapHeader() - start");
		}
		pSitemapXML
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:mobile=\"http://www.google.com/schemas/sitemap-mobile/1.0\">");
		
		if(isLoggingDebug()){
			this.logDebug("MobileSitemapTools.appendSitemapHeader() - end");
		}
	}

	
	/**
	 * This method is used to generate sitemap xml.
	 * 
	 * @param pUrl
	 * @param pFrequency
	 * @param pPriority
	 * @param pDebugMode
	 * 
	 */
	@SuppressWarnings("boxing")
	@Override
	public String generateSitemapUrlXml(final String pUrl, final String pFrequency, final String pPriority, final boolean pDebugMode) {
		if(isLoggingDebug()){
			this.logDebug("MobileSitemapTools.generateSitemapUrlXml() - start");
		}
		
		if (BBBUtility.isEmpty(pUrl) || pUrl.equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)) {
			return "";
		}
		
		final String [] urlParts = StringUtils.splitStringAtString(pUrl, BBBCoreConstants.SEMICOLON);
		final String itemURL = urlParts[0];
		
		final StringBuilder urlXml = new StringBuilder(100);
		String indent1 = BBBCoreConstants.BLANK;
		String indent2 = BBBCoreConstants.BLANK;
		if (pDebugMode) {
			indent1 = BBBCoreConstants.INDENT_1;
			indent2 = BBBCoreConstants.INDENT_2;
		}

		urlXml.append(indent1);
		urlXml.append(BBBCoreConstants.URL_TAG);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.LOC_TAG);
		urlXml.append(itemURL);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.LOC_TAG_CLOSE);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.MOBILE_TAG);			
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.CHANGE_FREQ_TAG);
		urlXml.append(pFrequency);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.CHANGE_FREQ_TAG_CLOSE);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.PRIORITY_TAG);
		urlXml.append(pPriority);
		urlXml.append(indent2);
		urlXml.append(BBBCoreConstants.PRIORITY_TAG_CLOSE);
		urlXml.append(indent1);
		urlXml.append(BBBCoreConstants.URL_TAG_CLOSE);

		if(isLoggingDebug()){
			this.logDebug("MobileSitemapTools.generateSitemapUrlXml() - end");
		}
		return urlXml.toString();
	}

}
