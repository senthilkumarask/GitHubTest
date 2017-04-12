package com.bbb.common.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/*
 *This class will check  
 */
public class CheckForSDDDroplet extends BBBDynamoServlet {

	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		logDebug("CheckForSDDDroplet.service method begins");

		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		boolean sddEligiblilty = false;
		String inputZipNoHyphen = BBBCoreConstants.BLANK;
		String inputZip = pRequest.getParameter(BBBCoreConstants.INPUT_ZIP);

		// Check that we get input Zip from request
		if (!StringUtils.isBlank(inputZip)) {
			// remove hyphen from zipcode
			if (inputZip.contains(BBBCoreConstants.HYPHEN)) {
				inputZipNoHyphen = BBBUtility.hyphenExcludedZip(inputZip);
			} else {
				inputZipNoHyphen = inputZip;
			}
			RegionVO regionVo = getSameDayDeliveryManager()
					.populateDataInVO(sessionBean, pRequest, inputZipNoHyphen,
							BBBCoreConstants.RETURN_FALSE,
							BBBCoreConstants.RETURN_TRUE,
							BBBCoreConstants.RETURN_FALSE);

			if (null != regionVo) {
				sddEligiblilty = true;
				pRequest.setParameter(BBBCatalogConstants.REGIONVO_DISP_CUT_OFF, regionVo.getDisplayCutoffTime());
				pRequest.setParameter(BBBCatalogConstants.REGIONVO_DISP_GET_BY, regionVo.getDisplayGetByTime());
			}
		}

		pRequest.setParameter(BBBCoreConstants.SDD_ENABLED, sddEligiblilty);

		pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

		logDebug("CheckForSDDDroplet.service method ends");

	}

	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	public void setSameDayDeliveryManager(BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}

}
