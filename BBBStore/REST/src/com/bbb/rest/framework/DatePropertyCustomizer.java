package com.bbb.rest.framework;

import java.text.SimpleDateFormat;

import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.rest.filtering.RestPropertyCustomizer;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.RestConstants;


public class DatePropertyCustomizer implements RestPropertyCustomizer{

	public static String CLASS_VERSION = "$Id: //hosting-blueprint/MobileCommerce/version/10.1.1/server/MobileCommerce/src/atg/rest/filtering/customizers/RepriceCartPropertyCustomizer.java#1 $$Change: 698235 $";
	private static final ApplicationLogging DateLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(DatePropertyCustomizer.class);

	@Override
	public Object getPropertyValue(final String pPropertyName, final Object pResource) {

		if(DateLOGGING.isLoggingDebug()){
			DateLOGGING.logDebug("Entering DatePropertyCustomizer.getPropertyValue");
		}

		String date = null;
		SimpleDateFormat sdf = null;
		final String siteId = SiteContextManager.getCurrentSiteId();

		if(pResource instanceof java.sql.Timestamp)
		{
			if((siteId!=null)&&siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				sdf = new SimpleDateFormat(RestConstants.CA_TIMESTAMP_DATE_FORMAT);}
			else{
			sdf = new SimpleDateFormat(RestConstants.TIMESTAMP_DATEFORMAT);}
			if(DateLOGGING.isLoggingDebug()){
				DateLOGGING.logDebug("DatePropertyCustomizer :: Simple DateFormat : " + sdf );
			}
		}
		else if((pResource instanceof java.sql.Date) || (pResource instanceof java.util.Date) )
		{
			if((siteId!=null)&&siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				sdf = new SimpleDateFormat(RestConstants.CA_DATE_FORMAT);}
			else{
			sdf = new SimpleDateFormat(RestConstants.DATE_DATEFORMAT);}
			if(DateLOGGING.isLoggingDebug()){
				DateLOGGING.logDebug("DatePropertyCustomizer :: Simple DateFormat : " + sdf );
			}
		} else {
			DateLOGGING.logError("Invalid Date object");
			return null;
		}

		date = sdf.format(pResource);
		
		if(DateLOGGING.isLoggingDebug()){
			DateLOGGING.logDebug("DatePropertyCustomizer :: Formatted Date  : " + date );
			DateLOGGING.logDebug("Exiting DatePropertyCustomizer.getPropertyValue");
		}

		return date;
	}

	@Override
	public void setPropertyValue(final String arg0, final Object arg1, final Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
