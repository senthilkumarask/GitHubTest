<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  singleShipping.jsp
 *
 *  DESCRIPTION: page for collection single shipping address for an order
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

	<bbb:pageContainer index="false" follow="false" >
	    <jsp:attribute name="headerRenderer">
	      <dsp:include page="/checkout/checkout_header.jsp" flush="true">
	        <dsp:param name="step" value="shipping"/>
	        <dsp:param name="link" value="single"/>
            <dsp:param name="pageId" value="6"/>
	      </dsp:include>
	    </jsp:attribute>
	    <jsp:attribute name="section">checkout</jsp:attribute>
	    <jsp:attribute name="pageWrapper">billing shippingWrapper singleShippingPage useGoogleAddress</jsp:attribute>
	    <jsp:attribute name="PageType">SingleShipping</jsp:attribute>
	    <jsp:body>
	     <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
         <div id="content" class="container_12 clearfix" role="main">
            <h3 class="sectionHeading noBorder marLeft_10"><bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />:</h3>
			 <dsp:include page="/global/gadgets/errorMessage.jsp">
				 <dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
			 </dsp:include>
			<dsp:getvalueof var="formExceptions" bean="BBBShippingGroupFormhandler.formExceptions"/>
          		<c:choose>
            		<c:when test="${empty formExceptions}">
            		<c:set var="isFormException" value="false"/>
            		</c:when>
            		<c:otherwise>
            		<c:set var="isFormException" value="true"/>
            		</c:otherwise>
            		</c:choose>
					              <dsp:droplet name="/com/bbb/commerce/cart/BBBItemCountDroplet">
                <dsp:param name="shoppingCart" bean="ShoppingCart.current" />
                <dsp:oparam name="output">
                  <dsp:getvalueof param="registryItemCount" var="registryCount"/>      
                </dsp:oparam>
              </dsp:droplet>
            <div class="clearfix">

                <div id="leftCol" class="grid_8 highlightSection">
                <dsp:form id="formShippingSingleLocation"  formid="com_bbb_checkoutShippingAddress"
                        action="${pageContext.request.requestURI}" method="post">

                        <dsp:input type="hidden" id="removePorchServiceInput" bean="BBBShippingGroupFormhandler.removePorchService" value="false" />

                        <dsp:include page="/checkout/shipping/shippingAddressForm.jsp">
                        	<dsp:param name="isFormException" value="${isFormException}"/>
                        </dsp:include>
                        <fieldset class="radioGroup">
                            <legend class="hidden"><bbbl:label key="lbl_shipping_shipping_method" language="${pageContext.request.locale.language}" /></legend>
							<h3 class="sectionHeading">
							<bbbl:label key="lbl_shipping_shipping_method" language="${pageContext.request.locale.language}" /></h3>
                            <dsp:include page="/checkout/shipping/shippingMethods.jsp">
								<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
							</dsp:include>
							<div class="marTop_5"><bbbl:label key="lbl_shipping_option_disclaimer" language="${pageContext.request.locale.language}" /> 
							<bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="${pageContext.request.locale.language}" /></div>
                        </fieldset>
                       
						 <fieldset class="padTop_10">
						  <dsp:include page="frag/singleShippingOptionsFrag.jsp">
						        <dsp:param name="registryItemCount" value="${registryCount}"/>      
						    <dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
						  </dsp:include>
						 </fieldset>
                        <div class="button button_active button_disabled">
                            <c:set var="lbl_continue_to_billing" scope="page">
	                               <bbbl:label
										key="lbl_continue_to_billing"
										language="${pageContext.request.locale.language}" />
                            </c:set>
                            <dsp:input type="submit" value="${lbl_continue_to_billing}" disabled="true" iclass="enableOnDOMReady" id="submitShippingSingleLocationBtn" 
                            	 bean="BBBShippingGroupFormhandler.addShipping">                              
                                <dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
                            
                        </div>
                     </dsp:form>

                </div>
               <dsp:include page="/checkout/order_summary_frag.jsp">
               		<dsp:param name="displayTax" value="false"/>
               		<dsp:param name="displayShippingDisclaimer" value="true"/>
               </dsp:include>
            </div>
        </div>
        
    </jsp:body>
	 		
		<jsp:attribute name="footerContent">

			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				 <dsp:oparam name="output">
					<dsp:getvalueof var="productIds" param="commerceItemList" />
		        </dsp:oparam>     
		    </dsp:droplet>

			<script type="text/javascript">
			           if(typeof s !=='undefined') {
			        	s.pageName='Check Out>Shipping';
			        	s.channel='Check Out';
			        	s.prop1='Check Out';
			        	s.prop2='Check Out';
			        	s.prop3='Check Out';
			        	s.prop6='${pageContext.request.serverName}'; 
						s.eVar9='${pageContext.request.serverName}';
			            s.events="scCheckout,event8";
    					s.products='${productIds}';			           
			            var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			</script>
		</jsp:attribute>
	
  </bbb:pageContainer>  
</dsp:page>