<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean	bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:page>
	<dsp:getvalueof var="categoryId" param="categoryId" />
	<dsp:getvalueof var="siteL3Count" bean="Site.siteL3Count" />
	
	<dsp:getvalueof var="maxL2Count" param="landingTemplateVO.L2CategoryCount"/>
	<dsp:getvalueof var="maxL3Count" param="landingTemplateVO.L3CategoryCount"/>
	<c:if test="${not empty maxL3Count}">
		<c:set var="siteL3Count" value="${maxL3Count}"/>
	</c:if>
	
	<c:set var="l3ViewMore"><bbbl:label key="lbl_cat_landing_l3_viewMore" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	<c:set var="l3ViewLess"><bbbl:label key="lbl_cat_landing_l3_viewLess" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	<c:set var="l2ViewMore"><bbbl:label key="lbl_cat_landing_l2_viewMore" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	<c:set var="l2ViewLess"><bbbl:label key="lbl_cat_landing_l2_viewLess" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	
	<div id="popularCategory" class="grid_12 clearfix">
		<h2><bbbl:label key="lbl_header_sub_category_list" language="${pageContext.request.locale.language}" /></h2>
		<div class="popularCategoryList grid_12 clearfix">
			<%-- outer loop for creating category columns --%>
			<%-- class alpha is ONLY for the first UL and class omega is ONLY for the last UL --%>
		 
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param param="subcategoriesList" name="array" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="countL2" param="count" />
					<dsp:getvalueof var="sizeL2" param="size" />
					<c:set var="catListULClass" value="" />
					<c:choose>
						<c:when test="${countL2 % 5 == 1}">
							<c:set var="catListULClass" value="alpha" />
							<div class="popularCategoryRow clearfix">
						</c:when>
						<c:when test="${countL2 % 5 == 0}">
							<c:set var="catListULClass" value="omega" />
						</c:when>
					</c:choose>
					
					<ul class="grid_2 clearfix ${catListULClass}">
					<%-- Generate CLP URL if CLP is mapped to it --%>
						<dsp:droplet name="CustomLandingTemplateDroplet">
							<dsp:param name="categoryId" param="element.categoryId" />
							<dsp:param name="templateName" value="customLandingTemplate" />
							<dsp:param name="alternateURLRequired" value="false" />
							<dsp:oparam name="output">
								<dsp:param name="cmResponseVO"
									param="cmsResponseVO.responseItems[0]" />
								<dsp:getvalueof var="clpName" param="cmResponseVO.clpName" />
								<dsp:getvalueof var="catName" param="element.categoryName" />

								<dsp:getvalueof var="id" param="cmResponseVO.id" />
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${id}" />
									<dsp:param name="itemDescriptorName"
										value="customLandingTemplate" />
									<dsp:param name="repositoryName"
										value="/com/bbb/cms/repository/CustomLandingTemplate" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="clpUrl" vartype="java.lang.String"
											param="url" />
										<li class="catCaption"><dsp:a page="${clpUrl}"
												title="${catName}">${catName}</dsp:a></li>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							<%-- Generate Category URL if CLP is not mapped to it --%>
							<dsp:oparam name="error">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="element.categoryId" />
									<dsp:param name="itemDescriptorName" value="category" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />
										<dsp:getvalueof var="catName" param="element.categoryName" />
										<li class="catCaption"><dsp:a page="${finalUrl}"
												title="${catName}">${catName}</dsp:a></li>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>

							<dsp:oparam name="empty">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="element.categoryId" />
									<dsp:param name="itemDescriptorName" value="category" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />
										<dsp:getvalueof var="catName" param="element.categoryName" />
										<li class="catCaption"><dsp:a page="${finalUrl}"
												title="${catName}">${catName}</dsp:a></li>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>


						<%-- START Level 3 Categories  for current L2 --%>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="element.subCategories" name="array" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="countL3" param="count" />
								<dsp:getvalueof var="sizeL3" param="size" />
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="element.categoryId" />
									<dsp:param name="itemDescriptorName" value="category" />
									<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										<dsp:getvalueof var="catName" param="element.categoryName" />
										<li><dsp:a page="${finalUrl}" title="${catName}">${catName}</dsp:a></li>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								<%--  Check if the total size of L3 for a given L2 is more than the threshold set at Site level, If yes, Display View More link. --%>
								<c:if test="${sizeL3 > siteL3Count}">
									<li>
										<span class="viewMoreCat"><a href="javascript:void(0)" title="${l3ViewMore}">${l3ViewMore}</a></span>
										<span class="viewLessCat"><a href="javascript:void(0)" title="${l3ViewLess}">${l3ViewLess}</a></span>
									</li>
								</c:if>
								<dsp:getvalueof var="sizeL3" value="0" />
							</dsp:oparam>
						</dsp:droplet>
					</ul>
					<c:if test="${countL2 % 5 == 0}">
						</div>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			
			<%-- Render this Promo if and only if Total L2 count mod 5 is between 1 to 3 --%>
			<c:if test="${(sizeL2 % 5 <= 3) && sizeL2 % 5 > 0 }">
				<dsp:getvalueof var="promoAvl" param="landingTemplateVO.categoryPromoWidget"/>
				<dsp:getvalueof var="imageAltText" param="landingTemplateVO.categoryPromoWidget.imageAltText" />
				<dsp:getvalueof var="imgSrc" param="landingTemplateVO.categoryPromoWidget.imageURL" />
				<dsp:getvalueof var="imgLink" param="landingTemplateVO.categoryPromoWidget.imageLink" />
				<dsp:getvalueof var="promoBoxFirst" param="landingTemplateVO.categoryPromoWidget.promoBoxContent" />
				<c:if test="${not empty promoAvl}">
					<div class="categoryWidgetPromo">
						<c:choose>
							<c:when test="${not empty promoBoxFirst}">
								<dsp:valueof value="${promoBoxFirst}" valueishtml="true"/>
							</c:when>
							<c:otherwise>
								<dsp:a href="${imgLink}" title="${imageAltText}">
									<c:choose>
										<c:when test="${empty imgSrc}">
											<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${imageAltText}" title="${imageAltText}"/>
										</c:when>
										<c:otherwise>
											<img src="${imgSrc}" onerror="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${imageAltText}" title="${imageAltText}"/>
										</c:otherwise>
									</c:choose>
								</dsp:a>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
			</c:if>
			
			<c:if test="${(countL2 % 5 != 0) && (sizeL2 == countL2)}">
				</div>
			</c:if>
			
			<%-- Render this View More Link if and only if Total L2 count / 10 is >0 --%>
			<c:if test="${sizeL2 / 10  > 1}">
				<div id="expandCollapseCategories">
					<a href="javascript:void(0);" class="expandCategories" title="${l2ViewMore}">${l2ViewMore}</a>
					<a href="javascript:void(0);" class="collapseCategories" title="${l2ViewLess}">${l2ViewLess}</a>
				</div>
			</c:if>
		</div>
	</div>
</dsp:page>