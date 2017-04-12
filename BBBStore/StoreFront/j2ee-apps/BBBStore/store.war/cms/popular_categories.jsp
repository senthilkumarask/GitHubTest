<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />

<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">

<dsp:param name="value" param="categoryContainer" />

<dsp:oparam name="false">

<div id="popularCategory" class="grid_12">
	<h2><bbbl:label key="lbl_homepage_popular_categories" language="<c:out param='${pageContext.request.locale.language}'/>" /></h2>

	 
	<div class="popularCategoryList clearfix">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param param="categoryContainer" name="array" />
	<dsp:oparam name="output">
	<dsp:getvalueof var="size" param="size"/>
	<dsp:getvalueof var="count" param="count"/>
	 
 <c:if test="${count < 7}">
	<c:choose>
		<c:when test="${count eq 1}">
	 		<ul class="grid_2 alpha">
		</c:when>
		<c:otherwise>
			 <c:choose>
				   	<c:when test="${count eq 6}">
				    	<ul class="grid_2 omega">
				  	</c:when>
				  	<c:otherwise>
				     	<ul class="grid_2">
				  	</c:otherwise>
			 </c:choose>
		</c:otherwise>
	</c:choose>
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
					<li class="catImage">
						<dsp:a iclass="catImage" href="${contextPath}${finalUrl}">
		                	<dsp:getvalueof var="imgSrc" param="mainCategory.smallImage" />
		                	<dsp:getvalueof var="categoryName" param="mainCategory.displayName" />
			                <c:choose>
								<c:when test="${empty imgSrc}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="image of ${categoryName}" height="94" width="146" />
								</c:when>
								<c:otherwise>
									<img class="productImage lazyLoad loadingGIF" src="" data-lazyloadsrc="${scene7Path}/${imgSrc}" height="94" width="146" alt="image of ${categoryName}" />
								</c:otherwise>
							</c:choose>	
	                	</dsp:a>
					</li>
					 
					<li class="catCaption">
	                 	<dsp:a iclass="catCaption" href="${contextPath}${finalUrl}" title="${categoryName}">
		                	<dsp:valueof param="mainCategory.displayName" /> 
	                	</dsp:a>
					</li>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
		
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param param="element.subCategories" name="array" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="count" param="count"/>
			<c:if test="${count < 6}">
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
			</c:if>
			</li>
		</dsp:oparam>
	</dsp:droplet>	
	</ul>
	</c:if>
	</dsp:oparam>
</dsp:droplet>
	</div>
</div>
	</dsp:oparam>
</dsp:droplet>
</dsp:page>