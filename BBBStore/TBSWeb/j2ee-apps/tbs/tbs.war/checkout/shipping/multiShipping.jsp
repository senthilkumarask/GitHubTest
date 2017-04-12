<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>

	<%-- Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
	<c:set var="errorSize" value="${fn:length(errorList)}"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<c:set var="errMessageShown" value="false" scope="page"/>

	<%-- render page --%>
	<bbb:pageContainer index="false" follow="false" >

		<%-- checkout header --%>
		<jsp:attribute name="headerRenderer">
			<dsp:include page="/checkout/checkout_header.jsp" flush="true">
				<dsp:param name="step" value="shipping"/>
				<dsp:param name="link" value="multiple"/>
				<dsp:param name="pageId" value="6"/>
			</dsp:include>
		</jsp:attribute>

		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="pageWrapper">billing shippingWrapper useMapQuest multiShippingPage useStoreLocator</jsp:attribute>
		<jsp:attribute name="PageType">MultiShipping</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout multiship</jsp:attribute>

		<%-- checkout body --%>
		<jsp:body>

			<div class="row">
				<div class="small-12 columns">
					<%-- multiship progress bar --%>
					<dsp:getvalueof var="step" param="step"/>
					<dsp:include page="/checkout/progressBar.jsp">
						<dsp:param name="step" param="step"/>
					</dsp:include>
				</div>
				<div class="small-12 columns">
					<h2 class="divider no-padding-left">
						Where are your items going?
						<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
					</h2>

					<dsp:getvalueof var="commerceItems" bean="ShoppingCart.current.commerceItems"/>
					<c:if test="${fn:length(commerceItems) != 0}" >

						<%-- error messages --%>
						<%-- Defect ID : BBBR-441 - If paypal error is there in session then dont show form exception : It displays the error twice--%>
						<c:if test="${errorSize eq 0}">
							<dsp:include page="/global/gadgets/errorMessage.jsp" />
						</c:if>

						<%-- form exceptions --%>
						<dsp:getvalueof bean="BBBShippingGroupFormhandler.formExceptions" id="ShipFormExceptions">
							<c:if test="${not empty ShipFormExceptions}">
								<c:set var="errMessageShown" value="true" scope="page" />
							</c:if>
						</dsp:getvalueof>

						<div class="row">
							<dsp:form action="#" name="form" id="formShippingMultipleLocation" method="post">

								<input type="hidden" name="cisiShipGroupName" value="" />

								<div class="small-12 columns">
									<dsp:include page="frag/multi_shipping_line_item.jsp" />
								</div>

								<div class="small-12 columns ctaRow">
									<dsp:getvalueof id="disableButton" bean="BBBShippingGroupFormhandler.multiShipOutOfStock"/>

									<c:if test="${disableButton eq 'yes'}">
										<div class="small-12 error columns">
											<bbbl:label key="lbl_cart_outofstockmessage" language="${pageContext.request.locale.language}" />
										</div>
									</c:if>

									<c:choose>
										<c:when test="${empty sessionScope.giftsAreIncludedInOrder}">
											<c:set var="showCheckbox" value="false"></c:set>
										</c:when>
										<c:otherwise>
											<c:set var="showCheckbox" value="${sessionScope.giftsAreIncludedInOrder}"></c:set>
										</c:otherwise>
									</c:choose>

									<dsp:getvalueof var="displayGiftWrap" bean ="ShoppingCart.current.bopusOrder" />
									<c:if test="${not displayGiftWrap or containsShipOnline}">
										<div class="small-12 large-6 columns checkboxItem input">
											<label for="orderIncludesGifts" class="inline-rc checkbox">
												<dsp:input type="checkbox" checked="${showCheckbox}" value="true" name="orderIncludesGifts" id="orderIncludesGifts" bean="BBBShippingGroupFormhandler.orderIncludesGifts" />
												<span></span>
												<strong><bbbl:label key="lbl_gift_order_include_gifts" language="<c:out param='${language}'/>"/></strong>
											</label>
										</div>
									</c:if>

									<c:set var="lbl_button_next" scope="page">
										<bbbl:label key="lbl_shipping_button_next" language="${pageContext.request.locale.language}" />
									</c:set>

									<c:set var="productIds" scope="request"/>

									<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
									<dsp:param name="order" bean="ShoppingCart.current"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" param = "commerceItemList" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="productId" param="element" />
												<dsp:getvalueof var="count" param="count"/>
												<dsp:getvalueof var="size" param="size"/>
												<c:choose>
													<c:when test="${count eq size}">
														<c:set var="productIds" scope="request">
															${productIds};${productId}
														</c:set>
													</c:when>
													<c:otherwise>
														<c:set var="productIds" scope="request">
															${productIds};${productId},
														</c:set>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
									</dsp:droplet>

									<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.cisiIndex" value="" name="shipToMultiplePeople_cisiIndex" />
									<input type="hidden" value="" name="shipToMultiplePeople_shippingGr" />
									<dsp:input bean="BBBShippingGroupFormhandler.fromPage" type="hidden"
												value="multiShip" />
							<%-- 	 <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleSuccessURL" value="${contextPath}/checkout/shipping/multiShipping.jsp" />
									<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleErrorURL" value="${contextPath}/checkout/shipping/multiShipping.jsp" /> --%>
									<dsp:input type="submit" bean="BBBShippingGroupFormhandler.shipToMultiplePeople" value="true" iclass="hidden" id="shipToMultiplePeople" />

									<%-- continue button --%>
									<c:choose>
										<c:when test="${not displayGiftWrap or containsShipOnline}">
											<c:set var="largeOffset" value="4" scope="session" />
										</c:when>
										<c:otherwise>
											<c:set var="largeOffset" value="10" scope="session" />
										</c:otherwise>
									</c:choose>
									<div class="small-12 large-offset-<c:out value='${largeOffset}'/> large-2 columns">
										<c:choose>
											<c:when test="${disableButton eq 'yes'}">
												<dsp:input type="submit" disabled="true" iclass="enableOnDOMReady small button service expand" id="submitShippingMultipleLocationBtn" value="${lbl_button_next}" bean="BBBShippingGroupFormhandler.multipleShipping" >
													<dsp:tagAttribute name="aria-pressed" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="submitShippingMultipleLocationBtn"/>
													<dsp:tagAttribute name="role" value="button"/>
												</dsp:input>
											</c:when>
											<c:otherwise>
												<dsp:input type="submit" iclass="enableOnDOMReady small button service expand" id="submitShippingMultipleLocationBtn" value="${lbl_button_next}" bean="BBBShippingGroupFormhandler.multipleShipping" >
													<dsp:tagAttribute name="aria-pressed" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="submitShippingMultipleLocationBtn"/>
													<dsp:tagAttribute name="role" value="button"/>
												</dsp:input>
											</c:otherwise>
										</c:choose>
										<dsp:input type="submit" iclass="hidden" id="saveBaseFormChanges" value="true" bean="BBBShippingGroupFormhandler.saveNewAddress" />
									</div>
								</div>

							</dsp:form>
						</div>
					</c:if>

				</div>
			</div>

			<dsp:form method="post" action="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi" formid="changeToShipOnline">
				<dsp:input id="onlineSubmit" type="submit" style="display:none" bean="BBBShippingGroupFormhandler.changeToShipOnline"/>
				<dsp:input bean="BBBShippingGroupFormhandler.fromPage" type="hidden" value="changeToShipOnline" />
			<%-- 	<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.successURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/>
				<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.errorURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/>
				<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.systemErrorURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/> --%>
				<dsp:input id="onlineComId" type="hidden" bean="BBBShippingGroupFormhandler.commerceItemId"/>
				<dsp:input id="onlineShipId" type="hidden" bean="BBBShippingGroupFormhandler.oldShippingId"/>
				<dsp:input id="onlineQuantity" type="hidden" bean="BBBShippingGroupFormhandler.newQuantity"/>
			</dsp:form>

			<c:import url="/selfservice/find_in_store.jsp" >
				<c:param name="enableStoreSelection" value="true"/>
				<c:param name="errMessageShown" value="${errMessageShown}"/>
			</c:import>
			<c:import url="/_includes/modules/change_store_form.jsp" >
				<c:param name="action" value="${contextPath}/_includes/modules/cart_store_pickup_form.jsp"/>
			</c:import>
			<%-- <c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>--%>
			<script type="text/javascript">
				function changeToShipOnline(commerceItemId,oldShippingId,newQuantity){
					document.getElementById('onlineComId').value=commerceItemId;
					document.getElementById('onlineShipId').value=oldShippingId;
					document.getElementById('onlineQuantity').value=newQuantity;
					document.getElementById('onlineSubmit').click();
				}
			</script>
			<%--Including the p2p_directions_input.jsp for taking the Start and destination locations input from customer for displaying the directions.--%>

			<div id="addNewAddressModal" class="reveal-modal medium " data-reveal="">
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param = "commerceItemList" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productId" param="element" />
							<dsp:getvalueof var="count" param="count"/>
							<dsp:getvalueof var="size" param="size"/>
							<c:choose>
								<c:when test="${count eq size}">
									<c:set var="productIds" scope="request">
										${productIds};${productId}
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="productIds" scope="request">
										${productIds};${productId},
									</c:set>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
			<script type="text/javascript">
					if(typeof s !=='undefined') {

					s.pageName='Check Out>Shipping';
					s.channel='Check Out';
					s.prop1='Check Out';
					s.prop2='Check Out';
					s.prop3='Check Out';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					s.events = "scCheckout,event8";
					s.products = '${productIds}';
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
