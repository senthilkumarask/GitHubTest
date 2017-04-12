/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/

package atg.tbsqas;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axis2.AxisFault;

import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.qas.www.web_2010_04.AddressDocument;
import com.qas.www.web_2010_04.AddressLineType;
import com.qas.www.web_2010_04.EngineType;
import com.qas.www.web_2010_04.Fault;
import com.qas.www.web_2010_04.PicklistDocument;
import com.qas.www.web_2010_04.PicklistEntryType;
import com.qas.www.web_2010_04.ProWebStub;
import com.qas.www.web_2010_04.PromptSetType;
import com.qas.www.web_2010_04.QARefineDocument.QARefine;
import com.qas.www.web_2010_04.QASearchDocument;
import com.qas.www.web_2010_04.QASearchDocument.QASearch;
import com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult;

public class QasAddressVerification extends GenericService{

	private static final String SINGLELINE = "Singleline";
	private static EngineType m_EngineType;

	
	protected static String onDemandUrl;
	protected String onDemandUsername;
	protected String onDemandPassword;
	protected String proxyHost;
	protected String proxyPort;
	protected String proxyUser;
	protected String proxyPassword;
	protected String onDemandLayout;
	protected String addressLines[];
	protected ArrayList picklistItems;
	protected String verificationLevel;
	protected String dpvStatus;
	protected String fullMoniker;
	protected String country;
	protected String searchString;
	protected String errmsg;

	public QasAddressVerification() {
		onDemandUrl = "";
		onDemandUsername = "";
		onDemandPassword = "";
		proxyHost = "";
		proxyPort = "";
		proxyUser = "";
		proxyPassword = "";
		onDemandLayout = "";
		addressLines = null;
		verificationLevel = "";
		dpvStatus = "";
		fullMoniker = "";
		country = "";
		searchString = "";
		errmsg = "";
	}

	public static String getOnDemandUrl() {
         return onDemandUrl;
	}
		
	public void setOnDemandUrl(String s) {
	          onDemandUrl = s;
	}

	public String getOnDemandUsername() {
		return onDemandUsername;
	}

	public void setOnDemandUsername(String s) {
		onDemandUsername = s;
	}

	public String getOnDemandPassword() {
		return onDemandPassword;
	}

	public void setOnDemandPassword(String s) {
		onDemandPassword = s;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String s) {
		proxyHost = s;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String s) {
		proxyPort = s;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String s) {
		proxyUser = s;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String s) {
		proxyPassword = s;
	}

	public String getOnDemandLayout() {
		return onDemandLayout;
	}

	public void setOnDemandLayout(String s) {
		onDemandLayout = s;
	}

	public String[] getAddressLines() {
		return addressLines;
	}

	public void setAddressLines(String as[]) {
		addressLines = as;
	}

	public ArrayList getPicklistItems() {
		return picklistItems;
	}

	public void setPicklistItems(ArrayList arraylist) {
		picklistItems = arraylist;
	}

	public String getVerificationLevel() {
		return verificationLevel;
	}

	public void setVerificationLevel(String s) {
		verificationLevel = s;
	}

	public String getDpvStatus() {
		return dpvStatus;
	}

	public void setDpvStatus(String s) {
		dpvStatus = s;
	}

	public String getFullMoniker() {
		return fullMoniker;
	}

	public void setFullMoniker(String s) {
		fullMoniker = s;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String s) {
		country = s;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String s) {
		searchString = s;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String s) {
		errmsg = s;
	}

	public void doSearch(DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse)
			throws IOException, Fault {
		//QASearchResult searchresult = search(getCountry(),
			//	getSearchString(),"Default", getOnDemandLayout(),null);
		String layout=getOnDemandLayout();
		QASearchResult searchresult = search(getCountry(),
				getSearchString(),"Default", "BBB Layout",null);
		
		verificationLevel = searchresult.getVerifyLevel().toString();
		setVerificationLevel(verificationLevel);
		if (verificationLevel == "Verified"
				|| verificationLevel == "InteractionRequired") {
			setDpvStatus(searchresult.getQAAddress().getDPVStatus().toString());
			ArrayList arraylist = new ArrayList();
			Object aobj[] = searchresult.getQAAddress().getAddressLineArray();
			int i = aobj.length;
			for (int j = 0; j < i; j++) {
				AddressLineType addressline = (AddressLineType) aobj[j];
				arraylist.add(addressline.getLine());
			}

			aobj = new String[arraylist.size()];
			aobj = (String[]) arraylist.toArray(aobj);
			setAddressLines((String[]) aobj);
		} else {
			setFullMoniker(searchresult.getQAPicklist().getFullPicklistMoniker());
			ArrayList arraylist1 = new ArrayList();
			PicklistEntryType apicklistitem[] = searchresult.getQAPicklist().getPicklistEntryArray();
					
			int k = apicklistitem.length;
			for (int l = 0; l < k; l++) {
				PicklistEntryType picklistitem = apicklistitem[l];
				HashMap hashmap = new HashMap();
				hashmap.put("partialtext", picklistitem.getPartialAddress());
				hashmap.put("addresstext", picklistitem.getPicklist());
				hashmap.put("postcode", picklistitem.getPostcode());
				hashmap.put("moniker", picklistitem.getMoniker());
				if (picklistitem.isSetFullAddress())
					hashmap.put("fulladdress", "true");
				else
					hashmap.put("fulladdress", "false");
				arraylist1.add(hashmap);
			}

			setPicklistItems(arraylist1);
		}
	}
	
	/** Method to perform a refinement.
     * @param   request 
     * @param   response    
     * @param 	moniker
     * @param 	refineText
     * @return Picklist containing the results of the refinement
     */
	public void doRefine(DynamoHttpServletRequest request, DynamoHttpServletResponse response, String moniker, String refineText) throws Exception {

		   String layout=getOnDemandLayout();

	        Picklist picklist = refine(moniker, refineText);

	        if((picklist.getItems().length == 1) && (picklist.getItems()[0].isFullAddress())) {
	            FormattedAddress formatResult = getFormattedAddress(getOnDemandLayout(), picklist.getItems()[0].getMoniker());
	            setVerificationLevel("Verified");
	            setDpvStatus(formatResult.getDPVStatus().toString());

	            ArrayList<String> address = new ArrayList<String>();
	            for(AddressLine line : formatResult.getAddressLines()) {
	                address.add(line.getLine());
	            }
	            String arr[] = new String[address.size()];
	            arr = address.toArray(arr);
	            setAddressLines(arr);
	        } else {
	            setVerificationLevel("PremisesPartial");
	            setFullMoniker(picklist.getMoniker());
	            
	            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	            HashMap<String, String> temp;
	            for(PicklistItem item : picklist.getItems()) {
	                temp = new HashMap<String, String>();
	                temp.put("partialtext", item.getPartialAddress());
	                temp.put("addresstext", item.getText());
	                temp.put("postcode", item.getPostcode());
	                temp.put("moniker", item.getMoniker());
	                if(item.isFullAddress()) {
	                    temp.put("fulladdress", "true");
	                } else {
	                    temp.put("fulladdress", "false");
	                }
	                list.add(temp);
	            }
	            setPicklistItems(list);
	        }
	    }
	   
	   /** Method to perform a refinement.
	     * This is used after an initial search has been performed,
	     * when the user enters text to be used to filter upon the picklist,
	     * creating a smaller set of picklist results.
	     * Refinement and stepIn delegate to the same low-level function.
	     * @param   sRefinementText <code>String</code> the refinement text
	     * @param   sMoniker    <code>String</code> the search point moniker of the picklist (item) being refined.
	     * @param sRequestTag	<code>String</code> Request tag supplied by user
	     * @return Picklist containing the results of the refinement
	     */
	    public Picklist refine(String sMoniker, String sRefinementText,String sRequestTag) throws RemoteException, Fault  {
	        Picklist result = null;

	        QARefine param = com.qas.www.web_2010_04.QARefineDocument.QARefine.Factory.newInstance();

	        param.setMoniker(sMoniker);
	        param.setRefinement(sRefinementText);
	        com.qas.www.web_2010_04.QARefineDocument lQaRefineDocument =  com.qas.www.web_2010_04.QARefineDocument.Factory.newInstance();
	        lQaRefineDocument.setQARefine(param);
	        try {
	            // make the call to the server
	        	PicklistDocument lPicklistDocument = getProWeb().DoRefine(lQaRefineDocument);
	           

	            result = new Picklist(lPicklistDocument.getPicklist().getQAPicklist());
	        //axis2
	        } catch (AxisFault fault) {
				fault.printStackTrace();
			}
	        return result;
	    }

	    /** Method overload: to perform a refinement.
	     * This is used after an initial search has been performed,
	     * when the user enters text to be used to filter upon the picklist,
	     * creating a smaller set of picklist results.
	     * Refinement and stepIn delegate to the same low-level function.
	     * @param   sRefinementText <code>String</code> the refinement text
	     * @param   sMoniker    <code>String</code> the search point moniker of the picklist (item) being refined.
	     * @param sRequestTag	<code>String</code> Request tag supplied by user
	     * @return Picklist containing the results of the refinement
	     */
	    public Picklist refine(String sMoniker, String sRefinementText)
	    throws Exception {
	    	return refine(sMoniker, sRefinementText, null);
	    }
	    
	    /** Method to perform a Format Address.
	     * @param   request 
	     * @param   response    
	     * @param 	String
	     * @return Picklist containing the results of the refinement
	     */
	    
	    public void formatAddress(
			DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse, String s)
			throws Exception, IOException {
		String layout=getOnDemandLayout();
		FormattedAddress formattedaddress = getFormattedAddress(
				getOnDemandLayout(), s);
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
	
    /** Method to retrieve the final formatted address, formatting a picklist entry.
     * Typically the user selects a <code>PicklistItem</code> for which <code>isFullAddress()</code>
     * returns <code>true</code>.
     * Address formatting is performed using the picklist item moniker and the specified layout.
     * @param   sLayoutName     <code>String</code> layout name (specifies how the address should be formatted)
     * @param   sMoniker        <code>String</code> search point moniker that represents the address
     * @param 	sRequestTag		<code>String</code> User supplied tag for the request
     * @return  FormattedAddress relating to the search point moniker and layout.
     */
	public FormattedAddress getFormattedAddress(String sLayoutName,
			String sMoniker, String sRequestTag) throws RemoteException, Fault {
		FormattedAddress result = null;
		com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress param =  com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress.Factory.newInstance();
		param.setLayout(sLayoutName);
		param.setMoniker(sMoniker);
	/*	if (sRequestTag != null) {
			param.setRequestTag(sRequestTag);
		}*/
		com.qas.www.web_2010_04.QAGetAddressDocument paramQAGetAddressDocument =  com.qas.www.web_2010_04.QAGetAddressDocument.Factory.newInstance();
		paramQAGetAddressDocument.setQAGetAddress(param);
		try {
			AddressDocument address = getProWeb().DoGetAddress(paramQAGetAddressDocument);
			result = new FormattedAddress(address.getAddress().getQAAddress());
		} catch (AxisFault fault) {
			fault.printStackTrace();
		}
		return result;
	}
	
    /** Method overload: to retrieve the final formatted address, formatting a picklist entry.
     * Typically the user selects a <code>PicklistItem</code> for which <code>isFullAddress()</code>
     * returns <code>true</code>.
     * Address formatting is performed using the picklist item moniker and the specified layout.
     * @param   sLayoutName     <code>String</code> layout name (specifies how the address should be formatted)
     * @param   sMoniker        <code>String</code> search point moniker that represents the address
     * @return  FormattedAddress relating to the search point moniker and layout.
     */
	public FormattedAddress getFormattedAddress(String sLayoutName,
			String sMoniker) throws Exception {
		return getFormattedAddress(sLayoutName, sMoniker, null);
	}

	public static QASearchResult search(String sDataId, String sSearch,
			String sPromptSet, String sLayout, String sRequestTag)
			throws RemoteException, Fault {
		QASearchResult result = null;
		EngineType engine = getEngine();
		engine.setPromptSet(PromptSetType.DEFAULT);
		QASearch param = com.qas.www.web_2010_04.QASearchDocument.QASearch.Factory.newInstance();
		param.setCountry(new String(sDataId));
		param.setEngine(engine);
		if (sLayout != null)
			param.setLayout(sLayout);
		param.setSearch(sSearch);
		QASearchDocument mQASearchDocument= com.qas.www.web_2010_04.QASearchDocument.Factory.newInstance();
		
		mQASearchDocument.setQASearch(param);
		try {
			//System.out.print("RequestDocument is" + mQASearchDocument.toString());
			com.qas.www.web_2010_04.QASearchResultDocument searchResult = getProWeb().DoSearch(mQASearchDocument);
			//System.out.print("Final response" + searchResult);
			result = searchResult.getQASearchResult();
		} catch (AxisFault fault) {
			fault.printStackTrace();
		}
		return result;
		
		
	}
	
	public static void setEngineType(String s) {
		
		EngineType engineType = EngineType.Factory.newInstance();
		engineType.setStringValue("Verification");
		engineType.setFlatten(true);
		//engineType.setStringValue(EngineEnumType.SINGLELINE.toString());
		m_EngineType = engineType;
	}


	
	public static EngineType getEngine() {
		if (m_EngineType == null)
			try{
			setEngineType(SINGLELINE);
			}
		catch(Exception e){
			e.printStackTrace();
		}
		return m_EngineType;
	}
	
	 public static ProWebStub getProWeb() throws AxisFault{
         return new ProWebStub(getOnDemandUrl());
        
	 }
}
