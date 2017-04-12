<dsp:page>
	
        <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
        <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
        <dsp:importbean bean="/atg/commerce/ShoppingCart" />
		<c:set var="firstVisit" scope="session">false</c:set>
        <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
        <dsp:getvalueof var="state" value="${55}"/>
        <dsp:getvalueof param="colg" var="colg" scope="request"/>
        <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
		<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
		<dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.current"/>
		<dsp:getvalueof var="commerceItemCount" value="${order.commerceItemCount}"/>
		<c:if test = "${payPalOrder eq true && isFromPreview eq true}">
    		<dsp:setvalue value="true" bean = "PayPalSessionBean.fromPayPalPreview"/>
    	</c:if>
    	<dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
        <c:choose>
            <c:when test="${currentState lt state or couponMap}">
                <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                    <dsp:param name="url" value="${failureURL}"/>
                </dsp:droplet>
            </c:when>
            <c:otherwise>
                <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_BILLING"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
        	<c:when test="${commerceItemCount gt 0}">
        		<dsp:include page="/checkout/singlePage/billing/billingForm.jsp" />
        	</c:when>
        	<c:otherwise>
        		<dsp:droplet name="/atg/dynamo/droplet/Redirect">
					<dsp:param name="url" value="/cart/cart.jsp" />
				</dsp:droplet>
        	</c:otherwise>
        </c:choose>
		
</dsp:page>