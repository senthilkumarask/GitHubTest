<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<%-- R2.2 SEO Friendly URL changes : Start--%>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet"/>
<dsp:getvalueof var="isFromSWD" value="${fn:escapeXml(param.isFromSWD)}"/>
<c:choose>
		<c:when test="${not empty isFromSWD && isFromSWD eq 'true'}">
		    <c:set var="swdFlag" value="&isFromSWD=true"/>
		</c:when>
		<c:otherwise>
			 <c:set var="swdFlag" value="&isFromSWD=false"/>
		</c:otherwise>
</c:choose>
<c:set var="filterAppliedWithPrefix" value="/${filterApplied}"></c:set>
<dsp:getvalueof var="swsterms" value="${fn:escapeXml(param.swsterms)}"/>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
</c:if>
<%-- R2.2 SEO Friendly URL changes : End--%>
<dsp:page>

<%-- Retrieving Vendor Parameter for Vendor Story --%>
<%@ include file="getVendorParam.jsp"%>
	
<div class="pagination">
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof param="showDepartment" var="showDepartment"/>
	<c:set var="searchViewParam" value="&view=${view}"/>
	<form id="frmPaginationBottom" method="post" action="#">
		<fieldset class="pagControls clearfix">
			<legend class="offScreen"><bbbl:label key="lbl_pagination" language="${pageContext.request.locale.language}" /></legend>
					<div class="pagSort">
					<ul class="pagLevel1UL">
						<li class="listCount noPadLeft">
							<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
							<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize"/> 
							<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
							<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
							<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
							<c:set var="pageSize" value="1-${size}"/>
							<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
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
							<span><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;
								<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
							</span>
						</li>
						<li class="listSort">
							<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
							<strong><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></strong>&nbsp; 
							<label class="txtOffScreen" for="pagSortOpt"><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></label>
							<select	class="sortOptions dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
								id="pagSortOptBottom" name="pagSortOpt" aria-required="false" aria-live="assertive" aria-describedby="pagSortOptBottom" >
								<c:choose>
									<c:when test="${showDepartment eq false}">
										<c:set var="option8"><bbbl:label key="lbl_sortby_options_8" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Date-1') || (pagSortOpt == null)}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_8" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option8}')"><bbbl:label key="lbl_sortby_options_8" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
										<c:set var="option5"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
										<c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
												
										<c:choose>
								         <c:when test="${defaultUserCountryCode ne 'MX'}">
												<c:choose>
													<c:when test="${ (pagSortOpt == 'Price-0')}">
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
													</c:when>
													<c:otherwise>
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option5}')"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
													</c:otherwise>
												</c:choose>
												<c:choose>
													<c:when test="${ (pagSortOpt == 'Price-1')}">
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
													</c:when>
													<c:otherwise>
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option6}')"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
													</c:otherwise>
												</c:choose>
										   </c:when>
										    <c:otherwise>
												<c:choose>
													<c:when test="${ (pagSortOpt == 'PriceMX-0')}">
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=PriceMX-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected> <bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}"></bbbl:label></option>
													</c:when>
													<c:otherwise>
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=PriceMX-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option5}')"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
													</c:otherwise>
												</c:choose>
												<c:choose>
													<c:when test="${ (pagSortOpt == 'PriceMX-1')}">
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=PriceMX-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
													</c:when>
													<c:otherwise>
														<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=PriceMX-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option6}')"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
													</c:otherwise>
												</c:choose>
										 </c:otherwise>
										</c:choose>
										<c:set var="option7"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Ratings-1')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option7}')"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:set var="option1"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${(pagSortOpt == null)}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option1}')"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
										<c:set var="option5"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Price-0')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option5}')"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
										<c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Price-1')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option6}')"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></option> 
											</c:otherwise>
										</c:choose>
										<c:set var="option7"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Ratings-1')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option7}')"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
										<c:set var="option9"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Brand-0')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option9}')"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></option> 
											</c:otherwise>
										</c:choose>
										<c:set var="option2"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Date-1')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option2}')"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
											
										</c:choose>
										<%-- Start : Added as part of R2.1 --%>
										<c:set var="option10"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
										<c:choose>
											<c:when test="${ (pagSortOpt == 'Sales-1')}">
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" selected><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${url}${filterAppliedWithPrefix}/${pageSize}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}${vendorParam}${swdFlag}" onclick="javascript:sortTrack('${option10}')"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></option>
											</c:otherwise>
										</c:choose>
										<%-- End : Added as part of R2.1 --%>
									</c:otherwise>
								</c:choose>
							</select>
						</li>
					</ul>
				</div>
				<div class="pagFilter">
				<%-- START : R2.2 Story - 116-D1 & D2 --%>
					<dsp:getvalueof param="isViewAll" var="isViewAll"/>
					<dsp:getvalueof param="dropdownList" var="dropdownList"/>
					<dsp:getvalueof param="pagFilterOpt" id="pageFilterSize" />		
					<c:if test="${not empty pageFilterSize}">
						<c:set var = "size" value= "${pageFilterSize}"></c:set>
					</c:if>
					<ul class="pagLevel1UL">
						<li class="listFilter"><strong><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></strong>&nbsp; 
						
						<label  class="txtOffScreen" for="pagFilterOpt"><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></label>
						<select	class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" id="pagFilterOptBottom" name="pagFilterOpt" aria-required="false" aria-live="assertive" aria-describedby="lblpagFilterOptBottom">
									<dsp:droplet name="ForEach">
									<dsp:param name="array" param="dropdownList" />
									<dsp:oparam name="output">
									<dsp:getvalueof param="element" id="item" />
									<%-- R2.2 SEO Friendly URL changes--%>
									<c:choose>
										<c:when test="${item eq 'View All'}">
											<c:choose>
												<c:when test="${isViewAll eq true}">
													<option value="${url}${filterAppliedWithPrefix}/1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${swdFlag}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}/1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${swdFlag}">${item}</option>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${size eq item && isViewAll eq false}">
													<option value="${url}${filterAppliedWithPrefix}/1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${swdFlag}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}/1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}${partialFlagUrl}${vendorParam}${swdFlag}">${item}</option>
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
							<%-- R2.2 SEO Friendly URL changes : Start--%>
								<dsp:getvalueof var="seoPageNo" param="pagNum"/>
								<dsp:getvalueof var="pagFilterOpt" param="pagFilterOpt"/>
								<c:set var="seoPageNoNext" value="${currentPage+1}-${size}"></c:set>
								<c:set var="seoPageNoPrev" value="${currentPage-1}-${size}"></c:set>
								<c:set var="seoPageCurrent" value="${currentPage}-${size}"></c:set>
									
								<c:set var="seoPageNoFirstPage" value="1-${size}"></c:set>
								<c:set var="seoPageNoSecondPage" value="2-${size}"></c:set>
								<c:set var="seoPageNoThirdPage" value="3-${size}"></c:set>
								<c:set var="seoPageNoLastPage" value="${pageCount}-${size}"></c:set>
								<c:set var="seoPageNoSecondLastPage" value="${pageCount-1}-${size}"></c:set>
								<c:set var="seoPageNoThirdLastPage" value="${pageCount-2}-${size}"></c:set>
							<%-- R2.2 SEO Friendly URL changes : End--%>
								<ul>
									<c:choose>
										<c:when test="${currentPage eq 1 || currentPage eq 2 }">
											<c:if test="${currentPage eq 1}">
												<li class="active"><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}?${firstPageURL}${searchQueryParams}${vendorParam}${swdFlag}">${currentPage}</a></li>
											</c:if>
											<c:if test="${currentPage eq 2}">
												<li class="lnkPrevPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${prevPageParams}${searchQueryParams}${vendorParam}${swdFlag}">&lt;</a>
												</li>
												<li ><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}?${firstPageURL}${searchQueryParams}${vendorParam}${swdFlag}">1</a></li>
												<li class="active"><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="2" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondPage}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${searchQueryParams}${vendorParam}${swdFlag}">${currentPage}</a></li>
											</c:if>
											<c:if test="${pageCount >= '2' && currentPage ne 2}">
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="2" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondPage}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${searchQueryParams}${vendorParam}${swdFlag}"><bbbl:label key="lbl_page_link_2" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount >= '3'}">
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="3" href="${url}${filterAppliedWithPrefix}/${seoPageNoThirdPage}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdPage"/>${searchQueryParams}${vendorParam}${swdFlag}"><bbbl:label key="lbl_page_link_3" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount > '3'}">
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
												<li>
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="<dsp:valueof param="browseSearchVO.pagingLinks.pageCount"/>" href="${url}${filterAppliedWithPrefix}/${seoPageNoLastPage}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${searchQueryParams}${vendorParam}${swdFlag}">
														<dsp:valueof param="browseSearchVO.pagingLinks.pageCount" />
													</a>
												</li>
												<li class="lnkNextPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Next Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}?${nextPageParams}${searchQueryParams}${vendorParam}${swdFlag}">&gt;</a>
												</li>
											</c:if>
										</c:when>
										<c:when test="${pageCount eq currentPage}">
											<c:if test="${currentPage ne 3}">	
												<li class="lnkPrevPage arrow">
													<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${prevPageParams}${searchQueryParams}${vendorParam}${swdFlag}">&lt;</a>
												</li>
												<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}?${firstPageURL}${searchQueryParams}${vendorParam}${swdFlag}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a></li>
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage-2}" href="${url}${filterAppliedWithPrefix}/${seoPageNoThirdLastPage}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdLast"/>${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage-2}" /></a>
											</li>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoSecondLastPage}?<dsp:valueof param="browseSearchVO.pagingLinks.secondLast"/>${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage}" href="${url}${filterAppliedWithPrefix}/${seoPageCurrent}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage}" /></a>
											</li>
										</c:when>
										<c:otherwise>
											<li class="lnkPrevPage arrow">
												<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Previous Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${prevPageParams}${searchQueryParams}${vendorParam}${swdFlag}">&lt;</a>
											</li>
											<c:if test="${currentPage > '2'}">
												<li>
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="1" href="${url}${filterAppliedWithPrefix}/${seoPageNoFirstPage}?${firstPageURL}${searchQueryParams}${vendorParam}${swdFlag}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a>
												</li>
												<c:if test="${currentPage ne '3'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${prevPageParams}${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage}" href="${url}${filterAppliedWithPrefix}/${seoPageCurrent}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage}" /></a>
											</li>
											<li>
												<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${currentPage+1}" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}?${nextPageParams}${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${currentPage+1}" /></a>
											</li>
											<c:if test="${currentPage <= pageCount-2}">
												<c:if test="${(currentPage < pageCount-2)}">
													<li class="ellips">...</li>
												</c:if>
												<li >
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="${pageCount}" href="${url}${filterAppliedWithPrefix}/${seoPageNoLastPage}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${searchQueryParams}${vendorParam}${swdFlag}"><c:out value="${pageCount}" /></a>
												</li>
											</c:if>
											<li class="lnkNextPage arrow">
												<a class="dynFormSubmit pagArrow" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" title="Next Page" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}?${nextPageParams}${searchQueryParams}${vendorParam}${swdFlag}">&gt;</a>
											</li>
										</c:otherwise>
									</c:choose>
								</ul>
							</li>
						</c:if>
					</ul>
				</div>
			</fieldset>
			<input type="submit" class="hidden" value="" />
	</form>
</div>
</dsp:page>