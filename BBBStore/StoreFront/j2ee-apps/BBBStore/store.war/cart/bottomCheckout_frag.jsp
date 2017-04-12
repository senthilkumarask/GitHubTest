<dsp:importbean
	bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
<dsp:importbean
	bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet" />
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean
	bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler" />
<c:set var="lbl_checkout_checkout" scope="page">
	<bbbl:label key="lbl_checkout_checkout"
		language="${pageContext.request.locale.language}" />
</c:set>

<dsp:getvalueof var="showCheckOutBtn" param="showCheckOutBtn" />
<dsp:getvalueof var="itemNotOOS" param="itemNotOOS" />
<dsp:getvalueof var="isOutOfStock" param="isOutOfStock" />
<dsp:getvalueof var="countryCode" param="countryCode" />
<dsp:getvalueof var="currencyCode" param="currencyCode" />
<dsp:getvalueof var="orderHasPersonalizedItem"
	param="orderHasPersonalizedItem" />
<dsp:getvalueof var="orderHasErrorPrsnlizedItem"
	param="orderHasErrorPrsnlizedItem" />
<dsp:getvalueof var="orderHasIntlResterictedItem"
	param="orderHasIntlResterictedItem" />
<dsp:getvalueof var="singlePageEligible" param="singlePageEligible" />

<dsp:getvalueof var="isInternationalCustomer"
	param="isInternationalCustomer" />
<dsp:getvalueof var="paypalCartButtonEnable"
	param="paypalCartButtonEnable" />
<dsp:getvalueof var="isOrderAmtCoveredVar" param="isOrderAmtCoveredVar" />
<dsp:getvalueof var="paypalOn" param="paypalOn" />
<dsp:getvalueof var="registryFlag" param="registryFlag" />
<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
<dsp:getvalueof var="orderType" param="orderType" />
<dsp:getvalueof var="showCheckOutBtn" param="showCheckOutBtn" />
<dsp:getvalueof var="itemNotOOS" param="itemNotOOS" />

<dsp:getvalueof var="securityStatus" bean="Profile.securityStatus" />
<div id="bottomCheckoutButton" class="clearfix">

	<c:choose>
		<c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">

			<dsp:input bean="CartModifierFormHandler.fromCart" value="${true}"
				type="hidden" />
			<div
				class="button button_active button_active_orange btnCheckout <c:if test="${orderHasIntlResterictedItem || orderHasPersonalizedItem || orderHasErrorPrsnlizedItem}">button_disabled</c:if>">
				<c:choose>
					<c:when test="${not isInternationalCustomer}">
						<c:choose>
							<c:when test="${orderHasErrorPrsnlizedItem}">
								<dsp:input id="botCheckoutBtn"
									bean="CartModifierFormHandler.checkout" type="submit"
									value="${lbl_checkout_checkout}">
									<dsp:tagAttribute name="role" value="button" />
									<dsp:tagAttribute name="disabled" value="disabled" />
								</dsp:input>
							</c:when>
							<c:otherwise>
								<%-- --%>
								<c:choose>
									<c:when test="${singlePageEligible}">
										<div class="singlePageCheckout" id='SPCbottomCheckoutButton'>
											<dsp:input id="botCheckoutBtn"
												bean="CartModifierFormHandler.checkout" type="submit"
												value="${lbl_checkout_checkout}">
												<dsp:tagAttribute name="role" value="button" />
											</dsp:input>
										</div>
									</c:when>
									<c:otherwise>
										<dsp:input id="botCheckoutBtn"
											bean="CartModifierFormHandler.checkout" type="submit"
											value="${lbl_checkout_checkout}">
											<dsp:tagAttribute name="role" value="button" />
										</dsp:input>
									</c:otherwise>
								</c:choose>
								<%-- --%>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<%-- BBBH-391 | Client DOM XSRF
				 		<dsp:input bean="InternationalShipFormHandler.successURL" type="hidden" value="/store/checkout/envoy_checkout.jsp" />
				 		<dsp:input bean="InternationalShipFormHandler.errorURL" type="hidden" value="/store/cart/cart.jsp" /> --%>
						<dsp:input bean="InternationalShipFormHandler.fromPage"
							type="hidden" value="envoyCheckout" />

						<dsp:input bean="InternationalShipFormHandler.userSelectedCountry"
							type="hidden" value="${countryCode}" />
						<dsp:input
							bean="InternationalShipFormHandler.userSelectedCurrency"
							type="hidden" value="${currencyCode}" />
						<input name="internationalOrderType" type="hidden"
							value="internationalOrder" />
														
							 <dsp:getvalueof var="exceptionString" bean="InternationalShipFormHandler.exceptionString" />  
							 <dsp:getvalueof var="exceptionCode" bean="InternationalShipFormHandler.exceptionCode" />  
							  
															  
							 <div id="error" class="hidden">
								  <ul>
									<li id="tl_atg_err_code">${exceptionCode}
									<li id="tl_atg_err_value">${exceptionString}
								  </ul>
							 </div>                                
							
						<c:choose>
							<c:when
								test="${orderHasIntlResterictedItem || orderHasPersonalizedItem || orderHasErrorPrsnlizedItem}">
								<dsp:input id="botCheckoutBtn"
									bean="InternationalShipFormHandler.envoyCheckout" type="submit"
									value="${lbl_checkout_checkout}">
									<dsp:tagAttribute name="disabled" value="disabled" />
									<dsp:tagAttribute name="role" value="button" />
								</dsp:input>
							</c:when>
							<c:otherwise>
								<dsp:input id="botCheckoutBtn"
									bean="InternationalShipFormHandler.envoyCheckout" type="submit"
									value="${lbl_checkout_checkout}">
									<dsp:tagAttribute name="role" value="button" />
								</dsp:input>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<c:otherwise>
			<div
				class="button button_active button_active_orange button_disabled btnCheckout">
				<dsp:input id="botCheckoutBtn"
					bean="CartModifierFormHandler.checkout" type="submit"
					value="${lbl_checkout_checkout}">
					<dsp:tagAttribute name="disabled" value="disabled" />
					<dsp:tagAttribute name="role" value="button" />
				</dsp:input>
			</div>
		</c:otherwise>
	</c:choose>
	<dsp:droplet name="BBBContinueShoppingDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof var="linkURL" param="continue_shopping_url" />
			<c:set var="continueshoppinglink">
				<bbbl:label key="lbl_cartdetail_continueshoppinglink"
					language="<c:out param='${language}'/>" />
			</c:set>
			<div class="clear"></div>
			<dsp:a iclass="buttonTextLink marTop_10 noPadLeft" href="${linkURL}"
				title="${continueshoppinglink}">
				<bbbl:label key="lbl_cartdetail_continueshoppinglink"
					language="<c:out param='${language}'/>" />
			</dsp:a>
		</dsp:oparam>
	</dsp:droplet>

	<c:if
		test="${not (showCheckOutBtn and itemNotOOS and (not isOutOfStock))}">
		<div class="clearfix padTop_10 padBottom_10 cb">
			<span class="error"><bbbl:label
					key="lbl_cart_outofstockmessage"
					language="<c:out param='${language}'/>" /></span>
		</div>
	</c:if>

	<div class="clear"></div>
</div>

<div class="clear"></div>
<%-- #Scope 83 A : Start R2.2 pay pal button dependeding upon its flag in bcc   --%>
<dsp:getvalueof var="orderType"
	bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
<c:if test="${paypalCartButtonEnable and (not isInternationalCustomer)}">
	<c:choose>
		<c:when test="${paypalOn}">
			<c:choose>
				
				<c:when test="${orderHasLTLItem eq true || (registryFlag eq true) }">
					<div class="paypalCheckoutContainer paypal_disabled fl">
						<bbbt:textArea key="txt_paypal_disable_image_new"
							language="${pageContext.request.locale.language}" />
					</div>
					<div
						class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_10 disableText">
						<bbbl:label key="lbl_paypal_bill_me_later"
							language="${pageContext.request.locale.language}" />
					</div>
					<div class="clear"></div>
					<div class="marTop_15 smallText bold highlightRed">
						<bbbl:label key="lbl_paypal_not_available_ltl_or_reg_product"
							language="${pageContext.request.locale.language}" />
					</div>
				</c:when>
				<c:when test="${orderHasErrorPrsnlizedItem}">
					<div class="paypalCheckoutContainer paypal_disabled fl">
						<bbbt:textArea key="txt_paypal_disable_image_new"
							language="${pageContext.request.locale.language}" />
					</div>
					<div
						class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_10 disableText">
						<bbbl:label key="lbl_paypal_bill_me_later"
							language="${pageContext.request.locale.language}" />
					</div>
				</c:when>
				<c:when
					test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
					<div class="paypalCheckoutContainer paypal_disabled fl">
						<bbbt:textArea key="txt_paypal_disable_image_cart"
							language="${pageContext.request.locale.language}" />
					</div>
					<div
						class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
						<bbbl:label key="lbl_paypal_bill_me_later"
							language="${pageContext.request.locale.language}" />
					</div>
					<div class="clear"></div>
					<div class="error marTop_15">
						<bbbl:label key="lbl_paypal_not_avilable_bopus_hybrid"
							language="${pageContext.request.locale.language}" />
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when
							test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock) }">
							<c:choose>
								<c:when test="${isOrderAmtCoveredVar}">
									<!-- Remove Paypal Info if whole amount is covered from gift card  -->
									<dsp:getvalueof id="payPalOrder"
										bean="ShoppingCart.current.payPalOrder" />
									<c:if test="${payPalOrder}">
										<dsp:droplet
											name="/com/bbb/commerce/order/paypal/RemovePayPalInfoDroplet">
										</dsp:droplet>
									</c:if>

									<div class="paypalCheckoutContainer fl button_disabled">
										<img src="/_assets/global/images/paypal_disabled.png"
											role="image" class="paypalCheckoutButton"
											alt="paypal disabled" />
									</div>
									<div
										class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
										<bbbl:label key="lbl_paypal_bill_me_later"
											language="${pageContext.request.locale.language}" />
									</div>
									<div class="clear"></div>
									<div class="error marTop_15">
										<bbbe:error key="err_paypal_zero_balance"
											language="${pageContext.request.locale.language}" />
									</div>
								</c:when>
								<c:otherwise>
									<%--START:: change for defect no:BPS-439    --%>
									<div class="paypalCheckoutContainer fl <c:if test='${isInternationalCustomer eq true}'>paypal_disabled</c:if> ">

										<c:choose>
											<c:when test="${not empty securityStatus and securityStatus == 2}">
												<dsp:a href="">
														<dsp:property bean="ProfileFormHandler.redirectURL" value="${contextPath}/account/frags/pre_paypal.jsp"/>
														<dsp:property bean="ProfileFormHandler.removeItemsFromOrderAndLogOut" value="true"  />
														<img alt="Paypal checkout" role="button"
															src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>"
															class="paypalCheckoutButton">
												</dsp:a>
											</c:when>
											<c:otherwise>
												<dsp:a href="">
													<dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8" />
													<dsp:property bean="CartModifierFormHandler.payPalErrorURL"	value="/cart/cart.jsp" priority="9" />
													<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal"	value="true" priority="0" />
														<img alt="Paypal checkout" role="button"
																src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>"
																	class="paypalCheckoutButton">
												</dsp:a>
											</c:otherwise>
										</c:choose>
									</div>
									<%--END:: change for defect no:BPS-439    --%>
									</div>
									<div class="fl marLeft_10 billMeLaterLink marTop_15">
										<bbbl:label key="lbl_paypal_bill_me_later"
											language="${pageContext.request.locale.language}" />
									</div>
								</c:otherwise>
							</c:choose>

						</c:when>
						<c:otherwise>
							<div class="paypalCheckoutContainer paypal_disabled fl">
								<bbbt:textArea key="txt_paypal_disable_image_cart"
									language="${pageContext.request.locale.language}" />
							</div>

							<div
								class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
								<bbbl:label key="lbl_paypal_bill_me_later"
									language="${pageContext.request.locale.language}" />
							</div>
							<div class="clear"></div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<div class="paypalCheckoutContainer paypal_disabled fl">
				<bbbt:textArea key="txt_paypal_disable_image_cart"
					language="${pageContext.request.locale.language}" />
			</div>
			<div
				class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
				<bbbl:label key="lbl_paypal_bill_me_later"
					language="${pageContext.request.locale.language}" />
			</div>
			<div class="clear"></div>
			<div class="error marTop_15">
				<bbbl:label key="lbl_paypal_not_available"
					language="${pageContext.request.locale.language}" />
			</div>
		</c:otherwise>
	</c:choose>
</c:if>

<%--  end R2.2 pay pal button dependeding upon its flag in bcc   --%>
