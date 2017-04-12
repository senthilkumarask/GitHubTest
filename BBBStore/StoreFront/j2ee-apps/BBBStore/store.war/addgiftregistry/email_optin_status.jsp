<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:getvalueof var="emailOptIn" param="emailOptIn" />
		<dsp:getvalueof var="registryId" param="registryId" />

		<dsp:setvalue bean="GiftRegistryFormHandler.emailOptIn" value="${emailOptIn}" />
		<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${registryId}" />

		<dsp:setvalue bean="GiftRegistryFormHandler.emailOptInChange" />

        <dsp:getvalueof bean="GiftRegistryFormHandler.errorFlagEmailOptIn" var="flag" />
	    <c:choose>
				<c:when test="${flag==true }">
					<div ><bbbl:label key="lbl_error_occured" language="${pageContext.request.locale.language}" /></div>
				</c:when>
				<c:otherwise>
					<div><bbbl:label key="lbl_success_proceed" language="${pageContext.request.locale.language}" /></div>
				</c:otherwise>
		</c:choose>

</dsp:page>