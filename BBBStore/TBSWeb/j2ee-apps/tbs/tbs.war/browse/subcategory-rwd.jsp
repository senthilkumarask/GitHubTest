<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="nextHref" param="nextHref"/>
	<dsp:getvalueof var="prevHref" param="prevHref"/>
	<dsp:getvalueof var="catID" param="catID"/>
	<dsp:getvalueof var="fromCollege" param="fromCollege"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="prop9Var" param="brandNameForUrl" />
	<dsp:getvalueof var="brandId" param="brandId"/>
	
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
		<c:when test="${not empty catID && l2l3BoostFlag }">
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
	
	
	<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
	<c:set var="plpGridSize">
		<bbbc:config key="GridSize" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="CertonaContext" value="" scope="request" />
	<c:if test="${fromCollege eq 'true' }">
		<c:set var="variation" value="bc" scope="request" />
	</c:if>
	
			<dsp:getvalueof var="categoryId" param="categoryId"/>
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:getvalueof id="applicationId" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>

			<%-- Added as part of R2.2 SEO friendly URL Story : Start --%>
			<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage" scope="request" />
			<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
			<dsp:getvalueof var="firstPageURL" param="browseSearchVO.pagingLinks.firstPage" scope="request" />
			<dsp:getvalueof var="filterApplied" param="browseSearchVO.pagingLinks.pageFilter" scope="request" />
			<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" scope="request" />
			<dsp:getvalueof var="nextPageParams" param="browseSearchVO.pagingLinks.nextPage" scope="request" />
			<dsp:getvalueof var="prevPageParams" param="browseSearchVO.pagingLinks.previousPage" scope="request" />

			<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
			<dsp:getvalueof param="CatalogId" var="CatalogId" />
			<c:set var="subCategoryPageParam" value="" scope="request" />
			<%-- R2.2 - Story 116E Start --%>
			<c:if test="${empty frmBrandPage}">
				<c:if test="${not empty param.subCatPlp && param.subCatPlp eq true}">
					<c:set var="subCategoryPageParam">&subCatPlp=true&a=1</c:set>
				</c:if>
				<dsp:droplet name="CanonicalItemLink">
					<dsp:param name="id" value="${CatalogRefId}" />
					<dsp:param name="itemDescriptorName" value="category" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="seoUrl" vartype="java.lang.String" param="url" scope="request" />
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<c:set var="isFilterApplied" value="false" scope="request" />
			<c:choose>
				<c:when test="${not empty frmBrandPage and frmBrandPage eq true}">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="descriptorLength" param="size"/>
							<dsp:getvalueof var="type" param="element.rootName"/>
							<c:if test="${(descriptorLength ge 1) && (type ne 'RECORD TYPE')}">
								<c:set var="isFilterApplied" value="true" scope="request" />
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="descriptorLength" param="size"/>
							<dsp:getvalueof var="type" param="element.rootName"/>
							<c:if test="${(descriptorLength ge 1) && (type ne 'DEPARTMENT')}">
								<c:set var="isFilterApplied" value="true" scope="request" />
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</c:otherwise>

			</c:choose>


		<bbb:pageContainer>
			<jsp:attribute name="section">browse</jsp:attribute>
			<jsp:attribute name="pageWrapper">subCategory useCertonaJs useScene7</jsp:attribute>
			<jsp:attribute name="pageVariation">${variation}</jsp:attribute>
			<jsp:attribute name="PageType">SubCategoryDetails</jsp:attribute>
			<jsp:attribute name="bodyClass">product-grid</jsp:attribute>
		<jsp:body>
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>

			<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
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
							<dsp:droplet name="ForEach">
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
			<dsp:getvalueof var="subCatHeader"  param="browseSearchVO.categoryHeader.name"/>
			<div class="row" id="content">
				<div class="small-12 columns no-padding">
					
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="browseSearchVO.promoMap" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="elementList" param="element"/>
										<dsp:getvalueof var="key1" param="key"/>
										<c:if test="${key1 eq promoKeyTop &&  not empty elementList}">
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
												            <c:choose>
												                <c:when test="${not empty imageHREF}">
												                 <div class="grid_12 clearfix promo-12col promoHeadCat">
												                	<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
							                                        	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
							                                        </a>
							                                      </div>  
												                </c:when>
												          	    <c:otherwise>
												          	      <div class="grid_12 clearfix promo-12col">
							                                    	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
							                                      </div>
												            	</c:otherwise>
												        	</c:choose>
							                    		</dsp:oparam>
							                    </dsp:droplet>
						                </c:if>
									</dsp:oparam>
							</dsp:droplet>
							<c:choose>
						<c:when test="${!(CatFlagL3 eq null)}">
							<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/>: <span class="subheader"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></span></h1>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
									<h1 ><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
											<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/>: <span class="subheader"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></span></h1>
										</c:when>
										<c:otherwise>
											<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<c:if test="${frmBrandPage}">
						<h1><dsp:valueof value="${Keyword}" valueishtml="true"/></h1>
					</c:if>
				</div>
			</div>
			
			<dsp:getvalueof var="portrait" value="false"/>
			<dsp:getvalueof var="refinements" param="browseSearchVO.categoryHeader.categoryRefinement"/>
			<dsp:getvalueof var="phantomCategory" param="browseSearchVO.categoryHeader.phantomCategory"/>
			<c:if test="${(refinements eq null || empty refinements ) && phantomCategory}">
				<c:set var="dontShowAllTab" value="true"></c:set>
			</c:if>

			<div class="row">
				<div id="left-nav" class="large-3 columns small-medium-right-off-canvas-menu left-nav category_nav">
					<c:choose>
						<c:when test="${frmBrandPage }">
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
					<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${sortOptionVO.sortingOptions}" />

								<dsp:oparam name="outputStart">
									<div class="row show-for-medium-down">
										<h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></h3>
										<ul class="category-list">
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
													</ul>
									</div>
								</dsp:oparam>
								<dsp:oparam name="output">

									<dsp:getvalueof var="sortOption" param="element"/>
									<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">
									<c:choose>
										<c:when test="${sortOption.ascending == 1}">
												<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />
											<c:choose>
												<c:when test="${pagSortOpt == currentSortCode}">
													<li class="active"><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}" >${sortOption.sortValue}</a></li>
												</c:when>
												<c:otherwise>
												<li><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
											<c:choose>
												<c:when test="${pagSortOpt == currentSortCode}">
													<li class="active"><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
												</c:when>
												<c:otherwise>
													<li><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
										</c:if>
								</dsp:oparam>
							</dsp:droplet>
					</c:when>
						<c:otherwise>

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

							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="categoryVO.sortOptionVO.sortingOptions" />

								<dsp:oparam name="outputStart">
									<div class="row show-for-medium-down">
										<h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></h3>
										<ul class="category-list">
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
													</ul>
									</div>
								</dsp:oparam>

								<dsp:oparam name="output">

									<dsp:getvalueof var="sortOption" param="element"/>
									<c:if test="${sortOption.sortUrlParam ne boostDefault && not empty boostDefault}">
									<c:choose>
										<c:when test="${sortOption.ascending == 1}">
												<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-1" />

											<c:choose>
												<c:when test="${pagSortOpt == currentSortCode}">
													<li class="active"><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}" >${sortOption.sortValue}</a></li>
												</c:when>
												<c:otherwise>
												<li><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
												</c:otherwise>
											</c:choose>

										</c:when>
										<c:otherwise>
											<c:set var="currentSortCode" value="${sortOption.sortUrlParam}-0" />
											<c:choose>
												<c:when test="${pagSortOpt == currentSortCode}">

													<li class="active"><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}" >${sortOption.sortValue}</a></li>
												</c:when>
												<c:otherwise>
													<li><a href="${url}${filterAppliedWithoutPrefix}?pagSortOpt=${currentSortCode}${searchQueryParams}${plpViewParam}">${sortOption.sortValue}</a></li>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
										</c:if>
								</dsp:oparam>
							</dsp:droplet>

						</c:otherwise>
					</c:choose>
								<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
								<c:if test="${not empty facets}">

										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="browseSearchVO.descriptors" />
											<dsp:oparam name="outputStart">
												<dsp:getvalueof var="totSize" param="size"/>
											</dsp:oparam>
										</dsp:droplet>

									<%--R2.2 Story 116C  Changes--%>
									<c:choose>
										<c:when test="${frmBrandPage }">
											<%-- KP TODO: This left nav needs refactoring --%>
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
											</dsp:include>
										</c:otherwise>
									</c:choose>
								</c:if>

				</div>
				<div class="small-12 large-9 columns product-grid-container">
					<div class="row grid-control hide-for-medium-down">
						<div class="small-3 medium-2 large-2 right columns">
							<c:choose>
								<c:when test="${view == 'list'}">
									<a class="active layout-icon-list list-grid-toggle" data-view="list" title="List View" id="plpListView"><span></span></a>
									<a class="layout-icon-grid list-grid-toggle" data-view="grid" title="Grid View" id="plpGridView"><span></span></a>
									<a class="layout-icon-grid-4 list-grid-toggle" data-view="grid4" title="Grid View" id="plpGridView4"><span></span></a>
								</c:when>
								<c:when test="${view == 'grid4'}">
									<a class="layout-icon-list list-grid-toggle" data-view="list" title="List View" id="plpListView"><span></span></a>
									<a class="layout-icon-grid list-grid-toggle" data-view="grid" title="Grid View" id="plpGridView"><span></span></a>
									<a class="active layout-icon-grid-4 list-grid-toggle" data-view="grid4" title="Grid View" id="plpGridView4"><span></span></a>
								</c:when>
								<c:otherwise>
									<a class="layout-icon-list list-grid-toggle" data-view="list" title="List View" id="plpListView"><span></span></a>
									<a class="active layout-icon-grid list-grid-toggle" data-view="grid" title="Grid View" id="plpGridView"><span></span></a>
									<a class="layout-icon-grid-4 list-grid-toggle" data-view="grid4" title="Grid View" id="plpGridView4"><span></span></a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>

					<div class="row">
											<dsp:include page="/_includes/modules/brand_pagination_top_1bar.jsp">
											<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
											<dsp:param name="view" value="${defaultView}"/>
											<dsp:param name="showViewOptions" value="true"/>
											</dsp:include>
											<%-- Start: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>
											<dsp:droplet name="ResultListDroplet">
												<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
												<dsp:oparam name="output">
												<c:choose>
													<c:when test="${defaultView=='list'}">
														<%-- KP TO DO: Refactor list --%>
														<dsp:include page="/_includes/modules/product_list-rwd.jsp">
															<dsp:param name="BBBProductListVO" param="BBBProductListVO" />
															<dsp:param name="promoSC" value="${promoSC}"/>
															<dsp:param name="url" value="${url}" />
															<dsp:param name="portrait" value="${portrait}"/>
															<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
														</dsp:include>
													</c:when>
													<c:otherwise>
														<dsp:include page="/_includes/modules/product_grid_5x4-rwd.jsp">
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
										<%--<c:if test="${promoSC and plpGridSize == '5'}">\
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
									</c:if> --%>


									<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
									<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
									<dsp:param name="view" value="${defaultView}"/>
									</dsp:include>

					</div>
				</div>
			</div>
			<%-- KP COMMENT: Commenting out SEO text, which we shouldn't need on an internal application --%>
			<%--
			<div class="row">
				<div class="small-12 columns">


					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.promoMap" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="elementList" param="element"/>
							<dsp:getvalueof var="key2" param="key"/>
							<c:if test="${key2 eq 'FOOTER'}">

								<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${elementList}" />
									<dsp:oparam name="output">
										<div class="category-description"><dsp:valueof param="element.blurbPlp" valueishtml="true"/></div>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>
			--%>
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
		<%-- <c:set var="cert_categoryId" scope="request"><dsp:valueof param="categoryId" /></c:set>
		<c:set var="cert_scheme" scope="request">clp_cd;clp_jfy</c:set>
		<c:set var="cert_cd" scope="request">clp_cd</c:set>
        <c:set var="cert_jfy" scope="request">clp_jfy</c:set>
		<dsp:droplet name="CertonaDroplet">
	      <dsp:param name="scheme" value="${cert_scheme}"/>
	      <dsp:param name="userid" value="${userId}"/>
	      <dsp:param name="siteId" value="${appid}"/>
	       <dsp:param name="categoryId" value="${cert_categoryId}"/>
	      <dsp:oparam name="output">
	          <dsp:getvalueof var="certona_clearenceProductsList" param="certonaResponseVO.resonanceMap.${cert_cd}.productIDsList"/>
	          <dsp:getvalueof var="certona_justForYouProductsList" param="certonaResponseVO.resonanceMap.${cert_jfy}.productIDsList"/>
	          <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
	          <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
	      </dsp:oparam>
	      <dsp:oparam name="error">
	          <c:set var="displayFlag" value="false"/>
	      </dsp:oparam>
	      <dsp:oparam name="empty">
	          <c:set var="displayFlag" value="false"/>
	      </dsp:oparam>
	  </dsp:droplet>  --%>
	  <dsp:getvalueof var="linkString" param="linkString" />
   	  <script type="text/javascript">
    	linksCertona =linksCertona + '${linkString}';
      </script>
		<script type="text/javascript">
			resx.appid = "${appIdCertona}";
			resx.top1 = 100000;
			resx.top2 = 100000;
			resx.customerid = "${userId}";
		    resx.pageid = "${pageIdCertona}";
		    resx.links = linksCertona;
		</script>

		<dsp:getvalueof var="var1" value="" />
		<dsp:getvalueof var="var2" value="" />
		<dsp:getvalueof var="var3" value="" />
		<dsp:droplet name="ForEach">
						<dsp:param name="array"
					param="browseSearchVO.categoryHeader.categoryTree" />
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
				<dsp:getvalueof var="applicationId" bean="Site.id" />

			<dsp:droplet name="BBBConfigKeysDroplet">
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
						<c:set var="prop2Var">${var1} > ${var2}</c:set>
						<c:choose>
						<c:when test="${empty var3}">
							<c:choose>
							<c:when test="${var1=='Whats New' || var1=='Clearance'}">
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
										<c:when test="${var1=='Whats New'}">
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
										<c:when test="${var1=='Clearance'}">
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
											<c:set var="prop3Var">${var1} > ${var2}</c:set>
										</c:otherwise>
									</c:choose>
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
						<c:set var="prop4Var">List of Products</c:set>
						<c:set var="prop5Var">List of Products in sub-categories</c:set>
					</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<%--DoubleClick Floodlight START --%>
			<%-- Commenting out DoubleClick as part of 34473
			<c:if test="${DoubleClickOn}">
				<c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
					<c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
					<c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
					<c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
				</c:if>
				<c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
					<c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
					<c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
					<c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
				</c:if>
				<dsp:include page="/_includes/double_click_tag.jsp">
					<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=null;u5=null;u11=${var1},${subCatHeader},${var4}"/>
				</dsp:include>
			</c:if>
			--%>
			<%--DoubleClick Floodlight END --%>

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
			var prop9Var = '${fn:replace(fn:replace(prop9Var,'\'',''),'"','')}';
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
			if(!var13 && ('${var1=='Whats New' || var1=='Clearance'}' == 'false')){
				prop3var = prop3var+">"+'All';
			}
			if(var10)
			{
			var var12 ="Brand Page ";
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
				BBB.subCategoryPageInfo = {
					pageNameIdentifier: 'SubCategoryPage',
					pageName: prop3var,
					channel: var1,
					prop1: prop1Var,
					prop2: prop2var,
					prop3: prop3var,
					prop4: prop1Var,
					prop5: var1,
					eVar4: var1,
					eVar5: var2,
					eVar6: prop3var + ':' + '${categoryId}',
					prop6: '${pageContext.request.serverName}',
					eVar9: '${pageContext.request.serverName}',
					eVar61: "${OmnitureVariable}"
				};
				}
		</script>
		<%-- R2.2 Story 178-a4 Product Comparison page | Start--%>
		<%-- Sets the url of last navigated PLP in a user session--%>
		<c:if test="${fromCollege}">
			<c:set var="queryParam" value="?fromCollege=${fromCollege}"/>
		</c:if>
		<dsp:setvalue bean="ProductComparisonList.url" value="${url}${queryParam}"/>
		<%-- R2.2 Story 178-a4 Product Comparison page | End--%>

		<!-- Find/Change Store Form jsps-->
		<c:import url="../_includes/modules/change_store_form.jsp" />
		<c:import url="../selfservice/find_in_store.jsp" />

</jsp:body>
		<jsp:attribute name="footerContent">

		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
