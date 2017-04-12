<dsp:page>

	<%-- Imports --%>
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
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<dsp:importbean bean="/com/bbb/commerce/common/BBBVBVSessionBean"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="state" value="${70}"/>
	<dsp:getvalueof var="order" bean="ShoppingCart.current"/>
	<%-- payment info --%>
	<dsp:getvalueof var="paymentMethod" value=""/>
	<dsp:droplet name="ForEach">
	<dsp:param name="array" param="order.paymentGroups"/>
	<dsp:param name="elementName" value="paymentGroup" />
	<dsp:oparam name="output">
	<dsp:getvalueof var="paymentMethod" param="paymentGroup.paymentMethod"/>
		</dsp:oparam>
	</dsp:droplet>
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="PAYMENT"/>
		</c:otherwise>
	</c:choose>
	<dsp:setvalue value="false" bean="PayPalSessionBean.fromPayPalPreview"/>
	<dsp:param name="order" bean="ShoppingCart.current" />

	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<c:set var="chooseCardType" scope="page">
		<bbbl:label key="lbl_choose_cardtype" language="${pageContext.request.locale.language}"/>
	</c:set>
	<c:set var="year" scope="page">
		<bbbl:label key="lbl_addcreditcard_year" language="${pageContext.request.locale.language}" />
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

	<div class="row checkout-panel hidden" id="paymentForm">

		<div class="small-12 columns">
			<h2 class="divider">
				How Would You Like to Pay for Your Items?
			</h2>
		</div>

		<%-- errors --%>
		<div class="small-12 columns backend-errors" id="payErrors">

			<%-- 258 - Verified by Visa - refresh back button story - display error message and reset the payment state --%>
			<dsp:getvalueof var="bBBVerifiedByVisaVO" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO"/>
			<c:if test="${bBBVerifiedByVisaVO ne null}">
				<dsp:getvalueof var="token" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.token"/>
				<dsp:getvalueof var="message" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.message"/>
				<c:if test="${token eq 'error_true'}">
					<div class="row errors">
						<div class="small-12 columns">
							<ul class="error">
								<li class="error">${message}</li>
							</ul>
						</div>
					</div>
				</c:if>
				<dsp:setvalue bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.token" value=""/>
			</c:if>

			<%-- PayPal Errors --%>
			<dsp:getvalueof param="payPalError" var="payPalError"/>
			<c:if test="${payPalError}">
				<div class="row errors">
					<div class="small-12 columns">
						<ul class="error">
							<li class="error">
								<bbbl:error key="err_paypal_get_service_fail" language="${pageContext.request.locale.language}" />
							</li>
						</ul>
					</div>
				</div>
			</c:if>

			<%-- R2.2 PayPal Change: Display error message in case of webservice error --%>
			<dsp:droplet name="ForEach">
				<dsp:param bean="CartModifierFormHandler.errorMap" name="array" />
				<dsp:oparam name="outputStart">
					<div class="row errors">
						<div class="small-12 columns">
							<ul class="error">
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:getvalueof var="errorCode" param="key" />
					<li class="error">
						<dsp:valueof bean="CartModifierFormHandler.errorMap.${errorCode}" />
					</li>
				</dsp:oparam>
				<dsp:oparam name="outputEnd">
							</ul>
						</div>
					</div>
				</dsp:oparam>
			</dsp:droplet>

			<%-- Gift Card Form Errors --%>
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="BBBGiftCardFormHandler"/>
			</dsp:include>

			<%-- Payment Form Errors --%>
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="PaymentGroupFormHandler"/>
			</dsp:include>
		</div>

		<div class="small-12 columns">

			<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" param="order.priceInfo"/>
			<c:if test="${orderPriceInfo == null}">
				<dsp:droplet name="RepriceOrderDroplet">
					<dsp:param value="ORDER_TOTAL" name="pricingOp"/>
				</dsp:droplet>
			</c:if>

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
					<dsp:getvalueof var="payAtRegisterTotal" value="${storeSubtotal+orderTotal}"/>
					
					<c:choose>
						<c:when test="${orderType eq 'BOPUS_ONLY'}">
							<c:set var="priceValue" value="${storeSubtotal}"/>
						</c:when>
						<c:otherwise>
							<c:set var="priceValue" value="${orderTotal - totalGCAmt}"/>
						</c:otherwise>
					</c:choose>

					<c:if test="${ValueLinkOn}"> 
						<div class="row">
							<div class="small-12 columns">
								<dsp:include page="giftcard/billing_payment_giftcardfrag.jsp"/>
							</div>
						</div>
					</c:if>
                    <c:if test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
                     <div class="small-12">
                          <bbbt:textArea key="txt_paypal_disable_image" language ="${pageContext.request.locale.language}"/>
                          <h3 class="inline">
                             <bbbl:label key="lbl_payment_section_paypal" language="${pageContext.request.locale.language}"/>
                             <bbbl:label key="lbl_paypal_title" language="${pageContext.request.locale.language}"/>
                          </h3>
                          <a href="<bbbl:label key="paypal_static_page" language="${pageContext.request.locale.language}"/>" class="newOrPopup modalWindow" title="<bbbl:label key="lbl_what_is_paypal" language="${pageContext.request.locale.language}"/>">
                              <bbbl:label key="lbl_what_is_paypal" language="${pageContext.request.locale.language}"/>
                          </a>
                          <p class="p-footnote"><bbbl:label key="lbl_paypal_not_avilable_bopus_hybrid" language="${pageContext.request.locale.language}" /></p>
                     </div>
                  </c:if>
					<dsp:form name="frmAddCreditCardMultiple" id="frmAddCreditCardMultiple" formid="frmAddCreditCardMultiple" action="${pageContext.request.requestURI}" method="post">

						<div class="row">

							<%-- Payment Method Radio Buttons --%>
							<div class="small-12 large-4 columns">
								<h3 class="checkout-title">Payment Method</h3>
								<c:choose>
									<c:when test="${paypalButtonEnable}"> 
										<c:choose>
											<c:when test="${paypalOn and not ((orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID'))}">
												<%-- paypal is enabled --%>
												     <label class="inline-rc radio gray-panel payment-type-trigger credit-card  <c:if test="${isOrderAmtCoveredVar eq true}">disabled</c:if>" for="creditCartPaymentOpt">
                                                        <c:choose>
                                                            <c:when test="${payPalOrder ne true}">
                                                            	<input id="creditCartPaymentOpt" type="radio" name="paymentOpt" value="creditCard" checked="checked" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input id="creditCartPaymentOpt" type="radio" name="paymentOpt" value="creditCard" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span></span>
                                                        <bbbl:label key="lbl_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'TBS_BedBathCanada'}"><!-- <sup>2</sup> --></c:if>
                                                        <%-- <div class="due-amount-creditcard">
                                                        	<bbbl:label key="lbl_payment_amtCharged" language="<c:out param='${language}'/>"/><span class="dueamount-creditcard"><dsp:valueof value="${priceValue}" converter="currency"/></span>
                                                        </div> --%>
                                                    </label>
													<c:choose>
														<c:when test="${isOrderAmtCoveredVar}">
															<label class="inline-rc radio gray-panel payment-type-trigger paypal disabled" for="paypalPaymentOpt">
														</c:when>
														<c:otherwise>
															<label class="inline-rc radio gray-panel payment-type-trigger paypal" for="paypalPaymentOpt">
														</c:otherwise>
													</c:choose>
												
													<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
													<c:choose>
														<c:when test="${payPalOrder eq true}">
															<input id="paypalPaymentOpt" type="radio" name="paymentOpt" value="paypal" checked="checked" />
														</c:when>
														<c:otherwise>
															<input id="paypalPaymentOpt" type="radio" name="paymentOpt" value="paypal" />
														</c:otherwise>
													</c:choose>
													<span></span>
													<!-- pay with paypal text -->
													<bbbl:label key="lbl_payment_section_paypal" language="${pageContext.request.locale.language}"/> <bbbl:label key="lbl_paypal_title" language="${pageContext.request.locale.language}"/>
													 <%-- <div class="due-amount-paypal hidden">
                                                   		 <bbbl:label key="lbl_payment_amtCharged_paypal" language="${pageContext.request.locale.language}"/><span class="dueamount-paypal"> <dsp:valueof value="${priceValue}" converter="currency"/></span>
                                                    </div> --%>
												</label>
											</c:when>
											<c:otherwise>
												<label class="inline-rc radio gray-panel payment-type-trigger credit-card <c:if test="${isOrderAmtCoveredVar eq true}">disabled</c:if>" for="creditCartPaymentOpt">
													<input id="creditCartPaymentOpt" type="radio" name="paymentOpt" value="creditCard" checked="checked" />
													<span></span>
													<bbbl:label key="lbl_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'TBS_BedBathCanada'}"><!-- <sup>2</sup> --></c:if>
												</label>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<label class="inline-rc radio gray-panel payment-type-trigger credit-card" for="creditCartPaymentOpt">
											<input id="creditCartPaymentOpt" type="radio" name="paymentOpt" value="creditCard" checked="checked" />
											<span></span>
											<bbbl:label key="lbl_payment_sectionTitle" language="${pageContext.request.locale.language}"/><c:if test="${currentSiteId eq 'TBS_BedBathCanada'}"><!-- <sup>2</sup> --></c:if>
										</label>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${not empty paymentMethod and paymentMethod eq 'giftCard'}">
										<label class="inline-rc radio gray-panel disabled" for="registerPaymentOpt">
											<input id="registerPaymentOpt" type="radio" name="paymentOpt" value="register" disabled="disabled"/>
											<span></span>
											Pay at Register
										</label>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
												<label class="inline-rc radio gray-panel payment-type-trigger register hidden" for="registerPaymentOpt">
											</c:when>
											<c:otherwise>
												<label class="inline-rc radio gray-panel payment-type-trigger register" for="registerPaymentOpt">
													<input id="registerPaymentOpt" type="radio" name="paymentOpt" value="register" />
													<span></span>
													Pay at Register
		                                             <%-- <div class="due-amount-payatregister hidden">
		                                                 <bbbl:label key="lbl_payment_amtCharged_payatregister" language="${pageContext.request.locale.language}"/><span class="dueamount-payatregister"> <dsp:valueof value="${payAtRegisterTotal}" converter="currency"/></span>
		                                             </div> --%>
												</label>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</div>

							<%-- Payment Details --%>
							<c:choose>
								<c:when test="${isOrderAmtCoveredVar}">
									<div class="small-12 large-4 columns">
										<%-- <h3 class="checkout-title">Paypal Details</h3>
										<dsp:a href="javascript:void(0);" iclass="checkoutPaypal small button service expand">
											<bbbl:label key="lbl_continue_to_paypal" language="${pageContext.request.locale.language}" />
											<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/payment/billing_payment.jsp" priority="10"/>
											<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/checkoutType.jsp" priority="10"/>
											<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/>
										</dsp:a> --%>
										<p class="p-secondary"><bbbl:label key="lbl_gift_card_cover_total_paypal" language="<c:out param='${language}'/>"/></p>
										<input type="hidden" name="IsOrderAmtCoveredByGC" value="${isOrderAmtCoveredVar}"/>
									</div>
								</c:when>
								<c:otherwise>
									<div class="small-12 large-4 columns credit-card-details">
										<div class="payment-type credit-card <c:if test="${payPalOrder eq true}">hidden</c:if>">
											<h3 class="checkout-title">
												Credit Card Details
											</h3>

											<%-- For logged in user and any credit card in order or profile --%>
											<dsp:droplet name="Switch">
												<dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
												<dsp:oparam name="false">

													<%-- KP COMMENT START: don't see this in comps --%>
													<%--
													<c:if test="${not paypalButtonEnable}">
														<span>
															<bbbl:label key="lbl_payment_amtCharged" language="<c:out param='${language}'/>"/>
															<dsp:droplet name="CurrencyFormatter">
																<dsp:param name="currency" value="${priceValue}"/>
																<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																<dsp:oparam name="output">
																	<dsp:valueof param="formattedCurrency">no Total amount</dsp:valueof>
																</dsp:oparam>
															</dsp:droplet>
														</span>
													</c:if>
													--%>
													<%-- KP COMMENT END --%>

													<dsp:droplet name="GetCreditCardsForPayment">
														<dsp:param name="Profile" bean="Profile" />
														<dsp:param name="Order" bean="ShoppingCart.current" />
														<dsp:param name="CreditCardContainer" bean="BBBCreditCardContainer" />
														<dsp:param name="elementName" value="creditCardContainer" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="containerMap" bean="BBBCreditCardContainer.creditCardMap.empty" />
														</dsp:oparam>
													</dsp:droplet>

													<%-- view all saved credit cards --%>
													<c:if test="${containerMap eq 'false'}">
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
																		<label class="inline-rc radio gray-panel credit-card saved-card <c:if test="${expired == true}">disabled</c:if>" for="${key}">
																			<c:if test="${key == selectedId}">
																				<c:set var="checkedFlag" value="true" />
																			</c:if>
																			<c:if test="${expired == true}">
																				<c:set var="disabledFlag" value="true" />
																			</c:if>
																			<dsp:input type="radio" value="${key}" bean="PaymentGroupFormHandler.selectedCreditCardId" name="selectedCreditCardId" id="${key}" checked="${checkedFlag}"  disabled="${disabledFlag}">
																				<dsp:tagAttribute name="aria-checked" value="true"/>
																			</dsp:input>
																			<span></span>
																			<ul class="address">
																				<li>
																					<dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.creditCardType" />&nbsp;ending&nbsp;in&nbsp;<dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.lastFourDigits" />
																				</li>
																				<li>
																					<dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.nameOnCard" />
																				</li>
																				<li>
																					<c:choose>
																						<c:when test="${expired == true}">
																							expired
																						</c:when>
																						<c:otherwise>
																							<dsp:getvalueof var="expDate" bean="BBBCreditCardContainer.creditCardMap.${key}.expirationMonth" scope="page" />
																							Exp. <c:if test='${expDate lt 10}'><c:out value="0"/></c:if><c:out value="${expDate}" /><bbbl:label key="lbl_checkout_forward_slash" language="<c:out param='${language}'/>"/><dsp:valueof bean="BBBCreditCardContainer.creditCardMap.${key}.expirationYear" />
																						</c:otherwise>
																					</c:choose>
																				</li>
																			</ul>

																			<c:if test="${disabledFlag != true}">
																				<dsp:getvalueof var="uiCreditCardType" bean="BBBCreditCardContainer.creditCardMap.${key}.creditCardType" />
																				<div class="row">
																					<div class="small-12 columns cvvPayUsingSavedCard <c:if test="${key != selectedId}">hidden</c:if>">
																						<c:set var="placeholderText">
																							CVC
																						</c:set>
																						<c:choose>
                                                                                            <c:when test="${key != selectedId}">
                                                                                                <dsp:input disabled="disabled" id="securityCodeCVV${key}" type="text" name="securityCodeCVV" iclass="textRight securityCodeCVV ${uiCreditCardType}" maxlength="4" bean="PaymentGroupFormHandler.verificationNumber" autocomplete="off" >
                                                                                                    <dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
                                                                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                    <dsp:tagAttribute name="aria-labelledby" value="securityCodeCVV${key} cvvInfo errorsecurityCodeCVV${key}"/>
                                                                                                </dsp:input>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <dsp:input id="securityCodeCVV${key}" type="text" name="securityCodeCVV" iclass="textRight securityCodeCVV ${uiCreditCardType}" maxlength="4" bean="PaymentGroupFormHandler.verificationNumber" autocomplete="off" >
                                                                                                    <dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
                                                                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                                                                    <dsp:tagAttribute name="aria-labelledby" value="securityCodeCVV${key} cvvInfo errorsecurityCodeCVV${key}"/>
                                                                                                </dsp:input>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
																						<a href="#" data-reveal-id="cvvInfoModal" class="cvc-info">
																							<bbbl:label key="lbl_payment_txt" language="<c:out param='${language}'/>"/>
																						</a>
																						<label id="errorsecurityCodeCVV${key}" class="error" for="securityCodeCVV${key}" generated="true" class="error"></label>
																					</div>
																				</div>
																			</c:if>

																		</label>
																	</dsp:oparam>
																</dsp:droplet>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>

													<%-- new credit card form --%>
													<c:import url="/checkout/payment/billing_payment_new_user.jsp" />

												</dsp:oparam>
												<dsp:oparam name="true">
													<p class="p-secondary">
														<bbbl:label key="lbl_gift_card_covers_order_total" language="<c:out param='${language}'/>"/>
													</p>
												</dsp:oparam>
											</dsp:droplet>
										</div>
									</div>
								</c:otherwise>
							</c:choose>
							<div class="small-12 large-4 columns"></div>
						</div>

						<%-- Continue Button --%>
						<div class="row">
							<c:choose>
								<c:when test="${isOrderAmtCoveredVar and paypalButtonEnable and paypalOn}">
									<div class="small-12 large-offset-10 large-2 columns">
										<c:set var="lbl_button_next" scope="page">
											<bbbl:label key="lbl_button_next" language="${pageContext.request.locale.language}" />
										</c:set>
										<dsp:input type="submit" value="${lbl_button_next}" disabled="false" iclass="small button service expand" bean="PaymentGroupFormHandler.payment" id="paymentNextBtn">
											<dsp:tagAttribute name="aria-pressed" value="false"/>
											<dsp:tagAttribute name="role" value="button"/>
										</dsp:input>
									</div>
								</c:when>
								<c:otherwise>
									<c:if test="${paypalButtonEnable and paypalOn}">
										<div class="small-12 columns payment-type paypal <c:if test="${payPalOrder ne true}">hidden</c:if>">
											<dsp:a href="" iclass="checkoutPaypal small button service right">
												<bbbl:label key="lbl_continue_to_paypal" language="${pageContext.request.locale.language}" />
<%-- 											<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/payment/billing_payment.jsp" priority="10"/> --%>
											<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/checkout/checkoutType.jsp" priority="10"/>
												<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/>
											</dsp:a>
										</div>
									</c:if>
									<div class="small-12 large-2 columns payment-type credit-card <c:if test="${payPalOrder eq true}">hidden</c:if>">
										<dsp:droplet name="Switch">
											<dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
											<dsp:oparam name="true">
												<input type="hidden" name="IsOrderAmtCoveredByGC" value="${isOrderAmtCoveredVar}"/>
											</dsp:oparam>
										</dsp:droplet>
										<c:set var="lbl_button_next" scope="page">
											<bbbl:label key="lbl_button_next" language="${pageContext.request.locale.language}" />
										</c:set>
										<dsp:input type="submit" value="${lbl_button_next}" iclass="small button service right expand" bean="PaymentGroupFormHandler.payment" id="paymentNextBtn">
											<dsp:tagAttribute name="aria-pressed" value="false"/>
											<dsp:tagAttribute name="role" value="button"/>
										</dsp:input>
									</div>
								</c:otherwise>
							</c:choose>
						</div>

					</dsp:form>
					<%--pay with a credit card section end--%>

				</dsp:oparam>
			</dsp:droplet>

			<%-- pay at register --%>
			<dsp:form name="frmAddPayAtRegisterMultiple" iclass="payment-type register hidden" id="frmAddPayAtRegisterMultiple" formid="frmAddPayAtRegisterMultiple" action="" method="post">
				<div class="row">
					<div class="small-12 columns">
						<dsp:input type="submit" value="Pay At Register" iclass="small button service right" bean="PaymentGroupFormHandler.pARPayment" id="paymentNextBtn">
							<dsp:tagAttribute name="aria-pressed" value="false"/>
							<dsp:tagAttribute name="role" value="button"/>
						</dsp:input>
					</div>
				</div>
			</dsp:form>

		</div>
	</div>

	<%-- cvv info modal --%>
	<div id="cvvInfoModal" class="reveal-modal small" data-reveal>
		<h2><bbbl:label key="lbl_payment_securityCode" language="<c:out param='${language}'/>"/></h2>
		<p class="p-secondary"><bbbl:textArea key="txt_payment_dynamicText" language="<c:out param='${language}'/>"/></p>
		<a class="close-reveal-modal">&#215;</a>
	</div>

</dsp:page>
