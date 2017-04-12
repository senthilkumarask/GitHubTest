<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapCheckDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>

	<%-- Variables --%>
<%-- 	<c:set var="firstVisit" scope="session">false</c:set> --%>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="state" value="${40}"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<dsp:getvalueof id="shipGrpCount" bean="ShoppingCart.current.shippingGroupCount" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:choose>
		<c:when test="${currentState lt state or couponMap}">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" bean="CheckoutProgressStates.failureURL"/>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="GIFT"/>
		</c:otherwise>
	</c:choose>

	<%--
	<div class="small-12 columns">
		<dsp:include page="/checkout/progressBar.jsp">
			<dsp:param name="step" value="gifting"/>
		</dsp:include>
	</div>
	--%>

	<div class="row gifting checkout-panel hidden" id="giftingForm">

		<%-- errors --%>
		<div class="small-12 columns backend-errors" id="shipErrors">
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="BBBShippingGroupFormhandler"/>
			</dsp:include>
		</div>

		<dsp:form id="formGiftingMultipleLocation" name="form" formid="formGiftingMultipleLocation" action="${pageContext.request.requestURI}" method="post">
			<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.siteId" value="${currentSiteId}"/>
			<c:set var="count" value="0" scope="page" />

			<div class="gift-shipments">

				<dsp:droplet name="GiftWrapCheckDroplet">
					<dsp:param name="order" bean="ShoppingCart.current" />
					<dsp:param name="siteId" value="${currentSiteId}" />
					<dsp:param name="giftWrapOption" value="multi" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="tempCompanyName" param="shipAddress.companyName" />
						<dsp:getvalueof var="tempShippingGroup" param="shipGroupParam" />
						<dsp:getvalueof var="shipAddress" param="shipAddress" />
						<dsp:getvalueof var="shippingMethod" param="shippingMethod" />
						<dsp:getvalueof var="shippingMethodDescription" param="shippingMethodDescription" />

						<div class="small-12 columns">
							<h2 class="divider no-padding-left">
								Shipment ${count+1}
								<%-- <bbbl:label key="lbl_shipping_address_selection" language="${pageContext.request.locale.language}" />: --%>
							</h2>
						</div>

						<div class="small-12 large-5 columns" id="multiGiftShip">
							<h3 class="checkout-title no-margin-top">Shipping Information</h3>
							<ul class="address">
								<c:choose>
									<c:when test="${not empty tempShippingGroup.registryId}">
										<li>${tempShippingGroup.registryInfo}</li>
										<li><br/> ${shippingMethodDescription} </li>
									</c:when>
									<c:otherwise>
										<li>
											${shipAddress.firstName} ${shipAddress.lastName}
											<c:if test="${fn:trim(tempCompanyName) != '' || not empty tempCompanyName}">
												(${shipAddress.companyName})
											</c:if>
										</li>
										<li>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value" param="shipAddress.address2"/>
												<dsp:oparam name="true">
													${shipAddress.address1}
												</dsp:oparam>
												<dsp:oparam name="false">
													${shipAddress.address1}, ${shipAddress.address2}
												</dsp:oparam>
											</dsp:droplet>
										</li>
										<li>${shipAddress.city}, ${shipAddress.state} ${shipAddress.postalCode}</li>
										<li><br/> ${shippingMethodDescription} </li>
									</c:otherwise>
								</c:choose>
							</ul>
						</div>
						<div class="small-12 large-7 columns">
							<dsp:include page="frag/giftWrapMessage.jsp">
								<dsp:param name="giftWrapPrice" param="giftWrapPrice" />
								<dsp:param name="count" value="${count}" />
								<dsp:param name="shipGroupId" param="shipGroupId" />
								<dsp:param name="shipGroupParam" param="shipGroupParam" />
								<dsp:param name="shipGroupGiftMessage" param="shipGroupGiftMessage" />
								<dsp:param name="shipGroupGiftInd" param="shipGroupGiftInd" />
								<dsp:param name="nonGiftWrapSkus" param="nonGiftWrapSkus" />
								<dsp:param name="giftWrapFlag" param="giftWrapFlag" />
							</dsp:include>
						</div>
						<div class="viewOrderContentContainer small-12 columns">
							<div><a class="viewOrderContents iconExpand" href="#" title="Click here to expand and view cart contents"><bbbl:label key="lbl_multi_gift_view_order" language="<c:out param='${language}'/>"/></a></div>
							<div class="small-12 columns">
								<div class="orderContents small-12 columns">
									<div class="orderContentsCarousel">
										<dsp:include page="frag/shippingGroupItems.jsp">
											<dsp:param name="giftWrapMap" param="giftWrapMap" />
											<dsp:param name="commItemList" param="commItemList" />
										</dsp:include>
									</div>
								</div>
							</div>
						</div>
						<c:set var="count" value="${count + 1}" scope="page" />
					</dsp:oparam>
					<dsp:oparam name="empty">
						<bbbl:label key="lbl_multi_gift_error_msg" language="<c:out param='${language}'/>"/>
					</dsp:oparam>
				</dsp:droplet>

			</div>

			<div class="small-12 columns last">
				<h2 class="divider">&nbsp;</h2>
			</div>

			<div class="small-12 large-offset-10 large-2 columns">
				<c:set var="lbl_button_next" scope="page">
					<bbbl:label key="lbl_button_next" language="${pageContext.request.locale.language}" />
				</c:set>
				<dsp:input type="submit" value="${lbl_button_next}" id="submitGiftingMultipleLocationBtn" bean="BBBShippingGroupFormhandler.giftMessaging" iclass="small button service expand">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="submitGiftingMultipleLocationBtn"/>
					<dsp:tagAttribute name="role" value="button"/>
				</dsp:input>
			</div>

		</dsp:form>
	</div>

</dsp:page>
