<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  coupon.jsp
 *
 *  DESCRIPTION: page for showing coupons to user
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:getvalueof var="couponMap" bean="ShoppingCart.current.couponMap.empty"/>
        <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
        <dsp:getvalueof var="state" value="${60}"/>
        <dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
        <c:choose>
            <c:when test="${currentState lt state or couponMap}">
            	<c:if test="${currentState eq state}">
            		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="PAYMENT"/>
            	</c:if>
                <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                    <dsp:param name="url" value="${failureURL}"/>
                </dsp:droplet>
            </c:when>
            <c:otherwise>
                <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="COUPONS"/>
            </c:otherwise>
        </c:choose>
    <dsp:getvalueof var="cartEmpty" bean="ShoppingCart.empty"/>
    <c:if test="${cartEmpty}">
        <dsp:droplet name="/atg/dynamo/droplet/Redirect">
            <dsp:param name="url" value="/cart/cart.jsp"/>
        </dsp:droplet>
    </c:if>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
    <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>

    <bbb:pageContainer index="false" follow="false">
        <jsp:attribute name="headerRenderer">
          <dsp:include page="/checkout/checkout_header.jsp" flush="true">
            <dsp:param name="step" value="billing"/>
            <dsp:param name="link" value="coupons"/>
            <dsp:param name="pageId" value="5"/>
          </dsp:include>
        </jsp:attribute>
        <jsp:attribute name="section">checkout</jsp:attribute>
        <jsp:attribute name="pageWrapper">billing billingCoupons</jsp:attribute>
        <jsp:body>
            <dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
                <dsp:param name="order" bean="ShoppingCart.current"/>
            </dsp:droplet>

            <div id="content" class="container_12 clearfix" role="main">
                <div class="marBottom_20">
                    <a id="viewCartContents" class="iconExpand marLeft_10" role="button" href="#" title='<bbbl:label key="lbl_coupon_view_cart" language="${language}"/>' aria-expanded="false" aria-live="assertive" ><bbbl:label key="lbl_coupon_view_cart" language="${language}"/></a>
                </div>
                <div class="clearfix">
                    <div id="leftCol" class="grid_8">
                        <div class="highlightSection grid_8 alpha omega">
                        	<%-- Below if block is for R2 change --%>
                        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>
                        	<c:if test="${(empty errorMap) or (errorMap.size eq 0)}">
						    	<dsp:include page="/global/gadgets/errorMessage.jsp" />
						    </c:if>
                            <dsp:include page="/cart/cartItems.jsp"/>
                        </div>

                        <dsp:include page="/cart/couponDisplay.jsp">
                            <dsp:param name="couponPage" value="COUPONS"/>
                            <dsp:param name="action" value="/store/checkout/coupons/coupon.jsp"/>
                        </dsp:include>

                        <dsp:form formid="checkoutForm" id="checkoutCoupon">
                            <c:set var="nextButton"><bbbl:label key="lbl_continue_to_payment" language="${language}"/></c:set>
                            <div class="button btnNext button_active button_disabled">
                                <dsp:input type="submit" disabled="true" iclass="enableOnDOMReady" bean="CartModifierFormHandler.moveToPayment" value='${nextButton}' >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                            </div>
                        </dsp:form>
                    </div>
                    <dsp:include page="/checkout/order_summary_frag.jsp"/>
                </div>
            </div>

        </jsp:body>

			<jsp:attribute name="footerContent">

				<c:set var="productIds" scope="request"/>
				 <dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					 <dsp:oparam name="output">
						<dsp:getvalueof var="productIds" param="commerceItemList" />
			        </dsp:oparam>
			    </dsp:droplet>

				<script type="text/javascript">
				           if(typeof s !=='undefined') {
				        	   s.pageName = 'Check Out>Coupons';
					   			s.channel = 'Check Out';
					   			s.prop1='Check Out';
					   			s.prop2='Check Out';
					   			s.prop3='Check Out';
					            s.events="event11";
		    					s.products='${productIds}';
					            var s_code=s.t();
					            if(s_code)document.write(s_code);
				           }
				</script>
			</jsp:attribute>
    </bbb:pageContainer>
</dsp:page>
