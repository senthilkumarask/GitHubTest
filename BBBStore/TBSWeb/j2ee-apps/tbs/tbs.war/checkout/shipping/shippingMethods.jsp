<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- Variables --%>
	<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
	<dsp:getvalueof bean="BBBShippingGroupFormhandler.shippingOption" id="shippingMethodSelected" />
	
	<dsp:droplet name="GetApplicableShippingMethodsDroplet">
		<dsp:param name="operation" value="perOrder" />
		<dsp:param name="order" bean="ShoppingCart.current" />
		<c:choose>
			<c:when test="${formExceptionFlag eq 'true'}">
				<dsp:setvalue bean="BBBShippingGroupFormhandler.shippingOption" value="${shippingMethodSelected}" />
			</c:when>
			<c:otherwise>
				<dsp:setvalue bean="BBBShippingGroupFormhandler.shippingOption" paramvalue="preSelectedShipMethod" />
			</c:otherwise>
		</c:choose>
		<dsp:oparam name="output">
			<div class="shipping-methods">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="shipMethodVOList" />
					<dsp:param name="sortProperties" value="shippingCharge,shipMethodId"/>
					<dsp:oparam name="output">
				    <dsp:getvalueof var="shippingCharge" param="element.shippingCharge"/>

						<dsp:getvalueof param="element.shipMethodDescription" id="elementVal">
							<dsp:getvalueof param="element.shipMethodId" id="elementId">
							<c:choose>
								<c:when test="${cmo}">
									
											<label class="inline-rc radio gray-panel disabled" id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
												<dsp:input type="radio" name="shippingMethod" id="shippingMethod${elementId}" value="${elementId}" bean="BBBShippingGroupFormhandler.shippingOption" disabled="true">
													<dsp:tagAttribute name="aria-checked" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
												</dsp:input>
												<span></span>
												<c:out value="${elementVal}" /> (<fmt:formatNumber value="${shippingCharge}"  type="currency"/>)
											</label>
								</c:when>
								<c:when test="${kirsch}">
									<c:if test="${elementVal eq 'Standard'}">
										<label class="inline-rc radio gray-panel" id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
											<dsp:input type="radio" name="shippingMethod" id="shippingMethod${elementVal}" value="${elementId}" bean="BBBShippingGroupFormhandler.shippingOption">
												<dsp:tagAttribute name="aria-checked" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
											</dsp:input>
											<span></span>
											<c:out value="${elementVal}" /> (<fmt:formatNumber value="${shippingCharge}"  type="currency"/>)
										</label>
									</c:if>
								</c:when>
								<c:otherwise>
									<label class="inline-rc radio gray-panel" id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
										<dsp:input type="radio" name="shippingMethod" id="shippingMethod${elementId}" value="${elementId}" bean="BBBShippingGroupFormhandler.shippingOption">
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
										</dsp:input>
										<span></span>
										<c:choose>
											<c:when test="${HolidayMessagingOn}">
												<dsp:include src="/tbs/common/holidayMessaging.jsp">								 		
											 		<dsp:param name="timeframe" value="${timeframe}"/>
											 		<dsp:param name="shipMethodDesc" value="${elementVal}"/>
											 		<dsp:param name="shipMethod" value="${elementId}"/>
													<dsp:param name="shippingCharge" param="element.shippingCharge"/>
									 		</dsp:include>
											</c:when>
											<c:otherwise>
												<c:out value="${elementVal}" /> (<fmt:formatNumber value="${shippingCharge}"  type="currency"/>)
											</c:otherwise>
										</c:choose>
										
										<c:if test="${elementVal eq 'Standard'}">
											<dsp:droplet name="ForEach">
												<dsp:param name="array" bean="ShoppingCart.current.shippingGroups" />
												<dsp:param name="elementName" value="shipGroup"/>
												<dsp:oparam name="output">
													<dsp:getvalueof param="shipGroup.shippingMethod" var="shipme"/>
													<dsp:getvalueof param="shipGroup.autoWaiveFlag" var="autoWaiveFlag"/>
													<c:if test="${shipme eq '3g' and autoWaiveFlag}">
														<bbbl:label key="lbl_autowaive_message" language="${pageContext.request.locale.language}" />
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</c:if>
									</label>
								</c:otherwise>
							</c:choose>
								
							</dsp:getvalueof>
						</dsp:getvalueof>
					</dsp:oparam>
				</dsp:droplet>
				<c:if test="${cmo}">
					<label class="inline-rc radio gray-panel" id="lblshippingMethodStorePickup" for="shippingMethodStorePickup">
						<input type="radio" name="shippingMethod" id="shippingMethodStorePickup" value="storeShippingGroup" checked="checked">
						<span></span>
						Store Pick Up
					</label>
				</c:if>
			</div>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
