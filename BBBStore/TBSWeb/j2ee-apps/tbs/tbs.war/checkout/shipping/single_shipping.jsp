<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="paypal" param="paypal" />
	<dsp:getvalueof var="addcheck" param="addcheck" />
	
	
	<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
	<dsp:getvalueof var="paypalErrorList" bean="PayPalSessionBean.errorList"/>

	<div class="row <c:if test='${(paypal and addcheck) or isExpress }'>hidden</c:if>" id="shippingForm">
		
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

		<dsp:form id="formShippingSingleLocation" formid="com_bbb_checkoutShippingAddress" action="${pageContext.request.requestURI}" method="post">

			<%-- shipping address --%>
			<div class="small-12 large-4 columns">
				<h3 class="checkout-title">Shipping Address</h3>
				<dsp:include page="/checkout/shipping/shippingAddressForm.jsp">
					<dsp:param name="isFormException" value="${isFormException}" />
					<dsp:param name="isPaypal" value="${paypal}" />
				</dsp:include>
			</div>

			<%-- shipping method --%>
			<div class="small-12 large-4 columns">
				<h3 class="checkout-title">Shipping Method</h3>
				<dsp:include page="/checkout/shipping/shippingMethods.jsp">
					<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
				</dsp:include>
				<%-- Added for RM # 33032 Start--%>
				<c:if test="${cmo || kirsch}">
				<bbbt:textArea key="txt_cmokirsch_giftpackagingnotemessage" language="${pageContext.request.locale.language}" />
				</c:if>
				<%-- Added for RM # 33032 End--%>
				<c:if test="${not cmo}">
				<p class="p-footnote">
					<bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="${pageContext.request.locale.language}" />
				</p>
				</c:if>
			</div>

			<%-- shipping options (gift options) --%>
			<div class="small-12 large-4 columns">
				<dsp:include page="frag/singleShippingOptionsFrag.jsp">
					<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
				</dsp:include>
			</div>

			<%-- continue button --%>
			<div class="small-12 columns">
				<c:set var="lbl_button_next" scope="page">
					CONTINUE
					<%--<bbbl:label key="lbl_shipping_button_next" language="${pageContext.request.locale.language}" />--%>
				</c:set>
				<dsp:input type="submit" value="${lbl_button_next}" iclass="small button service right" id="submitShippingSingleLocationBtn" bean="BBBShippingGroupFormhandler.addShipping">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
					<dsp:tagAttribute name="role" value="button"/>
				</dsp:input>
			</div>

		</dsp:form>
	</div>

</dsp:page>
