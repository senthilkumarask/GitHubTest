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
<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <%--<dsp:include page="/_includes/third_party_on_of_tags.jsp"/> --%>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
    <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
        
    <div id="header" class="container_12 clearfix">

        <div class="grid_12 headerContent clearfix">
            <div class="grid_4 alpha noMar">
                <dsp:include page="checkoutLogo.jsp" />                

                <dsp:a page="/cart/cart.jsp" iclass="returnToCart fl not-active"  title="Return to cart">                                        
                <span class="icon-chevron-left" aria-hidden="true"></span>                                       
                    <bbbl:label key="lbl_spc_checkout_return_to_cart" language="${pageContext.request.locale.language}" />
                </dsp:a>
            </div>
            
            

            <%--start signin section --%>
            <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_GUEST"/> 
            <div class="grid_8 omega singlePageLogin fbConnectWrap">
                <c:choose>
                    <c:when test="${isTransient}">                                        
                        <dsp:include page="/account/singlePage/login_frag.jsp">
                            <dsp:param name="checkoutFlag" value="1" />
                        </dsp:include>
                    </c:when>
                    <c:otherwise>
                        <dsp:getvalueof bean="/atg/userprofiling/Profile.firstName" var="displayName" />
                        <span id="loginName" class="fr"><bbbl:label key="lbl_spc_checkout_welcome2" language="${pageContext.request.locale.language}" />, <c:out value="${displayName}"/></span>
                        
                    </c:otherwise>
                </c:choose>

            </div>
            <%--Ending signin section --%>

            <dsp:include page="/common/click2chatlink.jsp">
                <dsp:param name="pageId" param="pageId"/>
            </dsp:include>
        </div>
        <div class="clear"></div>
    </div>
</dsp:page>