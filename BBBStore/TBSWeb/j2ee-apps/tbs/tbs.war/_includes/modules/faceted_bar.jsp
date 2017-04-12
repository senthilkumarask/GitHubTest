<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
	<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
	<dsp:getvalueof var="emptyFacets" value="false"/>
	<dsp:getvalueof var="emptyBox" value="true"/>
	<dsp:getvalueof var="frmSubCatBrandPage" param="frmSubCatBrandPage"/>
	
	<%--R2.2 Story 116A Changes--%>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO" />
	<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
	<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
	<c:set var="pageSize" value="1-${size}"/>
	
	<dsp:droplet name="ForEach">
       	<dsp:param name="array" param="browseSearchVO.descriptors" />
       	<dsp:oparam name="outputStart">
       		<dsp:getvalueof var="totSize" param="size"/>
		</dsp:oparam>
  	</dsp:droplet>
  	
  	<c:set var="typesOfPersonalization">
  		<bbbc:config key="Eligible_Customizations" configName="DimDisplayConfig"></bbbc:config>
  	</c:set>	

  	<c:set var="brandFacetDisplayName">
  	    <bbbc:config key="Brand" configName="DimDisplayConfig" />
    </c:set>
    <c:set var="colorGroupFacetDisplayName">
        <bbbc:config key="ColorGroup" configName="DimDisplayConfig" />
    </c:set>
	
	<dsp:getvalueof var="view" param="view"/>
	<c:set var="deptAvail" value="${false}"/>

	<c:set var="url" value="${url}/"></c:set>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:oparam name="output">
			<dsp:getvalueof  var="size" param = "size"/>
			<dsp:getvalueof var="facetName" param="element.name"/> 
			<c:if test="${(size eq 1) && (facetName eq 'DEPARTMENT')}">
				<dsp:getvalueof var="plpOnlyDeptLeft" value="true"/>
			</c:if>
			<c:if test="${facetName eq 'DEPARTMENT'}">
				<dsp:getvalueof var="deptAvail" value="${true}"/>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>		
	
	<%--R2.2 Defect BED-208 fixed : Start --%>
	<c:if test="${not empty frmSubCatBrandPage && frmSubCatBrandPage}">
		<c:set var="searchQueryParams" value="&view=${view}"/>
	</c:if>
	<c:set var="urlT" value="${url}"/>
	<c:if test="${not empty view}">
		<c:set var="urlT" value="${urlT}?view=${view}"/>
	</c:if>
	<%--R2.2 Defect BED-208 fixed : End --%>
	
	<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="priceLabel"><bbbl:label key="lbl_facet_price_range" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="brandCount" value = "0"/>
	<c:set var="schoolCount" value = "0"/>
	<c:set var="attributeCount" value = "0"/>
	<c:set var="colorCount" value = "0"/>
	<c:set var="customizationOptionCount" value = "0"/>
	<c:set var="priceCount" value = "0"/>
	<c:if test="${empty facets || plpOnlyDeptLeft eq true}">
		<dsp:getvalueof var="facets" param="browseSearchVO.descriptors"/>
		<dsp:getvalueof var="emptyFacets" value="true"/>
		<dsp:getvalueof var="emptyBox" value="false"/>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${facets}" />
			<dsp:oparam name="output">
				<dsp:getvalueof  var="size" param = "size"/>
				<dsp:getvalueof var="facetName" param="element.rootName"/> 
				<c:if test="${(size gt 1) && (facetName eq brandFacetDisplayName || facetName eq 'ATTRIBUTES' || facetName eq colorGroupFacetDisplayName || facetName eq 'PRICE RANGE')}">
					<dsp:getvalueof var="emptyBox" value="true"/>
				</c:if>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	
	
<c:set var="checkStatus" value="${false}"/>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:getvalueof var="size" param="size" />
		<dsp:oparam name="output">
			
			<dsp:getvalueof var="fieldsetClass" value="facetGroup firstGroup" />
			<dsp:getvalueof var="facetName" param="element.name" />
			<%--R2.2 Story 116A Changes--%>

			<dsp:getvalueof var="count" param="count" />
			<c:if test="${(!deptAvail) && (count eq 1)}">
				<c:set var="showNarrow" value="${true}" />
			</c:if>
			
			<c:if test="${not empty facetName}">
				<div class="row">
			</c:if>
				
			<c:set var="showNarrow" value="${false}" />
			<c:set var="deptAvail" value="${false}" />
			<%--R2.2 Story 116A Changes End--%>
			<c:if test="${emptyFacets}">
				<dsp:getvalueof var="facetName" param="element.rootName" />
			</c:if>
			<c:if test="${facetName == brandFacetDisplayName}">
				<c:set var="brandCount">${brandCount+1}</c:set>
			</c:if>
			<c:if test="${facetName == 'COLLEGE'}">
				<c:set var="schoolCount">${schoolCount+1}</c:set>
			</c:if>
			<c:if test="${facetName == 'ATTRIBUTES'}">
				<c:set var="attributeCount">${attributeCount+1}</c:set>
			</c:if>
			<c:if test="${facetName == colorGroupFacetDisplayName}">
				<c:set var="colorCount">${colorCount+1}</c:set>
			</c:if>
			<c:if test="${facetName == typesOfPersonalization}">
				<c:set var="customizationOptionCount">${customizationOptionCount+1}</c:set>
			</c:if>
			<c:if test="${facetName == 'PRICE RANGE'}">
				<c:set var="priceCount">${priceCount+1}</c:set>
			</c:if>
			
			<c:if test="${(not empty facetName) and !checkStatus}">
				<c:set var="checkStatus" value="${true}" />
				<c:if test="${(totSize gt 1)}">
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
											<a rel="nofollow" href="${urlT}?${partialFlagUrl}" class="dynFormSubmit lnkSearchReset" data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}"
												title="${clear}"> ${clear} </a></span>
									</h3>
									
								
								<div class="searchContent noBorder">
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
											<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
											<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter" />
											<c:if test="${type ne 'RECORD TYPE'}">
												<li class="clearfix"><a rel="nofollow" href="${url}${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}" class="lnkSearchRemove" title="Remove"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /><span></span></a></li>
											</c:if>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</ul>
										</dsp:oparam>
									</dsp:droplet>
								</div>
						</form>
					</div>
				</c:if>
				<c:if test="${totSize eq 1}">
					<%-- R2.2 SEO Friendly URL changes --%>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:param name="elementName" value="selectedFacetRefItem" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="size" param="size" />
							<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
							<c:if test="${type ne 'RECORD TYPE'}">
								
									<div class="row filter-applied">
										<form id="frmFacetedSearch" method="post">
										
											<div class="searchTitle">
												<h3>
													<bbbl:label key="lbl_search_facet_bar" language="${pageContext.request.locale.language}" />
													<span>
														<c:set var="clear">
															<bbbl:label key="lbl_search_filter_box_clearall" language="${pageContext.request.locale.language}" />
														</c:set> <a rel="nofollow" href="${urlT}" class="dynFormSubmit lnkSearchReset" data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}"
														title="${clear}"> ${clear} </a>
													</span>
												</h3>
											</div>

											<ul class="filter-applied-list">
												<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
												<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter" />
												<li class="clearfix">
												<a rel="nofollow" href="${url}${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}" class="lnkSearchRemove" title="Remove"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /><span></span></a>
												
												</li>
												<dsp:getvalueof var="currSize" param="size" />
												<dsp:getvalueof var="currCount" param="count" />
												<c:if test="${currSize eq currCount}">
													<li></li>
												</c:if>
											</ul>
										</form>
									</div>
								
							</c:if>
							</dsp:oparam>
						</dsp:droplet>	
				</c:if>
			</c:if>
			<c:if test="${facetName ne 'RECORD TYPE' }">
				<c:set var="firstgroup" value="" />
				<c:set var="facetListStyle" value="facetList" />
				<c:choose>
					<c:when test="${facetName eq department}">
						<c:choose>
							<c:when test="${!emptyFacets}">
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
						<c:if test="${!emptyFacets}">
								<c:set var="fieldset" value="true" />
								<h3><bbbl:label key="lbl_facetname_department" language="${pageContext.request.locale.language}" /><span></span></h3>
						</c:if>
					</c:when>
					<c:when test="${facetName eq colorGroupFacetDisplayName }">
						<c:if test="${colorCount eq 1}">
								<c:set var="fieldset" value="true" />
										<h3>${colorGroupFacetDisplayName }<span></span></h3>
						</c:if>
					</c:when>
					<c:when test="${facetName eq typesOfPersonalization}">
						<c:if test="${customizationOptionCount eq 1}">							
								<c:set var="fieldset" value="true" />
								<h3>${facetName}<span></span> </h3>
								
						</c:if>
					</c:when>
					
					<c:when test="${facetName eq 'ATTRIBUTES'}">
						<c:if test="${attributeCount eq 1}">
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
					
						<c:choose>
							<c:when test="${facetName eq department}">
								<ul class="category-list">
							</c:when>
							
							<c:otherwise>
								<ul class="facetContent filter-list">
							</c:otherwise>
						</c:choose>
				</c:if>
				
				<%-- Show facets refinements--%>
				<c:if test="${!emptyFacets}">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="element.facetRefinement" />
						<dsp:oparam name="output">
							<li class="facetListItem ">
												<c:set var="escUrl"><c:out value="${url}"/></c:set>
												<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
												<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
												<dsp:getvalueof var="facetItemQuery" param="element.query"/>
												<c:set var="refinedName_3"><c:out value='${fn:replace(refinedName_3,"\'","")}' />
											</c:set> <c:set var="refinedName_3">
												<c:out value="${fn:replace(refinedName_3,'\"', '')}" />
											</c:set> <%-- R2.2 SEO Friendly URL changes --%> <a class="dynFormSubmit" data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" 
													rel="nofollow" onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}" title="<dsp:valueof param="element.name" valueishtml="true"/>"><dsp:valueof param="element.name" valueishtml="true"/> ${bracOpen}<dsp:valueof param="element.size" />${bracClose}</a>
										</li>
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
				<c:if test="${fieldset }">
					</ul>
					<c:set var="fieldset" value="false"></c:set>
				</c:if>
			</c:if>
		</div>
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