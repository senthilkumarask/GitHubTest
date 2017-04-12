<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet"/>
<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/SearchDescriptorDroplet" />
<dsp:getvalueof var="siteId" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
<dsp:getvalueof var="count" param="count"/>
<dsp:getvalueof var="openfacet" param="openfacet"/>
<dsp:getvalueof var="facetName" param="facetName" />
<dsp:getvalueof var="filterName" param="filterName" />
<dsp:getvalueof var="facetId" param="facetId" />
<dsp:getvalueof var="x" param="x" />
<dsp:getvalueof var="sizeFilter" param="sizeFilter" />
<dsp:getvalueof var="redirectUrl" param="redirectUrl"/> 
<dsp:getvalueof var="size" param="size"/>
<c:set var="pageSize" value="1-${size}"/>
<c:set var="keepCache" value="false"/>
<dsp:getvalueof var="categoryId" param="categoryId"/>
<c:set var="sessCat" value="${categoryId}" scope="session"/>
<dsp:getvalueof var="paginationPage" param="paginationPage"/>
<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
<dsp:getvalueof var="totSize" value="${fn:length(browseSearchVO.descriptors)}" />
<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
<dsp:getvalueof var="narrowSearchUsed" param="narrowSearchUsed"/> 
<input type="hidden" name="filtersAlreadyApplied" value="${param.categoryId}" id="filtersAlreadyApplied"> 
	<c:if test="${not empty subCatPlp && subCatPlp eq true}">
					<c:set var="subCategoryPageParam">&subCatPlp=true&a=1</c:set>
				</c:if>	
<c:choose>
	<c:when test="${not empty paginationPage and paginationPage eq 'true'}">
		<c:set var="fromPaginationPage" value="true"></c:set>
	</c:when>		
	<c:otherwise>
		<c:set var="fromPaginationPage" value="false"></c:set>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${narrowSearchUsed eq 'true' and fromPaginationPage eq 'false'}">
				<input type="hidden" name="redirectUrl" value="${redirectUrl}" id="redirectUrl"> 		
	</c:when>			
	<c:otherwise>
		<c:if test="${fromPaginationPage eq 'false'}">
			<input type="hidden" name="redirectUrl" value="" id="redirectUrl"> 
			<dsp:droplet name="CategoryLandingDroplet">
				<dsp:param param="categoryId" name="id" />
				<dsp:param name="siteId" value="${siteId}"/>
				<dsp:param name="catFlg" value="true"/>
				<dsp:param name="subCatPlp" value="false"/>
				<dsp:param name="fetchSubCategories" value="true"/>		
				<dsp:oparam name="output">
					 <dsp:getvalueof var="seoUrl" param="categoryVO.seoURL"/>
		 		</dsp:oparam>		 
		 	</dsp:droplet>	
		 </c:if>			 																	
	                    <div id="facetBox" class="filterContainer">	                
							<div id="scrollwrap">
								<div id="searchBox" <c:if test="${fromPaginationPage eq 'false'}"> class="hidden" </c:if>> 
					<form id="frmSearchCriteria" method="post">
						<fieldset class="searchGroup">
							<div class="searchTitle">
								<dsp:getvalueof var="descriptors" param="browseSearchVO.descriptors" />								
								<c:set var="elementDesc" value="${descriptors[totSize-1]}"/>
								<dsp:getvalueof var="type" value="${elementDesc.rootName}" />
								<c:if test="${(type ne 'RECORD TYPE' and type ne 'DEPARTMENT') or swsTermsLength> 0 }">
									
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
				<fieldset class="facetGroup firstGroup narrowSearch" style="display:none">
				<legend class="hidden">NARROW YOUR SEARCH</legend>
					
					<div class="facetContent">
						<form method="post" action="#" id="narrowForm">
					         <div class="narrowSearch">
					         <span class="icon icon-search" aria-hidden="true"></span>
					        	<input id="additionalKeyword" class="removeDisabled escapeHTMLTag" type="text" value="" title="narrowDown" name="additionalKeywordsearch" placeholder="Search Within">
					        	<input type="submit" class="removeDisabled hidden" value="submit" title="narrow">
					        	<!-- <a role="button" title="narrow" aria-label="submit" class="removeDisabled facetSearchLink" href="javascript:void(0);" onclick="s_objectID=&quot;javascript:void(0);_43&quot;;return this.s_oc?this.s_oc(e):true"></a> -->
					             <input type="hidden" name="additionalKeyword" value="">
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
					</div>
				</fieldset> 
				
				<dsp:droplet name="SearchDescriptorDroplet">
				<dsp:param name="searchResults" param="browseSearchVO"/>
					<dsp:oparam name="output">
					 <dsp:droplet name="ForEach">
						<dsp:param name="array" param="descriptorMap"/>
						<dsp:getvalueof  param="key" var="rootName"/>
						 <dsp:getvalueof param="element" var="descriptorVOs"/>
						<dsp:oparam name="output">
							<c:if test="${rootName ne 'DEPARTMENT'}">
								<fieldset class="facetGroup firstgroup">
										<legend class="hidden">${rootName}</legend>
										<div class="facetTitle padTop_10">	
												<a href="#"  title="${rootName}" aria-label="${rootName} ${leftAccordionCollapse}">${rootName}</a>
										</div>
										<c:set var="facetIdies" value="" scope="page" />
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${descriptorVOs}"/>
											<dsp:getvalueof var="descVo" param="element" />
											<dsp:oparam name="outputStart">
												<div class="facetContent">
												<div  aria-hidden="false" class="flyoutAccordian facetScroller">
												<ul class="facetList">	
											</dsp:oparam>
											<dsp:oparam name="output">
											    <c:choose>
														<c:when test="${empty facetIdies}">
															<c:set var="facetIdies" value="${descVo.categoryId}" scope="page"/>
														</c:when>
														<c:otherwise>
															<c:set var="facetIdies" value="${facetIdies},${descVo.categoryId}" scope="page"/>
														</c:otherwise>
												</c:choose>
												<li class="facetListItem clearfix <c:if test="${facetName eq descVo.rootName || filterName eq descVo.categoryId}">selectedFil</c:if>">
												<div class="checker">
												<span class="checked">
													<input id="selectedBrand" class="linkFilter" data-submit-param-length="5" data-submit-param1-name="swsterms"  data-submit-param1-value="${swsterms}" data-submit-param2-name="facetName" data-submit-param2-value="${descVo.rootName}" data-submit-param3-name="filterName" data-submit-param3-value="${descVo.categoryId}" data-submit-param4-name="x" data-submit-param4-value="false" data-submit-param5-name="sizeFilter" data-submit-param5-value="${sizeFilter}" 
										onclick="omnitureRefineCall('${descVo.rootName}', '${descVo.name}');" value="${contextPath}${seoUrl}${escUrl}${descVo.descriptorFilter}/${pageSize}${storeIdParam}?${descVo.removalQuery}${subCategoryPageParam}${searchQueryParams}&refType=true${inStoreParam}${swdFlag}&facetId=${facetIdies}" title="${descVo.name}" aria-label="${descVo.name}&nbsp;. <dsp:valueof param='element.size' />&nbsp;.">
													 </input>
													 </span>
													   </div>  	
													   <label for="selectedBrand"> 
													   ${descVo.name}
													   </label>	
													</li>
													</dsp:oparam>
													<dsp:oparam name="outputEnd">
													</ul>					
												</div>
											</div>
											</dsp:oparam>
											
										</dsp:droplet> 
									</fieldset>
							</c:if>
						</dsp:oparam>
					</dsp:droplet> 
				</dsp:oparam>
				
				</dsp:droplet>
						
				
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:getvalueof var="index" param="index"/>
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
									<a href="#"  title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName}</a>
								</div>
							</c:if>
						</c:when>
						<c:otherwise>
						<c:set var="scrollerClass">facetScroller</c:set>
							<div class="${divClass}">
								<c:choose>
									<c:when test="${fromPaginationPage eq 'true'}">
										<a href="#" <c:if test="${!(index == 0) && !(index == 1)}">data-default="hide"</c:if> title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName}</a>									
									</c:when>
									<c:otherwise>
										<a href="#" <c:if test="${!fn:containsIgnoreCase(openfacet, displayName)}">data-default="hide" class="clos"</c:if> title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName}</a>
									</c:otherwise>
								</c:choose>							
							</div>
						</c:otherwise>
					</c:choose>						
				<div class="facetContent" <c:if test="${fromPaginationPage eq 'false' and !fn:containsIgnoreCase(openfacet, displayName)}">style="display:none"</c:if>>
					<c:choose><%-- For displaying brand typeahead box --%>						
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
					<div  aria-hidden="false" class="flyoutAccordian ${scrollerClass}">							
						<ul class="${facetListStyle}">						
				<%-- For multi selected facets show selected refinements/descriptors --%>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="element.facetDescriptors" />
						<dsp:oparam name="false">
						   <c:set var="facetIdies" value="" scope="page" />
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
									<c:set var="brandNameTitle"><dsp:valueof param="selectedFacetRefItem.categoryId" valueishtml="true" /></c:set>
									
									<c:choose>
										<c:when test="${empty facetIdies}">
											<c:set var="facetIdies" value="${brandNameTitle}" scope="page"/>
										</c:when>
										<c:otherwise>
											<c:set var="facetIdies" value="${facetIdies},${brandNameTitle}" scope="page"/>
										</c:otherwise>
								</c:choose>
									
									
									<c:choose>
										<c:when test="${fromPaginationPage eq 'true'}">
											<c:set var="filterSelected" value="true"/>
											<input type="hidden" name="filtersonLoad" value="${filterSelected}"/>
											
											<li class="facetListItem clearfix <c:if test="${(filterName eq brandNameTitle) || (fn:contains(facetId,filterName))}">selectedFil</c:if>">	
												<%--   R2.2 Story 116C Changes--%>
												  <%-- R2.2 SEO Friendly URL changes--%>
												 <input type="checkbox" id="${refinedFacetName}_${selectedFacetRefItemName}" data-filtername="${brandNameTitle}"
												onclick="omnitureRefineCall('${facetName}', '${refinedName_0}');"
												 value="${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}&facetId=${facetIdies}" name="brandSearch"
												 title="${selectedFacetRefItemName}" checked="checked" class="checkbox facetedCheckBox" aria-checked="true" aria-describedby="${facetName}_${selectedFacetRefItemName}" />
												 <label for="${refinedFacetName}_${selectedFacetRefItemName}"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true"/></label>
											</li>
										</c:when>
										<c:otherwise>
											
											<li class="facetListItem clearfix <c:if test="${(filterName eq brandNameTitle) || (fn:contains(facetId,filterName))}">selectedFil</c:if>">										
												<%--   R2.2 Story 116C Changes--%>
												  <%-- R2.2 SEO Friendly URL changes--%>										  
												 <input type="checkbox" id="${refinedFacetName}_${selectedFacetRefItemName}" data-filtername="${brandNameTitle}"
												onclick="omnitureRefineCall('${facetName}', '${refinedName_0}');"
												 value="${contextPath}${seoUrl}${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}&facetId=${facetIdies}" name="brandSearch"
												 title="${selectedFacetRefItemName}" checked="checked" class="checkbox facetedCheckBox" aria-checked="true" aria-describedby="${facetName}_${selectedFacetRefItemName}" />
												 <label for="${refinedFacetName}_${selectedFacetRefItemName}"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true"/></label>
											</li>
										</c:otherwise>
									</c:choose>
									
									
									
									
								</dsp:oparam>
							</dsp:droplet>
							<%--End ForEach--%>
						</dsp:oparam>
					</dsp:droplet>
					<%--End IsEmpty--%>	
				
							
				<%--Show facets refinements--%>
				<dsp:getvalueof var="facetParentVoName" param="element.name"/>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="element.facetRefinement" />
						<dsp:oparam name="output">
							<c:choose>
										<%-- Show refinements for brand, attributes and color as checkbox--%>
								<c:when test="${multiSelectFacet}">
								<c:set var="facetItemTitle"><dsp:valueof param="element.name" valueishtml="true"/>
										</c:set>
										<c:set var="filterId"><dsp:valueof param="element.catalogId" valueishtml="true"/>
										</c:set>
									<li class="facetListItem clearfix <c:if test="${fromPaginationPage eq 'false' and filterName eq facetItemTitle}">selectedFil</c:if>">
										<dsp:getvalueof var="facetRefItem" param="element.name" />
										<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
										<dsp:getvalueof var="facetItemQuery" param="element.query" />
										<dsp:getvalueof var="refinedName_1" param="element.refinedName" />
										<dsp:getvalueof var="idx" param="index" />
										
										<dsp:getvalueof var="intlFlagAtt" param="element.intlFlag"/>
										<c:choose>
												<c:when test="${facetParentVoName eq Attributes }">
												<c:choose>
													<c:when test="${isInternationalCustomer eq true}">
														<c:if test="${intlFlagAtt eq 'Y'}">
															<c:choose>
																<c:when test="${fromPaginationPage eq 'true'}">
																	<input type="checkbox" id="${refinedFacetName}_${idx}"  data-filtername="${filterId}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/><label for="${refinedFacetName}_${idx}"> 
																	<dsp:valueof param="element.name" valueishtml="true"/>
																	&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
																</c:when>
																<c:otherwise>
																	<input type="checkbox" id="${refinedFacetName}_${idx}" data-filtername="${filterId}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${contextPath}${seoUrl}${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/><label for="${refinedFacetName}_${idx}"> 
																	<dsp:valueof param="element.name" valueishtml="true"/>
																	&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
																</c:otherwise>
															</c:choose>																										
															
														</c:if>
													</c:when>
													<c:otherwise>
															<c:choose>
																<c:when test="${fromPaginationPage eq 'true'}">
																	<input type="checkbox" id="${refinedFacetName}_${idx}"  data-filtername="${filterId}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/><label for="${refinedFacetName}_${idx}"> 
																	<dsp:valueof param="element.name" valueishtml="true"/>
																	&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
																</c:when>
																<c:otherwise>
																	<input type="checkbox" id="${refinedFacetName}_${idx}" data-filtername="${filterId}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${contextPath}${seoUrl}${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/><label for="${refinedFacetName}_${idx}"> 
																	<dsp:valueof param="element.name" valueishtml="true"/>
																	&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
																</c:otherwise>
															</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>	
												<c:choose>
													<c:when test="${fromPaginationPage eq 'true'}">
														<input type="checkbox"  data-filtername="${filterId}" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/> <label for="${refinedFacetName}_${idx}"> 
														<dsp:valueof param="element.name" valueishtml="true"/>
														&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
													</c:when>
													<c:otherwise>
														<input type="checkbox" id="${refinedFacetName}_${idx}" data-filtername="${filterId}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${contextPath}${seoUrl}${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}"  name="brandSearch" title="${facetItemTitle}" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;."/> <label for="${refinedFacetName}_${idx}"> 
														<dsp:valueof param="element.name" valueishtml="true"/>
														&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span></label>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</li>
								</c:when>
								<c:when test="${facetName == department}">
									<dsp:getvalueof param="showDepartment" var="showDepartment" />
									<c:if test="${showDepartment eq 'true'}">
									<dsp:getvalueof var="string" param="element.name" />
									<dsp:getvalueof var="filterId" param="element.catalogId" />
										<li class="facetListItem clearfix <c:if test="${fromPaginationPage eq 'false' and filterName eq filterId}">selectedFil</c:if>">
											
											<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
											<dsp:getvalueof var="facetItemQuery" param="element.query"/>
											<dsp:getvalueof var="refinedName_2" param="element.refinedName" />
											<%--R2.2 Story 116C Changes--%>
											<%--R2.2 SEO Friendly URL changes --%>	
											<c:choose>
												<c:when test="${fromPaginationPage eq 'true'}">
													<a class="linkFilter" data-submit-param-length="5" data-submit-param1-name="" data-submit-param2-name="facetName" data-submit-param2-value="${facetName}" data-submit-param3-name="filterName" data-submit-param3-value="<dsp:valueof param='element.catalogId'/>" data-submit-param4-name="x" data-submit-param4-value="true" data-submit-param5-name="sizeFilter" data-submit-param5-value="<dsp:valueof param='element.size' />" onclick="omnitureRefineCall('${facetName}', '${refinedName_2}');" href="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param='element.name'/>" aria-label="<dsp:valueof param='element.name'/>&nbsp;. <dsp:valueof param='element.size' />&nbsp;."> data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}"> 
														<%--<img width="24" height="24" src="${imagePath}/_assets/global/images/icons/<c:out value="${fn:toLowerCase(string)}"/>.gif" />--%>
														<span class="facetDepartmentListItemName"><dsp:valueof param="element.name" /></span>
														<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span>
													</a>
												</c:when>
												<c:otherwise>
													<a class="linkFilter" data-submit-param-length="5" data-submit-param1-name="" data-submit-param2-name="facetName" data-submit-param2-value="${facetName}" data-submit-param3-name="filterName" data-submit-param3-value="<dsp:valueof param='element.catalogId'/>" data-submit-param4-name="x" data-submit-param4-value="true" data-submit-param5-name="sizeFilter" data-submit-param5-value="<dsp:valueof param='element.size' />" onclick="omnitureRefineCall('${facetName}', '${refinedName_2}');" href="${contextPath}${seoUrl}${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param='element.name'/>" aria-label="<dsp:valueof param='element.name'/>&nbsp;. <dsp:valueof param='element.size' />&nbsp;."> data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}"> 
														<%--<img width="24" height="24" src="${imagePath}/_assets/global/images/icons/<c:out value="${fn:toLowerCase(string)}"/>.gif" />--%>
														<span class="facetDepartmentListItemName"><dsp:valueof param="element.name" /></span>
														<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span>
													</a>
												</c:otherwise>
											</c:choose>
										</li>
									</c:if>
								</c:when>
								<c:otherwise>
								<dsp:getvalueof var="string" param="element.name" />
								<dsp:getvalueof var="filterId" param="element.catalogId" />
									<li class="facetListItem clearfix <c:if test="${fromPaginationPage eq 'false' and filterName eq filterId}">selectedFil</c:if>">
										<c:set var="escUrl"><c:out value="${url}"/></c:set>
										<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
										<dsp:getvalueof var="intlFlagAtt" param="element.intlFlag"/>
										<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
										<dsp:getvalueof var="facetItemQuery" param="element.query"/>						
										 <%-- R2.2 Story 116C Changes--%>
												 <%--R2.2 SEO Friendly URL changes--%>												 
										<c:choose>
											<c:when test="${fromPaginationPage eq 'true'}">
												<a class="linkFilter" data-submit-param-length="5" data-submit-param1-name="swsterms" data-submit-param1-value="${swsterms}" data-submit-param2-name="facetName" data-submit-param2-value="${facetName}" data-submit-param3-name="filterName" data-submit-param3-value="<dsp:valueof param='element.catalogId'/>" data-submit-param4-name="x" data-submit-param4-value="false" data-submit-param5-name="sizeFilter" data-submit-param5-value="<dsp:valueof param='element.size' />"
												onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param='element.name'/>" aria-label="<dsp:valueof param='element.name'/>&nbsp;. <dsp:valueof param='element.size' />&nbsp;."><dsp:valueof param="element.name" /> 
												<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </a>
											</c:when>
											<c:otherwise>
												<a class="linkFilter" data-submit-param-length="5" data-submit-param1-name="swsterms"  data-submit-param1-value="${swsterms}" data-submit-param2-name="facetName" data-submit-param2-value="${facetName}" data-submit-param3-name="filterName" data-submit-param3-value="<dsp:valueof param='element.catalogId'/>" data-submit-param4-name="x" data-submit-param4-value="false" data-submit-param5-name="sizeFilter" data-submit-param5-value="<dsp:valueof param='element.size' />" 
												onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${contextPath}${seoUrl}${escUrl}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}&refType=true${inStoreParam}${swdFlag}" title="<dsp:valueof param='element.name'/>" aria-label="<dsp:valueof param='element.name'/>&nbsp;. <dsp:valueof param='element.size' />&nbsp;."><dsp:valueof param="element.name" /> 
												<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </a>
											</c:otherwise>
										</c:choose>
									</li>
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

</div>
	<div class="filterWrap">
		<div class="applyFilterBtn">
			<c:choose>
				<c:when test="${fromPaginationPage eq 'true'}">
					<a href="javascript:void(0);" role='button' aria-label="Apply selected filters. The page will reload once applied" class="applyFiltr"  onclick="" ><bbbl:label key="lblApplyFilter" language="${pageContext.request.locale.language}" /> <div style="display:none"><c:if test="${filterSelected eq 'true'}">(${count})</c:if></div></a>
				</c:when>
				<c:otherwise>
					<a href="${redirectUrl}<c:if test="${keepCache eq 'true'}">&keepCache=true</c:if> role='button' aria-label="Apply selected filters. The page will reload once applied" class="applyFiltr"  onclick="" ><bbbl:label key="lblApplyFilter" language="${pageContext.request.locale.language}" /> <div style="display:none">(${count})</div></a>
				</c:otherwise>
			</c:choose>
			
		</div>
	</div>	
	</div>
							
			<c:if test="${fromPaginationPage eq 'false'}">
				<style>
	                .selector_arrowOnly.focus{
		             border:1px dotted #666 !important;
	                 }
               	</style>               
				<input type="submit" class="hidden" value="" />
			</c:if>
		  </c:otherwise>
		</c:choose>	                                      
</dsp:page>
							
