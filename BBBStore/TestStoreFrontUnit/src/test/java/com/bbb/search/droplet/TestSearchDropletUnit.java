package com.bbb.search.droplet;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.framework.BaseTestCase;
import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBUtility;

@PrepareForTest({BBBUtility.class})
public class TestSearchDropletUnit extends BaseTestCase{
	
	@Mock Profile mockProfile;
	@Mock SearchManager mockSearchManager;
	@Mock PersonalStoreManager mockPersonalStoreManager;
	@Mock BBBCatalogTools mockCatalogTools;
	
	
	public void setUp() {
		super.setUp();
		mockBBBUtility();
		
	}
	
	public void mockBBBUtility() {
		BBBUtility mockBBBUtility=mock(BBBUtility.class);
		PowerMockito.mockStatic(BBBUtility.class);
		
	}

	public SearchDroplet setupSearchDroplet() throws Exception {
		doReturn(mockProfile).when(getDynHttpRequest()).resolveName("/atg/userprofiling/Profile");
		when(mockProfile.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE)).thenReturn("US");
		doNothing().when(mockPersonalStoreManager).
				createCategoryCookie((DynamoHttpServletRequest)anyObject(),(DynamoHttpServletResponse)anyObject(),anyString(),anyString());
		when(mockCatalogTools.getAllValuesForKey(anyString(),anyString())).thenReturn(null);
		List<String> pageSize=new ArrayList<String>();
		pageSize.add("1-48");
		when(mockSearchManager.fetchPerPageDropdownList()).thenReturn(pageSize);
		SearchDroplet searchDroplet=new SearchDroplet();
		searchDroplet.setSearchManager(mockSearchManager);
		searchDroplet.setPersonalStoreMgr(mockPersonalStoreManager);
		searchDroplet.setCatalogTools(mockCatalogTools);
		return searchDroplet;

	}
	/**
	 * TODO:1. Test not complete: Need to test catalogid, catalogrefid, frombranpage,pagenum, bccSortCode etc. in search query
	 * 2. Need to test special character handling for some parameters: -(hyphen) will be replaced by +
	 * 3. handle special handling of pageOptSort - replace of hyphen and handling sort ascending or descending
	 * 4.  
	 * @throws Exception
	 */
	@Test
	public void testHeaderSearchQuery() throws Exception{
			SearchDroplet searchDroplet=setupSearchDroplet();
			
			getDynHttpRequest().setQueryString("Keyword=sheets&narrowDown=red&partialFlag=true&searchMode=search_mode");
			
			getDynHttpRequest().setParameter("isHeader", "Y");			
			getDynHttpRequest().setParameter("savedUrl", "/store/s/sheets");
			getDynHttpRequest().setParameter("isRedirect","true");
			getDynHttpRequest().setParameter("view","view");
			getDynHttpRequest().setServerName("servername");
			getDynHttpRequest().setParameter(BBBCoreConstants.SITE_ID, "US_Site");
			when(BBBUtility.isAlphaNumeric("view")).thenReturn(true);
			
			ArgumentCaptor<SearchQuery> argForSearchQry=ArgumentCaptor.forClass(SearchQuery.class);

			searchDroplet.service(getDynHttpRequest(), getDynHttpResponse());
			
			verify(mockSearchManager).performSearch(argForSearchQry.capture());	
			assertEquals("header search check",argForSearchQry.getValue().isHeaderSearch(),true);
			assertNull("keyword",argForSearchQry.getValue().getKeyWord());
			assertNull("narrowDown match",argForSearchQry.getValue().getNarrowDown());
			assertEquals("partial Flag",argForSearchQry.getValue().getPartialFlag(),"true");
			assertEquals("servername",argForSearchQry.getValue().getHostname());
			assertEquals("is redirected check",argForSearchQry.getValue().isRedirected(),true);
			assertEquals("site id", "US_Site",argForSearchQry.getValue().getSiteId());
			
	}
	

	/**
	 * TODO: Handle pageSizeFilter cookie
	 * @throws Exception
	 */
	@Test
	public void testPersistCookie() throws Exception {
		SearchDroplet searchDroplet = setupSearchDroplet();
		getDynHttpRequest().setParameter("savedUrl", "/store/s/sheets");
		getDynHttpRequest().setServerName("servername");
		getDynHttpRequest().setParameter("view", "currentView");
		when(BBBUtility.isAlphaNumeric("currentView")).thenReturn(true);

		ArgumentCaptor<String> headerString = ArgumentCaptor.forClass(String.class);

		searchDroplet.service(getDynHttpRequest(), getDynHttpResponse());
		getDynHttpResponse().getResponse().addHeader(anyString(), headerString.capture());
		verify(getDynHttpResponse().getResponse(), times(1)).addHeader(anyString(), headerString.capture());
		// assertEquals("SAVEDURL=/store/s/sheets; domain=servername; path=/; HttpOnly", headerString.getAllValues().get(0));
		// assertEquals("currentView; domain=servername; path=/; HttpOnly", headerString.getAllValues().get(1));
	}
	
	/**
	 * TODO:This would be very similar to header query search test
	 * Effort: 1 hr
	 */
	public void testKeywordSearchQuery(){
		
	}
	
	/**
	 * TODO:Test encoding of keyword and narrowdown parameters
	 * Effort: 1 hr
	 */
	
	public void testKeywordEncoding(){
		
	}
	/**
	 * TODO:Droplet sets parameter like origSearchTerm and narrowdown in droplet to be used by JSP. 
	 * Validate that parameters are set in droplet and are encoded properly
	 */
	public void testParamsSetByDroplet(){
		
	}

	/**
	 * TODO:Test VO created by narrow down blue pills
	 */
	public void testNarrowDownBluePills(){
		
	}
	
	/**
	 * TODO:Test VO created drop down list
	 */
	public void testDropDownList(){
		
	}
	
	/**
	 * TODO:
	 */
	public void testPhantomCategoryManipulation(){
		
	}
	
	
	public void checkPreselectedForComparison(){
		
	}
	
	public void checkDepartmenetAsFirstDimension(){
		
	}
}
