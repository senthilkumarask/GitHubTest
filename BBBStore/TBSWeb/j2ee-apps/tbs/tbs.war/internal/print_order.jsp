<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBSkuPropDetailsDroplet" />
	<c:set var="totalStateTax" value="0" scope="request" />
	<c:set var="totalCountyTax" value="0" scope="request" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="section">ErrorPage</jsp:attribute>

		<jsp:body>		
		<dsp:getvalueof var="format" param="format" />		
		<%-- ############R2.2 Code PayPal########## --%>
		
		<dsp:getvalueof var="orderId" param="orderId" />
		<div class="container_12 clearfix" id="content">
			<c:choose>
				<c:when test="${not empty orderId}">
					<c:set var="isBopus" value="${fn:substring(orderId, 0, 3)}"/>
					<c:choose>
						<c:when test="${isBopus eq 'OLP'}">
							<dsp:getvalueof var="property" value="bopusOrderNumber" />
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="property" value="onlineOrderNumber" />
						</c:otherwise>
					</c:choose>
					<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
						<dsp:param value="/atg/commerce/order/OrderRepository" name="repository"/>
						<dsp:param name="itemDescriptor" value="order"/>
						<dsp:param name="${property}" value="${orderId}" />
						<dsp:param name="queryRQL" value="${property}=:${property}" />
						<dsp:param name="elementName" value="element" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="orderId" param="element.id" />
						</dsp:oparam>
					</dsp:droplet>
				
					<%-- ############R2.2 Code PayPal########## --%>
					<dsp:droplet name="/atg/commerce/order/OrderLookup">
				        <dsp:param name="orderId" value="${orderId}" />
				        <dsp:oparam name="empty"> Not a valid ATG Order Id </dsp:oparam>
				        <dsp:oparam name="output">
							<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber" />
							<dsp:getvalueof var="bopusOrderNumber" param="order.bopusOrderNumber" />
							<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail" />
							<div class="grid_12" style="padding-top:20px;">	
								<c:choose>
									<c:when test="${format eq 'message'}">
										<dsp:droplet name="/com/bbb/commerce/order/droplet/internal/BBBOrderXMLDroplet">
											<dsp:param name="order" param="result" />
											<dsp:oparam name="output">
												<pre><code><dsp:valueof param="orderXML" /></code></pre>
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
								    <c:otherwise>
										<dsp:getvalueof var="order" param="result" scope="request"/>
								   		<div class="container_12 clearfix" id="content" role="main">
											<div class="topContent clearfix">
												<div id="subHeader" class="grid_10 clearfix">
													<p class="noMar marTop_5">ATG Order ID: <span class="bold"><dsp:valueof value="${order.id}" /></span></p>
													<p class="noMar marTop_5">Order Date: <span class="bold"><dsp:valueof value="${order.submittedDate}" date="M/dd/yyyy hh:ss" /></span></p>
													<p class="noMar marTop_5">Order Status: <span class="bold"><dsp:valueof value="${order.stateAsString}" /></span></p>
													<p class="noMar marTop_5">Profile ID: <span class="bold"><dsp:valueof value="${order.profileId}" /></span></p>
													<p class="noMar marTop_5">IP Address: <span class="bold"><dsp:valueof value="${order.userIP}" /></span></p>
													<p class="noMar marTop_5">Affiliate: <span class="bold"><dsp:valueof value="${order.affiliate}" /></span></p>
													<p class="noMar marTop_5">School: <span class="bold"><dsp:valueof value="${order.schoolId}" /></span></p>
													<p class="noMar marTop_5">School Promo: <span class="bold"><dsp:valueof value="${order.schoolCoupon}" /></span></p>
												</div>
											</div>
											<div class="clearfix marTop_25">
												<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
												<dsp:getvalueof var="order" param="result"/>
												<dsp:getvalueof var="onlineOrderNumber" param="result.onlineOrderNumber"/>
												<dsp:getvalueof var="bopusOrderNumber" param="result.bopusOrderNumber"/>	
												<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>	
												
												<div class="grid_8" id="leftCol">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="result.shippingGroups" />
														<dsp:param name="elementName" value="shippingGroup" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
																<dsp:droplet name="/atg/dynamo/droplet/Compare">
																	<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
																	<dsp:param name="obj2" value="hardgoodShippingGroup"/>
																	<dsp:oparam name="equal">
																		<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
																		
																		<%-- Start get count of bbb commerceItem --%>
																		<c:if test="${commerceItemRelationshipCount gt 0}">
																			<c:set var="bbbItemcount" value="0" scope="page" />
																			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
																		
																		<%-- Start trackingNumber --%>
																		<c:if test="${empty showLinks}">
																			<c:if test="${not empty onlineOrderNumber}">
																				<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
																					<p><bbbl:label key="lbl_checkoutconfirmation_split_order_message" language="${language}"/><p>
																				</c:if>
																				<h3 class="sectionHeading noBorderBot">Delivery Order #: <span class="normal">${onlineOrderNumber}</span></h3>
																				<p class="noMar marTop_5">Shipping Group ID: <span class="bold"><dsp:valueof value="${shippingGroup.id}" /></span></p>
																				<p class="noMar marTop_5">Shipping Group Type: <span class="bold"><dsp:valueof value="${shippingGroup.shippingGroupClassType}" /></span></p>
																				<p/>
																				<dsp:getvalueof var="billingAddress" param="result.billingAddress"/>
																				<h3 class="sectionHeading">${bbbItemcount} item(s) shipping to <span>${billingAddress.firstName} ${billingAddress.lastName}</span></h3>
																					<dsp:droplet name="TrackingInfoDroplet">
																					<dsp:oparam name="orderOutput">
																						<dsp:getvalueof var="carrierURL" param="carrierURL"/>
																					</dsp:oparam>
																					</dsp:droplet>
																			
																					<ul class="shippingStatusDetails">
																						<li class="clearfix">
																							<div class="grid_5 alpha">
																								<p><strong>Shipping Status</strong></p>
																								<p class="marTop_5">${shippingGroup.stateDetail}</p>
																							</div>
																							<div class="grid_2">
																								<p><strong><bbbl:label key='lbl_trackorder_tracking' language='${pageContext.request.locale.language}' /></strong></p>
																							<ul>
																								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																								<dsp:param name="array" value="${order.trackingInfos}" />
																								<dsp:param name="elementName" value="TrackingInfo" />
																								<dsp:oparam name="output">
																									<dsp:getvalueof var="trackingNumber" param="TrackingInfo.trackingNumber" />
																									<dsp:getvalueof var="carrierName" param="TrackingInfo.carrierCode" />
																									<dsp:getvalueof var="imageKey" value="${carrierName}_Image"/>
																									<dsp:getvalueof var="trackKey" value="${carrierName}_Track"/>
																									<dsp:getvalueof var="carrierImageURL" value="${carrierURL[imageKey]}"/>
																									<dsp:getvalueof var="carrierTrackUrl" value="${carrierURL[trackKey]}"/>
																									<dsp:getvalueof var="trackURL" value="${carrierTrackUrl}?id=${trackingNumber}"/>
																									<li class="marBottom_10"><a href="${trackURL}" target="_blank"><img alt="${carrierName}" src="${imagePath}${carrierImageURL}" align="left"/>
																									${trackingNumber}</a></li>
																								</dsp:oparam>
																								</dsp:droplet>
																							</ul>
																							</div>
																						</li>
																					</ul>
																			</c:if>
																		</c:if>
																		<%-- End tracking Number --%>
				
																		<%-- Shipping Items details Start here --%>
																		<c:if test="${commerceItemRelationshipCount gt 0}">
																			<div class="sectionHeadingBorderTop marTop_20">									
																				<ul class="shippingItemAddDetails marTop_10">
																					<li class="clearfix">
																						<dsp:include page="/checkout/preview/frag/shipping_group_order_details.jsp" flush="true">
																							<dsp:param name="shippingGroup" value="${shippingGroup}"/>
																							<dsp:param name="order" param="result"/>
																						</dsp:include>
																						<dsp:include page="/checkout/preview/frag/gift_wrap.jsp" flush="true">
																							<dsp:param name="shippingGroup" value="${shippingGroup}"/>
																							<dsp:param name="order" param="result"/>
																						</dsp:include>
																					</li>
																				</ul>
																				
																					<dsp:include page="/checkout/preview/frag/checkout_order_items.jsp" flush="true">
																						<dsp:param name="shippingGroup" value="${shippingGroup}"/>
																						<dsp:param name="order" param="result"/>
																						<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
																					</dsp:include>
																				
																					<dsp:include page="/checkout/preview/frag/shipping_group_order_summary.jsp" flush="true">
																						<dsp:param name="shippingGroup" value="${shippingGroup}"/>
																						<dsp:param name="order" param="result"/>
																					</dsp:include>
																				</div>
																			</c:if>
																		<%-- Shipping Items details Ends here --%>
																	</dsp:oparam>
																</dsp:droplet>	
															</dsp:oparam>
														</dsp:droplet>										
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="result.shippingGroups" />
															<dsp:param name="elementName" value="shippingGroup" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>		
																<dsp:droplet name="/atg/dynamo/droplet/Compare">
																	<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
																	<dsp:param name="obj2" value="storePickupShippingGroup"/>
																	<dsp:oparam name="equal">
																	<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
																	
																		<%-- Start get count of bbb commerceItem for StorePickup Shipping group --%>
																		<c:if test="${commerceItemRelationshipCount gt 0}">
																			<c:set var="bbbStoreItemcount" value="0" scope="page" />
																			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
																				<div class="marBottom_10 grid_8 alpha">
																					<h3 class="sectionHeading noBorderBot"><bbbl:label key="lbl_checkoutconfirmation_instorepickup_order" language="${language}"/> <span class="normal">${bopusOrderNumber}</span></h3>
																					<p class="noMar marTop_5">Shipping Group ID: <span class="bold"><dsp:valueof value="${shippingGroup.id}" /></span></p>
																					<p class="noMar marTop_5">Shipping Group Type: <span class="bold"><dsp:valueof value="${shippingGroup.shippingGroupClassType}" /></span></p>
																					<p/>																	
																					<h3 class="sectionHeading">${bbbStoreItemcount} Item(s) to be picked up</h3>
																					<p>Status : Item(s) ready to be picked up on <fmt:formatDate value="${order.submittedDate}" pattern="dd/MM/yyyy"/></p>
																				</div>
																			</c:if>
																		</c:if>
																		
																		<%-- Items pick from store details Starts here --%>
																		<c:if test="${commerceItemRelationshipCount gt 0}">								
																			<div class="grid_8 alpha omega marBottom_20 sectionHeadingBorderTop">									
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
																		</c:if>
																		<%-- Items pick from store details Ends here --%>
																	</dsp:oparam>
																</dsp:droplet>
														</dsp:oparam>
													</dsp:droplet>
													<%-- Billing information Starts here --%>
													<div class="billingInformation grid_8 alpha omega marTop_20 marBottom_20">
														<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
														<c:if test="${not cartEmpty}">
															<h3 class="sectionHeading noBorder"><bbbl:label key="lbl_preview_billinginformation" language="<c:out param='${language}'/>"/></h3>
															<div id="summary" class="spclSectionBox">
																<div class="spclSection clearfix">
																	<div class="grid_4 omega">	
																		<p class="marBottom_10"><strong><bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/></strong></p>
																		<dl class="clearfix">
																			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																			<dsp:param name="array" param="result.paymentGroups"/>
																			<dsp:param name="elementName" value="paymentGroup" />
																			<dsp:oparam name="output">													
																				<dsp:droplet name="Switch">
																					<dsp:param name="value" param="paymentGroup.paymentMethod"/>
																					<dsp:oparam name="creditCard">
																						<p class="noMar marTop_5">Payment Group ID: <span class="bold"><dsp:valueof param="paymentGroup.id" /></span></p>
																						<p class="noMar marTop_5">Payment Group Type: <span class="bold"><dsp:valueof param="paymentGroup.paymentMethod" /></span></p>																	
																						<p class="noMar marTop_5">Payment Group Auth Code: <span class="bold"><dsp:valueof param="paymentGroup.authorizationStatus[0].authorizationCode" /></span></p>
																						<p>
																						<dt><dsp:valueof param="paymentGroup.creditCardType"/></dt>
																						<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBCreditCardDisplayDroplet">
																							<dsp:param name="creditCardNo" param="paymentGroup.creditCardNumber"/>
																							<dsp:oparam name="output">	
																								<dd><dsp:valueof param="maskedCreditCardNo"/></dd>	
																							</dsp:oparam>
																						</dsp:droplet>
																							<dt><bbbl:label key="lbl_preview_exp" language="<c:out param='${language}'/>" /></dt>
																							<dd>
																								<dsp:getvalueof var="expDate" param="paymentGroup.expirationMonth" scope="page" />
																								<c:if
																									test='<%=new Integer((String)pageContext.getAttribute("expDate"))<10 %>'>
																									<c:out value="0${expDate}" />
																								</c:if>
																								<c:if
																									test='<%=new Integer((String)pageContext.getAttribute("expDate"))>=10 %>'>
																									<c:out value="${expDate}" />
																								</c:if>
																								/
																								<dsp:valueof param="paymentGroup.expirationYear" />
																							</dd>
																						<p/>
																					</dsp:oparam>
																				</dsp:droplet>
																			</dsp:oparam> 
																			</dsp:droplet>
																			<p/>
																			<dsp:droplet name="BBBPaymentGroupDroplet">
																			  <dsp:param name="order" param="result" />
																			  <dsp:param name="serviceType" value="GiftCardDetailService"/>
																			  <dsp:oparam name="output">															  
																				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																					<dsp:param name="array" param="giftcards" />
																					<dsp:param name="elementName" value="giftcard" />
																					<dsp:oparam name="output">
																						<p class="noMar marTop_5">Payment Group ID: <span class="bold"><dsp:valueof param="giftcard.id" /></span></p>
																						<p class="noMar marTop_5">Payment Group Type: <span class="bold"><dsp:valueof param="giftcard.paymentMethod" /></span></p>
																						<p>																	
																						<dt><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></dt>			
																						<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
																						<dd><bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}</dd>
																						<dt><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></dt>
																						<dsp:droplet name="CurrencyFormatter">
																							<dsp:param name="currency" param="giftcard.amount"/>
																							<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																							<dsp:oparam name="output">
																								<dd><dsp:valueof param="formattedCurrency"/></dd>
																							</dsp:oparam>
																						</dsp:droplet>
																						<p class="marBottom_10"/>
																					</dsp:oparam>
																				</dsp:droplet>
																			  </dsp:oparam>
																			</dsp:droplet>
																			<c:if test="${not empty showLinks}">	
																				<p class="marTop_5"><a class="upperCase" href="${pageContext.request.contextPath}/checkout/payment/billing_payment.jsp"><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a></p>
																			</c:if>
																			<dsp:getvalueof var="billingAddress" param="result.billingAddress"/>
																			<div class="grid_4 alpha omega marBottom_20 marTop_20">
																				<c:if test="${not empty billingAddress.city}">
																					<p><strong><bbbl:label key="lbl_preview_billingaddress" language="<c:out param='${language}'/>"/></strong></p>
																					<p class="marTop_5">${billingAddress.firstName} ${billingAddress.lastName}</p>
																					<p>${billingAddress.address1}, ${billingAddress.address2}</p>
																					<p>${billingAddress.city}, ${billingAddress.state} ${billingAddress.postalCode}</p>
																					<dsp:oparam name="false">
											                                      		<c:choose>
											                                      			<c:when test="${internationalCCFlag}">
											                                      				<p class="countryName internationalCountry">${billingAddress.countryName}</p>
											                                      			</c:when>
											                                      			<c:otherwise>
											                                      				<p class="countryName hidden">${billingAddress.countryName}</p>
											                                      			</c:otherwise>
											                                      		</c:choose>
											                                      		<span class="country hidden">${billingAddress.country}</span>
											                                      	</dsp:oparam>
																					<c:if test="${not empty showLinks}">
																						<p class="marTop_10"><a class="upperCase" href="${pageContext.request.contextPath}/checkout/billing/billing.jsp"><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a></p>
																					</c:if>
																				</c:if>
																			</div>															  
																		   <dsp:droplet name="BBBSkuPropDetailsDroplet">
																		   <dsp:param name="order" value="${order}"/>									 
																			<dsp:oparam name="output">
																			   <dsp:getvalueof var="skuList" param="skuProdStatus" scope="request"/>
																			   <c:if test="${not empty skuList}">
																					<div class="marTop_10 highlight"><bbbl:label key="lbl_skuprod_statename" language="<c:out param='${language}'/>"/></div>
																				   <a class="popup" href="${contextPath}/_includes/modals/prop65.jsp">
																					 <strong><bbbl:label key="lbl_skuprod_link" language="<c:out param='${language}'/>"/></strong>
																					</a>
																			   </c:if>
																			</dsp:oparam>
																			</dsp:droplet> 															 
																		</dl>
																	</div>													
																	<div class="grid_4 omega">						
																		<dsp:include page="/checkout/order_summary_data.jsp">
																			<dsp:param name="order" param="result"/>
																			<dsp:param name="displayTax" value="true"/>
																		</dsp:include>
																	</div>
																</div>
															</div>
														</c:if>	
													</div>
												</div>
											</div>							
										</div>
									</c:otherwise>
								</c:choose>
							</div>		
				    	</dsp:oparam>
				    	<dsp:oparam name="error">
				    		<div class="grid_12" style="padding-top:20px; color:#ff0000;">
				    			<bbbe:error key="err_invalid_order_id" language="${language}"/>
				    		</div>
				    	</dsp:oparam>
				    </dsp:droplet>	
				</c:when>
				<c:otherwise>
					<div class="grid_12" style="padding-top:20px; color:#ff0000;">
						<bbbe:error key="err_invalid_order_id" language="${language}"/>
					</div>
				</c:otherwise>
			</c:choose>	
		</div>		
	</jsp:body>
</bbb:pageContainer>
</dsp:page>