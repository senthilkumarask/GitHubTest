<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="pageSectionValue" param="pageSectionValue"/>
<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/>
<dsp:getvalueof var="storeInvCount" param="browseSearchVO.bbbProducts.storeInvCount"/>
<c:set var="inStoreParam" value=""/>
<c:set var="storeIdParam" value=""/>
<c:set var="storeIdParamForSortBy" value="" />
<c:if test="${inStore == 'true'}">
	<c:set var="inStoreParam" value="&inStore=true"/>
</c:if>
<c:if test="${not empty storeIdFromURL}">
	<c:set var="storeIdParam" value="/store-${storeIdFromURL}/"/>
	<c:set var="storeIdParamForSortBy" value="/store-${storeIdFromURL}/"/>
</c:if>
<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
<c:set var="filterAppliedWithPrefix" value="${filterApplied}/"></c:set>
<c:set var="filterAppliedWithoutPrefix" value="${filterApplied}"></c:set>
<dsp:getvalueof var="swsterms" value="${fn:escapeXml(param.swsterms)}"/>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
	<c:set var="filterAppliedWithoutPrefix" value=""></c:set>
	<c:if test="${not empty storeIdFromURL}">
		<c:set var="storeIdParamForSortBy" value="store-${storeIdFromURL}/"/>
	</c:if>
</c:if>
<dsp:getvalueof var="subCategoryPageParam" param="subCategoryPageParam" />
<%-- R2.2 Story - SEO Friendly URL changes : End --%>
<dsp:page>
	<div class="pagination">
	<%-- Start: List/ Grid view options are diplayed on PLP above pagination tab. R2.2 116-g --%>
		<dsp:getvalueof param="view" var="plpView"/>
<%-- R2.2 - Story 116E Start --%>
		<c:choose>
			<c:when test="${not frmBrandPage}">
				<c:set var="url" value="${contextPath}${seoUrl}"/>
			</c:when>
			<c:otherwise>
				<c:set var="url" value="${url}/"/>
			</c:otherwise>
		</c:choose>

		<c:if test="${not empty param.narrowDown}">
           <c:set var="url" value="${url}${param.narrowDown}/" scope="page"></c:set>
           </c:if>
<%-- R2.2 - Story 116E END --%>
		<dsp:getvalueof var="showViewOptions" param="showViewOptions"/>
		<c:set var="plpViewParam" value="&view=${plpView}"/>
		
		<c:set var="lbl_grid_view"><bbbl:label key="lbl_view_options_1" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lbl_list_view"><bbbl:label key="lbl_view_options_2" language="${pageContext.request.locale.language}" /></c:set>
		<form method="post" action="#">
		<c:if test="${showViewOptions}">
		 <script type="text/javascript">
                BBB.subCategoryPaginationInfo = {
                    <c:choose>
                        <c:when test="${plpView == 'list'}">
                            prop25: "List View",
                            eVar47: "List View"
                        </c:when>
                        <c:when test="${plpView == 'grid4'}">
                            prop25: "Grid View-4",
                            eVar47: "Grid View-4"
                        </c:when>
                        <c:otherwise>
                            prop25: "Grid View-3",
                            eVar47: "Grid View-3"
                        </c:otherwise>
                    </c:choose>
                };
            </script>       
			 <div class="pagGroupings clearfix">
				<!-- <span id="lblpagViewMode" class="txtOffScreen"><bbbl:label key="lbl_page_mode" language ="${pageContext.request.locale.language}"/></span>-->
				<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
				<div id="pagViewMode" class="searchViewMode clearfix" aria-labelledby="lblpagViewMode">
				  <span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
					<c:choose>
						<c:when test="${plpView == 'list'}">
						     <a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${inStoreParam}" aria-selected="false" class="defaultView  redirPage dynFormSubmit" title="${lbl_grid_view}" aria-label="Product Grid View - 4 Across" >${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${inStoreParam}" aria-selected="false" class="gridView redirPage dynFormSubmit" title="${lbl_grid_view}" aria-label="Product Grid View - 3 Across">${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${inStoreParam}" class="listView active redirPage dynFormSubmit" aria-selected="true" aria-label="Product List View" title="${lbl_list_view}">${lbl_list_view}</a>
						</c:when>
						<c:when test="${plpView == 'grid4'}">
						     <a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${inStoreParam}" aria-label="Product Grid View - 4 Across" class="defaultView active redirPage dynFormSubmit" aria-selected="true" title="${lbl_grid_view}">${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${inStoreParam}" aria-label="Product Grid View - 3 Across" aria-selected="false" class="gridView redirPage dynFormSubmit" title="${lbl_grid_view}">${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${inStoreParam}" aria-label="Product List View" aria-selected="false" class="listView  redirPage dynFormSubmit" title="${lbl_list_view}">${lbl_list_view}</a>
						</c:when>
						<c:otherwise>
						     <a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid4${inStoreParam}" aria-label="Product Grid View - 4 Across" aria-selected="false" class="defaultView redirPage dynFormSubmit" title="${lbl_grid_view}">${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=grid${inStoreParam}" aria-selected="true" aria-label="Product Grid View - 3 Across"  class="gridView active redirPage dynFormSubmit" title="${lbl_grid_view}">${lbl_grid_view}</a>
							<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" href="${url}${filterAppliedWithPrefix}${currentPage}-${size}${storeIdParam}?${subCategoryPageParam}<dsp:valueof param="browseSearchVO.searchQuery"/>${searchQueryParamsWithoutView}&view=list${inStoreParam}" aria-selected="false" class="listView redirPage dynFormSubmit" title="${lbl_list_view}" aria-label="Product List View">${lbl_list_view}</a>
						</c:otherwise>
					</c:choose>
				</div>
			</div> 
			
			</c:if>
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
			<%-- End: List/ Grid view options are diplayed on PLP above pagination tab. R2.2 116-g --%>
			<fieldset class="pagControls clearfix">
				<legend class="offScreen"></legend>
				<div class="pagSort">
					<ul class="pagLevel1UL">
						<li class="listCount noPadLeft">
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
						</li>
						<script>BBB.addPerfMark('ux-primary-content-displayed');</script>
						<c:choose>
			<%-- R2.2 - Story 116E Start --%>
							 <c:when test="${frmBrandPage }">
									<li class="listSort">
									<strong><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></strong>&nbsp;
									<dsp:getvalueof var="defSortCode" value="${sortOptionVO.defaultSortingOption.sortUrlParam }"/> 
									<dsp:getvalueof var="defSortValue" value="${sortOptionVO.defaultSortingOption.sortValue}"/> 
									<dsp:getvalueof var="defSortIsAscend" value="${sortOptionVO.defaultSortingOption.ascending}"/> 
									<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
							
									<c:set var="boostDefault"><bbbc:config key="boost_bury_sort_order" configName='DimNonDisplayConfig'/></c:set>							
									<c:if test="${empty pagSortOpt}">
										<c:choose>
											<c:when test="${not empty defSortCode}">
												<c:choose>
													<c:when test="${defSortIsAscend == 1}">
													   <c:set var="pagSortOpt" value="${defSortCode}-1"/>
													 
														 
													</c:when>
													<c:otherwise>
													<c:set var="pagSortOpt" value="${defSortCode}-0"/>
														 
													</c:otherwise>
												</c:choose>
											</c:when>
											
										</c:choose>		
									</c:if>
							<label class="txtOffScreen" for="pagSortOptTop1"><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></label>
							<select	class="sortOptions redirPage dynFormSubmit" id="pagSortOptTop1" name="pagSortOpt" aria-required="false" aria-live="assertive" data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" >
								  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" value="${sortOptionVO.sortingOptions}" />
									
									<dsp:oparam name="outputStart">
										<c:if test="${not empty defSortCode && not empty boostDefault}">
											<c:choose>
												<c:when test="${defSortCode eq boostDefault}">		
													<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:otherwise>
											</c:choose>
										</c:if>	
                                     </dsp:oparam>	
									 
									<dsp:oparam name="output">
										<dsp:getvalueof var="sortOption" param="element"/>
										<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">	
										<c:choose>
											<c:when test="${sortOption.ascending == 1}">
													<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />	
													<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX' ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price' )}">							
												  <c:choose>
													<c:when test="${pagSortOpt == currentSortCode}">
														<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${inStoreParam}" >${sortOption.sortValue}</option>
													</c:when>
													<c:otherwise>
													<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
													</c:otherwise>
												</c:choose>	
												</c:if>	
											</c:when>
											<c:otherwise>
												<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
												<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX' ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price' )}">							
												<c:choose>
													<c:when test="${pagSortOpt == currentSortCode}">
														<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${inStoreParam}" >${sortOption.sortValue}</option>
													</c:when>
													<c:otherwise>
														<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
													</c:otherwise>
												</c:choose>	
												</c:if>
											</c:otherwise>
										</c:choose>
											</c:if>	
									</dsp:oparam>
								</dsp:droplet>
							</select>
						</li>
							</c:when>
<%-- R2.2 - Story 116E End --%>
				<%-- BBBJ-1220 | Checklist category page start--%>
					<c:when	test="${isFromChecklistCategory}" >
						<li class="listSort">
							<strong><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></strong>&nbsp;
							<dsp:getvalueof var="defSortCode" value="${sortOptions.defaultSortingOption.sortUrlParam}"/> 
							<dsp:getvalueof var="defSortValue" value="${sortOptions.defaultSortingOption.sortValue}"/> 
							<dsp:getvalueof var="defSortIsAscend" value="${sortOptions.defaultSortingOption.ascending}"/> 
							<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" />
							
							<%-- BBBSL-4250 start - if pagSortOpt is not recieved in param, then retrieve it from session--%>
							<c:if test="${empty pagSortOpt }">
								<c:set var="pagSortOpt" value="${pagSortOptSess}"/>
							</c:if>
							<%-- BBBSL-4250 end --%>
							<c:set var="boostDefault"><bbbc:config key="boost_bury_sort_order" configName='DimNonDisplayConfig'/></c:set>							
							<c:if test="${empty pagSortOpt}">
								<c:choose>
									<c:when test="${not empty defSortCode}">
										<c:choose>
											<c:when test="${defSortIsAscend == 1}">
											   <c:set var="pagSortOpt" value="${defSortCode}-1"/>
											 
												 
											</c:when>
											<c:otherwise>
											<c:set var="pagSortOpt" value="${defSortCode}-0"/>
												 
											</c:otherwise>
										</c:choose>
									</c:when>
									
								</c:choose>		
							</c:if>
							<label class="txtOffScreen" for="pagSortOpt"><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></label>
							
							<dsp:getvalueof var="sortOptions" value="${sortOptions}" /> 
								<select class="sortOptions redirPage dynFormSubmit" name="pagSortOpt" aria-required="false" aria-describedby="pagSortOpt" aria-live="assertive" data-submit-param-length="1" >
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="sortOptions.sortingOptions" />
									<dsp:oparam name="outputStart">
										<c:if test="${not empty defSortCode && not empty boostDefault}">
											<c:choose>
												<c:when test="${defSortCode eq boostDefault}">	
													<option selected value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:otherwise>
											</c:choose>
										</c:if>
									</dsp:oparam>
									
									<dsp:oparam name="output">
										<dsp:getvalueof var="sortOption" param="element"/>
										<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">		
											<c:choose>
												<c:when test="${sortOption.ascending == 1}">
														<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />	
														<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX'  ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price')}">							
															<c:choose>
																<c:when test="${pagSortOpt == currentSortCode}">
																	<option selected value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" >${sortOption.sortValue}</option>
																</c:when>
																<c:otherwise>
																<option value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
																</c:otherwise>
															</c:choose>		
														</c:if>							
												</c:when>
												<c:otherwise>
													<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
													<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX'  ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price')}">							
															<c:choose>
															<c:when test="${pagSortOpt == currentSortCode}">
						
																<option selected value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" >${sortOption.sortValue}</option>
															</c:when>
															<c:otherwise>
																<option value="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
															</c:otherwise>
														</c:choose>	
													</c:if>
												</c:otherwise>
											</c:choose>
										</c:if>	
									</dsp:oparam>
								</dsp:droplet>
								</select>
							
						</li>
					</c:when>
							<%-- BBBJ-1220 end --%>
							<c:otherwise>
								<li class="listSort">
									<strong><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></strong>&nbsp;
									<dsp:getvalueof var="defSortCode" param="categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam"/> 
									<dsp:getvalueof var="defSortValue" param="categoryVO.sortOptionVO.defaultSortingOption.sortValue"/> 
									<dsp:getvalueof var="defSortIsAscend" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/> 
									<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
							
							<%-- BBBSL-4250 start - if pagSortOpt is not recieved in param, then retrieve it from session--%>
							<c:if test="${empty pagSortOpt }">
								<c:set var="pagSortOpt" value="${pagSortOptSess}"/>
							</c:if>
							<%-- BBBSL-4250 end --%>
							
							<c:set var="boostDefault"><bbbc:config key="boost_bury_sort_order" configName='DimNonDisplayConfig'/></c:set>							
							<c:if test="${empty pagSortOpt}">
								<c:choose>
									<c:when test="${not empty defSortCode}">
										<c:choose>
											<c:when test="${defSortIsAscend == 1}">
											   <c:set var="pagSortOpt" value="${defSortCode}-1"/>
											 
												 
											</c:when>
											<c:otherwise>
											<c:set var="pagSortOpt" value="${defSortCode}-0"/>
												 
											</c:otherwise>
										</c:choose>
									</c:when>
									
								</c:choose>		
							</c:if>
							<label class="txtOffScreen" for="pagSortOpt"><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></label>
							<select	class="sortOptions redirPage dynFormSubmit" name="pagSortOpt" aria-required="false" aria-live="assertive" data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" >
								  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="categoryVO.sortOptionVO.sortingOptions" />
									
									<dsp:oparam name="outputStart">
										<c:if test="${not empty defSortCode && not empty boostDefault}">

											<c:choose>
											
												<c:when test="${defSortCode eq boostDefault}">		
												
																							
													<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${boostDefault}-0${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('Select')"><bbbl:label key="lbl_select_default" language="${pageContext.request.locale.language}" /> </option>
												</c:otherwise>
											</c:choose>
										</c:if>	
                                     </dsp:oparam>	
									 
									<dsp:oparam name="output">
								
										<dsp:getvalueof var="sortOption" param="element"/>
										<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">		
										<c:choose>
											<c:when test="${sortOption.ascending == 1}">
													<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />	
													<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX'  ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price')}">							
				                                        <c:choose>
															<c:when test="${pagSortOpt == currentSortCode}">
																<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" >${sortOption.sortValue}</option>
															</c:when>
															<c:otherwise>
															<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
															</c:otherwise>
														</c:choose>		
													</c:if>							
											</c:when>
											<c:otherwise>
												<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
												<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX'  ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price')}">							
				                                       	<c:choose>
														<c:when test="${pagSortOpt == currentSortCode}">
					
															<option selected value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" >${sortOption.sortValue}</option>
														</c:when>
														<c:otherwise>
															<option value="${url}${filterAppliedWithoutPrefix}${storeIdParamForSortBy}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}${subCategoryPageParam}${inStoreParam}" onclick="javascript:sortTrack('${sortOption.sortValue}')">${sortOption.sortValue}</option>
														</c:otherwise>
													</c:choose>	
												</c:if>
											</c:otherwise>
										</c:choose>
											</c:if>	
									</dsp:oparam>
								</dsp:droplet>
							</select>
						</li>
							</c:otherwise>
						</c:choose>
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
					<ul class="pagLevel1UL">
						<li class="listFilter"><strong><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></strong>&nbsp; 
							<%-- R2.2 Story - SEO Friendly URL changes--%>
							
						<label  class="txtOffScreen" for="pagFilterOptUp1"><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></label>
						<select	class="redirPage dynFormSubmit" name="pagFilterOpt" aria-required="false" aria-live="assertive" data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" >
									<dsp:droplet name="ForEach">
									<dsp:param name="array" param="dropdownList" />
									<dsp:oparam name="output">
									<dsp:getvalueof param="element" id="item" />
									<c:choose>
										<c:when test="${item eq 'View All'}">
											<c:choose>
												<c:when test="${isViewAll eq true}">
													<option value="${url}${filterAppliedWithPrefix}1-${count}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}1-${count}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">${item}</option>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${size eq item && isViewAll eq false}">
													<option value="${url}${filterAppliedWithPrefix}1-${item}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}" selected="selected">${item}</option>
												</c:when>
												<c:otherwise>
													<option value="${url}${filterAppliedWithPrefix}1-${item}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">${item}</option>
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
								<ul>
									<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
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
									
									<c:choose>
										<c:when test="${currentPage eq 1 || currentPage eq 2 }">
											<c:if test="${currentPage eq 1}">
												<li class="active"><a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="1" href="${url}${filterAppliedWithPrefix}${seoPageCurrent}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">${currentPage}</a></li>
											</c:if>
											<c:if test="${currentPage eq 2}">
												<li class="lnkPrevPage arrow">
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit pagArrow" title="Previous Page" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">&lt;</a>
												</li>
												<li ><a class="redirPage dynFormSubmit" title="1" href="${url}${filterAppliedWithPrefix}${seoPageNoFirstPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">1</a></li>
												<li class="active"><a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="2" href="${url}${filterAppliedWithPrefix}${seoPageNoSecondPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">${currentPage}</a></li>
											</c:if>
											<c:if test="${pageCount >= '2' && currentPage ne 2}">
												<li><a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="2" href="${url}${filterAppliedWithPrefix}${seoPageNoSecondPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><bbbl:label key="lbl_page_link_2" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount >= '3'}">
												<li><a class="redirPage dynFormSubmit" title="3" href="${url}${filterAppliedWithPrefix}${seoPageNoThirdPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><bbbl:label key="lbl_page_link_3" language="${pageContext.request.locale.language}" /></a></li>
											</c:if>
											<c:if test="${pageCount > '3'}">
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
												<li>
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="<dsp:valueof param="browseSearchVO.pagingLinks.pageCount"/>" href="${url}${filterAppliedWithPrefix}${seoPageNoLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">
														<dsp:valueof param="browseSearchVO.pagingLinks.pageCount" />
													</a>
												</li>
												<li class="lnkNextPage arrow">
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit pagArrow" title="Next Page" href="${url}${filterAppliedWithPrefix}${seoPageNoNext}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.nextPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">&gt;</a>
												</li>
											</c:if>
										</c:when>
										<c:when test="${pageCount eq currentPage}">
											<c:if test="${currentPage ne 3}">	
												<li class="lnkPrevPage arrow">
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit pagArrow" title="Previous Page" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">&lt;</a>
												</li>
												<li><a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="1" href="${url}${filterAppliedWithPrefix}${seoPageNoFirstPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a></li>
												<c:if test="${pageCount ne '4'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage-2}" href="${url}${filterAppliedWithPrefix}${seoPageNoThirdLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.thirdLast"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage-2}" /></a>
											</li>
											<li>
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}${seoPageNoSecondLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.secondLast"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage}" href="${url}${filterAppliedWithPrefix}${seoPageCurrent}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage}" /></a>
											</li>
										</c:when>
										<c:otherwise>
											<li class="lnkPrevPage arrow">
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit pagArrow" title="Previous Page" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">&lt;</a>
											</li>
											<c:if test="${currentPage > '2'}">
												<li>
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="1" href="${url}${filterAppliedWithPrefix}${seoPageNoFirstPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><bbbl:label key="lbl_page_link_1" language="${pageContext.request.locale.language}" /></a>
												</li>
												<c:if test="${currentPage ne '3'}">
													<li class="ellips">...</li>
												</c:if>
											</c:if>
											<li>
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage-1}" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage-1}" /></a>
											</li>
											<li class="active">
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage}" href="${url}${filterAppliedWithPrefix}${seoPageCurrent}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.currentPageUrl"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage}" /></a>
											</li>
											<li>
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${currentPage+1}" href="${url}${filterAppliedWithPrefix}${seoPageNoNext}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.nextPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${currentPage+1}" /></a>
											</li>
											<c:if test="${currentPage <= pageCount-2}">
												<c:if test="${(currentPage < pageCount-2)}">
													<li class="ellips">...</li>
												</c:if>
												<li >
													<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit" title="${pageCount}" href="${url}${filterAppliedWithPrefix}${seoPageNoLastPage}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.lastPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}"><c:out value="${pageCount}" /></a>
												</li>
											</c:if>
											<li class="lnkNextPage arrow">
												<a data-submit-param-length="1"  data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" class="redirPage dynFormSubmit pagArrow" title="Next Page" href="${url}${filterAppliedWithPrefix}${seoPageNoNext}${storeIdParam}?<dsp:valueof param="browseSearchVO.pagingLinks.nextPage"/>${subCategoryPageParam}${plpViewParam}${inStoreParam}">&gt;</a>
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
