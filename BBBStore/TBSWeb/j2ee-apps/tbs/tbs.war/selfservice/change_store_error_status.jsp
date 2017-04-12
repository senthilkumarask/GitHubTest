<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	
	<c:choose>
			<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZipCA}" />
			</c:when>
			<c:otherwise>
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZip}" />
			</c:otherwise>
	</c:choose>
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.address"
		value="${param.add2}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.city"
		value="${param.storeCity}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state"
		value="${param.storeState}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.radius"
		value="${param.selRadiusName}" />
	<dsp:setvalue bean="SearchStoreFormHandler.siteContext" 
		beanvalue="/atg/multisite/Site.id" />
	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
		value="" />
	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
		value="" />

	<dsp:setvalue bean="SearchStoreFormHandler.searchStore" />
	<dsp:getvalueof bean="SearchStoreFormHandler.formError" var="flag" />	
	<c:choose>
		<c:when test="${flag==true}">
			<json:object>
				<json:property name="status" value="${flag}"/>								
			</json:object>
		</c:when>
		<c:otherwise>			
			<json:object>
				<json:property name="status" value="${flag}"/>
			</json:object>
		</c:otherwise>
	</c:choose>
</dsp:page>