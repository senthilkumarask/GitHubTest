<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />

<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
	<dsp:param name="value" param="categoryContainer" />
	<dsp:oparam name="false">
		
		<%-- featured Categories Display --%>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h2 class="divider">Featured Categories <%--<bbbl:label key="lbl_homepage_popular_categories" language="<c:out param='${pageContext.request.locale.language}'/>" /> --%></h2>
			</div>
		</div>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param param="categoryContainer" name="array" />
			<dsp:oparam name="outputStart">
				<div class="row">
					<ul class="small-block-grid-2 medium-block-grid-3 large-block-grid-6">
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</ul>
					</div>
			</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof var="size" param="size"/>
				<dsp:getvalueof var="count" param="count"/>
				 
					<li class="category-list">
					 <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
						<dsp:param name="id" param="element.categoryId" />
						<dsp:param name="elementName" value="mainCategory" />
						<dsp:oparam name="output">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.categoryId" />
								<dsp:param name="itemDescriptorName" value="category" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
									
									<dsp:a href="${contextPath}${finalUrl}">
					                	<dsp:getvalueof var="imgSrc" param="mainCategory.smallImage" />
					                	<dsp:getvalueof var="categoryName" param="mainCategory.displayName" />
						                <c:choose>
											<c:when test="${empty imgSrc}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${categoryName}" class="stretch" title="${categoryName}"/>
											</c:when>
											<c:otherwise>
												<img data-interchange="[${scene7Path}/${imgSrc}, (default)], [${scene7Path}/${imgSrc}, (small)], [${scene7Path}/${imgSrc}, (medium)], [${scene7Path}/${imgSrc}, (large)]" alt="${categoryName}" class="stretch" title="${categoryName}">
												<noscript><img src="${scene7Path}/${imgSrc}"></noscript>
											</c:otherwise>
										</c:choose>	
				                	</dsp:a>           
									<h4 class="category-list-header">
					                 	<dsp:a href="${contextPath}${finalUrl}" title="${categoryName}">
						                	<dsp:valueof param="mainCategory.displayName" /> 
					                	</dsp:a>
									</h4>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="element.subCategories" name="array" />
						<dsp:oparam name="outputStart">
							<ul class="category-group-list">
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</ul>
						</dsp:oparam>
						<dsp:oparam name="output">
								<li>
								<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
									<dsp:param name="id" param="element" />
									<dsp:param name="elementName" value="subCategory" />
									<dsp:oparam name="output">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="element" />
											<dsp:param name="itemDescriptorName" value="category" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												<dsp:getvalueof var="subCatName" vartype="java.lang.String" param="subCategory.displayName" />
												<dsp:a href="${contextPath}${finalUrl}" title="${subCatName}">
													<dsp:valueof param="subCategory.displayName" />
												</dsp:a>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</li>
						</dsp:oparam>
					</dsp:droplet>
				</li>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>
</dsp:page>