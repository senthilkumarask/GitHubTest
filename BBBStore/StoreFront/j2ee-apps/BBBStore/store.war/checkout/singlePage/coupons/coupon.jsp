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
    <dsp:getvalueof var="state" value="${65}"/>
    
    <%--
        <c:choose>
            <c:when test="${currentState lt state or couponMap}">
            	<c:if test="${currentState eq state}">
            		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_PAYMENT"/>
            	</c:if>
                <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                    <dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
                </dsp:droplet>
            </c:when>
            <c:otherwise>
                <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_COUPONS"/>
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

        
    
        <jsp:attribute name="headerRenderer">
          <dsp:include page="/checkout/checkout_header.jsp" flush="true">
            <dsp:param name="step" value="billing"/>
            <dsp:param name="link" value="coupons"/>
            <dsp:param name="pageId" value="5"/>
          </dsp:include>
        </jsp:attribute>
        <jsp:attribute name="section">checkout</jsp:attribute>
        <jsp:attribute name="pageWrapper">billing billingCoupons</jsp:attribute>
        
        --%>

            <dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
                <dsp:param name="order" bean="ShoppingCart.current"/>
            </dsp:droplet>

            <div id="SpcCouponContent" class="clearfix" role="main">

                

              <%--  <div class="marBottom_20">
                    <a id="viewCartContents" class="iconExpand marLeft_10" role="button" href="#" title='<bbbl:label key="lbl_spc_coupon_view_cart" language="${language}"/>'><bbbl:label key="lbl_spc_coupon_view_cart" language="${language}"/></a>
                </div> --%>
                
                <%--    <div id="leftCol" class="grid_8 alpha">
                        <div class="highlightSection grid_8 alpha omega">
                        	
                        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>
                        	<c:if test="${(empty errorMap) or (errorMap.size eq 0)}">
						    	<dsp:include page="/global/gadgets/errorMessage.jsp" />
						    </c:if>
                            <dsp:include page="/cart/cartItems.jsp"/>
                        </div> --%>




                        <dsp:include page="/cart/spcCouponDisplay.jsp">
                            <dsp:param name="couponPage" value="SP_COUPONS"/>
                            <dsp:param name="action" value="/store/checkout/singlePage/coupons/coupon.jsp"/>
                        </dsp:include>


            </div>


        


            <%--
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
            --%>
    
</dsp:page>
