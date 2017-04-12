<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	
	<%-- Variables --%>
	<dsp:getvalueof id="paypal" param="paypal" />
	<dsp:getvalueof var="addcheck" param="addcheck" />
	<dsp:getvalueof var="paypalErrorList" bean="PayPalSessionBean.errorList"/>

	<div class="row checkout-panel <c:if test='${paypal and addcheck}'>hidden</c:if>" id="shippingForm">

		<div class="small-12 columns">
			<h2 class="divider">
				Where are your items going?
				<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
			</h2>
		</div>

		<%-- errors --%>
		<div class="small-12 columns backend-errors" id="shipErrors">
			<dsp:getvalueof var="exceptions" bean="BBBShippingGroupFormhandler.formExceptions"/>
			<c:choose>
				<c:when test="${not empty exceptions}">
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
					</dsp:include>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty paypalErrorList}">
							<div class="small-12 columns">
								<p class="error">
								<c:forEach var="errMsg" items="${paypalErrorList}">
								   <dsp:valueof value="${errMsg}"/>
								</c:forEach>
								</p>
							</div>
						</c:when>
						<c:otherwise>
							<dsp:include page="/global/gadgets/errorMessage.jsp">
								<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
							</dsp:include>
						</c:otherwise>
					</c:choose>	
				</c:otherwise>
			</c:choose>
		</div>

		<dsp:form id="formShippingMultipleLocation" name="form" method="post" action="#">

			<input type="hidden" name="cisiShipGroupName" value="" />

			<%-- cart items / shipping groups --%>
			<div class="small-12 columns">
				<dsp:include page="frag/multi_shipping_line_item.jsp" />
			</div>

			<%-- if there is an out of stock item, disable stuff --%>
			<dsp:getvalueof id="disableButton" bean="BBBShippingGroupFormhandler.multiShipOutOfStock"/>

			<div class="small-12 columns ctaRow">

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
						<label for="orderIncludesGifts" class="inline-rc checkbox orderIncludesGifts">
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

				<dsp:droplet name="LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="commerceItemList" param="commerceItemList" />
						<dsp:droplet name="ForEach">
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
												value="multiShipping" />
<%-- 				 <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleSuccessURL" value="${contextPath}/checkout/multi_shipping.jsp" />
				<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleErrorURL" value="${contextPath}/checkout/multi_shipping.jsp" /> --%>
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
							<a href="#" id="multiShipSubmit" class="small button service expand" disabled>${lbl_button_next}</a>
						</c:when>
						<c:otherwise>
							<a href="#" id="multiShipSubmit" class="small button service expand">${lbl_button_next}</a>
						</c:otherwise>
					</c:choose>
					<dsp:input type="submit" iclass="hidden" id="submitShippingMultipleLocationBtn" value="${lbl_button_next}" bean="BBBShippingGroupFormhandler.multipleShipping" >
						<dsp:tagAttribute name="aria-pressed" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="submitShippingMultipleLocationBtn"/>
						<dsp:tagAttribute name="role" value="button"/>
					</dsp:input>
					<dsp:input type="submit" iclass="hidden" id="saveBaseFormChanges" value="true" bean="BBBShippingGroupFormhandler.saveNewAddress" />
				</div>
			</div>

		</dsp:form>
	</div>

	<dsp:form method="post" action="${contextPath}/checkout/checkoutType.jsp?shippingGr=multi" formid="changeToShipOnline">
		<dsp:input id="onlineSubmit" type="submit" style="display:none" bean="BBBShippingGroupFormhandler.changeToShipOnline"/>
			<dsp:input bean="BBBShippingGroupFormhandler.fromPage" type="hidden"
												value="multiShippingForm" />
<%-- 		 <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.successURL" value="${contextPath}/checkout/checkoutType.jsp?shippingGr=multi"/>
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.errorURL" value="${contextPath}/checkout/checkoutType.jsp?shippingGr=multi"/>
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.systemErrorURL" value="${contextPath}/checkout/checkoutType.jsp?shippingGr=multi"/> --%>
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

	<div id="addNewAddressModal" class="reveal-modal medium" data-reveal></div>

</dsp:page>
