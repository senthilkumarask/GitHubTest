/**
 * 
 */
package com.bbb.account.order.droplet;



import java.io.IOException;


import javax.servlet.ServletException;

import com.bbb.account.BBBOrderTrackingManager;
import com.bbb.account.order.manager.OrderDetailsManager;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBTrackOrderVO;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;

import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

import atg.commerce.CommerceException;

import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

public class OrderSummaryDetails extends BBBDynamoServlet {
	
	private BBBOrderTrackingManager mOrderTrackingManager;
	
	private BBBTrackOrderVO mBBBTrackOrderVO;
	private String mOrderId;
	private String mEmailId;
	private String mEmailIdMD5;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private String userTokenBVRR;
	private BBBCatalogTools mCatalogTools;
	private static final String EMPTY = "empty";
	private static final String OUTPUT = "output";
	private String emailMD5HashPrefix;
	private OrderDetailsManager orderDetailsManager;
	private BBBOrderTools orderTools;

	public BBBOrderTools getOrderTools() {
		return orderTools;
	}

	public void setOrderTools(BBBOrderTools orderTools) {
		this.orderTools = orderTools;
	}

	public OrderDetailsManager getOrderDetailsManager() {
		return orderDetailsManager;
	}

	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}

	/**
	 * @return the emailMD5HashPrefix
	 */
	public String getEmailMD5HashPrefix() {
		return this.emailMD5HashPrefix;
	}

	/**
	 * @param emailMD5HashPrefix the emailMD5HashPrefix to set
	 */
	public void setEmailMD5HashPrefix(String emailMD5HashPrefix) {
		this.emailMD5HashPrefix = emailMD5HashPrefix;
	}
	
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		
		
		Profile profile = (Profile) pReq.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String orderId= pReq.getParameter("orderNum");
		String webServiceOrderFlag= pReq.getParameter("wsOrder");
		boolean wsOrder =Boolean.valueOf(webServiceOrderFlag);
		
		if (!BBBUtility.isEmpty(orderId)) {
		this.setBBBTrackOrderVO(new BBBTrackOrderVO());
		this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_FALSE);
		BBBOrderVO bbbOrderVO=null;  
		BBBOrderImpl order = null;
		try {	
				if(!wsOrder){
				order = (BBBOrderImpl) getOrderTools().getOrderFromOnlineOrBopusOrderNumber(
							orderId);
				bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order, true);
			if (bbbOrderVO != null) {
				this.getBBBTrackOrderVO().setBbbOrderVO(bbbOrderVO); 
			}else{
				this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_TRUE);
			}
				}
		} catch (BBBBusinessException e) {
			//If no order with the entered order Id is found in ATG
			if ("account_1073:err_no_such_order".equals(e.getMessage())) {
				this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_TRUE);
			} else {
				//If the order id entered and email id are not associated
				if ("account_1074:order_email_not_associated".equals(e.getMessage())) {					
					logError(e.getMessage());
				} else {
					logError(LogMessageFormatter.formatMessage(pReq, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
							, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
				}
			}
		} catch (BBBSystemException | CommerceException e) {
			//In case of no business error but a system (technical)exception
			logError(e.getMessage(),e);
		}catch (RepositoryException e) {
			logError(
					LogMessageFormatter.formatMessage(
							null,
							"RepositoryException got from method getOrderTools()"
									+ ".getOrderFromOnlineOrBopusOrderNumber(pOrderId). Order Id: "
									+ orderId,
									BBBCoreErrorConstants.ACCOUNT_ERROR_1133), e);
		}

		//If no ATG order found with entered fields then manager's method to get legacy orders is called
		if (this.getBBBTrackOrderVO().isLegacyOrderFlag()|| wsOrder) {
			try {
				// R2.2 517C start
				if (!BBBUtility.isEmpty(orderId)) {
					this.getBBBTrackOrderVO().setLegacyOrderVO(this.getOrderDetailsManager().getLegacyOrderDetailInfo(orderId)); //manager's method to get legacy order
					//this.setUserTokenBVRR(BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()), this.getCatalogTools().getBazaarVoiceKey(),emailId));
				}
				//R2.2 517C end
				if (this.getBBBTrackOrderVO().getLegacyOrderVO().getStatus().isErrorExists()) {
					this.mBBBTrackOrderVO = null;
					pReq.serviceLocalParameter(EMPTY, pReq, pRes);
				}

			} catch (BBBSystemException e) {
				//Error while calling Web Service
				logError(LogMessageFormatter.formatMessage(pReq, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
						, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pReq, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
						, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
			}
		}
	}

			pReq.setParameter("BBBTrackOrderVO", getBBBTrackOrderVO());
			pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
	}


public BBBOrderTrackingManager getOrderTrackingManager() {
	return mOrderTrackingManager;
}

public void setOrderTrackingManager(
		BBBOrderTrackingManager mOrderTrackingManager) {
	this.mOrderTrackingManager = mOrderTrackingManager;
}

public BBBTrackOrderVO getBBBTrackOrderVO() {
	return mBBBTrackOrderVO;
}

public void setBBBTrackOrderVO(BBBTrackOrderVO mBBBTrackOrderVO) {
	this.mBBBTrackOrderVO = mBBBTrackOrderVO;
}

public String getOrderId() {
	return mOrderId;
}

public void setOrderId(String mOrderId) {
	this.mOrderId = mOrderId;
}

public String getEmailId() {
	return mEmailId;
}

public void setEmailId(String mEmailId) {
	this.mEmailId = mEmailId;
}

public String getEmailIdMD5() {
	return mEmailIdMD5;
}

public void setEmailIdMD5(String mEmailIdMD5) {
	this.mEmailIdMD5 = mEmailIdMD5;
}

public LblTxtTemplateManager getLblTxtTemplateManager() {
	return mLblTxtTemplateManager;
}

public void setLblTxtTemplateManager(
		LblTxtTemplateManager mLblTxtTemplateManager) {
	this.mLblTxtTemplateManager = mLblTxtTemplateManager;
}

public String getUserTokenBVRR() {
	return userTokenBVRR;
}

public void setUserTokenBVRR(String userTokenBVRR) {
	this.userTokenBVRR = userTokenBVRR;
}

public BBBCatalogTools getCatalogTools() {
	return mCatalogTools;
}

public void setCatalogTools(BBBCatalogTools mCatalogTools) {
	this.mCatalogTools = mCatalogTools;
}
}