<dsp:page>

	<dsp:importbean bean="/com/bbb/cms/droplet/CmsNavFlyoutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="brandFacetDisplayName">
        		<bbbc:config key="Brand" configName="DimDisplayConfig" />
    </c:set>

	
	
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof id="catalogId" param="CatalogId" />
	<dsp:getvalueof id="flyoutCacheTimeout" param="flyoutCacheTimeout" />

<%-- 	<dsp:droplet name="Cache"> --%>
<%-- 		<dsp:param name="key" value="${catalogId}_${currentSiteId}" /> --%>
<%-- 		<dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/> --%>
<%-- 		<dsp:oparam name="output"> --%>
			<dsp:droplet name="CmsNavFlyoutDroplet">
				<dsp:param name="rootCategory" param="rootCategory"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="categoryTreeMap" param="categoryTreeMap" />
					<dsp:droplet name="ForEach">
						<dsp:param param="categoryTreeMap" name="array" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="subelement" param="element" />
							<c:if test="${!empty subelement}">

								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${subelement}" />
										<dsp:oparam name="outputStart">
										<ul class="off-canvas-list">
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
										</ul>
									</dsp:oparam>
									<dsp:oparam name="output">
										<dsp:getvalueof var="categoryRefinement" param="element.categoryRefinement" scope="request" vartype="java.util.List" />
										<li class="collapse-subnav">
											<dsp:droplet name="CanonicalItemLink">
												<dsp:param name="id" param="element.query" />
												<dsp:param name="itemDescriptorName" value="category" />
												<dsp:param name="repositoryName"
													value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
														param="url" />
													<dsp:a page="${finalUrl}">
														<dsp:valueof param="element.name" />
													</dsp:a>
													<div class="plus-minus">
														<span></span>
													</div>
												</dsp:oparam>
											</dsp:droplet>
												<c:if test="${categoryRefinement != null}">
													<ul class="off-canvas-list">
														<dsp:droplet name="ForEach">
														<dsp:param param="element.categoryRefinement" name="array" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="catName" param="element.name" />
															<dsp:droplet name="CanonicalItemLink">
																<dsp:param name="id" param="element.query" />
																<dsp:param name="itemDescriptorName" value="category" />
																<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
																<dsp:oparam name="output">
																	<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
																	<li><dsp:a page="${finalUrl}">${catName}</dsp:a></li>
																</dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
														</dsp:droplet>
													</ul>
												</c:if>
										</li>
									</dsp:oparam>
								</dsp:droplet>

							</c:if>
						</dsp:oparam>
					</dsp:droplet>

					<ul class="shop-by-brand">
						<li class="sub-cat-title"><a href="${contextPath}/page/brands"><bbbl:label key="lbl_menu_shop_by_brand" language ="${pageContext.request.locale.language}"/></a></li>
						<dsp:droplet name="ForEach">
							<dsp:param param="brandCategory.facets" name="array" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="facetName" param="element.name" scope="page" />
								<c:if test="${facetName == brandFacetDisplayName}">
									<dsp:droplet name="ForEach">
										<dsp:param param="element.facetRefinement" name="array" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="count1" param="index" />
											<dsp:getvalueof var="size1" param="size" />
											<c:set var="more" value='${size1}'/>
											<c:if test="${count1 < 11}">
												<dsp:getvalueof var="name" param="element.name" />
												<%-- R2.2 SEO Friendly URL changes : Start--%>
												<dsp:droplet name="BrandDetailDroplet">
													<dsp:param name="origBrandName" value="${name}"/>
													<dsp:oparam name="seooutput">
														<dsp:getvalueof var="seoUrl" param="seoUrl" />
														<c:url var="brandetailUrl" value="${seoUrl}" scope="request" />
													</dsp:oparam>
												</dsp:droplet>
												<li><a rel="nofollow" href="${brandetailUrl}">${name}</a></li>
												<%-- R2.2 SEO Friendly URL changes : End--%>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
									<c:if test="${more > 10}">
										<li><a rel="nofollow" href="/tbs/page/brands"><bbbl:label key="lbl_menu_more" language ="${pageContext.request.locale.language}"/></a></li>
									</c:if>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</ul>

				</dsp:oparam>
			</dsp:droplet>
<%-- 		</dsp:oparam> --%>
<%-- 	</dsp:droplet> --%>

</dsp:page>
