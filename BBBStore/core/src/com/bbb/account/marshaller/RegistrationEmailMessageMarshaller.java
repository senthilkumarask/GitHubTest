/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE: RegistrationEmailMessageMarshaller.java
 *
 *  DESCRIPTION: marshall the Registration Email
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  27/03/12 Initial version
 *
 */
package com.bbb.account.marshaller;

import java.io.IOException;
import java.io.StringWriter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.bbb.account.vo.profile.ProfileDetail;
import com.bbb.account.vo.profile.RegistrationEmailRequestVO;
import com.bbb.account.vo.profile.UserProfile;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.MessageMarshaller;

public class RegistrationEmailMessageMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;

	@Override
	public void marshall(final ServiceRequestIF reqVO, final Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		final RegistrationEmailRequestVO emailRequestVO = (RegistrationEmailRequestVO) reqVO;

		final ProfileDetail profileDetail = new ProfileDetail();
		profileDetail.setEmailType(emailRequestVO.getEmailType());
		profileDetail.setFirstName(emailRequestVO.getFirstName());
		profileDetail.setLastName(emailRequestVO.getLastName());
		profileDetail.setEmailAddress(emailRequestVO.getEmailAddress());
		profileDetail.setPhone1(emailRequestVO.getPhone1());
		profileDetail.setPhone2(emailRequestVO.getPhone2());
		profileDetail.setOptin(emailRequestVO.getOptin());
		profileDetail.setProfileId(emailRequestVO.getProfileId());
		profileDetail.setShareAccount(emailRequestVO.getShareAccount());
		profileDetail.setSiteFlag(emailRequestVO.getSiteFlag());
		profileDetail.setSubmitDate(emailRequestVO.getSubmitDate());

		final UserProfile profile = new UserProfile();
		profile.setProfileAttributes(profileDetail);

		StringWriter stringWriter = null;

		try {

			stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(UserProfile.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(profile, stringWriter);

			final String emailXml = stringWriter.getBuffer().toString();

			final TextMessage txtMessage = (TextMessage) pMessage;

			txtMessage.setText(emailXml);

		} catch (JMSException e) {
			throw new BBBSystemException(e.getErrorCode(),e.getMessage(), e);

		} catch (Exception e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1266,e.getMessage(), e);
		} finally {
			try {
				if (stringWriter != null) {
					stringWriter.close();
				}
			} catch (IOException exception) {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1267,exception.getMessage(), exception);
			}
		}

	}
}
