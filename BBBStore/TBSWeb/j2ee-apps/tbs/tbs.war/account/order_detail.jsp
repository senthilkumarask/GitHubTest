<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSBarcodeGeneratorDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Variables --%>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	
	<dsp:getvalueof var="onlineOrderNumber" value="${order.onlineOrderNumber}"/>
	<dsp:getvalueof var="bopusOrderNumber" value="${order.bopusOrderNumber}"/>
	
	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account order-detail</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist orderDetailWrapper myAccount useBazaarVoice</jsp:attribute>

		<jsp:body>
		
			<dsp:getvalueof var="isGuestUser" bean="Profile.transient"/>
			<div class="row">
				<div class="small-12 columns hide-for-print">
					<h1><c:if test="${not isGuestUser}"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/>: </c:if><span class="subheader"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav hide-on-print">
				<c:if test="${not isGuestUser}">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</c:if>
				</div>
				<div class="right print-email"> <a href="#" class="pdp-sprite email" title="Email Cart"><span></span></a>|<a href="#" class="pdp-sprite print print-trigger" title="Print"><span></span></a> </div>
				<div class="small-12 <c:if test='${not isGuestUser}'> large-9 </c:if> columns">
					<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
					<dsp:droplet name="OrderLookup">
						<c:set var="orderId"><c:out value="${param.orderId}"/></c:set>
						<dsp:param name="orderId" value="${orderId}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="order" param="result"/>
							<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
								<dsp:param name="order" value="${order}"/>
							</dsp:droplet>
							<div class="row">
							
								<%-- <div class="small-12 large-8 columns print-8 show-for-print hide">
									<h1>
										<bbbl:label key="lbl_checkoutconfirmation_confirmation" language="${language}"/>
									</h1>
									<h1 class="subheader">
										<span class="left"><strong><bbbl:label key="lbl_checkoutconfirmation_orderconfirmationsuccessmsg" language="${language}"/></strong></span><strong><span class="show-for-print hide">&nbsp;Bring the printout to a cashier to complete the order.</strong></span>
									</h1>
									<p class="p-secondary left no-margin">
										<bbbl:label key="lbl_checkoutconfirmation_confirmationemailsentto" language="${language}"/>&nbsp;<strong><dsp:valueof value="${order.billingAddress.email}"/></strong>
									</p>
								</div>
								<div class="small-12 columns print-right print-4 show-for-print hide">
									<p class="no-padding no-margin"><Strong>BED BATH & BEYOND</Strong></p>
									<p class="p-secondary no-padding no-margin">
									<!-- TODO: need the following in a bbbl:lable -->
										<strong>Questions About Your Order?</strong></p>
									<p class="p-secondary beyondCall no-padding">Call 1-800-GO BEYOND (1-800-462-3966)</p>
								</div> --%>
								
								<div class="small-12 large-8 columns orderLookup no-padding-left print-12">
										<c:set var="onlineOrderNumber" value="${order.onlineOrderNumber}" scope="request"/>
										<c:set var="bopusOrderNumber" value="${order.bopusOrderNumber}" scope="request"/>
										<c:set var="isFromOrderDetail" value="true" scope="request"/>
										<c:if test="${empty onlineOrderNumber && not empty bopusOrderNumber || empty bopusOrderNumber && not empty onlineOrderNumber}">
											<div class="small-12 large-6 columns no-padding-left">
												<div class="print-4 medium-6 columns">
													<h3><bbbl:label key="lbl_order_id" language="${pageContext.request.locale.language}"/></h3>
												</div>
												<span class="print-8 medium-6 columns">
													<c:choose>
														<c:when test="${empty onlineOrderNumber}">
															<p class="p-secondary">${bopusOrderNumber}</p>
														</c:when>
														<c:otherwise>
															<p class="p-secondary">${onlineOrderNumber}</p>
														</c:otherwise>
													</c:choose>
												</span>
											</div>
										</c:if>
										<div class="small-12 large-6 columns">
											<div class="row">
												<div class="small-12 large-5 columns no-padding-right print-4">
													<p class="p-secondary"><strong>Store Number</strong></p>
												</div>
												<div class="small-12 large-7 columns print-8">
													<p class="p-secondary"><c:out value="${order.tbsStoreNo}"/></p>
												</div>
											</div>
										</div>

										<dsp:getvalueof id="orderDetails" param="orderDetails"/>
										<div class="small-12 large-6 columns no-padding-left">
											<div class="print-4 medium-6 columns">
												<h3><bbbl:label key="lbl_order_date" language="${pageContext.request.locale.language}"/></h3>
											</div>
											<span class="print-8 medium-6 columns">
												<c:choose>
													<c:when test="${currentSiteId == TBS_BedBathCanadaSite}"  >
														<p class="p-secondary"><dsp:valueof value="${order.submittedDate}"	converter="date" date="dd/MM/yyyy"/></p>
													</c:when>
													<c:otherwise>
														<p class="p-secondary"><dsp:valueof value="${order.submittedDate}"	converter="date" date="MM/dd/yyyy"/></p>
													</c:otherwise>
												</c:choose>
											</span>
										</div>
										<div class="small-12 large-6 columns">
											<div class="row">
												<div class="small-12 large-5 columns print-4">
													<p class="p-secondary"><strong>Associate ID</strong></p>
												</div>
												<div class="small-12 large-7 columns print-8">
													<p class="p-secondary"><c:out value="${order.TBSAssociateID}"/></p>
												</div>
											</div>
										</div>
                                	</div>
                                	
                                	<%-- barcodes: this should be shown only for print --%>
                                	
						            <c:choose>
										<c:when test="${empty onlineOrderNumber}">
											<c:set var="genOrderCode" value="${bopusOrderNumber}" />
											<c:set var="bopusOrder" value="true" />
										</c:when>
										<c:otherwise>
											<c:set var="genOrderCode" value="${onlineOrderNumber}" />
											<c:set var="bopusOrder" value="false" />
										</c:otherwise>
									</c:choose>               	
                                	
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${order.paymentGroups}"/>
									<dsp:param name="elementName" value="paymentGroup" />
									<dsp:oparam name="outputStart">
										<div class="row barcodes">
									</dsp:oparam>
									<dsp:oparam name="output">
										<dsp:droplet name="Switch">
											<dsp:param name="value" param="paymentGroup.paymentMethod"/>
											<dsp:oparam name="payAtRegister">
												<dsp:droplet name="TBSBarcodeGeneratorDroplet">
													<dsp:param name="orderId" value="${genOrderCode }"/>
													<dsp:param name="orderTotal" value="${order.priceInfo.total }"/>
													<dsp:oparam name="output">
														<dsp:getvalueof param="orderIdBarcodeImg" var="orderCode"/>
														<dsp:getvalueof param="orderTotalBarcodeImg" var="orderTotal"/>
														<hr class="divider show-for-print hide"></hr>
														<div class="small-12 large-12 columns">
															<div class="small-12 large-7 columns"> </div>
															<div class="small-12 large-5 columns">
																<div class="small-12 large-5 columns">Order Total:</div>
																<div class="small-12 large-7 columns"><div class="print-center divider"><fmt:formatNumber value="${order.priceInfo.total}" type="currency" /></div></div>
															</div>
														</div>
														<div class="small-12 large-12 columns">
															<div class="small-12 columns medium-1">&nbsp;&nbsp;</div>
															<div class="small-12 columns medium-11">Order Number <strong>${genOrderCode}</strong></div>
															
														</div>
														<div class="small-12 columns medium-1 print-right">
															<span class="digits">1</span>
														</div>
														<div class="small-12 large-5 columns">
															<img src="data:image/jpg;base64, <c:out value='${orderCode}' />" />
														</div>
														<!-- <div class="small-12 columns medium-1 print-right left">
															
														</div> -->
														<div class="small-12 large-5 columns">
															<div class="small-12 columns print-1 no-padding">
															<span class="digits">2</span>
															</div>
															<div class="small-12 columns print-11 left">
															<img src="data:image/jpg;base64, <c:out value='${orderTotal}' />" />
															</div>
														</div>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
									
										</div>
									</dsp:oparam>
								</dsp:droplet>
								
                                <div class="small-12 large-4 columns right">
									<dsp:getvalueof bean="Profile.repositoryId" var="profileId"/>
									<c:if test="${order.profileId eq profileId}">
										<a href="${contextPath}/account/order_summary.jsp" class="small button primary"><strong><bbbl:label key="lbl_view_all_order" language="${pageContext.request.locale.language}"/></strong></a>
									</c:if>	
								</div>
								<div class="row">
								<c:choose>
									<c:when test="${fn:length(order.shippingGroups) > 1}">
										<%-- multiship --%>
										<hr class="divider show-for-print hide"></hr>
										<%-- preview --%>
										<div class="small-12 columns">
											<dsp:include page="/checkout/preview/multi_preview.jsp" flush="true">
												<dsp:param name="order" value="${order}"/>
												<dsp:param name="hideOrderNumber" value="true"/>
												<dsp:param name="displayTax" value="true"/>
												<dsp:param name="isConfirmation" value="true"/>
												<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
											</dsp:include>
										</div>
				
									</c:when>
										<c:otherwise>
											<%-- singleship --%>
					
											<%-- shipping --%>
											<div class="small-12 columns">
												<h2 class="divider">Shipping Information</h2>
												<dsp:include page="/checkout/shipping/single_shipping_review.jsp">
													<dsp:param name="order" value="${order}"/>
													<dsp:param name="isConfirmation" value="true"/>
												</dsp:include>
											</div>
					
											<%-- billing --%>
											<div class="small-12 columns">
												<h2 class="divider">Billing Information</h2>
												<dsp:include page="/checkout/billing/single_billing_review.jsp">
													<dsp:param name="order" value="${order}"/>
													<dsp:param name="isConfirmation" value="true"/>
												</dsp:include>
											</div>
					
											<%-- payment --%>
											<div class="small-12 columns">
												<h2 class="divider">Payment Information</h2>
												<dsp:include page="/checkout/payment/single_payment_review.jsp">
													<dsp:param name="order" value="${order}"/>
													<dsp:param name="isConfirmation" value="true"/>
												</dsp:include>
											</div>
					
											<%-- preview --%>
											<div class="small-12 columns">
												<h2 class="divider preview-section">Delivery Summary<span></span></h2>
												<dsp:include page="/checkout/preview/single_preview.jsp" flush="true">
													<dsp:param name="order" value="${order}"/>
													<dsp:param name="hideOrderNumber" value="true"/>
													<dsp:param name="displayTax" value="true"/>
													<dsp:param name="isConfirmation" value="true"/>
													<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
												</dsp:include>
											</div>
					
										</c:otherwise>
									</c:choose>
					
								</div>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !=='undefined') {
					s.pageName='My Account>Order view';
					s.channel='My Account';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
