<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  shipping.jsp
 *
 *  DESCRIPTION: common page for shipping address pages, renders single or multi address page based on the conditions
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" var="shoppingcart" />
<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="isGuestCheckout" param="guestCheckout"/>
<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
<dsp:getvalueof var="order" vartype="java.lang.Object" bean="ShoppingCart.current"/>
<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
<dsp:getvalueof var= "fromPayPal" bean = "PayPalSessionBean.address.fromPayPal"/>
<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
<dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
<dsp:getvalueof var="formExceptions" bean="BBBSPShippingGroupFormhandler.formExceptions"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
<c:set var="anonymousLogin">${valueMap.anonymousLogin}</c:set>

<c:if test="${shoppingcart.currentEmpty eq 'true'}">
	<dsp:droplet name="/atg/dynamo/droplet/Redirect">
	   <dsp:param name="url" value="/store/cart/cart.jsp"/>
	 </dsp:droplet>
</c:if>
<c:if test="${anonymousLogin eq 'true'}">
	<dsp:setvalue value="${null}" bean="SessionBean.values.anonymousLogin" />
</c:if>
<c:set var="pageNameFB" value="login" scope="request" />

<bbb:pageContainer index="false" follow="false" >
    <jsp:attribute name="headerRenderer">
      <dsp:include page="/checkout/singlePage/checkout_header.jsp">
        <dsp:param name="step" value="shipping"/>
        <dsp:param name="link" value="single"/>
        <dsp:param name="pageId" value="6"/>
      </dsp:include>
    </jsp:attribute>
   
    <jsp:attribute name="section">singleCheckout</jsp:attribute>
    <jsp:attribute name="pageWrapper">billing shippingWrapper singleShippingPage  billingCoupons billingPayment useFB useGoogleAddress</jsp:attribute>
    <jsp:attribute name="PageType">SingleShipping</jsp:attribute>

    <jsp:body>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
				<c:set var="jSessionId">${pageContext.session.id}</c:set>
					<c:if test="${sessionIdForSPC ne jSessionId && anonymousLogin ne 'true'}">
					<c:set var="pageLocation" value="/cart/cart.jsp" />	
					<script type="text/javascript">
					window.location = "${contextPath}${pageLocation}";
					</script>
 				</c:if>
    <c:choose>
        <c:when test="${empty formExceptions}">
           <c:set var="isFormException" value="false"/>
        </c:when>
        <c:otherwise>
            <c:set var="isFormException" value="true"/>
        </c:otherwise>
    </c:choose>
            		
    <c:set var="size" value="${fn:length(errorList)}"/>  

    <c:choose>
    	<c:when test="${size gt 0}">
    		<c:set var="paypalError" value="true"/>
    	</c:when>
    	<c:otherwise>
    		<c:set var="paypalError" value="false"/>
    	</c:otherwise>
    </c:choose>
	
	
    <c:set var="isFromPreview" value="${param.isFromPreview}"/>
	<c:if test = "${payPalOrder eq true && isFromPreview eq true}">
    	<dsp:setvalue value="true" bean = "PayPalSessionBean.fromPayPalPreview"/>
    </c:if>
	<%--<c:if test="${orderPriceInfo == null}"> 
		<dsp:droplet name="RepriceOrderDroplet">
			<dsp:param value="ORDER_SUBTOTAL" name="pricingOp"/>
		</dsp:droplet>
	</c:if> --%>

        <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_CHECKOUT_SINGLE"/>


	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="state" value="${95}"/>
    <dsp:getvalueof var="url" bean="CheckoutProgressStates.failureURL"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="failureURL" value="${contextPath}${url}"/>
    <c:if test="${currentState lt state}">
        <dsp:droplet name="/atg/dynamo/droplet/Redirect">
            <dsp:param name="url" value="${failureURL}"/>
        </dsp:droplet>
    </c:if>
        <c:choose>
        <c:when test="${currentState lt state or couponMap}">
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" value="${failureURL}"/>
            </dsp:droplet>
        </c:when>
		
    </c:choose>

    <div id="content" class="clearfix" role="main">
        <div class="container_12 clearfix">
            <div class="grid_12  checkoutContent">
                <div class="grid_8 alpha">
					<%--R2.2 PayPal Change: Display error message in case of webservice error : Start--%>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param bean="CartModifierFormHandler.errorMap" name="array" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="errorCode" param="key" />
								<dsp:getvalueof var="errorMsg" bean="CartModifierFormHandler.errorMap.${errorCode}"/>
							</dsp:oparam>
					</dsp:droplet>
					<div class="error">${errorMsg}</div>
					<%--End paypal error --%>
					

                    <dsp:getvalueof var="shippingAdd1" bean="ShoppingCart.current.shippingAddress.address1" />
                    <c:set var="shippingSubmitted" value="${not empty shippingAdd1}"></c:set>
                    <dsp:getvalueof var="billingAdd1" bean="ShoppingCart.current.billingAddress.address1" />
                    <c:set var="billingSubmitted" value="${not empty billingAdd1}" scope="request"></c:set>

                    <%-- debugging 
                                    
                    shippingSubmitted: ${shippingSubmitted} <dsp:valueof bean="ShoppingCart.current.shippingAddress.address1" />  
                     <dsp:valueof bean="ShoppingCart.current.shippingAddress.id" />   <br>
                    billingSubmitted: ${billingSubmitted} <dsp:valueof bean="ShoppingCart.current.billingAddress.address1" />    
                    --%>

                    <div id="shippingWrapper">
                        
                            <dsp:include page="/checkout/singlePage/shipping/singleShipping.jsp">
                            
                                <dsp:param name="shippingSubmitted" value="${shippingSubmitted}"/>
                                <dsp:param name="isFromPreview" value="${isFromPreview}"/>
                                <dsp:param name="payPalOrder" value="${payPalOrder}"/>
                            </dsp:include>
                        
                    </div>
                    

                    <c:if test="${CouponOn}" >
                        <a name="spcCoupons"></a>
                        <div id="SpcCouponContent" class="clearfix" role="main">
                        
        				        <dsp:include page="/checkout/singlePage/coupons/coupon.jsp" />
                        
        				</div>
                    </c:if>
                    
                    <div id="billingSection" class="clearfix" >
                        
                            <dsp:include page="/checkout/singlePage/billing/billing.jsp" />
                        
                    </div>

                    <a name="spcPayment"></a>
        			<a name="spcCreditCard"></a>
        			<a name="spcGiftCard"></a>
                    <div id="paymentWrapper">
                        
                        <dsp:include page="/checkout/singlePage/payment/billing_payment.jsp" />
                        
                    </div>
                    <%-- <dsp:include page="/checkout/singlePage/payment/paymentMethod.jsp" /> --%>


                    <dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
                </div>

                <div class="grid_4 omega start" id="orderSummary">
                    
                        <dsp:include page="/checkout/singlePage/order_summary_frag.jsp">
                            <dsp:param name="displayTax" value="${isFromPreview}"/>
                            <dsp:param name="displayShippingDisclaimer" value="true"/>
                        </dsp:include>
                    
                </div>
            </div>
        </div>
		 <a name="spcBilling"></a>
        <dsp:include page="/checkout/singlePage/orderActions.jsp"/>
    </div>

    <div id="currCartProduct" data-omniproductIds="${productIds}" class="hidden"></div>	
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
                        s.pageName='Check Out > One page checkout';
                        s.channel='Check Out';
                        s.prop1='Check Out';
                        s.prop2='Check Out';
                        s.prop3='Check Out';
                        s.prop6='${pageContext.request.serverName}'; 
                        s.eVar9='${pageContext.request.serverName}';
                        s.events="scCheckout";
                        s.products='${productIds}';                    
                        var s_code=s.t();
                        if(s_code)document.write(s_code);           
                       }
            </script>
 

            <%-- YourAmigo code starts  6/18/2013--%>
            <%-- turning this off - bp --%>
    		<c:if test="${YourAmigoON}">
                <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
    		    <c:if test="${isTransient eq false}">
                	<%-- ######################################################################### --%>
                	<%--  Configuring the javascript for tracking signups (to be placed on the     --%>
                	<%--  signup confirmation page, if any).                                       --%>
                	<%-- ######################################################################### --%>
                    <c:choose>
                        <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
                            <script src="https://support.youramigo.com/52657396/tracev2.js"></script>
                            <c:set var="ya_cust" value="52657396"></c:set>
                        </c:when>
                        <c:when test="${(currentSiteId eq BedBathUSSite)}">
                            <script src="https://support.youramigo.com/73053126/trace.js"></script>
                            <c:set var="ya_cust" value="73053126"></c:set>
                        </c:when>
						<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
                            <script src="https://support.youramigo.com/73053127/tracev2.js"></script>
                            <c:set var="ya_cust" value="73053127"></c:set>
                        </c:when>
                    </c:choose>
        	
                	<script type="text/javascript">
                	/* <![CDATA[ */
                	
                	    /*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/
                		  
                		// --- begin customer configurable section ---
                		
                		ya_tid = Math.floor(Math.random()*1000000);	// Set XXXXX to the ID counting the signup, or to a random
                	                          // value if you have no such id - eg,
                	                          // ya_tid = Math.random();
                		ya_pid = ""; // Set YYYYY to the type of signup - can be blank
                	                          // if you have only one signup type.
                	
                		ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
                		// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---
                		
                		ya_cust = '${ya_cust}';
                		try { yaConvert(); } catch(e) {}
                	
                	/* ]]> */
                	</script>
                </c:if>
            </c:if>
        </jsp:attribute>

<%--R2.2 SCOPE #158 START deviceFingerprint JS call to cybersource--%>
    <c:if test="${deviceFingerprintOn}">  
		<c:set var="merchandId"><bbbc:config key="DF_merchandId" configName="ThirdPartyURLs"/></c:set>
		<c:set var="orgId"><bbbc:config key="DF_orgId" configName="ThirdPartyURLs"/></c:set>
		<c:set var="jSessionId">${pageContext.session.id}</c:set>
		<c:set var="jSession_id" value="${fn:split(jSessionId, '!')}" />
		<c:set var="org_jsessionid">${jSession_id[0]}</c:set>
        <div style="position:absolute; top:-1000px; overflow:hidden; height:1px; width:1px;">
		<div style="background:url(<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}&m=1)"></div> 
		<img src="<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}&m=2" alt="device finger-print pixel" /> 
		<object type="application/x-shockwave-flash" data="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}" width="1" height="1"> 
			<param name="movie" value="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}" /> 
			<param name="wmode" value="transparent" /> 
		<div></div>
		</object>
		</div>
		<script src="<bbbc:config key="deviceFinger_js" configName="ThirdPartyURLs"/>?org_id=${orgId}&session_id=${merchandId}${org_jsessionid}"></script> 
	</c:if>
<%--R2.2 SCOPE #158 END deviceFingerprint JS call to cybersource--%>

  </bbb:pageContainer>  
</dsp:page>


