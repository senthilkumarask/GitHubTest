<%-- R2.2 Story - SEO Friendly URL changes --%>
<c:set var="pagNum" value="${param.pagNum}" scope="request" />
<dsp:importbean bean="/com/bbb/search/droplet/TBSSkuUpcSearchDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
<dsp:importbean bean="/atg/userprofiling/Profile" var="profile"/>
<dsp:importbean bean="/atg/multisite/Site" var="site"/>

<dsp:getvalueof var="queryParameters" bean="/OriginatingRequest.queryString" />
<dsp:getvalueof var="contextRoot" bean="/OriginatingRequest.contextPath"/>

<dsp:page>

		<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
		<c:set var="searchTerm" scope="request"><c:out value="${param.keyword}"/></c:set>
		<c:set var="origSearchTerm" value="${param.origSearchTerm}" scope="request"/>
		<c:set var="searchType" scope="request"><c:out value="${param.type}"/></c:set>
		
		<dsp:droplet name="TBSSkuUpcSearchDroplet">
		<dsp:param name="Keyword" value="${searchTerm}"/>
		<dsp:param name="type" value="${searchType}"/>
		<dsp:param name="site" value="${site}"/>
		<dsp:param name="profile" value="${profile}"/>
		
		<dsp:oparam name="output">
			<%-- If currentAssetType is null then search for products--%>
			<dsp:getvalueof var="searchAssetType" value="${currentAssetType}"/>

			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<c:set var="Keyword" value="${enteredSearchTerm}" scope="request" />

			<%-- R2.2 story 116. PLP/Grid view --%>
			<c:set var="plpGridSize" scope="request">
				<bbbc:config key="GridSize" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="pageGridClass" value="" scope="request"/>
			<c:set var="gridClass" value="grid_10" scope="request"/>
				
			<c:if test="${plpGridSize == '3'}">
				<c:if test="${view == 'grid' || empty view}">
					<c:set var="pageGridClass" value="plp_view_3" scope="request"/>
				</c:if>
				<c:set var="gridClass" value="grid_9" scope="request"/>
				<c:set var="facetClass" value="grid_3" scope="request"/>
			</c:if>
						
			<dsp:getvalueof var="prodCount" value="2"/>
			<c:choose>
			<c:when test="${not empty prodCount}">
				<c:choose>
					<c:when test="${view eq 'list' && (searchAssetType == null)}">
					</c:when>
					<c:otherwise> 
						<dsp:include page="/search/search_upc_from_repo.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="bbbProductListVO" param="bbbProductListVO" />
							<dsp:param name="Keyword" value="${Keyword}" />		
							<dsp:param name="searchType" value="${searchType}"/>
							<dsp:param name="resultsList" param="foundSkuUpcNumbers"/>
							<dsp:param name="noResultsList" param="notFoundSkuUpcNumbers"/>
							<dsp:param name="validSkuUpcCount" param="foundSkuUpcCount"/>
							<dsp:param name="invalidSkuUpcCount" param="notFoundSkuUpcCount"/>
						</dsp:include>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
					<dsp:include page="/search/search_upc_from_repo.jsp">
							<dsp:param name="bbbProductListVO" param="bbbProductListVO" />							
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
	
</dsp:page>