<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<dsp:getvalueof param="cItem" var="cItem" />
	<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
	<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
	<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="priceInfoVO.priceBeans" />
		<dsp:param name="elementName" value="unitPriceBean"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="unitPriceBean.pricingModels" var="promos"/>
			<dsp:getvalueof param="unitPriceBean.quantity" var="promoqty"/>
			<c:if test="${not empty promos}">
				<dsp:valueof param="unitPriceBean.quantity"/> &nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/>
				
				<div class="prodDeliveryInfo pricingModels noMar">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="unitPriceBean.pricingModels" />
						<dsp:param name="elementName" value="pricingModel" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="appliedPromoId" param="pricingModel.id"/>
							<c:if test="${not empty appliedPromoId}">
								<dsp:getvalueof var="promoId" value="${appliedPromoId}"/>
								<span>
									<strong>
										<dsp:valueof param="pricingModel.displayName"/>
									</strong>
									<a href="${contextPath}/static/shippingexclusionsinclusions" class="popup">
										<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>
									</a>
								</span>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	<c:choose>
		<c:when test="${undiscountedItemsCount gt 0 && unitSalePrice gt 0}">
			<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<fmt:formatNumber value="${unitSalePrice}"  type="currency"/><br/>
		</c:when>
		<c:when test="${undiscountedItemsCount gt 0 && unitListPrice gt 0}">
			<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<fmt:formatNumber value="${unitListPrice}"  type="currency"/><br/>
		</c:when>
	</c:choose>

	
</dsp:page>
