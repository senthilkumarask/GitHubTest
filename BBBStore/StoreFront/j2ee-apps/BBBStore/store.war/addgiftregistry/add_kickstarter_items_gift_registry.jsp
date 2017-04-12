<dsp:page>
	<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:importbean bean="/atg/multisite/Site"/>     
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />   
		<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:getvalueof var="appid" bean="Site.id" />	
			<dsp:getvalueof var="transient" bean="Profile.transient" />	
			<dsp:droplet name="AddItemToGiftRegistryDroplet">
				<dsp:param name="siteId" value="${appid}"/>
			 	<c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_add_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
			</dsp:droplet>
			<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
			<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
			<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
			
			<c:choose>
				<c:when test="${transient == 'false' && sizeValue==0}">
				<div class="button button_active addToRegistry <c:if test='${isInternationalCustomer}'>button_disabled</c:if>"> <%-- addToRegistryCreate ... this class should only be used if the value/label of the button is "Create Registry" --%>
					<input type="button" class="btnAddToRegNoRegistry" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />" name="btnAddToRegNoRegistry"    <c:if test='${isInternationalCustomer}'>disabled="disabled"</c:if> /> />
				</div>
				</c:when>
				<c:when test="${transient == 'false' && sizeValue==1}">
				<div class="button button_active <c:if test='${isInternationalCustomer}'>button_disabled</c:if>">
					<input class="addKickStarterItemsToRegistry" name="addKickStarterItemsToRegistry" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />" role="button"   <c:if test='${isInternationalCustomer}'>disabled="disabled"</c:if> />/>
				</div>
				</c:when>
				<c:when test="${transient == 'false' && sizeValue>1}">
				<div class="button button_active <c:if test='${isInternationalCustomer}'>button_disabled</c:if>">
					<input class="addKickStarterItemsToMultipleRegistry" name="addKickStarterItemsToMultipleRegistry" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />"   <c:if test='${isInternationalCustomer}'>disabled="disabled"</c:if> />/>
				</div>
				</c:when>
				<c:when test="${transient == 'true'}">
				<div class="button button_active <c:if test='${isInternationalCustomer}'>button_disabled</c:if>">
					<input  class="addKickStarterItemsToRegNoLogin" name="addKickStarterItemsToRegNoLogin" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />"  <c:if test='${isInternationalCustomer}'>disabled="disabled"</c:if> /> />
				</div>
				</c:when>
			</c:choose>
</dsp:page>