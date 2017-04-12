<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductRelatedCategoriesDroplet"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="productId" param="productId" />
<dsp:getvalueof var="siteId" param="siteId" />
<dsp:droplet name="ProductRelatedCategoriesDroplet">
	<dsp:param name="id" param="productId" />
	<dsp:param name="siteId" param="siteId"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="relatedCategories" param="relatedCategories" />
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" value="${relatedCategories}"/>
			<dsp:oparam name="false">
			<h3><bbbl:label key='lbl_related_categories' language="${pageContext.request.locale.language}" /></h3>
				<ul class="bread-crumb-pdp">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${relatedCategories}"/>
					<dsp:oparam name="output">
						<dsp:droplet name="CanonicalItemLink">
							<dsp:param name="id" param="element.categoryId" />
							<dsp:param name="itemDescriptorName" value="category" />
							<dsp:param name="repositoryName"
								value="/atg/commerce/catalog/ProductCatalog" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
									param="url" />
								<li><a href="${contextPath}${finalUrl}"><dsp:valueof param="element.categoryName"/></a></li>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
				</ul>
			</dsp:oparam>
		</dsp:droplet>		
	</dsp:oparam>
</dsp:droplet>
</dsp:page>