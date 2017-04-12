<%--R2.2 Story 116A Changes--%>
<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
	<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="facets" param="browseSearchVO.facets" />
	<dsp:getvalueof var="emptyFacets" value="false" />
	<dsp:getvalueof var="emptyBox" value="true" />
	<dsp:getvalueof var="totSize" param="totSize" />
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO" />
	<dsp:getvalueof var="dontShowAllTab" param="showDepartment" />
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="floatNode" param="floatNode"/>
	<c:set var="pageSize" value="1-${size}"/>
	<dsp:getvalueof param="showDepartment" var="showDepartment" />
	<c:set var="plpViewParam" value="&view=${defaultView}"/>

	<c:set var="brandFacetDisplayName">
	    <bbbc:config key="Brand" configName="DimDisplayConfig" />
    </c:set>
    <c:set var="colorGroupFacetDisplayName">
        <bbbc:config key="ColorGroup" configName="DimDisplayConfig" />
    </c:set>
	<c:set var="typesOfPersonalization">
  		<bbbc:config key="Eligible_Customizations" configName="DimDisplayConfig"></bbbc:config>
  	</c:set>
	
	<c:if test="${showDepartment eq false}">
			<c:if test="${not empty seoUrl}">
				<c:set var="url" value="${contextPath}${seoUrl}" scope="request" />
			</c:if>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${facets}" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="size" param="size" />
					<dsp:getvalueof var="facetName" param="element.name" />
					<c:if test="${(size eq 1) && (facetName eq 'DEPARTMENT')}">
						<dsp:getvalueof var="plpOnlyDeptLeft" value="true" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
	</c:if>		
	<c:set var="department">
		<bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="bracOpen">
		<bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="bracClose">
		<bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="priceLabel">
		<bbbl:label key="lbl_facet_price_range" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="brandCount" value="0" />
	<c:set var="schoolCount" value="0" />
	<c:set var="attributeCount" value="0" />
	<c:set var="colorCount" value="0" />
	<c:set var="priceCount" value="0" />
	<c:set var="persCount" value="0" />
	<c:if test="${empty facets || plpOnlyDeptLeft eq true}">
		<dsp:getvalueof var="facets" param="browseSearchVO.descriptors" />
		<dsp:getvalueof var="emptyFacets" value="true" />
		<dsp:getvalueof var="emptyBox" value="false" />
		<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${facets}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="size" param="size" />
				<dsp:getvalueof var="facetName" param="element.rootName" />
				<c:if test="${(size gt 1) && (facetName eq brandFacetDisplayName || facetName eq typesOfPersonalization ||  facetName eq 'ATTRIBUTES' || facetName eq colorGroupFacetDisplayName || facetName eq 'PRICE RANGE')}">
					<dsp:getvalueof var="emptyBox" value="true" />
				</c:if>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:getvalueof var="size" param="size" />
		<c:if test="${(emptyFacets && size ne 1) || !emptyFacets}">
			<dsp:oparam name="outputStart">
			<c:choose>
				<c:when test="${(!emptyFacets || (emptyFacets && emptyBox)) || !emptyBox}">
										
						<c:if test="${floatNode ne 'true'}">
							<div class="row">
								<h3><bbbl:label key='lbl_facet_cat' language="${pageContext.request.locale.language}" /></h3>
								
								<dsp:getvalueof var="name" param="browseSearchVO.categoryHeader.name" />
								<dsp:getvalueof var="rootQuery" param="browseSearchVO.categoryHeader.query" />
								<dsp:getvalueof var="portraitEligible" param="browseSearchVO.categoryHeader.isPortraitEligible" />
								<dsp:getvalueof var="allTabList" value="" />
								<dsp:getvalueof var="allHighlighted" value="false" />
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="browseSearchVO.descriptors" />
									<dsp:param name="elementName" value="selectedFacetRefItem" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="selectedFacetRefItemName" param="selectedFacetRefItem.name" />
										<dsp:getvalueof var="selectedRefItemRoot" param="selectedFacetRefItem.rootName" />
										<dsp:getvalueof var="selectedRefItemId" param="selectedFacetRefItem.categoryId" />
										<c:if test="${selectedRefItemRoot eq department}">
											<dsp:getvalueof var="currentCat" value="${selectedFacetRefItemName}" />
											<dsp:getvalueof var="currentCatId" value="${selectedRefItemId}" />
										</c:if>
										<c:if test="${(name eq currentCat) && (rootQuery eq CatalogRefId)}">
											<dsp:getvalueof var="allTabList" value="active" />
											<dsp:getvalueof var="allHighlighted" value="true" />
											<c:if test="${portraitEligible eq 'true'}">
												<dsp:getvalueof var="portrait" value="true" />
											</c:if>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								
										<ul class="category-list">
											<li>
											<c:if test="${dontShowAllTab ne 'true'}">
												<c:set var="all_cat_link">
														<bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />
													</c:set> <dsp:droplet name="CanonicalItemLink">
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
															<c:url value="${finalUrl}" var="urlSe">
																<c:param name="subCatPlp" value="true" />
																<c:if test="${fromCollege eq 'true' }">
																	<c:param name="fromCollege" value="true" />
																</c:if>
																<c:param name="view" value="${defaultView}"/>
															</c:url>
															<c:if test="${allTabList eq 'active'}">
																<c:set var="var4" value="${all_cat_link}" />
															</c:if>
															<dsp:a iclass="${allTabList}" href="${urlSe}" title="${all_cat_link}">${all_cat_link}</dsp:a>
															
														</dsp:oparam>
													</dsp:droplet>
											</c:if>
											</li>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="browseSearchVO.categoryHeader.categoryRefinement" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="tabList" value="" />
													<dsp:getvalueof var="name" param="element.name" />
													<dsp:getvalueof var="subCatId" param="element.query" />
													<dsp:getvalueof var="totalSize" param="element.totalSize" />
													<dsp:getvalueof var="portraitElig" param="element.isPortraitEligible" />
													<c:if test="${(name eq currentCat) && (subCatId eq CatalogRefId)}">
														<dsp:getvalueof var="tabList" value="active" />
														<c:if test="${portraitElig eq 'true'}">
															<dsp:getvalueof var="portrait" value="true" />
														</c:if>
													</c:if>
													<dsp:droplet name="CanonicalItemLink">
														<dsp:param name="id" param="element.query" />
														<dsp:param name="itemDescriptorName" value="category" />
														<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
															<c:url value="${finalUrl}" var="urlSe">
																<c:if test="${fromCollege eq 'true' }">
																	<c:param name="fromCollege" value="true" />
																</c:if>
																<c:param name="view" value="${defaultView}"/>
															</c:url>
															<c:set var="name">
																<dsp:valueof param="element.name" /> (${totalSize})</c:set>
															<li ><dsp:a iclass="${tabList}" href="${urlSe}" title="${name}">${name}</dsp:a></li>
															<c:if test="${tabList eq 'active'}">
																<c:set var="var4" value="${name}" />
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
										</ul>
									
							</div>
						</c:if>
						<c:if test="${totSize gt 1}">
							<div class="row filter-applied">
								<form id="frmSearchCriteria" method="post">
										
										<h3>
											<bbbl:label key="lbl_search_facet_bar" language="${pageContext.request.locale.language}" />
										
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="browseSearchVO.descriptors" />
												<dsp:oparam name="outputStart">
													<dsp:getvalueof var="size" param="size" />
													<dsp:getvalueof var="type" param="element.rootName" />
													<c:if test="${(size ge 1) && (type ne 'DEPARTMENT')}">
														<dsp:droplet name="CanonicalItemLink">
															<dsp:param name="id" value="${CatalogRefId}" />
															<dsp:param name="itemDescriptorName" value="category" />
															<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
																<c:url value="${finalUrl}" var="urlSe">
																	<c:param name="subCatPlp" value="true" />
																	<c:if test="${fromCollege eq 'true' }">
																		<c:param name="fromCollege" value="true" />
																	</c:if>
																	<c:param name="view" value="${defaultView}"/>
																</c:url>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
											<span>
												<c:set var="clear">
	                                          		<bbbl:label key="lbl_search_filter_box_clearall" language="${pageContext.request.locale.language}" />
                                          		</c:set>
												<a rel="nofollow" href="${urlSe}" class="lnkSearchReset" title="${clear}">${clear}</a></span>
										</h3>
										
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="browseSearchVO.descriptors" />
												<dsp:oparam name="outputStart">
													<dsp:getvalueof var="size" param="size" />
													<dsp:getvalueof var="type" param="element.rootName" />
													<ul class="filter-applied-list">
												</dsp:oparam>
												<%-- R2.2 SEO Friendly URL changes --%>
												<dsp:param name="elementName" value="selectedFacetRefItem" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
													<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery"/>
													<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter"/>
													<c:if test="${type ne 'DEPARTMENT'}">
														<li class="clearfix">
															<a rel="nofollow" href="${url}${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}" class="lnkSearchRemove" title="Remove"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /><span></span></a>
														</li>
													</c:if>
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
													</ul>
												</dsp:oparam>
											</dsp:droplet>
										
								</form>
							</div>
						</c:if>				
				</c:when>				
				</c:choose>
			</dsp:oparam>
		</c:if>
		<dsp:oparam name="output">
		<dsp:getvalueof var="fieldsetClass" value="facetGroup firstGroup" />
		<dsp:getvalueof var="facetName" param="element.name" />
		<c:if test="${!(facetName == 'PRICE RANGE' && defaultUserCountryCode eq 'MX') && !(facetName == 'PRICE RANGE MX' && defaultUserCountryCode ne 'MX')}">
			<div class="row filter-menu">
		
							<c:if test="${emptyFacets}">
								<dsp:getvalueof var="facetName" param="element.rootName" />
							</c:if>
							<c:if test="${facetName == brandFacetDisplayName }">
								<c:set var="brandCount">${brandCount+1}</c:set>
							</c:if>
							<c:if test="${facetName == 'COLLEGE'}">
								<c:set var="schoolCount">${schoolCount+1}</c:set>
							</c:if>
							<c:if test="${facetName == 'ATTRIBUTES'}">
								<c:set var="attributeCount">${attributeCount+1}</c:set>
							</c:if>
							<c:if test="${facetName == colorGroupFacetDisplayName }">
								<c:set var="colorCount">${colorCount+1}</c:set>
							</c:if>
							<c:if test="${facetName == 'PRICE RANGE'}">
								<c:set var="priceCount">${priceCount+1}</c:set>
							</c:if>
							<c:if test="${facetName == typesOfPersonalization}">
								<c:set var="persCount">${persCount+1}</c:set>
							</c:if>
							<c:if test="${facetName ne 'RECORD TYPE' }">
								<c:set var="firstgroup" value="" />
								<c:set var="facetListStyle" value="facetList" />
								
								<c:choose>
									<c:when test="${facetName eq department}">
										<c:choose>
											<c:when test="${showDepartment eq 'true' && !emptyFacets}">
												<c:set var="firstgroup" value="firstGroup" />
												<dsp:getvalueof var="fieldsetClass" value="facetGroup ${firstgroup}" />
												<c:set var="facetName">${department}</c:set>
												<c:set var="facetListStyle" value="facetDepartmentList" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="fieldsetClass" value="hidden" />
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:when test="${facetName == 'RATINGS'}">
										<c:set var="facetListStyle" value="facetDepartmentList" />
									</c:when>
								</c:choose>
								<c:choose>
									<c:when test="${facetName eq department}">
										<c:if test="${showDepartment eq 'true' && !emptyFacets}">
											
												<c:set var="fieldset" value="true" />
												<h3>${facetName} <span></span></h3>
										</c:if>
									</c:when>
									<c:when test="${facetName eq colorGroupFacetDisplayName }">
										<c:if test="${colorCount eq 1}">
												<c:set var="fieldset" value="true" />
												<h3>${colorGroupFacetDisplayName} <span></span></h3>
										</c:if>
									</c:when>
									<c:when test="${facetName eq 'ATTRIBUTES'}">
										<c:if test="${attributeCount eq 1}">
												<c:set var="fieldset" value="true" />
												<h3>${facetName} <span></span></h3>
										</c:if>
									</c:when>
									<c:when test="${facetName eq typesOfPersonalization}">
										<c:if test="${persCount eq 1}">										
												<c:set var="fieldset" value="true" />
												<h3>${facetName} <span></span></h3>												
										</c:if>
									</c:when>
									<c:when test="${facetName eq brandFacetDisplayName }">
										<c:if test="${brandCount eq 1}">
												<c:set var="fieldset" value="true" />
												<h3>${facetName} <span></span></h3>
										</c:if>
									</c:when>
									<c:when test="${facetName eq 'PRICE RANGE' }">
										<c:if test="${priceCount eq 1}">
												<c:set var="fieldset" value="true" />
												<h3>${priceLabel} <span></span></h3>
										</c:if>
									</c:when>
									
									<c:when test="${facetName eq 'COLLEGE'}">
				
										<c:if test="${!emptyFacets}">
												<c:set var="fieldset" value="true" />
												<c:set var="college_text">
													<bbbl:label key="lbl_search_facet_college_title_text" language="${pageContext.request.locale.language}" />
												</c:set>
												<h3>${college_text} <span></span></h3>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${!emptyFacets}">
												<c:set var="fieldset" value="true" />
												<h3>${facetName} <span></span></h3>
										</c:if>
									</c:otherwise>
								</c:choose>
								<c:if test="${fieldset}">
									<ul class="facetContent filter-list">
								</c:if>
								
								<%-- Show facets refinements--%>
								<c:if test="${!emptyFacets}">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="element.facetRefinement" />
										<dsp:oparam name="output">
											<c:choose>
												<c:when test="${facetName == department}">
													<dsp:getvalueof param="showDepartment" var="showDepartment" />
													<c:if test="${showDepartment eq 'true'}">
													<li class="facetListItem ">
														<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
														<dsp:getvalueof var="facetItemQuery" param="element.query"/>
														<dsp:getvalueof var="refinedName_2" param="element.refinedName" />
														<c:set var="refinedName_2">
															<c:out value='${fn:replace(refinedName_2,"\'","")}'/></c:set>
																<c:set var="refinedName_2"><c:out value="${fn:replace(refinedName_2,'\"', '')}"/></c:set>													  
																<%--R2.2 Story 116C Changes--%>
																<%-- R2.2 SEO Friendly URL changes --%>
																<a class="redirPage" rel="nofollow" onclick="omnitureRefineCall('${facetName}', '${refinedName_2}');" href="${url}${facetRefFilter}/${pageSize}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}" title="<dsp:valueof param="element.name" valueishtml="true"/>"><dsp:valueof param="element.name" valueishtml="true"/> ${bracOpen}<dsp:valueof param="element.size" />${bracClose}</a>
															</li>
														</c:if>
												</c:when>
												<c:otherwise>
													<li class="facetListItem ">
													  <%--R2.2 Story 116C Changes--%>
														<c:set var="escUrl"><c:out value="${url}"/></c:set>
														<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
														<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
														<dsp:getvalueof var="facetItemQuery" param="element.query"/>
														<c:set var="refinedName_3"><c:out value='${fn:replace(refinedName_3,"\'","")}'/></c:set>
														<c:set var="refinedName_3"><c:out value="${fn:replace(refinedName_3,'\"', '')}"/></c:set>
														<%-- R2.2 SEO Friendly URL changes --%>
														<a class="redirPage" rel="nofollow"  onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${plpViewParam}" title="<dsp:valueof param="element.name" valueishtml="true"/>"> 
														<dsp:valueof param="element.name" valueishtml="true"/> ${bracOpen}<dsp:valueof param="element.size" />${bracClose}
														</a></li>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
								<c:if test="${fieldset }">
									</ul>
									<c:set var="fieldset" value="false"></c:set>
								</c:if>
							</c:if>
			</div>
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
</dsp:page>
