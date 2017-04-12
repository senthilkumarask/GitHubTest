package com.bbb.tbs.gs.service;

import atg.commerce.inventory.InventoryManager;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import com.bbb.common.BBBGenericService;
import com.bbb.tbs.gs.service.vo.StatusVO;

/**
 * Created by acer on 9/11/2014.
 */
public class BBBTBSGSService extends BBBGenericService {

	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	private Repository mGsRepository;

	private InventoryManager mInventoryManager;

	public StatusVO addGSOrderInfo(String gsorderID, String userToken,
			String storeID, String terminalId, String reLastNm, String reFirstNm,
			String reEmail, String rePhone, String skus, String quantities,
			String registryID) {
		logDebug("BBBTBSGSService :: addGSOrderInfo () :: START");
		StatusVO status = new StatusVO();
		boolean error = false;
		String displayMessage = null;
		Integer errorID = null;
		String errorMessage = null;
		
		boolean gsorderIDMissing = false;
		boolean skusMissing = false;
		boolean quantitiesMissing = false;

		try {
			vlogDebug("gsorderID :: "+gsorderID);
			if(StringUtils.isBlank(gsorderID)){
				gsorderIDMissing = true;
			}
			vlogDebug("skus :: "+skus);
			if(StringUtils.isBlank(skus)){
				skusMissing = true;
			}
			vlogDebug("quantities :: "+quantities);
			if(StringUtils.isBlank(quantities)){
				quantitiesMissing = true;
			}
			
			if(gsorderIDMissing || skusMissing || quantitiesMissing){
				logDebug("Entered into missing values :: ");
				StringBuffer sb = new StringBuffer();
				if(gsorderIDMissing){
					if(skusMissing){
						sb.append("Missing required parameters : ");
						if(quantitiesMissing){
							sb.append("arg1, arg9 and arg10.");
						}else{
							sb.append("arg1 and arg9.");
						}
					} else if(quantitiesMissing){
						sb.append("Missing required parameters : ");
						sb.append("arg1 and arg10.");
					} else {
						sb.append("Missing required parameter : ");
						sb.append("arg1.");
					}
				} else if(skusMissing){
					if(quantitiesMissing){
						sb.append("Missing required parameters : ");
						sb.append("arg9 and arg10.");
					}else{
						sb.append("Missing required parameter : ");
						sb.append("arg9.");
					}
				} else if(quantitiesMissing){
					sb.append("Missing required parameter : ");
					sb.append("arg10.");
				}
				vlogError("DisplayMessage :: "+sb.toString());
				status.setErrorExists(true);
				status.setDisplayMessage(sb.toString());
				status.setErrorId(130);
				status.setErrorMessage("Missing Required Params");
				logDebug("Exiting from missing values condition :: ");
				return status;
			}
			status.setOrderNum(gsorderID);
			vlogDebug("reEmail :: "+reEmail);
			if (!StringUtils.isBlank(reEmail) && !reEmail.matches(EMAIL_REGEX)) {
				error = true;
				displayMessage = "Email address is invalid";
				errorID = 180;
				errorMessage = "Email address is invalid";
			}

			if (!error) {
				logDebug("BBBTBSGSService :: inside parameters without error ");
				
				String[] skusArray = skus.split(",");
				String[] qtyArray = quantities.split(",");

				if (skusArray.length != qtyArray.length) {
					error = true;
					displayMessage = "SKUs and quantities array sizes are not matching";
					errorID = 130;
					errorMessage = "SKUs and quantities array sizes are not matching";
					vlogError("error displayMessage :: "+displayMessage);

				} else {

					long stockLevel;
					for (String lString : skusArray) {

						stockLevel = getInventoryManager()
								.queryStockLevel(lString.trim());

						if (!(stockLevel > 0 || stockLevel == -1)) {
							error = true;
							displayMessage = "Item(s) out of stock";
							errorID = 214;
							errorMessage = "Item(s) out of stock";
							vlogError("errorMessage :: "+errorMessage);
							break;
						}

					}

				}
			}

			if (!error) {
				logDebug("No errors found");
				MutableRepository lRepository = (MutableRepository) getGsRepository();

				MutableRepositoryItem lOrderInfo = lRepository.createItem(
						gsorderID, "gsOrderInfo");
				
				lOrderInfo.setPropertyValue("userToken", userToken);
				vlogDebug("storeID :: "+storeID);
				if(!StringUtils.isBlank(storeID)){
					lOrderInfo.setPropertyValue("storeID", Integer.parseInt(storeID));
				}
				vlogDebug("terminalId :: "+terminalId);
				if(!StringUtils.isBlank(terminalId)){
					lOrderInfo.setPropertyValue("terminalId", Integer.parseInt(terminalId));
				}
				lOrderInfo.setPropertyValue("reLastNm", reLastNm);
				lOrderInfo.setPropertyValue("reFirstNm", reFirstNm);
				lOrderInfo.setPropertyValue("reEmail", reEmail);
				vlogDebug("rePhone :: "+rePhone);
				lOrderInfo.setPropertyValue("rePhone", rePhone);
				lOrderInfo.setPropertyValue("skus", skus);
				lOrderInfo.setPropertyValue("quantities", quantities);
				lOrderInfo.setPropertyValue("registryID", registryID);

				lRepository.addItem(lOrderInfo);
				error = false;
			}

		} catch (RepositoryException e) {

			error = true;
			displayMessage = e.getMessage();
			errorID = 110;
			errorMessage = "There is an error while adding the Guided Selling Order";
			logError(e);
		} catch (Exception e) {
			error = true;
			displayMessage = e.getMessage();
			errorID = 110;
			errorMessage = "There is an error while adding the Guided Selling Order";
			logError(e);
		}

		status.setErrorExists(error);
		status.setDisplayMessage(displayMessage);
		status.setErrorId(errorID);
		status.setErrorMessage(errorMessage);
		logDebug("BBBTBSGSService :: addGSOrderInfo () :: End");
		return status;
	}

	public Repository getGsRepository() {
		return mGsRepository;
	}

	public void setGsRepository(Repository pGsRepository) {
		mGsRepository = pGsRepository;
	}

	public InventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	public void setInventoryManager(InventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}
}
