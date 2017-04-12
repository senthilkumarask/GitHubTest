/*
 *  Copyright 2014, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CreatePendingShipmentReqBuilder.java
 *
 *  DESCRIPTION: to build email tag request for Fedex Service
 *
 *  HISTORY:
 *  12/19/2014 Initial version
 *
 */
package com.bbb.selfservice.manager;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import atg.multisite.SiteContext;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.fedex.CreatePendingShipmentReqBuilder;
import com.bbb.fedex.ProcessShipmentReqBuilder;
import com.bbb.fedex.vo.CreatePendingShipmentReqVo;
import com.bbb.fedex.vo.CreatePendingShipmentResVo;
import com.bbb.fedex.vo.ProcessShipmentReqVo;
import com.bbb.fedex.vo.ProcessShipmentResVo;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.fedex.ws.openship.v7.CreateOpenShipmentRequest;
import com.fedex.ws.ship.v15.CompletedPackageDetail;
import com.fedex.ws.ship.v15.CompletedShipmentDetail;
import com.fedex.ws.ship.v15.Notification;
import com.fedex.ws.ship.v15.ProcessShipmentReply;
import com.fedex.ws.ship.v15.ProcessShipmentRequest;
import com.fedex.ws.ship.v15.ShippingDocument1;
import com.fedex.ws.ship.v15.ShippingDocumentPart;
/** 
 *  This class is used to call the FedEx Web services.
 */

public class FedexShipService extends BBBGenericService{

	/** 
	 *  to hold processShipment string value
	 */
	private  String PROCESSSHIPMENT =  "processShipment";
	
	/** 
	 *  to hold FedEx string value
	 */
	private  String FedEx =  "FedEx";
	
	/** 
	 *  to hold PDF string value
	 */
	private  String PDF =  ".pdf";
	

	/** 
	 *  to hold TRACKING_NUMBER string value
	 */
	private  String TRACKING_NUMBER =  "trackingNumber";
	
	
	/** 
	 *  to hold SUCCESS string value
	 */
	private  String SUCCESS =  "success";
	
	/** 
	 *  to hold Failure string value
	 */
	private  String FAILURE = "failure";
	
	/** 
	 *  to hold CREATEPENDINGSHIPMENT string value
	 */
	private  String CREATEPENDINGSHIPMENT = "createPendingShipment";
	
	/** 
	 *  to hold URL string value
	 */
	private  String URL = "Url";
	
	/** This method is used to  create a Label by using FedEx  ProcessShipment Service
	 * @param pMap - to hold customer details
	 * @param pBBBCatalogTools - to hold CatalogTools
	 * @param pSiteContext - to hold SiteContext Object
	 * @return - return Result Map
	 */
	@SuppressWarnings("unchecked")
	public Map<String , Object>  getLabel(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext){
		
		ProcessShipmentReqBuilder pTagReqBuilder = new ProcessShipmentReqBuilder();
		ProcessShipmentReqVo pReqVo = new ProcessShipmentReqVo();
		Map<String , Object> finalResult = new HashedMap();
		
		ProcessShipmentRequest request = this.buildRequestGetLabel(pMap, pBBBCatalogTools, pSiteContext, pTagReqBuilder); // Build a request object		
		try {
			pReqVo.setProcessShipmentRequest(request);
			pReqVo.setServiceName(PROCESSSHIPMENT);
			ProcessShipmentResVo replyRes = this.invokeServiceHandler(pReqVo);
			ProcessShipmentReply reply = replyRes.getProcessShipmentReply();
			if (pTagReqBuilder.isResponseOk(reply.getHighestSeverity())) // check if the call was successful
			{
				CompletedShipmentDetail csd = reply.getCompletedShipmentDetail();
				CompletedPackageDetail[] cpKd = csd.getCompletedPackageDetailsArray();				
				for(int j=0;j<cpKd.length; j++){
					CompletedPackageDetail cpd= cpKd[j];
					ShippingDocument1 sd1 = 	(ShippingDocument1)cpd.getLabel();
					ShippingDocumentPart[] sdparts  = sd1.getPartsArray();
					for (int a=0; a < sdparts.length; a++) {
						ShippingDocumentPart sdpart = sdparts[a];
					byte[] bytes1 = sdpart.getImage();
					if(bytes1!=null){
						String trackingNumber = cpd.getTrackingIdsArray(0).getTrackingNumber();
						trackingNumber  = FedEx+trackingNumber+PDF;
						finalResult.put(TRACKING_NUMBER, trackingNumber);
						finalResult.put(SUCCESS, bytes1);
						return finalResult;
					}
				}
				}		
					
			}else{
				Notification[] nfs = reply.getNotificationsArray();
				if(nfs!=null){				
					finalResult.put(FAILURE, nfs);	
					return finalResult;
				}
			}

			

		} catch (Exception e) {
		   logError(e.getMessage(),e);
		}
		return null; 
	
		
	}

	/**
	 * @param pMap
	 * @param pBBBCatalogTools
	 * @param pSiteContext
	 * @param pTagReqBuilder
	 * @return
	 */
	protected ProcessShipmentRequest buildRequestGetLabel(
			Map<String, String> pMap, BBBCatalogTools pBBBCatalogTools,
			SiteContext pSiteContext, ProcessShipmentReqBuilder pTagReqBuilder) {
		return pTagReqBuilder.buildRequest(pMap ,pBBBCatalogTools ,pSiteContext);
	}

	/**
	 * @param pReqVo
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected ProcessShipmentResVo invokeServiceHandler(
			ProcessShipmentReqVo pReqVo) throws BBBBusinessException,
			BBBSystemException {
		return (ProcessShipmentResVo)	ServiceHandlerUtil.invoke(pReqVo);
	}
	
	/**
	 * This method is used to send a label in email by using FedEx CreatePendingShipment Service.
	 * @param pMap - to hold customer information
	 * @param pBBBCatalogTools - to hold catalog tools
	 * @param pSiteContext - to hold site context object
	 * @return -   Map
	 */
	public Map<String , Object> sendEmailLabel(Map<String , String> pMap , BBBCatalogTools pBBBCatalogTools ,SiteContext pSiteContext ){		 
		CreatePendingShipmentReqBuilder createPendShipqBuilder = new CreatePendingShipmentReqBuilder();
		CreateOpenShipmentRequest csR = this.buildRequestPendingShipment(pMap, pBBBCatalogTools, pSiteContext, createPendShipqBuilder);
		CreatePendingShipmentReqVo cpShipReqVo = new CreatePendingShipmentReqVo();
		cpShipReqVo.setServiceName(CREATEPENDINGSHIPMENT);
		cpShipReqVo.setCreateOpenShipmentRequest(csR);
		Map<String , Object> finalResult = new HashedMap();
		try {
			CreatePendingShipmentResVo cpSRVO = this.createPendingService(cpShipReqVo);
			com.fedex.ws.openship.v7.CreateOpenShipmentReply cosReply = cpSRVO.getCreateOpenShipmentReply();
			if (createPendShipqBuilder.isResponseOk(cosReply.getHighestSeverity()))
			{
				com.fedex.ws.openship.v7.CompletedShipmentDetail cms =  cosReply.getCompletedShipmentDetail();
				com.fedex.ws.openship.v7.PendingShipmentAccessorDetail[] accessorDetail=cms.getAccessDetail().getAccessorDetailsArray();				
				for (int i = 0; i < accessorDetail.length;i++){
					finalResult.put(URL, accessorDetail[i].getEmailLabelUrl());
				}
				return finalResult;
			}else{

				com.fedex.ws.openship.v7.Notification[] nfs = cosReply.getNotificationsArray();
				if(nfs!=null){				
					finalResult.put(FAILURE, nfs);	
					return finalResult;
				}
			
			}
		} catch (BBBBusinessException e) {
			 logError(e.getMessage(),e);
		} catch (BBBSystemException e) {
			 logError(e.getMessage(),e);
		}
		
		return null;
		
	}

	/**
	 * @param pMap
	 * @param pBBBCatalogTools
	 * @param pSiteContext
	 * @param createPendShipqBuilder
	 * @return
	 */
	protected CreateOpenShipmentRequest buildRequestPendingShipment(
			Map<String, String> pMap, BBBCatalogTools pBBBCatalogTools,
			SiteContext pSiteContext,
			CreatePendingShipmentReqBuilder createPendShipqBuilder) {
		return createPendShipqBuilder.buildCreatePendingShipmentRequest(pMap , pBBBCatalogTools , pSiteContext);
	}

	/**
	 * @param cpShipReqVo
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected CreatePendingShipmentResVo createPendingService(
			CreatePendingShipmentReqVo cpShipReqVo)
			throws BBBBusinessException, BBBSystemException {
		return (CreatePendingShipmentResVo) ServiceHandlerUtil.invoke(cpShipReqVo);
	}

	
}
