<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.bbb.constants.BBBAccountConstants"%>
	<dsp:importbean bean="/com/bbb/account/OrderHistoryDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<%-- Variables --%>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<bbb:pageContainer>

		<jsp:attribute name="bodyClass">my-account order-summary</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">orderDetailWrapper myAccount useBazaarVoice orderSummary</jsp:attribute>

		<%-- use url param to show all orders, for debugging only --%>
		<c:set var="showAll" scope="request" value="false"/>
		<c:if test="${not empty param.showAll}">
			<c:set var="showAll" scope="request" value="${param.showAll}"/>
		</c:if>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_personalinfo_myaccount" language="${pageContext.request.locale.language}" />: <span class="subheader"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">

					<%-- cancelled order status --%>
					<c:set var="trackOrderMsgOn"><bbbc:config key="Track_Order_Cancelled_Msg_Tag" configName="ContentCatalogKeys" /></c:set>
					<c:if test="${trackOrderMsgOn}">
						<div id="cancledOrderMsg" class="alert-box info" data-alert>
							<h3><bbbt:textArea key="txtarea_trackorder_cancelOrder_head" language='${pageContext.request.locale.language}'/></h3>
							<p class="p-secondary"><bbbt:textArea key="txtarea_trackorder_cancelOrder_body" language='${pageContext.request.locale.language}'/></p>
							<a href="#" class="close">&times;</a>
						</div>
					</c:if>

					<ul data-tab="" class="tabs radius">
						<li class="tab-title tab active"><a href="#panel2-1">Online</a></li>
						<li class="tab-title tab"><a href="#panel2-2">In-Store</a></li>
					</ul>
					<div class="tabs-content trackOrderDetail">

						<%-- online orders --%>
						<section role="tabpanel" class="content active" id="panel2-1">
							<div id="onlineOrders">
								<dsp:droplet name="OrderHistoryDroplet">
									<dsp:param name="siteId" value="${currentSiteId}"/>
									<dsp:param name="orderType" value="ONL"/>
									<dsp:oparam name="orderOutputStart">
										<div class="row ordersHeader hide-for-medium-down">
											<div class="small-12 large-4 columns">
												<h3>Order Date</h3>
											</div>
											<div class="small-12 large-3 columns">
												<h3>Order Number</h3>
											</div>
											<div class="small-12 large-2 columns">
												<h3>Total</h3>
											</div>
											<div class="small-12 large-3 columns">
												<h3>Order Status</h3>
											</div>
										</div>
									</dsp:oparam>
									<dsp:oparam name="orderOutput">
										<c:set var="onlineCounter" value="0" />
										<%-- Need 2 loops for online. First loop show only ATG Second loop show only legacy --%>
										<div id="onlineOrdersContent">
											<%-- this loop is for atg orders only (legacyOrder==false) --%>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="OrderList" />
												<dsp:param name="elementName" value="Orders" />
												<dsp:oparam name="output">

													<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
													<c:if test="${legacyOrder != true}">

														<dsp:getvalueof var="orderType" param="Orders.orderType" />
														<dsp:getvalueof	var="orderId" param="Orders.orderNumber" />
														<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
														<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
														<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
														<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
														<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />

														<c:choose>
															<c:when test="${legacyOrder}">
																<c:set var="splitOrder" value="false"/>
																<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
																<c:set var="orderType" value="online"/>
															</c:when>
															<c:when test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
																<c:set var="splitOrder" value="true"/>
																<c:set var="orderNum" value="${onlineOrderNumber }"/>
																<c:set var="orderType" value="online"/>
																<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
																<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
															</c:when>
															<c:otherwise>
																<c:set var="splitOrder" value="false"/>
																<c:if test="${not empty onlineOrderNumber}">
																	<c:set var="orderNum" value="${onlineOrderNumber }"/>
																	<c:set var="orderType" value="online"/>
																	<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
																	<c:set var="bopusJsonData" value=''/>
																</c:if>
																<c:if test="${not empty bopusOrderNumber}">
																	<c:set var="orderNum" value="${bopusOrderNumber }"/>
																	<c:set var="orderType" value="bopus"/>
																	<c:set var="onlineJsonData" value=''/>
																	<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
																</c:if>
															</c:otherwise>
														</c:choose>

														<div class="row orderHeader">
															<div class="small-12 large-4 columns order-date">
																<c:choose>
																	<%--only show first 10 ATG order details, or showAll param was passed --%>
																	<c:when test="${legacyOrder || (onlineCounter >= 10 && showAll != 'true')}">
																		<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">&nbsp;</a>
																	</c:when>
																	<c:otherwise>
																		<a class="collapseLink" href="#">&nbsp;</a>
																	</c:otherwise>
																</c:choose>
																<span>Order Date: </span><fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
															</div>
															<div class="small-12 large-3 columns order-number">
																<span>Order Number: </span>${orderNum}
																<c:if test="${splitOrder == 'true'}">
																	<br>${bopusOrderNumber}
																</c:if>
															</div>
															<div class="small-12 large-2 columns order-total">
																<span>Total: </span><dsp:valueof param="Orders.totalAmt" converter="currency" />
															</div>
															<div class="small-12 large-3 columns order-status">
																<span>Order Status: </span>${orderStatus}
															</div>
														</div>

														<c:if test="${onlineCounter >= 10 && showAll != 'true'}">
															<c:set var="orderContentClass" value="hidden" />
														</c:if>
														<div class="row orderContent ${orderContentClass}">
															<div class="small-12 columns">
																<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>
																	<dsp:include page="frags/order_summary_frag.jsp">
																		<dsp:param name="orderNum" value="${orderNum}"/>
																		<dsp:param name="orderType" value="${orderType}"/>
																		<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																	</dsp:include>
																	<c:if test="${splitOrder == 'true' }">
																		<hr>
																		<dsp:include page="frags/order_summary_frag.jsp">
																			<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
																			<dsp:param name="orderType" value="bopus"/>
																			<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																		</dsp:include>
																	</c:if>
																	<c:set var="onlineCounter" value="${onlineCounter+1}" />
															</div>
														</div>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>

											<%-- this loop is for legacy orders only (legacyOrder==true) --%>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="OrderList" />
												<dsp:param name="elementName" value="Orders" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
													<c:if test="${legacyOrder == true}">
														<dsp:getvalueof var="orderType" param="Orders.orderType" />
														<dsp:getvalueof	var="orderId" param="Orders.orderNumber" />
														<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
														<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
														<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
														<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
														<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
														<dsp:getvalueof var="legacyOrder" param="Orders.wsOrder" />
														<c:set var="splitOrder" value="false"/>
														<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
														<c:set var="orderType" value="online"/>
														<div class="row orderHeader">
															<div class="small-12 large-4 columns order-date">
																<c:choose>
																	<%--only show first 10 ATG order details, or showAll param was passed --%>
																	<c:when test="${legacyOrder || (onlineCounter >= 10 && showAll != 'true')}">
																		<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">&nbsp;</a>
																	</c:when>
																	<c:otherwise>
																		<a class="collapseLink" href="#">&nbsp;</a>
																	</c:otherwise>
																</c:choose>
																<span>Order Date: </span><fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
															</div>
															<div class="small-12 large-3 columns order-number">
																<span>Order Number: </span>${orderNum}
																<c:if test="${splitOrder == 'true'}">
																	<br>${bopusOrderNumber}
																</c:if>
															</div>
															<div class="small-12 large-2 columns order-total">
																<span>Total: </span><dsp:valueof param="Orders.totalAmt" converter="currency" />
															</div>
															<div class="small-12 large-3 columns order-status">
																<span>Order Status: </span>${orderStatus}
															</div>
														</div>

														<c:if test="${onlineCounter >= 10 && showAll != 'true'}">
															<c:set var="orderContentClass" value="hidden" />
														</c:if>
														<div class="row orderContent ${orderContentClass}">
															<div class="small-12 columns">
																<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>
																<dsp:include page="frags/order_summary_frag.jsp">
																	<dsp:param name="orderNum" value="${orderNum}"/>
																	<dsp:param name="orderType" value="${orderType}"/>
																	<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																</dsp:include>
															</div>
														</div>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</div>
									</dsp:oparam>
									<dsp:oparam name="orderEmpty">
										<p class="no-orders"><bbbe:error key="err_orderhistory_noorder" language="${pageContext.request.locale.language}" /></p>
									</dsp:oparam>
									<dsp:oparam name="error">
										<p class="no-orders error"><bbbe:error key="err_orderhistory_techerr" language="${pageContext.request.locale.language}" /></p>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</section>

						<%-- in store orders --%>
						<section role="tabpanel" class="content" id="panel2-2">
							<div id="instoreOrders">
								<dsp:droplet name="OrderHistoryDroplet">
									<dsp:param name="siteId" value="${currentSiteId}"/>
									<dsp:param name="orderType" value="TBS"/>
									<dsp:oparam name="orderOutputStart">
										<div class="row ordersHeader hide-for-medium-down">
											<div class="small-12 large-4 columns">
												<h3>Order Date</h3>
											</div>
											<div class="small-12 large-3 columns">
												<h3>Order Number</h3>
											</div>
											<div class="small-12 large-2 columns">
												<h3>Total</h3>
											</div>
											<div class="small-12 large-3 columns">
												<h3>Order Status</h3>
											</div>
										</div>
									</dsp:oparam>
									<dsp:oparam name="orderOutput">
										<c:set var="onlineCounter" value="0" />
										<div id="instoreOrdersContent">
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="OrderList" />
												<dsp:param name="elementName" value="Orders" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="orderType" param="Orders.orderType" />
													<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
													<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
													<dsp:getvalueof var="orderDate" param="Orders.orderDate" />
													<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
													<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
													<dsp:getvalueof var="legacyTBSOrder" param="Orders.wsOrder" />

													<c:choose>
														<c:when test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
															<c:set var="splitOrder" value="true"/>
															<c:set var="orderNum" value="${onlineOrderNumber }"/>
															<c:set var="orderType" value="online"/>
															<c:set var="legacyOrder" value="false"/>
															<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
															<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
														</c:when>
														<c:otherwise>
															<c:set var="splitOrder" value="false"/>
															<c:if test="${not empty onlineOrderNumber}">
																<c:set var="orderNum" value="${onlineOrderNumber }"/>
																<c:set var="orderType" value="online"/>
																<c:set var="legacyOrder" value="false"/>
																<c:set var="onlineJsonData" value='{"orderNumber":"${onlineOrderNumber}"}'/>
															</c:if>
															<c:if test="${not empty bopusOrderNumber}">
																<c:set var="orderNum" value="${bopusOrderNumber }"/>
																<c:set var="orderType" value="bopus"/>
																<c:set var="legacyOrder" value="false"/>
																<c:set var="bopusJsonData" value='{"orderNumber":"${bopusOrderNumber}"}'/>
															</c:if>
														</c:otherwise>
													</c:choose>

													<div class="row orderHeader">
														<div class="small-12 large-4 columns order-date">
															<%-- <a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}' data-legacyOrder='' href="#">&nbsp;</a>
															<span>Order Date: </span><fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" /> --%>
															<c:choose>
																<c:when test="${legacyOrder || (onlineCounter >= 10 && showAll != 'true')}">
																	<a class="expandLink" data-onlineOrder='${onlineJsonData}' data-bopusOrder='${bopusJsonData}'  href="#">&nbsp;</a>
																</c:when>
																<c:otherwise>
																	<a class="collapseLink" href="#">&nbsp;</a>
																</c:otherwise>
															</c:choose>
															<span>Order Date: </span><fmt:formatDate value="${orderDate}" pattern="MMMM d, yyyy" />
														</div>
														<div class="small-12 large-3 columns order-number">
															<span>Order Number: </span>${orderNum}
															<c:if test="${splitOrder == 'true'}">
																<br>${bopusOrderNumber}
															</c:if>
														</div>
														<div class="small-12 large-2 columns order-total">
															<span>Total: </span><dsp:valueof param="Orders.totalAmt" converter="currency" />
														</div>
														<div class="small-12 large-3 columns order-status">
															<span>Order Status: </span>${orderStatus}
														</div>
													</div>

													<c:if test="${onlineCounter >= 10 && showAll != 'true'}">
														<c:set var="orderContentClass" value="hidden" />
													</c:if>
													<div class="row orderContent ${orderContentClass}">
														<div class="small-12 columns">
															<%-- Need droplet here that will pass orderID, and bring back TrackOrderVO.bbbOrderVO --%>
															<dsp:getvalueof var="emailIdMD5" param="eid" scope="request"/>
															<dsp:include page="frags/legacy_order_summary_frag.jsp">
																<dsp:param name="orderNum" value="${orderNum}"/>
																<dsp:param name="orderType" value="${orderType}"/>
																<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																<dsp:param name="wsOrder" value="${legacyTBSOrder}"/>
															</dsp:include>
															<c:if test="${splitOrder == 'true' }">
																<hr>
																<dsp:include page="frags/legacy_order_summary_frag.jsp">
																	<dsp:param name="orderNum" value="${bopusOrderNumber}"/>
																	<dsp:param name="orderType" value="bopus"/>
																	<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
																	<dsp:param name="wsOrder" value="${legacyTBSOrder}"/>
																</dsp:include>
															</c:if>
														</div>
													</div>

												</dsp:oparam>
											</dsp:droplet>
										</div>
									</dsp:oparam>
									<dsp:oparam name="orderEmpty">
										<p class="no-orders"><bbbe:error key="err_orderhistory_noorder" language="${pageContext.request.locale.language}" /></p>
									</dsp:oparam>
									<dsp:oparam name="error">
										<p class="no-orders error"><bbbe:error key="err_orderhistory_techerr" language="${pageContext.request.locale.language}" /></p>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</section>
					</div>

					<%--START International shipping Order Tracking link--%>
					<c:if test="${internationalShippingOn}">
						<a href="international_order_summary.jsp">
							<bbbl:label key="lbl_international_track_order" language="${pageContext.request.locale.language}" />
						</a>
					</c:if>
					<%--END International shipping Order Tracking link--%>

				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !=='undefined') {
					s.pageName='My Account>My Orders';
					s.channel='My Account';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
				function omnitureExternalLinks(data){
					if (typeof s !== 'undefined') {
					externalLinks(data);
					}
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
