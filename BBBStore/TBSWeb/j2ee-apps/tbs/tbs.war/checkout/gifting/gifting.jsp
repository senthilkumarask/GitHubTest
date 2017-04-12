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
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
 	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapCheckDroplet"/>
 	<dsp:importbean bean="/atg/multisite/Site"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
        
	<c:set var="firstVisit" scope="session">false</c:set>
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="state" value="${40}"/>
    <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
    <dsp:getvalueof id="shipGrpCount" bean="ShoppingCart.current.shippingGroupCount" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
         
    <c:choose>
        <c:when test="${currentState lt state or couponMap}">
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
            </dsp:droplet>
        </c:when>
        <c:otherwise>
            <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="GIFT"/>
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
	    <jsp:attribute name="bodyClass">checkout gifting</jsp:attribute>
    
		<jsp:body>
    	
    		<div class="row">
				<div class="small-12 columns">
					<%-- multiship progress bar --%>
					<dsp:include page="/checkout/progressBar.jsp">
						<dsp:param name="step" value="gifting"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<dsp:include page="/global/gadgets/errorMessage.jsp">
				 		<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
		 			</dsp:include>
		     	</div>
				<dsp:form id="formShippingSingleLocation" name="form" formid="formShippingSingleLocation" action="${pageContext.request.requestURI}" method="post">
                	<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.siteId" value="${currentSiteId}"/>
                	<c:set var="count" value="0" scope="page" /> 
                	
                	<dsp:droplet name="GiftWrapCheckDroplet">
               		<dsp:param name="order" bean="ShoppingCart.current" />
             		<dsp:param name="siteId" value="${currentSiteId}" />
                 	<dsp:param name="giftWrapOption" value="multi" />
                 	<dsp:oparam name="output">
                   		<dsp:getvalueof var="tempCompanyName" param="shipAddress.companyName" />
						<dsp:getvalueof var="tempShippingGroup" param="shipGroupParam" />
						
						<div class="small-12 columns">
							<h2 class="divider no-padding-left">
								Shipment ${count+1}
								<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
							</h2>
						</div>
        		
						<div class="small-12 large-5 columns">
							<h3 class="checkout-title no-margin-top">Shipping Information</h3>
							<ul class="address">
								<c:choose>
									<c:when test="${not empty tempShippingGroup.registryId}">
										<li>${tempShippingGroup.registryInfo}</li>	
									</c:when>
									<c:otherwise>
										<li>
											<dsp:valueof param="shipAddress.firstName" /> <dsp:valueof param="shipAddress.lastName" />
											<c:if test="${fn:trim(tempCompanyName) != '' || not empty tempCompanyName}">
												(<dsp:valueof param="shipAddress.companyName" />)
											</c:if>
										</li>
										<li>
											<dsp:droplet name="IsEmpty">
											<dsp:param name="value" param="shipAddress.address2"/>
											<dsp:oparam name="true">
												<dsp:valueof param="shipAddress.address1" />
											</dsp:oparam>
											<dsp:oparam name="false">
												<dsp:valueof param="shipAddress.address1" />, <dsp:valueof param="shipAddress.address2" />
											</dsp:oparam>
											</dsp:droplet>
										</li>
										<li><dsp:valueof param="shipAddress.city" />, <dsp:valueof param="shipAddress.state"/> <dsp:valueof param="shipAddress.postalCode" /></li>				
									</c:otherwise>
								</c:choose>
							</ul>
						</div>
						<div class="small-12 large-7 columns">
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
						</div>
						<div class="viewOrderContentContainer small-12 columns">
                           	<div><a class="viewOrderContents iconExpand" href="#" title="Click here to expand and view cart contents"><bbbl:label key="lbl_multi_gift_view_order" language="<c:out param='${language}'/>"/></a></div>
                             <div class="small-12 columns">
                             	<div class="orderContents small-12 columns">
                                   	<div class="orderContentsCarousel">
	                                   	<dsp:include page="frag/shippingGroupItems.jsp">
	                                        <dsp:param name="giftWrapMap" param="giftWrapMap" />
	                                   		<dsp:param name="commItemList" param="commItemList" />
	                                	</dsp:include>
	                           		</div>
                            	</div>
                        	</div>
	                    </div>
						<c:set var="count" value="${count + 1}" scope="page" />
					</dsp:oparam>
					<dsp:oparam name="empty">
                    	<bbbl:label key="lbl_multi_gift_error_msg" language="<c:out param='${language}'/>"/>
 					</dsp:oparam>                 
					</dsp:droplet>
					
					<div class="small-12 columns last">
						<h2 class="divider">&nbsp;</h2>
					</div>
					<div class="small-12 large-offset-10 large-2 columns">
                      	<c:set var="lbl_button_next" scope="page">
                   			<bbbl:label key="lbl_button_next" language="${pageContext.request.locale.language}" />
                 		</c:set>
                  		<dsp:input type="submit" value="${lbl_button_next}" id="submitShippingSingleLocationBtn" bean="BBBShippingGroupFormhandler.giftMessaging" iclass="enableOnDOMReady small button service expand">
                        	<dsp:tagAttribute name="aria-pressed" value="false"/>
                        	<dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
                       		<dsp:tagAttribute name="role" value="button"/>
                  		</dsp:input>
              		</div>
                </dsp:form>
			</div>
			
		</jsp:body>
    
	</bbb:pageContainer>
</dsp:page>