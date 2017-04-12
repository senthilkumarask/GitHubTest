package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * changes starts for story : BBBP-4573 : Notify Registrant (Show Message) while adding N & D status items
 * 
 * The Class NotifyRegValidateDroplet.
 * This class will be used to validate whether
 * notify registrant modal/slider will open or not.
 *  
 */
public class NotifyRegValidateDroplet extends BBBDynamoServlet {
	
	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	
	/** The Constant OPARAM_OUTPUT. */
	public final static String OPARAM_OUTPUT = "output";

	/** The Constant OPARAM_DISPLAY_FLAG. */
	public final static String OPARAM_DISPLAY_FLAG = "displayMessageType";
	
	/** EVENT_DATE. */
	public final static String EVENT_DATE = "eventDate";
	
	/** SKU_ID. */
	public final static String SKU_ID = "skuId";
	
	/** REGISTRY_ID. */
	public final static String REGISTRY_ID = "registryId";
	
	String displayMessageType;

	/**
	 * @return the displayMessage
	 */
	public String getDisplayMessage() {
		return displayMessageType;
	}

	/**
	 * @param displayMessage the displayMessage to set
	 */
	public void setDisplayMessage(String displayMessage) {
		this.displayMessageType = displayMessage;
	}

	private GiftRegistryManager mGiftRegistryManager;
	
	
	/** @return the giftRegistryManager */
    public final GiftRegistryManager getGiftRegistryManager() {
        return this.mGiftRegistryManager;
    }

    /** @param pGiftRegistryManager the giftRegistryManager to set */
    public final void setGiftRegistryManager(final GiftRegistryManager pGiftRegistryManager) {
        this.mGiftRegistryManager = pGiftRegistryManager;
    }
    
	/**
	 * This method gets event date, sku id and registry id to validate whether
	 * notify registrant modal/slider will open or not.
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

		logDebug(" NotifyRegValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String eventDate = pRequest.getParameter(EVENT_DATE);
		String skuId = pRequest.getParameter(SKU_ID);		
		
		try {
			if(BBBUtility.isEmpty(eventDate)){
				String regId = pRequest.getParameter(REGISTRY_ID);
				eventDate = getGiftRegistryManager().getRegistryDate(pRequest, regId);
				
				logDebug("regId:" + regId + ":eventDate:" + eventDate);
			}			
			this.displayMessageType = getGiftRegistryManager().getNotifyRegistrantMsgType(skuId, eventDate);
			logDebug("response after all rule validations : "+this.displayMessageType);
			
		} catch (BBBSystemException e) {
			logError(" BBBSystemException error occured in NotifyRegValidateDroplet",e);
		} catch (BBBBusinessException e) {
			logError(" BBBBusinessException error occured in NotifyRegValidateDroplet while fetching event date",e);
		}
		catch (Exception e){
			logError(" other exceptions in NotifyRegValidateDroplet",e);
		}
		pRequest.setParameter(OPARAM_DISPLAY_FLAG, this.displayMessageType);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		
		logDebug(" NotifyRegValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - End");
	}

}
/*changes ends for story : BBBP-4573 : Notify Registrant (Show Message) while adding N & D status items*/