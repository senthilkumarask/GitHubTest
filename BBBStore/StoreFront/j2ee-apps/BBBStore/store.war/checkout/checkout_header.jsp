<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  checkout_header.jsp
 *
 *  DESCRIPTION: header for checkout pages
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
        
    <div id="header" class="container_12 clearfix">
        <dsp:include page="/checkout/progressBar.jsp">
            <dsp:param name="step" param="step"/>
        </dsp:include>
        <c:choose>
            <c:when test="${step == 'shipping'}">
                <dsp:include page="/checkout/shipping/shippingTitle.jsp">
                    <dsp:param name="link" param="link"/>
                    <dsp:param name="isFromPreview" param="isFromPreview"/>
                    <dsp:param name="paypalShipping" param="paypalShipping"/>
                </dsp:include>
            </c:when>
            <c:when test="${step == 'gifting'}">
                <dsp:include page="/checkout/gifting/giftingTitle.jsp">
                </dsp:include>
            </c:when>
            <c:when test="${step == 'payment'}">
            	<dsp:include page="/checkout/payment/paymentTitle.jsp">
                    <dsp:param name="link" param="link"/>
                </dsp:include>
            </c:when>
            <c:when test="${step == 'billing'}">
                 <c:choose>
                    <c:when test="${link == 'coupons'}">
                        <dsp:include page="/checkout/coupons/couponTitle.jsp"/>
                    </c:when>
                    <c:otherwise>
                        <dsp:include page="/checkout/billing/billingTitle.jsp">
                            <dsp:param name="link" param="link"/>
                        </dsp:include>    
                    </c:otherwise>
                </c:choose>            	
            </c:when>
            <c:when test="${step == 'preview'}">
           		<dsp:include page="/checkout/preview/previewTitle.jsp">
                </dsp:include>
            </c:when>
       </c:choose>
       <dsp:include page="/common/click2chatlink.jsp">
            <dsp:param name="pageId" param="pageId"/>
        </dsp:include>
    </div>
</dsp:page>