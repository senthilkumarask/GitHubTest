<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  progressBar.jsp
 *
 *  DESCRIPTION: Progress bar for checkout pages
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:getvalueof var="step" param="step"/>
   <dsp:include page="checkoutLogo.jsp" />
    <div class="marRight_10 fr">
        <dsp:a page="/cart/cart.jsp" iclass="returnToCart fl"><bbbl:label key="lbl_spc_shipping_return_cart" language="${pageContext.request.locale.language}" /></dsp:a>
        <c:choose>
            <c:when test="${step == 'shipping' or step == 'gifting'}">
                <c:set var="shipping" value="active" scope="request" />
            </c:when>
             <c:when test="${step == 'billing'}">
                <c:set var="billing" value="active" scope="request" />
            </c:when>
            <c:when test="${step == 'payment'}">
                <c:set var="payment" value="active" scope="request" />
            </c:when>
            <c:when test="${step == 'preview'}">
                <c:set var="preview" value="active" scope="request" />
            </c:when>
        </c:choose>
    
        <div id="steps" class="fl">
            <a class="first ${shipping}"><span>1</span><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" /></a>
            <a class="${billing}"><span>2</span><bbbl:label key="lbl_spc_bread_crumb_billing" language="${pageContext.request.locale.language}" /></a>
            <a class="${payment}"><span>3</span><bbbl:label key="lbl_spc_bread_crumb_payment" language="${pageContext.request.locale.language}" /></a>
            <a class="${preview}"><span>4</span><bbbl:label key="lbl_spc_bread_crumb_preview" language="${pageContext.request.locale.language}" /></a>
        </div>
    </div>
</dsp:page>