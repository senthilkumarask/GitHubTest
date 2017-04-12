<dsp:page>

    <%-- Imports --%>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
    <dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
    <dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler"/>

    <%-- Variables --%>
    <dsp:getvalueof var="orderType" vartype="java.lang.String" bean="ShoppingCart.current.onlineBopusItemsStatusInOrder" scope="page"/>
    <dsp:getvalueof var="giftCardException" bean="BBBGiftCardFormHandler.formExceptions"/>

	<dsp:getvalueof var="giftCardApplied" value="false"/>
    <c:choose>
        <c:when test="${orderType eq 'BOPUS_ONLY'}">
            <h3 class="checkout-title"><bbbl:label key="lbl_gc_label2" language="<c:out param='${language}'/>"/></h3>
            <p class="p-footnote"><bbbl:label key="lbl_gc_label1" language="<c:out param='${language}'/>"/></p>
        </c:when>
        <c:when test="${orderType ne 'BOPUS_ONLY'}">
        	<h3 class="sectionHeading noBorder">Gift Card</h3>
            <dsp:droplet name="BBBPaymentGroupDroplet">
                <dsp:param bean="ShoppingCart.current" name="order"/>
                <dsp:param name="serviceType" value="GiftCardDetailService"/>
                <dsp:oparam name="output">
                    <dsp:getvalueof var="orderTotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.total"></dsp:getvalueof>
                    <dsp:getvalueof vartype="java.lang.String" var="totalGCAmt" param="coveredByGC" scope="request"/>
                  
                    <%-- pay with gift card Grid section start--%>
                    <dsp:droplet name="Switch">
                        <dsp:param name="value" value="${isGiftcardsVar}"/>
                        <dsp:oparam name="true">
                            <div class="grid_8 highlightSection alpha omega gray-panel">
                                 <dl class="productsListHeader">
                                 	<dt class="small-1 columns"></dt>
                                     <dt class="small-3 columns wrapWord"><bbbl:label key="lbl_gc_gcNumber" language="<c:out param='${language}'/>"/></dt>
                                     <dt class="small-2 columns wrapWord no-padding">Amount Applied</dt>
                                     <dt class="small-3 columns wrapWord">Card Balance</dt>
                                     <dt class="small-3 columns wrapWord">Remainder Due</dt>
                                 </dl>
                                 <hr style="border-color:white"/>
                                 <div class="cartProducts">
                                     <dsp:droplet name="ForEach">
                                         <dsp:param param="giftcards" name="array"/>
                                         <dsp:oparam name="output">
                                         <div class="row">
                                           <dsp:getvalueof var="giftCardApplied" value="true"/>
                                           		<%-- <input id="gcId" type="hidden" value='<dsp:valueof param="element.id"/>'/> --%>
                                                 <label class="checkbox removeGiftCardlbl small-1 columns" id='<dsp:valueof param="element.id"/>' for="payWith">
                                                 	<input type="checkbox" value="" name="payWith" id="" checked="checked" disabled="disabled" aria-checked="true" aria-labelledby="lblpayWith"/><span></span>
                                                 </label>
                                                 <div class="small-3 columns wrapWord"><dsp:valueof param="element.cardNumber"></dsp:valueof></div>
                                                 <div class="small-2 columns wrapWord no-padding">
                                                     <dsp:droplet name="CurrencyFormatter">
                                                         <dsp:param name="currency" param="element.amount"/>
                                                         <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                         <dsp:oparam name="output">
                                                             <dsp:valueof param="formattedCurrency"/>
                                                         </dsp:oparam>
                                                     </dsp:droplet>
                                                 </div>
                                                 <div class="small-3 columns wrapWord">
                                                     <dsp:getvalueof var="cardBalance" param="element.balance">
                                                         <dsp:getvalueof var="cardAmount" param="element.amount">
                                                             <c:choose>
                                                                 <c:when test="${cardBalance >= cardAmount}">
                                                                     <dsp:droplet name="CurrencyFormatter">
                                                                         <dsp:param name="currency" value="${cardBalance - cardAmount}"/>
                                                                         <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                                         <dsp:oparam name="output">
                                                                             <dsp:valueof param="formattedCurrency"/>
                                                                         </dsp:oparam>
                                                                     </dsp:droplet>
                                                                 </c:when>
                                                                 <c:otherwise>
                                                                     <dsp:droplet name="CurrencyFormatter">
                                                                         <dsp:param name="currency" value="0"/>
                                                                         <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                                         <dsp:oparam name="output">
                                                                             <dsp:valueof param="formattedCurrency"/>
                                                                         </dsp:oparam>
                                                                     </dsp:droplet>
                                                                 </c:otherwise>
                                                             </c:choose>
                                                         </dsp:getvalueof>
                                                     </dsp:getvalueof>
                                                 </div>
                                                 <div class="small-3 columns wrapWord">
                                                    
                                                     <dsp:getvalueof param="count" var="cnt"/>
                                                     <dsp:getvalueof param="size" var="sz"/>
                                                     <c:choose>
                                                         <c:when test="${cnt eq sz}">
                                                  		<dsp:droplet name="CurrencyFormatter">
			                                         <dsp:param name="currency" value="${orderTotal - totalGCAmt}"/>
			                                         <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
			                                         <dsp:oparam name="output">
			                                             <dsp:valueof param="formattedCurrency"/>
			                                         </dsp:oparam>
			                                     </dsp:droplet>
			                                     </c:when>
			                                     <c:otherwise>
			                                     	--
			                                     </c:otherwise>
		                                     </c:choose>
                                                 </div>
                                                </div> 
                                         </dsp:oparam>
                                     </dsp:droplet>
                                 </div>
                                 <hr style="border-color:white"/>
                                 <dsp:form id="frmRemoveGiftCard" action="${contextPath}/checkout/payment/billing_payment.jsp" method="post" >
                                     <dsp:input bean="BBBGiftCardFormHandler.paymentGroupId" type="hidden" id="giftCardId" value=""/>
                                     <dsp:input bean="BBBGiftCardFormHandler.removePaymentGroup" type="hidden" id="removedGC" value="false"/>
                                 </dsp:form>
                            </div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <%-- pay with gift card grid section end --%>
                    <dsp:droplet name="Switch">
		                <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
		                <dsp:oparam name="false">
		                    <label class="inline-rc checkbox gray-panel add-gift-card-trigger" id="lblpayWith" for="payWith" class="payWithGirftCardLabel">
		                        <%-- NOTE: if there is a gift card applied then checkbox should be disabled and checked. If no gift cards applied then enabled and not checked --%>
		                        <dsp:droplet name="Switch">
		                            <dsp:param name="value" value="${isGiftcardsVar || not empty giftCardException}"/>
		                            <dsp:oparam name="true">
		                                <input type="checkbox" value="" name="payWith" id="payWith" checked="checked" disabled="disabled" aria-checked="true" aria-labelledby="lblpayWith" <c:if test="${empty giftCardException}">disabled="disabled" </c:if>/>
		                            </dsp:oparam>
		                            <dsp:oparam name="false">
		                                <input type="checkbox" value="" name="payWith" aria-checked="false" aria-labelledby="lblpayWith" id="payWith" />
		                            </dsp:oparam>
		                        </dsp:droplet>
		                        <span></span>
		                        <c:choose>
		                        	<c:when test="${giftCardApplied}">
		                        		<c:if test="${giftCardApplied}">Apply another gift card</c:if>
		                        	</c:when>
		                        	<c:otherwise>
		                        		<bbbl:label key="lbl_gc_label2" language="<c:out param='${language}'/>"/>
		                        	</c:otherwise>
		                        </c:choose>
		                        
		                        
		                        
		                    </label>
		                </dsp:oparam>
		                <dsp:oparam name="true">
		                </dsp:oparam>
		            </dsp:droplet>

                    <%-- Add a/another Gift Card start --%>
                    <dsp:droplet name="Switch">
                        <dsp:param name="value" value="${isMaxGiftcardAddedVar}"/>
                        <dsp:oparam name="false">
                            <dsp:droplet name="Switch">
                                <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
                                <dsp:oparam name="false">

                                    <%-- add gift card modal --%>
                                    <div id="addGiftCardModal" class="reveal-modal medium addGiftCardModal" data-reveal>
                                        <dsp:form id="frmPayWithGiftCard" iclass="frmPayWithGiftCard" action="${contextPath}/checkout/payment/billing_payment.jsp" method="post">
                                            <div class="row">
                                                <div class="small-12 columns">
                                                    <dsp:droplet name="Switch">
                                                        <dsp:param name="value" value="${isGiftcardsVar}"/>
                                                        <dsp:oparam name="true">
                                                            <h1 class="checkout-title"><bbbl:label key="lbl_gc_addGC" language="<c:out param='${language}'/>"/></h1>
                                                        </dsp:oparam>
                                                        <dsp:oparam name="false">
                                                            <h1 class="checkout-title">Apply a Gift Card</h1>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                </div>
                                                <div id="payErrorsGC" class="small-12 columns"></div>
                                                <div class="small-12 large-8 columns">
                                                    <c:set var="placeholderText"><bbbl:label key="lbl_gc_gcNumber" language="<c:out param='${language}'/>"/></c:set>
                                                    <dsp:input autocomplete="off" type="text" bean="BBBGiftCardFormHandler.giftCardNumber" id="giftCardNumberPWGC" maxlength="16"  iclass="validateCard" value="">
                                                        <dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardNumberPWGC errorgiftCardNumberPWGC"/>
                                                    </dsp:input>
                                                    <small for="giftCardNumberPWGC" class="error hidden" id="errorGiftCardNumberPWGC">Please enter valid Gift Card Number.</small>
                                                </div>
                                                <div class="small-12 large-4 columns">
                                                    <c:set var="placeholderText"><bbbl:label key="lbl_gc_gcPin" language="<c:out param='${language}'/>"/></c:set>
                                                    <dsp:input autocomplete="off" type="text" bean="BBBGiftCardFormHandler.giftCardPin" id="giftCardPinPWGC" maxlength="8" iclass="validateCard" value="">
                                                        <dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardPinPWGC errorgiftCardPinPWGC"/>
                                                    </dsp:input>
                                                    <small for="giftCardPinPWGC" class="error hidden" id="errorGiftCardPinPWGC">Please enter valid Gift Card Pin.</small>
                                                    <%-- <bbbl:label key="lbl_payment_find_your_pin" language="<c:out param='${language}'/>"/> --%>
                                                    <a title="How to Find Your Pin" href="/tbs/checkout/payment/find_your_pin.jsp" class="smallPopup">How to Find Your Pin</a>
                                                </div>

                                                <div class="small-12 columns hidden" id="addGCBal">
                                                    <div class="grid_3 alpha">
                                                        <label><strong>Gift Card:</strong> <span class="giftCardNumberResult giftCardResultItem"></span></label>
                                                    </div>
                                                    <div class="grid_2 alpha omega">
                                                        <label class="bal"><strong><bbbl:label key="lbl_gc_bal" language="<c:out param='${language}'/>"/></strong> <span class="balance giftCardResultItem"></span></label>
                                                    </div>
                                                </div>
                                                
                                                <div class="small-12 large-offset-8 large-4 columns">
                                                    <dsp:input type="submit" bean="BBBGiftCardFormHandler.createGiftCard" id="createGiftCard" value="Apply" iclass="small button service expand">
                                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                        <dsp:tagAttribute name="role" value="button"/>
                                                    </dsp:input>
                                                    <dsp:input bean="BBBGiftCardFormHandler.fromPage" type="hidden" value="billingpayment" />
                                                   <!--  <dsp:input type="hidden" bean="BBBGiftCardFormHandler.giftCardSuccessURL" value="${contextPath}/checkout/checkoutType.jsp"/>-->
                                                </div>
                                            </div>
                                        </dsp:form>
                                        
                                        <dsp:form action="${contextPath}/checkout/payment/billing_payment.jsp" method="post"  id="frmPayWithGiftCardHiddenForm" iclass="">
                                            <dsp:input type="hidden" name="giftCardNumber" value="" id="giftCardNumberPWGC" bean="BBBGiftCardFormHandler.giftCardNumber"/>
                                            <dsp:input type="hidden" name="giftCardPin" value="" id="giftCardPinPWGC"  bean="BBBGiftCardFormHandler.giftCardPin" />
                                            <dsp:input type="submit" name="submit" value="CHECK BALANCE" id="btnSubmitCheckGC" bean="BBBGiftCardFormHandler.balance" iclass="small secondary button">
                                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="btnSubmitCheckGC"/>
                                                <dsp:tagAttribute name="role" value="button"/>
                                            </dsp:input>
                                        </dsp:form>
                                       
                                        <a class="close-reveal-modal">&#215;</a>
                                    </div>

                                </dsp:oparam>
                            </dsp:droplet>
                        </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
            </dsp:droplet>
        </c:when>
    </c:choose>

    <script type="text/javascript">
        function omnitureExternalLinks(data){
            if (typeof s !== 'undefined') {
                externalLinks(data);
            }
        }
		$('body').on('click', '#addGiftCardModal a.close-reveal-modal', function(e) {
			////console.log("addGiftCardModal closed");
			$('#lblpayWith input[type="checkbox"]').prop('checked',false);
			var $targetPayAtRegister = $('#registerPaymentOpt');
			//$targetPayAtRegister.prop('checked',false);
		    $targetPayAtRegister.prop('disabled', false);
		    $('label.payment-type-trigger.credit-card').click();
		});
	
    </script>

</dsp:page>
