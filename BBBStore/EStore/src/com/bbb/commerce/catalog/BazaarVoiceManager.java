package com.bbb.commerce.catalog;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;

import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 *BazaarVoiceSchedulerJob will interact with this class.It will update the product review rating in the repository. 
 * @author ajosh8
 */
public class BazaarVoiceManager extends BBBGenericService {

	private Map<String, String> failedProductIdList = new HashMap<String, String>();
	private MutableRepository mBazaarVoiceRepository;
	private MutableRepository scheduledRepository;
	private SMTPEmailSender emailSender;
	private String[] recipients;
	private String sender;
	private String subject;
	
	private boolean sendStatusReport;

	/**
	 * @return Scheduled Repository
	 */
	public final MutableRepository getScheduledRepository() {
		return this.scheduledRepository;
	}

	/**
	 * @param scheduledRepository Scheduled Repository
	 */
	public final void setScheduledRepository(final MutableRepository scheduledRepository) {
		this.scheduledRepository = scheduledRepository;
	}

	/**
	 * @return the bazaarVoiceRepository
	 */
	public MutableRepository getBazaarVoiceRepository() {
		return this.mBazaarVoiceRepository;
	}

	/**
	 * @param pBazaarVoiceRepository the bazaarVoiceRepository to set
	 */
	public final void setBazaarVoiceRepository(final MutableRepository pBazaarVoiceRepository) {
		this.mBazaarVoiceRepository = pBazaarVoiceRepository;
	}


	/**
	 * @return the failedProductIdList
	 */
	public final Map<String, String> getFailedProductIdList() {
		return this.failedProductIdList;
	}

	/**
	 * @param failedProductIdList the failedProductIdList to set
	 */
	public final void setFailedProductIdList(final Map<String, String> failedProductIdList) {
		this.failedProductIdList = failedProductIdList;
	}

	/**
	 * @return the sendStatusReport
	 */
	public final boolean isSendStatusReport() {
		return this.sendStatusReport;
	}

	/**
	 * @param sendStatusReport the sendStatusReport to set
	 */
	public final void setSendStatusReport(final boolean sendStatusReport) {
		this.sendStatusReport = sendStatusReport;
	}

	/**
	 * @return the emailSender
	 */
	public final SMTPEmailSender getEmailSender() {
		return this.emailSender;
	}

	/**
	 * @param emailSender the emailSender to set
	 */
	public final void setEmailSender(final SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * @return the sender
	 */
	public final String getSender() {
		return this.sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public final void setSender(final String sender) {
		this.sender = sender;
	}

	/**
	 * @return the subject
	 */
	public final String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public final void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return the recipients
	 */
	public final String[] getRecipients() {
		return this.recipients;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public final void setRecipients(final String[] recipients) {
		this.recipients = recipients;
	}

	/**
	 * It will update the product review rating.BazaarVoiceVo we would get from BazaarVoiceSchedulerJob.
	 * If product is already present in the repository , it will update the review rating.
	 * If product is not in the repository it will insert its review rating in the repository.
	 * @param pBazaarVoiceVO Bazaar Voice 
	 * @throws BBBSystemException Exception
	 * @throws BBBBusinessException Exception
	 */
	public final void createUpdateProductBV(final BazaarVoiceVO pBazaarVoiceVO) 
	        throws BBBSystemException, BBBBusinessException {

		logDebug("BazaarVoiceManager:createUpdateProductBV");

		MutableRepositoryItem bazaarVoiceItem = null;
		String productId;
		final List<BazaarVoiceProductVO> lstProductBazaarVoice = pBazaarVoiceVO.getBazaarVoiceProduct();

		if (!BBBUtility.isListEmpty(lstProductBazaarVoice)) {
			for (int i = 0; i < lstProductBazaarVoice.size(); i++) {

				final BazaarVoiceProductVO bazaarVoiceProductVO = lstProductBazaarVoice.get(i);
				productId = bazaarVoiceProductVO.getId();
				final String siteId = bazaarVoiceProductVO.getSiteId();
				boolean createNew = false;
				try {
					
					bazaarVoiceItem = getBazaarVoiceRepository().getItemForUpdate(productId + ":"
					        + siteId, BBBCatalogConstants.BAZAAR_VOICE);
					//Checking that if product id and site id are not present in the repository it will create a
					// new record with that site id and product id,if it is there with same site id and product id
					// then update it with latest review rating.
					
					if (bazaarVoiceItem == null) {
						logDebug("No record exist with this product id.New Repository Item for product id "
						            + productId + " created ");
						bazaarVoiceItem = getBazaarVoiceRepository().createItem(productId + ":"
						            + siteId, BBBCatalogConstants.BAZAAR_VOICE);
						createNew = true;
					}
					if (null != bazaarVoiceItem) {
						bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING,
						        Float.valueOf(bazaarVoiceProductVO.getAverageOverallRating()));
						bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.EXTERNAL_ID, 
						        bazaarVoiceProductVO.getExternalId());
						bazaarVoiceItem.setPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT, 
						        Integer.valueOf(bazaarVoiceProductVO.getTotalReviewCount()));
					}
					
					if (createNew) {
						getBazaarVoiceRepository().addItem(bazaarVoiceItem);
						logDebug("new Repository Item for product id " + productId + " created");
					} else {
						getBazaarVoiceRepository().updateItem(bazaarVoiceItem);
						logDebug("Existing Repository Item for product id " + productId + " Updated");
						
					}
				} catch (RepositoryException e) {
					
					logError(LogMessageFormatter.formatMessage(null, "BazaarVoiceManager.createUpdateProductBV() "
						        + "| RepositoryException ", "catalog_1070"), e);
					
					this.failedProductIdList.put(productId, "Repository Exception while updating product id "
					        + productId);
					if (this.isSendStatusReport()) {
						logDebug("Sending mail for bazaar voice update");
					
						sendFailedRecordsReport();
					}
				}
			}
		}
		}


	/** This method sends the report for records that failed to update.  
	 */
	private void sendFailedRecordsReport() {
		try {
				logDebug("sendFailedRecordsReport :Sender :" + getSender() + " recipients: "
				        + Arrays.toString(this.getRecipients()) + " subject " + getSubject());
			this.getEmailSender().sendEmailMessage(getSender(), this.getRecipients(), getSubject(), getMessage());
		} catch (EmailException e) {
			
			logDebug("EmailException when sending mail for bazaar voice");
			
			
			logError(LogMessageFormatter.formatMessage(null, "BazaarVoiceManager.sendFailedRecordsReport() "
				        + "| EmailException Failed to send email message:"
				        + "\n Remember to set /atg/dynamo/service/SMTPEmail.emailHandlerHostName and "
				        + "/atg/dynamo/service/SMTPEmail.emailHandlerPort", "catalog_1071"), e);
			
		}
	}

	/**The method returns the message with list of order and shipping ids that failed to update along with the reason. 
	 * of failure
	 * @return Message
	 */
	private String getMessage() {
		final StringBuffer messageString = new StringBuffer(45);
		if (!BBBUtility.isMapNullOrEmpty(this.getFailedProductIdList())) {
			final Set<String> productIdKey = this.getFailedProductIdList().keySet();
			for (final String productId:productIdKey) {
				messageString.append("Updation of product Id" + productId + " failed because "
				        + this.getFailedProductIdList().get(productId) + "\n");
			}
		}

		logDebug("message body of the mail  " + messageString.toString());

		return messageString.toString();	
	}


	/**The method updates the certona repository with the details of the scheduler run.
	 * 
	 * @param schedulerStartDate Scheduler Start Date
	 * @param fullDataFeed  Full Data Feed
	 * @param isFullDataFeed Full Data Feed 
	 * @param typeOfFeed Type of Feed
	 * @param status Status
	 */
	public void updateScheduledRepository(final Timestamp schedulerStartDate, final String fullDataFeed, 
	        final String typeOfFeed, final boolean status) {
		logDebug("BazaarVoiceManager Method : updateScheduledRepository");
		logDebug("scheduler Start Date :" + schedulerStartDate + " full feed?: " + fullDataFeed + " typeOfFeed: "
					+ typeOfFeed + " status:" + status);
		final Date schedulerEndDate = new Date();
		try {
			final MutableRepositoryItem certonaItem = this.getScheduledRepository().createItem(
			        BBBCertonaConstants.FEED);

			certonaItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, schedulerEndDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate);
			certonaItem.setPropertyValue(BBBCertonaConstants.STATUS, Boolean.valueOf(status));
			certonaItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, typeOfFeed);

			certonaItem.setPropertyValue(BBBCertonaConstants.MODE, fullDataFeed);
			this.getScheduledRepository().addItem(certonaItem);

		} catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null,
						"BazaarVoiceManager.updateScheduledRepository() | RepositoryException ", "catalog_1072"), e);
			
		}
	}
}
