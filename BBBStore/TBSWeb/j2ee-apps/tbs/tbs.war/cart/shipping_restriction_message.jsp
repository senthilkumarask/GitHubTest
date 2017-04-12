<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet" />
	<dsp:getvalueof var="pageType" param="pageType" />
	<dsp:droplet name="IsProductSKUShippingDroplet">
		<dsp:param name="skuId" param="skuId" />
		<dsp:param name="prodId" param="productId" />
		<dsp:param name="siteId" bean="/atg/multisite/Site.id" />
		<dsp:oparam name="true">
		<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>																
		<c:forEach var="item" items="${restrictedAttributes}">
	
		<c:choose>
		<c:when test="${null ne item.actionURL}">
		 <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
		</c:when>
		<c:otherwise>
		${item.attributeDescrip}
		</c:otherwise>
		</c:choose>	
		</c:forEach>
		</dsp:oparam>
	
		<dsp:oparam name="false">
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>