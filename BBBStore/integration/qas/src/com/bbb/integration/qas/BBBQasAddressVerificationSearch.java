package com.bbb.integration.qas;

import atg.qas.QasAddressVerificationSearch;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class BBBQasAddressVerificationSearch extends QasAddressVerificationSearch {

	protected String onDemandUsername;
	protected String onDemandPassword;
	
	@Override
	public String getOnDemandUsername() {
		if(BBBUtility.isEmpty(this.onDemandUsername))
		{
			this.onDemandUsername = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.ON_DEMAND_UNAME);
		}
		return this.onDemandUsername;
	}
	
	@Override
	public String getOnDemandPassword() {
				if(BBBUtility.isEmpty(this.onDemandPassword))
		{
			this.onDemandPassword = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.ON_DEMAND_PSWD);
		}
		return this.onDemandPassword;
	}

	/*public void service(DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse)
			throws IOException, ServletException {
		try {
			String s = URLDecoder.decode(
					dynamohttpservletrequest.getParameter("country"), "UTF-8");
			String s1 = URLDecoder.decode(
					dynamohttpservletrequest.getParameter("searchstring"),
					"UTF-8");
			String s2 = URLDecoder
					.decode(dynamohttpservletrequest.getParameter("addlayout"),
							"UTF-8");
			BBBQasAddressVerification qasaddressverification = new BBBQasAddressVerification();
			qasaddressverification.setOnDemandUsername(getOnDemandUsername());
			qasaddressverification.setOnDemandPassword(getOnDemandPassword());
			qasaddressverification.setOnDemandUrl(getOnDemandUrl());
			qasaddressverification.setOnDemandLayout(s2);
			qasaddressverification.setCountry(s);
			qasaddressverification.setSearchString(s1);
			qasaddressverification.doSearch(dynamohttpservletrequest,
					dynamohttpservletresponse);
			dynamohttpservletrequest.setParameter("addresslines",
					qasaddressverification.getAddressLines());
			dynamohttpservletrequest.setParameter("verificationlevel",
					qasaddressverification.getVerificationLevel());
			dynamohttpservletrequest.setParameter("dpvstatus",
					qasaddressverification.getDpvStatus());
			dynamohttpservletrequest.setParameter("picklistitems",
					qasaddressverification.getPicklistItems());
			dynamohttpservletrequest.setParameter("fullmoniker",
					qasaddressverification.getFullMoniker());
		} catch (Exception exception) {
			dynamohttpservletrequest.setParameter("qaserror",
					exception.getMessage());
		}
		dynamohttpservletrequest.serviceLocalParameter("output",
				dynamohttpservletrequest, dynamohttpservletresponse);
	}*/
}
