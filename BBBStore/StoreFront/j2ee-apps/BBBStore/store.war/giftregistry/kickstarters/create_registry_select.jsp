<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isTransient" param="isTransient" />
	<dsp:getvalueof var="showHeader" param="showHeader" />
	<dsp:getvalueof var="showLoginLink" param="showLoginLink" />
	
<div class="kickstartersCreateRegistrySelect">
	
	<c:if test="${showHeader == 'true' }">
		<span class="createRegistryHeader"><bbbl:label key="lbl_kickstarters_create_registry_header" language="${pageContext.request.locale.language}" /></span>
	</c:if>	                	
	
	<dsp:form action="${contextPath}/kickstarters/top_consultants_picks.jsp" iclass="createRegistryDropdownForm"  method="POST" >
      	<dsp:droplet name="ErrorMessageForEach">
 			<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
 			<dsp:oparam name="output">
 				<dsp:valueof param="message"/><br>
 			</dsp:oparam>
		</dsp:droplet>                	
		<dsp:select bean="GiftRegistryFormHandler.registryEventType"  
					iclass="selector_primary selectRegToCreate"
					id="selectRegType" >	
	           <dsp:tagAttribute name="aria-required" value="false"/>
			<dsp:tagAttribute name="data-submit-button" value="submitClick"/>	
			<dsp:droplet name="GiftRegistryTypesDroplet">
				<dsp:param name="siteId" value="${appid}"/>
				<dsp:oparam name="output">
				<dsp:option value="" selected="selected"><bbbl:label key="lbl_kickstarters_create_registry_dropdown_text" language="${pageContext.request.locale.language}" /></dsp:option>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="registryTypes" />
						<dsp:oparam name="output">
							<dsp:param name="regTypes" param="element" />	
							<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
							<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
							<dsp:option value="${registryCode}"><dsp:valueof param="element.registryName"></dsp:valueof>
							</dsp:option>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>					
		</dsp:select>
		<%-- Client DOM XSRF | Part -1	
		<dsp:input 	bean="GiftRegistryFormHandler.ErrorURL" 
					type="hidden"
					value="${contextPath}/giftregistry/registry_type_select.jsp" />
		<dsp:input 	bean="GiftRegistryFormHandler.SuccessURL" 
					type="hidden"
					value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
					<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="babyLandingLoggedMulReg" />
		<dsp:input 	bean="GiftRegistryFormHandler.registryTypes" 
					type="submit" 
					value="submit" 
					style="display:none;"  
					iclass="createRegistryDropdownFormSubmit" />
	</dsp:form>
	<c:choose>
       	<c:when test="${isTransient == 'true' && showLoginLink == 'true'}">	
       		<span class="loginLink">	
       			<bbbl:label key="lbl_kickstarters_create_registry_have_existing_registry" language="${pageContext.request.locale.language}" />		    	  
		       	<dsp:a 	href="${contextPath}/account/Login" 
		       			bean="GiftRegistryFormHandler.loginToManageActiveRegistry" 
		       			value="loginToView"><bbbl:label key="lbl_kickstarters_create_registry_have_existing_login" language="${pageContext.request.locale.language}" /></dsp:a>
       		</span>
       	</c:when>						        	
	</c:choose>
</div>
</dsp:page>       