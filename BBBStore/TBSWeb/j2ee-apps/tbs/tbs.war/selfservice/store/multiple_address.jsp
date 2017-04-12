
<%-- ====================== Description===================
/**
* This page is used to do the mapping of suggested address and call the handleSearchStore method of SearchStoreFormHandler.
* @author Seema
**/
--%>

<%--
1. Added city first in next search because the next search will be City,County,State
2. Address field in multiple address suggestions contains County rather than street address
--%>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.address"
		value="${param.city}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.city"
		value="${param.address}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state"
		value="${param.stateCode}" />
	
	
	<dsp:setvalue bean="SearchStoreFormHandler.searchStore" />
	
</dsp:page>