<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>	
	<dsp:getvalueof var="orderDate" param="orderDate"/>
	<dsp:getvalueof var="isPackAndHoldFlag" param="isPackAndHoldFlag"/>
	
	<ul class="SPCdeliveryMethod SPCborderRightPreview">
		<span class="grid_3 SPCpreviewText" style="float:left;">
			<h3 class="subHeading"><bbbl:label key="lbl_spc_preview_shippingmethod" language="<c:out param='${language}'/>"/></h3>
		</span>
		<div class="grid_3 noMarLeft">
		<span class="SPCpreviewShippingText">
			<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
			  <dsp:param name="shippingMethod" value="${shippingGroup.shippingMethod}" />
              <dsp:param name="priceObject" value="${shippingGroup}" />
              <dsp:param name="orderObject" param="order" />
              <dsp:oparam name="output">
				<dsp:getvalueof var="shippingMethodDesc" param="shippingMethodDescription"/>
				<span><dsp:valueof value="${shippingMethodDesc}"/></span>
				 <c:if test="${shippingGroup.shippingMethod ne 'LC' and shippingGroup.shippingMethod ne 'LR' and shippingGroup.shippingMethod ne 'LT' and shippingGroup.shippingMethod ne 'LW'}">
                <dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount"/>              	
              	<span class="marLeft_20"><dsp:valueof value="${totalShippingAmount}" converter="defaultCurrency"/></span>    
              	</c:if>   	
              </dsp:oparam>
            </dsp:droplet>           
		</span>
		<c:if test="${not empty showLinks}">
			<span class="marLeft_5SPCeditPreviewLink">
				<a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcShippingMethods" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/> </strong></a>
			</span>
		</c:if> 
		<c:if test="${isPackAndHoldFlag eq true}">
		<p class="previewTextPackNHold">
				<bbbl:label key="lbl_spc_shipping_pack_hold" language="<c:out param='${language}'/>"/>
		</p>
		</c:if>
		</div>
	</ul>
</dsp:page>