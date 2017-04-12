<dsp:page>
	<dsp:getvalueof id="orderDetails" param="orderDetails" />
	<dsp:getvalueof id="errorStatus" param="orderDetails.status" />
	<dsp:getvalueof id="billing" param="orderDetails.billing" />
	<dsp:getvalueof id="shipping" param="orderDetails.shipping" />
	<dsp:getvalueof id="payments" param="orderDetails.payments" />
	<dsp:getvalueof id="giftPackaging" param="orderDetails.giftPackaging" />
	<dsp:getvalueof id="orderHeaderInfo" param="orderDetails.orderInfo.orderHeaderInfo" />
	<dsp:getvalueof id="cartDetailInfo" param="orderDetails.orderInfo.cartDetailInfo" />
	<dsp:getvalueof id="shipments" param="orderDetails.shipments" />
	<dsp:getvalueof id="fromOrderSummary" param="fromOrderSummary" />
	<dsp:getvalueof id="orderId" param="orderId" />
	<div class="grid_9 alpha omega clearfix">
		<div class="grid_3 alpha">
			<div class="detail">
				<dl class="clearfix">
					<dt class="fl"><bbbl:label key="lbl_trackorder_shipping_method" language='${pageContext.request.locale.language}'/>&nbsp;</dt>
					<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
						<dsp:param name="priceObject" value="${shipping}" />
						<dsp:param name="shippingMethod" value="${shipping.shipMethodCD}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="shippingMethodDesc" param="shippingMethodDescription" scope="page"/>
						</dsp:oparam>
					</dsp:droplet>
					<dd class="fl">${shippingMethodDesc}</dd>
				</dl>
				<%-- Start trackingNumber --%>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" value="${shipments}" />
					<dsp:param name="elementName" value="shipment" />
					<dsp:oparam name="output">
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
						<dsp:getvalueof var="carrierTrackUrl" value="${carrKey[trackKey]}"/>
						<dl class="clearfix">
							<dt class="fl"><bbbl:label key="lbl_trackorder_tracking" language='${pageContext.request.locale.language}'/>&nbsp;#:&nbsp;</dt>
							<c:choose>
								<c:when test="${carrierTrackUrl  ne null && not empty carrierTrackUrl}">
									<dd class="fl tracking-id">
										<a href="${carrKey[trackKey]}${trackingNumber}" class="upperCase">
										<img alt="${carrierName}" src="${imagePath}${carrKey[imageKey]}" align="left"/>${trackingNumber}</a>
									</dd>
								</c:when>
								<c:otherwise>
									<dd class="fl tracking-id">${trackingNumber}</li></dd>
								</c:otherwise>
							</c:choose>
						</dl>	
					</dsp:oparam>
				</dsp:droplet>
				<%-- End tracking Number --%>
				<dl class="clearfix">
					<dl class="clearfix">
						<dt class="fl"><bbbl:label key="lbl_preview_giftoptions" language="<c:out param='${language}'/>"/>:&nbsp;</dt>
						<c:choose>
							<c:when test="${giftPackaging.giftMessage}">
								<dd class="fl tracking-id see-gift">
									<a href="javascript:void(0);" rel="GM${shipmentCount}"><bbbl:label key="lbl_trackorder_gift_message_link" language="<c:out param='${language}'/>"/></a>
									<div class="width_4 gift_message hidden" id="GM${shipmentCount}">							
										<span class="arrow">&nbsp;</span>
										<a href="javascript:void(0);" class="close"></a>
										<h3><bbbl:label key="lbl_trackorder_gift_message" language="<c:out param='${language}'/>"/></h3>
										<p><c:out value='${giftPackaging.giftMessage}'/></p>
									</div>
								</dd>
							</c:when>
							<c:otherwise>
								<dd class="fl"><bbbl:label key="lbl_trackorder_gift_none" language="<c:out param='${language}'/>"/></dd>
							</c:otherwise>
						</c:choose>
					</dl>
				</dl>
			</div>
			<div class="detail">
				<dl>
					<dt><bbbl:label key="lbl_trackorder_ship_head" language="<c:out param='${language}'/>"/></dt>
					<c:choose>
						<c:when test="${empty shipping.address.firstNm}">
							<dd>
						     	<bbbl:label key="lbl_trackorder_ship_regAddress" language='${pageContext.request.locale.language}'/> ${cartDetailInfo.registrantName} 
						     	<c:if test="${cartDetailInfo.coRegistrantName ne null}">
			                        &nbsp;&amp; ${cartDetailInfo.coRegistrantName}
		                    	</c:if> 
							</dd>
						</c:when>
						<c:otherwise>
							<dd>${shipping.address.firstNm}&nbsp;${shipping.address.lastNm}</dd>
							<c:if test="${shipping.address.company != ''}">
									 <dd>${shipping.address.company}<dd>
							</c:if>
							<dd>${shipping.address.addr1}</dd>
							<c:if test="${shipping.address.addr2 != ''}">
								<dd>${shipping.address.addr2}</dd>
							</c:if>
							<dd>${shipping.address.city},&nbsp;${shipping.address.state}&nbsp;${shipping.address.zip}</dd>
						</c:otherwise>				
					</c:choose>
				</dl>							
			</div>
			<c:if test="${fromOrderSummary}">
			<div class="detail clearfix">		
				<c:set var="orderDetailsURL" value="legacy_order_detail.jsp?orderId=${orderId}" />
				<c:set var="lbl_orderhistory_vieworder" ><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></c:set>
				<div class="button button_active button_light_blue">
					<a href="${orderDetailsURL}" title="${lbl_orderhistory_vieworder}"><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></a>
				</div> 
		   </div>
		   </c:if>
		</div>
		<dsp:include page="track_order_items_legacy.jsp">
			<dsp:param name="cartDetailInfo" value="${cartDetailInfo}"/>
		</dsp:include>
	</div>		
</dsp:page>