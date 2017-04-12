<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isTransient" param="isTransient" />
	<dsp:getvalueof var="showHeader" param="showHeader" />
	<dsp:getvalueof var="showLoginLink" param="showLoginLink" />
	
<div class="kickstartersCreateRegistrySelect">
	
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