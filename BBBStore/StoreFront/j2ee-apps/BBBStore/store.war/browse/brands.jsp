<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" /> 
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage" scope="request" />
	<dsp:getvalueof var="searchTerm" param="Keyword" scope="request"/>
	<dsp:getvalueof var="inStore" param="inStore" scope="request"/>
	<dsp:getvalueof var="storeIdFromURL" param="storeID" scope="request"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ClearFilterRedirectDroplet"/>
    <c:set var="clearFilter" scope="request"><c:out value="${param.clearFilters}"/></c:set>
	<c:set var="brandNameForUrl" scope="request"><c:out value="${param.brandNameForUrl}"/></c:set>
	<c:set var="pageTypeForCanonicalURL" value="Brand" scope="request" />
	
	<c:set var="localStorePLPFlag" scope="request">
		<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
	</c:set>
	
	<%--Adding logic for cookie generation for Lat/Lng From URL--%>
	<c:if test="${not empty storeIdFromURL && localStorePLPFlag}">
	<dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBSetLatLngCookieDroplet">
	<dsp:param name="storeIdFromURL" value="${storeIdFromURL}" />
	<dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="latLngFromPLP" param="latLngFromPLP" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
	</c:if>
	<%--Adding logic for cookie generation for Lat/Lng From URL--%>
	
	<dsp:droplet name="BrandDetailDroplet">
		<dsp:param name="keywordName" value="${searchTerm}"/>
		<dsp:oparam name="seooutput">
			<dsp:getvalueof var="seoUrl" param="seoUrl" />
			<dsp:getvalueof var="brandName" param="brandName" />
			<c:set var="Keyword" value="${brandName}" scope="request" />
			<c:set var="url" value="${contextPath}${seoUrl}" scope="request" />
			<dsp:getvalueof var="sortOptionVO" param="sortOptionVO"/>
			<c:set var="sortOptionVO" value="${sortOptionVO}" scope="request" />
			<%-- R2.2 BRAND Promo container from BCC Start --%>
			<dsp:getvalueof var="jsFilePath" param="jsFilePath"  />	
			<c:set var="jsFilePath" value="${jsFilePath}" scope="request" />
			<dsp:getvalueof var="cssFilePath" param="cssFilePath"  />
			<c:set var="cssFilePath" value="${cssFilePath}" scope="request" />
			<dsp:getvalueof var="promoContent" param="promoContent"  />
			<c:set var="promoContent" value="${promoContent}" scope="request" />
			<dsp:getvalueof var="isPromoContentAvailable" param="isPromoContentAvailable" />
			<c:set var="isPromoContentAvailable" value="${isPromoContentAvailable}" scope="request" />
			<%--R2.2 BRAND Promo container from BCC END --%>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test = "${clearFilter}">
	      <dsp:droplet name="ClearFilterRedirectDroplet">
	      	<dsp:param name="url" value="${seoUrl}" />
	      </dsp:droplet>
	</c:if>
	
	<dsp:droplet name="SearchDroplet">
		<dsp:param name="bccSortCode" value="${sortOptionVO.defaultSortingOption.sortUrlParam}"/>
		<dsp:param name="bccSortOrder" value="${categoryVO.sortOptionVO.defaultSortingOption.ascending}"/>
		<c:if test="${localStorePLPFlag}">
		<c:choose>
		<c:when test="${inStore == 'true'}">
			<dsp:param name="storeId" value="${storeIdFromURL}"/>
			<dsp:param name="onlineTab" value="false"/>
		</c:when>
		<c:otherwise>
			<%--<dsp:param name="storeId" value="${storeIdFromURL}"/>
			<dsp:param name="onlineTab" value="true"/>--%>
		</c:otherwise>
		</c:choose>
		</c:if>
		<dsp:oparam name="error_PageNumOutOfBound">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
		<dsp:oparam name="output">
		 <dsp:getvalueof param="swsterms" var="swsterms" scope="request"/>
			<dsp:include page="subcategory.jsp" >
				<dsp:param name="brandName" value="${brandName}"/>
				<dsp:param name="narrowDown" value="${narrowDown}"/>
				<dsp:param name="seoUrl" value="${seoUrl}"/>
				<dsp:param name="fromBrandPage" value="true"/>
				
			</dsp:include>
			<dsp:getvalueof var="linkString" param="linkString" scope="request"/>
			<dsp:param name="brandNameForUrl" value="${brandNameForUrl}"/>
		</dsp:oparam>
		<dsp:oparam name="empty">
				   <dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
				 <dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&" flush="true"  />
				</dsp:oparam>
		<dsp:oparam name="redirect">
			<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
				<c:if test="${isRedirectToParent eq 'true'}">
					<dsp:droplet name="/atg/dynamo/droplet/Redirect">
						<dsp:param name="url" value="${contextPath}${seoUrl}" />
					</dsp:droplet>
				</c:if>
		</dsp:oparam>	
		<%--Start: BPS-1952 | Search Within Search | Null Search Page for SWS --%>
		<dsp:oparam name="swsempty">
				<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
				<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
				<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
				<dsp:param name="browseSearchVO" param="browseSearchVO" />
				<dsp:param name="swsTermsList" param="swsTermsList" />
				<dsp:param name="seoUrl" value="${seoUrl}" />
				<dsp:include page="/search/sws_no_search_results.jsp?_dyncharset=UTF-8&"/>
		</dsp:oparam>
		<%--End: BPS-1952 | Search Within Search | Null Search Page for SWS --%>		
		<dsp:oparam name="error">
			<%-- instead of showing plain old error message, redirecting the user to a more meaningful server error page
			<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
			<div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
			--%>
			<dsp:include page="../global/serverError.jsp" flush="true"/>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>
