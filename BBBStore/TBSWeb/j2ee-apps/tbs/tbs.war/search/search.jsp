<%-- R2.2 Story - SEO Friendly URL changes --%>
<c:set var="pagNum" value="${param.pagNum}" scope="request" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
<dsp:getvalueof var="queryParameters" bean="/OriginatingRequest.queryString" />
<dsp:getvalueof var="contextRoot" bean="/OriginatingRequest.contextPath"/>

<dsp:page>

		<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
		<c:set var="searchTerm" scope="request"><c:out value="${param.Keyword}"/></c:set>
		<c:set var="brandNameForUrl" scope="request"><c:out value="${param.brandNameForUrl}"/></c:set>
		<c:set var="origSearchTerm" value="${sessionScope.origSearchTerm}" scope="request"/>
		<c:if test="${empty origSearchTerm}"><c:set var="origSearchTerm" value="${sessionScope.origSearchTermDisplay}" scope="request"/></c:if>
		<c:set var="lastSearchedUrl" value="${contextRoot}/s/${searchTerm}" />
		<dsp:getvalueof var="comparisonListItems" bean="ProductComparisonList.items" />
		<input type="hidden" name="fromPage" value="searchPage"/>
		
		<dsp:droplet name="SearchDroplet">
		<dsp:param name="savedUrl" value="${lastSearchedUrl}"/>
		<dsp:param name="comparisonListItems" value="${comparisonListItems}"/>
		<dsp:oparam name="error_PageNumOutOfBound">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
		<dsp:oparam name="output">
		    <dsp:getvalueof var="view" param="view"/>
		    <dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
			<%-- Find out the record type value (asset type is child of record type descriptor)--%>
			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			    <dsp:param name="value" param="browseSearchVO.descriptors"/>
                   <dsp:oparam name="false">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:param name="elementName" value="currentDescriptor" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="descriptorRoot" param="currentDescriptor.rootName" />
							<%-- Only Brand, Attribute & Color are multi select facets. So when selected descriptor is child either of these then show this descriptor as selected checkox--%>
							<c:if test="${(descriptorRoot =='RECORD TYPE')}">
								<dsp:getvalueof var="currentAssetType" param="currentDescriptor.name"/>									
							</c:if>
						</dsp:oparam>
					</dsp:droplet><%-- End ForEach --%>
				</dsp:oparam>
			</dsp:droplet>
			<%-- If currentAssetType is null then search for products--%>
			<dsp:getvalueof var="searchAssetType" value="${currentAssetType}"/>
			<c:set var="product"><bbbl:label key="lbl_search_tab_value_product" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="guide"><bbbl:label key="lbl_search_tab_value_guides" language="${pageContext.request.locale.language}"/></c:set>
			<c:set var="media"><bbbl:label key="lbl_search_tab_value_videos" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="other"><bbbl:label key="lbl_search_tab_value_others" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="guide_tbs">Guide_TBS</c:set>
			
			<%-- Added as part of R2.2 SEO friendly URL Story : Start --%>
			<c:set var="isFilterApplied" value="false" scope="request" />
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="browseSearchVO.descriptors" />
				<dsp:oparam name="outputStart">
					<dsp:getvalueof var="descriptorLength" param="size"/>
				    <dsp:getvalueof var="type" param="element.rootName"/>
				    <c:if test="${(descriptorLength ge 1) && (type ne 'RECORD TYPE')}">
						<c:set var="isFilterApplied" value="true" scope="request" />
				     </c:if>
				</dsp:oparam>
			</dsp:droplet>
			
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<c:set var="Keyword" value="${enteredSearchTerm}" scope="request" />
			<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage" scope="request" />
			<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
			<dsp:getvalueof var="firstPageURL" param="browseSearchVO.pagingLinks.firstPage" scope="request" />
			<dsp:getvalueof var="filterApplied" param="browseSearchVO.pagingLinks.pageFilter" scope="request" />
			<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" scope="request" />
			<dsp:getvalueof var="nextPageParams" param="browseSearchVO.pagingLinks.nextPage" scope="request" />
			<dsp:getvalueof var="prevPageParams" param="browseSearchVO.pagingLinks.previousPage" scope="request" />
			<dsp:getvalueof var="frmBrandPage" param="frmBrandPage" />
			<c:set var="pageTypeForCanonicalURL" value="Search" scope="request" />
			
			<%-- R2.2 story 116. PLP/Grid view --%>
			<c:set var="plpGridSize" scope="request">
				<bbbc:config key="GridSize" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="pageGridClass" value="" scope="request"/>
			<c:set var="gridClass" value="grid_10" scope="request"/>
			<c:set var="facetClass" value="grid_2" scope="request"/>
				
			<c:if test="${plpGridSize == '3'}">
				<c:if test="${view == 'grid' || empty view}">
					<c:set var="pageGridClass" value="plp_view_3" scope="request"/>
				</c:if>
				<c:set var="gridClass" value="grid_9" scope="request"/>
				<c:set var="facetClass" value="grid_3" scope="request"/>
			</c:if>
			
			<c:set var="url" value="${contextPath}/s/${searchTerm}" scope="request" />
			<c:set var="urlPrefixForSuggestion" value="${contextPath}/s/" scope="request" />
			
			<c:set var="searchQueryParams" value="&view=${view}&_dyncharset=UTF-8" scope="request" />
			<c:set var="searchQueryParamsWithoutView" value="&_dyncharset=UTF-8" scope="request" />
			
			<c:set var="searchQueryParamsForSuggestion" value="?view=grid&_dyncharset=UTF-8" scope="request" />
			
			<c:if test="${frmCollegePage eq 'true'}">
				<c:set var="searchQueryParams" value="${searchQueryParams}&frmCollegePage=true" scope="request" />
				<c:set var="searchQueryParamsWithoutView" value="${searchQueryParamsWithoutView}&frmCollegePage=true" scope="request" />
			</c:if>
			<c:if test="${isRedirect eq 'true'}">
				<c:set var="searchQueryParams" value="${searchQueryParams}&isRedirect=true" scope="request" />
				<c:set var="searchQueryParamsWithoutView" value="${searchQueryParamsWithoutView}&isRedirect=true" scope="request" />
			</c:if>
			<c:if test="${frmBrandPage eq 'true'}">
				<c:set var="pageTypeForCanonicalURL" value="Brand" scope="request" />
				<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet"/>
				<dsp:droplet name="BrandsDroplet">
					<dsp:param name="keywordName" value="${searchTerm}"/>
					<dsp:oparam name="seooutput">
						<dsp:getvalueof var="seoUrl" param="seoUrl" />
						<dsp:getvalueof var="brandName" param="brandName" />
						<c:set var="Keyword" value="${brandName}" scope="request" />
						<c:set var="url" value="${contextPath}${seoUrl}" scope="request" />
					</dsp:oparam>
				</dsp:droplet>
				<c:set var="urlPrefixForSuggestion" value="${contextPath}/brand/" scope="request" />
			</c:if>
			
			<c:url value="${url}" var="url" scope="request" />
			
			<%-- Added as part of R2.2 SEO friendly URL Story : End --%>
			
			<c:if test="${(currentAssetType == null)}">
				<dsp:getvalueof var="searchAssetType" value="Product"/>
			</c:if>
			<dsp:getvalueof var="prodCount" param="browseSearchVO.bbbProducts.bbbProductCount"/>
			<c:choose>
			<c:when test="${not empty  prodCount}">
				<%-- KP COMMENT: Dropping out search types that are not part of TBS UI --%>
				<c:choose>
<%-- 					<c:when test="${searchAssetType eq other}">
					
						<dsp:include page="/search/search_other_results.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />	
							<dsp:param name="Keyword" value="${Keyword}" />	
						</dsp:include>					
					</c:when>
					<c:when test="${searchAssetType eq media}">
					
						<dsp:include page="/search/search_videos.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="Keyword" value="${Keyword}" />	
						</dsp:include>						
					</c:when> --%>
			
					<c:when test="${searchAssetType eq guide_tbs}">
					
						<dsp:include page="/search/search_guide_advice.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="CatalogId" param="CatalogId"/>
							<c:set var="isFilterApplied" value="true" scope="request" />
							<dsp:param name="Keyword" value="${Keyword}" />	
						</dsp:include>
					</c:when>
					<c:otherwise> 
						
						<dsp:include page="/search/search_grid.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="Keyword" value="${Keyword}" />		
							<dsp:param name="origSearchTerm" value="${origSearchTerm}" />					
						</dsp:include>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
					<dsp:include page="/search/search_grid.jsp">
							<dsp:param name="browseSearchVO" param="browseSearchVO" />							
						</dsp:include>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
		<dsp:oparam name="empty">
				<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
				<dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&"/>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&">
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="redirect">
		<dsp:getvalueof param="redirectUrl" var="UrlToRedirect"/>
		<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
			<c:if test="${empty UrlToRedirect && isRedirectToParent eq 'true'}">
				<c:set var="UrlToRedirect" value = "${contextRoot}/s/${searchTerm}"/>
			</c:if>
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="${UrlToRedirect}"/>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="unmetSearchCriteria">
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&Keyword=${searchTerm}&">
				<dsp:param name="invalidCriteria" value="true"/>
		</dsp:include>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${TellApartOn}">
	<bbb:tellApart actionType="pv" pageViewType="SearchResult" />		
	</c:if>
	<%-- R2.2 Story 178-a4 Product Comparison page | Start--%>
	<%-- Sets the url of last navigated search page in a user session--%>
	<dsp:setvalue bean="ProductComparisonList.url" value="${url}"/>
	<%-- R2.2 Story 178-a4 Product Comparison page | End--%>
	
	<!-- Find/Change Store Form jsps-->	
	<c:import url="../_includes/modules/change_store_form.jsp" />
	<c:import url="../selfservice/find_in_store.jsp" />
<!-- 	<script type="text/javascript">
		resx.appid = "${appIdCertona}";
		resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
		resx.customerid = "${userId}";
		resx.Keyword = "${term}";
		resx.pageid = "${pageIdCertona}";
	</script> -->
</dsp:page>