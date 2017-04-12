<%-- R2.2 Story - SEO Friendly URL changes --%>
<c:set var="pagNum" value="${param.pagNum}" scope="request" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
<dsp:getvalueof var="queryParameters" bean="/OriginatingRequest.queryString" />
<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
<dsp:getvalueof var="contextRoot" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ClearFilterRedirectDroplet"/>
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
<c:set var="clearFilter" scope="request"><c:out value="${param.clearFilters}"/></c:set>
<dsp:getvalueof var="inStore" param="inStore" scope="request"/>
<dsp:getvalueof var="storeIdFromURL" param="storeID" scope="request"/>
<dsp:getvalueof var="isRedirect" param="isRedirect"/>
<dsp:page>

		<%--BBBSL-8716 | PLP Fixes for BOTs --%>
		 <c:set var="isRobotOn" value="false" scope="request"/> 
		<dsp:droplet name="/atg/repository/seo/BrowserTyperDroplet">
		 <dsp:oparam name="output">
		   <dsp:getvalueof var="browserType" param="browserType"/>
				 <c:set var="isRobotOn"  value="${browserType eq 'robot'}" scope="request"/>                       
		 </dsp:oparam>
		</dsp:droplet>
		
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

		<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
		<c:set var="searchTerm" scope="request"><c:out value="${param.Keyword}"/></c:set>
		<c:set var="narrowDown" scope="request"><c:out value="${param.narrowDown}"/></c:set>
		<c:set var="brandNameForUrl" scope="request"><c:out value="${param.brandNameForUrl}"/></c:set>
		<c:set var="origSearchTerm" value="${param.origSearchTerm}" scope="request"/>
		<c:set var="lastSearchedUrl" value="${contextRoot}/s/${searchTerm}" />
		
		<%-- post value from submitted search from, indicating department dropdown value for Omniture --%>
		<c:set var="deptValue"><dsp:valueof value="${fn:escapeXml(param.deptValue)}"/></c:set>

		<%-- querystring value from search bar type ahead flyout suggestions, for Omniture tracking --%>
		<c:set var="typeAhead" value="${param.ta}" />

		<%--Setting deptValue from param ta, as value is missed in Post to Get Redirect --%>
		<c:if test="${typeAhead !='typeahead' && empty deptValue}">
			<c:set var="deptValue"><dsp:valueof value="${typeAhead}"/></c:set>
		</c:if>

		<c:if test = "${clearFilter}">
	      	<dsp:droplet name="ClearFilterRedirectDroplet">
	        	<dsp:param name="url" value="/s/${searchTerm}" />
	      	</dsp:droplet>
	    </c:if>
		
		<dsp:droplet name="SearchDroplet">
		<dsp:param name="savedUrl" value="${lastSearchedUrl}"/>
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
		<dsp:oparam name="output">
		
		    <dsp:getvalueof var="view" param="view"/>
		    <dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
		    <dsp:getvalueof value="${fn:escapeXml(param.swsterms)}" var="swsterms" scope="request"/>
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
			
			<dsp:getvalueof var="swsTermsList" param="swsTermsList" />
			<%-- Added as part of R2.2 SEO friendly URL Story : Start --%>
			<c:set var="isFilterApplied" value="false" scope="request" />
			<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
			<c:set var="size" value="${fn:length(descriptorsList)}" />
	
			<c:if test="${(size ge 1) && (descriptorsList[size-1].rootName ne 'RECORD TYPE')}">
				<c:set var="isFilterApplied" value="true" scope="request" />
			</c:if>
			
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<%-- HYD-402, HYD-420 Start Encoding the value enteredSearchTerm, and setting in variable origSearchTerm--%>
			<c:if test="${empty origSearchTerm}">
				<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
					<dsp:param name="URL" value="${enteredSearchTerm}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="origSearchTerm" param="encodedURL" scope="request"/>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
			<%-- HYD-402, HYD-420 End --%>

			<c:set var="Keyword" value="${enteredSearchTerm}" scope="request" />
			<c:set var="narrowDown" value="${param.narrowDown}" scope="request" />
			<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage" scope="request" />
			<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
			<dsp:getvalueof var="firstPageURL" param="browseSearchVO.pagingLinks.firstPage" scope="request" />
			<dsp:getvalueof var="filterApplied" param="browseSearchVO.pagingLinks.pageFilter" scope="request" />
			<dsp:getvalueof var="canonicalFilterApplied" param="browseSearchVO.pagingLinks.canonicalPageFilter" scope="request" />
			<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" scope="request" />
			<dsp:getvalueof var="nextPageParams" param="browseSearchVO.pagingLinks.nextPage" scope="request" />
			<dsp:getvalueof var="prevPageParams" param="browseSearchVO.pagingLinks.previousPage" scope="request" />
			<dsp:getvalueof var="frmBrandPage" param="frmBrandPage" />
			<c:set var="pageTypeForCanonicalURL" value="Search" scope="request" />
			
			
			<%-- PS-24710 Start --%>
			<c:set var="count" value="0"/>
			<c:forEach var="descriptor" items="${browseSearchVO.descriptors}">
				<c:if test="${descriptor.rootName ne 'RECORD TYPE' and count lt 4}">
					<c:choose>
						<c:when test="${not empty titleFilterString}">
							<c:set var="titleFilterString" value="${titleFilterString}, ${descriptor.name }"/>
						</c:when>
						<c:otherwise>
							<c:set var="titleFilterString" value="${descriptor.name }"/>
						</c:otherwise>
					</c:choose>
					<c:set var="count" value="${count + 1 }"/>
				</c:if>
			</c:forEach>
			<c:forEach var="narrowDownFilter" items="${swsTermsList}">
				<c:if test="${count lt 4}">
					<c:choose>
						<c:when test="${not empty titleFilterString}">
							<c:set var="titleFilterString"
								value="${titleFilterString}, ${narrowDownFilter.name}" />
						</c:when>
						<c:otherwise>
							<c:set var="titleFilterString" value="${narrowDownFilter.name}" />
						</c:otherwise>
					</c:choose>
					<c:set var="count" value="${count + 1 }" />
				</c:if>
			</c:forEach>
			<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
			<c:choose>
				<c:when test="${ (pagSortOpt == 'Price-0')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Price-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Ratings-1')}">
				<c:if test="${currentSiteId ne BedBathCanadaSite}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
				</c:if>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Brand-0')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Date-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Sales-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${not empty titleFilterString && not empty selectedSortOptionValue}">
					<c:set var="titleFilterString" value="${titleFilterString}, ${selectedSortOptionValue}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty selectedSortOptionValue}">
					<c:set var="titleFilterString" value="${selectedSortOptionValue}"/>
				</c:when>
			</c:choose>
			<%-- PS-24710 End --%>
			
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
				<c:choose>
					<c:when test="${view eq 'list' && (searchAssetType == null || searchAssetType eq product)}">
			 			<dsp:include page="/search/search_list.jsp">
			 				<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="Keyword" value="${Keyword}" />	
							<dsp:param name="narrowDown" value="${narrowDown}" />	
							<dsp:param name="filterString" value="${titleFilterString}" />	
							<dsp:param name="searchDepartment" value="${deptValue}" />								
							<dsp:param name="typeAhead" value="${typeAhead}" />							
						</dsp:include>
					</c:when>
					<c:when test="${searchAssetType eq other}">
						<dsp:include page="/search/search_other_results.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />	
							<dsp:param name="Keyword" value="${Keyword}" />
							<dsp:param name="narrowDown" value="${narrowDown}" />	
							<dsp:param name="searchDepartment" value="${deptValue}" />	
							<dsp:param name="typeAhead" value="${typeAhead}" />
						</dsp:include>					
					</c:when>
					<c:when test="${searchAssetType eq media}">
						<dsp:include page="/search/search_videos.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="Keyword" value="${Keyword}" />
							<dsp:param name="narrowDown" value="${narrowDown}" />	
							<dsp:param name="searchDepartment" value="${deptValue}" />	
							<dsp:param name="typeAhead" value="${typeAhead}" />
						</dsp:include>						
					</c:when>
					<c:when test="${searchAssetType eq guide}">
						<dsp:include page="/search/search_guide_advice.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="CatalogId" param="CatalogId"/>
							<c:set var="isFilterApplied" value="true" scope="request" />
							<dsp:param name="Keyword" value="${Keyword}" />
							<dsp:param name="narrowDown" value="${narrowDown}" />	
							<dsp:param name="searchDepartment" value="${deptValue}" />								
							<dsp:param name="typeAhead" value="${typeAhead}" />
						</dsp:include>
					</c:when>
					<c:otherwise> 
						<dsp:include page="/search/search_grid.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="Keyword" value="${Keyword}" />		
							<dsp:param name="filterString" value="${titleFilterString}" />
							<dsp:param name="narrowDown" value="${narrowDown}" />
							<dsp:param name="searchDepartment" value="${deptValue}" />	
							<dsp:param name="typeAhead" value="${typeAhead}" />
						</dsp:include>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
					<dsp:include page="/search/search_grid.jsp">
							<dsp:param name="browseSearchVO" param="browseSearchVO" />							
							<dsp:param name="narrowDown" value="${narrowDown}" />					
						</dsp:include>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
		<dsp:oparam name="empty">
				<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
					<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
				<dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&">
					<dsp:param name="currentSelectedCat" param="browseSearchVO.currentCatName"/>
				</dsp:include>
			</dsp:oparam>
		<%--Start: BPS-1952 | Search Within Search | Null Search Page for SWS --%>		
		<dsp:oparam name="swsempty">
				<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
				<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
			    <dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
				<dsp:param name="browseSearchVO" param="browseSearchVO" />
				<dsp:param name="swsTermsList" param="swsTermsList" />
				<dsp:include page="/search/sws_no_search_results.jsp?_dyncharset=UTF-8&"/>
		</dsp:oparam>
		<%--End: BPS-1952 | Search Within Search | Null Search Page for SWS --%>	
		
		<dsp:oparam name="error">
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
			<dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&">
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="error_PageNumOutOfBound">
			        <dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
		
		<%-- Retrieving Vendor Parameter for Vendor Story --%>
		<c:set var="vendorKey"><bbbc:config key="VendorParam" configName="VendorKeys"/></c:set>
		<c:if test="${!fn:containsIgnoreCase(vendorKey, 'VALUE NOT FOUND FOR KEY') && not empty vendorKey}">
			<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
			<c:if test="${not empty vendorParam}">
			  <c:set var="vendorParamValue" value="${vendorKey}=${vendorParam}" />
		    </c:if>
	    </c:if>
	    
		<dsp:oparam name="redirect">
		<dsp:getvalueof param="redirectUrl" var="UrlToRedirect"/>
		<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
			<c:if test="${empty UrlToRedirect && isRedirectToParent eq 'true'}">
				<c:set var="UrlToRedirect" value = "${contextRoot}/s/${searchTerm}?${vendorParamValue}"/>
			</c:if>
			<dsp:droplet name="/atg/dynamo/droplet/Redirect">
				<dsp:param name="url" value="${UrlToRedirect}"/>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="unmetSearchCriteria">
			<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
			<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
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
	
	<%-- Find/Change Store Form jsps--%>	
	<c:import url="../_includes/modules/change_store_form.jsp" />
	<c:import url="../selfservice/find_in_store.jsp" />
	
</dsp:page>