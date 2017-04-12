package com.bbb.commerce.common;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * To populate addresses entered for shipping, profile addresses
 * 
 * @author manohar
 * @story UC_checkout_billing
 * @version 1.0
 */
public class BBBPopulateStatesDroplet extends BBBDynamoServlet {

	private BBBCheckoutManager mCheckoutMgr;
	public static final String STATES = "states";
	public static final String NO_SHOW_PAGE = "NoShowUSTerr";
	
	private String countrycode;
	public static final String US_COUNTRY_CODE = "US";
	public static final String CA_COUNTRY_CODE = "CA";
	public static final String CC = "cc";
	
	public BBBCheckoutManager getCheckoutMgr() {
		return mCheckoutMgr;
	}

	public void setCheckoutMgr(BBBCheckoutManager pCheckoutMgr) {
		this.mCheckoutMgr = pCheckoutMgr;
	}


	/**
	 * 
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("Entry BBBPopulateStateDroplet.service");
	
		countrycode=pRequest.getParameter("cc");
		String siteId = pRequest.getParameter("siteId");
		String noShowPage = pRequest.getParameter(NO_SHOW_PAGE);
		List<StateVO> stateList = getCheckoutMgr().getStatesBasedOnCountry(countrycode, siteId, true, noShowPage);
		sortBasedStateName(stateList);
        pRequest.setParameter(STATES, stateList);
        logDebug("States count " + stateList.size());
        pRequest.serviceParameter("output", pRequest, pResponse);
	}
	
	/**
	 * This method sorts List<ShippingMethodPriceVO>
	 * 
	 * @param pListOfStateVO
	 */
	private void sortBasedStateName(List<StateVO> pListOfStateVO) {

		if (pListOfStateVO != null
				&& !pListOfStateVO.isEmpty()) {

			Collections.sort(pListOfStateVO,
					new Comparator<StateVO>() {

						@Override
						public int compare(StateVO obj1, StateVO obj2) {

							int returnValue = 0;

							if (obj1.getStateName().compareTo(
									obj2.getStateName()) > 0) {
								returnValue = 1;
							} else {
								returnValue = -1;
							}

							return returnValue;
						}
					});
		}

	}
	
	/**
	 * Below method will provide List of state VO for Rest Service 
	 * @param countryCode
	 * @return StateVO
	 * @throws BBBSystemException
	 */
	public List<StateVO> getStateVO(String countryCode) throws BBBSystemException{
		logDebug(" BBBPopulateStatesDroplet.getStateVO method starts");
		logDebug(" countryCode = " + countryCode);	
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		pRequest.setParameter(CC,countryCode);
		
		try {
			service(pRequest, pResponse);
			List<StateVO>  stateVO = (List<StateVO>) pRequest.getObjectParameter(STATES);
			return stateVO;
		} catch (ServletException e) {
			 throw new BBBSystemException("err_servlet_exception_state_details", "ServletException in BBBPopulateStatesDroplet Droplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_state_details", "IO Exception in in BBBPopulateStatesDroplet Droplet");
		} finally {
				logDebug(" BBBPopulateStatesDroplet.getStateVO method ends");
		}
		
	}
}
