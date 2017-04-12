<dsp:page>

	<dsp:getvalueof var="requestUrl" bean="/OriginatingRequest.requestURL" />
	<dsp:getvalueof var="queryParameters" bean="/OriginatingRequest.queryString" />
	<c:set var="sessionExpiredPage" value="global/sessionExpired.jsp" />
	<dsp:getvalueof var="firstNameReg" bean="/OriginatingRequest.firstNameReg" />
	<dsp:getvalueof var="lastNameReg" bean="/OriginatingRequest.lastNameReg" />

	<dsp:getvalueof var="bbRegistryFirstName" bean="/OriginatingRequest.bbRegistryFirstName" />
	<dsp:getvalueof var="bbRegistryLastName" bean="/OriginatingRequest.bbRegistryLastName" />
	<dsp:getvalueof var="bbRegistryNumber" bean="/OriginatingRequest.bbRegistryNumber" />

	<c:set var="searchRegistryPage" value="bbb_search_registry" />
	
	<c:choose>
		<c:when test="${fn:contains(requestUrl, sessionExpiredPage)}">
			<dsp:include page="/409.jsp" />
		</c:when>
		<c:when test="${fn:contains(requestUrl, searchRegistryPage)}">
			<c:if test="${not empty bbRegistryFirstName}">
				<c:set var="firstNameReg" value="${bbRegistryFirstName}" />
			</c:if>
			<c:if test="${not empty bbRegistryLastName}">
				<c:set var="lastNameReg" value="${bbRegistryLastName}" />
			</c:if>
			<c:if test="${not empty bbRegistryNumber}">
				<c:set var="regNum" value="${bbRegistryNumber}" />
			</c:if>
			<c:redirect url="/giftregistry/registry_search_session_expired.jsp?firstNameReg=${firstNameReg}&lastNameReg=${lastNameReg}&regNum=${regNum}" />
		</c:when>
		<c:otherwise>
			<c:redirect url="${requestUrl}" />
		</c:otherwise>
	</c:choose>
</dsp:page>
