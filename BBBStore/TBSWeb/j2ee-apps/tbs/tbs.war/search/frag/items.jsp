<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:getvalueof var="commItem" param="commItem" />
	<dsp:getvalueof var="autoWaiveClassification" param="autoWaiveClassification"/>
	
	<dsp:droplet name="SKUDetailDroplet">
		<dsp:param name="skuId" param="skuId"/>
		<dsp:param name="elementName" value="pSKUDetailVO"/>
		<dsp:param name="fullDetails" value="true" />
		<dsp:oparam name="output">
			
			<dsp:getvalueof var="productId" param="pSKUDetailVO.parentProdId" />
			<dsp:getvalueof var="skuId" param="skuId" />
			
			<dsp:droplet name="CanonicalItemLink">
			<dsp:param name="id" value="${productId}" />
			<dsp:param name="itemDescriptorName" value="product" />
			<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="canonicalUrl" vartype="java.lang.String" param="url" />
				
				<c:url value="${canonicalUrl}" var="finalUrl">
					<c:param name="skuId" value="${skuId}"/>
				</c:url>
			</dsp:oparam>
			</dsp:droplet>
			<div class="medium-1 columns">&nbsp;&nbsp;</div>
			<c:choose>
				<c:when test="${commItem.commerceItemClassType eq 'default' ||
								commItem.commerceItemClassType eq 'tbsCommerceItem' || 
								commItem.commerceItemClassType eq 'bbbCommerceItem'}">
					<a href="${finalUrl}">
						<dsp:valueof param="pSKUDetailVO.displayName" valueishtml="true" />
					</a>
				</c:when>
				<c:otherwise>
						<dsp:valueof param="pSKUDetailVO.displayName" valueishtml="true" />
				</c:otherwise>
			</c:choose>
			(Quantity: <dsp:valueof param="qty"/>)
			<c:if test="${autoWaiveClassification ne null && not empty autoWaiveClassification}">
				<bbbl:label key="lbl_autowaive_item_code" language="<c:out param='${language}'/>"/> ${autoWaiveClassification}
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>