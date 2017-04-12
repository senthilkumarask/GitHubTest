<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  gifting.jsp
 *
 *  DESCRIPTION: Multi Gift options
 *
 *  HISTORY:
 *  Jan 16, 2012  Initial version
 * 
 * 
--%>
<dsp:page>

<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<c:set var="firstVisit" scope="session">false</c:set>
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="state" value="${45}"/>
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
            <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_GIFT"/>
        </c:otherwise>
    </c:choose>
    
<bbb:pageContainer index="false" follow="false">
    <jsp:attribute name="headerRenderer">
        <dsp:include page="/checkout/checkout_header.jsp" flush="true">
            <dsp:param name="pageId" value="6"/>
            <dsp:param name="step" value="gifting"/>
        </dsp:include>
    </jsp:attribute>

    <jsp:attribute name="section">checkout</jsp:attribute>
    <jsp:attribute name="pageWrapper">billing billingCoupons giftOptions</jsp:attribute>
    <jsp:attribute name="PageType">Gifting</jsp:attribute>
    <jsp:body>
        <dsp:importbean bean="/atg/commerce/ShoppingCart" />
        <dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapCheckDroplet"/>
        <dsp:importbean bean="/atg/multisite/Site"/>
        <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
        <dsp:getvalueof id="shipGrpCount" bean="ShoppingCart.current.shippingGroupCount" />
        <dsp:getvalueof id="currentSiteId" bean="Site.id" />
        <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
  
        <div id="content" class="container_12 clearfix" role="main">
            <h3 class="sectionHeading noBorder marLeft_10"><bbbl:label key="lbl_spc_multi_gift_select" language="<c:out param='${language}'/>"/></h3>
           
			 <dsp:include page="/global/gadgets/errorMessage.jsp">
			 	<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
	     	 </dsp:include>
            
            <div class="clearfix">
                <div id="leftCol" class="grid_8 highlightSection">
                    <dsp:form id="formShippingSingleLocation"  name="form" formid="formShippingSingleLocation" action="${pageContext.request.requestURI}" method="post">
                        <dsp:input bean="BBBShippingGroupFormhandler.siteId" value="${currentSiteId}" type="hidden"/>    
                        <fieldset class="noMarTop">
                            <c:set var="count" value="0" scope="page" />
                            <dsp:droplet name="GiftWrapCheckDroplet">
                                <dsp:param name="order" bean="ShoppingCart.current" />
                                <dsp:param name="siteId" value="${currentSiteId}" />
                                <dsp:param name="giftWrapOption" value="multi" />
                                <dsp:oparam name="output">
                                    <dsp:getvalueof var="tempCompanyName" param="shipAddress.companyName" />
									<dsp:getvalueof var="tempShippingGroup" param="shipGroupParam" />
                                    <div class="radioItem input clearfix noBorder">
                                        <div class="label">
                                            <label class="bold">
												<c:choose>
													<c:when test="${not empty tempShippingGroup.registryId}">
															<span><strong>${tempShippingGroup.registryInfo}</strong></span>	
													</c:when>
													<c:otherwise>
														<span><dsp:valueof param="shipAddress.firstName" /> <dsp:valueof param="shipAddress.lastName" /></span>
														<c:if test="${tempCompanyName != ''}">
															<span><dsp:valueof param="shipAddress.companyName" /></span>
														</c:if>
														<span>
															<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
																<dsp:param name="value" param="shipAddress.address2"/>
																<dsp:oparam name="true">
																	<dsp:valueof param="shipAddress.address1" />
																</dsp:oparam>
																<dsp:oparam name="false">
																	<dsp:valueof param="shipAddress.address1" />, <dsp:valueof param="shipAddress.address2" />
																</dsp:oparam>
															</dsp:droplet>
														</span>
														<span><dsp:valueof param="shipAddress.city" />, <dsp:valueof param="shipAddress.state"/> <dsp:valueof param="shipAddress.postalCode" /></span>				
													</c:otherwise>				
												</c:choose>
                                                
                                            </label>
                                            <p class="noMar"><dsp:valueof param="shippingMethodDescription" /></p>
                                        </div>
                                    </div>

                                    <div class="viewOrderContentContainer">
                                        <div class="marBottom_5"><a class="viewOrderContents iconExpand" role="button" href="#" title="Click here to expand and view cart contents" aria-expanded="false" aria-controls="orderContentDetails" aria-live="assertive"><bbbl:label key="lbl_spc_multi_gift_view_order" language="<c:out param='${language}'/>"/></a></div>
                                        <div>
                                            <div class="orderContents clearfix" id="orderContentDetails">
                                                <div class="carousel clearfix">
                                                    <div class="carouselBody grid_8 marTop_20">
                                                        <div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix"> &nbsp; <a class="carouselScrollPrevious" title="Previous" href="#"><bbbl:label key="lbl_spc_multi_gift_previous" language="<c:out param='${language}'/>"/></a> </div>
                                                        <div class="carouselContent grid_6 clearfix">
                                                            <ul class="prodGridRow">
                                                                <dsp:include page="frag/shippingGroupItems.jsp">
                                                                    <dsp:param name="giftWrapMap" param="giftWrapMap" />
                                                                    <dsp:param name="commItemList" param="commItemList" />
                                                                </dsp:include>
                                                            </ul>
                                                        </div>
                                                        <div class="grid_1 carouselArrow alpha carouselArrowNext clearfix"> &nbsp; <a class="carouselScrollNext" title="Next" href="#"><bbbl:label key="lbl_spc_multi_gift_next" language="<c:out param='${language}'/>"/></a> </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <fieldset class="noMarTop">                                        
										<dsp:include page="frag/giftWrapMessage.jsp">
                                            <dsp:param name="giftWrapPrice" param="giftWrapPrice" />
                                            <dsp:param name="count" value="${count}" />
                                            <dsp:param name="shipGroupId" param="shipGroupId" />
                                            <dsp:param name="shipGroupParam" param="shipGroupParam" />
                                            <dsp:param name="shipGroupGiftMessage" param="shipGroupGiftMessage" />
                                            <dsp:param name="shipGroupGiftInd" param="shipGroupGiftInd" />
                                            <dsp:param name="nonGiftWrapSkus" param="nonGiftWrapSkus" />    
                                            <dsp:param name="giftWrapFlag" param="giftWrapFlag" />
                                        </dsp:include>
                                    </fieldset>

                                    <c:set var="count" value="${count + 1}" scope="page" />

                                </dsp:oparam>
                                <dsp:oparam name="empty">
                                    <bbbl:label key="lbl_spc_multi_gift_error_msg" language="<c:out param='${language}'/>"/>
                                </dsp:oparam>
                            </dsp:droplet>
                        </fieldset>
                        <div class="button button_active">
                            <c:set var="lbl_button_next" scope="page">
                                <bbbl:label key="lbl_spc_button_next" language="${pageContext.request.locale.language}" />
                            </c:set>
                            <dsp:input type="submit" value="${lbl_button_next}" id="submitShippingSingleLocationBtn" bean="BBBShippingGroupFormhandler.giftMessaging" >
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
                        </div>
                    </dsp:form>
                </div>
                <dsp:include page="/checkout/order_summary_frag.jsp">
                  <dsp:param name="displayTax" value="false"/>
                </dsp:include>
            </div>
        </div>
    </jsp:body>
</bbb:pageContainer>
</dsp:page>