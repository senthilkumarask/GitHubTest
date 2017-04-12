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
	<dsp:getvalueof var="swsTermsList" param="swsTermsList" />
	<dsp:getvalueof var="swsTermsLength" value="${fn:length(swsTermsList)}" />
	<dsp:getvalueof var="dontShowAllTab" param="showDepartment" />
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="floatNode" param="floatNode"/>
	<dsp:importbean bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
	<c:set var="pageSize" value="1-${size}"/>
	<%--<dsp:getvalueof var="url" param="url"/>--%>
	<dsp:getvalueof var="view" param="view" />
	<dsp:getvalueof var="searchTerm" param="Keyword" />
	<dsp:getvalueof param="showDepartment" var="showDepartment" />
	<dsp:getvalueof param="hideDepartmentOnNullResultPage" var="hideDepartmentOnNullResultPage" />
	<dsp:getvalueof var="emptyFacets" param="browseSearchVO.emptyFacets"/>
	<dsp:getvalueof var="emptyBox" param="browseSearchVO.emptyBox"/>
	<dsp:getvalueof var="currentCat" param="browseSearchVO.currentCatName"/>
	<c:set var="Attributes"><bbbc:config key="Attributes" configName="DimDisplayConfig" /></c:set>
	<c:set var="fromCollegeUrl" value="" />
	<c:if test="${fromCollege eq true or param.fromCollege eq true}">
		<c:set var="fromCollegeUrl" value="&fromCollege=true" />
	</c:if>
	<c:set var="inStoreParam" value="" />
	<c:set var="storeIdParam" value="" />
	<c:set var="storeIdParamNoStartingSlash" value="" />
	<c:if test="${inStore == 'true'}">
		<c:set var="inStoreParam" value="&inStore=true" />
	</c:if>
	<c:if test="${not empty storeIdFromURL}">
		<c:set var="storeIdParam" value="/store-${storeIdFromURL}/" />
		<c:set var="storeIdParamNoStartingSlash" value="store-${storeIdFromURL}/" />
	</c:if>
	<%--BBBSL-8830 | Adding category name in the all link --%>
	<dsp:getvalueof var="categoryName" param="categoryName" />
	<c:set var="plpViewParam" value="&view=${defaultView}${fromCollegeUrl}"/>
	<dsp:getvalueof var="subCategoryPageParam" param="subCategoryPageParam" />
	<c:if test="${showDepartment eq false}">
		<c:if test="${not empty seoUrl}">
			<c:set var="url" value="${contextPath}${seoUrl}" scope="request" />
		</c:if>
		<c:if test="${not empty param.narrowDown}">
		 	<c:set var="flurl" value="${url}"></c:set>
          	<c:set var="url" value="${url}${param.narrowDown}/"></c:set>
         </c:if>
	</c:if>

	<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="priceLabel"><bbbl:label key="lbl_facet_price_range" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="brandDisplayDim"><bbbc:config key="Brand" configName="DimDisplayConfig" /></c:set>
	<c:set var="leftAccordionExpand"><bbbl:label key="lbl_search_left_nav_accordion_expand" language ="${pageContext.request.locale.language}" />'</c:set>
	<c:set var="leftAccordionCollapse"><bbbl:label key="lbl_search_left_nav_accordion_collapse" language ="${pageContext.request.locale.language}" /></c:set>

<%-- Back to CLP Link on Category landing pages changes start--%>
  <dsp:getvalueof var="parentCatId"  param="browseSearchVO.categoryHeader.query"/>
	<c:if test="${not empty parentCatId}">
		<dsp:droplet name="CustomLandingTemplateDroplet">
			<dsp:param value="${parentCatId}" name="categoryId" />
			<dsp:param name="templateName" value="customLandingTemplate" />
				<dsp:oparam name="output">
					<dsp:param name="cmResponseVO" param="cmsResponseVO.responseItems[0]"/>
					<dsp:getvalueof var="clpName"  param="cmResponseVO.clpName"/>
					<dsp:getvalueof var="id"  param="cmResponseVO.id"/>
				 	 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" value="${id}" />
						<dsp:param name="itemDescriptorName" value="customLandingTemplate" />
						<dsp:param name="repositoryName" value="/com/bbb/cms/repository/CustomLandingTemplate" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="clpUrl" vartype="java.lang.String" param="url" />
	                  	     </dsp:oparam>
	              		 </dsp:droplet>
				</dsp:oparam>
				 <dsp:oparam name="empty">
				 	<c:set var="noClp" value="true"/>
			    </dsp:oparam>
		</dsp:droplet>
	</c:if>
<%-- Back to CLP Link on Category landing pages changes end--%>
	<c:if test="${emptyFacets}">
		<dsp:getvalueof var="facets" param="browseSearchVO.emptyFacetsList"/>
		<dsp:getvalueof var="facetSize" value="${totSize}" />
	</c:if>
	
	<c:if test="${(emptyFacets && facetSize ne 1) || !emptyFacets || swsTermsLength> 0 || fromCatL2AndL3}">
		<c:choose>
			<c:when test="${(!emptyFacets || (emptyFacets && emptyBox)) || !emptyBox}">
				<div id="facetBox">
			
					<c:if test="${floatNode ne 'true' and empty hideDepartmentOnNullResultPage}">
						<div class="facetCategories">
							<div class="categoryTitle">
								<h3><bbbl:label key='lbl_facet_cat' language="${pageContext.request.locale.language}" /></h3>
							</div>
							<dsp:getvalueof var="allTabList" param="browseSearchVO.allTabList" />
							<div class="facetCategories">
							   <dsp:getvalueof var="categoryId" param="categoryId" />
							   <%-- Back to CLP Link on Category landing pages changes start--%>
								<c:if test="${not empty clpUrl}">
									<div class="categoriesGroup fromClp">
									  <ul>
										<li><a href="${contextPath}${clpUrl}" title="">
					                      	<span class="backArrow">&nbsp;</span>${clpName}
					          				</a>
        								</li>
        								</ul>
        								</div>
        						</c:if>			
							<%-- Back to CLP Link on Category landing pages changes end--%>
								<c:if test="${noClp eq true or empty noClp }">
									<div class="categoriesGroup ">
							 			<ul>
						 		</c:if>
								<c:if test="${dontShowAllTab ne 'true'}">
								<li>
									<c:set var="all_cat_link">
											<%--BBBSL-8830 | Adding category name in the all link --%>
											<bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${categoryName}
										</c:set> <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:getvalueof var="idAll" param="browseSearchVO.categoryHeader.query" />
											<c:choose>
												<c:when test="${empty idAll}">
													<dsp:param name="id" value="${CatalogRefId}" />
												</c:when>
												<c:otherwise>
													<dsp:param name="id" param="browseSearchVO.categoryHeader.query" />
												</c:otherwise>
											</c:choose>
											<dsp:param name="itemDescriptorName" value="category" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												<c:url value="${finalUrl}${storeIdParamNoStartingSlash}" var="urlSe">
													<c:param name="subCatPlp" value="true" />
													<c:if test="${fromCollege eq 'true' }">
														<c:param name="fromCollege" value="true" />
													</c:if>
												</c:url>
												<dsp:a iclass="${allTabList}" href="${urlSe}" title="${all_cat_link}">${all_cat_link}</dsp:a>

											</dsp:oparam>
										</dsp:droplet>
									</li>
								</c:if>
								
								<c:set var="narrowDownUrl" value="" />
								<c:choose>
									<c:when test="${not empty param.narrowDown}">
										<c:set var="narrowDownUrl" value="${param.narrowDown}/" />
										<c:set var="finalRefinementTemp" value="?refType=false${fromCollegeUrl}" />
									</c:when>
									<c:when test="${fromCollege eq 'true' }">
										<c:set var="finalRefinementTemp" value="?fromCollege=true" />
									</c:when>
								</c:choose>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="browseSearchVO.categoryHeader.categoryRefinement" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="tabList" value="" />
											<dsp:getvalueof var="name" param="element.name" />
											<dsp:getvalueof var="subCatId" param="element.query" />
											<dsp:getvalueof var="totalSize" param="element.totalSize" />
											<c:if test="${(name eq currentCat) && (subCatId eq CatalogRefId)}">
												<dsp:getvalueof var="tabList" value="active" />
											</c:if>
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="element.query" />
												<dsp:param name="itemDescriptorName" value="category" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
													<c:url var="urlSe" value="${finalUrl}"/>
													<c:if test="${totSize gt 1}">
														<dsp:getvalueof var="refine" param="browseSearchVO.descriptors[0].descriptorFilter" />
														<c:set var="finalRefinement" value="${subCatId}-${refine}${storeIdParam}?refType=false${fromCollegeUrl}" />
													</c:if>
													<c:if test="${empty finalRefinement}">
														<c:set var="finalRefinement" value="${finalRefinementTemp}" />
													</c:if>
													<c:set var="name">
													<dsp:valueof param="element.name" /> (${totalSize})</c:set>
													<c:choose>
														<c:when test="${not empty urlSe}">
															<c:choose>
																<c:when test="${not empty finalRefinement}">
																	<c:set var="finlURL" value="${urlSe}${narrowDownUrl}${finalRefinement}"/>
																</c:when>
																<c:otherwise>
																	<c:set var="finlURL" value="${urlSe}${narrowDownUrl}${storeIdParamNoStartingSlash}" />
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:set var="finlURL" value="" />
														</c:otherwise>
													</c:choose>
													<li ><a class="${tabList}" href="${finlURL}" title="${name}" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}">${name}</a></li>
													<c:url value="${finalUrl}" var="urlSe">
														<c:if test="${fromCollege eq 'true' }">
															<c:param name="fromCollege" value="true" />
														</c:if>
														<c:if test="${inStore eq 'true'}">
															<c:param name="inStore" value="true"/>
														</c:if>
													</c:url>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									
									</dsp:droplet>
									<c:if test="${noClp eq true or empty noClp }">
										</ul>
									</div>
								</c:if>
							</div>
					</div>
					<script>BBB.addPerfMark('ux-secondary-content-displayed');</script>
				</c:if>
<!-- End -->	
				<div id="searchBox">
					<form id="frmSearchCriteria" method="post">
						<fieldset class="searchGroup">
							<div class="searchTitle">
								<dsp:getvalueof var="descriptors" param="browseSearchVO.descriptors" />
								
								<c:set var="elementDesc" value="${descriptors[totSize-1]}"/>
								<dsp:getvalueof var="type" value="${elementDesc.rootName}" />
								<c:if test="${(type ne 'RECORD TYPE' and type ne 'DEPARTMENT') or swsTermsLength> 0 }">
									<h3>
									<bbbl:label key="lbl_search_facet_bar" language="${pageContext.request.locale.language}" />
									</h3>
								</c:if>
								<c:if test="${(totSize ge 0) && (type ne 'DEPARTMENT')}">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" value="${CatalogRefId}" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											<c:url value="${finalUrl}${storeIdParamNoStartingSlash}" var="urlSe">
												<c:param name="subCatPlp" value="true" />
												<c:if test="${fromCollege eq 'true' }">
													<c:param name="fromCollege" value="true" />
												</c:if>
												<c:if test="${inStore eq 'true'}">
													<c:param name="inStore" value="true"/>
												</c:if>
												<c:param name="view" value="${defaultView}"/>
											</c:url>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
								
								<%--BBBSL-6423 START | variable to contain total count of sws filters and facet filters --%>
								<c:set var="facetCountForRefType" value="${swsTermsLength}"/>
								<%--BBBSL-6423 END --%>
								<dsp:getvalueof var="facetCountForRefTypeParam" param="browseSearchVO.facetCountForRefTypeParam" />
								<c:set var="facetCountForRefTypeParam" value="${facetCountForRefTypeParam + facetCountForRefType}"/>
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
										
										<c:choose>
											<c:when test="${type eq 'DEPARTMENT'}">
												<dsp:getvalueof var="facetFilter" param="selectedFacetRefItem.descriptorFilter"/>
												<dsp:getvalueof var="catIdFilter" param="selectedFacetRefItem.categoryId"/>
												<c:set var="filter" value="${catIdFilter}-${facetFilter}" ></c:set>
											</c:when>
											<c:when test="${type ne 'RECORD TYPE'}">
												<c:if test="${not empty facetDescFilter}">
													<li class="clearfix">
														<span class="searchListItem">
															<span class="searchItem">
																<dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" />
															</span>
															<a  href="${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${refTypeParam}${inStoreParam}" class="lnkSearchRemove" title="Remove" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-value="${swsterms}">X</a>
														</span>
													</li>
												</c:if>
											</c:when>
										</c:choose>
									</dsp:oparam>
									
									<dsp:oparam name="outputEnd">
									<c:set var="filterParam"></c:set>
									<c:if test="${not empty filter}">
										<c:set var="filterParam">${filter}/</c:set>
									</c:if>
									<c:forEach var="narrowDownFilter" items="${swsTermsList}">
										<c:if test="${not empty narrowDownFilter}">
			                               <li class="clearfix">
												<span class="searchListItem">
													<span class="searchItem">
														<dsp:valueof value="${narrowDownFilter.name}" valueishtml="true" />
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
													<a  href="${flurl}${narrowDownFilterValue}${filterParam}${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}${refTypeParam}${inStoreParam}" class="lnkSearchRemove" title="Remove" data-value="${narrowDownFilter.removalValue}">X</a>
													<c:if test="${not empty hideDepartmentOnNullResultPage}">
														<c:set var="previousPage" scope="request">${flurl}${narrowDownFilterValue}${filterParam}${pageSize}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true</c:set>
														<c:set var="removalValue" scope="request">${narrowDownFilter.removalValue}</c:set>
													</c:if>
												</span>
											</li>
										</c:if>
                                      </c:forEach>

									<li>
										<c:if test="${(type ne 'RECORD TYPE' and type ne 'DEPARTMENT') or swsTermsLength> 0 }">
											<c:set var="clear">
                                      			<bbbl:label key="lbl_search_filter_box_clearall" language="${pageContext.request.locale.language}" />
                                      		</c:set>
                                   			<c:if test="${empty urlSe}">
                                   				<c:set var="urlSe">
												${flurl}${storeIdParamNoStartingSlash}?subCatPlp=true${fromCollegeUrl}&refType=true${inStoreParam}
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

				<dsp:getvalueof var="prodCount" param="browseSearchVO.bbbProducts.bbbProductCount"/>
				<c:choose>
					<c:when test="${prodCount>=2}"  >
						<c:set var="displayHideBlock"><bbbl:label key="lbl_block_small" language="${pageContext.request.locale.language}" /></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="displayHideBlock"><bbbl:label key="lbl_none" language="${pageContext.request.locale.language}" /></c:set>
					</c:otherwise>
				</c:choose>		
					<!-- Narrow Search Content Start -->
				<fieldset class="facetGroup firstGroup narrowSearch" style="display:${displayHideBlock}">
				<legend class="hidden"><bbbl:label key="lbl_narrow_your_search" language="${pageContext.request.locale.language}" /></legend>
					<div class="facetTitle padTop_10">
						<h5 class="noMar noPad"><span><bbbl:label key="lbl_narrow_your_search" language="${pageContext.request.locale.language}" /></span></h5>
					</div>
					<div class="facetContent">
						<form method="post" action="#" id="narrowForm">
					         <div class="narrowSearch">
					        	<input id="additionalKeyword" class="removeDisabled" disabled="disabled" type="text" value="" title="narrowDown" name="additionalKeywordsearch" placeholder="<bbbl:label key="lbl_narrow_search_within" language="${pageContext.request.locale.language}" />" />
					        	<input type="submit" class="removeDisabled hidden" disabled="disabled" value="submit" title="narrow">
					        	<a role="button" title="narrow" aria-label="submit" class="removeDisabled facetSearchLink" href="javascript:void(0);"></a>
					             <input type="hidden" name="additionalKeyword" value="" />
								<c:choose>
									<c:when test="${not empty param.narrowDown and totSize gt 2}">
										<c:set var="urlPath" scope="request"> ${flurl}${param.narrowDown}$swskeyword/${filter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}</c:set>
									</c:when>
									<c:when test="${empty param.narrowDown and totSize gt 1}">
										<c:set var="urlPath"  scope="request"> ${url}$swskeyword/${filter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}</c:set>
									</c:when>
									<c:when test="${empty param.narrowDown and totSize eq 1}">
										<c:set var="urlPath"  scope="request"> ${url}$swskeyword${storeIdParam}?&refType=true${fromCollegeUrl}${inStoreParam}</c:set>
									</c:when>
									<c:otherwise>
										<c:set var="urlPath"  scope="request"> ${flurl}${param.narrowDown}$swskeyword${storeIdParam}?&refType=true${fromCollegeUrl}${inStoreParam}</c:set>
									</c:otherwise>
								</c:choose>
								<input type="hidden" name="url" id="urlPath" value="${urlPath}"/>
					         </div>
						</form>
						<form method="post" id="hiddenFacetedForm" action="#">
							<input type="hidden" name="keyword" value="">
			                <input type="hidden" name="swsterms" value="${swsterms}">
							<input type="submit" class="hidden" value="">
						</form>
			
					</div>
				</fieldset>
				<script>BBB.addPerfMark('ux-secondary-content-displayed');</script>
			</c:when>
		</c:choose>
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
				<legend class="hidden">${title}</legend>
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
										 value="${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}" name="brandSearch"
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
															<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/><label for="${refinedFacetName}_${idx}"> 
															<dsp:valueof param="element.name" valueishtml="true"/>
															&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
														</c:if>
													</c:when>
													<c:otherwise>
															<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/><label for="${refinedFacetName}_${idx}"> 
															<dsp:valueof param="element.name" valueishtml="true"/>
															&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
											<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/> <label for="${refinedFacetName}_${idx}"> 
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
											<a class="dynFormSubmit" onclick="omnitureRefineCall('${facetName}', '${refinedName_2}');" href="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/> data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}"> 
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
										onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/><dsp:valueof param="element.name" /> 
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
	
	<c:if test="${(!emptyFacets || (emptyFacets && emptyBox)) || !emptyBox}">
			<dsp:getvalueof var="prodCount" param="browseSearchVO.bbbProducts.bbbProductCount"/>
			<c:if test ="${swsTermsLength== 0 && totSize==1 && facetSize==1 && prodCount<2 && !fromCatL2AndL3}">
			<div>
			</c:if>
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
