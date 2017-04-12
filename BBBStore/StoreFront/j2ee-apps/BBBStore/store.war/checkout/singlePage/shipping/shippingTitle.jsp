<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  shippingTitle.jsp
 *
 *  DESCRIPTION: Shipping address page title is rendered
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	 <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	 <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	 <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	 <dsp:droplet name="OrderHasLTLItemDroplet">
	 	 <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
	 	 <dsp:oparam name="output">
	 	 	<dsp:getvalueof param="orderHasLTLItem" var="orderHasLTLItem"/>
          </dsp:oparam>
	 </dsp:droplet>
    <c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBWeblinkDroplet">
		  <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
	 	  <dsp:param name="shippingGroup" param="order.shippingGroups"/>
	 	  <dsp:param name="siteId" value="${siteId}"/>
	 	   <dsp:oparam name="weblinkOrder">
           		<c:set var="weblinkOrder" value="true"/>
           </dsp:oparam>
           <dsp:oparam name="notWeblinkOrder">
           		<c:set var="weblinkOrder" value="false"/>
           </dsp:oparam>
	</dsp:droplet>

	<div id="subHeader" class="grid_12 clearfix">
        <div class="clearfix">
            <dsp:getvalueof var="link" param="link"/>
            <c:choose>
                <c:when test="${link == 'single'}">
                    <h2 class="section fl"><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" />:&nbsp;<span class="subSection"><bbbl:label key="lbl_spc_shipping_single_page_title" language="${pageContext.request.locale.language}" /></span></h2>
                    <c:if test="${weblinkOrder == 'false'}">
                    <div class="fr suffix_3 shippingOption marTop_10">
                        <dsp:a page="/checkout/shipping/shipping.jsp">
                        <dsp:param name="shippingGr" value="multi"/>
                        <%-- <c:if test = "${payPalOrder eq true && isFromPreview}">
                        	<dsp:param name="isFromPreview" value="${isFromPreview}"/>
                        </c:if> --%>
						<strong>
						<bbbl:label key="lbl_spc_shipping_multiple_page_link" language="${pageContext.request.locale.language}" />
						<c:choose>
						<c:when test="${MapQuestOn}"  >
							&nbsp;<bbbl:label key="lbl_spc_or_store_pickup" language="${pageContext.request.locale.language}" />
						</c:when>
						</c:choose>
						</strong>
						</dsp:a>
                    </div>
                    </c:if>
                </c:when>
                <c:when test="${link == 'multiple'}">
                    <h2 class="section fl"><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" />:&nbsp;
                    	<span class="subSection">
                    	<c:choose>
                    		<c:when test="${orderHasLTLItem }">
                    	    	<bbbl:label key="lbl_spc_shipping_advanced_page_title" language="${pageContext.request.locale.language}" />
                    		</c:when>
                    		<c:otherwise>
                    	    	<bbbl:label key="lbl_spc_shipping_multiple_page_title" language="${pageContext.request.locale.language}" />
                    		</c:otherwise>
                    	</c:choose>
                    	</span>
                    </h2>
                    <dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplaySingleShippingDroplet">
                        <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
                        <dsp:oparam name="single">
                            <div class="fr suffix_3 shippingOption marTop_10">
                                <dsp:a page="/checkout/shipping/shipping.jsp"><strong><bbbl:label key="lbl_spc_shipping_single_page_link" language="${pageContext.request.locale.language}" /></strong></dsp:a>
                            </div>
                        </dsp:oparam>                    
                    </dsp:droplet>
                    
                </c:when>
            </c:choose>
            
        </div>
    </div>
    <%-- Added as Part of PayPal Story - 83-N : Start --%>
    <%-- Adding Error Messages after validating Paypal shipping address. --%>
    <dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
     <dsp:getvalueof var="paypalShipping" param="paypalShipping"/>
	 <c:set var="size" value="${fn:length(errorList)}"/>
	 <c:if test="${size gt 0}">
	 <div class="clearfix fl">
	 	<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${errorList}" />
			<dsp:oparam name="output">
				<div class="payPalAddressError error marLeft_10">
					<dsp:getvalueof var="error" param="element"/>
					${error}
				</div>
			</dsp:oparam>
		</dsp:droplet>
	</div>	
	 </c:if>
	 <c:if test="${paypalShipping eq true}">
		<span class="payPalAddressError error marLeft_10 fl">
			<bbbl:error key="err_paypal_set_express_shipping_error" language="${pageContext.request.locale.language}" />
		</span>
	 </c:if>
	 <%-- Added as Part of PayPal Story - 83-N : End --%>
</dsp:page>