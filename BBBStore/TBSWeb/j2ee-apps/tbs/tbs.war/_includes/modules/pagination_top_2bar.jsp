<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<%-- R2.2 Story - SEO Friendly URL changes : Start --%>
<c:set var="filterAppliedWithPrefix" value="/${filterApplied}"></c:set>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
</c:if>
<%-- R2.2 Story - SEO Friendly URL changes : End --%>
<dsp:page>
	
	<div class="small-12 columns large-no-padding-right">
		
		<form id="frmPagination" method="post" action="#">
			
			<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
			<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
			<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:getvalueof var="searchView" param="view"/>
			<c:set var="searchViewParam" value="&view=${searchView}"/>
			
			<%-- search tabs --%>
			<div class="row show-for-large-up">
				<dsp:include page="/search/search_tabs.jsp">
						<dsp:param name="searchAssetType" value="${searchAssetType}"/>
						<dsp:param name="browseSearchVO" param="browseSearchVO" />	
						<dsp:param name="Keyword" param="Keyword"/>
				</dsp:include>
			</div>
			
			<dsp:getvalueof param="type" var="searchType"/>
				
			<div class="row grid-nav">
				<div class="showing-text small-6 large-4">
						<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
						<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize"/> 
						<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
						<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
						<c:set var="lowerRange" value="${1 + (size * (currentPage - 1 ))}"/>
						<c:set var="upperRange" value="${size * currentPage}"/>
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
							</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof
								param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
						</span>
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
							</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof
								param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
						</span>
						</h3>
						
				</div>
                
                <div class="refinements small-6 large-8">
				
			    <%-- Go to page number dropdown --%>
				<div class="grid-pagination">
					<c:if test="${pageCount > 1}">
						<ul class="inline-list right">
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
							
							<li>
                                <c:choose>
                                    <c:when test="${currentPage > 1}">
                                        <a class="button left secondary" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${subCategoryPageParam}${plpViewParam}"><span>&nbsp;</span></a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="button left secondary disabled" href="${url}${filterAppliedWithPrefix}/${seoPageNoPrev}?${subCategoryPageParam}${plpViewParam}"><span>&nbsp;</span></a>
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
                                        </c:when>
                                        <c:otherwise>
                                            <li><a href="${url}${filterAppliedWithPrefix}/<dsp:valueof param="count"/>-${size}?<dsp:valueof param="element"/>${subCategoryPageParam}${plpViewParam}"><dsp:valueof param="count"/></a></li>
                                        </c:otherwise>
                                    </c:choose>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									</ul>
                                            </li>
								</dsp:oparam>
							</dsp:droplet>
							
								<li>
									<c:choose>
										<c:when test="${currentPage <= pageCount-1}">
											<a class="button right secondary  dynFormSubmit" data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}?${nextPageParams}${searchQueryParams}" class="pagArrow"><span></span></a>
										</c:when>
										<c:otherwise>
											<a class="button right secondary disabled dynFormSubmit" data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" href="${url}${filterAppliedWithPrefix}/${seoPageNoNext}?${nextPageParams}${searchQueryParams}" class="pagArrow"><span></span></a>
										</c:otherwise>
									</c:choose>	
								</li>	
							</ul>
					</c:if>
				</div>
                
                <%-- Per page --%>
                <div class="grid-page-control pagination hide-for-medium-down">
                <%-- START : R2.2 Story - 116-D1 & D2 --%>
                    <dsp:getvalueof param="isViewAll" var="isViewAll"/>
                    <dsp:getvalueof param="dropdownList" var="dropdownList"/>
                    <dsp:getvalueof param="pagFilterOpt" id="pageFilterSize" />     
                    <c:if test="${not empty pageFilterSize}">
                        <c:set var = "size" value= "${pageFilterSize}"></c:set>
                    </c:if>
                    <%-- R2.2 Story - SEO Friendly URL changes --%>
                    <ul class="inline-list right">
                    <li><h3><bbbl:label key="lbl_guide_perpage" language="${pageContext.request.locale.language}" /></h3></li>
                    
                            <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="dropdownList" />
                            <dsp:oparam name="outputStart">
                                    <li>
                                            <a class="small download radius button dropdown" data-dropdown="perPage" href="#">&nbsp;<span>&nbsp;</span></a>
                                            <ul class="f-dropdown per-page" data-dropdown-content="" id="perPage" name="perPage">
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
                                            <li class="selected"><a href="${url}${filterAppliedWithPrefix}/1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}">${item}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><a href="${url}${filterAppliedWithPrefix}/1-${count}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}">${item}</a></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${size eq item && isViewAll eq false}">
                                            <li class="selected"><a href="${url}${filterAppliedWithPrefix}/1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}">${item}</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><a href="${url}${filterAppliedWithPrefix}/1-${item}?<dsp:valueof param="browseSearchVO.pagingLinks.firstPage"/>${searchViewParam}">${item}</a></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                            </dsp:oparam>
                            </dsp:droplet>
                    <%-- END : R2.2 Story - 116-D1 & D2 --%>
                    </ul>
                </div>
                
                <%-- Sort by --%>
                <div class="grid-sort sort hide-for-medium-down">
                        
                            <dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
                            <ul class="inline-list right">
                
                
                                <li><h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></h3>
                            <li>
                            <a class="small download radius button dropdown" data-dropdown="sortingOptions" href="#">Select<span>&nbsp;</span></a>
                            <ul class="f-dropdown sorting-options" data-dropdown-content="" id="sortingOptions" name="sortingOptions">
                                    
                                <c:set var="option1"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${(pagSortOpt == null)}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option1}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a onclick="javascript:sortTrack('${option1}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="option5"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Price-0')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option5}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a onclick="javascript:sortTrack('${option5}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Price-1')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option6}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a onclick="javascript:sortTrack('${option6}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                            <c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                <c:set var="option7"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Ratings-1')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option7}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}" onclick="javascript:sortTrack('${option7}')"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                                </c:if>
                                <c:set var="option9"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Brand-0')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option9}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}" selected><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a onclick="javascript:sortTrack('${option9}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="option2"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Date-1')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option2}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}" onclick="javascript:sortTrack('${option2}')"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>

                                </c:choose>
                                <%-- Start : Added as part of R2.1 --%>
                                <c:set var="option10"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
                                <c:choose>
                                    <c:when test="${ (pagSortOpt == 'Sales-1')}">
                                        <li class="selected"><a onclick="javascript:sortTrack('${option10}')" href="${url}${filterAppliedWithPrefix}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}" onclick="javascript:sortTrack('${option10}')"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></a></li>
                                    </c:otherwise>
                                </c:choose>
                                <%-- End : Added as part of R2.1 --%>
                            </ul>
                            </li>
                         </ul>
	                </div>
	                <div class="small-12 right columns show-for-medium-down no-padding">
						<a class="right-off-canvas-toggle right secondary expand button"><span>Filter</span></a>
					</div>
				</div>
   			</div>
   			<input type='submit' class='hide' value='submit' name='submit'/>
		</form>
	</div>
</dsp:page>
