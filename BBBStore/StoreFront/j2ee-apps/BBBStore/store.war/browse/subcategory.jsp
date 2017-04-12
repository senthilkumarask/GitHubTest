<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="nextHref" param="nextHref"/>
	<dsp:getvalueof var="prevHref" param="prevHref"/>
	<dsp:getvalueof var="catID" param="catID"/>
	<dsp:getvalueof var="fromCollege" param="fromCollege"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="prop9Var" param="brandNameForUrl"/>
	<dsp:getvalueof var="brandName" param="brandName" />
	<dsp:getvalueof var="brandId" param="brandId"/>
	<dsp:getvalueof var="seoUrl" param="seoUrl" scope="request" />
	<dsp:getvalueof var="swsTermsList" param="swsTermsList" />
	<dsp:getvalueof var="currentCat" param="browseSearchVO.currentCatName"/>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	<dsp:getvalueof var="fromBrandPage" param="fromBrandPage"/>
	<dsp:getvalueof var="fromCatPage" param="fromCatPage"/>
	
	
	<c:set var="radius_default_us">
		<bbbc:config key="radius_default_us" configName="MapQuestStoreType" />
    </c:set>
    <c:set var="radius_default_baby">
		<bbbc:config key="radius_default_baby" configName="MapQuestStoreType" />
	</c:set>
	<c:set var="radius_default_ca">
		<bbbc:config key="radius_default_ca" configName="MapQuestStoreType" />
	</c:set>
	<c:set var="radius_range_us">
		<bbbc:config key="radius_range_us" configName="MapQuestStoreType" />
	</c:set>
	<c:set var="radius_range_baby">
		<bbbc:config key="radius_range_baby" configName="MapQuestStoreType" />
	</c:set>
	<c:set var="radius_range_ca">
		<bbbc:config key="radius_range_ca" configName="MapQuestStoreType" />
	</c:set>
	<c:set var="radius_range_type">
		<bbbc:config key="radius_range_type" configName="MapQuestStoreType" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq 'BedBathUS'}">
			<c:set var="radius_default_selected">${radius_default_us}</c:set>
			<c:set var="radius_range">${radius_range_us}</c:set>
		</c:when>
		<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
			<c:set var="radius_default_selected">${radius_default_baby}</c:set>
			<c:set var="radius_range">${radius_range_baby}</c:set>
		</c:when>
		<c:when test="${currentSiteId eq 'BedBathCanada'}">
			<c:set var="radius_default_selected">${radius_default_ca}</c:set>
			<c:set var="radius_range">${radius_range_ca}</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="radius_default_selected">${radius_default_us}</c:set>
			<c:set var="radius_range">${radius_range_us}</c:set>
		</c:otherwise>
	</c:choose>
	
	<%--<input type="hidden" name="fromPage" value="categoryPage"/>--%>
	<dsp:getvalueof var="catId" param="categoryId"/>
		
	<!--BBBI-3804: omniture Tagging :start -->
	<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
	<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="defaultOmnitureVar"><bbbc:config key="DefaultEndecaOmnitureVariable" configName="OmnitureBoosting"/></c:set>
	<c:choose>
		<c:when test="${frmBrandPage && brandsBoostFlag}">
			<dsp:droplet name="OmnitureVariableDroplet">
				<dsp:param name="pageName" value="BRAND" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="OmnitureVariable" param="OmnitureVariable" />
				</dsp:oparam>
			</dsp:droplet>
		</c:when>
		<c:when test="${not empty catId && l2l3BoostFlag }">
			<dsp:droplet name="OmnitureVariableDroplet">
				<dsp:param name="pageName" value="L2L3" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="OmnitureVariable" param="OmnitureVariable" />
				</dsp:oparam>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<c:set var="OmnitureVariable" value="${defaultOmnitureVar}" />
		</c:otherwise>
	</c:choose>

	<!--BBBI-3804: omniture Tagging :end -->
	
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
		<c:set var="lblWhatsNew">
		<bbbl:label key="lbl_whats_new" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblClearance">
		<bbbl:label key="lbl_clearance" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
	<c:set var="plpGridSize">
		<bbbc:config key="GridSize" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="fromCatL2AndL3" scope="request" value="false"></c:set>
	 <c:set var="pageGridClass" value=""/>
	<c:set var="gridClass" value="grid_10"/>
	<c:set var="facetClass" value="grid_2"/>
	
	<c:if test="${plpGridSize == '3'}">
		<c:if test="${defaultView == 'grid'}">
			<c:set var="pageGridClass" value="plp_view_3"/>
		</c:if>
		<c:set var="gridClass" value="grid_9"/>
		<c:set var="facetClass" value="grid_3"/>
	</c:if>

	<c:if test="${fromCollege eq 'true' }">
		<c:set var="variation" value="bc" scope="request" />
    </c:if>

			<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet" />
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:importbean bean="/atg/userprofiling/Profile" />
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
			<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>

			<dsp:getvalueof var="categoryId" param="categoryId"/>


			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		    <dsp:getvalueof id="applicationId" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>

			<%-- Added as part of R2.2 SEO friendly URL Story : Start --%>
			<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage" scope="request" />
			<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
			<dsp:getvalueof var="firstPageURL" param="browseSearchVO.pagingLinks.firstPage" scope="request" />
			<dsp:getvalueof var="filterApplied" param="browseSearchVO.pagingLinks.pageFilter" scope="request" />
			<dsp:getvalueof var="canonicalFilterApplied" param="browseSearchVO.pagingLinks.canonicalPageFilter" scope="request" />
			<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" scope="request" />
			<dsp:getvalueof var="nextPageParams" param="browseSearchVO.pagingLinks.nextPage" scope="request" />
			<dsp:getvalueof var="prevPageParams" param="browseSearchVO.pagingLinks.previousPage" scope="request" />

			<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
			<dsp:getvalueof param="CatalogId" var="CatalogId" />
			<c:set var="subCategoryPageParam" value="" scope="request" />

			<%-- R2.2 - Story 116E Start --%>
			<c:if test="${empty frmBrandPage}">
				<c:if test="${not empty subCatPlp && subCatPlp eq true}">
					<c:set var="subCategoryPageParam">&subCatPlp=true&a=1</c:set>
				</c:if>
				<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
					<dsp:param name="id" value="${CatalogRefId}" />
					<dsp:param name="itemDescriptorName" value="category" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
					
						<dsp:getvalueof var="seoUrl" vartype="java.lang.String" param="url" scope="request" />
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<c:set var="isFilterApplied" value="false" scope="request" />
			<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
			<c:set var="descriptorLength" value="${fn:length(descriptorsList)}" />
			<c:choose>
				<c:when test="${not empty frmBrandPage and frmBrandPage eq true}">
					 <c:if test="${(descriptorLength ge 1) && (descriptorsList[descriptorLength-1].rootName ne 'RECORD TYPE')}">
						<c:set var="isFilterApplied" value="true" scope="request" />
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${(descriptorLength ge 1) && (descriptorsList[descriptorLength-1].rootName ne 'DEPARTMENT')}">
						<c:set var="isFilterApplied" value="true" scope="request" />
					</c:if>
					<%-- Below logic is added to handle canonical URL for SWS keywords --%>
					<c:if
						test="${(descriptorLength eq '2') && (not empty param.narrowDown)}">
						<c:set var="isSWSFilterApplied" value="true" scope="request" />
					</c:if>
				</c:otherwise>
			</c:choose>
		
		<%-- Added as part of BBBSL-6086 : Start --%>		
		<c:set var="subCategoryParamForLinkRel" scope="request" >${subCategoryPageParam}</c:set>
		<c:set var="isRefType" value="false"></c:set>
		<c:choose>
			<c:when test="${not frmBrandPage && (not empty param.narrowDown || descriptorLength gt 1)}">
				<c:set var="isRefType" value="true"></c:set>
				<c:set var="subCategoryParamForLinkRel" scope="request" value="${subCategoryParamForLinkRel}&refType=false&view=${defaultView}"/>
			</c:when>
			<c:otherwise>
				<c:set var="subCategoryParamForLinkRel" scope="request" value="${subCategoryParamForLinkRel}&view=${defaultView}"/>
			</c:otherwise>
		</c:choose>
		<%-- Added as part of BBBSL-6086 : End --%>
		
		<%-- Added as part of R2.2 SEO friendly URL Story : End --%>
<%-- R2.2 - Story 116E END --%>
		
		<%-- PS-24710 Start --%>
		
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<c:set var="count" value="0"/>
			<c:set var="brandstring" value=""/>
			<c:set var="colorstring" value=""/>
			<c:set var="departmentstring" value=""/>
			<c:set var="lastColorstring" value=""/>
			<c:set var="lastBrandstring" value=""/>
			<c:forEach var="descriptor" items="${browseSearchVO.descriptors}">
				<c:if test="${(frmBrandPage and descriptor.rootName ne 'RECORD TYPE') or ((empty frmBrandPage or not frmBrandPage) and descriptor.rootName ne 'DEPARTMENT' and descriptor.rootName ne 'RECORD TYPE' )}"><!--  and count lt 4)}">-->
					<c:choose>
						 <c:when test="${fn:containsIgnoreCase(descriptor.rootName,'COLOR')}">
						 	<c:choose>
						 		<c:when test="${empty colorstring || colorstring == ''}">
						 			<c:set var="colorstring" value="${descriptor.name}"/>
						 		</c:when>
						 		<c:otherwise>
						 			<c:set var="colorstring" value="${colorstring}, ${descriptor.name}"/>
						 		</c:otherwise>
						 	</c:choose>	
						 	<c:set var="lastColorstring" value="${descriptor.name}"/>
						</c:when>
						<c:when test="${fn:containsIgnoreCase(descriptor.rootName,'BRAND')}">
							<c:choose>
						 		<c:when test="${empty brandstring || brandstring == ''}">
						 			<c:set var="brandstring" value="${descriptor.name}"/>
						 		</c:when>
						 		<c:otherwise>
						 			<c:set var="brandstring" value="${brandstring}, ${descriptor.name}"/>
						 		</c:otherwise>
						 	</c:choose>	
						 	<c:set var="lastBrandstring" value="${descriptor.name}"/>
						</c:when> 
						<c:when test="${fn:containsIgnoreCase(descriptor.rootName,'DEPARTMENT')}">
							<c:set var="departmentstring" value="${departmentstring} ${descriptor.name }"/>
						</c:when>
						<c:when test="${not empty titleFilterStringfacet && (not fn:containsIgnoreCase(descriptor.rootName,'Price'))}">
							<c:set var="titleFilterStringfacet" value="${titleFilterStringfacet} - ${descriptor.name}"/>
						</c:when>
						<c:otherwise>
							<c:if test="${not fn:containsIgnoreCase(descriptor.rootName,'Price')}">
								<c:set var="titleFilterStringfacet" value="${descriptor.name}"/>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			
			<!-- Repalce last , with & for colorString-->
			<%-- <c:set var="relaceColorstring" value=", ${lastColorstring}"/>
			<c:set var="changeBrandstring" value=" & ${lastColorstring}"/>
			<c:set var="colorstring" value="${fn:replace(colorstring,relaceColorstring,changeBrandstring)}"/> --%>
			<!-- End colorString -->
			
		
			<%  String colorstring = (String)pageContext.getAttribute("colorstring");
				int ind = colorstring.lastIndexOf(",");
				if( ind>=0 ){
					String colorstringnew = (new StringBuilder(colorstring).replace(ind,ind+1," &" )).toString();
					pageContext.setAttribute("colorstring", colorstringnew);
				}%> 
			<%--  <c:set var="relaceColorstring" value=", ${lastBrandstring}"/>
			<c:set var="changeBrandstring" value=" & ${lastBrandstring}"/>
			<c:set var="brandstring" value="${fn:replace(brandstring,relaceColorstring,changeBrandstring)}"/>  --%>
				<!-- Repalce last , with & for brandString-->
			<%  String brandstring = (String)pageContext.getAttribute("brandstring");
				 ind = brandstring.lastIndexOf(",");
				if( ind>=0 ){
					String brandstringnew = (new StringBuilder(brandstring).replace(ind,ind+1," &" )).toString();
					pageContext.setAttribute("brandstring", brandstringnew);
				}%> 
			<!-- End brandString -->
			
			<!-- Check for stop words to be excluded from department keyword -->
			<c:if test="${not empty departmentstring}">
				<c:set var="stopWords"><bbbc:config key="stopcategoryintitle" configName="ContentCatalogKeys" /></c:set>
				<c:forTokens items="${stopWords}" delims="," var="mySplit">
	  				 <c:if test="${departmentstring eq mySplit || browseSearchVO.currentCatName eq mySplit}">
	  				 	<c:set var="departmentstring" value="${fn:replace(departmentstring,mySplit,'')}"/>
	  				 	<c:set var="categoryNameStopWord" value="true"/>
	  				 </c:if>
				</c:forTokens>
			</c:if>
			<!-- Add brand name if brand page and no brand filter -->
			<c:if test="${frmBrandPage && empty  brandstring}">
				<c:set var="brandstringnull" value="true"/>
				<c:set var="brandstring" value="${brandName}"/>
			</c:if>
			<!-- Append color brand and department name to the title -->
			<c:set var="titleFilterString" value="${colorstring}"/>
			<c:choose>
				<c:when test="${not empty titleFilterString && not empty brandstring}">
					<c:set var="titleFilterString" value="${titleFilterString} ${brandstring}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty brandstring}">
					<c:set var="titleFilterString" value="${brandstring}"/>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${not empty titleFilterString && not empty departmentstring}">
					<c:set var="titleFilterString" value="${titleFilterString} ${departmentstring}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty departmentstring}">
					<c:set var="titleFilterString" value="${departmentstring}"/>
				</c:when>
			</c:choose>
			<!-- Append category name for category pages -->
			<c:choose>
				<c:when test ="${(empty frmBrandPage or not frmBrandPage) && (not empty titleFilterString || not empty titleFilterStringfacet)}">
					<%-- <c:set var="titleFilterString" value="${titleFilterString} ${browseSearchVO.currentCatName}"/> --%>
					<c:if test="${empty categoryNameStopWord || categoryNameStopWord ne 'true' }">
						<c:set var="titleFilterString" value="${titleFilterString} ${browseSearchVO.currentCatName}"/>
					</c:if>
					<c:if test="${not empty titleFilterStringfacet }">
						<c:set var="titleFilterString" value="${titleFilterString} - ${titleFilterStringfacet}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:set var="titleFilterString" value="${titleFilterString}"/>
					<c:if test="${not empty titleFilterStringfacet }">
						<c:set var="titleFilterString" value="${titleFilterString} - ${titleFilterStringfacet}"/>
					</c:if>
				</c:otherwise>
			</c:choose>	
		
			<%-- <c:forEach var="narrowDownFilter" items="${swsTermsList}">
				<c:if test="${count lt 4}">
					<c:choose>
						<c:when test="${not empty titleSWSFilterString}">
							<c:set var="titleSWSFilterString"
								value="${titleSWSFilterString}, ${narrowDownFilter.name}" />
						</c:when>
						<c:otherwise>
							<c:set var="titleSWSFilterString" value="${narrowDownFilter.name}" />
						</c:otherwise>
					</c:choose>
					<c:set var="count" value="${count + 1 }" />
				</c:if>
			</c:forEach> --%>
	<%-- <c:choose>
		<c:when test="${not empty titleSWSFilterString && empty titleFilterString}">
			<c:set var="titleFilterString" value="${browseSearchVO.currentCatName} - ${titleSWSFilterString}"/>
		</c:when>
		<c:when test="${not empty titleSWSFilterString && not  empty titleFilterString}">
			<c:set var="titleFilterString" value="${titleFilterString} - ${titleSWSFilterString}"/>
		</c:when>
	</c:choose> --%>
	<%-- <dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
			<c:choose>
				<c:when test="${ (pagSortOpt == 'Price-0')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Price-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Ratings-1')}">
				<c:if test="${currentSiteId ne BedBathCanadaSite}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
				</c:if>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Brand-0')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Date-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${ (pagSortOpt == 'Sales-1')}">
					<c:set var="selectedSortOptionValue"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
			</c:choose> --%>
			
			<%-- <c:choose>
				<c:when test="${not empty titleFilterString && not empty selectedSortOptionValue}">
					<c:set var="titleFilterString" value="${titleFilterString} - ${selectedSortOptionValue}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty selectedSortOptionValue}">
					<c:set var="titleFilterString" value="${selectedSortOptionValue}"/>
				</c:when>
			</c:choose> --%>
			<c:if test="${not empty frmBrandPage and frmBrandPage eq true}">
				<c:choose>
					<c:when test="${not empty titleFilterString && (titleFilterString eq brandName &&  brandstringnull eq 'true')}">
						<c:set var="titleFilterString" value="${brandName} - "/>
						<c:set var="titleFilterString">${titleFilterString}<bbbl:label key="lbl_metadetails_title" language ="${pageContext.request.locale.language}"/></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="titleFilterString" value="${titleFilterString } | "/>
						<c:set var="titleFilterString">${titleFilterString}<bbbl:label key="lbl_metaDetail_FacetTitle" language ="${pageContext.request.locale.language}"/></c:set>
					</c:otherwise>
				</c:choose>
			</c:if>
		<%-- PS-24710 End --%>
		<bbb:pageContainer>
		
			<jsp:attribute name="section">browse</jsp:attribute>
			<jsp:attribute name="pageWrapper">subCategory useCertonaJs useStoreLocator ${pageGridClass} useScene7</jsp:attribute>
			<jsp:attribute name="pageVariation">${variation}</jsp:attribute>
			<jsp:attribute name="PageType">SubCategoryDetails</jsp:attribute>
			<jsp:attribute name="titleString">${titleFilterString }</jsp:attribute>

			
	       <jsp:body>
		   
			<c:if test="${fromBrandPage == 'true'}">
				<input type="hidden" name="fromPage" value="brandsPage"/>
				<input type="hidden" name="inStoreValue" value="${inStore}"/>
				<input type="hidden" name="queryStringFromBrandPage" value="${queryString}"/>
			</c:if>
			
			<c:if test="${fromCatPage == 'true'}">
				<input type="hidden" name="fromPage" value="categoryPage"/>
				<input type="hidden" name="inStoreValue" value="${inStore}"/>
				<input type="hidden" name="queryStringFromCategoryPage" value="${queryString}"/>
			</c:if>
			
			<input type="hidden" value="${radius_default_selected}" id="defaultRadius" name="defaultRadius">
		   
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>

			<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="subCatHeaderShown" scope="request" value="false"></c:set>
			<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
			<dsp:getvalueof param="browseSearchVO" var="browseSearchVO" />
			<dsp:getvalueof var="linkString" param="linkString" />
			<script type="text/javascript">
				linksCertona =linksCertona + '${linkString}';
			</script>
			<c:if test="${not frmBrandPage}">
				<dsp:droplet name="BreadcrumbDroplet">
					<dsp:param name="categoryId" param="categoryId" />
						<dsp:param name="siteId" value="${applicationId}"/>
						<dsp:oparam name="output">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
									<c:set var="categoryPath" scope="request"></c:set>
								</dsp:oparam>
								<dsp:oparam name="output">
									<%-- Start: Added for Scope # 81 H1 tags --%>
									<dsp:getvalueof var="count" param="count" />
										<c:if test="${count eq 2 }">
											<c:set var="CatFlagL2" scope="request" value="true"></c:set>
											<c:set var="CatNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:if test="${count eq 3 }">
											<c:set var="CatFlagL3" scope="request" value="true"></c:set>
											<c:set var="CatNameL3" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<%-- End: Added for Scope # 81 H1 tags --%>
									<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
				</dsp:droplet>
				<c:if test="${TagManOn}">
					<c:choose>
						<c:when test="${frmBrandPage}">
							<dsp:include page="/tagman/frag/search_frag.jsp" >
								<dsp:param name="searchTerm" value="${Keyword}"/>
							</dsp:include>
						</c:when>
						<c:otherwise>
							<dsp:include page="/tagman/frag/subcategory_frag.jsp">
			       				<dsp:param name="categoryPath" value="${categoryPath}"/>
			      			</dsp:include>
						</c:otherwise>
					</c:choose>
				</c:if>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="browseSearchVO.categoryHeader.categoryTree" />
	             	<dsp:oparam name="output">
	             		<dsp:getvalueof var="index" param="index"/>
	             		<c:if test="${index eq 0}">
	             			<dsp:getvalueof var="var1" param="element"/>
	             		</c:if>
	             		<c:if test="${index eq 1}">
	             			<dsp:getvalueof var="var2" param="element"/>
	             		</c:if>
	             		<c:if test="${index eq 2}">
	             			<dsp:getvalueof var="var3" param="element"/>
	             		</c:if>
	             	</dsp:oparam>
				</dsp:droplet>

			</c:if>

					<div id="subHeader" class="container_12 clearfix">
						<div class="grid_12 marTop_10">
							<dsp:getvalueof var="catBannerContent" param="categoryVO.bannerContent" />
							<%-- JIRA Defect # BBBSL-2 START --%>
							<c:if test="${not frmBrandPage}">
								<dsp:getvalueof var="subCatHeader"  param="browseSearchVO.categoryHeader.name"/>
								<c:if test="${!(catBannerContent eq null)}">
									
									<%-- <c:set var="subCatHeaderShown" scope="request" value="true"/> --%>
									<dsp:getvalueof var="catCSSFile" param="categoryVO.cssFilePath" />
							    	<dsp:getvalueof var="catJSFile" param="categoryVO.jsFilePath" />
									<script type="text/javascript" src="${catJSFile}"></script>
									<link rel="stylesheet" type="text/css" href="${catCSSFile }" />
									<div id="bannerContent" class="marBottom_10 clearfix">
										${catBannerContent}
									</div>
								</c:if>
								<dsp:getvalueof var="floatNode"  value="false"/>
								<c:set var="floatCatName"><bbbc:config key="float_node_cat_name" configName="DimNonDisplayConfig" /></c:set>
								<%--<c:if test="${subCatHeader eq 'Floating Nodes'}">--%>
								<c:if test="${fn:toLowerCase(subCatHeader) == fn:toLowerCase(floatCatName)}">
									<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
										<dsp:param name="id" param="catID" />
										<dsp:param name="elementName" value="mainCategory" />
										<dsp:oparam name="output">
						                	<dsp:getvalueof var="categoryName" param="mainCategory.displayName" />
						                </dsp:oparam>
						           	</dsp:droplet>
									<dsp:getvalueof var="subCatHeader"  value="${categoryName}"/>
									<dsp:getvalueof var="floatNode"  value="true"/>
								</c:if>
								<%-- JIRA Defect # BBBSL-2 END  --%>
								<%-- RS # 2094542 -- To see if chat link will come on this page request or not. If yes, --%>
								<dsp:getvalueof var="categoryVO" param="categoryVO" />
			                   	<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
								<dsp:getvalueof var="chatEnabled" param="categoryVO.isChatEnabled" />
								<dsp:getvalueof var="chatLink" value=""/>
								<c:choose>
									<c:when test="${(not empty chatEnabled)  && (chatEnabled eq 'true') && (chatLinkPlaceholder eq 'Top Right')}">
										<dsp:getvalueof var="chatLink" value="true"/>
									</c:when>
									<c:when test="${(empty chatEnabled)}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param bean="/atg/commerce/catalog/CatalogNavHistory.navHistory" name="array" />
											<dsp:param name="reverseOrder" value="true" />
											<dsp:param name="elementName" value="cat" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="size" id="size"/>
												<dsp:getvalueof param="count" id="count"/>
													<c:if test="${(size eq 4) and (count eq 2)}">
														<dsp:droplet name="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet">
															<dsp:param param="cat.repositoryId" name="id" />
															<dsp:param name="siteId" value="${applicationId}"/>
															<dsp:oparam name="subcat">
																<dsp:getvalueof id="parentCatVO" param="categoryVO"/>
																<dsp:getvalueof var="parentCatChatLinkPlaceholder" value="${parentCatVO.chatLinkPlaceholder}" />
																<dsp:getvalueof var="parentChatEnabled" value="${parentCatVO.isChatEnabled}" />
																<dsp:getvalueof id="chatEnabled" value="${parentCatVO.isChatEnabled}" />
																<c:if test="${(not empty parentChatEnabled)  && (parentChatEnabled eq 'true') && (parentCatChatLinkPlaceholder eq 'Top Right')}">
																	<dsp:getvalueof var="chatLink" value="true"/>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
								</c:choose>
							</c:if>
							<%-- RS # 2094542 -- To see if chat link will come on this page request or not. If yes, --%>

							<c:if test="${frmBrandPage }">
								<h1 id="address_l2_l3_brand"><dsp:valueof value="${Keyword}" valueishtml="true"/></h1>
							</c:if>

							<dsp:droplet name="ForEach">
		                     	<dsp:param name="array" param="browseSearchVO.promoMap" />
		                        <dsp:oparam name="output">
		                        	<dsp:getvalueof var="elementList" param="element"/>
		                            <dsp:getvalueof var="key1" param="key"/>
		                            <%-- <c:choose>	    --%>
		                            <c:if test="${(empty frmBrandPage || !frmBrandPage) && (key1 eq promoKeyCenter || key1 eq promoKeyTop)}">
		                            	<c:set var="fromCatL2AndL3" scope="request" value="true"></c:set>
		                            	<dsp:droplet name="ForEach">
		                                	<dsp:param name="array" value="${elementList}" />
		                                	<%-- Start Fix For Defect : PS-20691 --%>
		                                	<dsp:oparam name="outputStart">
												<dsp:getvalueof var="imageHREF" value=""/>
							                    <dsp:getvalueof var="imageURL" value=""/>
							                    <dsp:getvalueof var="imageAlt" value=""/>
							                    <dsp:getvalueof var="seoText" value=""/>
											</dsp:oparam>
		                                    <dsp:oparam name="output">
		                                    	<c:if test="${empty imageHREF}">
				                            		<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
				                            	</c:if>
				                            	<c:if test="${not empty imageHREF}">
				                            		<dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            	</c:if>
				                            	<c:if test="${empty imageURL}">
							                    	<dsp:getvalueof var="imageURL" param="element.imageSrc"/>
							                    </c:if>
							                    <c:if test="${empty imageAlt}">
							                    	<dsp:getvalueof var="imageAlt" param="element.imageAlt"/>
							                    </c:if>
							                    <c:if test="${empty seoText}">
							                    	<dsp:getvalueof var="seoText" param="element.seoText"/>
							                    </c:if>
							                </dsp:oparam>
							                <%-- End Fix For Defect : PS-20691 --%>
							              	<dsp:oparam name="empty">
							              		<dsp:getvalueof var="imageHREF" value=""/>
							                    <dsp:getvalueof var="imageURL" value=""/>
							                    <dsp:getvalueof var="imageAlt" value=""/>
							                    <dsp:getvalueof var="seoText" value=""/>
					                        </dsp:oparam>
							           	</dsp:droplet>
							           	<%-- Start: Added for Scope # 81 H1 tags --%>							           
							           	<c:choose>
					                    	<c:when test="${(not empty seoText) && (not empty imageURL) }">
						                    	<div class="grid_5 omega marBottom_10 promoImage <c:if test="${key1 eq promoKeyTop}">promoHead</c:if>">
													<c:choose>
														<c:when test="${not empty imageHREF}">
															<a href="${imageHREF}" title="${imageAlt}"><img src="${scene7Path}/${imageURL}" alt="${imageAlt}" /></a>
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" alt="${imageAlt}" />
														</c:otherwise>
													</c:choose>
												</div>
												<div class="grid_7 alpha">
						                    	 <c:if test="${not subCatHeaderShown}">
						                    	 	<c:set var="subCatHeaderShown" scope="request" value="true"/>
						                    	 	<c:choose>
						                    			<c:when test="${!(CatFlagL3 eq null)}">
						                    				<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${currentCat}" valueishtml="true"/></h1>
						                    				<c:set var="catHeaderValue" value="true" scope="request"/>
						                    			</c:when>
						                    			<c:otherwise>
						                    				<c:choose>
						                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
								                    				<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
								                    				<c:set var="catHeaderValue" value="true" scope="request"/>
						                    					</c:when>
						                    					<c:otherwise>
						                    						<c:choose>
						                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
						                    							<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
						                    								<!-- <p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p> -->
						                    								<c:set var="catHeaderValue" value="true" scope="request"/>
						                    							</c:when>
						                    							<c:otherwise>
						                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
						                    								<c:set var="catHeaderValue" value="true" scope="request"/>
						                    							</c:otherwise>
						                    						</c:choose>
						                    					</c:otherwise>
						                    				</c:choose>
						                    			</c:otherwise>
						                    		</c:choose>
						                    	 </c:if>
													<p class="catSEOText">${seoText}</p>
												</div>
						                    </c:when>
						                   	<c:otherwise>
						                   		<%-- RM Defect 15730: ALL Concepts: Endeca Center Column Promo Bug --%>
						                   		<%-- <div class="grid_7 alpha"> --%>
						                   			<c:choose>
							                   			<c:when test="${(not empty seoText)}">
							                   				<%-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 --%>
															<c:choose>
							                   					<c:when test="${chatLink eq true}">
							                   						<div class="grid_9 alpha">
							                   					</c:when>
							                   					<c:otherwise>
							                   						<div class="grid_12 alpha">
							                   					</c:otherwise>
							                   				</c:choose>
							                   				<%-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 --%>
							                   					<c:if test="${not subCatHeaderShown}">
							                   						<c:set var="subCatHeaderShown" scope="request" value="true"/>
							                   						<c:choose>
										                    			<c:when test="${!(CatFlagL3 eq null)}">
										                    				<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${currentCat}" valueishtml="true"/></h1>
										                    				<c:set var="catHeaderValue" value="true" scope="request"/>
										                    			</c:when>
										                    			<c:otherwise>
										                    				<c:choose>
										                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
												                    				<h1 id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
												                    				<c:set var="catHeaderValue" value="true" scope="request"/>
										                    					</c:when>
										                    					<c:otherwise>
										                    						<c:choose>
										                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
										                    							<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
										                    								<!-- <p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p> -->
										                    								<c:set var="catHeaderValue" value="true" scope="request"/>
										                    							</c:when>
										                    							<c:otherwise>
										                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
										                    								<c:set var="catHeaderValue" value="true" scope="request"/>
										                    							</c:otherwise>
										                    						</c:choose>
										                    					</c:otherwise>
										                    				</c:choose>
										                    			</c:otherwise>
										                    		</c:choose>
							                   					</c:if>
								                   				<p class="catSEOText">${seoText}</p>
								                   			</div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${empty imageURL}">

																	<%-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 --%>
																	<c:choose>
									                   					<c:when test="${chatLink eq true}">
									                   						<div class="grid_9 alpha">
									                   					</c:when>
									                   					<c:otherwise>
																			<div class="grid_7 alpha">
									                   					</c:otherwise>
									                   				</c:choose>
									                   				<%-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 --%>
									                   					<c:if test="${not subCatHeaderShown}">
									                   						<c:set var="subCatHeaderShown" scope="request" value="true"/>
									                   						<c:choose>
												                    			<c:when test="${!(CatFlagL3 eq null)}">
												                    				<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${currentCat}" valueishtml="true"/></h1>
												                    				<c:set var="catHeaderValue" value="true" scope="request"/>
												                    			</c:when>
												                    			<c:otherwise>
												                    				<c:choose>
												                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
														                    				<h1 id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
														                    				<c:set var="catHeaderValue" value="true" scope="request"/>
												                    					</c:when>
												                    					<c:otherwise>
												                    						<c:choose>
												                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
												                    							<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
												                    								<!-- <p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p> -->
												                    								<c:set var="catHeaderValue" value="true" scope="request"/>
												                    							</c:when>
												                    							<c:otherwise>
												                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
												                    								<c:set var="catHeaderValue" value="true" scope="request"/>
												                    							</c:otherwise>
												                    						</c:choose>
												                    					</c:otherwise>
												                    				</c:choose>
												                    			</c:otherwise>
												                    		</c:choose>
									                   					</c:if>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="grid_5 omega marBottom_10 promoImage <c:if test="${key1 eq promoKeyTop}">promoHead</c:if>">
																		<c:choose>
																			<c:when test="${not empty imageHREF}">
																				<a href="${imageHREF}" title="${imageAlt}"><img src="${scene7Path}/${imageURL}" alt="${imageAlt}" /></a>
																			</c:when>
																			<c:otherwise>
																				<img src="${scene7Path}/${imageURL}" alt="${imageAlt}" />
																			</c:otherwise>
																		</c:choose>
																	</div>
																	<div class="grid_7 alpha">
																	 <c:if test="${not subCatHeaderShown}">
																	 	<c:set var="subCatHeaderShown" scope="request" value="true"/>
																	 	<c:choose>
											                    			<c:when test="${!(CatFlagL3 eq null)}">
											                    				<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${currentCat}" valueishtml="true"/></h1>
											                    				<c:set var="catHeaderValue" value="true" scope="request"/>
											                    			</c:when>
											                    			<c:otherwise>
											                    				<c:choose>
											                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
													                    				<h1 id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
													                    				<c:set var="catHeaderValue" value="true" scope="request"/>
											                    					</c:when>
											                    					<c:otherwise>
											                    						<c:choose>
											                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
											                    							<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
											                    								<!-- <p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p> -->
											                    								<c:set var="catHeaderValue" value="true" scope="request"/>
											                    							</c:when>
											                    							<c:otherwise>
											                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
											                    								<c:set var="catHeaderValue" value="true" scope="request"/>
											                    							</c:otherwise>
											                    						</c:choose>
											                    					</c:otherwise>
											                    				</c:choose>
											                    			</c:otherwise>
											                    		</c:choose>
																	 </c:if>
																	</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
						                   		<%-- </div> --%>
						                   		<%-- RM Defect 15730: ALL Concepts: Endeca Center Column Promo Bug --%>
						                   	</c:otherwise>
						             	</c:choose>
		                            </c:if>
		                            <c:if test="${key1 eq promoKeyRight}">
		                         		<dsp:droplet name="ForEach">
		                                	<dsp:param name="array" value="${elementList}" />
		                                    <dsp:oparam name="output">
				                            	 <c:set var="promoSC" value="true"></c:set>
		                                  	</dsp:oparam>
		                            	</dsp:droplet>
			                        </c:if>
			                      	<%-- R2.2 BRAND Promo container from BCC Start --%>
			                        <c:if test="${(frmBrandPage and !isPromoContentAvailable) && key1 eq promoKeyTop &&  not empty elementList}">
		                           		<div class="clearfix promo-12col">
			                           		<dsp:droplet name="ForEach">
			                                	<dsp:param name="array" value="${elementList}" />
			                                    <dsp:oparam name="output">
					                            	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
								                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
								                    <c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            		</c:if>
								                    <c:choose>
								                    	<c:when test="${not empty imageHREF}">
								                    	 <div class="row promoHead">
								                    		<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
			                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
			                                          		</a>
			                                          	 </div>	
								                    	</c:when>
								          	          	<c:otherwise>
								          	          	  <div class="row">
			                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
			                                          	  </div>		
								            	        </c:otherwise>
								                    </c:choose>
			                                  	</dsp:oparam>
			                            	</dsp:droplet>
			                            </div>
			                        <%-- R2.2 BRAND Promo container from BCC END --%>
                         			</c:if>
			                            <%--
			                            <c:otherwise>
				                        	<div class="grid_12 alpha">
				                        		<h1><dsp:valueof param="browseSearchVO.categoryHeader.name" /></h1>
				                        	</div>
				                        </c:otherwise> --%>
			                        <%-- </c:choose> --%>
		                        </dsp:oparam>
		                        <dsp:oparam name="empty">
		                            <div class="grid_7 alpha omega">
		                            <c:set var="fromCatL2AndL3" scope="request" value="true"></c:set>
		                             <c:if test="${not subCatHeaderShown}">
		                             	<c:set var="subCatHeaderShown" scope="request" value="true"/>
		                             	<c:choose>
			                    			<c:when test="${!(CatFlagL3 eq null)}">
			                    				<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
			                    				<c:set var="catHeaderValue" value="true" scope="request"/>
			                    			</c:when>
			                    			<c:otherwise>
			                    				<c:choose>
			                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
					                    				<h1 id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
					                    				<c:set var="catHeaderValue" value="true" scope="request"/>
			                    					</c:when>
			                    					<c:otherwise>
			                    						<c:choose>
			                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
			                    							<h1 class="catHeader" id="address_l2_l3_brand"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
			                    								<!-- <p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p> -->
			                    								<c:set var="catHeaderValue" value="true" scope="request"/>
			                    							</c:when>
			                    							<c:otherwise>
			                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
			                    								<c:set var="catHeaderValue" value="true" scope="request"/>
			                    							</c:otherwise>
			                    						</c:choose>
			                    					</c:otherwise>
			                    				</c:choose>
			                    			</c:otherwise>
			                    		</c:choose>
		                             </c:if>
		                        	</div>
		                        </dsp:oparam>

		                   	<%-- End: Added for Scope # 81 H1 tags --%>
		                   	</dsp:droplet>
		                   		
		                   	
		                   	
							<c:if test="${frmBrandPage and isPromoContentAvailable}">
							    <script type="text/javascript" src="${jsFilePath}"></script>
								<link rel="stylesheet" type="text/css" href="${cssFilePath }" />
							        <div id="bannerContent" class="marBottom_10 clearfix">
										${promoContent}
									</div>
							</c:if>
							
		                   	<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
								<dsp:param param="categoryId" name="id" />
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/commerce/catalog/CatalogNavHistoryCollector">
										<dsp:param param="element" name="item" />
										<dsp:param value="jump" name="navAction" />
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
		                   	<%-- included as part of Release 2.1 implementation --%>
							<dsp:getvalueof var="categoryVO" param="categoryVO" />
		                   	<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
							<dsp:getvalueof var="chatEnabled" param="categoryVO.isChatEnabled" />

							<c:if test="${not frmBrandPage}">
								<%-- RS # 2094542 -- To render chat on link here only if there is no promotion set. --%>
								<c:if test="${empty imageURL}">
								<%-- RS # 2094542 -- To render chat on link here only if there is no promotion set. --%>
									<c:choose>
										<c:when test="${(not empty chatEnabled)  && (chatEnabled eq 'true') && (chatLinkPlaceholder eq 'Top Right')}">
											<dsp:include page="/common/click2chatlink.jsp">
												<dsp:param name="chatURL" value="${categoryVO.chatURL}"/>
												<dsp:param name="pageId" value="${categoryVO.chatCode}"/>
												<dsp:param name="catId" value="${catID}"/>
												<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="grid_3 omega fr"/>
											</dsp:include>
										</c:when>
										<c:when test="${(empty chatEnabled)}">
											<c:if test="${(not empty parentChatEnabled)  && (parentChatEnabled eq 'true') && (parentCatChatLinkPlaceholder eq 'Top Right')}">
												 <dsp:include page="/common/click2chatlink.jsp">
													<dsp:param name="chatURL" value="${categoryVO.chatURL}"/>
													<dsp:param name="pageId" value="${categoryVO.chatCode}"/>
													<dsp:param name="catId" value="${catID}"/>
													<dsp:param name="divApplied" value="${true}"/>
													<dsp:param name="divClass" value="grid_3 omega fr"/>
												 </dsp:include>
											</c:if>
										</c:when>
									</c:choose>
								</c:if>
			                   	 <c:if test="${fromCollege eq 'true' }">
			                   	  <bbbl:textArea key="txt_college_cat_image_grid" language="${pageContext.request.locale.language}"/>
								 </c:if>
							</c:if>
						</div>
						<script>BBB.addPerfMark('ux-destination-verified');</script>
					</div>
					<dsp:getvalueof var="portrait" value="false"/>
					<dsp:getvalueof var="refinements" param="browseSearchVO.categoryHeader.categoryRefinement"/>
					<dsp:getvalueof var="phantomCategory" param="browseSearchVO.categoryHeader.phantomCategory"/>
					<c:if test="${(refinements eq null || empty refinements ) && phantomCategory}">
						<c:set var="dontShowAllTab" value="true"></c:set>
					</c:if>
					
					<%-- Compare Matrix Div Begins --%>
					<div id="ec_adaptivenav"></div>
					<%-- Compare Matrix Div Ends --%>
					
					
					
					<%--R2.2 Story 116C Changes--%>
					<div id="content" class="container_12 clearfix">
					<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
					<dsp:getvalueof var="descriptorList" param="browseSearchVO.descriptors" />
						<c:if test="${not empty facets or fromCatL2AndL3 or fn:length(swsTermsList)>0 or fn:length(descriptorList)>1 }">
					     <div class="<c:out value="${facetClass}"/> marTop_20">
							<c:choose>
								<c:when test="${frmBrandPage }">
									<dsp:include page="/_includes/modules/faceted_bar.jsp">
										<dsp:param name="frmSubCatBrandPage" value="${frmBrandPage}"/>
										<dsp:param name="view" value="${defaultView}" />
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="/_includes/modules/faceted_bar_plp.jsp">
										<dsp:param name="browseSearchVO" param="browseSearchVO" />
										<dsp:param name="seoUrl" value="${seoUrl}" />
										<dsp:param name="showDepartment" value="false" />
										<dsp:param name="totSize" value="${totSize}" />
										<dsp:param name="view" value="${defaultView}" />
										<dsp:param name="floatNode" value="${floatNode}"/>
										<dsp:param name="categoryName" value="${subCatHeader}"/>
										<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
										<dsp:param name="narrowDown" value="${param.narrowDown}"/>
										
									</dsp:include>
								</c:otherwise>
							</c:choose>
					</div>

						<%-- Commented as part of R2.2 SEO friendly URL Story : Start --%>
						<%--
						<dsp:getvalueof var="url" vartype="java.lang.String"  value="?a=1"/>
						<c:if test="${subCatPlp eq true}">
							<dsp:getvalueof var="url" vartype="java.lang.String" value="?subCatPlp=true&a=1"/>
						</c:if>
						--%>
						<%-- Commented as part of R2.2 SEO friendly URL Story : End --%>
							<div class="<c:out value="${facetClass}"/> ">
								<c:if test="${not frmBrandPage}">
									<%-- included as part of Release 2.1 implementation --%>
									<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
									<dsp:getvalueof var="chatURL" param="categoryVO.chatURL" />
									<dsp:getvalueof var="chatCode" param="categoryVO.chatCode" />

									<dsp:getvalueof var="parChatEnabled" value="${parentCatVO.isChatEnabled}" />
									<dsp:getvalueof var="parCatChatLinkPlaceholder" value="${parentCatVO.chatLinkPlaceholder}" />
									<dsp:getvalueof var="parChatURL" value="${parentCatVO.chatURL}" />
									<dsp:getvalueof var="parChatCode" value="${parentCatVO.chatCode}" />

									<c:choose>
										<c:when test="${not empty parentCatVO}">
											<c:if test="${parChatEnabled && (parCatChatLinkPlaceholder eq 'Top Left')}">
												<dsp:include page="/common/click2chatlink.jsp">
													<dsp:param name="chatURL" value="${parChatURL}"/>
													<dsp:param name="pageId" value="${parChatCode}"/>
													<dsp:param name="catId" value="${catID}"/>
													<dsp:param name="divApplied" value="${true}"/>
													<dsp:param name="divClass" value="marTop_20 marBottom_20 chatNowLeft"/>
												</dsp:include>
											</c:if>
										</c:when>
										<c:when test="${chatEnabled && (chatLinkPlaceholder eq 'Top Left')}">
											<dsp:include page="/common/click2chatlink.jsp">
										 		<dsp:param name="chatURL" value="${chatURL}"/>
							             		<dsp:param name="pageId" value="${chatCode}"/>
							             		<dsp:param name="catId" value="${catID}"/>
							                	<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="marTop_20 marBottom_20 chatNowLeft"/>
							             	</dsp:include>
										</c:when>
									</c:choose>
								</c:if>
								<c:set var="totSize" value="${descriptorLength}"/>

	                          <%--R2.2 Story 116C  Changes--%>
	                     

								<%-- included as part of Release 2.1 implementation --%>
								<%-- R2.2 - Story 116E Start --%>
								<c:if test="${not frmBrandPage}">
									<c:choose>
										<c:when test="${not empty parentCatVO}">
											<c:if test="${parChatEnabled and (parCatChatLinkPlaceholder eq 'Bottom Left')}">
												<dsp:include page="/common/click2chatlink.jsp">
													<dsp:param name="chatURL" value="${parChatURL}"/>
													<dsp:param name="pageId" value="${parChatCode}"/>
													<dsp:param name="catId" value="${catID}"/>
													<dsp:param name="divApplied" value="${true}"/>
													<dsp:param name="divClass" value="marTop_10 chatNowLeft"/>
												</dsp:include>
											</c:if>
										</c:when>
										<c:when test="${chatEnabled and (chatLinkPlaceholder eq 'Bottom Left')}">
											<dsp:include page="/common/click2chatlink.jsp">
												<dsp:param name="chatURL" value="${chatURL}"/>
									            <dsp:param name="pageId" value="${chatCode}"/>
												<dsp:param name="catId" value="${catID}"/>
												<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="marTop_10 chatNowLeft"/>
									    	</dsp:include>
								        </c:when>
									</c:choose>
								</c:if>
								<%-- R2.2 - Story 116E End --%>
							</div>
						</c:if>
						
			
						
					
						
						<div id="prodGridContainer" class="<c:out value="${gridClass}"/> noMarTop clearfix ec_gridwall">
						
							<div id="pagTop" class="<c:out value="${gridClass}"/> alpha omega">
								<c:if test="${isRefType}">
									<dsp:getvalueof var="subCategoryPageParam" value="${subCategoryPageParam}&refType=false"/>
								</c:if>
								<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
								<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
								<dsp:param name="view" value="${defaultView}"/>
								<dsp:param name="showViewOptions" value="true"/>
								<dsp:param name="narrowDown" value="${param.narrowDown}"/>
								<dsp:param name="pageSectionValue" value="topOfResultSet"/>
								</dsp:include>
							</div>
								<%-- Start: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>
								<dsp:droplet name="ResultListDroplet">
									<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
									<dsp:oparam name="output">
									<c:choose>
										<c:when test="${defaultView=='list'}">
											<dsp:include page="/_includes/modules/product_list.jsp">
												<dsp:param name="BBBProductListVO" param="BBBProductListVO" />
												<dsp:param name="promoSC" value="${promoSC}"/>
												<dsp:param name="url" value="${url}" />
												<dsp:param name="portrait" value="${portrait}"/>
												<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
											</dsp:include>
										</c:when>
										<c:otherwise>
											<dsp:include page="/_includes/modules/product_grid_5x4.jsp">
												<dsp:param name="BBBProductListVO" param="BBBProductListVO" />
												<dsp:param name="promoSC" value="${promoSC}"/>
												<dsp:param name="url" value="${url}" />
												<dsp:param name="plpGridSize" value="${plpGridSize}"/>
												<dsp:param name="portrait" value="${portrait}"/>
												<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
												<%-- Including new jsp for 4x4 view |List View Redesign Story |Sprint2 |START --%>
												<c:if test="${defaultView=='grid4'}">
													<dsp:param name="plpGridSize" value="4" />
												</c:if>
												<%-- Including new jsp for 4x4 view |List View Redesign Story |Sprint2 |End --%> 
									   </dsp:include>
										</c:otherwise>
									</c:choose>
									</dsp:oparam>
									
								</dsp:droplet>
								<%-- End: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>

							<%-- R2.2 Stroy 116. Display promo content images only when grid size is 5x5 --%>
							<c:if test="${promoSC and plpGridSize == '5'}">
							<div class="<c:out value="${facetClass}"/> omega">
	                        	<dsp:droplet name="ForEach">
		                     		<dsp:param name="array" param="browseSearchVO.promoMap" />
		                        	<dsp:oparam name="output">
		                        		<dsp:getvalueof var="elementList" param="element"/>
		                            	<dsp:getvalueof var="key2" param="key"/>
			                            <c:if test="${key2 eq promoKeyRight}">
			                            	<dsp:droplet name="ForEach">
			                                   <dsp:param name="array" value="${elementList}" />
			                                    <dsp:oparam name="output">
			                                    	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
			                                    	<c:if test="${not empty imageHREF}">
			                                    	<dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            		</c:if>
								                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
								                    <dsp:getvalueof var="imageAlt" param="element.imageAlt"/>
													<div class="promo-2col">
					                                	<a href="${imageHREF}" title="${imageAlt}"><img src="${imagePath}${imageURL}" alt="${imageAlt}" /></a>
		                                            </div>
			                                    </dsp:oparam>
			                             	</dsp:droplet>
			                          	</c:if>
		                    		</dsp:oparam>
		                    	</dsp:droplet>
							</div>
						</c:if>
							<div id="pagBot" class="<c:out value="${gridClass}"/> alpha omega">
								<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
								<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
								<dsp:param name="view" value="${defaultView}"/>
								<dsp:param name="narrowDown" value="${param.narrowDown}"/>
								<dsp:param name="pageSectionValue" value="bottomOfResultSet"/>
								</dsp:include>
							</div>
							<div id="sa_s22_instagram" class="sa_s22_instagram_category"></div>
							<%-- START : Get Blurb of text in Botom of PLP if set.
							<dsp:droplet name="ForEach">
	                     		<dsp:param name="array" param="browseSearchVO.promoMap" />
	                        	<dsp:oparam name="output">
	                        		<dsp:getvalueof var="elementList" param="element"/>
	                            	<dsp:getvalueof var="key2" param="key"/>
		                            <c:if test="${key2 eq 'FOOTER'}">
		                            	<div class="<c:out value="${gridClass}"/> alpha omega clearfix blurbTxt">
		                            	<dsp:droplet name="ForEach">
		                                   <dsp:param name="array" value="${elementList}" />
		                                    <dsp:oparam name="output">
		                                    	<p><dsp:valueof param="element.blurbPlp" valueishtml="true"/></p>
		                                    </dsp:oparam>
		                             	</dsp:droplet>
		                             	</div>
		                          	</c:if>
	                    		</dsp:oparam>
	                    	</dsp:droplet>
	                    	END : Get Blurb of text in Botom of PLP if set. --%>
							
							<%-- START : Get Blurb of text in Botom of PLP if set in BCC. --%>
	                    	
	                    	<c:if test="${not empty seoFooterContent}">
								<div class="<c:out value="${gridClass}"/> alpha omega clearfix blurbTxt">
									<p><c:out value="${seoFooterContent}" escapeXml="false" /><p>
    							</div>
							</c:if>
							 
	                    	 <%-- END : Get Blurb of text in Botom of PLP if set in BCC. --%>
							 
						</div>
							<script>
								if($('#pageWrapper')[0].classList.contains('subCategory')){
									catType = function (){
							            var
							            	_path = document.location.pathname,
							                arr = document.location.href.split('/'),
							                ndx = arr.indexOf('store');

							                if (_path.indexOf('college') > -1) {
							                	catType = 'college';
							                } else {
							                	catType = arr[ndx + 1];
							                }
							                
							            return catType;
							        }
									var sa_page="3"; // constant for the page slider
									var sa_site_id= '${saSrc}';//pass by client
									var sa_instagram_category_type = catType(); // category | brand | search | college i.e. for id type
									
									if(catType != null && catType != undefined) {
										if(catType === 'brand') {
											var sa_instagram_category_code ='${brandName}';	
										} else {
											var sa_instagram_category_code = '${categoryId}';
										}
									}
									
									(function() {
										function sa_async_load() {
											var sa = document.createElement('script');sa.type = 'text/javascript';
											sa.async = true;sa.src = sa_site_id;
											var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);
								       }
									if (window.attachEvent) {
										window.attachEvent('onload', sa_async_load);
									} else {
										window.addEventListener('load', sa_async_load,false);
									}
									}());
								}
							</script>
				
					</div>
			<c:if test="${TellApartOn}">
				<c:choose>
					<c:when test="${frmBrandPage }">
						<bbb:tellApart actionType="pv" pageViewType="SearchResult" />
					</c:when>
					<c:otherwise>
						<bbb:tellApart actionType="pv" pageViewType="ProductCategory" />
					</c:otherwise>
				</c:choose>
			</c:if>
			<dsp:droplet name="Switch">
			<dsp:param name="value" bean="Profile.transient"/>
			<dsp:oparam name="false">
				<dsp:getvalueof var="userId" bean="Profile.id"/>
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:getvalueof var="userId" value=""/>
			</dsp:oparam>
		</dsp:droplet>
		<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.links = linksCertona;
				resx.customerid = "${userId}";


		</script>

		<dsp:getvalueof var="var1" value="" />
		<dsp:getvalueof var="var2" value="" />
		<dsp:getvalueof var="var3" value="" />
		 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array"	param="browseSearchVO.categoryHeader.categoryTree" />
		             	<dsp:oparam name="output"> 
		             		<dsp:getvalueof var="index" param="index" />
		             		<c:if test="${index eq 0}">
		             			<dsp:getvalueof var="var1" param="element" />
		             		</c:if>
		             		<c:if test="${index eq 1}">
		             			<dsp:getvalueof var="var2" param="element" />
		             		</c:if>
		             		<c:if test="${index eq 2}">
		             			<dsp:getvalueof var="var3" param="element" />
		             		</c:if>
		             	</dsp:oparam>
					</dsp:droplet>
				<dsp:importbean bean="/atg/multisite/Site"/>
				<dsp:getvalueof var="applicationId" bean="Site.id" />

			<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
			    <dsp:param name="configKey" value="DimNonDisplayConfig"/>
			    <dsp:oparam name="output">
			        <dsp:getvalueof var="configMap" param="configMap"/>
			    </dsp:oparam>
			</dsp:droplet>
			<c:forEach var="conMap" items="${configMap}">
			    <c:if test="${conMap.key eq applicationId}">
				    <c:choose>
					<c:when test="${fn:contains(conMap.value, var1)}">
						<c:set var="prop2Var" value=""/>
						<c:choose>
						<c:when test="${empty var3}">
							<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="prop3Var">${var1} > ${var2} > ${var3}</c:set>
							<c:set var="catL3Flag" scope="request" value="true"></c:set>
						</c:otherwise>
						</c:choose>
						<dsp:getvalueof var="phantomCategory" param="browseSearchVO.categoryHeader.phantomCategory"/>
						<dsp:getvalueof var="phantomCategoryName" param="browseSearchVO.categoryHeader.name"/>
						<c:if test="${not empty phantomCategory && phantomCategory == 'true'}">
							<c:set var="pageName">${phantomCategoryName}</c:set>
							<c:set var="var1">Floating nodes</c:set>
							<c:set var="prop1Var">Category Page</c:set>
							<c:set var="prop2Var">${phantomCategoryName}</c:set>
							<c:set var="prop3Var">Category Page</c:set>
							<c:set var="var3">${var2}</c:set>
						</c:if>
						<c:set var="prop4Var" value=""/>
						<c:set var="prop5Var" value=""/>
						<c:set var="pageName">Associate Incentive</c:set>
						<c:set var="var1">Associate Incentive</c:set>
						<c:set var="prop1Var">Category Page</c:set>
						<c:set var="prop2Var">Associate Incentive</c:set>
						<c:set var="prop3Var">Category Page</c:set>
					</c:when>
					<c:otherwise>  
						<c:set var="replacedVar1">${fn:replace(var1, "'", "")}</c:set>
						<c:set var="prop2Var">${var1} > ${var2}</c:set>
						<c:choose>
						<c:when test="${empty var3}">
							<c:choose>
							<c:when test="${replacedVar1 == lblWhatsNew || var1 == lblClearance}">
								<c:set var="pageName">${var1} > ${var1} > ${var2}</c:set>
								<c:set var="prop1Var">Sub Category Page</c:set>
								<c:set var="prop2Var"></c:set>
								<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
								<c:if test="${empty var2 || var2==1 || var2==2 || var2==3}">
									<c:set var="prop1Var">Category Page</c:set>
									<c:set var="pageName">${var1} > ${var1} > All</c:set>
									<c:set var="prop2Var">${var1} > ${var1}</c:set>
									<c:set var="prop3Var">${var1} > ${var1} > All</c:set>
								</c:if>
							</c:when>
							<c:otherwise>
							<c:set var="pageName">${var1} > ${var2}</c:set>
							<c:set var="prop1Var">Category Page</c:set>
								<c:set var="prop3Var">Category Page</c:set>
							</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${var3 == 1 || var3 == 2 || var3 == 3}">
									<c:choose>
										<c:when test="${replacedVar1 == lblWhatsNew}">
											<c:set var="pageName">${var1} > ${var1} > ${var2}</c:set>
											<c:set var="prop1Var">Sub Category Page</c:set>
											<c:set var="prop2Var"></c:set>
											<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
											<c:if test="${empty var2 || var2==1 || var2==2}">
												<c:set var="prop1Var">Category Page</c:set>
												<c:set var="pageName">${var1} > ${var1} > All</c:set>
												<c:set var="prop2Var">${var1} > ${var1}</c:set>
                                                <c:set var="prop3Var">${var1} > ${var1} > All</c:set>
											</c:if>
										</c:when>
										<c:when test="${var1 == lblClearance}">
											<c:set var="pageName">${var1} > ${var2} > All</c:set>
											<c:set var="prop1Var">Category Page</c:set>
											<c:set var="prop2Var"></c:set>
											<c:set var="prop3Var">${var1} > ${var2} > All</c:set>
											<c:if test="${empty var2 || var2==1 || var2==2 || var2==3}">
												<c:set var="prop1Var">Category Page</c:set>
												<c:set var="pageName">${var1} > ${var1} > All</c:set>
												<c:set var="prop2Var">${var1} > ${var1}</c:set>
												<c:set var="prop3Var">${var1} > ${var1} > All</c:set>
											</c:if>
										</c:when>
										<c:otherwise>
									<c:set var="pageName">${var1} > ${var2}</c:set>
									<c:set var="prop1Var">Category Page</c:set>
											<c:set var="prop3Var">${var1} > ${var2}</c:set>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
								  <c:choose>
								    <c:when test="${var1 == lblClearance && (var2 == 1 || var2 == 2 || var2 == 1)}">
								      <c:set var="pageName">${var1} > ${var1} > All</c:set>
									  <c:set var="prop1Var">Category Page</c:set>
									  <c:set var="prop3Var">${var1} > ${var1} > All</c:set>
									  <c:set var="catL3Flag" scope="request" value="true"></c:set>
								    </c:when>
								    <c:when test="${var1 == lblClearance}">
								      <c:set var="pageName">${var1} > ${var2} > ${var3}</c:set>
									  <c:set var="prop1Var">Category Page</c:set>
									  <c:set var="prop3Var">${var1} > ${var2} > ${var3}</c:set>
									  <c:set var="catL3Flag" scope="request" value="true"></c:set>
								    </c:when>
								    <c:otherwise>
								      <c:set var="pageName">${var1} > ${var2} > ${var3}</c:set>
									  <c:set var="prop1Var">Sub Category Page</c:set>
									  <c:set var="prop3Var">${var1} > ${var2} > ${var3}</c:set>
									  <c:set var="catL3Flag" scope="request" value="true"></c:set>
								    </c:otherwise>
								  </c:choose>
	             				</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
						<c:set var="prop4Var">List of Products</c:set>
						<c:set var="prop5Var">List of Products in sub-categories</c:set>
					</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<%-- BBBSL-4343 DoubleClick Floodlight START 
	       <c:if test="${DoubleClickOn}">
		       		<c:if test="${(currentSiteId eq BedBathUSSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
	    		 	</c:if>
		    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
		    		 </c:if>
		    		 <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
				    	 <c:set var="cat"><bbbc:config key="cat_category_bedbathcanada" configName="RKGKeys" /></c:set>
				    	 <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
				    	<c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
				   </c:if> 
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u10=${categoryId};u11=${var1},${var2},${var3}"/>
			 		</dsp:include>
		 		</c:if>
	 		 DoubleClick Floodlight END --%>
	 	<%--BBBSL-8716 | PLP Fixes for BOTs --%>	
	<c:if test="${not isRobotOn}">				 
		<script type="text/javascript">
		 function TLGetCookie(c_name){
				if(document.cookie.length>0){
			    c_start=document.cookie.indexOf(c_name + "=");
				if (c_start!=-1){
			      c_start=c_start + c_name.length+1;
			      c_end=document.cookie.indexOf(";",c_start);
				  if (c_end==-1) c_end=document.cookie.length;
			      return unescape(document.cookie.substring(c_start,c_end));
				  }
				}
				 return "";
				}
			var BBB = BBB || {};
			var var1 = '${fn:replace(fn:replace(var1,'\'',''),'"','')}';
			var prop2var='';
			var var2= '';
			var var3='';
            var CatFlagL3 = '${fn:replace(fn:replace(CatFlagL3,'\'',''),'"','')}';
            <c:choose>
                <c:when test="${frmBrandPage}">
                    var prop9Var = '${fn:replace(fn:replace(Keyword,'\'',''),'"','')}';
                </c:when>
                <c:otherwise>
                    var prop9Var = '${fn:replace(fn:replace(prop9Var,'\'',''),'"','')}';
                </c:otherwise>
            </c:choose>
            
			var prop3var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
            var prop1Var ='${fn:replace(fn:replace(prop1Var,'\'',''),'"','')}';
            var subCatHeader ='${fn:replace(fn:replace(subCatHeader,'\'',''),'"','')}';
			var omni_var4 ='${fn:replace(fn:replace(var4,'\'',''),'"','')}';
			var pageName='';
			var var9=prop9Var;
			var var10='${frmBrandPage}';
			var var11='Brand'+">"+var9;
			var var13="${catL3Flag}";
			if(isNaN("${subCatHeader}")){
			var2 = var1+">"+subCatHeader;
			prop2var =  var1+">"+subCatHeader;
			pageName =prop2var;
			}
			if(isNaN("${var4}")) {
				var var4=omni_var4;
			var3 = var2+ '>'+omni_var4;
			prop3var = prop2var+">"+omni_var4;
			pageName=prop3var;
			if(var4.toLowerCase() != 'all'){
				prop1Var = 'Sub Category Page';
			}
			}
			if(!var13 && ('${replacedVar1 == lblWhatsNew || var1 == lblClearance}' == 'false')){
				prop3var = prop3var+">"+'All';
			}
			if(var10)
			{
			var var12 ="Brand Page";
			BBB.subCategoryPageInfo = {
					pageNameIdentifier: 'SubCategoryPage',
					pageName: var11,
					channel: var12,
					prop1: var12,
					prop2: var12,
					prop3: var12,
					eVar4: var12,
					eVar5: var12,
					eVar6: var11 + ':' + '${brandId}',
					prop6: '${pageContext.request.serverName}',
					eVar9: '${pageContext.request.serverName}',
					eVar61: "${OmnitureVariable}"
			};
			}
			else {
				if(prop3var.indexOf('${lblClearance}') >= 0) {
					BBB.subCategoryPageInfo = {
							pageNameIdentifier: 'SubCategoryPage',
							pageName: prop3var,
							channel: var1,
							prop1: prop1Var,
							prop2: prop2var,
							prop3: prop3var,
							eVar4: var1,
							eVar5: var2,
							eVar6: prop3var,
							prop6: '${pageContext.request.serverName}',
							eVar9: '${pageContext.request.serverName}',
							eVar61: "${OmnitureVariable}"
						};
				} else {
					BBB.subCategoryPageInfo = {
							pageNameIdentifier: 'SubCategoryPage',
							pageName: prop3var,
							channel: var1,
							prop1: prop1Var,
							prop2: prop2var,
							prop3: prop3var,
							eVar4: var1,
							eVar5: var2,
							eVar6: prop3var + ':' + '${categoryId}',
							prop6: '${pageContext.request.serverName}',
							eVar9: '${pageContext.request.serverName}',
							eVar61: "${OmnitureVariable}"
						};
				  }				
				}
		</script>
	</c:if>
		
		<%-- R2.2 Story 178-a4 Product Comparison page | Start--%>
		<%-- Sets the url of last navigated PLP in a user session--%>
		<c:if test="${fromCollege}">
			<c:set var="queryParam" value="?fromCollege=${fromCollege}"/>
		</c:if>
		<dsp:setvalue bean="ProductComparisonList.url" value="${url}${queryParam}"/>
		<%-- R2.2 Story 178-a4 Product Comparison page | End--%>
		
		<%-- Find/Change Store Form jsps--%>	
		<c:import url="../_includes/modules/change_store_form.jsp" />
		<c:import url="../selfservice/find_in_store.jsp" />
		<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>

</jsp:body>
		<jsp:attribute name="footerContent">

         </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
