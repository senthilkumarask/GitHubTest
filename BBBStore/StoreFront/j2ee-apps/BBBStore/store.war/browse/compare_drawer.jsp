<dsp:page>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<div id="compareDrawer" class="compare-box-wrapper">
<div class="compare-box width_12 padTop_10">
		<div class="compare-text-wrapper marRight_20">
			<h3 class="marBottom_5"><bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" /></h3>
			<span><bbbl:textArea key="txt_compare_product" language="${pageContext.request.locale.language}"/></span>
		</div>
		<c:set var="imageSize" value="63"/>
		<div class="compare-items">
			<dsp:getvalueof var="items" bean="ProductComparisonList.items"/>
			<c:set var="size" value="${fn:length(items)}"/>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${items}" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="productId" param="element.productId"/>
					<dsp:getvalueof var="skuId" param="element.skuId"/>
					<dsp:getvalueof var="image" param="element.imagePath"/>
					<dsp:getvalueof var="mediumImage" param="element.thumbnailImagePath"/>
					<dsp:getvalueof var="imgPath" value="${image}"/>
					<c:set var="productName"><dsp:valueof param="element.productName" valueishtml="true"/></c:set>
					<div class="compare-item" data-productid="${productId}">
						<a href="javascript:void(0);" aria-label="Remove item for the ${productName}" class="close-button"></a>
						<c:choose>
						<c:when test="${not empty skuId}">
						<img class="compare-image noImageFound" src="${imgPath}" height="${imageSize}" width="${imageSize}" alt="${productName}" title="${productName}"/>
						</c:when>
						<c:otherwise>
						<img class="compare-image noImageFound" src="${imgPath}" height="${imageSize}" width="${imageSize}" alt="${productName}" title="${productName}"/>
						</c:otherwise>
					    </c:choose>
					</div>
					</dsp:oparam>
				</dsp:droplet>
			<c:set var="emptySize" value="${4-size}"/>
			<c:forEach var="i" begin="1" end="${emptySize}">
  			<div class="compare-item empty-item">
				<a href="javascript:void(0);" aria-label="Remove item for the ${productName}" class="close-button"></a>
				<img class="compare-image" src="${imagePath}/_assets/global/images/compare_add_item.png" title="Empty slot for product to compare" alt="Empty slot for product to compare" />
			</div>
			</c:forEach>
		</div>
	<div class="compare-controls">
		<div class="button button_active"> 
		<a href="${contextPath}/compare/product_comparison.jsp" style="height:26px" class="compare-button">
			<bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" />
		</a>
		</div>
		<div class="clear"></div>
		<div class="remove-btn fl cb marTop_10">
			<a href="#" id="remove-product" tabindex='0'><span  class="remove-icon marRight_5" ></span><span><bbbl:label key="lbl_remove_products" language="${pageContext.request.locale.language}"  /></span></a>
		</div>
	</div>
	
</div>
</div>
</dsp:page>