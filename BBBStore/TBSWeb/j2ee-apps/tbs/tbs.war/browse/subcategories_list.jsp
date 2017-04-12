<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
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
	
	 <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" param="categoryId" />
        <dsp:oparam name="output">
        <div class="row">
	        <div class="small-12 columns no-padding">
				<h1><dsp:valueof param="element.displayName" /></h1>
	        </div>
        </div>
        </dsp:oparam>
    </dsp:droplet>
    
  
	<div class="row">
		<div class="small-12 columns no-padding">
			<h2><bbbl:label key="lbl_header_sub_category_list" language="${pageContext.request.locale.language}" /></h2>
		</div>
	</div>
    
	
		
		
	<dsp:droplet name="ForEach">
		<dsp:param param="subcategoriesList" name="array" />
		<dsp:oparam name="outputStart">
			<div class="row">
				<ul class="small-block-grid-2 medium-block-grid-3 large-block-grid-6">
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
				</ul>
			</div>
		</dsp:oparam>
		<dsp:oparam name="output">
			
			<dsp:getvalueof var="countL2" param="count" />
			<dsp:getvalueof var="sizeL2" param="size" />
			<c:set var="catListULClass" value="" />
			<%-- <c:choose>
				<c:when test="${countL2 % 5 == 1}">
					<c:set var="catListULClass" value="alpha" />
					<div class="popularCategoryRow clearfix">
				</c:when>
				<c:when test="${countL2 % 5 == 0}">
					<c:set var="catListULClass" value="omega" />
				</c:when>
			</c:choose> --%>
			
			<li class="category-list">
				<dsp:droplet name="CanonicalItemLink">
					<dsp:param name="id" param="element.categoryId" />
					<dsp:param name="itemDescriptorName" value="category" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
						<dsp:getvalueof var="catName" param="element.categoryName" />
						
						<dsp:a href="${contextPath}${finalUrl}">
		                	<dsp:getvalueof var="imgSrc" param="element.smallImage" />
		                	<dsp:getvalueof var="categoryName" param="element.displayName" />
			                <c:choose>
								<c:when test="${empty imgSrc}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${categoryName}" class="stretch" title="${categoryName}"/>
								</c:when>
								<c:otherwise>
									<img data-interchange="[${scene7Path}/${imgSrc}, (default)], [${scene7Path}/${imgSrc}, (small)], [${scene7Path}/${imgSrc}, (medium)], [${scene7Path}/${imgSrc}, (large)]" class="stretch" title="${categoryName}">
									<noscript><img src="${scene7Path}/${imgSrc}"></noscript>
								</c:otherwise>
							</c:choose>	
	                	</dsp:a> 
						
						
						<h4 class="category-list-header">
							<dsp:a page="${finalUrl}" title="${catName}">${catName}</dsp:a>
						</h4>
					</dsp:oparam>
				</dsp:droplet>
				
					<%-- START Level 3 Categories  for current L2 --%>
					<dsp:droplet name="ForEach">
						<dsp:param param="element.subCategories" name="array" />
						<dsp:oparam name="outputStart">
								<ul class="category-group-list">
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								</ul>
							</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="countL3" param="count" />
							<dsp:getvalueof var="sizeL3" param="size" />
							<dsp:droplet name="CanonicalItemLink">
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
					</dsp:droplet>
			</li>
			<%-- <c:if test="${countL2 % 5 == 0}">
				</div>
			</c:if> --%>
		</dsp:oparam>
	</dsp:droplet>
			
			
			<%-- KP COMMENT START: Dropping this --%>
			
				<%-- Render this Promo if and only if Total L2 count mod 5 is between 1 to 3 --%>
				<%--
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
				</c:if> --%>
			<%-- KP COMMENT END --%>
			
		<%--<c:if test="${(countL2 % 5 != 0) && (sizeL2 == countL2)}">
			</div>
		</c:if> --%>
	
	
</dsp:page>