
<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="facets" param="browseSearchVO.facets" />
	<dsp:getvalueof var="requestPath" vartype="java.lang.String" bean="/OriginatingRequest.requestURI" />
	<%--R2.2 Story 116A Changes--%>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO" />
	<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag" />
	<dsp:getvalueof var="swsTermsList" param="swsTermsList" />
	<c:if test="${not empty partialFlag}">
  		<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
  	</c:if>
	<c:set var="inStoreParam" value=""/>
	<c:set var="storeIdParam" value=""/>
	<c:if test="${inStore == 'true'}">
		<c:set var="inStoreParam" value="&inStore=true"/>
	</c:if>
	<c:if test="${not empty storeIdFromURL}">
		<c:set var="storeIdParam" value="/store-${storeIdFromURL}/"/>
	</c:if>
	<c:set var="pageSize" value="1-${size}" />
	<c:set var="lbl_bluepill_remove"><bbbl:label key="lbl_bluepill_remove" language ="${pageContext.request.locale.language}"/></c:set>
	<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
	<dsp:getvalueof var="totSize" value="${fn:length(descriptorsList)}"/>
	<dsp:getvalueof var="totSizeSWS" value="${fn:length(swsTermsList)}"/>
	<dsp:getvalueof var="view" param="view" />
	<dsp:getvalueof var="searchQueryParams" param="searchQueryParams" />
	<c:set var="reqUrl" value="${url}" />
	<dsp:getvalueof var="url" param="url" />
	<dsp:getvalueof var="swdFlag" param="swdFlag" />
	<c:set var="flurl" value="${reqUrl}/"></c:set>

	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<%@ include file="getVendorParam.jsp"%>
	
	<%--R2.2 Defect BED-208 fixed : Start --%>
	<c:set var="urlT" value="${flurl}${pageSize}${storeIdParam}"/>
	<c:if test="${not empty view}">
		<c:set var="urlT" value="${urlT}?view=${view}"/>
	</c:if>
	<div id="searchBox">

		<form id="frmSearchCriteria" method="post">
			<fieldset class="searchGroup">
				
				<div class="searchTitle">
					<c:if test="${totSizeSWS gt 0 ||  totSize gt 1}">
					<h3>
						<bbbl:label key="lbl_search_facet_bar"
							language="${pageContext.request.locale.language}" />
					</h3>
					</c:if>
				</div>

				<div class="searchContent noBorder">

					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="size" param="size" />
							<%-- <dsp:getvalueof var="type" param="element.rootName" /> --%>
							<%-- <c:if test="${type ne 'DEPARTMENT'}">
														<div class="searchListTitle">
															<bbbl:label key="lbl_search_filter_list_header" language="${pageContext.request.locale.language}" />
														</div>
													</c:if> --%>
							<ul class="searchList">
						</dsp:oparam>
						<%-- R2.2 SEO Friendly URL changes --%>
						<dsp:param name="elementName" value="selectedFacetRefItem" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
							<dsp:getvalueof var="selectName" param="selectedFacetRefItem.name" />
							<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
							<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter" />
							<c:choose>
								<c:when test="${type eq 'RECORD TYPE'}">
	
									<dsp:getvalueof var="facetFilter" param="selectedFacetRefItem.descriptorFilter" />
									<dsp:getvalueof var="catIdFilter" param="selectedFacetRefItem.categoryId" />
									<c:set var="filter" value="${catIdFilter}-${facetFilter}"></c:set>
								</c:when>
								<c:otherwise>
									<c:if test="${not empty facetDescFilter}">
										<li class="clearfix"><span class="searchListItem"> <span
												class="searchItem"> <dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" />
											</span> <a 
												href="${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" 
												class="lnkSearchRemove  dynFormSubmit" data-submit-param-length="2"
												data-submit-param1-name="origSearchTerm"
												data-submit-param1-value="${origSearchTerm}"
												data-value="${swsterms}" aria-label="${lbl_bluepill_remove}" role="button" title="${lbl_bluepill_remove}">X</a>
										</span></li>
									</c:if>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>

						<dsp:oparam name="outputEnd">
							<c:forEach var="narrowDownFilter" items="${swsTermsList}">

								<c:if test="${not empty narrowDownFilter}">
									<li class="clearfix"><span class="searchListItem">
											<span class="searchItem"> 
											<c:set var="searchItem" value="${narrowDownFilter.name}" scope="page" ></c:set>
											<c:out value="${searchItem}" escapeXml="true"/>
											</span>
											<c:choose>
												<c:when test="${empty narrowDownFilter.value}">
													<c:set var="narrowDownFilterValue" value=""></c:set>

												</c:when>
												 <c:otherwise>
												<c:set var="narrowDownFilterValue"
													value="${narrowDownFilter.value}/"></c:set>

												</c:otherwise> 
											</c:choose>
											<c:choose>
												<c:when test="${not empty filter}">
												<a 
													href="${flurl}${narrowDownFilterValue}${filter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}"
													class="lnkSearchRemove" title="${lbl_bluepill_remove}"
													data-value="${narrowDownFilter.removalValue}" aria-label="${lbl_bluepill_remove}" role="button">X</a>
													<c:if test="${not empty getPreviousPageLink}">
														<c:set var="previousPage" scope="request">${flurl}${narrowDownFilterValue}${filter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${partialFlagUrl}${inStoreParam}</c:set>
														<c:set var="removalValue" scope="request">${narrowDownFilter.removalValue}</c:set>
													</c:if>
												</c:when>
											 	<c:otherwise>
												<a 
													href="${flurl}${narrowDownFilterValue}${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}"
													class="lnkSearchRemove" aria-label="${lbl_bluepill_remove}" role="button" title="${lbl_bluepill_remove}"
													data-value="${narrowDownFilter.removalValue}">X</a>
													<c:if test="${not empty getPreviousPageLink}">
													    <c:set var="previousPage" scope="request">${flurl}${narrowDownFilterValue}${pageSize}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}</c:set>
													    <c:set var="removalValue" scope="request">${narrowDownFilter.removalValue}</c:set>
													</c:if>
												</c:otherwise>
											</c:choose>
									</span></li>
								</c:if>
							</c:forEach>
							<c:if
								test="${totSizeSWS gt 0 || totSize > 1}">
								<c:set var="clear">
									<bbbl:label key="lbl_search_filter_box_clearall"
										language="${pageContext.request.locale.language}" />
								</c:set>
								<li><a 
									href="${urlT}${partialFlagUrl}&${facetItemRemoveQuery}${vendorParam}${inStoreParam}"
									class="dynFormSubmit lnkSearchReset"
									data-submit-param-length="1"
									data-submit-param1-name="origSearchTerm"
									data-submit-param1-value="${origSearchTerm}" title="${clear}">
										${clear} </a></li>
							</c:if>
							</ul>
						</dsp:oparam>
					</dsp:droplet>

				</div>
			</fieldset>
		</form>

	</div>
	<dsp:getvalueof var="prodCount" param="browseSearchVO.bbbProducts.bbbProductCount" />
	<c:choose>
		<c:when test="${prodCount>=2}">
			<c:set var="displayHideBlock"><bbbl:label key="lbl_block_small" language="${pageContext.request.locale.language}" /></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="displayHideBlock"><bbbl:label key="lbl_none" language="${pageContext.request.locale.language}" /></c:set>
		</c:otherwise>
	</c:choose>
	<fieldset class="facetGroup firstGroup narrowSearch" style="display:${displayHideBlock}">
	<legend class="hidden"><bbbl:label key="lbl_narrow_your_search" language="${pageContext.request.locale.language}"/></legend>
		<div class="facetTitle padTop_10">
			<h5 class="noMar noPad">
				<span><bbbl:label key="lbl_narrow_your_search" language="${pageContext.request.locale.language}" /></span>
			</h5>
		</div>
		<div class="facetContent">
			<form method="post" action="#" id="narrowForm">
				<%--  <input type="hidden" name="searchURL" value="${contextPath}/s/"> --%>
				<div class="narrowSearch">
					<input id="additionalKeyword" class="removeDisabled" disabled="disabled" type="text" value=""
						title="narrowDown" name="additionalKeywordsearch"
						placeholder="<bbbl:label key="lbl_narrow_search_within" language="${pageContext.request.locale.language}" />" />
					<input type="submit" class="removeDisabled hidden" disabled="disabled" value="submit" title="narrow">
					<a role="button" title="narrow" aria-label="submit" class="removeDisabled facetSearchLink" href="javascript:void(0);"></a>
					<input type="hidden" name="additionalKeyword" value="" />
					<c:choose>
						<c:when test="${totSize gt 1}">
							<c:set var="urlPath" scope="request"> ${flurl}${param.narrowDown}$swskeyword/${filter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${partialFlagUrl}${vendorParam}${inStoreParam}</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="urlPath"  scope="request"> ${flurl}${param.narrowDown}$swskeyword/${pageSize}${storeIdParam}?${partialFlagUrl}${vendorParam}${inStoreParam}</c:set>
						</c:otherwise>
					</c:choose>
					<input type="hidden" name="url" id="urlPath" value="${urlPath}" />
				</div>
			</form>
			<form method="post" id="hiddenFacetedForm" action="#">
				<input type="hidden" name="keyword" value=""> <input
					type="hidden" name="swsterms" value="${swsterms}"> <input
					type="hidden" name="origSearchTerm" value="${origSearchTerm}">
				<input type="submit" class="hidden" value="">
			</form>
		</div>
	</fieldset>
</dsp:page>