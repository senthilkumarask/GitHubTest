<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="pageSectionValue" param="pageSectionValue"/>
<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/>
<dsp:getvalueof var="storeInvCount" param="browseSearchVO.bbbProducts.storeInvCount"/>
<c:set var="inStoreParam" value=""/>
<c:set var="storeIdParam" value="" />
<c:set var="storeIdParamForSortBy" value="" />
<c:if test="${inStore == 'true'}">
	<c:set var="inStoreParam" value="&inStore=true"/>
</c:if>
<c:if test="${not empty storeIdFromURL}">
	<c:set var="storeIdParam" value="/store-${storeIdFromURL}/"/>
</c:if>
<dsp:getvalueof var="swsterms" value="${fn:escapeXml(param.swsterms)}"/>
<dsp:getvalueof var="isFromSWD" value="${fn:escapeXml(param.isFromSWD)}"/>
<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
<c:set var="filterAppliedWithPrefix" value="/${filterApplied}"></c:set>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
</c:if>
        <c:if test="${not empty narrowDown}">
			
           <c:set var="url" value="${url}/${narrowDown}"  scope="request"></c:set>
           </c:if>
<c:choose>
		<c:when test="${not empty isFromSWD && isFromSWD eq 'true'}">
		    <c:set var="swdFlag" value="&isFromSWD=true"/>
		</c:when>
		<c:otherwise>
			 <c:set var="swdFlag" value="&isFromSWD=false"/>
		</c:otherwise>
</c:choose>
<%-- R2.2 Story - SEO Friendly URL changes : End --%>
<dsp:page>

<%-- Retrieving Vendor Parameter for Vendor Story --%>
<%@ include file="getVendorParam.jsp"%>
	
	<div class="pagination">
	
		<form id="frmPagination2" method="post" action="#">
			<fieldset class="pagGroupings clearfix">
				<legend class="offScreen"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /></legend>
			
				<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
				<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
				<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
				<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
				<dsp:getvalueof var="searchView" param="view"/>
				<c:set var="searchViewParam" value="&view=${searchView}"/>
				
				<%-- search tabs --%>
				<dsp:include page="/search/search_tabs.jsp">
						<dsp:param name="searchAssetType" value="${searchAssetType}"/>
						<dsp:param name="browseSearchVO" param="browseSearchVO" />	
						<dsp:param name="Keyword" param="Keyword"/>
				</dsp:include>
				<!-- <span id="lblpagViewMode" class="txtOffScreen"><bbbl:label key="lbl_page_mode" language ="${pageContext.request.locale.language}"/></span>-->
				<div class="searchViewMode clearfix" id="pagViewMode" aria-labelledby="lblpagViewMode">
				<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
				<%--92 F Story - SEO Friendly URL changes --%>
					<c:choose>
							<c:when test="${empty browseSearchVO.partialSearchResults || fn:containsIgnoreCase(partialFlag, 'true') }">
					<%-- R2.2 Story - SEO Friendly URL changes --%>
				         <c:choose>
								<c:when test="${searchView eq 'list'}">
								    <a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${vendorParam}${inStoreParam}${swdFlag}" class="defaultView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 4x4"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${vendorParam}${inStoreParam}${swdFlag}" class="gridView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 3x3"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${vendorParam}${inStoreParam}${swdFlag}" class="listView active dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="List View"><bbbl:label key="lbl_view_options_2" language="${pageContext.request.locale.language}" /></a>
								</c:when>
								<c:when test="${searchView eq 'grid4'}">
								    <a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${vendorParam}${inStoreParam}${swdFlag}" class="defaultView active dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 4x4"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${vendorParam}${inStoreParam}${swdFlag}" class="gridView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 3x3"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${vendorParam}${inStoreParam}${swdFlag}" class="listView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="List View"><bbbl:label key="lbl_view_options_2" language="${pageContext.request.locale.language}" /></a>
								</c:when>
								<c:otherwise>
								    <a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${vendorParam}${inStoreParam}${swdFlag}" class="defaultView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 4x4"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${vendorParam}${inStoreParam}${swdFlag}" class="gridView active dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Grid View 3x3"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></a>
									<a href="${url}${filterAppliedWithPrefix}/${currentPage}-${size}${storeIdParam}?<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${vendorParam}${inStoreParam}${swdFlag}" class="listView dynFormSubmit" 
										data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="List View"><bbbl:label key="lbl_view_options_2" language="${pageContext.request.locale.language}" /></a>
								</c:otherwise>
							</c:choose>
							</c:when>
					</c:choose>
				</div>
			</fieldset>
			<c:if test="${localStorePLPFlag}">
			<c:set var="lblInStoreOnly"><bbbl:label key="lbl_In_Store_Only" language="${pageContext.request.locale.language}"/></c:set>
			<input id="currentPage" type="hidden" value="${currentPage}">
			<input id="currentSize" type="hidden" value="${size}">
			<input id="currentPageAndSize" type="hidden" value="${currentPage}-${size}">
			<input id="lblInStoreOnly" type="hidden" value="${lblInStoreOnly}">
			<c:if test="${pageSectionValue == 'topOfResultSet'}">
				<div id="onlineORinStoreChoice" class="pagControls clearfix">
				<fieldset class="checkboxGroup flatform">
							<input id="currentStoreIDtoJS" type="hidden" value="">
							<c:choose>
								<c:when test="${inStore == 'true'}">
								<div class="input_wrap checkboxSingle">
									<input id="inStoreCheckbox" type="checkbox" checked="checked" aria-checked="true" aria-label="${lblInStoreOnly}" />
									<label id="lblInStoreOnly" class="bold inStoreLbl" for="inStoreCheckbox">${lblInStoreOnly}<span id="storeInvCountFromJsp"> (${count})</span></label>
								</div>
								</c:when>
								<c:otherwise>
								<div class="input_wrap checkboxSingle">
									<input id="inStoreCheckbox" type="checkbox" value="inStore" aria-checked="false" aria-label="${lblInStoreOnly}" />
									<label id="lblInStoreOnly" class="bold inStoreLbl" for="inStoreCheckbox">${lblInStoreOnly}<span id="storeInvCountFromAjax"></span></label>
								</div>
								</c:otherwise>
							</c:choose>    
						<section class="storeDetails"></section>
				</fieldset>
				</div>
			</c:if>
			</c:if>
			<fieldset class="pagControls clearfix">
				<legend class="offScreen"><bbbl:label key="lbl_pagination" language="${pageContext.request.locale.language}" /></legend>
				<div class="pagSort">
					<ul class="pagLevel1UL">
						<li class="listCount noPadLeft">
							<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize"/> 
							<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
							<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
							<c:set var="pageSize" value="1-${size}"/>
							<c:set var="lowerRange" value="${1 + (size * (currentPage - 1 ))}"/>
							<c:set var="upperRange" value="${size * currentPage}"/>
							<strong><bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}" />&nbsp;
								<c:choose>
									<c:when test="${count > size}">
										<c:if test="${currentPage eq pageCount}">
											<c:set var="upperRange" value="${count}"/>
										</c:if>
										${lowerRange} - ${upperRange}
									</c:when>
									<c:otherwise>
										<c:out value="${count}" />
									</c:otherwise>
								</c:choose>
							</strong>
							<span><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof
									param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
							</span>
							<h3 class="txtOffScreen">
								<bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}" />&nbsp;
									<c:choose>
										<c:when test="${count > size}">
											<c:if test="${currentPage eq pageCount}">
												<c:set var="upperRange" value="${count}"/>
											</c:if>
											${lowerRange} - ${upperRange}
										</c:when>
										<c:otherwise>
											<c:out value="${count}" />
										</c:otherwise>
									</c:choose>
								&nbsp;<bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;
								<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
							</h3>
						</li>
						<script>BBB.addPerfMark('ux-primary-content-displayed');</script>
						<li class="listSort">
							<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
							<strong><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></strong>&nbsp; 
							<label class="txtOffScreen" for="pagSortOptTop2"><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></label>
							<select	class="sortOptions dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
								id="pagSortOptTop2" name="pagSortOpt" aria-required="false" aria-live="assertive" aria-hidden="false">
								<c:set var="option1"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${(pagSortOpt == null)}">
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></option>
									</c:when>
									<c:otherwise>
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option1}')"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></option>
									</c:otherwise>
								</c:choose>
								<c:set var="option5"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
								 <c:when test="${defaultUserCountryCode ne 'MX'}">
									<c:choose>
										<c:when test="${ (pagSortOpt == 'Price-0')}">
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
										</c:when>
										<c:otherwise>
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option5}')"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${ (pagSortOpt == 'Price-1')}">
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
										</c:when>
										<c:otherwise>
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option6}')"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${ (pagSortOpt == 'PriceMX-0')}">
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=PriceMX-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
										</c:when>
										<c:otherwise>
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=PriceMX-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option5}')"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
										</c:otherwise>
									</c:choose>
									<c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
									<c:choose>
										<c:when test="${ (pagSortOpt == 'PriceMX-1')}">
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=PriceMX-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
										</c:when>
										<c:otherwise>
											<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=PriceMX-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option6}')"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option> 
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
							<c:if test="${currentSiteId ne BedBathCanadaSite}">
								<c:set var="option7"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Ratings-1')}">
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option>
									</c:when>
									<c:otherwise>
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option7}')"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option> 
									</c:otherwise>
								</c:choose>
								</c:if>
								<c:set var="option9"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Brand-0')}">
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></option>
									</c:when>
									<c:otherwise>
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option9}')"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></option>
									</c:otherwise>
								</c:choose>
								<c:set var="option2"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Date-1')}">
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></option>
									</c:when>
									<c:otherwise>
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option2}')"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></option>
									</c:otherwise>

								</c:choose>
								<%-- Start : Added as part of R2.1 --%>
								<c:set var="option10"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Sales-1')}">
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></option>
									</c:when>
									<c:otherwise>
										<option value="${url}${filterAppliedWithPrefix}/${pageSize}${storeIdParam}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" onclick="javascript:sortTrack('${option10}')"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></option>
									</c:otherwise>
								</c:choose>
								<%-- End : Added as part of R2.1 --%>
							</select>
						</li>
					</ul>
				</div>
				<style>
	                .selector_arrowOnly.focus{
		             border:1px dotted #666 !important;
	                 }
               </style>
				<div class="pagFilter">
				<%-- START : R2.2 Story - 116-D1 & D2 --%>
					<dsp:getvalueof param="isViewAll" var="isViewAll"/>
					<dsp:getvalueof param="dropdownList" var="dropdownList"/>
					<dsp:getvalueof param="pagFilterOpt" id="pageFilterSize" />		
					<c:if test="${not empty pageFilterSize}">
						<c:set var = "size" value= "${pageFilterSize}"></c:set>
					</c:if>
					<%-- R2.2 Story - SEO Friendly URL changes --%>
					<ul class="pagLevel1UL">
						<li class="listFilter"><strong><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></strong>&nbsp; 
							<label  class="txtOffScreen" for="pagFilterOptUp2"><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></label>
							<select	class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
								 id="pagFilterOptUp2" name="pagFilterOpt" aria-required="false" aria-live="assertive" aria-hidden="false">
									<dsp:droplet name="ForEach">
									<dsp:param name="array" param="dropdownList" />
									<dsp:oparam name="output">
									<dsp:getvalueof param="element" id="item" />
									<c:choose>
										<c:when test="${item eq 'View All'}">
											<c:choose>
												<c:when test="${isViewAll eq true}">
													<option value="${url}${filterAppliedWithPrefix}/1-${count}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}/1-${count}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}">${item}</option>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${size eq item && isViewAll eq false}">
													<option value="${url}${filterAppliedWithPrefix}/1-${item}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}/1-${item}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}">${item}</option>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									</dsp:oparam>
									</dsp:droplet>
							<%-- END : R2.2 Story - 116-D1 & D2 --%>
							</select>
						</li>
						<c:if test="${pageCount > 1}">
							<li class="listPageNumbers">
								<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
								<dsp:getvalueof var="seoPageNo" param="pagNum"/>
									
								<c:set var="seoPageNoNext" value="${currentPage+1}-${size}"></c:set>
								<c:set var="seoPageNoPrev" value="${currentPage-1}-${size}"></c:set>
								<c:set var="seoPageCurrent" value="${currentPage}-${size}"></c:set>
									
								<c:set var="seoPageNoFirstPage" value="1-${size}"></c:set>
								<c:set var="seoPageNoSecondPage" value="2-${size}"></c:set>
								<c:set var="seoPageNoThirdPage" value="3-${size}"></c:set>
								<c:set var="seoPageNoLastPage" value="${pageCount}-${size}"></c:set>
								<c:set var="seoPageNoSecondLastPage" value="${pageCount-1}-${size}"></c:set>
								<c:set var="seoPageNoThirdLastPage" value="${pageCount-2}-${size}"></c:set>
								<%-- R2.2 Story - SEO Friendly URL changes : End --%>
								<ul>
									<c:choose>
										<c:when test="${currentPage eq 1 || currentPage eq 2 }">
											<c:if test="${currentPage eq 1}">
												<li class="active"><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
												title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}${storeIdParam}?${firstPageURL}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">${currentPage}</a></li>
											</c:if>
											<c:if test="${currentPage eq 2}">
												<li class="lnkPrevPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
													 title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}${storeIdParam}?${prevPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">&lt;</a>
												</li>
												<li ><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
													 title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}${storeIdParam}?${firstPageURL}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">1</a></li>
												<li class="active"><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
													 title="2" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${searchQueryParams}${vendorParam}${inStoreParam}">${currentPage}</a></li>
											</c:if>
											<c:if test="${pageCount >= '2' && currentPage ne 2}">
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="2" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><bbbl:label key="lbl_page_link_2" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount >= '3'}">
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
													 title="3" href="${url}${filterAppliedWithPrefix}/${seoPageNoThirdPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdPage"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><bbbl:label key="lbl_page_link_3" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount > '3'}">
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
												<li>
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
													 title="<dsp:valueof param="browseSearchVO.pagingLinks.pageCount"/>" href="${url}${filterAppliedWithPrefix}/${seoPageNoLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">
														<dsp:valueof param="browseSearchVO.pagingLinks.pageCount" />
													</a>
												</li>
												<li class="lnkNextPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Next Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}${storeIdParam}?${nextPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">&gt;</a>
												</li>
											</c:if>
										</c:when>
										<c:when test="${pageCount eq currentPage}">
											<c:if test="${currentPage ne 3}">	
												<li class="lnkPrevPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
													title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}${storeIdParam}?${prevPageParams}${searchQueryParams}${inStoreParam}${swdFlag}">&lt;</a>
												</li>
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}${storeIdParam}?${firstPageURL}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a></li>
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage-2}" href="${url}${filterAppliedWithPrefix}/${seoPageNoThirdLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdLast"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage-2}" /></a>
											</li>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondLast"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage}" href="${url}${filterAppliedWithPrefix}/${seoPageCurrent}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage}" /></a>
											</li>
										</c:when>
										<c:otherwise>
											<li class="lnkPrevPage arrow">
												<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}${storeIdParam}?${prevPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">&lt;</a>
											</li>
											<c:if test="${currentPage > '2'}">
												<li>
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}${storeIdParam}?${firstPageURL}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a>
												</li>
												<c:if test="${currentPage ne '3'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
												title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}${storeIdParam}?${prevPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
												 title="${currentPage}" href="${url}${filterAppliedWithPrefix}/${seoPageCurrent}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage}" /></a>
											</li>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
												 title="${currentPage+1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}${storeIdParam}?${nextPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${currentPage+1}" /></a>
											</li>
											<c:if test="${currentPage <= pageCount-2}">
												<c:if test="${(currentPage < pageCount-2)}">
													<li class="ellips">...</li>
												</c:if>
												<li >
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
														title="${pageCount}" href="${url}${filterAppliedWithPrefix}/${seoPageNoLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}"><c:out value="${pageCount}" /></a>
												</li>
											</c:if>
											<li class="lnkNextPage arrow">
												<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
												 title="Next Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}${storeIdParam}?${nextPageParams}${searchQueryParams}${vendorParam}${inStoreParam}${swdFlag}">&gt;</a>
											</li>
										</c:otherwise>
									</c:choose>
								</ul>
							</li>
						</c:if>
					</ul>
					<script>BBB.addPerfMark('ux-secondary-content-displayed');</script>
				</div>
			</fieldset>
			<input type="submit" class="hidden" value="" />
		</form>
	</div>
</dsp:page>
