package com.bbb.integration.qas;

import atg.qas.QasAddressVerification;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class BBBQasAddressVerification extends QasAddressVerification {

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
	/* Method is called while searching address
	 * @see atg.qas.QasAddressVerification#doSearch(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	
	/*public void doSearch(DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse)
			throws QasException {
		com.qas.newmedia.internet.ondemand.product.proweb.QuickAddress quickaddress = setUpQas(dynamohttpservletrequest);
		SearchResult searchresult = quickaddress.search(getCountry(),
				getSearchString(), PromptSet.DEFAULT, getOnDemandLayout());
		this.verificationLevel = searchresult.getVerifyLevel();
		setVerificationLevel(this.verificationLevel);
		if ((this.verificationLevel == "Verified")
				|| (this.verificationLevel == "InteractionRequired")) {
			setDpvStatus(searchresult.getAddress().getDPVStatus().toString());
			ArrayList arraylist = new ArrayList();
			Object[] aobj = searchresult.getAddress().getAddressLines();
			int i = aobj.length;
			for (int j = 0; j < i; ++j) {
				AddressLine addressline = (AddressLine) aobj[j];
				arraylist.add(addressline.getLine());
			}

			aobj = new String[arraylist.size()];
			aobj = (String[]) arraylist.toArray(aobj);
			setAddressLines((String[]) aobj);
		} else {
			setFullMoniker(searchresult.getPicklist().getMoniker());
			ArrayList arraylist1 = new ArrayList();
			PicklistItem[] apicklistitem = searchresult.getPicklist()
					.getItems();
			int k = apicklistitem.length;
			for (int l = 0; l < k; ++l) {
				PicklistItem picklistitem = apicklistitem[l];
				HashMap hashmap = new HashMap();
				hashmap.put("partialtext", picklistitem.getPartialAddress());
				hashmap.put("addresstext", picklistitem.getText());
				hashmap.put("postcode", picklistitem.getPostcode());
				hashmap.put("moniker", picklistitem.getMoniker());
				if (picklistitem.isFullAddress())
					hashmap.put("fulladdress", "true");
				else
					hashmap.put("fulladdress", "false");
				arraylist1.add(hashmap);
			}

			setPicklistItems(arraylist1);
		}
	}*/
	/* Method is used to format address while qas call
	 * atg.qas.QasAddressVerification#formatAddress(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse, java.lang.String)
	 */

	/*public void formatAddress(
			DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse, String Moniker)
			throws QasException {
		QuickAddress quickaddress = setUpQas(dynamohttpservletrequest);
		FormattedAddress formattedaddress = quickaddress.getFormattedAddress(
				getOnDemandLayout(), Moniker);
		setVerificationLevel("Verified");
		setDpvStatus(formattedaddress.getDPVStatus().toString());
		ArrayList arraylist = new ArrayList();
		Object[] aobj = formattedaddress.getAddressLines();
		int i = aobj.length;
		for (int j = 0; j < i; ++j) {
			AddressLine addressline = (AddressLine) aobj[j];
			arraylist.add(addressline.getLine());
		}

		aobj = new String[arraylist.size()];
		aobj = (String[]) arraylist.toArray(aobj);
		setAddressLines((String[]) aobj);
	}
	*/
	/* 
	 * This method is used to make Lookup thin client call
	 * Response parameters like Enrolled, ACSUrl, Payload, etc of this call are set in BBBVerifiedByVisaVO and this vo is set in session bean.
	 * @param atg.servlet.DynamoHttpServletRequest
	 * @param atg.servlet.DynamoHttpServletResponse
	 * @param String
	 * @param String
	 * 
	 */

	/*public void doRefine(DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse, String Moniker,
			String RefinementText) throws QasException {
		QuickAddress quickaddress = setUpQas(dynamohttpservletrequest);
		Picklist picklist = quickaddress.refine(Moniker, RefinementText);
		if ((picklist.getItems().length == 1)
				&& (picklist.getItems()[0].isFullAddress())) {
			FormattedAddress formattedaddress = quickaddress
					.getFormattedAddress(getOnDemandLayout(),
							picklist.getItems()[0].getMoniker());
			setVerificationLevel("Verified");
			setDpvStatus(formattedaddress.getDPVStatus().toString());
			ArrayList arraylist1 = new ArrayList();
			Object[] aobj = formattedaddress.getAddressLines();
			int i = aobj.length;
			for (int k = 0; k < i; ++k) {
				AddressLine addressline = (AddressLine) aobj[k];
				arraylist1.add(addressline.getLine());
			}

			aobj = new String[arraylist1.size()];
			aobj = (String[]) arraylist1.toArray(aobj);
			setAddressLines((String[]) aobj);
		} else {
			setVerificationLevel("PremisesPartial");
			setFullMoniker(picklist.getMoniker());
			ArrayList arraylist = new ArrayList();
			PicklistItem[] apicklistitem = picklist.getItems();
			int j = apicklistitem.length;
			for (int l = 0; l < j; ++l) {
				PicklistItem picklistitem = apicklistitem[l];
				HashMap hashmap = new HashMap();
				hashmap.put("partialtext", picklistitem.getPartialAddress());
				hashmap.put("addresstext", picklistitem.getText());
				hashmap.put("postcode", picklistitem.getPostcode());
				hashmap.put("moniker", picklistitem.getMoniker());
				if (picklistitem.isFullAddress())
					hashmap.put("fulladdress", "true");
				else
					hashmap.put("fulladdress", "false");
				arraylist.add(hashmap);
			}

			setPicklistItems(arraylist);
		}
	}*/
	/**Method is called to set up qas call
	 * @param dynamohttpservletrequest
	 * @return
	 * @throws QasException
	 * @throws IOException
	 */
	/*private QuickAddress setUpQas(
			DynamoHttpServletRequest dynamohttpservletrequest)
			throws QasException {
		QAAuthentication qaauthentication = new QAAuthentication();
		qaauthentication.setUsername(getOnDemandUsername());
		qaauthentication.setPassword(getOnDemandPassword());
		dynamohttpservletrequest.setAttribute("authentication",
				qaauthentication);
		int QasTimeOut=0;
		QasTimeOut =Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.QAS_TIME_OUT));
				 
		QuickAddress quickaddress = new QuickAddress(getOnDemandUrl(),
				dynamohttpservletrequest,QasTimeOut);
		quickaddress.setEngineType(QuickAddress.VERIFICATION);
		quickaddress.setFlatten(true);
		return quickaddress;
	}*/


}
