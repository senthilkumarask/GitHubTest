<dsp:page>
	<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

	<%-- Variables --%>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof param="isMultiShip" var="isMultiShip"/>
	<c:set var="narvarFlag"><bbbc:config key="Narvar_TrackingFlag" configName="FlagDrivenFunctions" /></c:set>
	<div class="row" id="shipReview">
		<%-- shipping info --%>
		<div class="small-12 columns">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="outputStart">
					<dsp:getvalueof var="shipcoupnt" param="size"/>
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
					<dsp:getvalueof var="count" param="count"/>
					<dsp:droplet name="Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="hardgoodShippingGroup"/>
						<dsp:oparam name="equal">
							<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>

							<%-- Start get count of bbb commerceItem --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<c:set var="bbbItemcount" value="0" scope="page" />
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
									<dsp:param name="elementName" value="commerceItemRelationship" />
									<dsp:oparam name="output">
										<dsp:droplet name="Compare">
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
									<%-- <c:if test="${isFromOrderDetail}">
										<h3 class="checkout-title">
										<!-- 149 -->
											${bbbItemcount} <bbbl:label key="lbl_item_shipping_to" language="${language}"/> ${shippingGroup.shippingAddress.firstName} ${shippingGroup.shippingAddress.lastName}
										</h3>
									</c:if> --%>
									<c:choose>
										<c:when test="${isFromOrderDetail == true}">
											<dsp:droplet name="TrackingInfoDroplet">
												<dsp:oparam name="orderOutput">
													<dsp:getvalueof var="carrierURL" param="carrierURL"/>
												</dsp:oparam>
											</dsp:droplet>
													<div class="grid_5 alpha">
												<p class="medium-4 print-4 columns"><strong><bbbl:label key="lbl_order_detail_shipping_status" language="${language}"/></strong></p>
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
											<ul class="shippingStatusDetails">
												<li>
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
																			<c:choose>
																				<c:when test="${narvarFlag eq true && shippingGroup.shippingMethod ne 'SDD'}">
																					<c:set var="narvarUrl"><bbbc:config key="Narvar_TrackingUrl" configName="ThirdPartyURLs" /></c:set>
																					<c:set var="narvarTrackParam"><bbbc:config key="Narvar_Track_Num_Param" configName="ThirdPartyURLs" /></c:set>
																					<dsp:getvalueof var="trackURL" value="${narvarUrl}${fn:toLowerCase(carrierName)}${narvarTrackParam}${trackingNumber}"/>
																				</c:when>
																				<c:otherwise>
																					<dsp:getvalueof var="trackURL" value="${carrierTrackUrl}${trackingNumber}"/>
																				</c:otherwise>
																			</c:choose>
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
								<div class="row" id="sg_${shippingGroup.id}">
								<h2 class="divider hide-for-print"></h2>
									<div class="small-12 columns hide-for-print">
										<h2>
											Shipment ${count}
										</h2>
									</div>
									<div class="small-12 large-12 columns print-gray-panel">
									<div class="small-12 large-3 columns show-for-print hide">
										<h2>
											&nbsp;
										</h2>
										<h2>
											Shipment ${count}
										</h2>
									</div>
									<%-- shipping address --%>
									<div class="small-12 large-3 columns">
										<input id="shipcount" class="shipcount" type="hidden" value="${shipcoupnt}"/>
										<dsp:include page="/checkout/preview/frag/display_address.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="beddingKit" param="beddingKit"/>
											<dsp:param name="collegeName" param="collegeName"/>
											<dsp:param name="weblinkOrder" param ="weblinkOrder"/>
											<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
											<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
										</dsp:include>
									</div>

									<%-- shipping method --%>
									<div class="small-12 large-3 shipping-method columns">
										<dsp:include page="/checkout/preview/frag/shipping_method.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="orderDate" param="order.submittedDate"/>
											<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
											<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
										</dsp:include>
									</div>

									<%-- gift options --%>
									<div class="small-12 large-3 columns">
										<dsp:include page="/checkout/preview/frag/gift_wrap.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="order" param="order"/>
											<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
											<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
										</dsp:include>
									</div>
</div>
									<%-- order items --%>
									<div class="small-12 columns">
										<dsp:include page="/checkout/preview/frag/multi_checkout_order_items.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="order" param="order"/>
											<dsp:param name="isConfirmation" param="isConfirmation"/>
											<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
										</dsp:include>
										<hr class="divider show-for-print"/>
									</div>
									<%-- order summary --%>
									<div class="small-12 medium-3 multi-shipping-totals print-3 columns right">
										<dsp:include page="/checkout/preview/frag/shipping_group_order_summary.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
											<dsp:param name="order" param="order"/>
											<dsp:param name="isConfirmation" param="isConfirmation"/>
											<dsp:param name="count" value="${count}" />
											<dsp:param name="isMultiShip" value="true" />
										</dsp:include>
									</div>
								</div>

							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>

</dsp:page>
