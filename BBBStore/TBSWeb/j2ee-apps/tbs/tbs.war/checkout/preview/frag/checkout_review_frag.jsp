<dsp:page>
 
	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBSkuPropDetailsDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/PromotionExclusionMsgDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSOverridenItemsDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<dsp:getvalueof param="order" var="order"/>

	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<dsp:droplet name="BBBPackNHoldDroplet">
		<dsp:param name="order" param="order"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="isPackHold" var="isPackHold"/>
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${isPackHold eq true}">
		<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet">
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
	<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
		<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
	</dsp:droplet>
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
		<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
			<dsp:param name="order" param="order"/>
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="order" param="order"/>
	<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber"/>
	<dsp:getvalueof var="bopusOrderNumber" param="order.bopusOrderNumber"/>
	<dsp:getvalueof var="hideOrderNumber" param="hideOrderNumber"/>

	<c:set var="showLinks" scope="request">
		<dsp:valueof param="showLinks"/>
	</c:set>

	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>

	<div class="row">

		<c:if test="${not empty isShippingMethodChanged}">
			<div class="small-12 columns">
				<p class="error">
					<bbbe:error key="${isShippingMethodChanged}" language="${language}"/>
				</p>
			</div>
		</c:if>


		<%-- shipping info --%>
		<div class="small-12 columns">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="shippingGroup.repositoryItem.type"/>
						<dsp:oparam name="hardgoodShippingGroup">
							<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>

							<%-- Start get count of bbb commerceItem --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<c:set var="bbbItemcount" value="0" scope="page" />
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
									<dsp:param name="elementName" value="commerceItemRelationship" />
									<dsp:oparam name="output">
										<dsp:droplet name="/atg/dynamo/droplet/Compare">
										<dsp:param name="obj1" param="commerceItemRelationship.commerceItem.repositoryItem.type"/>
										<dsp:param name="obj2" value="bbbCommerceItem"/>
											<dsp:oparam name="equal">
												<dsp:getvalueof var="quantity" param="commerceItemRelationship.quantity"/>
												<c:set var="bbbItemcount" value="${bbbItemcount + quantity}" scope="page" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
							<%-- End get count of bbb commerceItem --%>


<%-- TODO: find out how tracking number works --%>
							<%-- Start trackingNumber --%>
							<c:if test="${empty showLinks}">
								<c:if test="${not empty onlineOrderNumber}">
									<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
										<c:if test="${isFromOrderDetail}">
											<p><bbbl:label key="lbl_checkoutconfirmation_split_order_message" language="${language}"/><p>
										</c:if>
									</c:if>
									<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
									<c:if test="${isFromOrderDetail}">
										<h3 class="checkout-title">
										<!-- 149 -->
											${bbbItemcount} <bbbl:label key="lbl_item_shipping_to" language="${language}"/> ${shippingGroup.shippingAddress.firstName} ${shippingGroup.shippingAddress.lastName}
										</h3>
									</c:if>
									<c:choose>
										<c:when test="${isFromOrderDetail == true}">
											<dsp:droplet name="TrackingInfoDroplet">
												<dsp:oparam name="orderOutput">
													<dsp:getvalueof var="carrierURL" param="carrierURL"/>
												</dsp:oparam>
											</dsp:droplet>

											<ul class="shippingStatusDetails">
												<li>
													<div class="grid_5 alpha">
														<p><strong><bbbl:label key="lbl_order_detail_shipping_status" language="${language}"/></strong></p>
														<c:choose>
															<c:when test="${empty shippingGroup.stateDetail}">
																<p class="marTop_5">${order.stateAsString}</p>
																<c:set var="orderStatus" value="${fn:toLowerCase(order.stateAsString)}"/>
															</c:when>
															<c:otherwise>
																<p class="marTop_5">${shippingGroup.stateDetail}</p>
																<c:set var="orderStatus" value="${fn:toLowerCase(shippingGroup.stateDetail)}"/>
															</c:otherwise>
														</c:choose>
													</div>
													<div class="grid_2">
														<p>
															<strong>
																<bbbl:label key='lbl_trackorder_tracking' language='${pageContext.request.locale.language}' />
															</strong>
														</p>
														<c:choose>
															<c:when test="${orderStatus eq 'cancelled' || orderStatus eq 'canceled'}">

															</c:when>
															<c:otherwise>
																<ul>
																	<dsp:droplet name="ForEach">
																		<dsp:param name="array" value="${shippingGroup.trackingInfos}" />
																		<dsp:param name="elementName" value="TrackingInfo" />
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="trackingNumber" param="TrackingInfo.trackingNumber" />
																			<dsp:getvalueof var="carrierName" param="TrackingInfo.carrierCode" />
																			<dsp:getvalueof var="imageKey" value="${carrierName}_Image"/>
																			<dsp:getvalueof var="trackKey" value="${carrierName}_Track"/>
																			<dsp:getvalueof var="carrierImageURL" value="${carrierURL[imageKey]}"/>
																			<dsp:getvalueof var="carrierTrackUrl" value="${carrierURL[trackKey]}"/>
																			<dsp:getvalueof var="trackURL" value="${carrierTrackUrl}${trackingNumber}"/>
																			<c:choose>
																				<c:when test="${carrierTrackUrl  ne null && not empty carrierTrackUrl}">
																					<li class="marBottom_10"><a href="${trackURL}" target="_blank"><img alt="${carrierName}" src="${imagePath}${carrierImageURL}" align="left"/>${trackingNumber}</a></li>
																				</c:when>
																				<c:otherwise>
																					<li class="marBottom_10">${trackingNumber}</li>
																				</c:otherwise>
																			</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</ul>
															</c:otherwise>
														</c:choose>
													</div>
												</li>
											</ul>
										</c:when>
									</c:choose>
								</c:if>
							</c:if>
							<%-- End tracking Number --%>

							<%-- Shipping Items details Start here --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<div class="row">
									<%-- shipping title --%>
									<div class="small-12 columns">
										<h2 class="divider start">
											Where are your items going?
										</h2>
									</div>
									<%-- shipping edit link --%>
									<div class="small-12 large-offset-10 large-2 columns move-up-tiny">
										<c:if test="${not empty showLinks}">
											<c:set var="editText"><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></c:set>
											<a class="tiny button secondary right expand" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp?isFromPreview=true" title="<c:out value='${editText}'/>/>">
												<c:out value='${editText}'/>
											</a>
										</c:if>
									</div>

									<%-- shipping address --%>
									<div class="small-12 large-4 columns">
										<dsp:include page="/checkout/preview/frag/display_address.jsp" flush="true">
											<dsp:param name="shippingGroup" param="shippingGroup"/>
											<dsp:param name="beddingKit" param="beddingKit"/>
											<dsp:param name="collegeName" param="collegeName"/>
											<dsp:param name="weblinkOrder" param ="weblinkOrder"/>
										</dsp:include>
									</div>

									<%-- shipping method --%>
									<div class="small-12 large-4 columns">
										<dsp:include page="/checkout/preview/frag/shipping_method.jsp" flush="true">
											<dsp:param name="shippingGroup" param="shippingGroup"/>
											<dsp:param name="orderDate" param="orderDate"/>
										</dsp:include>
									</div>

									<%-- gift options --%>
									<div class="small-12 large-4 columns">
										<dsp:include page="/checkout/preview/frag/gift_wrap.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="order" param="order"/>
										</dsp:include>
									</div>
								</div>

							</c:if>
						</dsp:oparam>
						<dsp:oparam name="storePickupShippingGroup">
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
										<dsp:include page="/checkout/preview/frag/order_store_pickup_info.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										</dsp:include>
										<dsp:include page="/checkout/preview/frag/checkout_order_items.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
										</dsp:include>
										<dsp:include page="/checkout/preview/frag/store_pickup_shipping_group_order_summary.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										</dsp:include>
									</div>
								</div>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</div>

		<%-- billing info --%>
		<div class="small-12 columns">
			<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
			<c:if test="${not cartEmpty}">
				<div class="row">
					<div class="small-12 columns">
						<h2 class="divider bill">
							What is your Billing Address?
						</h2>
						<%-- TODO: Make bbblabel for this text --%>
						<div>
							<p class="error errorSummaryMsg hidden">
								There were 1 or more errors. Please correct these errors and try again.
							</p>
						<div>
					</div>
					<div class="small-12 large-offset-10 large-2 columns move-up-tiny">
						<c:if test="${not empty showLinks}">
							<c:set var="editText"><bbbl:label key='lbl_preview_edit' language="<c:out param='${language}'/>"/></c:set>
							<a class="tiny button secondary right expand" href="${pageContext.request.contextPath}/checkout/billing/billing.jsp?isFromPreview=true" title="<c:out value='${editText}'/>/>">
								<c:out value='${editText}'/>
							</a>
						</c:if>
					</div>
					<div class="small-12 large-4 columns">
						<h3 class="checkout-title">
							<bbbl:label key="lbl_preview_billingaddress" language="<c:out param='${language}'/>"/>
						</h3>
						<ul class="address">
							<c:if test="${not isPayPal}">
								<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
								<li><dsp:valueof param="order.billingAddress.firstName" valueishtml="false"/> <dsp:valueof param="order.billingAddress.lastName" valueishtml="false"/></li>
								<c:if test="${order.billingAddress.companyName != ''}">
									<li><dsp:valueof param="order.billingAddress.companyName" valueishtml="false"/></li>
								</c:if>
								<li>
									<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
										<dsp:param name="value" param="order.billingAddress.address2"/>
										<dsp:oparam name="true">
											<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>
										</dsp:oparam>
										<dsp:oparam name="false">
											<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.address2" valueishtml="false"/>
										</dsp:oparam>
									</dsp:droplet>
								</li>
								<li>
									<dsp:valueof param="order.billingAddress.city" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.state" /> <dsp:valueof param="order.billingAddress.postalCode" valueishtml="false"/>
								</li>
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" param="order.billingAddress.countryName"/>
									<dsp:oparam name="false">
										<c:choose>
											<c:when test="${internationalCCFlag}">
												<li class="countryName internationalCountry"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></li>
											</c:when>
											<c:otherwise>
												<li class="countryName hidden"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></li>
											</c:otherwise>
										</c:choose>
										<li class="country hidden"><dsp:valueof param="order.billingAddress.country" /></li>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</ul>
					</div>
					<div class="small-12 large-4 columns">
						<h3 class="checkout-title">
							Contact Information
						</h3>
						<ul class="address">
							<li><dsp:valueof param="order.billingAddress.email" valueishtml="false"/></li>
							<li><dsp:valueof param="order.billingAddress.mobileNumber" valueishtml="false"/></li>
						</ul>
					</div>
					<div class="small-12 large-4 columns"></div>
				</div>
			</c:if>
		</div>

		<%-- payment info / place order --%>
		<div class="small-12 columns">

			<dsp:getvalueof var="isFromOrderHistory" param="isFromOrderHistory"/>
			<c:if test="${isFromOrderHistory ne true}">
				<dsp:form name="commit_form" id="checkoutReview" method="post">
					<div class="small-12 columns">
						<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
						<c:if test="${not cartEmpty}">

							<%-- payment info --%>
							<div class="row">
								<div class="small-12 columns">
									<h2 class="divider pay">
										How Would You Like to Pay for Your Items?
									</h2>
									<div>
										<p class="error errorSummaryMsg hidden">
											There were 1 or more errors. Please correct these errors and try again.
										</p>
									</div>
								</div>
								<div class="small-12 large-offset-10 large-2 columns move-up-tiny">
									<c:if test="${not empty showLinks}">
										<c:set var="editText"><bbbl:label key='lbl_preview_edit' language="<c:out param='${language}'/>"/></c:set>
										<a class="tiny button secondary right expand" href="${pageContext.request.contextPath}/checkout/payment/billing_payment.jsp?isFromPreview=true" title="<c:out value='${editText}'/>/>">
											<c:out value='${editText}'/>
										</a>
									</c:if>
								</div>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="order.paymentGroups"/>
									<dsp:param name="elementName" value="paymentGroup" />
									<dsp:oparam name="output">
										<dsp:droplet name="Switch">
											<dsp:param name="value" param="paymentGroup.paymentMethod"/>
											<dsp:oparam name="paypal">
												<c:set var="isPayPal" value="${true}"/>

												<%-- Payment Method --%>
												<div class="small-12 large-4 columns">
													<h3 class="checkout-title">
														<bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/>
													</h3>
													<ul class="address">
														<li>PayPal</li>
													</ul>
												</div>

												<%-- Payment Details --%>
												<div class="small-12 large-4 columns">
													<h3 class="checkout-title">
														PayPal Details
													</h3>
													<ul class="address">
														<bbbl:label key="lbl_paypal_title" language="<c:out param='${language}'/>"/>
														<li><dsp:valueof param="paymentGroup.payerEmail" /></li>
													</ul>
												</div>

											</dsp:oparam>
											<dsp:oparam name="creditCard">

												<%-- Payment Method --%>
												<div class="small-12 large-4 columns">
													<h3 class="checkout-title">
														<bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/>
													</h3>
													<ul class="address">
														<li>Credit Card</li>
													</ul>
												</div>

												<%-- Payment Details --%>
												<div class="small-12 large-4 columns">
													<h3 class="checkout-title">
														Credit Card Details
													</h3>
													<ul class="address">
														<li>
															<dsp:valueof param="paymentGroup.creditCardType"/>
														</li>
														<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBCreditCardDisplayDroplet">
															<dsp:param name="creditCardNo" param="paymentGroup.creditCardNumber"/>
															<dsp:oparam name="output">
																<li><dsp:valueof param="maskedCreditCardNo"/></li>
															</dsp:oparam>
														</dsp:droplet>
														<li>
															Exp.
															<dsp:getvalueof var="expDate" param="paymentGroup.expirationMonth" scope="page" />
															<c:if test='${expDate lt 10}'>
																<c:out value="0${expDate}" />
															</c:if>
															<c:if test='${expDate ge 10}'>
																<c:out value="${expDate}" />
															</c:if>
															<bbbl:label key="lbl_checkout_forward_slash" language="<c:out param='${language}'/>"/>
															<dsp:valueof param="paymentGroup.expirationYear" />
														</li>
														<li>
															<dsp:getvalueof var="cardVerificationNumber" param="paymentGroup.cardVerificationNumber"/>
															<dsp:getvalueof var="cardVerNumber" bean="CommitOrderFormHandler.cardVerNumber"/>
															<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
															<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
															<c:if test="${not empty showLinks}">
																<c:if test="${isExpress}">
																	<div class="input grid_4 alpha omega noMar">
																		<div class="label">
																			<label id="lblsecurityCode" for="securityCode"> <bbbl:label key="lbl_payment_securityCode" language="<c:out param='${language}'/>"/>
																			<span class="required"><bbbl:label key="lbl_mandatory" language="<c:out param='${language}'/>"/></span> </label>
																		</div>
																		<div class="text grid_1">
																			<c:choose> 
                                                                				<c:when test="${creditCardType eq 'AmericanExpress'}">
																					<dsp:input type="text" id="securityCode" name="securityCode" bean="CommitOrderFormHandler.cardVerNumber" autocomplete="off" iclass="textRight" maxlength="4">
																						<dsp:tagAttribute name="aria-required" value="true"/>
																						<dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
																					</dsp:input>
																				</c:when>
																				<c:otherwise>
																					<dsp:input type="text" id="securityCode" name="securityCode" bean="CommitOrderFormHandler.cardVerNumber" autocomplete="off" iclass="textRight" maxlength="3">
																						<dsp:tagAttribute name="aria-required" value="true"/>
																						<dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
																					</dsp:input>
																				</c:otherwise>
																			</c:choose>
																		</div>
																		<div class="whatsThis grid_3 omega marTop_5">
																			<a class="info">
																				<strong><bbbl:label key="lbl_payment_txt" language="<c:out param='${language}'/>"/></strong>
																				<bbbl:textArea key="txt_payment_dynamicText" language="${language}"/>
																			</a>
																		</div>
																		<div class="cb label">
																			<label id="errorsecurityCode" for="securityCode" class="error" generated="true"></label>
																		</div>
																	</div>
																</c:if>
															</c:if>
														</li>
													</ul>
												</div>
											</dsp:oparam>
											<dsp:oparam name="payAtRegister">
												<%-- Payment Method --%>
												<div class="small-12 large-4 columns">
													<h3 class="checkout-title">
														<bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/>
													</h3>
													<ul class="address">
														<li>Pay At Register</li>
													</ul>
												</div>
												<%-- Payment Details --%>
												<div class="small-12 large-4 columns"></div>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>

								<%-- Gift Card Details --%>
								<div class="small-12 large-4 columns">
									<dsp:include page="/checkout/preview/frag/giftcard_balance_frag.jsp" flush="true">
										<dsp:param name="order" param="order"/>
									</dsp:include>
								</div>

								<%-- california prop 65 --%>
								<dsp:droplet name="BBBSkuPropDetailsDroplet">
									<dsp:param name="order" value="${order}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="skuList" param="skuProdStatus" scope="request"/>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${skuList}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="size" param="size" />
												<c:set var="size" value="${size}"/>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
								<c:if test="${size eq '1' }">
									<div class="small-12 columns">
										<h3>
											<bbbl:label key="lbl_skuprod_statename" language="<c:out param='${language}'/>"/>
										</h3>
										<a class="popup cali" href="${contextPath}/_includes/modals/prop65.jsp">
											<bbbl:label key="lbl_skuprod_link" language="<c:out param='${language}'/>"/>
										</a>
									</div>
								</c:if>

							</div>
							<%-- /payment info --%>

							<%-- preview --%>
							<div class="row">

								<div class="small-12 columns">
									<h2 class="divider preview-section">
										Preview Your Order
									</h2>
								</div>

								<%-- order items --%>
								<div class="small-12 columns">
									<dsp:include page="/checkout/preview/frag/single_checkout_order_items.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										<dsp:param name="order" param="order"/>
										<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
									</dsp:include>
								</div>

								<%-- override authorization --%>
								<input type="hidden" id="overrideAuthorized" value="${not order.TBSApprovalRequired}" />
								<dsp:droplet name="TBSOverridenItemsDroplet">
									<dsp:param name="order" param="order"/>
									<dsp:oparam name="outputStart">
											<div class="small-12 large-6 columns override-auth">
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
									</dsp:oparam>
								</dsp:droplet>
												
									</div>

								<%-- checkout totals --%>
								<div class="small-12 large-offset-8 large-4 columns checkout-total">

									<%-- order summary --%>
									<dsp:include page="/checkout/preview/frag/shipping_group_order_summary.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										<dsp:param name="order" param="order"/>
									</dsp:include>

									<c:if test="${not empty showLinks}">

										<%-- place order caption --%>
										<bbbl:label key="lbl_preview_placeordercaption" language="<c:out param='${language}'/>"/>
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

						</c:if>
					</div>
				</dsp:form>
			</c:if>
		</div>
	</div>

</dsp:page>
