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
    <dsp:importbean bean="/atg/userprofiling/Profile"/>   
    <dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<dsp:getvalueof param="isFormException" var="isFormException"/>
    <dsp:getvalueof param="commerceItemCount" var="totalCartCount"/>      
    <dsp:getvalueof param="registryItemCount" var="registryCount"/>      
    <dsp:getvalueof param="isDefaultShippAddr" var="isDefaultShippAddr"/>
    <dsp:getvalueof var="shippingSubmitted" param="shippingSubmitted" />

    <fieldset class="radioGroup ">
        <legend class="hidden"><bbbl:label key="lbl_spc_shipping_address_selection" language="${pageContext.request.locale.language}" />:</legend>
        
        <dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:oparam name="output">
               <dsp:getvalueof param="isPackHold" var="isPackHold"/>     
           </dsp:oparam>
        </dsp:droplet>
       

<%--        
        shippingAddressForm: registryCount: ${registryCount}<br />
        shippingAddressForm: totalCartCount: ${totalCartCount}<br />
        shippingAddressForm: shippingAddressForm: ${shippingSubmitted}<br />


        <dsp:include page="/checkout/singlePage/shipping/displayRegistry.jsp">                  
         <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
         <dsp:param name="registryItemCount" value="${registryCount}"/>      
         <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
        </dsp:include>
                
        --%> 


        <%-- 
            To improve performance, we will only show the new address form 
            if there is no address on the order  (shippingSubmitted)
            if there is no registry item in the cart (don't need to look up the registry address)
            if the user is anon - don't need to look up the profile addresses

            this will avoid caling the DisplayShippingAddress droplet
        --%>

        <c:choose>
            <c:when test="${!shippingSubmitted && registryCount == 0 && isAnonymousProfile}">
            
                 <dsp:getvalueof var="beddingShipAddrVO" param="collegeAddress"/>
                        
                    <dsp:include page="/checkout/singlePage/newAddress.jsp">                  
                        <dsp:param name="beddingShipAddrVO" value="${beddingShipAddrVO}"/>
                        <dsp:param name="isFormException" value="${isFormException}"/>
                        <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                        <dsp:param name="registryItemCount" value="${registryCount}"/>      
                        <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                        <dsp:param name="isPackHold" value="${isPackHold}"/>
                    </dsp:include>
                        
            </c:when>
            <c:otherwise>
                <dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplayShippingAddress">
                    <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
                    <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>            
                    <dsp:param name="addressContainer" bean="/com/bbb/commerce/checkout/ShippingAddContainer"/>
                    <dsp:param name="isPackHold" value="${isPackHold}"/> 
                    <dsp:oparam name="output">

                    <%-- --%>                       
                        <dsp:include page="/checkout/singlePage/shipping/displayRegistry.jsp">                  
                         <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                         <dsp:param name="registryItemCount" value="${registryCount}"/>      
                         <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                        </dsp:include>
                        
                        <c:if test="${registryCount == 0 || registryCount == '0'}">
                            <dsp:include page="/checkout/singlePage/addressDisplay.jsp" > 
                                <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                                <dsp:param name="registryItemCount" value="${registryCount}"/>      
                                <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                            </dsp:include>
                        </c:if>    
                        
                    </dsp:oparam>
                    <dsp:oparam name="end">
                           
                        <dsp:getvalueof var="beddingShipAddrVO" param="collegeAddress"/>
                        
                        <dsp:include page="/checkout/singlePage/newAddress.jsp">                  
                         <dsp:param name="beddingShipAddrVO" value="${beddingShipAddrVO}"/>
                         <dsp:param name="isFormException" value="${isFormException}"/>
                         <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                         <dsp:param name="registryItemCount" value="${registryCount}"/>      
                         <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                         <dsp:param name="isPackHold" value="${isPackHold}"/>
                        </dsp:include>
                        
                    </dsp:oparam>
                </dsp:droplet>
            </c:otherwise>
        </c:choose>

        
        


        <c:if test="${beddingShipAddrVO != null || isPackHold eq true}">
         
          <dsp:include page="/checkout/singlePage/newCollegeAddress.jsp"/>                  
         </c:if>
     </fieldset>
</dsp:page>
