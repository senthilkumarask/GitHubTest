<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartDeliveryDateDisplayDroplet"/>
	
	<%-- Variables --%>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
		<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="orderDate" param="orderDate"/>
	<dsp:getvalueof var="shipMethod" param="shippingGroup.shippingMethod"/>
	<dsp:getvalueof var="autoWaiveFlag" param="shippingGroup.autoWaiveFlag"/>

	<h3 class="checkout-title">
		Shipping Method
		<c:if test="${not isConfirmation && isMultiShip}">
			<a class="edit-step tiny secondary button" href="#" data-step="shipping">Edit</a>
		</c:if>
		
		<%--<bbbl:label key="lbl_preview_shippingmethod" language="<c:out param='${language}'/>"/>--%>
	</h3>
	<ul class="address">
			<dsp:droplet name="BBBPriceDisplayDroplet">
			  <dsp:param name="shippingMethod" value="${shippingGroup.shippingMethod}" />
              <dsp:param name="priceObject" value="${shippingGroup}" />
              <dsp:param name="orderObject" param="order" />
              <dsp:oparam name="output">
				<dsp:getvalueof var="shippingMethodDesc" param="shippingMethodDescription"/>
				<c:if test="${shippingGroup.shippingGroupClassType eq 'storeShippingGroup' }">
					<dsp:getvalueof var="shippingMethodDesc" value="Store Pick Up"/>
				</c:if>
				
				 <c:if test="${shippingGroup.shippingMethod ne 'LC' and shippingGroup.shippingMethod ne 'LR' and shippingGroup.shippingMethod ne 'LT' and shippingGroup.shippingMethod ne 'LW'}">
                <dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount"/>              	
              	<li class="show-for-print"><dsp:valueof value="${shippingMethodDesc}"/> <fmt:formatNumber value="${totalShippingAmount}"  type="currency"/></li>   
              	</c:if>   	
              </dsp:oparam>
            </dsp:droplet>
		<c:choose>
		<c:when test="${shippingGroup.shippingMethod eq 'SDD'}">
			<li class="orderShipMethodText"><bbbl:label key="lbl_sdd_signature_text" language="<c:out param='${language}'/>"/></li>
		</c:when>
		<c:when test="${shippingGroup.shippingMethod eq 'LC' or shippingGroup.shippingMethod eq 'LR' or shippingGroup.shippingMethod eq 'LT' or shippingGroup.shippingMethod eq 'LW'}">
			<li><dsp:valueof value="${shippingMethodDesc}"/></li>
		</c:when>
		</c:choose>
		
	  <c:if test="${HolidayMessagingOn}">
		 	<dsp:include src="/tbs/common/holidayMessaging.jsp">							 		
		 		<dsp:param name="timeframe" value="${timeframe}"/>
		 		<dsp:param name="currentStep" value="singleShipReview"/>
		 		<dsp:param name="shipMethodDesc" value="${shippingMethodDesc}"/>
		 		<dsp:param name="shipMethod" value="${shipMethod}"/>
		 	</dsp:include>
		</c:if>
		<c:if test="${shipMethod eq '3g' and autoWaiveFlag}">
			(<bbbl:label key="lbl_autowaive_message" language="${pageContext.request.locale.language}" />)
		</c:if>
	</ul>

</dsp:page>
