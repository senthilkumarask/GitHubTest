package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;


/**
 * The Class GetGenderKeyDroplet.
 */
public class GetGenderKeyDroplet extends BBBPresentationDroplet {
	private BBBCatalogTools mBBBCatalogTools;
	/**
	 * This method get the genderKey from the jsp and checks 
	 * if the key can be split into code.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug(" GetGenderKeyDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		
		final String genderKey = (String) pRequest.getParameter(GENDER_KEY);
		final String inverse = (String) pRequest.getParameter(INVERSE_FLAG);

		String genderCode ="";
		
		if (null != genderKey) {
			if(inverse!=null)
			{
				try{
				final Map<String, String> keyMap = getBBBCatalogTools().getConfigValueByconfigType(GENDER_MAP);
				for (Map.Entry<String, String> entry : keyMap.entrySet()) {
					if(entry.getKey().contains(genderKey))
					{
						genderCode=entry.getValue();
					}
				}
				
				}
				catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "Gender Key BBBSysyemException from service of GetGenderKeyDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1068),e);
					pRequest.setParameter(OUTPUT_ERROR_MSG, "genderValue is null");
					pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "Gender Key BBBBusinessException from service of GetGenderKeyDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1069),e);
						pRequest.setParameter(OUTPUT_ERROR_MSG, "genderValue is null");
						pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
				}
				logDebug("genderKey is:"+ genderKey);
			//if genderKey contains _ character , split
			logDebug(" GetGenderKeyDroplet genderCode="+genderCode +" for genderKey="+genderKey );
			pRequest.setParameter( GENDER_CODE, genderCode);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			
			}
			else{
				//if genderKey contains _ character , split
				if(genderKey.indexOf('_')>0){
					genderCode = genderKey.split("_")[1];
				} else {
					genderCode = genderKey;
				}
				logDebug(" GetGenderKeyDroplet genderCode="+genderCode +" for genderKey="+genderKey );
				
				pRequest.setParameter( GENDER_CODE, genderCode);
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}
		} else{
			pRequest.setParameter(OUTPUT_ERROR_MSG, "genderKy is null");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

		logDebug(" GetGenderKeyDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - End");
	}

	/** The genderCode string literal*/
	private static final String GENDER_CODE = "genderCode"; 
	/** The genderCode string literal*/
	private static final String GENDER_MAP = "GenderMap"; 
	
	/** The genderCode string literal*/
	private static final String INVERSE_FLAG = "inverseflag"; 
	
	/** The Parameter genderKey */
	private static final ParameterName GENDER_KEY = ParameterName
			.getParameterName("genderKey");

	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}
	/**
	 * @param mBBBCatalogTools the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}

}
