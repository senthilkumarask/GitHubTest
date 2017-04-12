<dsp:page>

	<dsp:getvalueof id="orderDetails" param="orderDetails" />
	<dsp:getvalueof id="errorStatus" param="orderDetails.status" />
	<dsp:getvalueof id="billing" param="orderDetails.billing" />
	<dsp:getvalueof id="shipping" param="orderDetails.shipping" />
	<dsp:getvalueof id="payments" param="orderDetails.payments" />
	<dsp:getvalueof id="giftPackaging" param="orderDetails.giftPackaging" />
	<dsp:getvalueof id="orderHeaderInfo"
		param="orderDetails.orderInfo.orderHeaderInfo" />
	<dsp:getvalueof id="cartDetailInfo"
		param="orderDetails.orderInfo.cartDetailInfo" />
	<dsp:getvalueof id="shipments" param="orderDetails.shipments" />
	<dsp:getvalueof var="isFromOrderHistory" param="isFromOrderHistory"/>	


	<c:set var="showLinks" scope="request">
		<dsp:valueof param="showLinks" />
	</c:set>
		<div class="grid_8" id="leftCol">
			<div>
				<h1 class="SPCSectionHeadingmarTopNeg_24">
					${fn:length(cartDetailInfo.cartItemDetailList)}
					<bbbl:label key="lbl_spc_preview_itemsshippingto"
						language="<c:out param='${language}'/>" />
					<span>${shipping.address.firstNm} ${shipping.address.lastNm}
					</span>
				</h1>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" value="${shipments}" />
					<dsp:param name="elementName" value="shipment" />
					<dsp:oparam name="output">
						<dsp:getvalueof id="shipment" param="shipment"/>
					</dsp:oparam>
				</dsp:droplet>
				<ul class="shippingItemAddDetails gridItemWrapper">
					<li class="clearfix">
						<div class="grid_5 alpha">
							<p>
								<strong><bbbl:label key="lbl_spc_order_detail_shipping_status" language="${pageContext.request.locale.language}"/></strong>
							</p>
							<p class="marTop_10">${orderDetails.orderInfo.orderHeaderInfo.orderStatus}</p>
						</div>
						<div class="grid_3 omega">
							<p>
								<strong><bbbl:label key="lbl_trackorder_tracking" language="${pageContext.request.locale.language}"/></strong>
							</p>
							<c:set var="orderStatus" value="${fn:toLowerCase(orderDetails.orderInfo.orderHeaderInfo.orderStatus)}"/>
							<c:choose>
								<c:when test="${orderStatus eq 'cancelled' || orderStatus eq 'canceled'}">
								</c:when>
								<c:otherwise>
							<ul class="marTop_10">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" value="${shipments}" />
									<dsp:param name="elementName" value="shipment" />
									<dsp:oparam name="output">
									<li class="clearfix">
									<dsp:getvalueof id="carrierName" param="shipment.carrier"/>
								   <dsp:getvalueof var="imageKey" value="${carrierName}_Image"/>
								   <dsp:getvalueof var="trackKey" value="${carrierName}_Track"/>
<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
    <dsp:param name="configKey" value="ShippingCarriers"/>
    <dsp:oparam name="output">
        <dsp:getvalueof var="carrKey" param="configMap"/>
    </dsp:oparam>
</dsp:droplet>
<c:set var="trackingNumber"><dsp:valueof param="shipment.trackingNumber"/></c:set>
										<img alt="${carrierName}" src="${imagePath}${carrKey[imageKey]}" align="left"/>
										<a href="${carrKey[trackKey]}?id=${trackingNumber}" class="upperCase">${trackingNumber}</a> 
								</li>
								</dsp:oparam>
								</dsp:droplet>
								</ul>
							</c:otherwise>
							</c:choose>
						</div></li>
				</ul>
				<ul class="shippingItemAddDetails marTop_10">
					<li class="clearfix"><dsp:include
							page="/checkout/singlePage/preview/frag/legacy_order_shipping_address.jsp"
							flush="true">
							<dsp:param name="shipping" value="${shipping}" />
							<dsp:param name="orderHeaderInfo" value="${orderHeaderInfo}" />
							<dsp:param name="cartDetailInfo" value="${cartDetailInfo}" />
						</dsp:include> <dsp:include
							page="/checkout/singlePage/preview/frag/legacy_order_gift_wrap.jsp"
							flush="true">
							<dsp:param name="giftPack" value="${giftPackaging}" />
						</dsp:include>
					</li>
				</ul>

			<dsp:include page="/checkout/singlePage/preview/frag/legacy_order_items.jsp"
				flush="true">
				<dsp:param name="orderDetails" value="${orderDetails}" />
			</dsp:include>
		</div>


		</div>

		<%-- <div class="grid_4" id="rightCol">
		<div class="spclSectionBoxGreen">
			<div class="spclSection">
				<div class="bigPromoText">
					<h2>
						<bbbl:label key="lbl_spc_preview_relax"
							language="<c:out param='${language}'/>" />
					</h2>
					<h2>
						<bbbl:label key="lbl_spc_preview_nopressure"
							language="<c:out param='${language}'/>" />
					</h2>
					<h2>
						<strong><bbbl:label
								key="lbl_spc_preview_returnsarepostagepaid"
								language="<c:out param='${language}'/>" />
						</strong>
					</h2>
				</div>
				<p>
					<bbbl:label key="lbl_spc_preview_checkoutinfo"
						language="<c:out param='${language}'/>" />
				</p>
			</div>
		</div>
	</div> --%>
</dsp:page>

