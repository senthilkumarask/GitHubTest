<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
    <dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
    <dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetCreditCardsForPayment"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/BBBCreditCardContainer"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:getvalueof var="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>    
   <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
   <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
   <dsp:getvalueof var="state" value="${75}"/>
   <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />


   <%-- need to get config values because ajax request --%>

   <c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_ca"/></c:set>	
		</c:otherwise>
	</c:choose>	
   
	<c:set var="paypalOn" scope="request"><bbbc:config key="paypalFlag" configName="FlagDrivenFunctions" /></c:set>
		<%-- adding this flag for R2.2 scope 83-3  --%>
	<c:set var="paypalButtonEnable" scope="request"><bbbc:config key="paypalButtonFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="paypalCartButtonEnable" scope="request"><bbbc:config key="paypalButtonCartFlag" configName="FlagDrivenFunctions" /></c:set>
	
	<%-- adding these 2 flags for R2.2 scope 258  --%>
	<c:set var="visaLogoOn" scope="request"><bbbc:config key="visaLogoFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="masterCardLogoOn" scope="request"><bbbc:config key="masterCardLogoFlag" configName="FlagDrivenFunctions" /></c:set>



	<c:set var="displayTax" scope="request">
        <dsp:valueof param="spcAddingGiftCard"/>
    </c:set>

	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	 <c:set var="isFromPreview" value="${param.isFromPreview}"/>
	 <c:choose>
	 <c:when test = "${payPalOrder eq true && isFromPreview eq true}">    
    </c:when>
    <c:otherwise>
    <dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>
    </c:otherwise>
    </c:choose>
    
    <dsp:param name="order" bean="ShoppingCart.current" />
  	<dsp:getvalueof var="billingAddress" vartype="java.lang.Object" param="order.billingAddress"/>
    <dsp:droplet name="/com/bbb/commerce/checkout/droplet/SuperScriptDroplet">
	        <dsp:param name="order" param="order"/>
	        <dsp:oparam name="output">
	           <dsp:tomap  var="placeHolderMap" param="SuperScriptMap"/>
	        </dsp:oparam>
    </dsp:droplet>
    
    <dsp:getvalueof var="billingAdd1" bean="ShoppingCart.current.billingAddress.address1" />    
    <c:if test="${not empty billingAdd1}">
    <dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
			<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
   
   		<%--
        	<jsp:attribute name="headerRenderer">
            <dsp:include page="/checkout/checkout_header.jsp" flush="true">
                <dsp:param name="step" value="payment"/>
                <dsp:param name="pageId" value="7"/>
            </dsp:include>
        	</jsp:attribute>
			--%>

        
        
        

        <%-- <c:import url="/checkout/singlePage/payment/billing_address_change.jsp"/> --%>
        
        <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
        <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
        <c:set var="chooseCardType" scope="page">
            <bbbl:label key="lbl_spc_choose_cardtype" language="${pageContext.request.locale.language}"/>
        </c:set>
        <c:set var="year" scope="page">
            <bbbl:label key="lbl_spc_addcreditcard_year" language="${pageContext.request.locale.language}" />
        </c:set>
        <c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>    
        <c:choose>
            <c:when test="${internationalCCFlag eq 'true'}">
                <input type="hidden" id="internationalCC" name="internationalCC" value="${internationalCCFlag}" />
            </c:when>
            <c:otherwise>
                <input type="hidden" id="internationalCC" name="internationalCC" value="" />
            </c:otherwise> 
        </c:choose>
         <%-- adding anchor here for verified by visa error message--%>
			<a name="vbvMsg"></a>
        <div class="clearfix" id="paymentSection" role="main">
            <div id="leftCol" class="">
               <div class="width_8 clearfix">

               	<hr />
               	<h1 class="SPCSectionHeading"><bbbl:label key="lbl_spc_section_heading_payment" language="${pageContext.request.locale.language}" /> <span class="icon icon-checkmark hidden"></span></h1>

						<c:if test="${payPalOrder eq true}">
               	<div class="alert alert-alert" id="editBillingForPaypalMSG">Please add a new billing address to enable and select a form of payment.</div>
               	</c:if >

	                <%-- Start: 258 - Verified by visa - refresh back button story - display error message and reset the payment state --%>
	                <dsp:getvalueof var="bBBVerifiedByVisaVO" bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO"/>
	                <c:if test="${bBBVerifiedByVisaVO ne null}">
		               <dsp:getvalueof var="token" bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.token"/>
		               <dsp:getvalueof var="message" bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.message"/>
		               <c:if test="${token eq 'error_true'}">
		               		<div class="error">${message}</div>
		               </c:if> 
		                <dsp:setvalue bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.token" value=""/>
	                </c:if>
	                <%-- End: 258 - Verified by visa - refresh back button story - display error message and reset the payment state --%>

               </div>
               
              	<dsp:droplet name="BBBPaymentGroupDroplet">
                 	<dsp:param bean="ShoppingCart.current" name="order"/>
                 	<dsp:param name="serviceType" value="GiftCardDetailService"/>
                 	<dsp:oparam name="output">
                  	 <dsp:getvalueof vartype="java.lang.String" var="totalGCAmt" param="coveredByGC"/>
                   	<c:set var="totalGCAmt">${totalGCAmt}</c:set>
                 	</dsp:oparam>
              	</dsp:droplet>

               <dsp:droplet name="BBBPaymentGroupDroplet">
                  <dsp:param bean="ShoppingCart.current" name="order"/>
                  <dsp:param name="serviceType" value="GetPaymentGroupStatusOnLoad"/>
                    
                  <dsp:oparam name="output">
                        <dsp:getvalueof var="isGiftcardsVar" vartype="java.lang.String" param="isGiftcards" scope="request"/>
                        <dsp:getvalueof var="isOrderAmtCoveredVar" vartype="java.lang.String" param="isOrderAmtCovered" scope="request"/>
                        <dsp:getvalueof var="isMaxGiftcardAddedVar" vartype="java.lang.String" param="isMaxGiftcardAdded" scope="request"/>
                        <dsp:getvalueof var="orderTotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.total"></dsp:getvalueof> 
                        <dsp:getvalueof var="storeSubtotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.storeSubtotal"></dsp:getvalueof>
                        <dsp:getvalueof var="orderType" vartype="java.lang.String" bean="ShoppingCart.current.onlineBopusItemsStatusInOrder" scope="page"/>
                        <c:choose>
                            <c:when test="${orderType eq 'BOPUS_ONLY'}">
                              <c:set var="priceValue" value="${storeSubtotal}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="priceValue" value="${orderTotal - totalGCAmt}"/>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                           <c:when test="${not isOrderAmtCoveredVar}">
                              <dsp:include page="/checkout/singlePage/payment/paymentMethod.jsp" >
	                        		<dsp:param name="paypalButtonEnable" value="${paypalButtonEnable}"/>
						               <dsp:param name="paypalOn" value="${paypalOn}"/>
						            </dsp:include>    
						            <div id="spcIsOrderAmtCovered" class="hidden" data-orderamtcovered="${isOrderAmtCoveredVar}"></div>
                           </c:when>
                           <c:otherwise>
 				                 	<div id="spcIsOrderAmtCovered" class="hidden" data-orderamtcovered="${isOrderAmtCoveredVar}"></div>            
                           </c:otherwise>
                        </c:choose>


                        <%--     Error Messages  starts --%>
			               <dsp:getvalueof param="payPalError" var="payPalError"/>
								<c:if test = "${payPalError}">
									<div class="error paypalError"><bbbl:error key="err_paypal_get_service_fail" language="${pageContext.request.locale.language}" /></div>
								</c:if>
		                  
		                  <%--R2.2 PayPal Change: Display error message in case of webservice error : Start--%>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param bean="CartModifierFormHandler.errorMap" name="array" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="errorCode" param="key" />
											<dsp:getvalueof var="errorMsg" bean="CartModifierFormHandler.errorMap.${errorCode}"/>
										</dsp:oparam>
								</dsp:droplet>
							
								<div class="error">${errorMsg}</div>
							
								<%--R2.2 PayPal Change: Display error message in case of webservice error : End--%>
		                    <dsp:include page="/global/gadgets/errorMessage.jsp" >
		                         <dsp:param name="formhandler" bean="PaymentGroupFormHandler"/>
		                    </dsp:include>
		                    <dsp:include page="/global/gadgets/errorMessage.jsp" >
		                         <dsp:param name="formhandler" bean="CommitOrderFormHandler"/>
		                    </dsp:include>
		                    <%--     Error Messages  ends --%>

                        <c:if test="${ValueLinkOn}">    
                            <dsp:include page="giftcard/billing_payment_giftcardfrag.jsp" />
                        </c:if>
                        
                        <%-- #Scope 83 A Start R2.2 pay pal button dependeding upon its flag in bcc   --%>
                              
                        <c:set var="showPaymentOptions" value="${false}" />
                        <c:if test = "${not isOrderAmtCoveredVar && showPaymentOptions}"> 
                           <c:if test="${paypalButtonEnable}"> 
                             	<c:choose>
											<c:when test="${paypalOn}">
												<c:choose >
													<c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
														<div id="paymentRadioGroup" class="marTop_10">
															<div class="radioItem noBorder clearfix"> 
																<div class="radio"> 
																	 <input id="paypalPaymentOpt" type="radio" style="opacity: 0;" name="paymentOpt" value="paypal" aria-hidden='false' disabled="disabled"/> 
																	 
																</div> 
																<div class="label"> 															
																	<span class="fl"><bbbt:textArea key="txt_spc_paypal_disable_image" language ="${pageContext.request.locale.language}"/></span>
																	<div class="fl block padLeft_5 padTop_5">
																		<label class="sectionHeading noBorder" for="paypalPaymentOpt"><bbbl:label key="lbl_spc_payment_section_paypal" language="${pageContext.request.locale.language}"/>
																		<span class="textPayPal"><bbbl:label key="lbl_spc_paypal_title" language="${pageContext.request.locale.language}"/> </span> 
																		</label>
																		<a href="<bbbl:label key="paypal_static_page" language="${pageContext.request.locale.language}"/>" class="newOrPopup modalWindow" title="<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>">
														  		 			<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>
														  		 		</a>
																		<div class="error"><bbbl:label key="lbl_spc_paypal_not_avilable_bopus_hybrid" language="${pageContext.request.locale.language}" /></div>
																	</div>
																	<span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged_paypal" language="${pageContext.request.locale.language}"/> 
																		 <dsp:droplet name="CurrencyFormatter">
																			<dsp:param name="currency" value="${priceValue}"/>
																			<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																			<dsp:oparam name="output">
																				<dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
																			</dsp:oparam>
																		</dsp:droplet>
																	</span>
																</div> 
															</div>

															<div class="radioItem noBorder clearfix"> 
																<div class="radio">
																	<span><input  checked="checked" id="creditCartPaymentOpt" type="radio" name="paymentOpt" style="opacity: 0;" value="creditCard" aria-hidden='false'/></span>							
																</div>
																<div class="label"> 
																		<label class="sectionHeading noBorder" for="creditCartPaymentOpt"><bbbl:label key="lbl_spc_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'BedBathCanada'}"><sup>${placeHolderMap.creditCardDisclaimer}</sup></c:if></label>
																	<span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged" language="<c:out param='${language}'/>"/> 
																			<dsp:droplet name="CurrencyFormatter">
																				<dsp:param name="currency" value="${priceValue}"/>
																				<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																				<dsp:oparam name="output">
																					<dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
																				</dsp:oparam>
																			</dsp:droplet>
																	</span>
																</div>
															</div>
														</div>
													</c:when>
													<c:otherwise>													
													   <div id="paymentRadioGroup" class="marTop_10">
															<div class="radioItem noBorder clearfix"> 
																<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
																<c:choose>
																	<c:when test="${payPalOrder eq true}">
																		<div class="radio"> 
																			<input id="paypalPaymentOpt" type="radio" style="opacity: 0;" name="paymentOpt" value="paypal" checked="true" aria-hidden='false'/> 
																			
																		</div> 
																	</c:when>
																	<c:otherwise>
																		<div class="radio"> 
																			<input id="paypalPaymentOpt" type="radio" style="opacity: 0;" name="paymentOpt" value="paypal" aria-hidden='false'/> 
																			
																		</div> 
																	</c:otherwise>
																</c:choose>
																<div class="label"> 
																	<img src="<bbbt:textArea key="txt_spc_paypal_image" language ="${pageContext.request.locale.language}"/>" aria-hidden='true'>
																	 <label class="sectionHeading noBorder" for="paypalPaymentOpt"><bbbl:label key="lbl_spc_payment_section_paypal" language="${pageContext.request.locale.language}"/> <span class="textPayPal"><bbbl:label key="lbl_spc_paypal_title" language="${pageContext.request.locale.language}"/> </span></label>
																
						                                            <a href="<bbbl:label key="paypal_static_page" language="${pageContext.request.locale.language}"/>" class="newOrPopup modalWindow" title="<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>">
													  		 			<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>
													  		 		</a>
																	<span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged_paypal" language="${pageContext.request.locale.language}"/> 
																		 <dsp:droplet name="CurrencyFormatter">
																			<dsp:param name="currency" value="${priceValue}"/>
																			<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																			<dsp:oparam name="output">
																				<dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
																			</dsp:oparam>
																		</dsp:droplet>
																	</span>
																</div> 
															</div>

															<div class="radioItem noBorder clearfix"> 
																<c:choose>
																	<c:when test="${payPalOrder ne true}">
																		<div class="radio">
																		<span><input  checked="checked" id="creditCartPaymentOpt" type="radio" name="paymentOpt" style="opacity: 0;" value="creditCard" aria-hidden='false'/></span>
																		</div>
																	</c:when>
																	<c:otherwise>
																		<div class="radio">
																			<span><input id="creditCartPaymentOpt" type="radio" name="paymentOpt" style="opacity: 0;" value="creditCard" aria-hidden='false'/></span>
																		</div>
																	</c:otherwise>
																</c:choose>
																<div class="label"> 
																	<label class="sectionHeading noBorder" for="creditCartPaymentOpt"><bbbl:label key="lbl_spc_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'BedBathCanada'}"><sup>${placeHolderMap.creditCardDisclaimer}</sup></c:if>
																	</label> 
																	<span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged" language="<c:out param='${language}'/>"/> 
																		<dsp:droplet name="CurrencyFormatter">
																			<dsp:param name="currency" value="${priceValue}"/>
																			<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																			<dsp:oparam name="output">
																				<dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
																			</dsp:oparam>
																		</dsp:droplet>
																	</span>
																</div>
															</div>
														</div>
													</c:otherwise>
												</c:choose>          
                                	</c:when>
                               	<c:otherwise>									
									 			<div id="paymentRadioGroup" class="marTop_10">
                                 		<div class="radioItem noBorder clearfix"> 
	                                       <div class="radio"> 
	                                          <input id="paypalPaymentOpt" type="radio" style="opacity: 0;" name="paymentOpt" value="paypal" disabled="disabled" aria-hidden='false'/>				
	                                       </div> 
		                                    <div class="label"> 		                                            
															<span class="fl"><bbbt:textArea key="txt_spc_paypal_disable_image" language ="${pageContext.request.locale.language}"/></span>
															<span class="fl block padLeft_5 padTop_5">
		                                       
		                                       <label class="sectionHeading noBorder" for="paypalPaymentOpt"><bbbl:label key="lbl_spc_payment_section_paypal" language="${pageContext.request.locale.language}"/> <span class="textPayPal"><bbbl:label key="lbl_spc_paypal_title" language="${pageContext.request.locale.language}"/> </span></label>
		                                       
		                                       <a href="<bbbl:label key="paypal_static_page" language="${pageContext.request.locale.language}"/>" class="newOrPopup modalWindow" title="<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>">
									  		 					<bbbl:label key="lbl_spc_what_is_paypal" language="${pageContext.request.locale.language}"/>
									  		 				</a>
													
															<div class="error"><bbbl:label key="lbl_spc_paypal_not_available" language="${pageContext.request.locale.language}" /></div></span>
		                                           
		                                         <span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged_paypal" language="${pageContext.request.locale.language}"/> 
		                                                 <dsp:droplet name="CurrencyFormatter">
		                                                    <dsp:param name="currency" value="${priceValue}"/>
		                                                    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
		                                                    <dsp:oparam name="output">
		                                                        <dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
		                                                    </dsp:oparam>
		                                                </dsp:droplet>
		                                            </span>
		                                    </div>
                                    	</div>
													
													<%-- MODAL FOR PAYPAL BUTTON --%>
													<div id="payPalInfo" style="z-index: 1002; outline: 0px none; height: auto; width: 440px; top:100px; left: 229px; display: none;" class="ui-dialog ui-widget ui-widget-content ui-corner-all changeStoreDialog" tabindex="-1" role="dialog" aria-labelledby="ui-dialog-title-changeStoreDialogWrapper">
			                                     <div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
			                                        <a href="#" class="ui-dialog-titlebar-close ui-corner-all" role="button" onclick="s_objectID=&quot;//bedbathandbeyond.com/store/product/Britto-Collection-by-Heys-New-Day-Hardside-Luggage/1246_31&quot;;return this.s_oc?this.s_oc(e):true">
			                                       <span class="ui-icon ui-icon-closethick"><bbbl:label key="lbl_close_small" language="${pageContext.request.locale.language}" /></span></a>
			                                   	 </div>
		
				                                  <div class="clearfix ui-dialog-content ui-widget-content" id="changeStoreDialogWrapper" style="display: block; width: auto; min-height: 25px; height: auto;" scrolltop="0" scrollleft="0"> 
				                                     <div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix" style="visibility: visible;">
													         	<div class="ui-dialog-buttonset" style="display:none;">
													             <button type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" aria-disabled="false">
													             <span class="ui-button-text"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}" /></span>
													             </button>
											                 </div>
				                                   	</div>
				                                </div>
		                            		</div>

			                              <div class="radioItem noBorder clearfix"> 
			                                 <div class="radio">
			                                 	<span><input  checked="checked" id="creditCartPaymentOpt" type="radio" name="paymentOpt" style="opacity: 0;" value="creditCard" aria-hidden='false'></span>		 	 
			                                 </div>
			                                 
			                                 <div class="label">
			                                 	<label class="sectionHeading noBorder" for="creditCartPaymentOpt"><bbbl:label key="lbl_spc_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'BedBathCanada'}"><sup>${placeHolderMap.creditCardDisclaimer}</sup></c:if></label>
				                                 <span class="amountCharged marTop_10 marLeft_20"><bbbl:label key="lbl_spc_payment_amtCharged" language="<c:out param='${language}'/>"/> 
				                                         <dsp:droplet name="CurrencyFormatter">
				                                             <dsp:param name="currency" value="${priceValue}"/>
				                                             <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
				                                             <dsp:oparam name="output">
				                                                 <dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
				                                             </dsp:oparam>
				                                         </dsp:droplet>
				                                 </span>
			                                 </div>
			                              </div>
                                		</div>
											</c:otherwise>
                               </c:choose>
                           </c:if>   
                        </c:if>   
                        
                        <%-- #Scope 83 A end R2.2 pay pal button dependeding upon its flag in bcc   --%>
                        <%-- pay with a credit card section start--%>
                        <dsp:form name="frmAddCreditCard" iclass="flatform clearfix" id="frmAddCreditCard" formid="frmAddCreditCard" action="${pageContext.request.requestURI}" method="post">   
                           <c:choose>
                           	<c:when test="${isOrderAmtCoveredVar and paypalButtonEnable and paypalOn}">
                           		<c:set var="lbl_continue_to_paypal" scope="page">
										   	<bbbl:label key="lbl_spc_continue_to_paypal" language="${pageContext.request.locale.language}" />
											</c:set>
											<%--
                             		<div class="clearfix padTop_20  marTop_20">
												<div class="button button_active button_disabled">
													<dsp:a href="javascript:void(0);" iclass="checkoutPaypal">${lbl_continue_to_paypal}
														<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/checkout_single.jsp" priority="10"/>
														<dsp:property bean="CartModifierFormHandler.checkoutSPWithPaypal" value="true" priority="0"/>
													</dsp:a>
												</div>
								 			</div>
                                 <p class="marTop_10"><bbbl:label key="lbl_spc_gift_card_cover_total_paypal" language="<c:out param='${language}'/>"/></p>
                                 <input type="hidden" name="IsOrderAmtCoveredByGC" value="${isOrderAmtCoveredVar}"/>
                                   	<div class="button button_active button_disabled">
                                        <c:set var="lbl_button_next" scope="page">
                                            <bbbl:label key="lbl_spc_button_next" language="${pageContext.request.locale.language}" />
                                        </c:set>
                                        <dsp:input type="submit" value="${lbl_button_next}" disabled="true" iclass="enableOnDOMReady" bean="PaymentGroupFormHandler.payment" id="paymentNextBtn">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
                                    </div>
                                    --%>
                             	</c:when>
                             	<c:otherwise>
                             		<c:if test="${paypalButtonEnable and paypalOn}">    
                                		<div class="payWithPayPal  hidden"> 
                                			 <c:set var="productIds" scope="request"/>
													<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
													<dsp:param name="order" bean="ShoppingCart.current"/>
												 		<dsp:oparam name="output">
															<dsp:getvalueof var="productIds" param="commerceItemList" />
										        		</dsp:oparam>     
										    		</dsp:droplet> 
	                                			<c:set var="lbl_continue_to_paypal" scope="page">
											   			<bbbl:label key="lbl_spc_continue_to_paypal" language="${pageContext.request.locale.language}" />
														</c:set>
                                       
														<dsp:a href="" iclass="button-Med btnPrimary checkoutPaypal" onclick="javascript:omniSubmitPayment('${productIds}')">${lbl_continue_to_paypal}
															<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/checkout_single.jsp" priority="10"/>
															<dsp:property bean="CartModifierFormHandler.checkoutSPWithPaypal" value="true" priority="0"/>
														</dsp:a>

														<span >
	                                          <bbbl:label key="lbl_spc_amount_pay_paypal" language="${pageContext.request.locale.language}" />
	                                          <dsp:droplet name="CurrencyFormatter">
	                                              <dsp:param name="currency" value="${priceValue}"/>
	                                              <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
	                                              <dsp:oparam name="output">
	                                                  <dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
	                                              </dsp:oparam>
	                                          </dsp:droplet>                                          
	                                      	</span> 
                                      	
										 			
                                		</div>     
                            		</c:if>                      

                            		<div class="grid_8 alpha omega payWithCreditCard hidden">
                                		<c:if test="${not paypalButtonEnable}">    
                                			<h3 class="sectionHeading noBorder"><bbbl:label key="lbl_spc_payment_sectionTitle" language="<c:out param='${language}'/>"/></h3>
                                		</c:if> 
                                		<%-- For logged in user and any credit card in order or profile --%>
                                
                                   	<dsp:droplet name="Switch">
                                       <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
                                       <dsp:oparam name="false">
	                                        
	                                            <span>
	                                                <bbbl:label key="lbl_spc_payment_amtCharged" language="<c:out param='${language}'/>"/>
	    
	                                                <dsp:droplet name="CurrencyFormatter">
	                                                    <dsp:param name="currency" value="${priceValue}"/>
	                                                    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
	                                                    <dsp:oparam name="output">
	                                                        <dsp:valueof param="formattedCurrency"><bbbl:label key="lbl_gc_label3" language="${pageContext.request.locale.language}" /></dsp:valueof>
	                                                    </dsp:oparam>
	                                                </dsp:droplet>                                          
	                                            </span>
	                                       <c:if test="${not paypalButtonEnable}">
	                                       </c:if> 
	                                     
	                                       <dsp:droplet name="GetCreditCardsForPayment">
	                                            <dsp:param name="Profile" bean="Profile" />
	                                            <dsp:param name="Order" bean="ShoppingCart.current" />
	                                            <dsp:param name="CreditCardContainer" bean="BBBCreditCardContainer" />
	                                            <dsp:param name="elementName" value="creditCardContainer" />
	                                            
	                                            <dsp:oparam name="output">
	                                                <dsp:getvalueof var="containerMap" bean="BBBCreditCardContainer.creditCardMap.empty" />
	                                            </dsp:oparam>
	                                       </dsp:droplet>                                        
                                       
                                       	<c:choose>
                                            	<c:when test="${containerMap != 'false'}">
                                            		
                                                <c:import url="/checkout/singlePage/payment/billing_payment_new_user.jsp" />
                                            	</c:when>
                                            	<c:otherwise>
                                                <h3 class="sectionHeading noBorder marTop_20"><bbbl:label key="lbl_spc_payment_savedProfile" language="<c:out param='${language}'/>"/></h3>
                                                <c:if test="${param.fromCardinalError eq 'true'}">
													<div class="customError paymentSPCError"><bbbe:error key='err_caridinal_payment_error' language='${language}'/></div>
											    </c:if>
                                                <div class=" fl">
                                                   <div class="clearfix">
                                                      <%-- Start of pay with a credit card Header --%>
                                                      <div class="productsListHeader noBorderTop">
                                                         <div class="cardName marLeft_10">Card</div>
                                                         <div class="grid_2 textLeft"><bbbl:label key="lbl_spc_payment_nameOnCard" language="<c:out param='${language}'/>"/></div>
                                                         <div class="grid_1 textLeft omega"><bbbl:label key="lbl_spc_payment_expires" language="<c:out param='${language}'/>"/></div>
                                                         <div class="grid_2 textRight omega"><bbbl:label key="lbl_spc_payment_cvv" language="<c:out param='${language}'/>"/></div>
                                                      </div>
                                                      <%-- End of pay with a credit card Header --%>
                                                        
                                                      <%-- pay with a credit card grid start --%>
                                                      <div id="frmPayUsingSavedCard">
                                                            <fieldset class="radioGroup">
                                                                <legend class="hidden"><bbbl:label key="lbl_spc_payment_savedCard" language="<c:out param='${language}'/>"/></legend>
                                                                <div class="cartProducts payUsingSavedCards">
                                                                    <dsp:droplet name="GetCreditCardsForPayment">
                                                                        <dsp:param name="Profile" bean="Profile" />
                                                                        <dsp:param name="Order" bean="ShoppingCart.current" />
                                                                        <dsp:param name="CreditCardContainer" bean="BBBCreditCardContainer" />
                                                                        <dsp:param name="elementName" value="creditCardContainer" />
                                                                        
                                                                        <dsp:oparam name="output">
                                                                            <dsp:getvalueof var="selectedId" param="selectedId" />
                                                                            <dsp:droplet name="ForEach">
                                                                                <dsp:param name="array" bean="BBBCreditCardContainer.creditCardMap" />
                                                                                
                                                                                <dsp:oparam name="output">
                                                                                    <c:set var="checkedFlag" value="false" />
                                                                                    <c:set var="disabledFlag" value="false" />
                                                                                    <dsp:getvalueof var="key" param="key" />
                                                                                    <dsp:getvalueof var="expired" bean="BBBCreditCardContainer.creditCardMap.${key}.expired" />
                                                                                        <div class="prevCardInfo">
                                                                                            <div class="cardInfoContainer radioItem input clearfix">
                                                                                                <div class="cardName">
                                                                                                    <div class="radio">
                                                                                                        <c:if test="${key == selectedId}">
                                                                                                            <c:set var="checkedFlag" value="true" />
                                                                                                        </c:if>
                                                                                                        <c:if test="${expired == true}">
                                                                                                            <c:set var="disabledFlag" value="true" />
                                                                                                        </c:if>
                                                                                                        <dsp:input type="radio" value="${key}" bean="PaymentGroupFormHandler.selectedCreditCardId" name="selectedCreditCardId" id="${key}" checked="${checkedFlag}"  disabled="${disabledFlag}">
                                                                                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                                                        </dsp:input>
                                                                                                    </div>
                                                                                                    <div class="label">
                                                                                                        <label for="${key}"> 
                                                                                                            <span>
                                                                                                                <dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.creditCardType" />&nbsp;ending&nbsp;in&nbsp;<dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.lastFourDigits" />
                                                                                                            </span> 
                                                                                                        </label>
                                                                                                    </div>
                                                                                                </div>
                                                                                                <div class="grid_2 textLeft breakWord">
                                                                                                    <div class="label">
                                                                                                        <label for="${key}"> 
                                                                                                            <span>
                                                                                                                <dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.nameOnCard" />
                                                                                                            </span> 
                                                                                                        </label>
                                                                                                    </div>
                                                                                                </div>
                                                                                                <div class="grid_1 textLeft omega">
                                                                                                    <div class="label">
                                                                                                        <label for="${key}"> 
                                                                                                            <span>
                                                                                                                <c:choose>
                                                                                                                    <c:when test="${expired == true}">
                                                                                                                        expired
                                                                                                                    </c:when>
                                                                                                                    <c:otherwise>
                                                                                                                        <dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.expirationMonth" />/<dsp:valueof 
                                                                                                                        bean="BBBCreditCardContainer.creditCardMap.${key}.expirationYear" />
                                                                                                                    </c:otherwise>
                                                                                                                </c:choose>
                                                                                                            </span> 
                                                                                                        </label>
                                                                                                    </div>
                                                                                                </div>
                                                                                                <c:if test="${disabledFlag != true}">
                                                                                                    <dsp:getvalueof var="uiCreditCardType" bean="BBBCreditCardContainer.creditCardMap.${key}.creditCardType" />
                                                                                                    <div class="cvvPayUsingSavedCardContainer">
                                                                                                        <div class="grid_2 fr textRight omega input cvvPayUsingSavedCard subForm">
                                                                                                            
																																				<div class="whatsThis">                                                                                                
					                                                                                             	<span class="icon-fallback-text"> 
					                                                                                                   <a class="info icon icon-question-circle" id="cvvInfo"  aria-hidden="true">                                                                                                      
					                                                                                                      <span><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span> 
					                                                                                                   </a>
					                                                                                                   <span class="icon-text"><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span>
					                                                                                                </span>
					                                                                                             </div> 
                                                                                                            <div class="text grid_1 text grid_1 fr marRight_20 fr">
                                                                                                                <dsp:input id="securityCodeCVV${key}" type="text" name="securityCodeCVV" iclass="securityCodeCVV ${uiCreditCardType}" maxlength="4" bean="PaymentGroupFormHandler.verificationNumber" autocomplete="off" >
                                                                                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                                    <dsp:tagAttribute name="aria-labelledby" value="securityCodeCVV${key} cvvInfo errorsecurityCodeCVV${key}"/>
                                                                                                                </dsp:input>
                                                                                                            </div>
                                                                                                           
                                                                                                            
                                                                                                            
	                                                                                                        <label id="errorsecurityCodeCVV${key}" for="securityCodeCVV${key}" generated="true" class="error fl width_2"></label>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:if>
                                                                                                    
                                                                                                <%--
                                                                                                <div class="grid_2 textRight omega input">
                                                                                                    <div class="text width_1 fr">
                                                                                                        <dsp:input id="${key}" type="text" name="${key}" iclass="textRight" maxlength="4" bean="PaymentGroupFormHandler.verificationNumber" disabled="true" />
                                                                                                    </div>
                                                                                                </div>
                                                                                                --%>
                                                                                            </div>
                                                                                        </div>
                                                                                </dsp:oparam>
                                                                            </dsp:droplet>
                                                                        </dsp:oparam>
                                                                    </dsp:droplet>
                                                                    
                                                                    <div class="addNewCardContainer">
                                                                        <div id="addNewCardContainer" class="radioItem input clearfix">
                                                                            <div class="grid_3 alpha">
                                                                                <div class="radio">
                                                                                    <dsp:input type="radio" value="New" bean="PaymentGroupFormHandler.selectedCreditCardId" name="selectedCreditCardId" id="selectedCreditCardId" >
                                                                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                                         <dsp:tagAttribute name="aria-hidden" value="false"/>
                                                                                    </dsp:input>
                                                                                </div>
                                                                                <div class="label">
                                                                                    <label for="selectedCreditCardId"> <span><bbbl:label key="lbl_spc_payment_newCard" language="<c:out param='${language}'/>"/></span> </label>
                                                                                </div>
                                                                            </div>
                                                                            <div class="grid_3 textCenter">
                                                                                <div class="label">
                                                                                    <label for="selectedCreditCardId"> <span>&nbsp;</span> </label>
                                                                                </div>
                                                                            </div>
                                                                            <div class="grid_2 textRight omega">
                                                                                <div class="label">
                                                                                    <label for="selectedCreditCardId"> <span>&nbsp;</span> </label>
                                                                                </div>
                                                                            </div>
                                                                            <%--Add credit card form start--%>
                                                                            <div class="clear"></div>
                                                                            <div class="subForm clearfix grid_8 hidden">
                                                                                <fieldset>
                                                                                		<dsp:input type="hidden" id="cardType" name="cardType" bean="PaymentGroupFormHandler.creditCardInfo.creditCardType" />


                                                                                    <div class="input_wrap text grid_4  marBottom_20">
																													<label id="lblcardNumber" for="cardNumber" class="popUpLbl"> 
																														<bbbl:label key="lbl_spc_payment_cardNumber" language="<c:out param='${language}'/>"/> 
																													</label>
																						    						<dsp:input bean="PaymentGroupFormHandler.creditCardInfo.creditCardNumber" type="text" name="cardNumber" id="cardNumber" maxlength="16" autocomplete="off">
																						        						<dsp:tagAttribute name="aria-required" value="true"/>
																						    							<dsp:tagAttribute name="aria-labelledby" value="lblcardNumber errorcardNumber"/>
																						    						</dsp:input>
                                                                                    </div>
                                                                                    <div class="cardType grid_3 omega"> 
                                                                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                                            <dsp:param name="array" bean="PaymentGroupFormHandler.creditCardTypes" />
                                                                                            <dsp:param name="elementName" value="cardlist" />
                                                                                            <dsp:oparam name="output">
                                                                                                <dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
                                                                                                <dsp:getvalueof id="cardName" param="cardlist.name" />
                                                                                                <dsp:getvalueof id="cardCode" param="cardlist.code" />
                                                    															<img class="creditCardIcon" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}" data-cardcode="${cardCode}">                                                                                                
                                                                                            </dsp:oparam>
                                                                                        </dsp:droplet>
                                                                                    </div>
                                                                                    <div class="input_wrap text grid_7 alpha marTop_20">
                                                                                            <label class="popUpLbl"><bbbl:label key="lbl_Exp_Month" language="${pageContext.request.locale.language}"/></label>
                                                                                        <div class="select grid_2 marRight_20">
                                                                                            <dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationMonth" id="creditCardMonth" name="creditCardMonth">
                                                                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                            <dsp:tagAttribute name="aria-labelledby" value="lblcreditCardMonth errorcreditCardMonth"/>
                                                                                                <dsp:option value=""><bbbl:label key="lbl_addcreditcard_month" language="${pageContext.request.locale.language}"/></dsp:option>
                                                                                                <dsp:option value="1">01</dsp:option>
                                                                                                <dsp:option value="2">02</dsp:option>
                                                                                                <dsp:option value="3">03</dsp:option>
                                                                                                <dsp:option value="4">04</dsp:option>
                                                                                                <dsp:option value="5">05</dsp:option>
                                                                                                <dsp:option value="6">06</dsp:option>
                                                                                                <dsp:option value="7">07</dsp:option>
                                                                                                <dsp:option value="8">08</dsp:option>
                                                                                                <dsp:option value="9">09</dsp:option>
                                                                                                <dsp:option value="10">10</dsp:option>
                                                                                                <dsp:option value="11">11</dsp:option>
                                                                                                <dsp:option value="12">12</dsp:option>
                                                                                            </dsp:select>
                                                                                            <label id="errorcreditCardMonth" for="creditCardMonth" generated="true" class="error"></label>
                                                                                    </div>
                                                                                    <div class="select grid_2 marRight_20">
                                                                                        	<label class="popUpLbl"><bbbl:label key="lbl_Exp_Year" language="${pageContext.request.locale.language}"/></label>
                                                                                            <jsp:useBean id="now" class="java.util.Date" scope="request" />
                                                                                            <fmt:formatDate var="year_val" value="${now}" pattern="yyyy" />
                                                                                            <dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationYear" id="creditCardYear" name="creditCardYear">
                                                                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                <dsp:tagAttribute name="aria-labelledby" value="creditCardYear lblcreditCardYear errorcreditCardYear"/>
                                                                                                <dsp:option value="">${year}</dsp:option>
                                                                                                <dsp:option value="${year_val}">${year_val}</dsp:option>
                                                                                                <dsp:getvalueof var="cardMaxYear" bean="PaymentGroupFormHandler.creditCardYearMaxLimit"></dsp:getvalueof>
                                                                                                <c:forEach begin="0" end="${cardMaxYear-1}" step="1" varStatus="count">
                                                                                                    <dsp:option value="${year_val+count.count}">${year_val+count.count}</dsp:option>
                                                                                                </c:forEach>
                                                                                            </dsp:select>
                                                                                            <label id="errorcreditCardYear" for="creditCardYear" generated="true" class="error"></label>
                                                                                            <label id="lblcreditCardYear" for="creditCardYear" class="offScreen"> <bbbl:label key="lbl_spc_payment_expiresOn" language="<c:out param='${language}'/>"/> </label>
                                                                                    </div>
                                                                                    <div class="input grid_2 omega">
                                                                                                <label id="lblsecurityCode" for="securityCode" class="popUpLbl"> <bbbl:label key="lbl_spc_payment_securityCode" language="<c:out param='${language}'/>"/> </label>
                                                                                            <div class="text noMarBot">
                                                                                                <dsp:input bean="PaymentGroupFormHandler.creditCardInfo.cardVerificationNumber" type="text" name="securityCode" id="securityCode" maxlength="4" autocomplete="off" iclass="securityCodeCVV" >
                                                                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
                                                                                                </dsp:input>
                                                                                            </div>


																															<div class="whatsThis">                                                                                                
                                                                                             	<span class="icon-fallback-text"> 
                                                                                                   <a class="info icon icon-question-circle" id="cvvInfo"  aria-hidden="true">                                                                                                      
                                                                                                      <span><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span> 
                                                                                                   </a>
                                                                                                   <span class="icon-text"><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span>
                                                                                                </span>
                                                                                             </div>

                                                                                             <%--
                                                                                            <div class="whatsThis"><a class="info icon-question-circle"> <!-- <b><bbbl:label key="lbl_spc_payment_txt" language="<c:out param='${language}'/>"/></b> -->
                                                                                            	<b class="icon-question-circle"></b>
                                                                                            	<span><bbbl:textArea key="txt_payment_dynamicText" language="<c:out param='${language}'/>"/></span> </a>
                                                                                            </div>
                                                                                            --%>
	

                                                                                            <div class="cb">
                                                                                                <label id="errorsecurityCode" class="error" for="securityCode" generated="true"></label>
                                                                                                <label class="offScreen" for="securityCode"> <bbbl:label key="lbl_spc_payment_securityCode" language="<c:out param='${language}'/>"/> </label>
                                                                                            </div>
                                                                                    </div>
                                                                                    <div class="clear"></div>
                                                                                    <div class="input_wrap grid_4 alpha marTop_20">
                                                                                            <label id="lblnameCard" for="nameCard" class="popUpLbl"> <bbbl:label key="lbl_spc_payment_nameOnCard" language="<c:out param='${language}'/>"/> </label>
                                                                                            <dsp:input bean="PaymentGroupFormHandler.creditCardInfo.nameOnCard" type="text" name="nameCard" maxlength="61" id="nameCard" >
                                                                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                <dsp:tagAttribute name="aria-labelledby" value="lblnameCard errornameCard"/>
                                                                                            </dsp:input>
                                                                                    </div>
                                                                                    <dsp:droplet name="Switch">
                                                                                        <dsp:param name="value" bean="Profile.transient"/>
                                                                                        <dsp:oparam name="false">
                                                                                            <c:choose>
                                                                                                <c:when test="${internationalCCFlag ne 'true'}">
                                                                                                    <div class="input grid_6 alpha saveToAccountWrapper">
                                                                                                        <div class="checkbox fl">
                                                                                                            <dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" name="saveToAccount" id="saveToAccount" checked="true">
                                                                                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                                                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                                                                            </dsp:input>
                                                                                                        </div>
                                                                                                        <div class="label">
                                                                                                            <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_payment_saveToAct" language="<c:out param='${language}'/>"/></label>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <div class="input grid_6 alpha saveToAccountWrapper hidden">
                                                                                                        <div class="checkbox fl">
                                                                                                            <dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="true">
                                                                                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                                                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                                                                            </dsp:input>
                                                                                                        </div>
                                                                                                        <div class="label">
                                                                                                            <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_payment_saveToAct" language="<c:out param='${language}'/>"/></label>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </dsp:oparam>
                                                                                    </dsp:droplet>
                                                                                </fieldset>
                                                                                <div class="clear"></div>
                                                                            </div>
                                                                            <%--Add credit card form end--%>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </fieldset>
                                                        </div>
                                                        <%-- pay with a credit card grid end --%>
                                                    </div>
                                                </div>
                                            	</c:otherwise>
                                        	</c:choose>
                                        
                                        	<%--Billing address section start
                                        	<div class="grid_4 alpha billingAddress">
                                            <h3 class="noBorder">Billing Address</h3>
                                            <div id="billingAddressContainer" class="item marTop_5">
                                                <div class="name"> 
                                                    <span class="firstName">${billingAddress.firstName }</span>
                                                    <span class="middleName">${billingAddress.middleName }</span>
                                                    <span class="lastName">${billingAddress.lastName}</span>
                                                    <span class="company">${billingAddress.companyName }</span>
                                                </div>
                                                
                                                    
                                                    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                                        <dsp:param name="value" param="order.billingAddress.address2"/>
                                                        <dsp:oparam name="true">
                                                            <div class="street1">${billingAddress.address1}</div>
                                                        </dsp:oparam>
                                                        <dsp:oparam name="false">
                                                            <div class="street1">${billingAddress.address1}</div>
                                                            <div class="street2">${billingAddress.address2}</div>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                    
                                                                                                    
                                                
                                                <div class="cityStateZip">
                                                    <span class="city">${billingAddress.city}</span>, 
                                                    <span class="state">${billingAddress.state}</span>
                                                    <span class="zip">${billingAddress.postalCode}</span>
                                                </div>
                                                <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                                    <dsp:param name="value" param="order.billingAddress.countryName" />
                                                    <dsp:oparam name="false">
                                                        <c:choose>
                                                            <c:when test="${internationalCCFlag}">
                                                                <span class="countryName internationalCountry">${billingAddress.countryName}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="countryName hidden">${billingAddress.countryName}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:set var="countryName" value="${billingAdress.country}" />
                                                        <c:choose>
                                                            <c:when test="${countryName eq 'Canada'}">
                                                                <span class="country hidden">CA</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="country hidden">${billingAdress.country}</span>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                    </dsp:oparam>
                                                    <dsp:oparam name="true">
                                                        <c:choose>
                                                            <c:when test="${defaultCountry eq 'Canada'}">
                                                                <span class="country hidden">CA</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="country hidden">${defaultCountry}</span>
                                                            </c:otherwise>
                                                        </c:choose> 
                                                    </dsp:oparam>
                                                    
                                                </dsp:droplet>
                                            </div>
                                            <a href="#" title="Change" class="bold marTop_10 fl upperCase" id="changeBillingAddress">CHANGE</a> 
                                        	</div>
                                        	--%>
                                        	<%--Billing address section end--%>
                                    	</dsp:oparam>
                                    	<dsp:oparam name="true">
                                       	<p class="noMar"><bbbl:label key="lbl_spc_gift_card_covers_order_total" language="<c:out param='${language}'/>"/></p>
                                    	</dsp:oparam>
                                		</dsp:droplet>
                                
                                		<div class="grid_8 alpha omega hidden">
                                     	<dsp:droplet name="Switch">
                                        <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
                                        <dsp:oparam name="true">
                                            <input type="hidden" name="IsOrderAmtCoveredByGC" value="${isOrderAmtCoveredVar}"/>
                                        </dsp:oparam>
                                    	</dsp:droplet>
	                                    
	                                        <c:set var="lbl_button_next" scope="page">
	                                            <bbbl:label key="lbl_spc_button_next" language="${pageContext.request.locale.language}" />
	                                        </c:set>
	                                        <dsp:input type="submit" value="Apply" iclass="enableOnDOMReady button-Med btnPrimary" bean="PaymentGroupFormHandler.spPayment" id="paymentNextBtn">
	                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
	                                            <dsp:tagAttribute name="role" value="button"/>
	                                        </dsp:input>
	                                    
                                		</div>
                            		</div>
                            	</c:otherwise>
                            </c:choose>
                        </dsp:form>
                        <%--pay with a credit card section end--%>
                  </dsp:oparam>
               </dsp:droplet>
               <%-- end <dsp:droplet name="BBBPaymentGroupDroplet"> --%>
            </div>

            <%--
            <dsp:include page="/checkout/order_summary_frag.jsp">
                <dsp:param name="displayTax" value="true"/>
                <dsp:param name="PageType" value="Payment"/>
            </dsp:include>    
            --%>
        </div> 
      <%--    moving this droplet to line 449 as we need product ids in omniSubmitPayment function   --%> 
           <%-- <c:set var="productIds" scope="request"/>
					<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
				 		<dsp:oparam name="output">
							<dsp:getvalueof var="productIds" param="commerceItemList" />
		        		</dsp:oparam>     
		    		</dsp:droplet>  --%>
        <%-- end <div class="clearfix" id="paymentSection" role="main"> --%>
    
         
        	<%--    
        	<jsp:attribute name="footerContent">

 
        	
            <c:set var="productIds" scope="request"/>
					<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
				 		<dsp:oparam name="output">
							<dsp:getvalueof var="productIds" param="commerceItemList" />
		        		</dsp:oparam>     
		    		</dsp:droplet>
       
  			<c:set var="isCreditCardFail" value="${false}" scope="request"/>
	    	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param bean="CommitOrderFormHandler.errorMap" name="array" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="errorCode" param="key" />
					<c:if test="${errorCode eq 'err_checkout_cybersource_error' || errorCode eq 'err_checkout_creditcard_error'}">
						<c:set var="isCreditCardFail" value="${true}" scope="request"/>
                    </c:if>
				</dsp:oparam>
			</dsp:droplet>

           <script type="text/javascript">
                                                
                if (typeof s !== 'undefined') {
                    if(${isCreditCardFail}){
                                    s.pageName='error code cybersource error:' + document.location.href;
                                    s.pageType='errorPage';
                    }
                    else {
                                    s.pageName = 'Check Out>Payment';
                                    s.channel = 'Check Out';
                                    s.prop1 = 'Check Out';
                                    s.prop2 = 'Check Out';
                                    s.prop3 = 'Check Out';
                                    s.prop6='${pageContext.request.serverName}'; 
                                    s.eVar9='${pageContext.request.serverName}';
                                    s.events = "event10";
                                    s.products = '${productIds}';
                    }
                    var s_code = s.t();
                    if (s_code)document.write(s_code);
                }
            </script>
      	

        	</jsp:attribute>
    		--%>      
    
</dsp:page>