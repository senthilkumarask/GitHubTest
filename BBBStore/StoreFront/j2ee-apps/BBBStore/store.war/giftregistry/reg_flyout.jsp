<%@ page import="com.bbb.constants.BBBCoreConstants" %>
<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/> 
    <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />   
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean
        bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />

	        <c:set var="BuyBuyBabySite">
	        
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<c:set var="babyCAMode" value="${sessionScope.babyCanada }" scope="request" />
	<input type="hidden" id="regBABYCA" value="${requestScope.babyCAMode }" />
<dsp:droplet name="GiftRegistryFlyoutDroplet">
<dsp:param name="profile" bean="Profile"/>
    <dsp:oparam name="output">
	 <dsp:getvalueof id="siteId" idtype="java.lang.String" param="<%= BBBCoreConstants.SITE_ID %>" >
    <c:choose>
    <c:when test="${currentSiteId eq BedBathUSSite}">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param name="value" param="userStatus"/>
			<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
			
		        <dsp:include page= "bridal_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="true"/>
				</dsp:include>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
		        <dsp:include page= "bridal_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="false"/>					
				</dsp:include>
			</dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES %>">
	                <dsp:include page= "bridal_gift_registry_loggedin_no.jsp"/>
						
            </dsp:oparam>
            
			<dsp:oparam name="<%= BBBCoreConstants.USER_NOT_LOGGED_IN %>">
	                <dsp:include page= "bridal_gift_registry.jsp"/>	
            </dsp:oparam>   

            <dsp:oparam name="<%= BBBCoreConstants.BBB_BUSINESS_EXCEPTION %>">
               <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.BBB_SYSTEM_EXCEPTION %>">
                <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>       
        </dsp:droplet>  
        </c:when>
       	<c:when test="${currentSiteId eq BuyBuyBabySite}">
		        <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param name="value" param="userStatus"/>
			<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
		        <dsp:include page= "baby_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="true"/>
				</dsp:include>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
		        <dsp:include page= "baby_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="false"/>					
				</dsp:include>
			</dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES %>">
	                <dsp:include page= "baby_gift_registry_loggedin_no.jsp"/>
						
            </dsp:oparam>
            
			<dsp:oparam name="<%= BBBCoreConstants.USER_NOT_LOGGED_IN %>">
	                <dsp:include page= "baby_gift_registry.jsp"/>	
            </dsp:oparam>   

            <dsp:oparam name="<%= BBBCoreConstants.BBB_BUSINESS_EXCEPTION %>">
               <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.BBB_SYSTEM_EXCEPTION %>">
                <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>       
        </dsp:droplet>
        </c:when>
        
           <c:when test="${(siteId eq BedBathCanadaSite) && (requestScope.babyCAMode == 'true')}">
           
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param name="value" param="userStatus"/>
			<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
		        <dsp:include page= "baby_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="true"/>
				</dsp:include>
            </dsp:oparam>
            
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
		        <dsp:include page= "baby_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="false"/>					
				</dsp:include>
			</dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES %>">
	                <dsp:include page= "baby_gift_registry_loggedin_no.jsp"/>
						
            </dsp:oparam>
            
			<dsp:oparam name="<%= BBBCoreConstants.USER_NOT_LOGGED_IN %>">
	                <dsp:include page= "baby_gift_registry.jsp"/>	
            </dsp:oparam>   

            <dsp:oparam name="<%= BBBCoreConstants.BBB_BUSINESS_EXCEPTION %>">
               <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.BBB_SYSTEM_EXCEPTION %>">
                <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>       
        </dsp:droplet>        
     
        </c:when>
        <c:otherwise>
         <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param name="value" param="userStatus"/>
			<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
		        <dsp:include page= "bridal_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="true"/>
				</dsp:include>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
		        <dsp:include page= "bridal_gift_registry_loggedin.jsp">
					<dsp:param name="registrySummaryVO" param="<%= BBBCoreConstants.REGISTRY_SUMMARY_VO %>" />
					<dsp:param name="multiReg" value="false"/>					
				</dsp:include>
			</dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES %>">
	                <dsp:include page= "bridal_gift_registry_loggedin_no.jsp"/>
						
            </dsp:oparam>
            
			<dsp:oparam name="<%= BBBCoreConstants.USER_NOT_LOGGED_IN %>">
	                <dsp:include page= "bridal_gift_registry.jsp"/>	
            </dsp:oparam>   

            <dsp:oparam name="<%= BBBCoreConstants.BBB_BUSINESS_EXCEPTION %>">
               <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
            <dsp:oparam name="<%= BBBCoreConstants.BBB_SYSTEM_EXCEPTION %>">
                <bbbe:error key="err_regflyout_errormsg" language ="${pageContext.request.locale.language}"/> <dsp:valueof param="errorMsg"/>
            </dsp:oparam>       
        </dsp:droplet>  
        
        </c:otherwise>
        
        
        </c:choose>  
	</dsp:getvalueof>  	
    </dsp:oparam>
</dsp:droplet>

</dsp:page>