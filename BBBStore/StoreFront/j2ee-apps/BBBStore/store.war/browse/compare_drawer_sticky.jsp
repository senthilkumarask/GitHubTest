<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="url" bean="ProductComparisonList.url"/>
	<div id="compareDrawerSticky" class="compare-box-topWrapper">
		<div class="compare-box width_12 padTop_10">
				<c:set var="imageSize" value="63"/>
				<div class="compare-items">
					<dsp:getvalueof var="items" bean="ProductComparisonList.items"/>
					<c:set var="size" value="${fn:length(items)}"/>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${items}" />
						<dsp:oparam name="output">
						   <dsp:getvalueof var="count" param="count"/>
						   <dsp:getvalueof var="length" param="size"/>
						   <dsp:getvalueof var="productId" param="element.productId"/>
							<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
							<dsp:getvalueof var="isProdActive" param="element.productActive"/>
							<c:set var="productName"><dsp:valueof param="element.productName" valueishtml="true"/></c:set>
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productId"/>
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								</dsp:oparam>
							</dsp:droplet>
							
							<c:choose>
							  <c:when test="${count eq 1}">
									 <c:set var="compareClass" value="compare-item grid_3 alpha noMarLeft"/>
							  </c:when>
							  <c:when test="${count eq 4}">
									 <c:set var="compareClass" value="compare-item grid_3 omega noMarRight"/>
							  </c:when>
							  <c:otherwise>
									 <c:set var="compareClass" value="compare-item grid_3"/>
							  </c:otherwise>
							</c:choose>
				
							<div class="${compareClass} clearfix" data-productid="${productId}">
								<c:choose>
									<c:when test="${isProdActive}">
										<c:choose>
											<c:when test="${not empty thumbnailImagePath}">
												<dsp:a page="${finalUrl}" iclass="prodImg fl">
													<img class="compare-image" src="${scene7Path}/${thumbnailImagePath}" height="${imageSize}" width="${imageSize}" alt="${productName}" title="${productName}"/>
												</dsp:a>
											</c:when>
											<c:otherwise>
												<dsp:a page="${finalUrl}" iclass="prodImg fl">
													<img height="${imageSize}" width="${imageSize}" alt="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound compare-image" alt="${productName}" title="${productName}"/>
												</dsp:a>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${not empty thumbnailImagePath}">
												<img class="compare-image fl" src="${scene7Path}/${thumbnailImagePath}" height="${imageSize}" width="${imageSize}" alt="${productName}" title="${productName}"/>
											</c:when>
											<c:otherwise>
												<img height="${imageSize}" width="${imageSize}" alt="${productName}" title="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound compare-image fl"/>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<div class="prodName"> 
									<c:choose>
										<c:when test="${isProdActive}">
											<dsp:a page="${finalUrl}"><dsp:valueof param="element.productName" valueishtml="true"/></dsp:a>
										</c:when>
										<c:otherwise>
											<dsp:valueof param="element.productName" valueishtml="true"/>
										</c:otherwise>
									</c:choose> 
								</div>
							</div>			
						</dsp:oparam>
					</dsp:droplet>
					<c:set var="emptySize" value="${4-size}"/>
					<c:forEach var="i" begin="1" end="${emptySize}">
			  			<div class="compare-item empty-item">
							<a href="${url}" class="prodImg block" title="Add Another Item">
							<img class="compare-image" src="${imagePath}/_assets/global/images/compare_add_item.png" alt="Add Another Item"></a>
							<div class="prodName">&nbsp; </div>
						</div>
					</c:forEach>
					
					
					
					
				</div>
				<div class="clear"></div>
			</div>
	</div>
	
	<div class="stickyPlaceholder hidden">
		<div class="compare-item empty-item"> <a title="Add Another Item" class="prodImg" href=""> <img src="/_assets/global/images/compare_add_item.png" class="compare-image" alt="Add Another Item"></a> <div class="prodName">&nbsp; </div>
		
		</div>
	</div>
</dsp:page>