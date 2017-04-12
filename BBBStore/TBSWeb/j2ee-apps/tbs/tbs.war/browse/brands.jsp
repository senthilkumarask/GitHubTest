<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" /> 
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage" scope="request" />
	<dsp:getvalueof var="comparisonListItems" bean="ProductComparisonList.items" />
	<dsp:getvalueof var="searchTerm" param="Keyword" scope="request"/>
	<c:set var="brandNameForUrl" scope="request"><c:out value="${param.brandNameForUrl}"/></c:set>
	<c:set var="pageTypeForCanonicalURL" value="Brand" scope="request" />
	<input type="hidden" name="fromPage" value="brandPage"/>
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
	<dsp:droplet name="SearchDroplet">
		<dsp:param name="bccSortCode" value="${sortOptionVO.defaultSortingOption.sortUrlParam}"/>
		<dsp:param name="bccSortOrder" value="${categoryVO.sortOptionVO.defaultSortingOption.ascending}"/>
		<dsp:param name="comparisonListItems" value="${comparisonListItems}"/>
		<dsp:oparam name="error_PageNumOutOfBound">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:include page="subcategory-rwd.jsp" />
			<dsp:getvalueof var="linkString" param="linkString" scope="request"/>
			<dsp:param name="brandNameForUrl" value="${brandNameForUrl}"/>
					
		</dsp:oparam>
		<dsp:oparam name="redirect">
			<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
				<c:if test="${isRedirectToParent eq 'true'}">
					<dsp:droplet name="/atg/dynamo/droplet/Redirect">
						<dsp:param name="url" value="${contextPath}${seoUrl}" />
					</dsp:droplet>
				</c:if>
		</dsp:oparam>	
		<dsp:oparam name="empty">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
		<dsp:oparam name="error">
			<%-- instead of showing plain old error message, redirecting the user to a more meaningful server error page
			<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
			<div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
			--%>
			<dsp:include page="../global/serverError.jsp" flush="true"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
