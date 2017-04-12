package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This droplet Fetch current date from registry and convert to Mexico date format
 */
public class MxDateFormatDroplet extends BBBPresentationDroplet {
	
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug("MxDateFormatDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("MxDateFormatDroplet", "service method");
		
		String mxConvertedDate;
		String currentDate = pRequest.getParameter("currentDate");		
		
		if (currentDate!=null)
		{
			mxConvertedDate =  BBBUtility.convertUSDateIntoWSFormatCanada(currentDate);
			logDebug("MxDateFormatDroplet mxConvertedDate="+mxConvertedDate );				
			pRequest.setParameter( MXCONVERTEDDATE, mxConvertedDate);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		}
		else
		{
			logError(LogMessageFormatter.formatMessage(pRequest, "Date Format BBBBusinessException from service of MxDateFormatDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1069),null);
			pRequest.setParameter(OUTPUT_ERROR_MSG, "currentDate is null");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
			
		logDebug(" MxDateFormatDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - End");
	}
	
	/** The mxConvertedDate string literal*/
	private static final String MXCONVERTEDDATE = "mxConvertedDate"; 	
	
}