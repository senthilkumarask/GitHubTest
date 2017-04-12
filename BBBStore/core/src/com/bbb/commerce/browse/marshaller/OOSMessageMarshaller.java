/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE: OOSMessageMarshaller.java
 *
 *  
 *  HISTORY:
 *  Rajesh Saini: Added New OOSEmailRequestVO parameter
 *  
 *
 */
package com.bbb.commerce.browse.marshaller;

import java.io.IOException;
import java.io.StringWriter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.bbb.commerce.browse.vo.DATARECORD;
import com.bbb.commerce.browse.vo.Main;
import com.bbb.commerce.browse.vo.OOSEmailRequestVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.MessageMarshaller;

public class OOSMessageMarshaller extends MessageMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void marshall(final ServiceRequestIF reqVO, final Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		OOSEmailRequestVO emailRequestVO=(OOSEmailRequestVO) reqVO;

		final DATARECORD datarecord = new DATARECORD();
		datarecord.setSKUID(emailRequestVO.getSkuId());
		datarecord.setPRODUCTID(emailRequestVO.getProductId());
		datarecord.setPRODUCTNAME(emailRequestVO.getProductName());
		datarecord.setEMAILADDR(emailRequestVO.getEmailAddr());
		datarecord.setCUSTNAME(emailRequestVO.getCustName());
		datarecord.setUSERIP(emailRequestVO.getUserIp());
		datarecord.setREQUESTEDDT(emailRequestVO.getRequestedDT());
		datarecord.setINSTOCKNOTIFYDT(emailRequestVO.getInStockNotifyDT());
		datarecord.setNOTICE1DT(emailRequestVO.getNotice1DT());
		datarecord.setNOTICE2DT(emailRequestVO.getNotice2DT());
		datarecord.setUNSUBSCRIBEDT(emailRequestVO.getUnsubscribeDT());
		datarecord.setFINALNOTICEDT(emailRequestVO.getFinalNoticeDT());
		datarecord.setSITEFLAG(emailRequestVO.getSiteFlag());
		
		final Main main = new Main();
		main.setDATARECORD(datarecord);
		
		StringWriter stringWriter = null;

		try {

			stringWriter = new StringWriter();
			final JAXBContext context = JAXBContext.newInstance(Main.class);
			final Marshaller marshaller = callCreateMarshaller(context);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(main, stringWriter);

			final String emailXml = stringWriter.getBuffer().toString();

			final TextMessage txtMessage = (TextMessage) pMessage;

			txtMessage.setText(emailXml);

		} catch (JMSException e) {
			throw new BBBSystemException(e.getMessage(), e);

		} catch (Exception e) {
			throw new BBBSystemException(BBBCoreErrorConstants.BROWSE_ERROR_1042,e.getMessage(), e);
		} finally {
			try {
				if (stringWriter != null){
					stringWriter.close();
				}
			} catch (IOException exception) {
				throw new BBBSystemException(BBBCoreErrorConstants.BROWSE_ERROR_1043,exception.getMessage(),
						exception);
			}
		}

	}

	protected Marshaller callCreateMarshaller(final JAXBContext context) throws JAXBException {
		return context.createMarshaller();
	}

}
