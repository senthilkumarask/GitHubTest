<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof var="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>	
	<dsp:getvalueof param="event" var="registryTypesEvent"/>
	
 <dsp:droplet name="GetRegistryTypeNameDroplet">
                        		<dsp:param name="siteId" value="${currentSiteId}"/>
							 	<dsp:param name="registryTypeCode" value="${registryTypesEvent}"/>
    							<dsp:oparam name="output">
									<dsp:getvalueof var="eventType" param="registryTypeName"/>
								</dsp:oparam>
							</dsp:droplet>
<dsp:form action="${contextPath}/giftregistry/simpleReg_creation_form.jsp">


<div class="simpleRegTypeWrap">  
	<label class="hidden" id="createRegLabel" for="simpleRegType" >
		<c:choose>
			<c:when test="${not empty eventType}">
					<bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/>. currently selected ${eventType}. dropdown
			</c:when>
			<c:otherwise>
				<bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/>.  dropdown
			</c:otherwise>
		</c:choose>
	</label>

	<dsp:select  bean="GiftRegistryFormHandler.registryEventType" id="simpleRegType"  iclass="selectRegToCreate hidden">	
            <dsp:tagAttribute name="aria-required" value="false"/>
			<dsp:tagAttribute name="data-submit-button" value="submitClick"/>	
					<dsp:droplet name="GiftRegistryTypesDroplet">
						<dsp:param name="siteId" value="${currentSiteId}"/>
						<dsp:oparam name="output">
						<dsp:option value="${fn:escapeXml(registryTypesEvent)}" selected="selected">${eventType}</dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registryTypes" />
								<dsp:oparam name="output">
									<dsp:param name="regTypes" param="element" />	
									<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
									  <c:if test="${regTypesId ne eventType}">
														    <dsp:option value="${registryCode}">
																<dsp:valueof param="element.registryName"></dsp:valueof>
															</dsp:option>
															</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
			</dsp:select>
			
		<%-- Client DOM XSRF | Part -1
		dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/simpleReg_type_select.jsp" />
		<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
			<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden"
			value="simpleReg" />
		<dsp:input id="submitClick"
			bean="GiftRegistryFormHandler.registryTypes" type="submit" value="Submit" style="display:none;"></dsp:input>

</div>


    
</dsp:form>
</dsp:page>
