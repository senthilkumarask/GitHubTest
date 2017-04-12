<c:if test="${pageContext.request.method ne 'POST'}" >
	<% response.sendError(405, "Method Not Allowed!" );%>
</c:if>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	
	<c:choose>
			<c:when test="${currentSiteId eq 'BedBathCanada'}">
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZipCA}" />
		<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state" 
		value="${param.storeStateCA}"/>
			</c:when>
			<c:otherwise>
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZip}" />
		<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state"
		value="${param.storeState}" />
			</c:otherwise>
	</c:choose>
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.address"
		value="${param.add2}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.city"
		value="${param.storeCity}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.radius"
		value="${param.selRadiusName}" />
	<dsp:setvalue bean="SearchStoreFormHandler.siteContext" 
		beanvalue="/atg/multisite/Site.id" />
	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
		value="" />
	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
		value="" />

	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreWithNoRedirect" />
	<dsp:getvalueof bean="SearchStoreFormHandler.formError" var="flag" />
	<dsp:getvalueof var="skipJsonError" param="skipJsonError"/>
	<c:if test="${empty skipJsonError or skipJsonError eq false }">
		<json:object>
			<json:property name="status" value="${flag}"/>								
		</json:object>
	</c:if>
</dsp:page>