package com.bbb.internationalshipping.fulfillment.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderFeed;
import com.bbb.internationalshipping.fulfillment.poservice.BBBFileUtils;
import com.bbb.internationalshipping.fulfillment.poservice.IntlPODecryptService;
import com.bbb.internationalshipping.fulfillment.poservice.IntlPOFileUnMarshaller;
import com.bbb.internationalshipping.manager.BBBInternationalSubmitOrderManager;
import com.bbb.internationalshipping.manager.InternationalOrderConfirmationManager;
import com.bbb.internationalshipping.utils.InternationalOrderXmlRepoTools;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfResponse;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBConfigRepoUtils;


/**
 * The Class InternationalPurchaseOrderProcessor.
 * The process does the following tasks
 * 1)Gets all the PO files available for processing
 * 2)Decrypt the Po file on the fly
 * 3)Process the Po file and update the order
 * 4)Send order confirmation to Borderfree
 * 
 * 
 */
public class InternationalPurchaseOrderProcessor extends BBBGenericService {

	/** The international decryption service. */
	private IntlPODecryptService intlPODecryptService;

	/** The po file unmarshaller. */
	private IntlPOFileUnMarshaller poFileUnmarshaller;

	/**
	 * 
	 */
	private Map<String,String> siteFeedPOConfiguration = new HashMap<String,String>();
	/**
	 * 
	 */
	private Map<String, String> internationalPOFileLocation = new HashMap<String, String>();
	/**
	 * 
	 */
	private Map<String, String> internationalPOArchiveFolder = new HashMap<String, String>();
	/**
	 * 
	 */
	private Map<String, String> internationalPOErrorFolder = new HashMap<String, String>();

	/** The decryption disabled. */
	private boolean decryptionDisabled;
	/** The international po error folder. */

	/** The test file path. */
	private String testFilePath ;

	/** The retry count. */
	private int mRetryCount;

	private InternationalOrderXmlRepoTools intlRepoTools;


	/** The print run time decrypted po. */
	private boolean printRunTimeDecryptedPO;

	/**
	 * 
	 */
	private InternationalOrderConfirmationManager orderConfirmationManager;
	/**
	 * 
	 */
	private BBBInternationalSubmitOrderManager submitOrderManager;

	/**
	 * @return the orderConfirmationManager
	 */
	public final InternationalOrderConfirmationManager getOrderConfirmationManager() {
		return orderConfirmationManager;
	}

	/**
	 * @param orderConfirmationManager the orderConfirmationManager to set
	 */
	public final void setOrderConfirmationManager(final 
			InternationalOrderConfirmationManager orderConfirmationManager) {
		this.orderConfirmationManager = orderConfirmationManager;
	}


	public InternationalOrderXmlRepoTools getIntlRepoTools() {
		return intlRepoTools;
	}



	public void setIntlRepoTools(InternationalOrderXmlRepoTools intlRepoTools) {
		this.intlRepoTools = intlRepoTools;
	}


	/**
	 * @return the submitOrderManager
	 */
	public final BBBInternationalSubmitOrderManager getSubmitOrderManager() {
		return submitOrderManager;
	}

	/**
	 * @param submitOrderManager the submitOrderManager to set
	 */
	public final void setSubmitOrderManager(final 
			BBBInternationalSubmitOrderManager submitOrderManager) {
		this.submitOrderManager = submitOrderManager;
	}

	/**
	 * Gets the po file unmarshaller.
	 *
	 * @return the po file unmarshaller
	 */
	public IntlPOFileUnMarshaller getPoFileUnmarshaller() {
		return poFileUnmarshaller;
	}

	/**
	 * Sets the po file unmarshaller.
	 *
	 * @param poFileUnmarshaller the new po file unmarshaller
	 */
	public void setPoFileUnmarshaller(final IntlPOFileUnMarshaller poFileUnmarshaller) {
		this.poFileUnmarshaller = poFileUnmarshaller;
	}

	/**
	 * Gets the test file path.
	 * 
	 * @return the testFilePath
	 */
	public String getTestFilePath() {
		return testFilePath;
	}

	/**
	 * Sets the test file path.
	 * 
	 * @param testFilePath
	 *            the testFilePath to set
	 */
	public void setTestFilePath(final String testFilePath) {
		this.testFilePath = testFilePath;
	}

	/**
	 * @return the decryptionDisabled
	 */
	public boolean isDecryptionDisabled() {
		return decryptionDisabled;
	}

	/**
	 * @return the printRunTimeDecryptedPO
	 */
	public boolean isPrintRunTimeDecryptedPO() {
		return printRunTimeDecryptedPO;
	}

	/**
	 * @param printRunTimeDecryptedPO the printRunTimeDecryptedPO to set
	 */
	public void setPrintRunTimeDecryptedPO(boolean printRunTimeDecryptedPO) {
		this.printRunTimeDecryptedPO = printRunTimeDecryptedPO;
	}


	/**
	 * Gets the retry count.
	 *
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return mRetryCount;
	}

	/**
	 * Sets the retry count.
	 *
	 * @param pRetryCount the retryCount to set
	 */
	public void setRetryCount(final int pRetryCount) {
		mRetryCount = pRetryCount;
	}

	/**
	 * Gets the intl po decrypt service.
	 *
	 * @return the intl po decrypt service
	 */
	public IntlPODecryptService getIntlPODecryptService() {
		return intlPODecryptService;
	}


	/**
	 * Sets the intl po decrypt service.
	 *
	 * @param intlPODecryptService the new intl po decrypt service
	 */
	public void setIntlPODecryptService(final IntlPODecryptService intlPODecryptService) {
		this.intlPODecryptService = intlPODecryptService;
	}

	/**
	 * @return the siteFeedPOConfiguration
	 */
	public final Map<String, String> getSiteFeedPOConfiguration() {
		return siteFeedPOConfiguration;
	}

	/**
	 * @param siteFeedPOConfiguration the siteFeedPOConfiguration to set
	 */
	public final void setSiteFeedPOConfiguration(
			Map<String, String> siteFeedPOConfiguration) {
		this.siteFeedPOConfiguration = siteFeedPOConfiguration;
	}

	/**
	 * @return the internationalPOFileLocation
	 */
	public final Map<String, String> getInternationalPOFileLocation() {
		return internationalPOFileLocation;
	}

	/**
	 * @param internationalPOFileLocation the internationalPOFileLocation to set
	 */
	public final void setInternationalPOFileLocation(
			Map<String, String> internationalPOFileLocation) {
		this.internationalPOFileLocation = internationalPOFileLocation;
	}

	/**
	 * @return the internationalPOArchiveFolder
	 */
	public final Map<String, String> getInternationalPOArchiveFolder() {
		return internationalPOArchiveFolder;
	}

	/**
	 * @param internationalPOArchiveFolder the internationalPOArchiveFolder to set
	 */
	public final void setInternationalPOArchiveFolder(
			Map<String, String> internationalPOArchiveFolder) {
		this.internationalPOArchiveFolder = internationalPOArchiveFolder;
	}

	/**
	 * @return the internationalPOErrorFolder
	 */
	public final Map<String, String> getInternationalPOErrorFolder() {
		return internationalPOErrorFolder;
	}

	/**
	 * @param internationalPOErrorFolder the internationalPOErrorFolder to set
	 */
	public final void setInternationalPOErrorFolder(
			Map<String, String> internationalPOErrorFolder) {
		this.internationalPOErrorFolder = internationalPOErrorFolder;
	}

	/**
	 * Process the Purchase Order Files.
	 * Step 1) Get all the PO files from the requested location.
	 * Step 2) Decrypt the file one by one.
	 * Step 3) Send the decrypted file to order update service for processing.
	 * Step 4) Run Retry logic.
	 *
	 * @param pSite the site
	 */
	public void processPOFiles() {

		logInfo("InternationalPurchaseOrderProcessor.processPOFiles() method starts");

		List<File> allFile = new ArrayList<File>();
		String filePath = null;
		String archivePath = null;
		String errorPath = null;
		Set<String> siteCodes = getSiteFeedPOConfiguration().keySet();

		for(String siteCode:siteCodes ){

			if (!Boolean.parseBoolean(getSiteFeedPOConfiguration()
					.get(siteCode))) {
				continue;
			}

			filePath = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOFileLocation().get(siteCode));
			archivePath = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOArchiveFolder().get(siteCode));
			errorPath = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOErrorFolder().get(siteCode));
			
			logInfo("PO File Path : " + filePath + "PO Archive File Path : " + archivePath + "PO Error File Path : " + errorPath);

			allFile = BBBFileUtils.getFileNamesFromDirectory(filePath);
			if (allFile == null || allFile.isEmpty()) {
				logInfo("No files found for processing ->");
				continue;
			}
			this.doAllProcessingTasks(allFile, archivePath, errorPath,false,siteCode);

			logInfo("InternationalPurchaseOrderProcessor.processPOFiles() method ends");
		}
	}




	/**
	 * Process the Purchase Order Files.
	 * Step 1) Get all the PO files from the requested location.
	 * Step 2) Decrypt the file one by one.
	 * Step 3) Send the decrypted file to order update service for processing.
	 * Step 4) Run Retry logic.
	 *
	 * @param pSite the site
	 */
	public void retryPOFileProcessing() {

		logInfo("InternationalPurchaseOrderProcessor.processPOFiles() method starts");

		List<String> fileNames = null;

		String archivePath = null;
		String errorPath = null;
		Set<String> siteCodes = getSiteFeedPOConfiguration().keySet();

		for(String siteCode:siteCodes ){

			if (!Boolean.parseBoolean(getSiteFeedPOConfiguration()
					.get(siteCode))) {
				continue;
			}

			
			archivePath = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOArchiveFolder().get(siteCode));
			errorPath = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOErrorFolder().get(siteCode));
		
			logInfo("PO File Path : " + errorPath + "PO Archive File Path : " + archivePath + "PO Error File Path : " + errorPath);
			List<File> allRetryFiles=new ArrayList<File>();
			fileNames =getIntlRepoTools().getPoFilesForRetry(getRetryCount(),siteCode);
			if (fileNames == null || fileNames.isEmpty()) {
				logInfo("No files found for processing retry files ->");
				continue;
			}
			else{
				File purchaseOrderFile = null;
				for (final String purchaseOrderFileName : fileNames) {
					try{
						if (StringUtils.isBlank(purchaseOrderFileName)) {
							final String errStr = "The PO file name retrieved is null"+purchaseOrderFileName;
							logError(errStr);
							throw new BBBBusinessException(errStr);
						}
						purchaseOrderFile = BBBFileUtils.getFile(errorPath, purchaseOrderFileName);
						logInfo("purchaseOrderFile --------------------->"+purchaseOrderFile);
						if (purchaseOrderFile == null) {
							final String errStr = "The PO file "+purchaseOrderFileName +" retrieved is null";
							logError(errStr);
						
						}
						allRetryFiles.add(purchaseOrderFile);
					} catch (BBBBusinessException e) {
						logError("BBBBusinessException: processPOFiles ------> "+purchaseOrderFileName+" ---->"+ e.getMessage());
					} 
				}

			}
			if(allRetryFiles!=null && !allRetryFiles.isEmpty()){
				this.doAllProcessingTasks(allRetryFiles, archivePath, errorPath,true,siteCode);
			}
		}

		logInfo("InternationalPurchaseOrderProcessor.processPOFiles() method ends");
	}

	private void doAllProcessingTasks(List<File> allFile,String archivePath,String errorPath,boolean isRetry,String siteId){
		//BBBInternationalOrderPOFileVO orderFileVo = new BBBInternationalOrderPOFileVO();
				OrderFeed orderfeed=null;
				BBBInternationalShippingOrderConfResponse orderConfResponse = new BBBInternationalShippingOrderConfResponse();

				for (final File purchaseOrderFile : allFile) {
					vlogDebug("PO file Processing start --------------------->"+purchaseOrderFile);
					boolean isFilePOSuccess =false;
					String runtimePOFileDecryptionXML =null;
					try {

						vlogDebug("purchaseOrderFile --------------------->"+purchaseOrderFile);

						vlogDebug("calling  runtimePOFileDecryption for--------------------->"+purchaseOrderFile);
						runtimePOFileDecryptionXML = intlPODecryptService.runtimePOFileDecryption(purchaseOrderFile);
						if (isPrintRunTimeDecryptedPO()) {
							logInfo("Runtime PO File Decryption XML -> "
									+ runtimePOFileDecryptionXML);
						}
						vlogDebug("calling  unmarshalPOFile for--------------------->"+purchaseOrderFile);
						if(!StringUtils.isEmpty(runtimePOFileDecryptionXML)){
							orderfeed = poFileUnmarshaller.unmarshalPOFile(runtimePOFileDecryptionXML,purchaseOrderFile,siteId);
							//isRetry=orderFileVo.isRetry();
						}
						
						if (null != orderfeed){// && !orderFileVo.isRetry()) {
							String bbbOrderId=poFileUnmarshaller.getBBBOrderId(orderfeed,orderfeed.getOrder().get(0).getOrderId().getMerchantOrderId());
							vlogDebug("Order ID --------------------->"+bbbOrderId);
							vlogDebug("calling  updateInternationalOrder for--------------------->"+purchaseOrderFile);
							this.getSubmitOrderManager().updateInternationalOrder(orderfeed);

							String fraudState =orderfeed.getOrder().get(0).getFraudState() == null ? null :  orderfeed.getOrder().get(0).getFraudState();
							vlogDebug("PO file State  -----------"+fraudState+"  for PO file---------->"+purchaseOrderFile);
							if (StringUtils.isEmpty(fraudState) || fraudState.equalsIgnoreCase(BBBInternationalShippingConstants.GREEN_PO_FILE)) {
								vlogDebug("calling  orderConfirmation for --------------------->"+purchaseOrderFile);
								orderConfResponse = this.orderConfirmationManager.orderConfirmation(orderfeed.getOrder().get(0).getOrderId().getMerchantOrderId(), orderfeed.getOrder().get(0).getOrderId().getE4XOrderId());
								logInfo("Inside class InternationalPurchaseOrderProcessor : orderConfResponse Confirmed state : " + orderConfResponse.isConfirmed());
							}
							isFilePOSuccess=true;
							this.getIntlRepoTools().setPOSucessFlag(purchaseOrderFile.getName());
						}

					} catch (BBBBusinessException e) {
						logError("BBBBusinessException: processPOFiles ------> "+purchaseOrderFile+" ---->"+ e.getMessage());
						this.getIntlRepoTools().retryProcessing(purchaseOrderFile.getName(), e.getMessage(), getRetryCount(),siteId);

					} catch (BBBSystemException e) {
						logError("BBBSystemException: processPOFiles ------> "+purchaseOrderFile+" ---->"+e.getMessage());
						this.getIntlRepoTools().retryProcessing(purchaseOrderFile.getName(), e.getMessage(), getRetryCount(),siteId);
					} catch (Exception e) {
						logError("Exception: processPOFiles ------> "+purchaseOrderFile+" ---->"+e.getMessage());
						this.logError(
								LogMessageFormatter.formatMessage(null, "InternationalPurchaseOrderProcessor | Exception "), e);
						this.getIntlRepoTools().retryProcessing(purchaseOrderFile.getName(), e.getMessage(), getRetryCount(),siteId);
					} 
					finally
					{
						BBBFileUtils.delay(1000);
						if(isFilePOSuccess)
						{
							if (BBBFileUtils.moveFile(archivePath, purchaseOrderFile)){
								vlogInfo("File --->"+purchaseOrderFile+" moved successfully "+archivePath);
							}
							else{
								logError("Error while moving File --->" + purchaseOrderFile + " to folder =" + archivePath);
							}
						}
						else if(!isRetry)
						{
							if (BBBFileUtils.moveFile(errorPath, purchaseOrderFile)){
								vlogInfo("File --->"+purchaseOrderFile+" moved successfully "+errorPath);
							}
							else{
								logError("Error while moving File --->" + purchaseOrderFile + " to folder =" + errorPath);
							}
				}
			}
		}
	}
}
