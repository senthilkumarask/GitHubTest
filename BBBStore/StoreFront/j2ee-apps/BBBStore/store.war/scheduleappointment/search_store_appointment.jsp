<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="com/bbb/selfservice/ScheduleAppointmentManager" />
	<c:choose>
			<c:when test="${currentSiteId eq 'BedBathCanada'}">
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZipCACollage}" />
		<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state" 
		value="${param.storeStateCACollage}"/>
			</c:when>
			<c:otherwise>
				<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.postalCode"
		value="${param.storeZipCollage}" />
		<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.state"
		value="${param.storeStateCollage}" />
			</c:otherwise>
	</c:choose>
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.address"
		value="${param.add2}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.city"
		value="${param.storeCityCollage}" />
	<dsp:setvalue bean="SearchStoreFormHandler.storeLocator.radius"
		value="${param.selRadiusName}" />
		<dsp:setvalue bean="SearchStoreFormHandler.defaultAppointment"
		value="${param.appCode}" />
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