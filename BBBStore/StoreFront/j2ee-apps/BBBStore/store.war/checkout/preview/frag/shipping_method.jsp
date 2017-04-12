<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>	
	<dsp:getvalueof var="orderDate" param="orderDate"/>	
	<dsp:getvalueof var="isPackAndHoldFlag" param="isPackAndHoldFlag"/>
	
	<div class="deliveryMethod borderRightPreview">
		<p class="previewText">
			<bbbl:label key="lbl_preview_shippingmethod" language="<c:out param='${language}'/>"/>
		</p>
		<c:if test="${not empty showLinks}">
			<p class="marTop_10 editPreviewLink">
				<a class="upperCase" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp?isFromPreview=true" title="<bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/>"><span class="txtOffScreen">Edit Shipping And Delivery Method</span><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/> </strong></a>
			</p>
		</c:if>
		<dl class="noMar marTop_5 previewShippingText">
			<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
			  <dsp:param name="shippingMethod" value="${shippingGroup.shippingMethod}" />
              <dsp:param name="priceObject" value="${shippingGroup}" />
              <dsp:param name="orderObject" param="order" />
              <dsp:oparam name="output">
				<dsp:getvalueof var="shippingMethodDesc" param="shippingMethodDescription"/>
				<dt><dsp:valueof value="${shippingMethodDesc}"/></dt>
				 <c:if test="${shippingGroup.shippingMethod ne 'LC' and shippingGroup.shippingMethod ne 'LR' and shippingGroup.shippingMethod ne 'LT' and shippingGroup.shippingMethod ne 'LW'}">
                <dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount"/>              	
              	<dd><dsp:valueof value="${totalShippingAmount}" converter="defaultCurrency"/></dd>    
              	</c:if>   	
              </dsp:oparam>
            </dsp:droplet>
            <c:if test="${isPackAndHoldFlag eq true}">
					<p class="previewText_PackNHold">
						<bbbl:label key="lbl_spc_shipping_pack_hold" language="<c:out param='${language}'/>"/>
					</p>
			</c:if>
		</dl>
		
	</div>
</dsp:page>