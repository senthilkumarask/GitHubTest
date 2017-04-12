<dsp:page>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
	
	<dsp:importbean	bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ClearFilterRedirectDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" /> 
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<dsp:getvalueof var="categoryId" param="categoryId" />

	
<jsp:useBean id="sessionMap" class="java.util.HashMap" scope="session"/>	

	 <%-- <c:if test="${!(param.keepCache eq 'true')}">
	
	<jsp:useBean id="sessionMap" class="java.util.HashMap" scope="session"/>
	
</c:if> --%>

 <c:if test="${sessCat ne categoryId}">

<c:forEach items="${sessionMap}" var="entry">
<c:set target="${sessionMap}" property="${entry.key}" value="${null}"/> 

</c:forEach>
</c:if> 



	<dsp:getvalueof var="ajaxifyFilters" param="ajaxifyFilters" />
	<%-- <c:if test="${ajaxifyFilters ne 'true'}">

<c:forEach items="${sessionMap}" var="entry">
<c:set target="${sessionMap}" property="${entry.key}" value="${null}"/> 

</c:forEach>
</c:if> --%>
	<dsp:getvalueof var="catFlg" param="catFlg"/>
	<dsp:getvalueof var="subCatPlp" param="subCatPlp" scope="request"/>
	<c:set var="clearFilter" scope="request"><c:out value="${param.clearFilters}"/></c:set>
	<dsp:getvalueof var="inStore" param="inStore" scope="request"/>
	<dsp:getvalueof var="storeIdFromURL" value="${fn:escapeXml(param.storeID)}" scope="request"/>

	<c:set var="localStorePLPFlag" scope="request">
		<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
	</c:set>
<dsp:getvalueof var="openfacet" param="openfacet" />
<dsp:getvalueof var="facetName" param="facetName" />
<dsp:getvalueof var="x" param="x" />
<dsp:getvalueof var="sizeFilter" param="sizeFilter" />
<dsp:getvalueof var="filterName" param="filterName" />
<input type="hidden" name="filtersAlreadyApplied" value="${categoryId}" id="filtersAlreadyApplied"> 

	<c:choose>
	<c:when test="${ajaxifyFilters eq 'true'}">
	<dsp:droplet name="CategoryLandingDroplet">
		<dsp:param param="categoryId" name="id" />
		<dsp:param name="siteId" value="${siteId}"/>
		<dsp:param name="catFlg" value="${catFlg}"/>
		<dsp:param name="subCatPlp" value="false"/>
		<dsp:param name="fetchSubCategories" value="false"/>				
		<dsp:oparam name="subcat">
		  <dsp:getvalueof var="seoUrl" param="categoryVO.seoURL"/>
		    <dsp:getvalueof var="categVO" param="categoryVO"/>
		    <dsp:getvalueof var="bccSortCode" param="categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam"/>
		    <dsp:param name="bccSortOrder" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/>
		    </dsp:oparam>
		    </dsp:droplet>
 		<dsp:droplet name="SearchDroplet">
			<dsp:param name="CatalogId" param="categoryId"/>	
 			<dsp:param name="bccSortCode" value="${bccSortCode}"/>	
 			<dsp:param name="bccSortOrder" value="${bccSortOrder}"/>	
			<dsp:param name="openfacet" value="${openfacet}"/>	
      		 <dsp:oparam name="output">	
	              <dsp:include page="/_includes/modules/ajaxifyFilters_new_plp.jsp" flush="true">
						<dsp:param name="browseSearchVO" param="browseSearchVO"/> 
						<dsp:param name="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
						<dsp:param name="categoryId" value="${categoryId}"/> 
						<dsp:param name="redirectUrl" param="redirectUrl"/> 				
						<dsp:param name="narrowSearchUsed" param="narrowSearchUsed"/> 
						<dsp:param name="size" param="browseSearchVO.pagingLinks.pageSize"/>
							<dsp:param name="openfacet" value="${openfacet}"/>		
							<dsp:param name="facetName" value="${facetName}"/>	
							<dsp:param name="filterName" value="${filterName}"/>		
							<dsp:param name="x" value="${x}"/>		
							<dsp:param name="sizeFilter" value="${sizeFilter}"/>								
				  </dsp:include>
	          </dsp:oparam>
	          <dsp:oparam name="swsempty">	                                
                  <dsp:include page="/_includes/modules/ajaxifyFilters_new_plp.jsp" flush="true">
						<dsp:param name="browseSearchVO" param="browseSearchVO"/> 
						<dsp:param name="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
						<dsp:param name="categoryId" value="${categoryId}"/> 
						<dsp:param name="redirectUrl" param="redirectUrl"/> 				
						<dsp:param name="narrowSearchUsed" value="true"/> 			
				  </dsp:include>
	          </dsp:oparam>
	   </dsp:droplet>  
	</c:when>
	 <c:otherwise>
	
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
	
	<%-- BBBSL-5987 start | display clearance page as PLP--%>
	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
     <c:when test="${currentSiteId == BedBathCanadaSite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${currentSiteId == BedBathUSSite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${currentSiteId == BuyBuyBabySite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	</c:choose>
	<c:if test="${clearanceCategory eq categoryId and empty subCatPlp}">
		<c:set var="subCatPlp" value="true" scope="request"/>
	</c:if>
	<%-- BBBSL-5987 end  --%>
	
	<dsp:droplet name="CategoryLandingDroplet">
		<dsp:param param="categoryId" name="id" />
		<dsp:param name="siteId" value="${siteId}"/>
		<dsp:param name="catFlg" value="${catFlg}"/>
		<dsp:param name="subCatPlp" value="${subCatPlp}"/>
		<dsp:param name="fetchSubCategories" value="false"/>
		
		<dsp:oparam name="output">
			<dsp:include page="category_landing.jsp" flush="true">
				<dsp:param name="categoryId" param="categoryId"/>
				<dsp:param name="landingTemplateVO" param="landingTemplateVO"/>
				<dsp:param name="categoryL1" param="categoryL1" />
				<dsp:param name="categoryL2" param="categoryL2" />
				<dsp:param name="categoryL3" param="categoryL3" />
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="subcat">
		  <dsp:getvalueof var="seoUrl" param="categoryVO.seoURL"/>
		    <dsp:getvalueof var="categVO" param="categoryVO"/>
	          <c:if test = "${clearFilter}">
		          <dsp:droplet name="ClearFilterRedirectDroplet">
		          	<dsp:param name="url" value="${seoUrl}" />
		          </dsp:droplet>
	          </c:if>
	
			<dsp:droplet name="SearchDroplet">
				<dsp:param name="CatalogRefId" param="categoryId"/>
				<dsp:param name="CatalogId" param="categoryId"/>
				<dsp:param name="bccSortCode" param="categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam"/>
				<dsp:param name="bccSortOrder" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/>
				<dsp:param name="bccSortOrder" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/>
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
				<dsp:getvalueof value="${fn:escapeXml(param.swsterms)}" var="swsterms" scope="request"/>
				
				<dsp:include page="subcategory_container.jsp" flush="true">
					<dsp:param name="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
					<dsp:param name="size" param="browseSearchVO.pagingLinks.pageSize"/> 
					<dsp:param name="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
					<dsp:param name="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
					<dsp:param name="browseSearchVO" param="browseSearchVO" />
					<dsp:param name="CatalogRefId" param="categoryId"/>
					<dsp:param name="CatalogId" param="categoryId"/>
					<dsp:param name="categoryVO" param="categoryVO"/>
					<dsp:param name="fromCatPage" value="true"/>
					<dsp:param name="filtersonLoad" param="filtersonLoad"/> 			
				</dsp:include>
				</dsp:oparam>
				<%--Start: BPS-1952 | Search Within Search | Null Search Page for SWS --%>		
				<dsp:oparam name="swsempty">
				<dsp:getvalueof	var="enteredSearchTerm" param="enteredSearchTerm" scope="request" />
				<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
				<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
				<dsp:param name="browseSearchVO" param="browseSearchVO" />
				<dsp:param name="swsTermsList" param="swsTermsList" />
				<dsp:param name="CatalogRefId" param="categoryId"/>
				<dsp:getvalueof value="${fn:escapeXml(param.swsterms)}" var="swsterms" scope="request"/>
				<c:choose>
					<c:when test="${categVO.bccManagedPromoCategoryVO.showFilters eq true}">			 
					<dsp:include page="/search/sws_no_search_results_new_plp.jsp?_dyncharset=UTF-8&">
							<dsp:param name="categoryId" param="categoryId"/>
							<dsp:param name="url" value="${seoUrl}"/>
							<dsp:param name="swsTermsList" param="swsTermsList" />
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
						</dsp:include>
					</c:when>
					<c:otherwise>
						<dsp:include page="/search/sws_no_search_results.jsp?_dyncharset=UTF-8&"/>
					</c:otherwise>
				</c:choose>
				</dsp:oparam>
				<%--End: BPS-1952 | Search Within Search | Null Search Page for SWS --%>	
				<dsp:oparam name="redirect">
				<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
					<c:if test="${isRedirectToParent eq 'true'}">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="${contextPath}${seoUrl}" />
						</dsp:droplet>
					</c:if>
				</dsp:oparam>	
				<dsp:oparam name="empty">
				   <dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
				 <dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&" flush="true"  />
				</dsp:oparam>
				<dsp:oparam name="error">
					<%-- instead of showing plain old error message, redirecting the user to a more meaningful server error page
					<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
					<div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
					--%>
					<dsp:include page="../global/serverError.jsp" flush="true"/>
				</dsp:oparam>
			  	<dsp:oparam name="error_PageNumOutOfBound">
			        <dsp:include page="../404.jsp" flush="true"/>
		        </dsp:oparam> 
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
	</dsp:droplet>
	</c:otherwise>
	</c:choose>
</dsp:page>