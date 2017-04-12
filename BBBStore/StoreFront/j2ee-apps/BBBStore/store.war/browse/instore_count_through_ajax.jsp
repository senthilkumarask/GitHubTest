<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="requestURIWithQueryString" bean="/OriginatingRequest.requestURIWithQueryString"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="storeIdFromAjax" param="storeIdFromAjax"/>
	<dsp:getvalueof var="categoryId" param="categoryId" />
	<dsp:getvalueof var="catFlg" param="catFlg"/>
	<dsp:getvalueof var="subCatPlp" param="subCatPlp" scope="request"/>
	<c:set var="searchTerm" scope="request"><c:out value="${param.Keyword}"/></c:set>
	<c:set var="lastSearchedUrl" value="${contextPath}/s/${searchTerm}" />
	
	<c:set var="localStorePLPFlag">
		<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
	</c:set>
	
	<c:if test="${fn:contains(requestURIWithQueryString,'brandsPage')}">
	<dsp:droplet name="BrandDetailDroplet">
		<dsp:param name="keywordName" value="${searchTerm}"/>
		<dsp:oparam name="seooutput">
			<dsp:getvalueof var="sortOptionVO" param="sortOptionVO" scope="request" />
		</dsp:oparam>
	</dsp:droplet>
	</c:if>
	
	<c:if test="${fn:contains(requestURIWithQueryString,'categoryPage')}">
	<dsp:droplet name="CategoryLandingDroplet">
		<dsp:param param="categoryId" name="id" />
		<dsp:param name="siteId" value="${siteId}"/>
		<dsp:param name="catFlg" value="${catFlg}"/>
		<dsp:param name="subCatPlp" value="${subCatPlp}"/>
		<dsp:param name="fetchSubCategories" value="false"/>
		<dsp:oparam name="subcat">
			<dsp:getvalueof var="categoryVO" param="categoryVO" scope="request" />
		</dsp:oparam>
	</dsp:droplet>
	</c:if>
	
	<dsp:droplet name="SearchDroplet">
		<c:if test="${fn:contains(requestURIWithQueryString,'brandsPage')}">
			<dsp:param name="bccSortCode" value="${sortOptionVO.defaultSortingOption.sortUrlParam}"/>
			<dsp:param name="bccSortOrder" value="${categoryVO.sortOptionVO.defaultSortingOption.ascending}"/>
		</c:if>
		
		<c:if test="${fn:contains(requestURIWithQueryString,'categoryPage')}">
			<dsp:param name="CatalogRefId" param="categoryId"/>
			<dsp:param name="CatalogId" param="categoryId"/>
			<dsp:param name="bccSortCode" value="${categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam}"/>
			<dsp:param name="bccSortOrder" value="${categoryVO.sortOptionVO.defaultSortingOption.ascending}"/>
		</c:if>
		
		<c:if test="${fn:contains(requestURIWithQueryString,'searchPage')}">
			<dsp:param name="savedUrl" value="${lastSearchedUrl}"/>
		</c:if>
		
		<c:if test="${localStorePLPFlag}">
			<dsp:param name="storeId" value="${storeIdFromAjax}"/>
			<dsp:param name="onlineTab" value="true"/>
			<dsp:param name="storeIdFromAjax" value="${storeIdFromAjax}"/>
		</c:if>
		
		<dsp:oparam name="output">
			<dsp:getvalueof var="storeInvCount" param="browseSearchVO.bbbProducts.storeInvCount"/>
			<c:choose>
				<c:when test="${storeInvCount != 0}">
					${storeInvCount}
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</dsp:oparam>

	</dsp:droplet>
	
</dsp:page>
