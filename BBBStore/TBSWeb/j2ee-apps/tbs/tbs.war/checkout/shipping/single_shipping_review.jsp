<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSGetStoreAddressDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<c:set var="narvarFlag"><bbbc:config key="Narvar_TrackingFlag" configName="FlagDrivenFunctions" /></c:set>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof var="paypal" param="paypal" />
	<dsp:getvalueof var="addcheck" param="addcheck" />
	<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
	

	<div class="row <c:if test='${not isConfirmation and not (paypal and addcheck) and not isExpress}'>hidden</c:if>" id="shipReview">

		<c:if test='${not isConfirmation}'>
			<div class="small-12 large-offset-10 large-2 columns move-up-tiny">
				<c:set var="editText"><bbbl:label key='lbl_preview_edit' language="<c:out param='${language}'/>"/></c:set>
				<a class="tiny button secondary right expand edit-step" data-step="shipping" title="<c:out value='${editText}'/>">
					<c:out value='${editText}'/>
				</a>
			</div>
		</c:if>

		<%-- shipping info --%>
		<div class="small-12 columns">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">

					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
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

							<%-- Start trackingNumber --%>
							<c:if test="${empty showLinks}">
								<c:if test="${not empty onlineOrderNumber}">
									<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
										<c:if test="${isFromOrderDetail}">
											<p><bbbl:label key="lbl_checkoutconfirmation_split_order_message" language="${language}"/><p>
										</c:if>
									</c:if>
									<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
									<c:choose>
										<c:when test="${isFromOrderDetail == true}">
											<dsp:droplet name="TrackingInfoDroplet">
												<dsp:oparam name="orderOutput">
													<dsp:getvalueof var="carrierURL" param="carrierURL"/>
												</dsp:oparam>
											</dsp:droplet>

											<div class="grid_5 alpha">
												<p class="print-4 medium-4 no-padding-left columns"><strong><bbbl:label key="lbl_order_detail_shipping_status" language="${language}"/></strong></p>
												<c:choose>
													<c:when test="${empty shippingGroup.stateDetail}">
														<p class="marTop_5 print-8 columns">${order.stateAsString}</p>
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
																	<c:choose>
																		<c:when test="${narvarFlag eq true && shippingGroup.shippingMethod ne 'SDD'}">
																			<c:set var="narvarTrackParam"><bbbc:config key="Narvar_Track_Num_Param" configName="ThirdPartyURLs" /></c:set>
																			<c:set var="narvarUrl"><bbbc:config key="Narvar_TrackingUrl" configName="ThirdPartyURLs" /></c:set>
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
										</c:when>
									</c:choose>
								</c:if>
							</c:if>
							<%-- End tracking Number --%>

							<%-- Shipping Items details Start here --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<div class="row">

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
											<dsp:param name="orderDate" param="order.submittedDate"/>
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
					</dsp:droplet>
					<c:if test="${shippingGroup.shippingGroupClassType eq 'storeShippingGroup' }">
					
						<div class="small-12 large-4 columns no-padding">
							<dsp:include page="/checkout/preview/frag/shipping_method.jsp" flush="true">
								<dsp:param name="shippingGroup" param="shippingGroup"/>
								<dsp:param name="orderDate" param="orderDate"/>
							</dsp:include>
						</div>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>

</dsp:page>
