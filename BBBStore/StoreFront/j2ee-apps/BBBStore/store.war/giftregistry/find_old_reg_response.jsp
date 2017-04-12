<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		
	<dsp:getvalueof var="registryPassword" param="registryPassword" />
	<dsp:getvalueof var="registryId" param="registryID" />
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="eventDate" param="eventDate" />
	
	<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${registryId}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.registryPassword" value="${registryPassword}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.importEventType" value="${eventType}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.importEventDate" value="${eventDate}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.importRegistry" />
	<dsp:getvalueof
		bean="GiftRegistryFormHandler.importURL" var="url" />
		<dsp:getvalueof bean="GiftRegistryFormHandler.formError" var="formError"/>
		<dsp:getvalueof bean="GiftRegistryFormHandler.importErrorMessage" var="errorMessage"/>
		<json:object>
		<c:choose>
		<c:when test="${formError == true}">
			<c:choose>
			<c:when test="${errorMessage !=null && errorMessage !=''}">
			<json:property name="error">true</json:property>
			<json:property name="errorMessage" escapeXml="false">${errorMessage}</json:property>
			</c:when>
			</c:choose>
		</c:when>
		<c:when test="${url !=null&&url !=''}">
		<json:property name="url">${url}</json:property>
		</c:when>
		<c:otherwise>
		<json:property name="exists">true</json:property>
		</c:otherwise>
		</c:choose>
		
		</json:object>
</dsp:page>