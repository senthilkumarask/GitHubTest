<dsp:page>
	<dsp:importbean bean="com/bbb/cms/droplet/CmsNavFlyoutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:importbean	bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:importbean bean="/com/bbb/redirectURLs/CategoryRedirectURLLoader"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof id="catalogId" param="CatalogId" />
	<dsp:getvalueof var="categoryName" param="catName" />
	<dsp:getvalueof var="CategoryMainURL" param="CategoryURL"/>
	<dsp:getvalueof var="categoryRedirectURLMap" bean="CategoryRedirectURLLoader.categoryRedirectURLMap"/>
	<dsp:getvalueof var="rootCategory" param="rootCategory"/>
	<c:set var="maxL3CategoryLimit"><bbbc:config key="HEADER_l3_MAX_LIMIT" configName="ContentCatalogKeys" /></c:set>
	<c:set var="brandValue" value="true"/>
	<c:set var="urlMoreCategory" value=""/>
	<div class="shopLinkPanel">
	<div style="min-height:500px">
	<dsp:droplet name="CmsNavFlyoutDroplet">
		<dsp:param name="rootCategory" param="rootCategory"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="categoryTreeMap" param="categoryTreeMap" />
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" value="${categoryTreeMap}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="countItems" param="count" />
						<dsp:getvalueof var="sizeItems" param="size" />
						<dsp:getvalueof var="subelement" param="element" />
						<c:if test="${!empty subelement}">							
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${subelement}" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="count" param="count" />
									<dsp:getvalueof var="size" param="size" />
									<dsp:getvalueof var="catName" param="element.name" />
									 <ul class="navListSS grid_3">
									<dsp:droplet name="CustomLandingTemplateDroplet">
									<dsp:param name="categoryId"  param="element.query"/>
										<dsp:param name="templateName" value="customLandingTemplate" />
										<dsp:param name="alternateURLRequired" value="false"/>
										<dsp:oparam name="output">
											<dsp:param name="cmResponseVO" param="cmsResponseVO.responseItems[0]" />
											<dsp:getvalueof var="clpName"  param="cmResponseVO.clpName"/>
											<dsp:getvalueof var="id" param="cmResponseVO.id"/>
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" value="${id}" />
													<dsp:param name="itemDescriptorName" value="customLandingTemplate" />
													<dsp:param name="repositoryName" value="/com/bbb/cms/repository/CustomLandingTemplate" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="clpUrl" vartype="java.lang.String" param="url"/> 
															<c:set var="urlMoreCategory" value="${clpUrl}"/>
															<dsp:getvalueof var="catName" param="element.name" />
																<li class="l2title">
																	<dsp:a page="${clpUrl}" title="${catName}">${catName}</dsp:a>
																</li>
														</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
									<dsp:oparam name="error">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.query" />
										<dsp:getvalueof var="sourceCategoryId" param="element.query"/>
										<c:if test="${not empty categoryRedirectURLMap and categoryRedirectURLMap.containsKey(sourceCategoryId)}">
											<dsp:param name="doItemLookUp" value="${false}"/>
										</c:if>
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="l2CategoryURL" vartype="java.lang.String" param="url"/>
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
													<c:set var="urlMoreCategory" value="${finalUrl}"/>
												<dsp:getvalueof var="catName" param="element.name" />
													<li class="l2title">
													<dsp:a page="${finalUrl}" title="${catName}">${catName}</dsp:a></li>
											</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							<%-- Generate Category URL if CLP is not mapped to it --%>
							<dsp:oparam name="empty">
							<dsp:getvalueof var="query" param="element.query"/>
								 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.query" />
								<dsp:getvalueof var="sourceCategoryId" param="element.query"/>
								 <c:if test="${not empty categoryRedirectURLMap and categoryRedirectURLMap.containsKey(sourceCategoryId)}">
								  <dsp:param name="doItemLookUp" value="${false}"/>
								 </c:if>
								<dsp:param name="itemDescriptorName" value="category" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="l2CategoryURL" vartype="java.lang.String" param="url"/>
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								<c:set var="urlMoreCategory" value="${finalUrl}"/>
								<dsp:getvalueof var="catName" param="element.name" />
									<li class="l2title"><dsp:a page="${finalUrl}" title="${catName}">${catName}</dsp:a></li>
								</dsp:oparam>
							 </dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="element.categoryRefinement" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="childCatCount" param="count" />
										<dsp:getvalueof var="childCatName" param="element.name" />
										<c:if test="${childCatCount <= maxL3CategoryLimit}">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											  <dsp:param name="id" param="element.query" />
											   <dsp:getvalueof var="sourceCategoryId" param="element.query"/>
												  <c:if test="${not empty categoryRedirectURLMap and categoryRedirectURLMap.containsKey(sourceCategoryId)}">
													 <dsp:param name="doItemLookUp" value="false"/>
												   </c:if>
													   <dsp:param name="itemDescriptorName" value="category" />
														<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
														 <dsp:oparam name="output">
														<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
															<li><dsp:a page="${finalUrl}">${childCatName}</dsp:a></li>

														</dsp:oparam>
													   </dsp:droplet>
											</c:if>
											</dsp:oparam>
											<dsp:oparam name="outputEnd">
												<c:if test="${childCatCount > maxL3CategoryLimit}">
													<li>
														<dsp:a page="${urlMoreCategory}">More</dsp:a>
													</li>
												</c:if> 
											</dsp:oparam>
									</dsp:droplet>
											</ul>
										</dsp:oparam>
										</dsp:droplet>										
							 </c:if>
						</dsp:oparam>
					</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
		</div>
		<div>
			<hr class="showAllRow">
			<span class="menuListShowAll">
				<dsp:a page="${CategoryMainURL}"><bbbl:label key="lbl_homePage_header_shopALL" language ="${pageContext.request.locale.language}"/> ${fn:toUpperCase(categoryName)}</dsp:a>
			</span>
		</div>
	</div>
</dsp:page>