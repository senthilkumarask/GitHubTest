<dsp:page>
	<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:importbean bean="/atg/multisite/Site"/>        
		<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:getvalueof var="appid" bean="Site.id" />	
			<dsp:getvalueof var="transient" bean="Profile.transient" />	
			<dsp:droplet name="AddItemToGiftRegistryDroplet">
				<dsp:param name="siteId" value="${appid}"/>
			 <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_add_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<dsp:oparam name="output">
					<c:set var="sizeValue">
						<dsp:valueof param="size" />
					</c:set>					
				</dsp:oparam>
			</dsp:droplet>
			<c:choose>
				<c:when test="${transient == 'false' && sizeValue==0}">
				<div class="button_active addToRegistry"> <%-- addToRegistryCreate ... this class should only be used if the value/label of the button is "Create Registry" --%>
					<input type="button" class="btnAddToRegNoRegistry tiny button secondary transactional" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />" name="btnAddToRegNoRegistry" />
				</div>
				</c:when>
				<c:when test="${transient == 'false' && sizeValue==1}">
				<div class="button_active">
					<input class="addKickStarterItemsToRegistry tiny button secondary transactional" name="addKickStarterItemsToRegistry" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />" role="button" />
				</div>
				</c:when>
				<c:when test="${transient == 'false' && sizeValue>1}">
				<div class="button_active">
					<input class="addKickStarterItemsToMultipleRegistry tiny button secondary transactional" name="addKickStarterItemsToMultipleRegistry" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />"/>
				</div>
				</c:when>
				<c:when test="${transient == 'true'}">
				<div class="button_active">
					<input  class="addKickStarterItemsToRegNoLogin tiny button secondary transactional" name="addKickStarterItemsToRegNoLogin" type="button" value="<bbbl:label key="lbl_add_all_to_registry__button" language="${pageContext.request.locale.language}" />" />
				</div>
				</c:when>
			</c:choose>
</dsp:page>