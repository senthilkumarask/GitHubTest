<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="requestURL" bean="/OriginatingRequest.requestURL"/>

<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
<c:set var="filterAppliedWithPrefix" value="${filterApplied}"></c:set>
<c:if test="${not empty filterAppliedWithPrefix}">
	<c:if test="${fn:substring(filterAppliedWithPrefix,fn:length(filterAppliedWithPrefix)-1,fn:length(filterAppliedWithPrefix)) ne '/'}">
	<c:set var="filterAppliedWithPrefix" value="${filterAppliedWithPrefix}/"></c:set>
	</c:if>
</c:if>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
</c:if>
<c:if test="${not empty url}">
	<c:if test="${fn:substring(url,fn:length(url)-1,fn:length(url)) ne '/'}">
	<c:set var="url" value="${url}/"></c:set>
	</c:if>
</c:if>
<dsp:getvalueof var="subCategoryPageParam" param="subCategoryPageParam" />
<%-- R2.2 Story - SEO Friendly URL changes : End --%>
<dsp:page>

	<div class="small-12 columns">
		<div class="row grid-nav">
			<form id="frmPagination" method="post" action="#">
				<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/>
				<c:set var="lowerRange" value="${1 + (size * (currentPage - 1 ))}"/>
				<c:set var="upperRange" value="${size * currentPage}"/>
				<div class="small-12 medium-9 large-12 columns">

					<div class="showing-text">
						<h3 class="show-for-medium-down">
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
							</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" /></span>
						</h3>
						<h3 class="show-for-large-up"><bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}" />
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
						</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" /></span></h3>
					</div>

					<div class="refinements">
                        <%-- Removing the last character to form the next button URL correctly --%>
                        
						<div class="grid-pagination">
							<c:if test="${pageCount > 1}">
								<ul class="inline-list right">
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
									<li class="nextViewPage">
										<c:choose>
											<c:when test="${currentPage > 1}">
												<a class="button left secondary" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}"><span>&nbsp;</span></a>
											</c:when>
											<c:otherwise>
												<a class="button left secondary disabled" href="${url}${filterAppliedWithPrefix}${seoPageNoPrev}?<dsp:valueof param="browseSearchVO.pagingLinks.previousPage"/>${subCategoryPageParam}${plpViewParam}"><span>&nbsp;</span></a>
											</c:otherwise>
										</c:choose>
									</li>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="browseSearchVO.pagingLinks.pageNumbers"/>
										<dsp:oparam name="outputStart">
											<li class="current-page-drop">
												<a class="small download button dropdown" data-dropdown="currentPage" href="#">&nbsp;<span>&nbsp;</span></a>
												<ul class="f-dropdown current-page" data-dropdown-content="" id="currentPage" name="currentPage">
													<li class="selected"><a href="#">${currentPage}</a></li>
										</dsp:oparam>
										<dsp:oparam name="output">
											<c:set var="pagpagesCount"><dsp:valueof param="count"/></c:set>
											<c:choose>
												<c:when test="${currentPage == '1' && pagpagesCount == '1'}">
													<%-- KP COMMENT: Skipping 1 in the dropdown when we are on page 1 --%>
												</c:when>
												<c:otherwise>
													<li><a href="${url}${filterAppliedWithPrefix}<dsp:valueof param="count"/>-${size}?<dsp:valueof param="element"/>${subCategoryPageParam}${plpViewParam}"><dsp:valueof param="count"/></a></li>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
												</ul>
											</li>
										</dsp:oparam>
									</dsp:droplet>
									<li class="nextViewPage">
										<c:choose>
											<c:when test="${currentPage <= pageCount-1}">
												<a href="${url}${filterAppliedWithPrefix}${seoPageNoNext}?<dsp:valueof param="browseSearchVO.pagingLinks.nextPage"/>${subCategoryPageParam}${plpViewParam}" class="button right secondary"><span></span></a>
											</c:when>
											<c:otherwise>
												<a href="${url}${filterAppliedWithPrefix}${seoPageNoNext}?<dsp:valueof param="browseSearchVO.pagingLinks.nextPage"/>${subCategoryPageParam}${plpViewParam}" class="button right secondary disabled"><span></span></a>
											</c:otherwise>
										</c:choose>
									</li>
								</ul>
							</c:if>
						</div>

						<div class="grid-page-control pagination hide-for-medium-down">
							<%-- START : R2.2 Story - 116-D1 & D2 --%>
							<dsp:getvalueof param="isViewAll" var="isViewAll"/>
							<dsp:getvalueof param="dropdownList" var="dropdownList"/>
							<dsp:getvalueof param="pagFilterOpt" id="pageFilterSize" />
							<c:if test="${not empty pageFilterSize}">
								<c:set var = "size" value= "${pageFilterSize}"></c:set>
							</c:if>

							<ul class="inline-list right">
								<li><h3><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></h3></li>
								<%-- R2.2 Story - SEO Friendly URL changes--%>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="dropdownList" />
									<dsp:oparam name="outputStart">
										<li>
											<a class="small download radius button dropdown" data-dropdown="perPage" href="#">&nbsp;<span>&nbsp;</span></a>
											<ul class="f-dropdown per-page" data-dropdown-content="" id="perPage" name="perPage">
		<%-- 										<li class="selected"><a href="#">${pageFilterSize}</a></li> --%>
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
											</ul>
										</li>
									</dsp:oparam>
									<dsp:oparam name="output">
										<dsp:getvalueof param="element" id="item" />
										<c:choose>
											<c:when test="${item eq 'View All'}">
												<c:choose>
													<c:when test="${isViewAll eq true}">
														<li class="selected"><a href="${url}${filterAppliedWithPrefix}1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}">${item}</a></li>
													</c:when>
													<c:otherwise>
														<li><a href="${url}${filterAppliedWithPrefix}1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}">${item}</a></li>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${size eq item && isViewAll eq false}">
														<li class="selected"><a href="${url}${filterAppliedWithPrefix}1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}">${item}</a></li>
													</c:when>
													<c:otherwise>
														<li><a href="${url}${filterAppliedWithPrefix}1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${subCategoryPageParam}${plpViewParam}">${item}</a></li>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
								<%-- END : R2.2 Story - 116-D1 & D2 --%>
							</ul>
						</div>

						<div class="grid-sort sort hide-for-medium-down">
							<c:choose>
								<c:when test="${frmBrandPage }">
									<ul class="inline-list right">
                                        <li><h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" />:</h3></li>
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
										<li>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" value="${sortOptionVO.sortingOptions}" />
												<dsp:oparam name="outputStart">
													<a class="small download radius button dropdown" data-dropdown="sortingOptions" href="#">Select<span>&nbsp;</span></a>
													<ul class="f-dropdown sorting-options" data-dropdown-content="" id="sortingOptions" name="sortingOptions">
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
													</ul>
												</dsp:oparam>
												<dsp:oparam name="output">
													<dsp:getvalueof var="sortOption" param="element"/>
													<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">
														<c:choose>
															<c:when test="${sortOption.ascending == 1}">
																<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />
															</c:when>
															<c:otherwise>
																<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
															</c:otherwise>
														</c:choose>
														<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX' ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price' )}">	
															<c:choose>
																<c:when test="${pagSortOpt == currentSortCode}">
																	<li class="selected"><a  onclick="javascript:sortTrack('${sortOption.sortValue}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}" >${sortOption.sortValue}</a></li>
																</c:when>
																<c:otherwise>
																	<li><a onclick="javascript:sortTrack('${sortOption.sortValue}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
																</c:otherwise>
															</c:choose>
														</c:if>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</li>
									</ul>
								</c:when>
								<c:otherwise>
									<ul class="inline-list right">
										<li><h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" />:</h3></li>
										<dsp:getvalueof var="defSortCode" param="categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam"/>
										<dsp:getvalueof var="defSortValue" param="categoryVO.sortOptionVO.defaultSortingOption.sortValue"/>
										<dsp:getvalueof var="defSortIsAscend" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/>
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
										<li>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" param="categoryVO.sortOptionVO.sortingOptions" />
												<dsp:oparam name="outputStart">
													<a class="small download radius button dropdown" data-dropdown="sortingOptions" href="#">Select<span>&nbsp;</span></a>
													<ul class="f-dropdown sorting-options" data-dropdown-content="" id="sortingOptions" name="sortingOptions">
												</dsp:oparam>
												<dsp:oparam name="outputEnd"> 
													</ul>
												</dsp:oparam>
												<dsp:oparam name="output">
													<dsp:getvalueof var="sortOption" param="element"/>
													<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">
														<c:choose>
															<c:when test="${sortOption.ascending == 1}">
																<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />
															</c:when>
															<c:otherwise>
																<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
															</c:otherwise>
														</c:choose>
														<c:if test="${(defaultUserCountryCode ne 'MX' && sortOption.sortUrlParam ne 'PriceMX' ) || (defaultUserCountryCode eq 'MX' && sortOption.sortUrlParam ne 'Price' )}">	
															<c:choose>
																<c:when test="${pagSortOpt == currentSortCode}">
																	<li class="selected"><a onclick="javascript:sortTrack('${sortOption.sortValue}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}" >${sortOption.sortValue}</a></li>
																</c:when>
																<c:otherwise>
																	<li><a onclick="javascript:sortTrack('${sortOption.sortValue}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
																</c:otherwise>
															</c:choose>
														</c:if>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</li>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>

					</div>

				</div>
				<div class="small-6 medium-3 right columns show-for-medium-down no-padding">
					<a class="right-off-canvas-toggle right secondary tiny button"><span>Filter</span></a>
				</div>
				<input type='submit' class='hide' value='submit' name='submit'/>
			</form>
		</div>
	</div>

</dsp:page>
