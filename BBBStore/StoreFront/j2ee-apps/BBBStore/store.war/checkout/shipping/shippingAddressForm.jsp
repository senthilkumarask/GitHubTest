<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  shippingTitle.jsp
 *
 *  DESCRIPTION: Shipping address page single address form
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:getvalueof param="isFormException" var="isFormException"/>
    <fieldset class="radioGroup hightlightLineItem shippingAddressRadioGroup">
        <legend class="hidden"><bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />:</legend>
        
        <dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:oparam name="output">
               <dsp:getvalueof param="isPackHold" var="isPackHold"/>     
           </dsp:oparam>
        </dsp:droplet>
       
        
        <dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplayShippingAddress">
            <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
            <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>            
            <dsp:param name="addressContainer" bean="/com/bbb/commerce/checkout/ShippingAddContainer"/>
            <dsp:param name="isPackHold" value="${isPackHold}"/> 
            <dsp:oparam name="output">
                <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
                <input type="hidden" id="selectedAddressId" value="${selectedAddress}" />

                <dsp:include page="/checkout/shipping/displayRegistry.jsp"/>
                <dsp:include page="/checkout/addressDisplay.jsp"/>                    
            </dsp:oparam>
            <dsp:oparam name="end">
                   
                 <dsp:getvalueof var="beddingShipAddrVO" param="collegeAddress"/>
                
                <dsp:include page="/checkout/newAddress.jsp">                  
                 <dsp:param name="beddingShipAddrVO" value="${beddingShipAddrVO}"/>
                 <dsp:param name="isFormException" value="${isFormException}"/>
                </dsp:include>
            </dsp:oparam>
        </dsp:droplet>
        <c:if test="${beddingShipAddrVO != null || isPackHold eq true}">
         
          <dsp:include page="/checkout/newCollegeAddress.jsp"/>                  
         </c:if>
     </fieldset>
</dsp:page>