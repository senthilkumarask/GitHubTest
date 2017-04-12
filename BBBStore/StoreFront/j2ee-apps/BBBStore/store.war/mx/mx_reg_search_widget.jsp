<dsp:page>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
		<c:set var="parenthesis" value='('/>
		<c:set var="underscore" value='_'/>
		<c:set var="pwsurl"><c:out value="${fn:replace(param.pwsurl,parenthesis, underscore)}"/></c:set>
		<c:set var="eventType"><c:out value="${fn:replace(param.eventType,parenthesis, underscore)}"/></c:set>
		<c:set var="registryId"><c:out value="${fn:replace(param.registryId,parenthesis, underscore)}"/></c:set>
		<c:set var="pwsToken"><c:out value="${fn:replace(param.pwsToken,parenthesis, underscore)}"/></c:set>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>	
		
	<dsp:getvalueof var="appid" bean="Site.id" />
	<%-- <c:choose>
		<c:when test="${not empty eventType && (eventType eq 'Wedding' || eventType eq 'Commitment Ceremony')}">
			<c:set var="pageVariation" value="br" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="" scope="request" />
		</c:otherwise>
	</c:choose> --%>
    <%-- as per update from Raj/Lokesh all registry pages will use Purple theme on BedBathUS and BedBathCA --%>
    <c:choose>
        <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
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
	
	<bbb:mxPageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		
		<jsp:body>
		<div class="grid_6 logIn">
				<div class="guests">						
					<h3><bbbl:label key="lbl_find_registry_registry_header" language ="${pageContext.request.locale.language}"/></h3>			
				</div>		
		        <c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="1" scope="request"/>
				<dsp:include page="find_mxregistry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="successURL" value="${contextPath}/mx/registry_search_guest.jsp" />
				    <dsp:param name="errorURL" value="${findErrorURL }" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
				</dsp:include>
			</div>
			</jsp:body>
		</bbb:mxPageContainer>
</dsp:page>		