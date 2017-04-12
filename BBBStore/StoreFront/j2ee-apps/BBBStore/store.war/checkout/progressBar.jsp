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
        <dsp:a page="/cart/cart.jsp" iclass="returnToCart fl"><bbbl:label key="lbl_shipping_return_cart" language="${pageContext.request.locale.language}" /></dsp:a>
        <c:choose>
            <c:when test="${step == 'shipping' or step == 'gifting'}">
                <c:set var="shipping" value="active" scope="request" />
                <c:set var="activeShipping" value="activeStep" scope="request" />
                <c:set var="progressShipping" value=". In Progress" scope="request" />
                <c:set var="shippingStepNum" value="1" scope="request" />
                <c:set var="billingStepNum" value="2" scope="request" />
                <c:set var="paymentStepNum" value="3" scope="request" />
                <c:set var="previewStepNum" value="4" scope="request" />
            </c:when>
             <c:when test="${step == 'billing'}">
                <c:set var="billing" value="active" scope="request" />
                <c:set var="activeBilling" value="activeStep" scope="request" />
                <c:set var="progressBilling" value=". In Progress" scope="request" />
                <c:set var="billingtick" value="icon icon-checkmark" scope="request" />
                <c:set var="billingStepNum" value="2" scope="request" />
                <c:set var="paymentStepNum" value="3" scope="request" />
                <c:set var="previewStepNum" value="4" scope="request" />
                <c:set var="billingComplete" value="completed" scope="request" />
            </c:when>
            <c:when test="${step == 'payment'}">
                <c:set var="payment" value="active" scope="request" />
                <c:set var="activePayment" value="activeStep" scope="request" />
                <c:set var="progressPayment" value=". In Progress" scope="request" />
                <c:set var="paymenttick" value="icon icon-checkmark" scope="request" />
                <c:set var="paymentStepNum" value="3" scope="request" />
                <c:set var="previewStepNum" value="4" scope="request" />
                <c:set var="paymentComplete" value="completed" scope="request" />
            </c:when>
            <c:when test="${step == 'preview'}">
                <c:set var="preview" value="active" scope="request" />
                <c:set var="activePreview" value="activeStep" scope="request" />
                <c:set var="progressPreview" value=". In Progress" scope="request" />
                <c:set var="previewtick" value="icon icon-checkmark" scope="request" />
                <c:set var="previewStepNum" value="4" scope="request" />
                <c:set var="previewComplete" value="completed" scope="request" />
            </c:when>
           
        </c:choose>


        <div id="steps" class="fl 3">
            <a  href='#' class="first ${shipping}" role="text"><span aria-label="Step 1 <bbbl:label key="lbl_bread_crumb_shipping" language="${pageContext.request.locale.language}" /> ${paymentComplete} ${billingComplete} ${previewComplete} ${progressShipping}" class="progressStepNumber ${activeShipping} ${billingtick} ${paymenttick} ${previewtick}">${shippingStepNum}</span><span aria-hidden="true"><bbbl:label key="lbl_bread_crumb_shipping" language="${pageContext.request.locale.language}" /></span> </a>

            <a  href='#' class="${billing}" role="text"><span aria-label="Step 2 <bbbl:label key="lbl_bread_crumb_billing" language="${pageContext.request.locale.language}" /> ${paymentComplete} ${previewComplete} ${progressBilling}" class="progressStepNumber ${activeBilling} ${paymenttick} ${previewtick}">${billingStepNum}</span><span  aria-hidden="true"><bbbl:label key="lbl_bread_crumb_billing" language="${pageContext.request.locale.language}" /></span></a>

            <a  href='#' class="${payment}" role="text"><span aria-label="Step 3 <bbbl:label key="lbl_bread_crumb_payment" language="${pageContext.request.locale.language}" /> ${previewComplete} ${progressPayment}" class="progressStepNumber ${activePayment} ${previewtick}">${paymentStepNum}</span><span aria-hidden="true"><bbbl:label key="lbl_bread_crumb_payment" language="${pageContext.request.locale.language}" /></span></a>

            <a  href='#' class="${preview}" role="text"><span aria-label="Step 4 <bbbl:label key="lbl_bread_crumb_preview" language="${pageContext.request.locale.language}" />  ${progressPreview}" class="progressStepNumber ${activePreview}">${previewStepNum}</span><span aria-hidden="true"><bbbl:label key="lbl_bread_crumb_preview" language="${pageContext.request.locale.language}" /></span></a>
        </div>
    </div>
</dsp:page>