
<%@ page import="com.bbb.constants.BBBCoreConstants"%>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:include page="/_includes/third_party_on_of_tags.jsp" />
	<dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event" />
	<dsp:getvalueof var="eventType" param="eventType" />
	<%--  <dsp:getvalueof var="babySite" param="babySite" /> jj ${babySite }
   <c:choose>
   <c:when test="${babySite eq 'baby' }">
   		<c:set var="babyPage" value="/giftregistry/simpleReg_creation_form_mobile.jsp"/>
   </c:when>
   <c:otherwise>
  	 	<c:set var="babyPage" value="/giftregistry/simpleReg_creation_form_bbus_mobile.jsp"/>
   </c:otherwise>
   </c:choose> --%>

	<dsp:form
		action="${contextPath}/giftregistry/simpleReg_creation_form_bbus_mobile.jsp}">
		<div class="columns simpleRegTypeWrap">
		<dsp:select
				bean="GiftRegistryFormHandler.registryEventType" id="simpleRegType"
				iclass="triggerSubmit hidden">
				<dsp:tagAttribute name="aria-required" value="false" />
				<dsp:tagAttribute name="data-submit-button" value="submitClick" />
				<dsp:droplet name="GiftRegistryTypesDroplet">
					<dsp:param name="siteId" value="${currentSiteId}" />
					<dsp:oparam name="output">
						<dsp:option value="${eventType}" selected="selected">${eventType}</dsp:option>
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
			</dsp:select> <%-- <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
				value="${contextPath}/giftregistry/simpleReg_type_select.jsp" /> 
				<dsp:input
				bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
				value="${contextPath}/giftregistry/simpleReg_creation_form_bbus_mobile.jsp" /> --%>
				        <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="simpleRegTypeSelect" />
			<dsp:input id="submitClick"
				bean="GiftRegistryFormHandler.registryTypes" type="submit"
				value="Submit" style="display:none;"></dsp:input>
		</div>

	</dsp:form>
</dsp:page>
