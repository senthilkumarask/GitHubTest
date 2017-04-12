<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/PromotionExclusionMsgDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSOverridenItemsDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean var="TBSStoreNumberFormHandler" bean="/com/bbb/selfservice/TBSStoreNumberFormHandler"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof param="order" var="order"/>
<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="totalStateTax" value="0" scope="request" />
	<c:set var="totalCountyTax" value="0" scope="request" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<dsp:droplet name="BBBPackNHoldDroplet">
		<dsp:param name="order" param="order"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="isPackHold" var="isPackHold"/>
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${isPackHold eq true}">
		<dsp:droplet name="BBBBeddingKitsAddrDroplet">
			<dsp:param name="order" param="order"/>
			<dsp:param name="shippingGroup" param="order.shippingGroups"/>
				<dsp:param name="isPackHold" value="${true}"/>
				<dsp:oparam name="beddingKit">
					<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="beddingKit" value="true"/>
			</dsp:oparam>
			<dsp:oparam name="weblinkOrder">
				<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="weblinkOrder" value="true"/>
			</dsp:oparam>
			<dsp:oparam name="notBeddingKit">
				<c:set var="beddingKit" value="false"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<c:if test="${beddingShipAddrVO != null && isPackHold eq true }">
		<dsp:getvalueof var="collegeName" value="${beddingShipAddrVO.collegeName}"/>
	</c:if>
	<c:set var="isPayPal" value="${false}"/>
	<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" param="order.priceInfo"/>
	<dsp:getvalueof var="billingAdd1" bean="ShoppingCart.current.billingAddress.address1" />
	<c:if test="${orderPriceInfo == null or not empty billingAdd1}">
		<dsp:droplet name="RepriceOrderDroplet">
			<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
	<dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>
	<dsp:droplet name="PromotionExclusionMsgDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="promoExclusionMap" vartype=" java.util.Map" param="promoExclusionMap" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="orderDate" param="orderDate"/>
	<c:if test="${isFromOrderDetail}">
		<dsp:droplet name="CartRegistryInfoDroplet">
			<dsp:param name="order" param="order"/>
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="order" param="order"/>
	<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber"/>
	<dsp:getvalueof var="bopusOrderNumber" param="order.bopusOrderNumber"/>
	<dsp:getvalueof var="hideOrderNumber" param="hideOrderNumber"/>

	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />

	<c:set var="showLinks" scope="request">
		<dsp:valueof param="showLinks"/>
	</c:set>
	<dsp:getvalueof id="paypal" param="paypal" />
	<dsp:getvalueof id="addCheck" param="addCheck" />
	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<div class="row checkout-panel <c:if test='${((not isConfirmation and not paypal) or (not addCheck and not isConfirmation))}'>hidden</c:if>" id="reviewForm">

		<%-- errors --%>
		<div class="small-12 columns backend-errors" id="previewErrors">
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="CommitOrderFormHandler"/>
			</dsp:include>
		</div>
				
		<div class="small-12 columns">

			<%-- TODO: store pickup --%>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
					<dsp:droplet name="Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="storePickupShippingGroup"/>
						<dsp:oparam name="equal">
							<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>

							<%-- Start get count of bbb commerceItem for StorePickup Shipping group --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<c:set var="bbbStoreItemcount" value="0" scope="page" />
								<dsp:droplet name="ForEach">
										<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
										<dsp:param name="elementName" value="commerceItemRelationship" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="quantityStoreItem" param="commerceItemRelationship.quantity"/>
											<c:set var="bbbStoreItemcount" value="${bbbStoreItemcount + quantityStoreItem}" scope="page" />
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
							<%-- End get count of bbb commerceItem for StorePickup Shipping group  --%>

							<c:if test="${empty showLinks}">
								<c:if test="${not empty bopusOrderNumber}">
									<div class="row">
										<div class="small-12 columns">
											<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber || not isFromOrderDetail}">
												<h3 class="checkout-title">
													<bbbl:label key="lbl_checkoutconfirmation_instorepickup_order" language="${language}"/> <strong>${bopusOrderNumber}</strong>
												</h3>
												<p>
													<bbbl:textArea key="txt_checkoutconfirmation_instorepickup_message" language="${language}"/>
												</p>
											</c:if>
											<c:if test="${isFromOrderDetail}">
												<h3 class="checkout-title">
													${bbbStoreItemcount} <bbbl:label key='lbl_item_to_be_picked_store' language='${pageContext.request.locale.language}' />
												</h3>
												<p>
													<bbbl:label key='lbl_trackorder_status' language='${pageContext.request.locale.language}' /> :
													<c:choose>
														<c:when test="${empty shippingGroup.stateDetail}">
															${order.stateAsString}
														</c:when>
														<c:otherwise>
															${shippingGroup.stateDetail}
														</c:otherwise>
													</c:choose>
												</p>
											</c:if>
										</div>
									</div>
								</c:if>
							</c:if>

							<%-- pick up at store --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<div class="row">
									<div class="small-12 columns">
										<div class="small-12 columns" id="sg_<dsp:valueof param='shippingGroup.id'/>">
											<dsp:include page="/checkout/preview/frag/order_store_pickup_info.jsp" flush="true">
												<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											</dsp:include>
											<dsp:include page="/checkout/preview/frag/multi_checkout_order_items.jsp" flush="true">
												<dsp:param name="shippingGroup" value="${shippingGroup}"/>
												<dsp:param name="order" param="order"/>
												<dsp:param name="isConfirmation" param="isConfirmation"/>
												<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
											</dsp:include>
											<dsp:include page="/checkout/preview/frag/store_pickup_shipping_group_order_summary.jsp" flush="true">
												<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											</dsp:include>
										</div>
									</div>
								</div>
							</c:if>

						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
			<%-- /in store pickup --%>

<%-- 			<dsp:getvalueof var="isFromOrderHistory" param="isFromOrderHistory"/> --%>
<%-- 			<c:if test="${isFromOrderHistory ne true}"> --%>
<%-- 				<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/> --%>
<%-- 				<c:if test="${not cartEmpty}"> --%>
					<dsp:form name="commit_form" id="checkoutReview" method="post" action="/tbs/checkout/checkoutType.jsp?shippingGr=multi">
						<div class="row">
							<%-- shipping review --%>
							<div class="small-12 columns" id="multiShippingReview">
								<dsp:include page="/checkout/shipping/multi_shipping_review.jsp" flush="true">
									<dsp:param name="shippingGroup" value="${shippingGroup}"/>
									<dsp:param name="order" param="order"/>
									<dsp:param name="isConfirmation" param="isConfirmation"/>
									<dsp:param name="isMultiShip" value="true"/>
								</dsp:include>
							</div>

							<div class="small-12 large-8 columns checkout-print-left">
								<div class="row">
									<div class="small-12 columns">
										<h2 class="divider">Billing Information</h2>
									</div>

									<%-- payment review --%>
									<div class="small-12 large-4 columns" id="multiPaymentReview">
										<dsp:include page="/checkout/payment/multi_payment_review.jsp" flush="true">
											<dsp:param name="order" param="order"/>
											<dsp:param name="isConfirmation" param="isConfirmation"/>
											<dsp:param name="isMultiShip" value="true"/>
										</dsp:include>
									</div>

									<%-- billing review --%>
									<div class="small-12 large-8 columns" id="multiBillingReview">
										<dsp:include page="/checkout/billing/multi_billing_review.jsp" flush="true">
											<dsp:param name="order" param="order"/>
											<dsp:param name="isConfirmation" param="isConfirmation"/>
											<dsp:param name="isMultiShip" value="true"/>
										</dsp:include>
									</div> 
										<c:if test="${(order.eci ne null || order.cavv ne null) && (isFromOrderDetail eq true) }">
										<div class="small-12 large-8 columns informationInt"> 
											<ul class="address"> 
												<li class="show-for-print"><bbbl:label key="lbl_internal_use_only" language="<c:out param='${language}'/>"/></li> 
												
													<li>
													  <span class="intInfo">
													  	<bbbl:label key="lbl_eci_flag" language="<c:out param='${language}'/>"/>
													  </span>
													 <c:choose>
													 	<c:when test="${order.eci ne null}">
													  		<span class="info"><dsp:valueof param="order.eci"/></span>
													  	</c:when>
													  	<c:otherwise>
													  		<span class="info">NULL</span>
													  	</c:otherwise>
													  </c:choose>
													</li>
													<li>
													  <span class="intInfo">
													  	<bbbl:label key="lbl_cavvucaf" language="<c:out param='${language}'/>"/></span>
													  	<c:choose>
													 	<c:when test="${order.cavv ne null}">
													  	<span class="info"><dsp:valueof param="order.cavv"/></span>
													  	</c:when>
													  	<c:otherwise>
													  		<span class="info">NULL</span>
													  	</c:otherwise>
													  </c:choose>
													</li>		
											</ul>										
										</div>
									</c:if>
									<%-- override authorization --%>
									<div class="small-12 large-12 columns override-auth">
										<c:if test="${not isConfirmation}">
											
												<dsp:droplet name="TBSOverridenItemsDroplet">
													<dsp:param name="order" param="order"/>
													<dsp:oparam name="outputStart">
															<div class="row">
																<div class="small-12 columns">
																	<h2 class="override">Override Authorization</h2>
																	<p class="p-secondary">
																		Approval for these overrides required by a second associate.
																	</p>
																	<ul class="overrides">
													</dsp:oparam>
													<dsp:oparam name="output">
														<li>
															<dsp:valueof param="priceOverrideVO.productName" valueishtml="true"/>
															<dsp:getvalueof param="priceOverrideVO.productName" var="overriddenProductName"/>
															<c:if test="${empty overriddenProductName}">
															<dsp:getvalueof value="NoTax" var="overriddenProductName"/>
														</c:if>
														<input type="hidden" id="productName" value="${overriddenProductName}"/>
														<c:choose>
															<c:when test="${currentSiteId eq bedBathCanadaSiteCode and overriddenProductName eq 'Tax'}">
																<span class="overrides"><bbbl:label key="lbl_override_to" language="<c:out param='${language}'/>"/>
																<dsp:valueof param="priceOverrideVO.overrideAmount" converter="currency"/> 
																<%-- (<dsp:valueof param="priceOverrideVO.overridePercent"/>%) --%></span>
															</c:when>
															<c:otherwise>
																<span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/>
																<dsp:valueof param="priceOverrideVO.overrideAmount" converter="currency"/> 
																<%-- (<dsp:valueof param="priceOverrideVO.overridePercent"/>%) --%></span>
															</c:otherwise>
														</c:choose>
															
														</li>
													</dsp:oparam>
															<dsp:oparam name="outputEnd">
																</ul>
															</div>
														</div>
														<div class="row">
															<div class="small-12 large-6 columns">
																<input type="text" id="overrideId" name="overrideId" placeholder="User Id" onkeypress = "GetKeyCode(event)"/>
															</div>
															<div class="small-12 large-6 columns">
																<input type="password" id="overridePassword" name="overridePassword" placeholder="Password" onkeypress = "GetKeyCode(event)"/>
															</div>
															<div class="small-12 large-offset-6 large-6 columns">
																<input id="approveOverrides" type="submit" name="approveOverrides" value="Approve Overrides" class="small button transactional expand"/>
															</div>
														</div>
													</dsp:oparam>
												</dsp:droplet>
										</c:if>
									</div>
								</div>
								
							</div>
								
						
							<%-- checkout totals --%>
							<div class="small-12 large-4 columns checkout-total no-padding checkout-print-right">
								<h2 class="divider"></h2>
								<div class="small-12 large-12 text-left columns">
									<h2>Delivery Information</h2>
								</div>
								<%-- order summary (i think this is 'preview your order' now) --%>
								<dsp:include page="/checkout/preview/frag/checkout_order_summary.jsp" flush="true">
									<dsp:param name="order" param="order" />
									<dsp:param name="isCheckout" value="true" />
									<dsp:param name="isConfirmation" param="isConfirmation" />
									<dsp:param name="isMultiShip" value="true" />
								</dsp:include>

								<c:if test="${not empty showLinks or not isConfirmation}">

									<%-- place order caption --%>
									<div class="row">
										<div class="small-12 columns">
											<bbbl:label key="lbl_preview_placeordercaption" language="<c:out param='${language}'/>"/>
										</div>
									</div>
									<c:set var="defaultStoreID"><bbbc:config key='DefaultStoreId' configName='GiftRegistryConfig' /></c:set>
									<%-- display store number on checkout page --%>
									<label class="inline-rc checkbox displayInlineBlock storeNumberPreview marBottom_15">
										<input type="checkbox" id="storeNumberPreview" <c:if test="${sessionScope.storenumber ne defaultStoreID}">checked</c:if> >
										<span class="marTop_5"></span>
										Order Taken at Store
									</label>
									<a href="#" id="storeNumberLink" data-reveal-id="storeNumberModal" class="">
                                    	<div id="storeNum" class="inline"><c:out value="${sessionScope.storenumber}"/></div>
                           			</a>
	
									<%-- review confirmation checkbox --%>
									<label class="inline-rc checkbox review-confirmation" for="reviewConfirmation">
										<input type="checkbox" name="reviewConfirmation" id="reviewConfirmation">
										<%-- KP COMMENT START: update this checkbox input with correct bean --%>
										<%--
										<dsp:input bean="" type="checkbox" name="reviewConfirmation" id="reviewConfirmation" />
										--%>
										<span></span>
										Check here to confirm that customer has reviewed this order
										and has made any necessary corrections.
									</label>

									<%-- place order button --%>
									<%-- Start: R2.2 Scope 258 Verified by Visa change to call handleVerifiedByVisaLookup instead of hanldeCommitOrder --%>
									<dsp:input id="shippingPlaceOrder1" bean="CommitOrderFormHandler.verifiedByVisaLookup" type="submit" value="Place Order" iclass="small button transactional expand" disabled="true">
										<dsp:tagAttribute name="aria-pressed" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="shippingPlaceOrder1"/>
										<dsp:tagAttribute name="role" value="button"/>
									</dsp:input>
									<%-- End: R2.2 Scope 258 Verified by Visa change to call handleVerifiedByVisaLookup instead of hanldeCommitOrder --%>
								</c:if>

								<ol>
									<c:if test="${not empty taxFailureFootNote}">
										<li class="footnote">* . ${taxFailureFootNote}</li>
									</c:if>
									<c:if test="${not empty ecofeeFootNote}">
										<li class="footnote"><bbbl:label key="lbl_footnote_ecofee" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
									</c:if>
									<c:if test="${not empty giftWrapFootNote}">
										<li class="footnote"><bbbl:label key="lbl_footnote_giftWrap" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
									</c:if>
									<c:if test="${not empty shippingFootNote}">
										<li class="footnote"><bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
									</c:if>
									<c:if test="${not empty totalFootNote}">
										<li class="footnote"><bbbl:label key="lbl_footnote_total" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
									</c:if>
									<%-- Baby Canada --%>
									<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
										<li class="footnote"><bbbl:textArea key="txt_billing_cc_disclaimer" language="<c:out param='${language}'/>" /></li>
									</c:if>
									<%-- Baby Canada --%>
								</ol>
							</div>
						</div>
						
					</dsp:form>
					<%-- This form used to validate the Store Number in session, before place the order --%>
			<dsp:form id="storeNumberValidationFromCheckout" action="../../selfservice/store_number_ajax.jsp" name="TBSStoreNumberFormHandlerValidation" method="post">
				<dsp:input id="btnValidateStoreNumber" bean="TBSStoreNumberFormHandler.validateStoreNumber" value="true" type="hidden"></dsp:input>
			</dsp:form>
<%-- 				</c:if> --%>

				<%-- don't add override's to order confirmation --%>
				<c:if test="${not isConfirmation}">

					<%-- override authorization --%>
					<dsp:form id="overrideAuthForm" name="overrideAuthForm" method="post">
						<input type="hidden" id="overrideAuthorized" value="${not order.TBSApprovalRequired}" />
						<dsp:input bean="CommitOrderFormHandler.approverId" type="hidden" id="approverId" />
						<dsp:input bean="CommitOrderFormHandler.approverPwd" type="hidden" id="approverPwd" />
						<dsp:input bean="CommitOrderFormHandler.approverAuth" type="submit" id="approverAuth" iclass="hidden" value="Override Approve"/>
					</dsp:form>

					<%-- price override modal inputs --%>
					<dsp:form id="priceOverrideForm" name="priceOverrideForm" method="post">
						<dsp:input type="hidden" bean="CartModifierFormHandler.overrideId" id="commerceItemId" value="" />
						<dsp:input type="hidden" bean="CartModifierFormHandler.overridePrice" id="overridePrice" value="" />
						<dsp:input type="hidden" bean="CartModifierFormHandler.overrideQuantity" id="overrideQuantity" value="0" />
						<dsp:input type="hidden" bean="CartModifierFormHandler.reasonCode" id="reasonCode" value="" />
						<dsp:input type="hidden" bean="CartModifierFormHandler.competitor" id="competitor" value="" />
							<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="multiPreview" />
						<%-- <dsp:input type="hidden" bean="CartModifierFormHandler.overrideSuccessURL" value="/tbs/cart/cart_includes/priceDisplay_json.jsp" />
						<dsp:input type="hidden" bean="CartModifierFormHandler.overrideErrorURL" value="/tbs/cart/cart_includes/priceDisplay_json.jsp" /> --%>
						<dsp:input type="submit" bean="CartModifierFormHandler.itemPriceOverride" id="overrideSubmit" iclass="hidden" value="Override"/>
					</dsp:form>

					<%-- override modals and forms --%>
					<dsp:include page="/checkout/overrides/shippingPriceOverride.jsp" flush="true"/>
					<dsp:include page="/checkout/overrides/taxPriceOverride.jsp" flush="true"/>
					<dsp:include page="/checkout/overrides/deliveryChargeOverride.jsp" flush="true"/>
					<dsp:include page="/checkout/overrides/assemblyFeeOverride.jsp" flush="true"/>
					<dsp:include page="/checkout/overrides/giftWrapPriceOverride.jsp" flush="true"/>
					<dsp:include page="/cart/cart_includes/surchargeOverride.jsp" flush="true"/>

				</c:if>

<%-- 			</c:if> --%>

			<c:set var="isCreditCardFail" value="${false}" scope="request"/>
			<dsp:droplet name="ForEach">
				<dsp:param bean="CommitOrderFormHandler.errorMap" name="array" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="errorCode" param="key" />
					<c:if test="${errorCode eq 'err_checkout_cybersource_error' || errorCode eq 'err_checkout_creditcard_error'}">
						<c:set var="isCreditCardFail" value="${true}" scope="request"/>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>

		</div>
	</div>
	
	<script>
		function GetKeyCode(evt)
	    {
			if (navigator.userAgent.match(/msie/i) || navigator.userAgent.match(/trident/i) ){
	          	if (evt.keyCode == 13){
	      			$('#approveOverrides').trigger('click');
	          	}
			}
	    }
	</script>
</dsp:page>
