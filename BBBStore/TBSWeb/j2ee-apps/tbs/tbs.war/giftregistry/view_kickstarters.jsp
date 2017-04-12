<dsp:page>
			
<%@ page import="com.bbb.constants.BBBCoreConstants" %>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />	
	<dsp:importbean bean="/atg/userprofiling/Profile"/>	
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
	
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
    <c:choose>
        <c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:getvalueof var="profileId" bean="Profile.id"/>
				<dsp:droplet name="GiftRegistryFlyoutDroplet">
					<dsp:param name="profile" bean="Profile"/>
				    <dsp:oparam name="output">
				    	<dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO" />
				    	<dsp:getvalueof param="registrySummaryVO.registryType.registryTypeDesc" var="eventType"/>
						<dsp:getvalueof param="registrySummaryVO.registryId"  var="ActRegistryId" />		
				 	</dsp:oparam>
				</dsp:droplet>
				<c:if test="${not empty ActRegistryId &&  profileId eq false }">
                        <c:redirect url="/giftregistry/view_registry_owner.jsp?eventType=${eventType}&registryId=${ActRegistryId}&autoSelect=true" />
               </c:if>
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper"> useFB useCertonaJs viewRegistry kickstarterLanding</jsp:attribute>
	<jsp:body>
		<%-- Droplet for showing error messages --%>
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
				<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">	
				<div class="error"><dsp:valueof param="message"/></div>
			</dsp:oparam>				
		</dsp:droplet>	

		<dsp:getvalueof var="sortSeq" param="sorting"/>
		<dsp:getvalueof var="view" param="view"/>
		
		
		<div id="heroContent" class="loggedOut"> 
       		<div class="row container_12 clearfix"> 
       			 
       			<div class="grid_5 ">
	       			<h1><bbbt:textArea key="txt_kickstarters_landing_header" language ="${pageContext.request.locale.language}"/></h1>
	       			 
	       		</div>       
				
     		</div>       			
		</div>
		
		
		<div id="content" class="container_12 clearfix" role="main">
			 <dsp:setvalue  bean="SessionBean.kickStarterEventType" value="KickStarter"/> 			
				
				<dsp:include page="kickstarters/top_consultants_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>	
				
				<dsp:include page="kickstarters/shop_look_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>	

				<dsp:getvalueof var="targetEventType" value="${eventType}" /> 
				<c:if test="${empty targetEventType}">
					<c:set var="targetEventType"><bbbc:config key="${fn:replace(appid, 'TBS_', '')}" configName="GiftRegistryConfig" /></c:set>
				</c:if>
				<!--values ${eventType}---${targetEventType}----${appid}-->

	          	<div class="grid_12 clearfix spacing spacingBottom">
	         		<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">	
						<dsp:param name="pageName" value="kickStartersView" />
						<dsp:param name="registryType" value="${targetEventType}" />
						<dsp:param name="promoSpot" value="Bottom" />
						<dsp:param name="siteId" value="${fn:replace(appid, 'TBS_', '')}" />
						<dsp:param name="channel" value="Desktop Web" />
					</dsp:include>
	          	</div>
	          	
	          	<dsp:include page="kickstarters/popular_items_grid.jsp?eventType=${targetEventType}" flush="true" >
	          	<dsp:param name="omniDesc" value="Popular items (viewKickStarters)" />
	          	</dsp:include>	
        </div>

        <script type="text/javascript">
           var BBB = BBB || {};
           var isTransient ='${isTransient}';
      		var profileStatus="non registered user";
      		if(isTransient=='false'){
      			profileStatus = "registered user"
      		}
      		BBB.viewKickStartersLoad = {
 					pageNameIdentifier: 'viewKickStartersLoad',
 					pageName:'Registry Consultant>kickstarters',
 					channel: 'Registry',
 					prop1: 'Registry',
 					prop2: 'Registry',
 					prop3: 'Registry',
 					var16: profileStatus,
 					var38:'${profileId}'
 				};
        </script>
        </jsp:body>
	<jsp:attribute name="footerContent">
    
    </jsp:attribute>

    </bbb:pageContainer>


</dsp:page>