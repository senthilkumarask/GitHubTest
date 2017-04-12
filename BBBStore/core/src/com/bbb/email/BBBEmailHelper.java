/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE: BBBEmailHelper.java
 *
 *  DESCRIPTION: Email Helper
 * 
 *  
 *  HISTORY:
 *  rsain4: Added sending Email message to tibco during registraion
 *  
 *
 */

package com.bbb.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import atg.repository.RepositoryException;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.commerce.browse.vo.OOSEmailRequestVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class BBBEmailHelper {
	
	/** COMMA DELIMITER*/
	private final static String DELIMITER_COMMA =";" ;
	
	/**
	 * This method is used to send the email to Tibco . 
	 *  @param Profile
	 *  @param Map
	 *  @param TemplateEmailSender
	 *  @param TemplateEmailInfoImpl
	 */
	
	public static void sendEmail(final Profile pProfile, final Map pEmailParameters, final BBBTemplateEmailSender pEmailSender, 
			final TemplateEmailInfoImpl pEmailInfo) throws TemplateEmailException, RepositoryException
	{
		
		TemplateEmailInfo emailInfo = createEmailInfo(pEmailInfo, pEmailParameters);
		List receipientList = createRecipientList(pEmailParameters);

		pEmailSender.sendEmailMessage(emailInfo, receipientList, true, false);
		
	}
	
	
	/**
	 * This methos is used to create email info. 
	 *  @param TemplateEmailInfoImpl
	 *  @param Map
	 */
	private static TemplateEmailInfo createEmailInfo(final TemplateEmailInfoImpl pEmailInfo, final Map pEmailParameters)
	{
		//TemplateEmailInfoImpl emailInfo = new TemplateEmailInfoImpl();
		pEmailInfo.setMessageFrom((String)pEmailParameters.get(BBBCoreConstants.SENDER_EMAIL_PARAM_NAME));
		pEmailInfo.setTemplateURL((String)pEmailParameters.get(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME));
		pEmailInfo.setTemplateParameters(pEmailParameters);
		pEmailInfo.setMessageSubject((String)pEmailParameters.get(BBBCoreConstants.SUBJECT_PARAM_NAME));
		if(pEmailParameters.containsKey("messageCC")){
			pEmailInfo.setMessageCc((String)pEmailParameters.get("messageCC"));
		}

		if(pEmailParameters.containsKey("messageBCC")){
			pEmailInfo.setMessageBcc((String)pEmailParameters.get("messageBCC"));
		}
		
		pEmailInfo.setSiteId((String)pEmailParameters.get("siteId"));
		return pEmailInfo;
	}
	
	/**
	 * This methos is used to create Recipient list. 
	 *  @param Map
	 */
	private static List<String> createRecipientList(final Map pEmailParameters)
	{
		final List<String> recipientList = new ArrayList<String>();
		
		final String emailListInString = (String)pEmailParameters.get(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME);
		String emails[] = null;
		
		//if comma delimited emails are input
		if( emailListInString !=null 
				&& emailListInString.indexOf(DELIMITER_COMMA)>0){
			
			emails = emailListInString.split(DELIMITER_COMMA);
			if(emails != null ){
				for(int index=0; index < emails.length ; index++){
					
					final String email = emails[index].trim();
					if(email.length() >0){
						recipientList.add( emails[index].trim() );
					}
				}
			}
				
		} else if(emailListInString != null) {
			//in case there is only one email
			recipientList.add(emailListInString );
		}
		
		return recipientList;
	}
	
	
	/**
	 * This method will set email params for Subscribe OOS Email Notification
	 * in the OOSEmailRequestVO
	 * 
	 * @param Map
	 * 
	 * @return void
	 */
	public static void sendTibcoEmail(final Map emailParams) throws  BBBSystemException, BBBBusinessException 
	{		
		final OOSEmailRequestVO oosEmailRequestVO = new OOSEmailRequestVO();
		if(emailParams.get(BBBCoreConstants.SKU_PARAM_NAME) != null){
			oosEmailRequestVO.setSkuId(emailParams.get(BBBCoreConstants.SKU_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.PRODUCT_ID_PARAM_NAME) != null){
			oosEmailRequestVO.setProductId(emailParams.get(BBBCoreConstants.PRODUCT_ID_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.PRODUCT_NAME_PARAM_NAME) != null){
			oosEmailRequestVO.setProductName(emailParams.get(BBBCoreConstants.PRODUCT_NAME_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME) != null){
			oosEmailRequestVO.setEmailAddr(emailParams.get(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.CUST_NAME_PARAM_NAME) != null){
			oosEmailRequestVO.setCustName(emailParams.get(BBBCoreConstants.CUST_NAME_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.USER_IP_PARAM_NAME) != null){
			oosEmailRequestVO.setUserIp(emailParams.get(BBBCoreConstants.USER_IP_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setRequestedDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.IN_STOCK_NOTIFY_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setInStockNotifyDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.IN_STOCK_NOTIFY_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.NOTICE_1_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setNotice1DT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.NOTICE_1_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.NOTICE_2_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setNotice2DT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.NOTICE_2_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.UNSUBSCRIBE_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setUnsubscribeDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.UNSUBSCRIBE_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.FINAL_NOTICE_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setFinalNoticeDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.FINAL_NOTICE_DT_PARAM_NAME)));
		}
		if((emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME) != null) && (!emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString().equals(""))){
			oosEmailRequestVO.setSiteFlag(emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString());
		}
		ServiceHandlerUtil.send(oosEmailRequestVO);
	}
	

	/**
	 * This method will set email params for UnSubscribe OOS Email Notification
	 * in the OOSEmailRequestVO
	 * 
	 * @param Map
	 * 
	 * @return void
	 */
	public static void sendUnsubscribeTibcoEmail(final Map emailParams) throws  BBBSystemException, BBBBusinessException 
	{		
		final OOSEmailRequestVO oosEmailRequestVO = new OOSEmailRequestVO();
		
		if(emailParams.get(BBBCoreConstants.SKU_PARAM_NAME) != null){
			oosEmailRequestVO.setSkuId(emailParams.get(BBBCoreConstants.SKU_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.PRODUCT_ID_PARAM_NAME) != null){
			oosEmailRequestVO.setProductId(emailParams.get(BBBCoreConstants.PRODUCT_ID_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.PRODUCT_NAME_PARAM_NAME) != null){
			oosEmailRequestVO.setProductName(emailParams.get(BBBCoreConstants.PRODUCT_NAME_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME) != null){
			oosEmailRequestVO.setEmailAddr(emailParams.get(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.CUST_NAME_PARAM_NAME) != null){
			oosEmailRequestVO.setCustName(emailParams.get(BBBCoreConstants.CUST_NAME_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.USER_IP_PARAM_NAME) != null){
			oosEmailRequestVO.setUserIp(emailParams.get(BBBCoreConstants.USER_IP_PARAM_NAME).toString());
		}
		if(emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setRequestedDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.REQUESTED_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.IN_STOCK_NOTIFY_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setInStockNotifyDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.IN_STOCK_NOTIFY_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.NOTICE_1_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setNotice1DT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.NOTICE_1_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.NOTICE_2_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setNotice2DT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.NOTICE_2_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.UNSUBSCRIBE_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setUnsubscribeDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.UNSUBSCRIBE_DT_PARAM_NAME)));
		}
		if(emailParams.get(BBBCoreConstants.FINAL_NOTICE_DT_PARAM_NAME) != null){
			oosEmailRequestVO.setFinalNoticeDT((XMLGregorianCalendar)(emailParams.get(BBBCoreConstants.FINAL_NOTICE_DT_PARAM_NAME)));
		}
		if((emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME) != null) && (!emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString().equals(""))){
			oosEmailRequestVO.setSiteFlag(emailParams.get(BBBCoreConstants.SITE_FLAG_PARAM_NAME).toString());
		}

		ServiceHandlerUtil.send(oosEmailRequestVO);
	}

	/**
	 *  Overload version of sendEmail
	 *   
	 *  @param Map
	 *  @param TemplateEmailSender
	 *  @param TemplateEmailInfoImpl
	 */	
	/*public static void sendEmail(final Map pEmailParameters,final TemplateEmailSender pEmailSender, 
			final TemplateEmailInfoImpl pEmailInfo) throws TemplateEmailException {
		
		final TemplateEmailInfo emailInfo = createEmailInfo(pEmailInfo, pEmailParameters);
		emailInfo.setTemplateParameters(pEmailParameters);
		final List<String> receipientList = createRecipientList(pEmailParameters);
		pEmailSender.sendEmailMessage(emailInfo, receipientList, true, false);
	}*/
	
	
	public static void sendEmail(Map pEmailParameters, BBBTemplateEmailSender pEmailSender, TemplateEmailInfoImpl pEmailInfo) throws TemplateEmailException
	{
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEND_EMAIL, "sendEmail");
		TemplateEmailInfo emailInfo = createEmailInfo(pEmailInfo, pEmailParameters);
		List receipientList = createRecipientList(pEmailParameters);

		pEmailSender.sendEmailMessage(emailInfo, receipientList, true, false);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_EMAIL, "sendEmail");
	}
	
/*	public static void sendEmailMulti(final Profile pProfile, final Map pEmailParameters, final BBBTemplateEmailSender pEmailSender, 
			final TemplateEmailInfoImpl pEmailInfo) throws TemplateEmailException, RepositoryException
	{
	
		TemplateEmailInfo emailInfo = createEmailInfo(pEmailInfo, pEmailParameters);
		List receipientList = createRecipientList(pEmailParameters);

		pEmailSender.sendEmailMessage(emailInfo, receipientList, true, false);
		
	}*/

}
