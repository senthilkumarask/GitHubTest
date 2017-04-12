<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  404.jsp
 *
 *  DESCRIPTION: page for error page
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <bbb:pageContainer index="false" follow="false" >
      <jsp:attribute name="SEOTagRenderer">
      <dsp:include page="/global/gadgets/metaDetails.jsp" flush="true" />
    </jsp:attribute>

    <jsp:attribute name="section">cms</jsp:attribute>
    <jsp:attribute name="pageWrapper">errorPages 404</jsp:attribute>
    <%-- <jsp:attribute name="footerContent">
            * Commenting the following code to implement omniture for errors thrown either by paypal or credit card payment page
           <!-- <script type="text/javascript">
           if(typeof s !=='undefined') {
            s.pageName='error code cybersource error: ' + document.location.href;
            s.pageType='errorPage';
            var s_code=s.t();
            if(s_code)document.write(s_code);
           }
        </script> -->
    </jsp:attribute> --%>

    <jsp:body>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="BILLING"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <script type="text/javascript">var BBB = BBB || {}; BBB.reasonFailure = "";</script>

    <dsp:param name="order" bean="ShoppingCart.current"/>
    <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" param="order.paymentGroups"/>
        <dsp:param name="elementName" value="paymentGroup" />
        <dsp:oparam name="output">
            <dsp:droplet name="Switch">
                <dsp:param name="value" param="paymentGroup.paymentMethod"/>
                <dsp:oparam name="paypal">
                <c:set var="isPayPal" value="${true}"/>
                </dsp:oparam>
                <dsp:oparam name="creditCard">
                <c:set var="isCreditCard" value="${true}"/>
                </dsp:oparam>
            </dsp:droplet>
        </dsp:oparam>
    </dsp:droplet>

    <div id="content" class="container_12 clearfix">
    <dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
      <dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
    </dsp:droplet>

    <div id="cmsPageContent">
      <div class="grid_12">
        <dsp:getvalueof param="cybersourceAVSFail" var="cybersourceAVSFail"/>
        <dsp:getvalueof param="cybersourceInvalidCVV" var="cybersourceInvalidCVV"/>
        <%---    if from paypal error R2.2--%>
        <dsp:getvalueof param="paypalFailOne" var="paypalFailOne"/>
        <dsp:getvalueof param="paypalFailTwo" var="paypalFailTwo"/>
        <dsp:getvalueof param="paypalError" var="paypalError"/>
     <c:if test="${payPalOrder and (not empty paypalFailOne or paypalError)}">
        <dsp:droplet name="/com/bbb/commerce/order/paypal/RemovePayPalInfoDroplet">
        </dsp:droplet>
    </c:if>
        <c:choose>
             <c:when test="${not empty cybersourceAVSFail}">
                <script type="text/javascript">BBB.reasonFailure = "cybersource error";</script>
               <span class="error"><bbbt:textArea key="txt_payment_csauth_avs_failure" language="${pageContext.request.locale.language}"/></span>
             </c:when>
             <c:when test="${not empty cybersourceInvalidCVV}">
                <script type="text/javascript">BBB.reasonFailure = "cybersource error";</script>
               <span class="error"><bbbt:textArea key="txt_payment_csauth_cvv_failure" language="${pageContext.request.locale.language}"/></span>
             </c:when>
             <%---    if from paypal error R2.2--%>
             <c:when test="${not empty paypalFailOne}">
                <script type="text/javascript">BBB.reasonFailure = "paypal authorization error";</script>
                  <div class="payPalError errorPayPalFailure error">
                  <bbbt:textArea key="txt_payment_auth_paypal_failure_one" language="${pageContext.request.locale.language}"/>
                    <dsp:a href=""> <dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8"/>
                <dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" priority="9"/>
                <dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/> PayPal </dsp:a>
                    <bbbt:textArea key="txt_payment_auth_paypal_failure_two" language="${pageContext.request.locale.language}"/>.
                </div>
             </c:when>
               <c:when test="${payPalOrder and paypalError}">
                <script type="text/javascript">BBB.reasonFailure = "paypal authorization error";</script>
                  <div class="payPalError errorPayPalFailure error">
             <bbbt:textArea key="txt_payment_paypal_error" language="${pageContext.request.locale.language}"/></div> </div>
                </div>
             </c:when>
           <%--   <c:when test="${empty paypalFailTwo && empty paypalFailOne && isPayPal }">
                <script type="text/javascript">BBB.reasonFailure = "paypal authorization error";</script>
                  <div class="payPalError errorPayPalFailure error">
                  <bbbt:textArea key="txt_payment_auth_paypal_failure_one" language="${pageContext.request.locale.language}"/>
                    <dsp:a href=""> <dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8"/>
                <dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" priority="9"/>
                <dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/> PayPal </dsp:a>
                    <bbbt:textArea key="txt_payment_auth_paypal_failure_two" language="${pageContext.request.locale.language}"/>.
                </div>
             </c:when> --%>
             <c:otherwise>
                <script type="text/javascript">BBB.reasonFailure = "cybersource error";</script>
                 <span class="error"><bbbt:textArea key="txt_payment_csauth_failure" language="${pageContext.request.locale.language}"/></span>
             </c:otherwise>
        </c:choose>
          </div>
     </div>
      </div>

    </jsp:body>
            <jsp:attribute name="footerContent">
               <script type="text/javascript">
                   if(typeof s !=='undefined') {
                    s.pageName='error code ' + BBB.reasonFailure + ': ' + document.location.href;
                    s.pageType='errorPage';
                    s.channel = s.prop1 = s.prop2 = s.prop3 = "Error Page";
                    var s_code=s.t();
                    if(s_code)document.write(s_code);
                   }
                </script>
        </jsp:attribute>

  </bbb:pageContainer>
</dsp:page>