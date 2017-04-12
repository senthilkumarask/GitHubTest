
<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />		
	
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
			
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
 
	<dsp:form action="${contextPath}/giftregistry/my_registries.jsp" requiresSessionConfirmation="false" >
		<dsp:droplet name="ErrorMessageForEach">
		  <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
		  <dsp:oparam name="output">
		  <dsp:valueof param="message"/><br>
		  </dsp:oparam>
		</dsp:droplet>
	
		<dsp:getvalueof var="fragName" param="fragName"/>
	
	   <div class="createRegistry selectTypeFOR">
			<dsp:select  bean="GiftRegistryFormHandler.registryIdEventType"  iclass="selector_primary triggerSubmit">
                <dsp:tagAttribute name="aria-required" value="false"/>            
				<dsp:tagAttribute name="data-submit-button" value="submitClick${fragName}"/>	
					<dsp:droplet name="AddItemToGiftRegistryDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
					</dsp:droplet>
							<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
							<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
							<dsp:option><bbbt:textArea key="txt_regflyout_viewandmanageregistry"
										language="${pageContext.request.locale.language}" /></dsp:option>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${registrySkinnyVOList}" />
									<dsp:oparam name="output">
										<dsp:param name="futureRegList" param="element" />
									
										<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
										<dsp:getvalueof var="event_type" param="element.eventType" />
										<dsp:option value="${regId}_${event_type}"  iclass="${event_type}">
											<dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
										</dsp:option>
									</dsp:oparam>
								</dsp:droplet>
				</dsp:select>
				<%-- Client DOM XSRF | Part -1
				< dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
					value="${contextPath}/giftregistry/view_manage_registry.jsp" />
				<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
					value="${contextPath}/giftregistry/view_registry_owner.jsp" /> --%>
				<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="viewManageRegistry" />
				<dsp:input id="submitClick${fragName}"
					bean="GiftRegistryFormHandler.viewManageRegistry" type="submit" value=""
					style="display:none;">
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="role" value="button"/>
                    <dsp:tagAttribute name="aria-labelledby" value="submitClick${fragName}"/>
                </dsp:input>
	    </div>
	    
	</dsp:form>
</dsp:page>
