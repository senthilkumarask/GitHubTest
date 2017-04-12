<%--R2.2 Story 116A Changes--%>
<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="facets" param="browseSearchVO.facets" />
	<dsp:getvalueof var="emptyFacets" value="false" />
	<dsp:getvalueof var="emptyBox" value="true" />
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO" />
	<dsp:getvalueof var="totSize" value="${fn:length(browseSearchVO.descriptors)}" />
	<dsp:getvalueof var="facetSize" value="${fn:length(facets)}" />
	<dsp:getvalueof var="dontShowAllTab" param="dontShowAllTab" />
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="floatNode" param="floatNode"/>
	<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListCatProdCountDroplet"  /> 
	<c:set var="pageSize" value="1-${size}"/>
	<dsp:getvalueof var="view" param="view" />
	<c:set var="showDepartment" value="false" />
	<dsp:getvalueof param="hideDepartmentOnNullResultPage" var="hideDepartmentOnNullResultPage" />
	<dsp:getvalueof var="emptyFacets" param="browseSearchVO.emptyFacets"/>
	<dsp:getvalueof var="emptyBox" param="browseSearchVO.emptyBox"/>
	<c:set var="Attributes"><bbbc:config key="Attributes" configName="DimDisplayConfig" /></c:set>
	<%--BBBSL-8830 | Adding category name in the all link --%>
	<c:set var="plpViewParam" value="&view=${defaultView}"/>
	<dsp:getvalueof var="subCategoryPageParam" param="subCategoryPageParam" />
	<c:if test="${not empty seoUrl}">
		<c:set var="url" value="${contextPath}${seoUrl}" scope="request" />
	</c:if>

	<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="priceLabel"><bbbl:label key="lbl_facet_price_range" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="brandDisplayDim"><bbbc:config key="Brand" configName="DimDisplayConfig" /></c:set>
	<c:set var="checklistCategoryDim"><bbbc:config key="Checklist_Category" configName="DimDisplayConfig" /></c:set>
	<c:set var="leftAccordionExpand"><bbbl:label key="lbl_search_left_nav_accordion_expand" language ="${pageContext.request.locale.language}" />'</c:set>
	<c:set var="leftAccordionCollapse"><bbbl:label key="lbl_search_left_nav_accordion_collapse" language ="${pageContext.request.locale.language}" /></c:set>

	<c:if test="${emptyFacets}">
		<dsp:getvalueof var="facets" param="browseSearchVO.emptyFacetsList"/>
		<dsp:getvalueof var="facetSize" value="${totSize}" />
	</c:if>
	
	<c:if test="${(emptyFacets && facetSize > 0) || !emptyFacets}">
		<c:choose>
			<c:when test="${(!emptyFacets || (emptyFacets && emptyBox)) || !emptyBox}">
				<div id="facetBox">
			
			<c:if test="${empty hideDepartmentOnNullResultPage }">
			
						<c:set var="facetIdCountInUrl" value="${0}" />						 
						<c:set var="currentseoUrlDimId" value="${browseSearchVO.checkListSeoUrlHierarchy.seoUrlDimensionId}"/>
						<c:set var="facetIdCollection" value="" />
						<c:set var="facetIdInUrl" value="${param.CatalogId}" />
						<c:set var="categoryLevel" value="${browseSearchVO.checkListSeoUrlHierarchy.categoryLevel}" />
						<c:if test="${ not empty facetIdInUrl }">
								<c:set var="facetIdInUrlArray" value="${fn:split(facetIdInUrl, '-')}" />
								<c:set var="facetIdCountInUrl" value="${fn:length(facetIdInUrlArray)}" />
								<c:if test="${ facetIdCountInUrl > 2 }">
								<c:set var="countFacet" value="${0}" />
									<c:forEach var="facetId" items="${facetIdInUrlArray}">
										<c:if test="${ currentseoUrlDimId  ne  facetId  }">
												<c:choose>
													 <c:when test="${ countFacet eq   0  }">
															<c:set var="facetIdCollection" value="${facetId}" />
													 </c:when>
													 <c:otherwise>
													 		<c:set var="facetIdCollection" value="${facetIdCollection}-${facetId}" />
													 </c:otherwise>
												 </c:choose>
												<c:set var="countFacet" value="${countFacet}+ ${countFacet}" />
										</c:if>
									</c:forEach>
								</c:if>
								
						</c:if>
				   		<div class="facetCategories">
						<c:choose>
							<c:when test="${categoryLevel eq 3  }">
								    <c:set var="selectedCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
									<c:set var="parentCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.checkListCategoryName}"/>
									<c:set var="parentCategorySeoUrl" value="${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.seoUrl}"/>
									
									    <div class="facetCategories">
											<div class="categoriesGroup ">
												<ul>
													<c:if test="${dontShowAllTab ne 'true'}">
														  <c:set var="all_cat_link"> <bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${parentCategoryName}</c:set> 
														  <li>	 
														       <c:choose>
																	<c:when test="${ not empty browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.siteURL}">
																	 <dsp:a iclass="${allTabList}" href="${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.siteURL}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:when>
																	<c:otherwise>
																	<dsp:a iclass="${allTabList}" href="${contextPath}/${parentCategorySeoUrl}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:otherwise>
															   </c:choose>	 
														</li>	
													</c:if>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="browseSearchVO.checkListSeoUrlHierarchy.siblingsSeoUrls" />
															<dsp:oparam name="output">
															    <dsp:getvalueof var="checkListCategoryName" param="element.checkListCategoryName" />
																<dsp:getvalueof var="seoUrl" param="element.seoUrl" />
																<dsp:getvalueof var="productCount" param="element.productCount" />
																<dsp:getvalueof var="categoryLevel" param="element.categoryLevel" />
																<dsp:getvalueof var="seoUrlDimensionId" param="element.seoUrlDimensionId" />
																<dsp:getvalueof var="categoryEnabled" param="element.categoryEnabled" />
																<dsp:getvalueof var="siteURL" param="element.siteURL" />
																<dsp:getvalueof var="tabList" value="" />
																<c:set var= "prodCountdetail" value=""/>
																<c:if test="${categoryEnabled}">
																		<c:if test="${selectedCategoryName eq checkListCategoryName}">
																				<dsp:getvalueof var="tabList" value="active" />
																		</c:if>
																	<c:if test="${not empty facetIdCollection && not empty facetIdInUrl && facetIdCountInUrl > 2 && empty siteURL}">
																			
																			<c:set var= "isFacetApplied" value="${true}"/>
																			 
																		     <dsp:droplet name="CheckListCatProdCountDroplet">
																				<dsp:param name="seoUrlDimensionId" value="${seoUrlDimensionId}" />
																				<dsp:param name="facetIdCollection" value="${facetIdCollection}" />
																				
																				<dsp:oparam name="output">
																				<dsp:getvalueof var="productCount" param="productCountUpdated" />
																				</dsp:oparam>
																			 </dsp:droplet>
																		</c:if>
																		
																		<c:if test="${empty siteURL}">
																			<c:set var= "prodCountdetail" value="&nbsp;(${productCount})"/>
																		</c:if>
																		
																		<c:if test="${productCount > 0 || not empty siteURL}">
																			<c:choose>
																			     <c:when test="${not empty siteURL}">
																						<li>
																							<a class="${tabList}" href="${siteURL}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:when>
																				 <c:when test="${ isFacetApplied }">
																						<li>
																							<a class="${tabList}" url-dim-val="${seoUrlDimensionId}-lvl:${categoryLevel}-SiblingOfC3" href="${contextPath}/${seoUrl}/${seoUrlDimensionId}-${facetIdCollection}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:when>
																				 <c:otherwise>
																				 		<li>
																							<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-SiblingOfC3" href="${contextPath}/${seoUrl}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:otherwise>
																			 </c:choose>
												 						</c:if>
																</c:if>
															 </dsp:oparam>
													</dsp:droplet>
												</ul>
											</div>	
										</div>
							</c:when>
							<c:when test="${ categoryLevel eq 2 }">
									<c:set var="selectedCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
									<c:set var="parentCategory" value="${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl}"/>
										
										<div class="facetCategories">
											<div class="categoriesGroup ">
											<ul>
												<c:if test="${dontShowAllTab ne 'true'}">
												 <c:choose>
													 <c:when test="${ empty browseSearchVO.checkListSeoUrlHierarchy.childsSeoUrls}">
													        <c:set var="all_cat_link"> <bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${parentCategory.checkListCategoryName}</c:set> 
															  <li>	 
																<c:choose>
																	<c:when test="${ not empty parentCategory.siteURL}">
																	 <dsp:a  href="${parentCategory.siteURL}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:when>
																	<c:otherwise>
																	<dsp:a  href="${contextPath}/${parentCategory.seoUrl}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:otherwise>
																</c:choose>	 
															 </li>	
													 </c:when>
												     <c:otherwise>
														<c:set var="all_cat_link"> <bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${selectedCategoryName}</c:set> 
															  <li>	 
																<c:choose>
																	<c:when test="${ not empty browseSearchVO.checkListSeoUrlHierarchy.siteURL}">
																	 <dsp:a  href="${browseSearchVO.checkListSeoUrlHierarchy.siteURL}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:when>
																	<c:otherwise>
																	<dsp:a iclass="active"  href="${contextPath}/${browseSearchVO.checkListSeoUrlHierarchy.seoUrl}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																	</c:otherwise>
																</c:choose>	 
															 </li>	
														
													  </c:otherwise>
													</c:choose>
													</c:if>
												<c:choose>
												<c:when test="${ empty browseSearchVO.checkListSeoUrlHierarchy.childsSeoUrls}">
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="browseSearchVO.checkListSeoUrlHierarchy.siblingsSeoUrls" />
																<dsp:oparam name="output">
																    <dsp:getvalueof var="checkListCategoryName" param="element.checkListCategoryName" />
																	<dsp:getvalueof var="seoUrl" param="element.seoUrl" />
																	<dsp:getvalueof var="productCount" param="element.productCount" />
																	<dsp:getvalueof var="categoryLevel" param="element.categoryLevel" />
																	<dsp:getvalueof var="seoUrlDimensionId" param="element.seoUrlDimensionId" />
																	<dsp:getvalueof var="categoryEnabled" param="element.categoryEnabled" />
																	<dsp:getvalueof var="siteURL" param="element.siteURL" />
																	<dsp:getvalueof var="tabList" value="" />
																	<c:set var= "prodCountdetail" value=""/>
																	<c:if test="${categoryEnabled}">
																			<c:if test="${selectedCategoryName eq checkListCategoryName}">
																					<dsp:getvalueof var="tabList" value="active" />
																			</c:if>
																		<c:if test="${not empty facetIdCollection && not empty facetIdInUrl && facetIdCountInUrl > 2 && empty siteURL}">
																				
																				<c:set var= "isFacetApplied" value="${true}"/>
																				 
																			     <dsp:droplet name="CheckListCatProdCountDroplet">
																					<dsp:param name="seoUrlDimensionId" value="${seoUrlDimensionId}" />
																					<dsp:param name="facetIdCollection" value="${facetIdCollection}" />
																					
																					<dsp:oparam name="output">
																					<dsp:getvalueof var="productCount" param="productCountUpdated" />
																					</dsp:oparam>
																				 </dsp:droplet>
																			</c:if>
																			
																			<c:if test="${empty siteURL}">
																				<c:set var= "prodCountdetail" value="&nbsp;(${productCount})"/>
																			</c:if>
																			<c:if test="${productCount > 0 || not empty siteURL}">
																				<c:choose>
																				<c:when test="${not empty siteURL}">
																						<li>
																							<a class="${tabList}" href="${siteURL}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:when>
																					 <c:when test="${ isFacetApplied }">
																							<li>
																								<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-siblingOfC2-${selectedCategoryName}" href="${contextPath}/${seoUrl}/${seoUrlDimensionId}-${facetIdCollection}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																							</li>
																					 </c:when>
																					 <c:otherwise>
																					 		<li>
																								<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-siblingOfC2-${selectedCategoryName}" href="${contextPath}/${seoUrl}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																							</li>
																					 </c:otherwise>
																				 </c:choose>
													 						</c:if>
																	</c:if>
																 </dsp:oparam>
														</dsp:droplet>
												</c:when>
												<c:otherwise>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="browseSearchVO.checkListSeoUrlHierarchy.childsSeoUrls" />
															<dsp:oparam name="output">
															    <dsp:getvalueof var="checkListCategoryName" param="element.checkListCategoryName" />
																<dsp:getvalueof var="seoUrl" param="element.seoUrl" />
																<dsp:getvalueof var="productCount" param="element.productCount" />
																<dsp:getvalueof var="categoryLevel" param="element.categoryLevel" />
																<dsp:getvalueof var="seoUrlDimensionId" param="element.seoUrlDimensionId" />
																<dsp:getvalueof var="categoryEnabled" param="element.categoryEnabled" />
																<dsp:getvalueof var="siteURL" param="element.siteURL" />
																<dsp:getvalueof var="tabList" value="" />
																<c:set var= "prodCountdetail" value=""/>
																<c:if test="${categoryEnabled}">
																		<c:if test="${selectedCategoryName eq checkListCategoryName}">
																				<dsp:getvalueof var="tabList" value="active" />
																		</c:if>
																	<c:if test="${not empty facetIdCollection && not empty facetIdInUrl && facetIdCountInUrl > 2 && empty siteURL}">
																			
																			<c:set var= "isFacetApplied" value="${true}"/>
																			 
																		     <dsp:droplet name="CheckListCatProdCountDroplet">
																				<dsp:param name="seoUrlDimensionId" value="${seoUrlDimensionId}" />
																				<dsp:param name="facetIdCollection" value="${facetIdCollection}" />
																				
																				<dsp:oparam name="output">
																				<dsp:getvalueof var="productCount" param="productCountUpdated" />
																				</dsp:oparam>
																			 </dsp:droplet>
																		</c:if>
																		<c:if test="${empty siteURL}">
																			<c:set var= "prodCountdetail" value="&nbsp;(${productCount})"/>
																		</c:if>
																		<c:if test="${productCount > 0 || not empty siteURL}">
																			<c:choose>
																			<c:when test="${ not empty siteURL}">
																						<li>
																							<a class="${tabList}"  href="${siteURL}"   title="${checkListCategoryName}" >${checkListCategoryName}</a>
																						</li>
																				 </c:when>
																				 <c:when test="${ isFacetApplied }">
																						<li>
																							<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-childOfC2-${selectedCategoryName}" href="${contextPath}/${seoUrl}/${seoUrlDimensionId}-${facetIdCollection}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:when>
																				 <c:otherwise>
																				 		<li>
																							<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-childOfC2-${selectedCategoryName}"  href="${contextPath}/${seoUrl}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:otherwise>
																			 </c:choose>
												 						</c:if>
																</c:if>
															 </dsp:oparam>
													</dsp:droplet>
												</c:otherwise>
												</c:choose>
											</ul>
										</div>	
									</div>
							</c:when>
							
							<c:when test="${categoryLevel eq 1 }">
									<c:set var="selectedCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
										
										<div class="facetCategories">
											<div class="categoriesGroup ">
												<ul>
													<c:if test="${dontShowAllTab ne 'true'}">
															  <c:set var="all_cat_link"> <bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${selectedCategoryName}</c:set> 
															  <li>	 
															       <c:choose>
																		<c:when test="${ not empty browseSearchVO.checkListSeoUrlHierarchy.siteURL}">
																		 <dsp:a  href="${browseSearchVO.checkListSeoUrlHierarchy.siteURL}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																		</c:when>
																		<c:otherwise>
																		<dsp:a iclass="active" href="${contextPath}/${browseSearchVO.checkListSeoUrlHierarchy.seoUrl}" title="${all_cat_link}">${all_cat_link}</dsp:a>
																		</c:otherwise>
																   </c:choose>	 
															</li>	
														</c:if>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="browseSearchVO.checkListSeoUrlHierarchy.childsSeoUrls" />
															<dsp:oparam name="output">
															    <dsp:getvalueof var="checkListCategoryName" param="element.checkListCategoryName" />
																<dsp:getvalueof var="seoUrl" param="element.seoUrl" />
																<dsp:getvalueof var="productCount" param="element.productCount" />
																<dsp:getvalueof var="categoryLevel" param="element.categoryLevel" />
																<dsp:getvalueof var="seoUrlDimensionId" param="element.seoUrlDimensionId" />
																<dsp:getvalueof var="categoryEnabled" param="element.categoryEnabled" />
																<dsp:getvalueof var="siteURL" param="element.siteURL" />
																<dsp:getvalueof var="tabList" value="" />
																<c:set var= "prodCountdetail" value=""/>
																<c:if test="${categoryEnabled}">
																		<c:if test="${selectedCategoryName eq checkListCategoryName}">
																				<dsp:getvalueof var="tabList" value="active" />
																		</c:if>
																	<c:if test="${not empty facetIdCollection && not empty facetIdInUrl && facetIdCountInUrl > 2 && empty siteURL}">
																			
																			<c:set var= "isFacetApplied" value="${true}"/>
																			 
																		     <dsp:droplet name="CheckListCatProdCountDroplet">
																				<dsp:param name="seoUrlDimensionId" value="${seoUrlDimensionId}" />
																				<dsp:param name="facetIdCollection" value="${facetIdCollection}" />
																				
																				<dsp:oparam name="output">
																				<dsp:getvalueof var="productCount" param="productCountUpdated" />
																				</dsp:oparam>
																			 </dsp:droplet>
																		</c:if>
																		<c:if test="${empty siteURL}">
																			<c:set var= "prodCountdetail" value="&nbsp;(${productCount})"/>
																		</c:if>
																		<c:if test="${productCount > 0 || not empty siteURL}">
																			<c:choose>
																			 <c:when test="${ not empty siteURL}">
																						<li>
																							<a class="${tabList}" href="${siteURL}"   title="${checkListCategoryName}" >${checkListCategoryName}</a>
																						</li>
																				 </c:when>
																				 <c:when test="${ isFacetApplied }">
																						<li>
																							<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-childsOfC1-${selectedCategoryName}"  href="${contextPath}/${seoUrl}/${seoUrlDimensionId}-${facetIdCollection}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:when>
																				 <c:otherwise>
																				 		<li>
																							<a class="${tabList}" url-detail="${seoUrlDimensionId}-lvl:${categoryLevel}-childsOfC1-${selectedCategoryName}" href="${contextPath}/${seoUrl}"   title="${checkListCategoryName}" >${checkListCategoryName}${prodCountdetail}</a>
																						</li>
																				 </c:otherwise>
																			 </c:choose>
												 						</c:if>
																</c:if>
															 </dsp:oparam>
													</dsp:droplet>
												</ul>
											</div>	
										</div>
							</c:when>
					</c:choose>	
					</div>


				</c:if>
<!-- End -->	
				<div id="searchBox">
					<form id="frmSearchCriteria" method="post">
						<fieldset class="searchGroup">
							<div class="searchTitle">
								<dsp:getvalueof var="descriptors" param="browseSearchVO.descriptors" />
								
								<c:set var="elementDesc" value="${descriptors[totSize-1]}"/>
								<dsp:getvalueof var="type" value="${elementDesc.rootName}" />
								<dsp:getvalueof var="facetCountForRefTypeParam" param="browseSearchVO.facetCountForRefTypeParam" />
								<c:if test="${type ne checklistCategoryDim or facetCountForRefTypeParam ge 1}">
									<h3>
									<bbbl:label key="lbl_search_facet_bar" language="${pageContext.request.locale.language}" />
									</h3>
								</c:if>
								
								<c:if test="${facetCountForRefTypeParam > 1}">
									<c:set var="refTypeParam" value="&refType=true"/>
								</c:if>
							</div>
							<div class="searchContent noBorder">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="browseSearchVO.descriptors" />
									<dsp:param name="elementName" value="selectedFacetRefItem" />
									<dsp:oparam name="outputStart">
										<dsp:getvalueof var="size" param="size" />
										<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
										<ul class="searchList">
									</dsp:oparam>
									<%-- R2.2 SEO Friendly URL changes --%>
									<dsp:oparam name="output">
										<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
										<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery"/>
										<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter"/>
										
										<c:if test="${(type ne checklistCategoryDim) and (not empty facetDescFilter)}">
											<li class="clearfix">
												<span class="searchListItem">
													<span class="searchItem">
														<dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" />
													</span>
													<a  href="${url}${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${searchQueryParams}${plpViewParam}${refTypeParam}" class="lnkSearchRemove" title="Remove" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-value="${swsterms}">X</a>
												</span>
											</li>
										</c:if>
									</dsp:oparam>
									
									<dsp:oparam name="outputEnd">
									<c:set var="filterParam"></c:set>
									<c:if test="${not empty filter}">
										<c:set var="filterParam">${filter}/</c:set>
									</c:if>
									<li>
										<c:if test="${(type ne checklistCategoryDim) or facetCountForRefTypeParam ge 1}">
											<c:set var="clear">
                                      			<bbbl:label key="lbl_search_filter_box_clearall" language="${pageContext.request.locale.language}" />
                                      		</c:set>
                                   			<c:if test="${empty urlSe}">
                                   				<c:set var="urlSe">
												${url}?refType=true
												</c:set>
                                   			</c:if>
											<a  href="${urlSe}" class="lnkSearchReset" title="${clear}"> ${clear} </a>
										</c:if>
									</li>
									</ul>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</fieldset>
					</form>
				</div>	
			</c:when>
		</c:choose>
		<form method="post" id="hiddenFacetedForm" action="#">
			<input type="hidden" name="keyword" value="">
               <input type="hidden" name="swsterms" value="${swsterms}">
			<input type="submit" class="hidden" value="">
		</form>
	</c:if>
	
	
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="facetName" param="element.name" />
			<dsp:getvalueof var="multiSelectFacet" param="element.multiSelect" />
			<dsp:getvalueof var="refinedFacetName" param="element.facetRefinedName" />
			<dsp:getvalueof var="facetListStyle" param="element.displayProperties.facetListStyle" />
			<dsp:getvalueof var="fieldsetClass" param="element.displayProperties.fieldsetClass" />
			<dsp:getvalueof var="offScreen" param="element.displayProperties.offScreen" />
			<dsp:getvalueof var="divClass" param="element.displayProperties.divClass" />
			<dsp:getvalueof var="title" param="element.displayProperties.title" />
			<dsp:getvalueof var="displayName" param="element.displayProperties.displayName" />
			
			<c:if test="${facetName ne 'RECORD TYPE' && !(facetName == 'PRICE RANGE' && defaultUserCountryCode eq 'MX') && !(facetName == 'PRICE RANGE MX' && defaultUserCountryCode ne 'MX')}">
				
				<fieldset class="${fieldsetClass}">
					<c:choose>
						<c:when test="${facetName eq department}">
							<c:set var="scrollerClass">facetScroller facetScrollerDept</c:set>
							<c:if test="${showDepartment eq 'true'}">
								<div class="${divClass}">
									<a href="#" title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName}</a>
								</div>
							</c:if>
						</c:when>
						<c:otherwise>
						<c:set var="scrollerClass">facetScroller</c:set>
							<div class="${divClass}">
								<a href="#" title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName}</a>
							</div>
						</c:otherwise>
					</c:choose>
						
				<div class="facetContent">
					<c:choose>
						<%-- For displaying brand typeahead box --%>
						<c:when test="${facetName == brandDisplayDim}">
							<div class="facetBrandSearch">
								<c:set var="guide_text">
									<bbbl:label key="lbl_brand_typeaheadbox_guide_text" language="${pageContext.request.locale.language}" />
								</c:set>
								<input class="txtFacetSearch" type="text" value="${guide_text}" title="${guide_text}" aria-required="false" />
							</div>
						</c:when>
						<c:when test="${facetName == 'COLLEGE'}">
							<div class="facetBrandSearch">
								<c:set var="guide_text">
									<bbbl:label key="lbl_college_typeaheadbox_guide_text" language="${pageContext.request.locale.language}" />
								</c:set>
								<input class="txtFacetSearch" type="text" value="${guide_text}" title="${guide_text}" aria-required="false" />
							</div>
						</c:when>
					</c:choose>
				
					<div aria-hidden="false" class="${scrollerClass}">
						<ul class="${facetListStyle}">
						
				<%-- For multi selected facets show selected refinements/descriptors --%>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="element.facetDescriptors" />
						<dsp:oparam name="false">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="element.facetDescriptors" />
								<dsp:param name="elementName" value="selectedFacetRefItem" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="selectedFacetRefItemName" param="selectedFacetRefItem.name" />
									<dsp:getvalueof var="selectedRefItemRoot" param="selectedFacetRefItem.rootName" />
									<dsp:getvalueof var="refinedName_0" param="selectedFacetRefItem.refinedName" />
									<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
									<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter"/>
									<%-- Only Brand, Attribute & Color are multiselect facets. So when selected descriptor is child either of these then show this descriptor as selected checkox--%>
									<li class="facetListItem clearfix">
										<c:set var="brandNameTitle"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /></c:set>
										  <%--R2.2 Story 116C Changes--%>
										  <%-- R2.2 SEO Friendly URL changes --%>
										 <input type="checkbox" id="${refinedFacetName}_${selectedFacetRefItemName}"
										onclick="omnitureRefineCall('${facetName}', '${refinedName_0}');"
										 value="${url}${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${searchQueryParams}${plpViewParam}&refType=true" name="brandSearch"
										 title="${brandNameTitle}" checked="checked" class="checkbox facetedCheckBox" aria-checked="true" aria-describedby="${facetName}_${selectedFacetRefItemName}" />
										 <label for="${refinedFacetName}_${selectedFacetRefItemName}"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true"/></label>
									</li>
								</dsp:oparam>
							</dsp:droplet>
							<%-- End ForEach--%>
						</dsp:oparam>
					</dsp:droplet>
					<%-- End IsEmpty--%>
					
				<%-- Show facets refinements--%>
				<dsp:getvalueof var="facetParentVoName" param="element.name"/>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="element.facetRefinement" />
						<dsp:oparam name="output">
							<c:choose>
								<%-- Show refinements for brand, attributes and color as checkbox--%>
								<c:when test="${multiSelectFacet}">
									<li class="facetListItem clearfix">
										<dsp:getvalueof var="facetRefItem" param="element.name" />
										<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
										<dsp:getvalueof var="facetItemQuery" param="element.query" />
										<dsp:getvalueof var="refinedName_1" param="element.refinedName" />
										<dsp:getvalueof var="idx" param="index" />
										<c:set var="facetItemTitle"><dsp:valueof param="element.name" valueishtml="true"/>
										</c:set>
										<dsp:getvalueof var="intlFlagAtt" param="element.intlFlag"/>
										<c:choose>
												<c:when test="${facetParentVoName eq Attributes }">
												<c:choose>
													<c:when test="${isInternationalCustomer eq true}">
														<c:if test="${intlFlagAtt eq 'Y'}">
															<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}?${facetItemQuery}${searchQueryParams}${plpViewParam}&refType=true${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" /><label for="${refinedFacetName}_${idx}"> 
															<dsp:valueof param="element.name" valueishtml="true"/>
															&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
														</c:if>
													</c:when>
													<c:otherwise>
															<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}?${facetItemQuery}${searchQueryParams}${plpViewParam}&refType=true${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" /><label for="${refinedFacetName}_${idx}"> 
															<dsp:valueof param="element.name" valueishtml="true"/>
															&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
											<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}?${facetItemQuery}${searchQueryParams}${plpViewParam}&refType=true${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" /><label for="${refinedFacetName}_${idx}"> 
											<dsp:valueof param="element.name" valueishtml="true"/>
											&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
											</c:otherwise>
										</c:choose>
									</li>
								</c:when>
								<c:when test="${facetName == department}">
									<dsp:getvalueof param="showDepartment" var="showDepartment" />
									<c:if test="${showDepartment eq 'true'}">
										<li class="facetListItem clearfix">
											<dsp:getvalueof var="string" param="element.name" />
											<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
											<dsp:getvalueof var="facetItemQuery" param="element.query"/>
											<dsp:getvalueof var="refinedName_2" param="element.refinedName" />
											<%--R2.2 Story 116C Changes--%>
											<%-- R2.2 SEO Friendly URL changes --%> 
											<a class="dynFormSubmit" onclick="omnitureRefineCall('${facetName}', '${refinedName_2}');" href="${url}${facetRefFilter}/${pageSize}?${facetItemQuery}${searchQueryParams}${plpViewParam}&refType=true${swdFlag}" title="<dsp:valueof param="element.name"/>" data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}"> 
												<%-- <img width="24" height="24" src="${imagePath}/_assets/global/images/icons/<c:out value="${fn:toLowerCase(string)}"/>.gif" /> --%>
												<span class="facetDepartmentListItemName"><dsp:valueof param="element.name" /></span>
												<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span>
											</a>
										</li>
									</c:if>
								</c:when>
								<c:otherwise>
									<li class="facetListItem clearfix">
										<c:set var="escUrl"><c:out value="${url}"/></c:set>
										<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
										<dsp:getvalueof var="intlFlagAtt" param="element.intlFlag"/>
										<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
										<dsp:getvalueof var="facetItemQuery" param="element.query"/>
						
										 <%--R2.2 Story 116C Changes--%>
												<%-- R2.2 SEO Friendly URL changes --%>
										<a class="dynFormSubmit" data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" 
										onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}?${facetItemQuery}${searchQueryParams}&refType=true${swdFlag}" title="<dsp:valueof param="element.name"/>"> <dsp:valueof param="element.name" /> 
										<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </a></li>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
					</ul>
					</div>
				</div>
			</fieldset>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${(emptyFacets && facetSize > 0) || !emptyFacets}">
	</div>
	</c:if>

	 <script type="text/javascript">
	function omnitureRefineCallInitial(refineString){
		var selPriceRange = document.getElementById("selPriceRange").value;
		var indexValuePrice=selPriceRange.lastIndexOf('=');
		var subRefineString = selPriceRange.substring(indexValuePrice+1, selPriceRange.length);
		omnitureRefineCall(refineString, subRefineString);
	}
		function omnitureRefineCall(refineString, subRefineString) {
		   if (typeof refinementTrack === 'function' && typeof s !== 'undefined') {
			   refinementTrack(refineString, subRefineString); }
		}
	</script>  
</dsp:page>